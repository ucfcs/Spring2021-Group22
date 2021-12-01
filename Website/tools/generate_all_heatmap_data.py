# Gets a list of all teams then calls generate_heatmap_data for each team
# also does it for all teams in one csv

import pymongo
from dotenv import load_dotenv
import os
import ssl

from collections import defaultdict

from utils import printProgressBar, writeToFile

from generate_heatmap_data import main as generate_heatmap_data

load_dotenv()

def main():
    print('Connecting to Mongo')

    client = pymongo.MongoClient(
        os.getenv('MONGO_URI'), ssl_cert_reqs=ssl.CERT_NONE)
    collection = client.epilog.data

    for experimentLabel in collection.find().distinct('experimentLabel'):
        print(experimentLabel)
        generate_heatmap_data([experimentLabel])
    generate_heatmap_data(['overall'])
    

if __name__ == '__main__':
    main()
