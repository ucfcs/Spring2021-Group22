## Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import json

from uuid_to_playerdata import UUID_MAP

def generate_trophy_count_column_chart(client, experimentLabel):
    query = { 'experimentLabel': experimentLabel if experimentLabel != None else { '$exists': True }, "event": 'CollectTrophyEvent' }
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$project' : { '_id' : 0, 'player': 1 } },
        { '$group': { '_id' : '$player', 'total': { '$sum': 1 } } },
    ]));

    intermediary_data = sorted(intermediary_data, key=lambda x: x['_id'])

    return {
        'series': [
            { 
                'name': 'Trophy Count', 
                'data': [data['total'] for data in intermediary_data]
            }
        ],
        'categories': [UUID_MAP[data['_id']]['name'] for data in intermediary_data],
        'colors': [UUID_MAP[data['_id']]['color'] for data in intermediary_data],
    }