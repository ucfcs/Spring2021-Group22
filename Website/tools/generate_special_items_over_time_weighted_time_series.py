## DEPRECATED - DO NOT REFERENCE/USE

import pymongo
import argparse
from dotenv import load_dotenv
import os
from datetime import datetime, timedelta
import matplotlib.pyplot as plt
import json
from uuid_to_playerdata import UUID_MAP

PLAYERS = ['14d285df-e64e-41f2-bc4b-979e846c3cec', '6dc38184-c3e7-49ab-a99b-799b01274d01',
           '7d80f280-eaa6-404c-8830-643ccb357b62', 'ffaa5663-850e-4009-80c4-c8bbe34cd285']

def repeating():
    return 1

def sparse_use():
    return 10

def one_use():
    return 60

def interpolate_missing_values(buckets):
    run = { 'location': len(buckets)-1, 'value': 0 }
    forwardLookup = [{ 'location': len(buckets)-1, 'value': 0 }]*(len(buckets))
    for i in range(len(buckets)):
        j = len(buckets)-1 - i
        if buckets[j] != None:
            run = { 'location': j, 'value': buckets[j]}
        forwardLookup[j] = run
    run_forward = { 'location': 0, 'value': 0 }
    interpolated_values = [0]*len(buckets)
    for i in range(len(buckets)-1):
        if buckets[i] != None:
            run_forward = { 'location': i, 'value': buckets[i] }
        interpolated_values[i] = (forwardLookup[i+1]['value'] - run_forward['value'])/(forwardLookup[i+1]['location'] - run_forward['location'])*(i-run_forward['location'])+run_forward['value']
    interpolated_values[len(buckets)-1] = buckets[len(buckets)-1] if buckets[len(buckets)-1] != None else 0
    return interpolated_values


def generate_special_items_over_time_weighted_time_series(client, experimentLabel):
    query = { "event": "UsingSpecialItemEvent" }
    if experimentLabel != None:
        query['experimentLabel'] = experimentLabel
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$sort': { 'time': 1 } },
        { '$project' : { '_id' : 0, 'player': 1, 'special': 1, 'time': 1 } },
        { '$group': { '_id' : '$player', 'events': { '$push': { 'special': '$special', 'time': { '$floor': { '$divide': ['$time', 1000] } } } } } },
    ]))

    # Normalize the values by their frequency. Wearing a helmet for 10 minutes
    # should be weighed the same as using the escape rope for 1 second
    items_to_value = {
        'hint_0': repeating(),
        'hint_1': repeating(),
        'hint_2': repeating(),
        'hint_3': repeating(),
        'hint_4': repeating(),
        'hint_5': repeating(),
        'hint_6': repeating(),
        'hint_7': repeating(),
        'hint_8': repeating(),
        'hint_9': repeating(),
        'hint_10': repeating(),
        'hint_11': repeating(),
        'hint_12': repeating(),
        'bow': sparse_use(),
        'boots': repeating(),
        'chestplate': repeating(),
        'leggings': repeating(),
        'helmet': repeating(),
        'sword': sparse_use(),
        'strong_boots': repeating(),
        'strong_chestplate': repeating(),
        'strong_leggings': repeating(),
        'strong_helmet': repeating(),
        'strong_sword': sparse_use(),
        'nether_brick_pickaxe': sparse_use(),
        'use_reveal_players': repeating(),
        'use_escape_rope': one_use(),
        'cave_torch': sparse_use(),
        'torch': sparse_use(),
        'fireworks': sparse_use(),
        'white_banner': sparse_use(),
        'use_glow_path': repeating(),
        'instant_heal_potion': one_use(),
        'speed_potion': repeating(),
        'invisibility_potion': repeating(),
        'trophy': 0, # investigate this? Hypothesis, we had someone wearing the skulls and it triggered the event...
    }

    start_time = list(client.epilog.data2.find(query).sort('time', 1).limit(1))[0]['time']
    processed_data = {}
    for player_data in intermediary_data:
        buckets = []
        for _ in range(60):
            buckets.append(None)
        for event in player_data['events']:
            local_event_time = (int(event['time']) - start_time // 1000) // 60
            if local_event_time < 60:
                buckets[local_event_time] = (0 if buckets[local_event_time] == None else buckets[local_event_time]) + items_to_value[event['special']]
        processed_data[player_data['_id']] = interpolate_missing_values(buckets)

    return {
        'series': [{
            'name': UUID_MAP[player]['name'],
            'data': processed_data[player],
            'color': UUID_MAP[player]['color'],
        } for player in PLAYERS],
        'categories': [idx for idx, _ in enumerate(buckets)]
    }