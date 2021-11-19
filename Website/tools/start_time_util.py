# def get_start_time(client, experiment_label):
#     start_query = { 'experimentLabel': experiment_label if experiment_label != None else { '$exists': True }, 'event': 'PlayerLocationEvent', 'zone': 'Center' }
#     return list(client.epilog.data2.find(start_query).sort('time', 1).limit(1))[0]['time']

def get_start_time(client, experiment_label):
    start_query = { 'experimentLabel': experiment_label if experiment_label != None else { '$exists': True } }
    return list(client.epilog.data2.find(start_query).sort('time', 1).limit(1))[0]['time']