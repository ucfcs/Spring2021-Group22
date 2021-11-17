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
ZONES = ['Cave', 'Center', 'Dunes', 'Farms', 'Forest', 'Mansion', 'Maze']

parser = argparse.ArgumentParser()
parser.add_argument('--out', help='where to write the data to');
parser.add_argument('--experiment', help='the experiment label to limit the data to');
args = parser.parse_args()

# Precompute the data structure needed for the trophy column chart. This would be
# the "tools" part of the process. This would be run once for each team we have run
# through the map
def precomputeJSON(experimentLabel):
    client = pymongo.MongoClient(mongo_connection_uri, serverSelectionTimeoutMS=5000)
    query = { 'experimentLabel': experimentLabel if experimentLabel != None else { '$exists': True }, "event": 'PlayerLocationEvent' }
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$sort' : { 'time': 1 } },
        { '$project' : { '_id' : 0, 'player': 1, 'zone': 1, 'time': 1 } },
        { '$group': { '_id' : '$player', 'events': { '$push': { 'zone': '$zone', 'time': '$time' } } } },
    ]));

    processed_data = { player: [] for player in PLAYERS}

    start_query = {}
    if experimentLabel != None:
        start_query['experimentLabel'] = experimentLabel 
    start_time = list(client.epilog.data2.find(start_query).sort('time', 1).limit(1))[0]['time'] // 1000
    for player_data in intermediary_data:
        previous_start_time = None
        previous_zone = None
        bucket = []

        for event in player_data['events']:
            zone, time = event['zone'], floor(event['time']) // 1000
            local_time = time - start_time
            
            if previous_start_time == None: previous_start_time = local_time
            if previous_zone == None: previous_zone = zone

            if zone != previous_zone:
                bucket.append({'zone': previous_zone, 'range': [previous_start_time, local_time-1]})
                previous_start_time = local_time
                previous_zone = zone
        
        if previous_start_time != player_data['events'][-1]['time'] // 1000:
            bucket.append({'zone': previous_zone, 'range': [previous_start_time, local_time-1]})
            previous_start_time = local_time
            previous_zone = zone

        processed_data[player_data['_id']] = bucket

    transformed_data = { zone: [] for zone in ZONES }
    for player in PLAYERS:
        for data in processed_data[player]:
            transformed_data[data['zone']].append({
                'x': UUID_MAP[player]['name'],
                'y': data['range']
            });
    
    data = {
        'series': [
            { 
                'name': zone, 
                'data': transformed_data[zone]
            }
        for zone in ZONES],
    }
    return data

# Write the results to a precomputed file. This file will likely be in the static_files 
# directory.
def writeToFile(data, file):
    with open(file, 'w+') as out:
        json.dump(data, out)

data = precomputeJSON(args.experiment)
writeToFile(data, args.out if args.out != None else '../static_files/data/zone_timeline.json')