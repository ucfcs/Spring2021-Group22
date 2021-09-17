## Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import matplotlib.pyplot as plt
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
    query = { "event": "EntityPickupItemEvent" }
    if experimentLabel != None:
        query['experimentLabel'] = experimentLabel
    cursor = collection.find(query, sort=[('time', pymongo.ASCENDING)])
    data = {};
    for event in cursor:
        if not isGoodData(event):
            continue
        if event['droppedBy'] == None:
            continue
        if event['droppedBy'] == event['player']:
            continue

        if event['player'] not in data:
            data[event['player']] = { "taken": 0, "given": 0 }
        if event['droppedBy'] not in data:
            data[event['droppedBy']] = { "taken": 0, "given": 0 }
        data[event['player']]['taken']+=1;
        data[event['droppedBy']]['given']+=1;
        
    return data

# Write the results to a precomputed file. This file will likely be in the static_files 
# directory.
def writeToFile(data, file):
    with open(file, 'w+') as out:
        json.dump(data, out)

data = precomputeJSON(args.experiment)
writeToFile(data, args.out if args.out != None else '../static_files/sharing_column_chart.json')