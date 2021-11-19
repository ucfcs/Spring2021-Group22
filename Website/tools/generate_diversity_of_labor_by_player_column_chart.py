# Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import json
from uuid_to_playerdata import UUID_MAP
from start_time_util import get_times

PLAYERS = ['14d285df-e64e-41f2-bc4b-979e846c3cec', '6dc38184-c3e7-49ab-a99b-799b01274d01',
           '7d80f280-eaa6-404c-8830-643ccb357b62', 'ffaa5663-850e-4009-80c4-c8bbe34cd285']
EVENTS = ['BarrelOpenedEvent', 'CollectTrophyEvent', 'DoFarmEvent',
          'DuneBreakEvent', 'OreBreakEvent', 'SolveMansionPuzzleEvent', 'VillagerTradeEvent']

def generate_diversity_of_labor_by_player_column_chart(client, experimentLabel):
    (start_time, end_time) = get_times(client, experimentLabel)
    query = { 
        'experimentLabel': experimentLabel if experimentLabel != None else { '$exists': True }, 
        'event': {'$in': ['DoFarmEvent', 'OreBreakEvent', 'DuneBreakEvent',
                               'VillagerTradeEvent', 'CollectTrophyEvent', 'BarrelOpenedEvent', 'SolveMansionPuzzleEvent']},
        'time': { '$gte': start_time, '$lte': end_time },
    }
    if experimentLabel != None:
        query['experimentLabel'] = experimentLabel
    intermediary_data = list(client.epilog.data2.aggregate([
        {'$match': query},
        {'$project': {'_id': 0, 'event': '$event', 'player': 1, 'zone': 1}},
        {'$group': {'_id': {'event': '$event', 'player': '$player'}, 'total': {'$sum': 1}}},
        {'$group': {
            '_id':  "$_id.player",
            'totals': {'$push': {'event': '$_id.event', 'total': '$total'}}
        }
        },
    ]))

    # normalize event totals
    event_totals = [0 for _ in EVENTS]
    for player_data in intermediary_data:
        for idx, event in enumerate(EVENTS):
            event_totals[idx] += (
                [event_data['total'] for event_data in player_data['totals']
                    if event_data['event'] == event][0]
                if (len([event_data['total'] for event_data in player_data['totals'] if event_data['event'] == event]) > 0)
                else 0
            )
    for player_data in intermediary_data:
        for idx, event in enumerate(EVENTS):
            for event_data in player_data['totals']:
                if event_data['event'] == event:
                    event_data['total'] /= event_totals[idx]
                    event_data['total'] *= 100.0

    return {
        'series': [{
            'name': UUID_MAP[ player]['name'],
            'data': [
                next((event_data['total'] for event_data in next(
                    (player_data['totals'] for player_data in intermediary_data if player_data['_id'] == player), [])
                    if event_data['event'] == event), 0)
            for event in EVENTS],
        } for player in PLAYERS],
        'categories': EVENTS,
    }
