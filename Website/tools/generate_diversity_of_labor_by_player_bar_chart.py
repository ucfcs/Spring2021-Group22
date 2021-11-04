## Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import json

load_dotenv();

MONGO_URI = 'MONGO_URI';
if (MONGO_URI not in os.environ.keys()):
    print('Please add MONGO_URI to your environment variables before using this utility');
    exit(0);

mongo_connection_uri = os.environ.get(MONGO_URI);

# I've changed the schema so without this the script crashes. This check will
# eventually be unnecessary
def isGoodData(event):
    return 'msg' in event;

parser = argparse.ArgumentParser()
parser.add_argument('--out', help='where to write the data to');
parser.add_argument('--experiment', help='the experiment label to limit the data to');
args = parser.parse_args()

# Precompute the data structure needed for the sharing column chart. This would be
# the "tools" part of the process. This would be run once for each team we have run
# through the map
def precomputeJSON(experimentLabel):
    client = pymongo.MongoClient(mongo_connection_uri, serverSelectionTimeoutMS=5000)
    query = { "event": { '$in' : [ 'DoFarmEvent', 'OreBreakEvent', 'DuneBreakEvent', 'VillagerTradeEvent', 'CollectTrophyEvent', 'BarrelOpenedEvent', 'SolveMansionPuzzleEvent' ] } }
    if experimentLabel != None:
        query['experimentLabel'] = experimentLabel
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$project' : { '_id' : 0, 'event': '$event', 'player': 1, 'zone': 1 } },
        { '$group': { '_id' : { 'event': '$event', 'player': '$player'}, 'total': { '$sum': 1 } } },
        { '$group' : { 
            '_id' :  "$_id.event",
            'totals': { '$push': { 'player': '$_id.player', 'total': '$total'} }
            } 
        },
    ]))

    players = set()
    for event_data in intermediary_data:
        for player_data in event_data['totals']:
            players.add(player_data['player'])
    players = list(players)
    events = set()
    for event_data in intermediary_data:
        events.add(event_data['_id'])
    events = list(events)

    # normalize event totals
    event_totals = [0 for _ in events]
    for event_data in intermediary_data:
        total = 0
        for player_data in event_data['totals']:
            total += player_data['total']
        for player_data in event_data['totals']:
            player_data['total'] /= total

    data = {
        'series': [{ 
                'name': event_data['_id'], 
                'data': [
                    (
                        [player_data['total'] for player_data in event_data['totals'] if player_data['player'] == player][0]
                        if (len([player_data['total'] for player_data in event_data['totals'] if player_data['player'] == player]) > 0) 
                        else 0
                    ) 
                for player in players] 
            } for event_data in intermediary_data],
        'categories': players,
    }

    return data

# Write the results to a precomputed file. This file will likely be in the static_files 
# directory.
def writeToFile(data, file):
    with open(file, 'w+') as out:
        json.dump(data, out)

data = precomputeJSON(args.experiment)
writeToFile(data, args.out if args.out != None else '../static_files/data/diversity_of_labor_by_player_column_chart.json')