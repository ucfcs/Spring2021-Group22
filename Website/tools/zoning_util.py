# Utility file for capturing what zone a location is in

#TODO make sure these boundaries capture everything and everything correctly
ZONE_DUNES = { "name": "Dunes", "start": [22, -76], "end": [76,-11] }
ZONE_FARMS = { "name": "Farms", "start": [-76, -76], "end": [-2, -12] }
ZONE_CAVE = { "name": "Cave", "start": [-76, 6], "end": [-5, 76] }
ZONE_FOREST = { "name": "Forest", "start": [-76, 6], "end": [14, 76] }
ZONE_MANSION = { "name": "Mansion", "start": [29, 20], "end": [76, 76] }
ZONE_CENTER = { "name": "Center", "start": [-76, -76], "end": [76, 76] }
ZONE_MAZE = { "name": "Maze" }
ALL_ZONES = [ZONE_DUNES, ZONE_FARMS, ZONE_CAVE, ZONE_FOREST, ZONE_MANSION, ZONE_CENTER, ZONE_MAZE];
GROUND_Y = 52

def inZoneXZ(x, z, zone):
    if x >= zone['start'][0] and x <= zone['end'][0] and z >= zone['start'][1] and z <= zone['end'][1]:
        return True
    return False

def getZone(x, y, z):
    if y < GROUND_Y:
        return ZONE_CAVE['name']
    for inner_zone in [ZONE_DUNES, ZONE_FARMS, ZONE_FOREST, ZONE_MANSION]:
        if inZoneXZ(x, z, inner_zone):
            return inner_zone['name']
    if inZoneXZ(x, z, ZONE_CENTER):
        return ZONE_CENTER['name']
    return ZONE_MAZE['name']