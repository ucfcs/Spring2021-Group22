## Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import matplotlib.pyplot as plt

load_dotenv();

MONGO_URI = 'MONGO_URI';
if (MONGO_URI not in os.environ.keys()):
    print('Please add MONGO_URI to your environment variables before using this utility');
    exit(0);

mongo_connection_uri = os.environ.get(MONGO_URI);

# I've changed the schema so without this the script crashes. This check will
# eventually be unnecessary
def isGoodData(event):
    return 'totalSize' in event;

parser = argparse.ArgumentParser()
parser.add_argument('--experiment', help='the experiment label to limit the data to');
args = parser.parse_args()


def precomputeJSON(experimentLabel):
    client = pymongo.MongoClient(mongo_connection_uri, serverSelectionTimeoutMS=5000)
    collection = client.epilog.data2;
    query = { "event": "InventoryContent" }
    if experimentLabel != None:
        query['experimentLabel'] = experimentLabel
    cursor = collection.find(query, sort=[('time', pymongo.ASCENDING)])
    data = {};
    for event in cursor:
        if not isGoodData(event):
            continue
        if event['player'] not in data:
            data[event['player']] = {'x': [], 'y': []}
        data[event['player']]['x'].append(event['time'] // 1000);
        data[event['player']]['y'].append(event['totalSize']);
    return data

# Simulate the client. THIS IS A SERVER SIDE MOCK. THE ACTUAL VISUALIZATIONS
# WILL BE JS.
def generatePlot(data):
    plt.title('Inventory Sizes Over Time')
    for player in data:
        x = [datetime.fromtimestamp(x) for x in data[player]['x']]
        y = data[player]['y'];
        plt.plot(x, y);
    plt.show();




def __main__():
    data = precomputeJSON(args.experiment)
    # "data" is saved to json file
    #
    # waiting... waiting... waiting...
    #
    # Client asks for data, data is served to client for visualization
    generatePlot(data)