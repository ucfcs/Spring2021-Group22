## Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import matplotlib.pyplot as plt
import json
from zoning_util import ALL_ZONES, getZone
from uuid_to_playerdata import UUID_MAP

load_dotenv();

MONGO_URI = 'MONGO_URI';
if (MONGO_URI not in os.environ.keys()):
    print('Please add MONGO_URI to your environment variables before using this utility');
    exit(0);

mongo_connection_uri = os.environ.get(MONGO_URI);

# I've changed the schema so without this the script crashes. This check will
# eventually be unnecessary
def isGoodData(event):
    return True;

parser = argparse.ArgumentParser()
parser.add_argument('--out', help='where to write the data to');
parser.add_argument('--experiment', help='the experiment label to limit the data to');
args = parser.parse_args()

# Precompute the data structure needed for the sharing column chart. This would be
# the "tools" part of the process. This would be run once for each team we have run
# through the map
def precomputeJSON(experimentLabel):
    client = pymongo.MongoClient(mongo_connection_uri, serverSelectionTimeoutMS=5000)
    query = { "event": 'PlayerLocationEvent' }
    if experimentLabel != None:
        query['experimentLabel'] = experimentLabel
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$project' : { '_id' : 0, 'player': 1, 'zone': 1 } },
        { '$group': { '_id' : { 'zone': '$zone', 'player': '$player'}, 'total': { '$sum': 1 } } },
        { '$group' : { 
            '_id' :  "$_id.player",
            'totals': { '$push': { 'zone': '$_id.zone', 'total': '$total'} }
            } 
        },
        { '$limit': 4 }
    ]))

    zones = set()
    for player in intermediary_data:
        for total in player['totals']:
            zones.add(total['zone'])
    zones = sorted(list(zones))
    players = set()
    for player in intermediary_data:
        players.add(player['_id'])
    players = sorted(list(players))
    data = {
        'series': [{ 
                'name': UUID_MAP[player_data['_id']]['name'], 
                'data': [
                    (
                        [zone_data['total'] for zone_data in player_data['totals'] if zone_data['zone'] == zone][0]
                        if (len([zone_data['total'] for zone_data in player_data['totals'] if zone_data['zone'] == zone]) > 0) 
                        else 0
                    ) 
                for zone in zones] 
            } for player_data in intermediary_data],
        'colors': [UUID_MAP[player_data['_id']]['color'] for player_data in intermediary_data],
        'categories': zones,
    }
    return data

# Write the results to a precomputed file. This file will likely be in the static_files 
# directory.
def writeToFile(data, file):
    with open(file, 'w+') as out:
        json.dump(data, out)

data = precomputeJSON(args.experiment)
writeToFile(data, args.out if args.out != None else '../static_files/data/time_of_player_by_location_column_chart.json')