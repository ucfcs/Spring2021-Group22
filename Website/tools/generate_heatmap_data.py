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


def def_value():
    return 0


def main():

    print('Connecting to Mongo')

    client = pymongo.MongoClient(
        os.getenv('MONGO_URI'), ssl_cert_reqs=ssl.CERT_NONE)
    collection = client.epilog.test

    print('fetching metadata')

    doc_count = collection.estimated_document_count()

    print('loading all data from database')

    printProgressBar(0, doc_count, prefix='Progress:',
                     suffix='Complete', length=50)

    data = defaultdict(def_value)

    index = 0
    for doc in collection.find({'experimentLabel': "Team5", 'x': {'$exists': True}, 'y': {'$exists': True}, 'z': {'$exists': True}}).sort('time', pymongo.ASCENDING):
        printProgressBar(index, doc_count, prefix='Progress:',
                         suffix='Complete', length=50)
        index = index + 1

        x = int(doc['x'])
        y = int(doc['z'])

        data[x, y] += 1

    data = sorted(data.items(), key=lambda key: key[0])

    print('')

    print('Writing data to file')
    writeToFile(data, 'heatmap', 'csv')

    # print(data)

    return 0

# write to file and increment file name if it already exists


def writeToFile(data, filename, extension='csv'):
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
        for tuple in data:
            writer.writerow([tuple[0][0], tuple[0][1], tuple[1]])


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
