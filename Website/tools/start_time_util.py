# def get_start_time(client, experiment_label):
#     start_query = { 'experimentLabel': experiment_label if experiment_label != None else { '$exists': True }, 'event': 'PlayerLocationEvent', 'zone': 'Center' }
#     return list(client.epilog.data2.find(start_query).sort('time', 1).limit(1))[0]['time']

def get_start_time(client, experiment_label):
    # Find the first event of a player in the center zone; this seems like a decent heuristic for the start of the game
    start_query = { 'experimentLabel': experiment_label if experiment_label != None else { '$exists': True }, 'event': 'PlayerLocationEvent', 'zone': 'Center' }
    return list(client.epilog.data2.find(start_query).sort('time', 1).limit(1))[0]['time']

def get_times(client, experiment_label):
    # Find the first event of a player in the center zone; this seems like a decent heuristic for the start of the game
    start_query = { 'experimentLabel': experiment_label if experiment_label != None else { '$exists': True }, 'event': 'PlayerLocationEvent', 'zone': 'Center' }
    start_time = list(client.epilog.data2.find(start_query).sort('time', 1).limit(1))[0]['time'];
    return (start_time, start_time + 60*60*1000)