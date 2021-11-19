## Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import json
from uuid_to_playerdata import UUID_MAP

PLAYERS = ['14d285df-e64e-41f2-bc4b-979e846c3cec', '6dc38184-c3e7-49ab-a99b-799b01274d01',
           '7d80f280-eaa6-404c-8830-643ccb357b62', 'ffaa5663-850e-4009-80c4-c8bbe34cd285'];
           
def generate_chat_count_column_chart(client, experimentLabel):
    query = { 'experimentLabel': experimentLabel if experimentLabel != None else { '$exists': True }, "event": 'AsyncPlayerChatEvent' }
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$project' : { '_id' : 0, 'player': 1, 'msg': 1 } },
        { '$group': { '_id' : '$player', 'total': { '$sum': 1 } } },
    ]))

    intermediary_data = sorted(intermediary_data, key=lambda x: x['_id'])

    return {
        'series': [
            { 
                'name': 'Messages Count', 
                'data': [next((data['total'] for data in intermediary_data if data['_id'] == player), 0) for player in PLAYERS] 
            }
        ],
        'categories': [UUID_MAP[player]['name'] for player in PLAYERS],
        'colors': [UUID_MAP[player]['color'] for player in PLAYERS],
    }