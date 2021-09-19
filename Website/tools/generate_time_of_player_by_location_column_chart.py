## Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import matplotlib.pyplot as plt
import json
from zoning_util import ALL_ZONES, getZone

load_dotenv();

MONGO_URI = 'MONGO_URI';
if (MONGO_URI not in os.environ.keys()):
    print('Please add MONGO_URI to your environment variables before using this utility');
    exit(0);

mongo_connection_uri = os.environ.get(MONGO_URI);

# I've changed the schema so without this the script crashes. This check will
# eventually be unnecessary
def isGoodData(event):
    return True;

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
    query = { "event": "PlayerLocationEvent" }
    if experimentLabel != None:
        query['experimentLabel'] = experimentLabel
    cursor = collection.find(query, sort=[('time', pymongo.ASCENDING)])
    zone_names = [zone['name'] for zone in ALL_ZONES]
    intermediary_data = {}
    intermediary_player_set = set()
    for event in cursor:
        if not isGoodData(event):
            continue
        
        intermediary_player_set.add(event['player'])
        event_zone = getZone(event['x'], event['y'], event['z'])
        if event['player'] not in intermediary_data:
            init_data = {}
            for zone in zone_names:
                init_data[zone] = 0
            intermediary_data[event['player']] = init_data
        intermediary_data[event['player']][event_zone]+=1;

    players = list(intermediary_player_set)
    data = {
        'series': [{ 
                'name': player, 
                'data': [intermediary_data[player][event_zone] for event_zone in zone_names] 
            } for player in players],
        'categories': zone_names,
    }
    return data

# Write the results to a precomputed file. This file will likely be in the static_files 
# directory.
def writeToFile(data, file):
    with open(file, 'w+') as out:
        json.dump(data, out)

data = precomputeJSON(args.experiment)
writeToFile(data, args.out if args.out != None else '../static_files/data/time_of_player_by_location_column_chart.json')