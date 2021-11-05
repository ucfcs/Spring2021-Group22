## Simple tool for querying the database for common events

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

# I've changed the schema so without this the script crashes. This check will
# eventually be unnecessary
def isGoodData(event):
    return 'droppedBy' in event;

parser = argparse.ArgumentParser()
parser.add_argument('--out', help='where to write the data to');
parser.add_argument('--experiment', help='the experiment label to limit the data to');
args = parser.parse_args()

# Precompute the data structure needed for the sharing column chart. This would be
# the "tools" part of the process. This would be run once for each team we have run
# through the map
def precomputeJSON(experimentLabel):
    client = pymongo.MongoClient(mongo_connection_uri, serverSelectionTimeoutMS=5000)
    collection = client.epilog.data2;
    query = { "event": "PlayerPickupItemEvent" }
    if experimentLabel != None:
        query['experimentLabel'] = experimentLabel
    cursor = collection.find(query, sort=[('time', pymongo.ASCENDING)])
    actions = ['given', 'taken']
    intermediary_data = {};
    for action in actions:
        intermediary_data[action] = {}
    intermediary_player_set = set()
    for event in cursor:
        if not isGoodData(event):
            continue

        if event['droppedBy'] == None:
            continue
        # if event['droppedBy'] == event['player']:
        #     continue

        intermediary_player_set.add(event['player'])
        if event['player'] not in intermediary_data['taken']:
            intermediary_data['taken'][event['player']] = 0
        intermediary_data['taken'][event['player']]+=1;
        if event['droppedBy'] not in intermediary_data['given']:
            intermediary_data['given'][event['droppedBy']] = 0
        intermediary_data['given'][event['droppedBy']]+=1;

    players = list(intermediary_player_set)
    data = {
        'series': [{ 
                'name': action, 
                'data': [intermediary_data[action][player] if (player in intermediary_data[action]) else 0 for player in players] 
            } for action in actions],
        'categories': [UUID_MAP[player]['name'] for player in players],
    }
    return data

# Write the results to a precomputed file. This file will likely be in the static_files 
# directory.
def writeToFile(data, file):
    with open(file, 'w+') as out:
        json.dump(data, out)

data = precomputeJSON(args.experiment)
writeToFile(data, args.out if args.out != None else '../static_files/data/sharing_column_chart.json')