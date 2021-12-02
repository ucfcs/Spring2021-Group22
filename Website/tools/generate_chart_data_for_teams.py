import pymongo
import argparse
from dotenv import load_dotenv
import os
import json
from generate_death_timeline_step_chart import generate_death_timeline_step_chart
from generate_chat_content_column_chart import generate_chat_content_column_chart
from generate_chat_count_column_chart import generate_chat_count_column_chart
from generate_distance_from_center_by_player_time_series import generate_distance_from_center_by_player_time_series
from generate_diversity_of_labor_by_event_pie_charts import generate_diversity_of_labor_by_event_pie_charts
from generate_diversity_of_labor_by_player_column_chart import generate_diversity_of_labor_by_player_column_chart
from generate_entity_damage_interactions_column_chart import generate_entity_damage_interactions_column_chart
from generate_location_spread_by_player_time_series import generate_location_spread_by_player_time_series
from generate_location_spread_time_series import generate_location_spread_time_series
from generate_percent_duplicate_location_column_chart import generate_percent_duplicate_location_column_chart
from generate_sharing_column_chart import generate_sharing_column_chart
from generate_special_items_over_time_unweighted_time_series import generate_special_items_over_time_unweighted_time_series
from generate_special_items_over_time_weighted_time_series import generate_special_items_over_time_weighted_time_series
from generate_time_in_location_by_player_column_chart import generate_time_in_location_by_player_column_chart
from generate_time_of_player_by_location_column_chart import generate_time_of_player_by_location_column_chart
from generate_time_of_player_by_location_pie_charts import generate_time_of_player_by_location_pie_charts
from generate_total_distance_column_chart import generate_total_distance_column_chart
from generate_trophy_count_column_chart import generate_trophy_count_column_chart
from generate_zone_timeline_chart import generate_zone_timeline_chart
from generate_trophy_timeline_step_chart import generate_trophy_timeline_step_chart

# Write the results to a precomputed file. This file will likely be in the static_files 
# directory.
def writeToFile(data, file):
    with open(file, 'w+') as out:
        json.dump(data, out)

def generate_data(client, experiment_label):
    print('Generating data for ' + experiment_label)
    return {
            'chat_content_column_chart': generate_chat_content_column_chart(client, experiment_label),
            'chat_count_column_chart': generate_chat_count_column_chart(client, experiment_label),
            'distance_from_center_by_player_time_series': generate_distance_from_center_by_player_time_series(client, experiment_label),
            'diversity_of_labor_by_event_pie_charts': generate_diversity_of_labor_by_event_pie_charts(client, experiment_label),
            'diversity_of_labor_by_player_column_chart': generate_diversity_of_labor_by_player_column_chart(client, experiment_label),
            'entity_damage_interactions_column_chart': generate_entity_damage_interactions_column_chart(client, experiment_label),
            'location_spread_by_player_time_series': generate_location_spread_by_player_time_series(client, experiment_label),
            'location_spread_time_series': generate_location_spread_time_series(client, experiment_label),
            'percent_duplicate_location_column_chart': generate_percent_duplicate_location_column_chart(client, experiment_label),
            'sharing_column_chart': generate_sharing_column_chart(client, experiment_label),
            'special_items_over_time_unweighted_time_series': generate_special_items_over_time_unweighted_time_series(client, experiment_label),
            'special_items_over_time_weighted_time_series': generate_special_items_over_time_weighted_time_series(client, experiment_label),
            'time_in_location_by_player_column_chart': generate_time_in_location_by_player_column_chart(client, experiment_label),
            'time_of_player_by_location_column_chart': generate_time_of_player_by_location_column_chart(client, experiment_label),
            'time_of_player_by_location_pie_charts': generate_time_of_player_by_location_pie_charts(client, experiment_label),
            'total_distance_column_chart': generate_total_distance_column_chart(client, experiment_label),
            'trophy_count_column_chart': generate_trophy_count_column_chart(client, experiment_label),
            'zone_timeline_chart': generate_zone_timeline_chart(client, experiment_label),
            'death_timeline_step_chart': generate_death_timeline_step_chart(client, experiment_label),
            'trophy_timeline_step_chart': generate_trophy_timeline_step_chart(client, experiment_label),
        };
    
# Main script
if __name__ == '__main__':
    parser = argparse.ArgumentParser()
    parser.add_argument('--out', help='where to write the data to');
    args = parser.parse_args()

    load_dotenv();

    MONGO_URI = 'MONGO_URI';
    if (MONGO_URI not in os.environ.keys()):
        print('Please add MONGO_URI to your environment variables before using this utility');
        exit(0);

    mongo_connection_uri = os.environ.get(MONGO_URI);
    client = pymongo.MongoClient(mongo_connection_uri, serverSelectionTimeoutMS=5000)
    
    data = [{
        'experiment_label': 'Team ' + repr(idx+1),
        'data': generate_data(client, team)
    } for idx, team in enumerate(['group1', 'Team2', 'Team3', 'Team3take2', 'Team5', 'team6'])];

    writeToFile(data, args.out if args.out != None else '../static_files/data/teams.json')