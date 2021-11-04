from typing import DefaultDict
import pymongo
from dotenv import load_dotenv
import argparse
import os
import json
import math
import ssl
import csv

from pymongo.message import MAX_INT32, MIN_INT32
from bson.json_util import dumps, loads

from collections import defaultdict

load_dotenv()

time_offset = True

def def_value():
    return 0

def main():

    print('Connecting to Mongo')

    client = pymongo.MongoClient(os.getenv('MONGO_URI'), ssl_cert_reqs=ssl.CERT_NONE)
    collection = client.test.data3

    print('fetching metadata')

    # for time in collection.find().sort('time', pymongo.ASCENDING).limit(1):
    #     _time = math.floor(time['time'] / 1000)
    #     if time_offset:
    #         _time_offset = _time
    #         min_time = 0
    #     else:
    #         min_time = _time
    # for time in collection.find().sort('time', pymongo.DESCENDING).limit(1):
    #     if time_offset:
    #         max_time = math.floor(time['time'] / 1000) - _time_offset
    #     else:
    #         max_time = math.floor(time['time'] / 1000)

    doc_count = collection.estimated_document_count()

    print('loading all data from database')

    printProgressBar(0, doc_count, prefix='Progress:',
                     suffix='Complete', length=50)

    data = defaultdict(def_value)
    # data['time'] = {'min': min_time, 'max': max_time}
    # data['timeline'] = dict()

    index = 0
    for doc in collection.find().sort('time', pymongo.ASCENDING):
        printProgressBar(index, doc_count, prefix='Progress:',
                         suffix='Complete', length=50)
        index = index + 1
        
        if doc['event'] != "PlayerLocationEvent":
            continue
        
        # doc['_id'] = str(doc['_id'])
        
        # print(doc)
        x = int(doc['x'])
        y = int(doc['z'])
        
        # print(x)
        # print(y)
        # print(data[x,y])
        
        data[x, y] += 1
        
        
    print('')

    print('Writing data to file')
    writeToFile(data, 'rawdata_timestamp', 'csv')
    
    # print(data)

    return 0

# write to file and increment file name if it already exists


def writeToFile(data, filename, extension='json'):
    i = 0
    istr = ''
    while os.path.exists(f'{filename}{istr}.{extension}'):
        i += 1
        istr = str(i)

    # with open(f'{filename}{istr}.{extension}', 'w') as outfile:
    #     json.dump(data, outfile)
    
    with open(f'{filename}{istr}.{extension}', 'w', newline="") as csv_file:  
        writer = csv.writer(csv_file)
        writer.writerow(['x', 'y', 'count'])
        for key, value in data.items():
            writer.writerow([key[0], key[1], value])


# Print iterations progress
def printProgressBar(iteration, total, prefix='', suffix='', decimals=1, length=100, fill='█', printEnd='\r'):
    '''
    Call in a loop to create terminal progress bar
    @params:
        iteration   - Required  : current iteration (Int)
        total       - Required  : total iterations (Int)
        prefix      - Optional  : prefix string (Str)
        suffix      - Optional  : suffix string (Str)
        decimals    - Optional  : positive number of decimals in percent complete (Int)
        length      - Optional  : character length of bar (Int)
        fill        - Optional  : bar fill character (Str)
        printEnd    - Optional  : end character (e.g. '\r', '\r\n') (Str)
    '''
    percent = ('{0:.' + str(decimals) + 'f}').format(100 *
                                                     (iteration / float(total)))
    filledLength = int(length * iteration // total)
    bar = fill * filledLength + '-' * (length - filledLength)
    print(f'\r{prefix} |{bar}| {percent}% {suffix}', end=printEnd)
    # Print New Line on Complete
    if iteration == total:
        print()


if __name__ == '__main__':
    main()
