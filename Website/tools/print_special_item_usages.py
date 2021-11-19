import pymongo
import argparse
from dotenv import load_dotenv
import os
from uuid_to_playerdata import UUID_MAP
from start_time_util import get_start_time 

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

    experimentLabel = args.experiment
    
    query = { 'experimentLabel': experimentLabel if experimentLabel != None else { '$exists': True }, "event": "UsingSpecialItemEvent" }
    intermediary_data = list(client.epilog.data2.aggregate([
        {'$match': query},
        {'$project': {'_id': 0, 'special': 1, 'player': 1, 'time': 1 }},
        {'$group': {'_id': {'player': '$player', 'special': '$special'}, 'total': {'$sum': 1}, 'events': { '$push': { 'time': '$time' } } }},
        {'$group': {
            '_id':  "$_id.player",
            'totals': {'$push': {'special': '$_id.special', 'total': '$total', 'events': '$events' }}
        }
        },
    ]))
    start_time = get_start_time(client, experimentLabel) // (60*1000)
    for entry in intermediary_data:
        print('--------------------------------')
        print(UUID_MAP[entry['_id']]['name'])
        for total in entry['totals']:
            times = sorted([a['time'] // (60*1000) - start_time for a in total['events']])
            print(total['special'], total['total'], times)