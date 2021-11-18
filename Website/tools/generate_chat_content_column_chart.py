import pymongo
import argparse
from dotenv import load_dotenv
import os
from uuid_to_playerdata import UUID_MAP



def generate_chat_content_column_chart(client, experimentLabel):
    query = { 'experimentLabel': experimentLabel if experimentLabel != None else { '$exists': True }, "event": 'AsyncPlayerChatEvent' }
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$project' : { '_id' : 0, 'player': 1, 'msg': 1 } },
        { '$group': { '_id' : '$player', 'total': { '$sum': { '$strLenCP': '$msg' } } } },
    ]))

    intermediary_data = sorted(intermediary_data, key=lambda x: x['_id'])

    return {
        'series': [
            { 
                'name': 'Messages Length', 
                'data': [data['total'] for data in intermediary_data] 
            }
        ],
        'categories': [UUID_MAP[data['_id']]['name'] for data in intermediary_data],
        'colors': [UUID_MAP[data['_id']]['color'] for data in intermediary_data],
    }