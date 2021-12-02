from start_time_util import get_times

PLAYERS = ['14d285df-e64e-41f2-bc4b-979e846c3cec', '6dc38184-c3e7-49ab-a99b-799b01274d01',
           '7d80f280-eaa6-404c-8830-643ccb357b62', 'ffaa5663-850e-4009-80c4-c8bbe34cd285']

def generate_death_timeline_step_chart(client, experimentLabel):
    (start_time, end_time) = get_times(client, experimentLabel)
    query = { 
        'experimentLabel': experimentLabel if experimentLabel != None else { '$exists': True }, 
        'event': 'PlayerDeathEvent',
        'time': { '$gte': start_time, '$lte': end_time },
    }
    intermediary_data = list(client.epilog.data2.aggregate([
        { '$match' : query },
        { '$project' : { '_id' : 0, 'time': { '$floor': { '$divide': ['$time', 1000*60] } } } },
        { '$sort': { 'time': 1 } },
        { '$group': { '_id' : '$time', 'total': { '$sum': 1 } } },
    ]));

    start_minute = start_time // (60 * 1000)
    game_timeline = [0 for _ in range(60)]
    cumulative_count = 0
    for i in range(60):
        event = next((data['total'] for data in intermediary_data if int(data['_id']) == start_minute + i), 0)
        cumulative_count += event // 2 # these events are duplicated for some reason in the data
        game_timeline[i] = cumulative_count

    return {
        'series': [
            { 
                'name': 'Cumulative Death Count', 
                'data': game_timeline
            }
        ],
    }