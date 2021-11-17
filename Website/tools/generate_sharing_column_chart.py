# Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import matplotlib.pyplot as plt
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

parser = argparse.ArgumentParser()
parser.add_argument('--out', help='where to write the data to')
parser.add_argument(
    '--experiment', help='the experiment label to limit the data to')
args = parser.parse_args()

# Precompute the data structure needed for the sharing column chart. This would be
# the "tools" part of the process. This would be run once for each team we have run
# through the map


def precomputeJSON(experimentLabel):
    client = pymongo.MongoClient(
        mongo_connection_uri, serverSelectionTimeoutMS=5000)
    collection = client.epilog.data2
    query = { 'experimentLabel': experimentLabel if experimentLabel != None else { '$exists': True }, "event": "PlayerPickupItemEvent"}
    cursor = collection.find(query, sort=[('time', pymongo.ASCENDING)])
    actions = ['given', 'taken']
    intermediary_data = {}
    for action in actions:
        intermediary_data[action] = {}
    intermediary_player_set = set()
    for event in cursor:
        if event['droppedBy'] == None:
            continue
        if event['droppedBy'] == event['player']:
            continue

        intermediary_player_set.add(event['player'])
        if event['player'] not in intermediary_data['taken']:
            intermediary_data['taken'][event['player']] = 0
        intermediary_data['taken'][event['player']] += 1
        if event['droppedBy'] not in intermediary_data['given']:
            intermediary_data['given'][event['droppedBy']] = 0
        intermediary_data['given'][event['droppedBy']] += 1

    data = {
        'series': [{
            'name': action,
            'data': [intermediary_data[action][player] if (player in intermediary_data[action]) else 0 for player in PLAYERS]
        } for action in actions],
        'categories': [UUID_MAP[player]['name'] for player in PLAYERS],
    }
    return data

# Write the results to a precomputed file. This file will likely be in the static_files
# directory.


def writeToFile(data, file):
    with open(file, 'w+') as out:
        json.dump(data, out)


data = precomputeJSON(args.experiment)
writeToFile(data, args.out if args.out !=
            None else '../static_files/data/sharing_column_chart.json')
