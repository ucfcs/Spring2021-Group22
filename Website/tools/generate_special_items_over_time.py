## DEPRECATED - DO NOT REFERENCE/USE

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

    # Normalize the values by their frequency. Wearing a helmet for 10 minutes
    # should be weighed the same as using the escape rope for 1 second
    items_to_value = {
        'hint_0': max_seconds() / expected_seconds(60),
        'hint_1': max_seconds() / expected_seconds(60),
        'hint_2': max_seconds() / expected_seconds(60),
        'hint_3': max_seconds() / expected_seconds(60),
        'hint_4': max_seconds() / expected_seconds(60),
        'hint_5': max_seconds() / expected_seconds(60),
        'hint_6': max_seconds() / expected_seconds(60),
        'hint_7': max_seconds() / expected_seconds(60),
        'hint_8': max_seconds() / expected_seconds(60),
        'hint_9': max_seconds() / expected_seconds(60),
        'hint_10': max_seconds() / expected_seconds(60),
        'hint_11': max_seconds() / expected_seconds(60),
        'hint_12': max_seconds() / expected_seconds(60),
        'bow': max_seconds() / expected_seconds(2),
        'boots': max_seconds() / expected_minutes(10),
        'chestplate': max_seconds() / expected_minutes(10),
        'leggings': max_seconds() / expected_minutes(10),
        'helmet': max_seconds() / expected_minutes(10),
        'sword': max_seconds() / expected_minutes(5),
        'strong_boots': max_seconds() / expected_minutes(10),
        'strong_chestplate': max_seconds() / expected_minutes(10),
        'strong_leggings': max_seconds() / expected_minutes(10),
        'strong_helmet': max_seconds() / expected_minutes(10),
        'strong_sword': max_seconds() / expected_minutes(5),
        'nether_brick_pickaxe': max_seconds() / expected_seconds(2),
        'use_reveal_players': max_seconds() / expected_seconds(20),
        'use_escape_rope': max_seconds() / expected_seconds(1),
        'cave_torch': max_seconds() / expected_seconds(1),
        'torch': max_seconds() / expected_seconds(1),
        'fireworks': max_seconds() / expected_seconds(4),
        'white_banner': max_seconds() / expected_seconds(1),
        'use_glow_path': max_seconds() / expected_seconds(60),
        'instant_heal_potion': max_seconds() / expected_seconds(1),
        'speed_potion': max_seconds() / expected_minutes(8),
        'invisibility_potion': max_seconds() / expected_minutes(8),
        'trophy': 0, # investigate this? Hypothesis, we had someone wearing the skulls and it triggered the event...
    }

    start_time = list(client.epilog.data2.find(query).sort('time', 1).limit(1))[0]['time']
    processed_data = {}
    for player_data in intermediary_data:
        buckets = []
        for _ in range(60):
            buckets.append(None)
        for event in player_data['events']:
            local_event_time = (int(event['time']) - start_time // 1000) // 60
            if local_event_time < 60:
                buckets[local_event_time] = (0 if buckets[local_event_time] == None else buckets[local_event_time]) + items_to_value[event['special']]
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
writeToFile(data, args.out if args.out != None else '../static_files/data/special_items_over_time.json')