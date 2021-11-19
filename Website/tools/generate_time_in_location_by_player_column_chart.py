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
from start_time_util import get_times

PLAYERS = ['14d285df-e64e-41f2-bc4b-979e846c3cec', '6dc38184-c3e7-49ab-a99b-799b01274d01',
           '7d80f280-eaa6-404c-8830-643ccb357b62', 'ffaa5663-850e-4009-80c4-c8bbe34cd285']
ZONES = ['Cave', 'Center', 'Dunes', 'Farms', 'Forest', 'Mansion', 'Maze']

def generate_time_in_location_by_player_column_chart(client, experimentLabel):
    (start_time, end_time) = get_times(client, experimentLabel)
    query = { 
        'experimentLabel': experimentLabel if experimentLabel != None else { '$exists': True }, 
        'event': 'PlayerLocationEvent',
        'time': { '$gte': start_time, '$lte': end_time },
    }
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$project' : { '_id' : 0, 'player': 1, 'zone': 1 } },
        { '$group': { '_id' : { 'zone': '$zone', 'player': '$player'}, 'total': { '$sum': 1 } } },
        { '$group' : { 
            '_id' :  "$_id.zone",
            'totals': { '$push': { 'player': '$_id.player', 'total': '$total'} }
            } 
        },
    ]))

    return {
        'series': [{ 
                'name': zone, 
                'data': [
                    next((player_data['total'] for player_data in next(
                        (zone_data['totals'] for zone_data in intermediary_data if zone_data['_id'] == zone), []
                    ) if player_data['player'] == player), 0)
                for player in PLAYERS],
            } for zone in ZONES],
        'colors': [UUID_MAP[player]['color'] for player in PLAYERS],
        'categories': [UUID_MAP[player]['name'] for player in PLAYERS],
    }