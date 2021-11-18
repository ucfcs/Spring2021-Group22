import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, time, timedelta
import matplotlib.pyplot as plt
import json
from uuid_to_playerdata import UUID_MAP



PLAYERS = ['14d285df-e64e-41f2-bc4b-979e846c3cec', '6dc38184-c3e7-49ab-a99b-799b01274d01',
           '7d80f280-eaa6-404c-8830-643ccb357b62', 'ffaa5663-850e-4009-80c4-c8bbe34cd285']

parser = argparse.ArgumentParser()
parser.add_argument('--out', help='where to write the data to');
parser.add_argument('--experiment', help='the experiment label to limit the data to');
args = parser.parse_args()

def generate_distance_from_center_by_player_time_series(client, experimentLabel):
    query = { 
        'experimentLabel': experimentLabel if experimentLabel != None else { '$exists': True }, 
        "event": "PlayerLocationEvent" 
    }
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$sort': { 'time': 1 } },
        { '$project' : { '_id' : 0, 'player': 1, 'x': 1, 'z': 1, 'time': 1 } },
    ]))

    start_time = list(client.epilog.data2.find(query).sort('time', 1).limit(1))[0]['time'] // (60*1000)
    proccessed_data = { player: [{ 'total': 0, 'count': 0 }]*(60) for player in PLAYERS }
    for event in intermediary_data:
        event_time = event['time'] // (60*1000)

        if (event_time - start_time) < 60:
            data = proccessed_data[event['player']][(event_time - start_time)]
            dx = event['x'] - 4;
            dz = event['z'] - 0;
            proccessed_data[event['player']][(event_time - start_time)] = {
                'total': data['total'] + (dx*dx + dz*dz)**0.5,
                'count': data['count'] + 1
            }

    return {
        'series': [{
            'name': UUID_MAP[player]['name'],
            'data': [(data['total'] / data['count'] if data['count'] > 0 else 0) for data in proccessed_data[player]]
        } for player in PLAYERS],
        'categories': [i for i in range(60)]
    }