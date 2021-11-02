## Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
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

client = pymongo.MongoClient(mongo_connection_uri, serverSelectionTimeoutMS=5000)
res = list(client.epilog.data2.aggregate([
    { '$match' : { 'event' : 'InventoryContent' } },
    { '$project' : { '_id' : 0, 'player': 1, 'totalSize': 1, 'time': 1 } },
    { '$group': { '_id' : '$player', 'events': { '$push': { 'totalSize': '$totalSize', 'time': { '$floor': { '$divide': ['$time', 1000] } } } } } },
    { '$limit': 1 }
]))
print(res[0])