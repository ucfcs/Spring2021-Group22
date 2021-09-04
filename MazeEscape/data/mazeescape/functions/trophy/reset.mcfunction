# Cleanup all existing trophy
kill @e[tag=trophy]

# reset score
scoreboard objectives remove trophy_collected
scoreboard objectives add trophy_collected dummy
