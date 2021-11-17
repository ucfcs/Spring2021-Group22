# Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import json
from uuid_to_playerdata import UUID_MAP

load_dotenv()

MONGO_URI = 'MONGO_URI'
if (MONGO_URI not in os.environ.keys()):
    print('Please add MONGO_URI to your environment variables before using this utility')
    exit(0)

mongo_connection_uri = os.environ.get(MONGO_URI)

PLAYERS = ['14d285df-e64e-41f2-bc4b-979e846c3cec', '6dc38184-c3e7-49ab-a99b-799b01274d01',
           '7d80f280-eaa6-404c-8830-643ccb357b62', 'ffaa5663-850e-4009-80c4-c8bbe34cd285']
EVENTS = ['BarrelOpenedEvent', 'CollectTrophyEvent', 'DoFarmEvent',
          'DuneBreakEvent', 'OreBreakEvent', 'SolveMansionPuzzleEvent', 'VillagerTradeEvent']


parser = argparse.ArgumentParser()
parser.add_argument('--out', help='where to write the data to')
parser.add_argument(
    '--experiment', help='the experiment label to limit the data to')
args = parser.parse_args()

def build_pie_chart(event, intermediary_data):
    player_data = next((data['totals'] for data in intermediary_data if data['_id'] == event), [])
    return {
        'title': 'Player Contributions for ' + event,
        'series': [next((data['total'] for data in player_data if data['player'] == player), 0) for player in PLAYERS],
        'labels': [UUID_MAP[player]['name'] for player in PLAYERS],
    };

# Precompute the data structure needed for the sharing column chart. This would be
# the "tools" part of the process. This would be run once for each team we have run
# through the map
def precomputeJSON(experimentLabel):
    client = pymongo.MongoClient(
        mongo_connection_uri, serverSelectionTimeoutMS=5000)
    query = { 'experimentLabel': experimentLabel if experimentLabel != None else { '$exists': True }, "event": {'$in': ['DoFarmEvent', 'OreBreakEvent', 'DuneBreakEvent',
                               'VillagerTradeEvent', 'CollectTrophyEvent', 'BarrelOpenedEvent', 'SolveMansionPuzzleEvent']}}
    intermediary_data = list(client.epilog.data2.aggregate([
        {'$match': query},
        {'$project': {'_id': 0, 'event': '$event', 'player': 1, 'zone': 1}},
        {'$group': {'_id': {'event': '$event', 'player': '$player'}, 'total': {'$sum': 1}}},
        {'$group': {
            '_id':  "$_id.event",
            'totals': {'$push': {'player': '$_id.player', 'total': '$total'}}
        }
        },
    ]))

    # normalize event totals
    for event_data in intermediary_data:
        total = 0
        for player_data in event_data['totals']:
            total += player_data['total']
        for player_data in event_data['totals']:
            player_data['total'] /= total
            player_data['total'] *= 100.0

    data = {
        'charts': [
            build_pie_chart(event, intermediary_data)
            for event in EVENTS
        ]
    }

    return data

# Write the results to a precomputed file. This file will likely be in the static_files
# directory.
def writeToFile(data, file):
    with open(file, 'w+') as out:
        json.dump(data, out)


data = precomputeJSON(args.experiment)
writeToFile(data, args.out if args.out !=
            None else '../static_files/data/diversity_of_labor_by_player_pie_charts.json')
