

# trophy access
fill -47 61 -58 -47 64 -58 minecraft:air
execute as @e[tag=PreventPlace] run tp @s ~ -10 ~
execute as @e[tag=PreventPlace] run data merge entity @s {Size:0}
execute as @e[tag=PreventPlace] run kill @s

summon slime -46.50 60.00 -58.60 {Silent:1b,Invulnerable:1b,Glowing:0b,CustomNameVisible:0b,PersistenceRequired:1b,NoAI:1b,Size:1,Tags:["PreventPlace"]}
summon slime -46.50 61.00 -58.60 {Silent:1b,Invulnerable:1b,Glowing:0b,CustomNameVisible:0b,PersistenceRequired:1b,NoAI:1b,Size:1,Tags:["PreventPlace"]}
summon slime -46.50 62.00 -58.60 {Silent:1b,Invulnerable:1b,Glowing:0b,CustomNameVisible:0b,PersistenceRequired:1b,NoAI:1b,Size:1,Tags:["PreventPlace"]}
summon slime -46.50 63.00 -58.60 {Silent:1b,Invulnerable:1b,Glowing:0b,CustomNameVisible:0b,PersistenceRequired:1b,NoAI:1b,Size:1,Tags:["PreventPlace"]}