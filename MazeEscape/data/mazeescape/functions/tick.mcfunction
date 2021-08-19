# First Join
execute as @a unless score @s firstJoin matches 1 run function mazeescape:firstjoin

# Glowing Trophys if within 10 blocks
execute as @e[tag=Trophy] at @s if entity @e[type=minecraft:player, distance=..10] run data merge entity @s {Glowing:1b,CustomNameVisible:1b}
execute as @e[tag=Trophy] at @s unless entity @e[type=minecraft:player,distance=..10] run data merge entity @s {Glowing:0b,CustomNameVisible:0b}

# Count Trophys