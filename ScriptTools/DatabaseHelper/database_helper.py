## Simple tool for querying the database for common events

import pymongo
import argparse
from dotenv import load_dotenv
import os

load_dotenv();

MONGO_URI = 'MONGO_URI';
if (MONGO_URI not in os.environ.keys()):
    print('Please add MONGO_URI to your environment variables before using this utility');
    exit(0);

# Global connection var
mongo_connection_uri = os.environ.get(MONGO_URI);
# Global common event names var
common_event_names = [
        'PlayerLocationEvent',
        'MazeEscapeUseSpecialItemEvent',
        'MazeEscapeVillagerTradeEvent',
        'PlayerInteractEntityEvent',
        'InventoryClickEvent',
        'InventoryContent',
        'ArmorContent',
        'PlayerPickupItemEvent',
        'PlayerDropItemEvent',
        'BlockPlaceEvent',
        'BlockBreakEvent',
        'PlayerDamageByPlayerEvent',
        'EntityShootBowEvent',
        'PlayerItemInHandEvent',
        'EntityPickupItemEvent',
        'PlayerDeathEvent',
        'PlayerDamageByEntityEvent',
        'InventoryOpenEvent',
        'InventoryCloseEvent',
        'PlayerItemConsumeEvent',
    ];

# Parse arguments
parser = argparse.ArgumentParser()
parser.add_argument('--event', help='the name of the event to query for')
parser.add_argument('--limit', help='limit the query to the last <x> values')
parser.add_argument('--listCommonEventNames', action='store_true', help='print out common queryable events');
parser.add_argument('--queryAllEventNames', action='store_true', help='query the entire database for unique events');
parser.add_argument('--queryAllCommonEventProperties', action='store_true', help='query the entire database for unique events and their properties');

args = parser.parse_args()

## List out the common event names. These are the ones that I thought would
## be the most useful to plot/analyze
if (args.listCommonEventNames):
    print(common_event_names)
    exit(0);

# Give default value
limit = 100;

# Verify limit value
if (args.limit != None):
    if (args.limit.isnumeric() and int(args.limit) > 0): # must be integer
        limit = int(args.limit);
    else:
        print("--limit must be a positive integer");
        exit(0)

# Setup connection for the final two execution cases of the cmd tool
client = pymongo.MongoClient(mongo_connection_uri, serverSelectionTimeoutMS=5000)
collection = client.epilog.data2;

## Print out all event names
if (args.queryAllEventNames):
    print('Querying the database for all event names. This could take a minute...')
    events_set = set()
    try:
        for document in collection.find({}):
            events_set.add(document.get('event'))
    except Exception as e:
        print(e)
    events_list = list(events_set);
    events_list.sort();
    print(events_list)
    exit(0)

## Print out all event docs
if (args.queryAllCommonEventProperties):
    print('Querying the database for all common event names and properties. This could take a second...')
    try:
        for event_name in common_event_names:
            document = collection.find_one({ "event": event_name }, sort=[('time', pymongo.DESCENDING)]);
            print(event_name + ": " + repr(document.keys()))
    except Exception as e:
        print(e)
    exit(0)

## Main behavior. Query the database for records for the requested event

# Verify event is provided
if (args.event == None):
    print('Please provide an event to query for. Use --help for guidance');
    exit(0);

event = args.event;

print("Querying '" + event + "' for the last " + str(limit) + " values...");
try:
    collection = client.epilog.data2;
    items = list(collection.find({ "event": event }).limit(limit).sort("time", pymongo.DESCENDING));
    print(str(len(items)) + " items found...");
    for document in items:
        print(document)
except Exception as e:
    print(e)