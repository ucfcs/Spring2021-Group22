import pymongo
import argparse
from dotenv import load_dotenv
import os

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
    print(list(client.epilog.data2.aggregate([{'$group': {'_id': { 'experimentLabel': '$experimentLabel' } }}])))