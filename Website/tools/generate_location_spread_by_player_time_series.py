## DEPRECATED - DO NOT REFERENCE/USE

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, time, timedelta
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

# Precompute the data structure needed for the inventory size time series. This would be
# the "tools" part of the process. This would be run once for each team we have run
# through the map
def precomputeJSON(experimentLabel):
    client = pymongo.MongoClient(mongo_connection_uri, serverSelectionTimeoutMS=5000)
    query = { "event": "PlayerLocationEvent" }
    if experimentLabel != None:
        query['experimentLabel'] = experimentLabel
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$sort': { 'time': 1 } },
        { '$project' : { '_id' : 0, 'player': 1, 'distances': 1, 'time': 1 } },
    ]))

    start_time = list(client.epilog.data2.find(query).sort('time', 1).limit(1))[0]['time'] // (60*1000)
    proccessed_data = { player: [{ 'total': 0, 'count': 0 }]*(60) for player in PLAYERS }
    for event in intermediary_data:
        event_time = event['time'] // (60*1000)

        if (event_time - start_time) < 60:
            data = proccessed_data[event['player']][(event_time - start_time)]
            proccessed_data[event['player']][(event_time - start_time)] = {
                'total': data['total'] + sum([event['distances'][uid] for uid in event['distances']]) / len(event['distances']),
                'count': data['count'] + 1
            }

    return {
        'series': [{
            'name': UUID_MAP[player]['name'],
            'data': [data['total'] / data['count'] for data in proccessed_data[player]]
        } for player in PLAYERS],
        'categories': [i for i in range(60)]
    }

# Write the results to a precomputed file. This file will likely be in the static_files 
# directory.
def writeToFile(data, file):
    with open(file, 'w+') as out:
        json.dump(data, out)

data = precomputeJSON(args.experiment)
writeToFile(data, args.out if args.out != None else '../static_files/data/location_spread_by_players_time_series.json')