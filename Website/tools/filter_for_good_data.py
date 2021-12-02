# Script that filtered out extraneous data that was collected
# as the server was running.

import pymongo
import argparse
from dotenv import load_dotenv
import os
from start_time_util import get_times
from tqdm import tqdm

PLAYERS = ['14d285df-e64e-41f2-bc4b-979e846c3cec', '6dc38184-c3e7-49ab-a99b-799b01274d01',
           '7d80f280-eaa6-404c-8830-643ccb357b62', 'ffaa5663-850e-4009-80c4-c8bbe34cd285']

# Main script
if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--out', help='where to write the data to');
    parser.add_argument('--experiment', help='the experiment label to limit the data to');
    args = parser.parse_args()

    load_dotenv();

    MONGO_URI = 'MONGO_URI';
    if (MONGO_URI not in os.environ.keys()):
        print('Please add MONGO_URI to your environment variables before using this utility');
        exit(0);

    mongo_connection_uri = os.environ.get(MONGO_URI);
    client = pymongo.MongoClient(mongo_connection_uri, serverSelectionTimeoutMS=5000)
    collection = client.epilog.data2
    new_collection = client.epilog["data3"]

    for experiment_label in ['group1', 'Team2', 'Team3', 'Team3take2', 'Team5', 'team6']:
        (start_time, end_time) = get_times(client, experiment_label)
        query = { 
            'experimentLabel': experiment_label, 
            'time': { '$gte': start_time, '$lte': end_time },
            'player': { '$in': PLAYERS },
        }
        for document in collection.find(query):
            new_collection.insert_one(document)
