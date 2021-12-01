import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime

def print_bar(size):
    for _ in range(size):
        print('=', end='')

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

    RECORD_PERIOD_IN_SECONDS = 60

    mongo_connection_uri = os.environ.get(MONGO_URI);
    client = pymongo.MongoClient(mongo_connection_uri, serverSelectionTimeoutMS=5000)
    results = list(client.epilog.data2.aggregate([
        { '$project': { 'experimentLabel': 1, 'time': { '$floor': { '$divide': ['$time', RECORD_PERIOD_IN_SECONDS*1000] } } } },
        { '$sort' : { 'time': 1 } },
        { '$group': { '_id': '$time', 'total': { '$sum': 1 }, 'labels': { '$push': '$experimentLabel' } }}
    ], allowDiskUse=True))
    min_time = min((int(data['_id']) for data in results))
    max_time = max((int(data['_id']) for data in results))
    last_real_data_time = min_time
    for i in range(min_time, max_time+1):
        datestring = datetime.fromtimestamp(i*RECORD_PERIOD_IN_SECONDS).strftime("%Y-%m-%d %H:%M:%S")
        data_point = next((data for data in results if data['_id'] == i), { '_id': i, 'total': 0, 'labels': [] })
        total = data_point['total']
        label = next((data for data in data_point['labels'] if data != None), None)
        if total > 0 or (i - last_real_data_time) < 10:
            print(datestring + ' ' + repr(label) + ': ', end='')
            print_bar(total)
            print()
        last_real_data_time = i if total > 0 else last_real_data_time
