## Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import json
from uuid_to_playerdata import UUID_MAP
from start_time_util import get_times

PLAYERS = ['14d285df-e64e-41f2-bc4b-979e846c3cec', '6dc38184-c3e7-49ab-a99b-799b01274d01',
           '7d80f280-eaa6-404c-8830-643ccb357b62', 'ffaa5663-850e-4009-80c4-c8bbe34cd285'];
EVENTS = ['PlayerDamageByEntityEvent', 'EntityDamageByPlayerEvent' ];

def generate_entity_damage_interactions_column_chart(client, experimentLabel):
    (start_time, end_time) = get_times(client, experimentLabel)
    query = { 
        'experimentLabel': experimentLabel if experimentLabel != None else { '$exists': True }, 
        'event': { '$in': EVENTS },
        'time': { '$gte': start_time, '$lte': end_time },
    }
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$project' : { '_id' : 0, 'player': 1, 'event': 1, 'damage': 1 } },
        { '$group': { '_id' : { 'player': '$player', 'event': '$event' }, 'total': { '$sum': '$damage' } } },
        { '$group': {
            '_id':  "$_id.event",
            'totals': {'$push': {'player': '$_id.player', 'total': '$total' }}
            }
        },
    ]))

    return {
        'series': [
            { 
                'name': event, 
                'data': [next((player_data['total'] for player_data in next((event_data['totals'] for event_data in intermediary_data if event_data['_id'] == event), []) if player_data['player'] == player), 0) for player in PLAYERS] 
            } for event in EVENTS
        ],
        'categories': [UUID_MAP[player]['name'] for player in PLAYERS],
    }