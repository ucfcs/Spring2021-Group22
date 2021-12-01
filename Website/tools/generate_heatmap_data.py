import pymongo
from dotenv import load_dotenv
import os
import ssl
import sys

from collections import defaultdict

from utils import printProgressBar, writeToFile

load_dotenv()


def def_value():
    return 0


def main(argv):
    team = argv[0] if argv[0] != 'overall' else {'$exists': True}
    teamName = argv[0] if argv[0] != 'overall' else 'overall'

    print('Connecting to Mongo')

    client = pymongo.MongoClient(
        os.getenv('MONGO_URI'), ssl_cert_reqs=ssl.CERT_NONE)
    collection = client.epilog.data

    print('fetching metadata')

    doc_count = collection.estimated_document_count()

    print('loading all data from database')

    printProgressBar(0, doc_count, prefix='Progress:',
                     suffix='Complete', length=50)

    data = defaultdict(def_value)

    index = 0
    for doc in collection.find({ 'experimentLabel': team,'x': {'$exists': True}, 'y': {'$exists': True}, 'z': {'$exists': True}}).sort('time', pymongo.ASCENDING):
        printProgressBar(index, doc_count, prefix='Progress:',
                         suffix='Complete', length=50)
        index = index + 1

        x = int(doc['x'])
        y = int(doc['z'])

        data[x, y] += 1

    data = sorted(data.items(), key=lambda key: key[0])

    print('')

    print('Writing data to file')
    writeToFile(data, teamName, 'csv')

    return 0



if __name__ == '__main__':
    main(sys.argv[1:])
