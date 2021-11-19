## TODO this one might be slightly inaccurate?

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, time, timedelta
import matplotlib.pyplot as plt
import json
from uuid_to_playerdata import UUID_MAP
from start_time_util import get_start_time 

PLAYERS = ['14d285df-e64e-41f2-bc4b-979e846c3cec', '6dc38184-c3e7-49ab-a99b-799b01274d01',
           '7d80f280-eaa6-404c-8830-643ccb357b62', 'ffaa5663-850e-4009-80c4-c8bbe34cd285']

def generate_location_spread_time_series(client, experimentLabel):
    query = { 'experimentLabel': experimentLabel if experimentLabel != None else { '$exists': True }, "event": "PlayerLocationEvent" }
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$sort': { 'time': 1 } },
        { '$project' : { '_id' : 0, 'player': 1, 'distances': 1, 'time': 1 } },
    ]))

    start_time = get_start_time(client, experimentLabel) // (60*1000)
    proccessed_data = [{ player: { 'count': 0, 'total': 0 } for player in PLAYERS } for _ in range(60)]
    for event in intermediary_data:
        event_time = event['time'] // (60*1000)

        if (event_time - start_time) < 60:
            data = proccessed_data[(event_time - start_time)][event['player']]
            proccessed_data[(event_time - start_time)][event['player']] = {
                'total': data['total'] + sum(event['distances'].values()) / len(event['distances']),
                'count': data['count'] + 1
            }

    return {
        'series': [{
            'title': 'Average Distance Between All Members Over Time',
            'data': [sum([data[player]['total'] / data[player]['count'] if data[player]['count'] > 0 else 0 for player in PLAYERS]) / len(PLAYERS) for data in proccessed_data],
        }],
        'categories': [i for i in range(60)]
    }