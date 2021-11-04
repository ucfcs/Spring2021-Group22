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
            '_id' :  "$_id.player",
            'totals': { '$push': { 'event': '$_id.event', 'total': '$total'} }
            } 
        },
    ]))

    events = set()
    for event in intermediary_data:
        for total in event['totals']:
            events.add(total['event'])
    events = list(events)
    players = set()
    for player in intermediary_data:
        players.add(player['_id'])
    players = list(players)

    # normalize event totals
    event_totals = [0 for _ in events]
    for player_data in intermediary_data:
        for idx, event in enumerate(events):
            event_totals[idx] += (
                [event_data['total'] for event_data in player_data['totals'] if event_data['event'] == event][0]
                        if (len([event_data['total'] for event_data in player_data['totals'] if event_data['event'] == event]) > 0) 
                        else 0
            )
    for player_data in intermediary_data:
        for idx, event in enumerate(events):
            for event_data in player_data['totals']:
                if event_data['event'] == event:
                    event_data['total'] /= event_totals[idx]
                    event_data['total'] *= 100.0

    data = {
        'series': [{ 
                'name': player_data['_id'], 
                'data': [
                    (
                        [event_data['total'] for event_data in player_data['totals'] if event_data['event'] == event][0]
                        if (len([event_data['total'] for event_data in player_data['totals'] if event_data['event'] == event]) > 0) 
                        else 0
                    ) 
                for event in events] 
            } for player_data in intermediary_data],
        'categories': events,
    }

    return data

# Write the results to a precomputed file. This file will likely be in the static_files 
# directory.
def writeToFile(data, file):
    with open(file, 'w+') as out:
        json.dump(data, out)

data = precomputeJSON(args.experiment)
writeToFile(data, args.out if args.out != None else '../static_files/data/diversity_of_labor_column_chart.json')