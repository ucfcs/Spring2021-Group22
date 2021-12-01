# Gets a list of all teams then calls generate_squigglemap_data for each team

import pymongo
from dotenv import load_dotenv
import os
import ssl

from collections import defaultdict

from utils import printProgressBar, writeToFile

from generate_squigglemap_data import main as generate_squigglemap_data

load_dotenv()

def main():
    print('Connecting to Mongo')

    client = pymongo.MongoClient(
        os.getenv('MONGO_URI'), ssl_cert_reqs=ssl.CERT_NONE)
    collection = client.epilog.data

    for experimentLabel in collection.find().distinct('experimentLabel'):
        print(experimentLabel)
        generate_squigglemap_data([experimentLabel])

if __name__ == '__main__':
    main()
