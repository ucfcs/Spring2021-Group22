## Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import json
from math import floor
from uuid_to_playerdata import UUID_MAP, ZONE_MAP
from start_time_util import get_start_time 

PLAYERS = ['14d285df-e64e-41f2-bc4b-979e846c3cec', '6dc38184-c3e7-49ab-a99b-799b01274d01',
           '7d80f280-eaa6-404c-8830-643ccb357b62', 'ffaa5663-850e-4009-80c4-c8bbe34cd285']
ZONES = ['Cave', 'Center', 'Dunes', 'Farms', 'Forest', 'Mansion', 'Maze']

def generate_zone_timeline_chart(client, experimentLabel):
    query = { 
        'experimentLabel': experimentLabel if experimentLabel != None else { '$exists': True }, 
        'event': 'PlayerLocationEvent',
    }
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$sort' : { 'time': 1 } },
        { '$project' : { '_id' : 0, 'player': 1, 'zone': 1, 'time': 1 } },
        { '$group': { '_id' : '$player', 'events': { '$push': { 'zone': '$zone', 'time': '$time' } } } },
    ]));

    processed_data = { player: [] for player in PLAYERS}

    start_time = get_start_time(client, experimentLabel) // (1000)
    for player_data in intermediary_data:
        previous_start_time = None
        previous_zone = None
        bucket = []

        for event in player_data['events']:
            zone, time = event['zone'], floor(event['time']) // 1000
            local_time = time - start_time
            
            if previous_start_time == None: previous_start_time = local_time
            if previous_zone == None: previous_zone = zone

            if zone != previous_zone:
                bucket.append({'zone': previous_zone, 'range': [previous_start_time, local_time-1]})
                previous_start_time = local_time
                previous_zone = zone
        
        if previous_start_time != player_data['events'][-1]['time'] // 1000:
            bucket.append({ 'zone': previous_zone, 'range': [previous_start_time, local_time-1]})
            previous_start_time = local_time
            previous_zone = zone

        processed_data[player_data['_id']] = bucket

    transformed_data = { zone: [] for zone in ZONES }
    for player in PLAYERS:
        for data in processed_data[player]:
            transformed_data[data['zone']].append({
                'x': UUID_MAP[player]['name'],
                'y': data['range']
            });
    
    return {
        'series': [
            { 
                'name': zone, 
                'data': transformed_data[zone],
                'color': ZONE_MAP[zone],
            }
        for zone in ZONES],
    }
