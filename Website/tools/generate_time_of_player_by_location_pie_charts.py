## Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import matplotlib.pyplot as plt
import json
from zoning_util import ALL_ZONES, getZone
from uuid_to_playerdata import UUID_MAP, ZONE_MAP
from start_time_util import get_times

PLAYERS = ['14d285df-e64e-41f2-bc4b-979e846c3cec', '6dc38184-c3e7-49ab-a99b-799b01274d01',
           '7d80f280-eaa6-404c-8830-643ccb357b62', 'ffaa5663-850e-4009-80c4-c8bbe34cd285']
ZONES = ['Cave', 'Center', 'Dunes', 'Farms', 'Forest', 'Mansion', 'Maze']

def build_pie_chart(player, intermediary_data):
    player_data = next((data['totals'] for data in intermediary_data if data['_id'] == player), [])
    return {
        'title': 'Time Spent in Zones for ' + UUID_MAP[player]['name'],
        'series': [next((data['total'] for data in player_data if data['zone'] == zone), 0) for zone in ZONES],
        'colors': [ZONE_MAP[zone] for zone in ZONES],
        'labels': ZONES,
    };

def generate_time_of_player_by_location_pie_charts(client, experimentLabel):
    (start_time, end_time) = get_times(client, experimentLabel)
    query = { 
        'experimentLabel': experimentLabel if experimentLabel != None else { '$exists': True }, 
        'event': 'PlayerLocationEvent',
        'time': { '$gte': start_time, '$lte': end_time },
    }
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$project' : { '_id' : 0, 'player': 1, 'zone': 1 } },
        { '$sort': { 'zone': 1 } },
        { '$group': { '_id' : { 'zone': '$zone', 'player': '$player'}, 'total': { '$sum': 1 } } },
        { '$group' : { 
            '_id' :  "$_id.player",
            'totals': { '$push': { 'zone': '$_id.zone', 'total': '$total'} }
            } 
        },
        { '$sort': { '_id': 1 } },
    ]))

    # normalize event totals
    for event_data in intermediary_data:
        total = 0
        for player_data in event_data['totals']:
            total += player_data['total']
        for player_data in event_data['totals']:
            player_data['total'] /= total
            player_data['total'] *= 100.0

    return {
        'charts': [
            build_pie_chart(player, intermediary_data)
            for player in PLAYERS
        ]
    }