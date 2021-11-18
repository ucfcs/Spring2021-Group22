## Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import json

from uuid_to_playerdata import UUID_MAP

PLAYERS = ['14d285df-e64e-41f2-bc4b-979e846c3cec', '6dc38184-c3e7-49ab-a99b-799b01274d01',
           '7d80f280-eaa6-404c-8830-643ccb357b62', 'ffaa5663-850e-4009-80c4-c8bbe34cd285']

def generate_total_distance_column_chart(client, experimentLabel):
    query = { 'experimentLabel': experimentLabel if experimentLabel != None else { '$exists': True }, "event": 'PlayerLocationEvent' }
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$sort' : { 'time': 1 } },
        { '$project' : { '_id' : 0, 'player': 1, 'x': 1, 'y': 1, 'z': 1, 'time': 1 } },
        { '$group': { '_id' : '$player', 'events': { '$push': { 'x': '$x', 'y': '$y', 'z': '$z', 'time': '$time' } } } },
    ]));

    processed_data = { player: 0 for player in PLAYERS}
    for player in PLAYERS:
        player_group = next((group['events'] for group in intermediary_data if group['_id'] == player), [])
        total_distance = 0.0
        for i in range(1, len(player_group)):
            a = player_group[i-1]
            b = player_group[i]
            total_distance += ((b['x'] - a['x'])**2+(b['y'] - a['y'])**2+(b['z'] - a['z'])**2)**0.5
        processed_data[player] = total_distance

    return {
        'series': [
            { 
                'name': 'Total Distance Travelled', 
                'data': [processed_data[player] for player in PLAYERS]
            }
        ],
        'categories': [UUID_MAP[player]['name'] for player in PLAYERS],
        'colors': [UUID_MAP[player]['color'] for player in PLAYERS],
    }
