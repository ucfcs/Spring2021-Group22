# Generates a json file of all of the data associated with a single team for the squggle map
# Call by running `python generate_heapmap_data.py team`
# Where team is the experimentLabel associated with the team

import pymongo
from dotenv import load_dotenv
import os
import math
import ssl
import sys

from utils import printProgressBar, writeToFile

time_offset = True

load_dotenv()

def main(argv):
    team = argv[0]

    print('Connecting to Mongo')

    client = pymongo.MongoClient(
        os.getenv('MONGO_URI'), ssl_cert_reqs=ssl.CERT_NONE)
    collection = client.epilog.data

    print('fetching metadata')

    mongo_filter = { 'experimentLabel': team, 'event': 'PlayerLocationEvent' }
    # mongo_filter = {'event': 'PlayerLocationEvent'}

    for time in collection.find(mongo_filter).sort('time', pymongo.ASCENDING).limit(1):
        _time = math.floor(time['time'] / 1000)
        if time_offset:
            _time_offset = _time
            min_time = 0
        else:
            min_time = _time
    for time in collection.find(mongo_filter).sort('time', pymongo.DESCENDING).limit(1):
        if time_offset:
            max_time = math.floor(time['time'] / 1000) - _time_offset
        else:
            max_time = math.floor(time['time'] / 1000)

    doc_count = collection.estimated_document_count()

    print('loading all data from database')

    # printProgressBar(0, doc_count, prefix='Progress:',
    #                  suffix='Complete', length=50)

    data = dict()
    data['time'] = {'min': min_time, 'max': max_time}
    data['timeline'] = dict()

    index = 0
    for doc in collection.find(mongo_filter).sort('time', pymongo.ASCENDING):
        printProgressBar(index, doc_count, prefix='Progress:',
                         suffix='Complete', length=50)
        index = index + 1

        if doc['event'] != "PlayerLocationEvent":
            continue

        doc['_id'] = str(doc['_id'])

        if time_offset:
            time = str(math.floor(doc['time'] / 1000) - _time_offset)
            doc['time'] = doc['time'] - (_time_offset * 1000)
        else:
            time = str(math.floor(doc['time'] / 1000))

        if time in data['timeline']:
            data['timeline'][time].append(doc)
        else:
            data['timeline'][time] = [doc]
    print('')

    print('Writing data to file')
    writeToFile(data, team, 'json')

    return 0

if __name__ == '__main__':
    main(sys.argv[1:])
