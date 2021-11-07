## TODO this file is almost 100% a copy-paste of the weighted version

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import matplotlib.pyplot as plt
import json
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

def max_seconds():
    return 60*60

def expected_seconds(x):
    return x

def expected_minutes(x):
    return x*60

def interpolate_missing_values(buckets):
    run = { 'location': len(buckets)-1, 'value': 0 }
    forwardLookup = [{ 'location': len(buckets)-1, 'value': 0 }]*(len(buckets))
    for i in range(len(buckets)):
        j = len(buckets)-1 - i
        if buckets[j] != None:
            run = { 'location': j, 'value': buckets[j]}
        forwardLookup[j] = run
    run_forward = { 'location': 0, 'value': 0 }
    interpolated_values = [0]*len(buckets)
    for i in range(len(buckets)-1):
        if buckets[i] != None:
            run_forward = { 'location': i, 'value': buckets[i] }
        interpolated_values[i] = (forwardLookup[i+1]['value'] - run_forward['value'])/(forwardLookup[i+1]['location'] - run_forward['location'])*(i-run_forward['location'])+run_forward['value']
    interpolated_values[len(buckets)-1] = buckets[len(buckets)-1] if buckets[len(buckets)-1] != None else 0
    return interpolated_values

# Precompute the data structure needed for the inventory size time series. This would be
# the "tools" part of the process. This would be run once for each team we have run
# through the map
def precomputeJSON(experimentLabel):
    client = pymongo.MongoClient(mongo_connection_uri, serverSelectionTimeoutMS=5000)
    query = { "event": "UsingSpecialItemEvent" }
    if experimentLabel != None:
        query['experimentLabel'] = experimentLabel
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$sort': { 'time': 1 } },
        { '$project' : { '_id' : 0, 'player': 1, 'special': 1, 'time': 1 } },
        { '$group': { '_id' : '$player', 'events': { '$push': { 'special': '$special', 'time': { '$floor': { '$divide': ['$time', 1000] } } } } } },
    ]))

    start_time = list(client.epilog.data2.find(query).sort('time', 1).limit(1))[0]['time']
    processed_data = {}
    for player_data in intermediary_data:
        buckets = []
        for _ in range(60):
            buckets.append(None)
        for event in player_data['events']:
            local_event_time = (int(event['time']) - start_time // 1000) // 60
            if local_event_time < 60:
                buckets[local_event_time] = (0 if buckets[local_event_time] == None else buckets[local_event_time]) + 1
        processed_data[player_data['_id']] = interpolate_missing_values(buckets)

    return {
        'series': [{
            'name': UUID_MAP[player]['name'],
            'data': processed_data[player]
        } for player in PLAYERS],
        'categories': [idx for idx, _ in enumerate(buckets)]
    }

# Write the results to a precomputed file. This file will likely be in the static_files 
# directory.
def writeToFile(data, file):
    with open(file, 'w+') as out:
        json.dump(data, out)

data = precomputeJSON(args.experiment)
writeToFile(data, args.out if args.out != None else '../static_files/data/special_items_over_time_unweighted.json')