## Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import json
from math import floor

from uuid_to_playerdata import UUID_MAP

load_dotenv();

MONGO_URI = 'MONGO_URI';
if (MONGO_URI not in os.environ.keys()):
    print('Please add MONGO_URI to your environment variables before using this utility');
    exit(0);

mongo_connection_uri = os.environ.get(MONGO_URI);

PLAYERS = ['14d285df-e64e-41f2-bc4b-979e846c3cec', '6dc38184-c3e7-49ab-a99b-799b01274d01',
           '7d80f280-eaa6-404c-8830-643ccb357b62', 'ffaa5663-850e-4009-80c4-c8bbe34cd285']

parser = argparse.ArgumentParser()
parser.add_argument('--out', help='where to write the data to');
parser.add_argument('--experiment', help='the experiment label to limit the data to');
args = parser.parse_args()

# Precompute the data structure needed for the trophy column chart. This would be
# the "tools" part of the process. This would be run once for each team we have run
# through the map
def precomputeJSON(experimentLabel):
    client = pymongo.MongoClient(mongo_connection_uri, serverSelectionTimeoutMS=5000)
    query = { "event": 'PlayerLocationEvent' }
    if experimentLabel != None:
        query['experimentLabel'] = experimentLabel
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$sort' : { 'time': 1 } },
        { '$project' : { '_id' : 0, 'player': 1, 'x': 1, 'y': 1, 'z': 1, 'time': 1 } },
    ]));

    processed_data = { player: 0 for player in PLAYERS}
    previous_locations = { player: (None, None, None) for player in PLAYERS }
    travelled_set = set()

    for event in intermediary_data:
        x, y, z = floor(event['x']), floor(event['y']), floor(event['z'])
        if (x, y, z) != previous_locations[event['player']] and (x, y, z) in travelled_set:
            processed_data[event['player']] += 1
        previous_locations[event['player']] = (x, y, z)
        travelled_set.add((x, y, z))

    APPROXIMATE_EVENTS_IN_AN_HOUR = 60*60;
    for player in PLAYERS:
        processed_data[player] = processed_data[player] / APPROXIMATE_EVENTS_IN_AN_HOUR

    data = {
        'series': [
            { 
                'name': 'Total Distance Travelled', 
                'data': [processed_data[player] for player in PLAYERS]
            }
        ],
        'categories': [UUID_MAP[player]['name'] for player in PLAYERS],
    }
    return data

# Write the results to a precomputed file. This file will likely be in the static_files 
# directory.
def writeToFile(data, file):
    with open(file, 'w+') as out:
        json.dump(data, out)

data = precomputeJSON(args.experiment)
writeToFile(data, args.out if args.out != None else '../static_files/data/percent_duplicate_location_column_chart.json')