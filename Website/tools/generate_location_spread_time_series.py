## DEPRECATED - DO NOT REFERENCE/USE

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, time, timedelta
import matplotlib.pyplot as plt
import json

load_dotenv();

MONGO_URI = 'MONGO_URI';
if (MONGO_URI not in os.environ.keys()):
    print('Please add MONGO_URI to your environment variables before using this utility');
    exit(0);

mongo_connection_uri = os.environ.get(MONGO_URI);

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
        { '$project' : { '_id' : 0, 'distances': 1, 'time': 1 } },
    ]))

    game_start = None
    time_cursor = None
    buckets = []
    total_events = 0
    total_distances = 0
    for event in intermediary_data:
        event_time = event['time']
        # counter += 1
        if time_cursor == None:
            game_start = event_time
            time_cursor = event_time

        if event_time - game_start > 60 * 60 * 1000:
            break

        if event_time - time_cursor > 60*1000:
            # print(datetime.fromtimestamp(event_time//1000).strftime('%c'), datetime.fromtimestamp(time_cursor//1000).strftime('%c'))
            buckets.append(total_distances / total_events)
            total_distances = 0
            total_events = 0
            time_cursor = event_time

        total_distances += sum([event['distances'][uid] for uid in event['distances']]) / len(event['distances'])
        total_events += 1

    return {
        'series': [{
            'title': 'Distance',
            'data': buckets
        }],
        'categories': [idx for idx, _ in enumerate(buckets)]
    }

# Write the results to a precomputed file. This file will likely be in the static_files 
# directory.
def writeToFile(data, file):
    with open(file, 'w+') as out:
        json.dump(data, out)

data = precomputeJSON(args.experiment)
writeToFile(data, args.out if args.out != None else '../static_files/data/location_spread_time_series.json')