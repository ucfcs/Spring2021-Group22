## Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import json
from math import floor

from uuid_to_playerdata import UUID_MAP



PLAYERS = ['14d285df-e64e-41f2-bc4b-979e846c3cec', '6dc38184-c3e7-49ab-a99b-799b01274d01',
           '7d80f280-eaa6-404c-8830-643ccb357b62', 'ffaa5663-850e-4009-80c4-c8bbe34cd285']

parser = argparse.ArgumentParser()
parser.add_argument('--out', help='where to write the data to');
parser.add_argument('--experiment', help='the experiment label to limit the data to');
args = parser.parse_args()

def generate_percent_duplicate_location_column_chart(client, experimentLabel):
    query = { 'experimentLabel': experimentLabel if experimentLabel != None else { '$exists': True }, "event": 'PlayerLocationEvent' }
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$sort' : { 'time': 1 } },
        { '$project' : { '_id' : 0, 'player': 1, 'x': 1, 'y': 1, 'z': 1, 'time': 1 } },
    ]));

    processed_data = { player: 0 for player in PLAYERS}
    previous_locations = { player: (None, None, None) for player in PLAYERS }
    travelled_set = set()

    for event in intermediary_data:
        x, y, z = floor(event['x']), floor(event['y']), floor(event['z'])
        if (x, y, z) != previous_locations[event['player']] and (x, y, z) in travelled_set:
            processed_data[event['player']] += 1
        previous_locations[event['player']] = (x, y, z)
        travelled_set.add((x, y, z))

    APPROXIMATE_EVENTS_IN_AN_HOUR = 60*60;
    for player in PLAYERS:
        processed_data[player] = (processed_data[player] / APPROXIMATE_EVENTS_IN_AN_HOUR) * 100.0

    return {
        'series': [
            { 
                'name': 'Percentage Overlap', 
                'data': [processed_data[player] for player in PLAYERS]
            }
        ],
        'categories': [UUID_MAP[player]['name'] for player in PLAYERS],
        'colors': [UUID_MAP[player]['color'] for player in PLAYERS],
    }
