## Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import json

from uuid_to_playerdata import UUID_MAP

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

# Precompute the data structure needed for the trophy column chart. This would be
# the "tools" part of the process. This would be run once for each team we have run
# through the map
def precomputeJSON(experimentLabel):
    client = pymongo.MongoClient(mongo_connection_uri, serverSelectionTimeoutMS=5000)
    query = { 'experimentLabel': experimentLabel if experimentLabel != None else { '$exists': True }, "event": 'CollectTrophyEvent' }
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$project' : { '_id' : 0, 'player': 1 } },
        { '$group': { '_id' : '$player', 'total': { '$sum': 1 } } },
    ]));

    intermediary_data = sorted(intermediary_data, key=lambda x: x['_id'])

    data = {
        'series': [
            { 
                'name': 'Trophy Count', 
                'data': [data['total'] for data in intermediary_data]
            }
        ],
        'categories': [UUID_MAP[data['_id']]['name'] for data in intermediary_data],
    }
    return data

# Write the results to a precomputed file. This file will likely be in the static_files 
# directory.
def writeToFile(data, file):
    with open(file, 'w+') as out:
        json.dump(data, out)

data = precomputeJSON(args.experiment)
writeToFile(data, args.out if args.out != None else '../static_files/data/trophy_count_column_chart.json')