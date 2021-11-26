# Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import matplotlib.pyplot as plt
import json
from start_time_util import get_times

from uuid_to_playerdata import UUID_MAP

PLAYERS = ['14d285df-e64e-41f2-bc4b-979e846c3cec', '6dc38184-c3e7-49ab-a99b-799b01274d01',
           '7d80f280-eaa6-404c-8830-643ccb357b62', 'ffaa5663-850e-4009-80c4-c8bbe34cd285']

def generate_sharing_column_chart(client, experimentLabel):
    collection = client.epilog.data2
    (start_time, end_time) = get_times(client, experimentLabel)
    query = { 
        'experimentLabel': experimentLabel if experimentLabel != None else { '$exists': True }, 
        'event': 'PlayerPickupItemEvent',
        'time': { '$gte': start_time, '$lte': end_time },
    }
    cursor = collection.find(query, sort=[('time', pymongo.ASCENDING)])
    actions = ['given', 'taken']
    intermediary_data = {}
    for action in actions:
        intermediary_data[action] = {}
    intermediary_player_set = set()
    for event in cursor:
        if event['droppedBy'] == None:
            continue
        if event['droppedBy'] == event['player']:
            continue

        intermediary_player_set.add(event['player'])
        if event['player'] not in intermediary_data['taken']:
            intermediary_data['taken'][event['player']] = 0
        intermediary_data['taken'][event['player']] += 1
        if event['droppedBy'] not in intermediary_data['given']:
            intermediary_data['given'][event['droppedBy']] = 0
        intermediary_data['given'][event['droppedBy']] += 1

    return {
        'series': [{
            'name': action,
            'data': [intermediary_data[action][player] if (player in intermediary_data[action]) else 0 for player in PLAYERS]
        } for action in actions],
        'categories': [UUID_MAP[player]['name'] for player in PLAYERS],
    }
