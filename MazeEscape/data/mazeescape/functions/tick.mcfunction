# Glowing Trophys if within 10 blocks
execute as @e[tag=trophy] at @s if entity @e[type=minecraft:player, distance=..10] run data merge entity @s {Glowing:1b,CustomNameVisible:1b}
execute as @e[tag=trophy] at @s unless entity @e[type=minecraft:player,distance=..10] run data merge entity @s {Glowing:0b,CustomNameVisible:0b}

# Count Trophys
function mazeescape:trophy/check
