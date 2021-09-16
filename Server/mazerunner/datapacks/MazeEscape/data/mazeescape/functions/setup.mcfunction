function mazeescape:villagers/setup
function mazeescape:desert/set
function mazeescape:forest/set

function mazeescape:trophy/summon

# Windmill Trophy Access
fill -47 61 -58 -47 64 -58 minecraft:air
execute as @e[tag=PreventPlace] run tp @s ~ -10 ~
execute as @e[tag=PreventPlace] run data merge entity @s {Size:0}
execute as @e[tag=PreventPlace] run kill @s

summon slime -46.50 60.00 -58.60 {Silent:1b,Invulnerable:1b,Glowing:0b,CustomNameVisible:0b,PersistenceRequired:1b,NoAI:1b,Size:1,Tags:["PreventPlace"]}
summon slime -46.50 61.00 -58.60 {Silent:1b,Invulnerable:1b,Glowing:0b,CustomNameVisible:0b,PersistenceRequired:1b,NoAI:1b,Size:1,Tags:["PreventPlace"]}
summon slime -46.50 62.00 -58.60 {Silent:1b,Invulnerable:1b,Glowing:0b,CustomNameVisible:0b,PersistenceRequired:1b,NoAI:1b,Size:1,Tags:["PreventPlace"]}
summon slime -46.50 63.00 -58.60 {Silent:1b,Invulnerable:1b,Glowing:0b,CustomNameVisible:0b,PersistenceRequired:1b,NoAI:1b,Size:1,Tags:["PreventPlace"]}

# Fishing hut
data merge block -27 55 4 {Items:[{Slot: 13b, id: "minecraft:fishing_rod", tag: {Damage: 0}, Count: 1b}]}
summon cod -26.5 56 6.0 {NoGravity:1b,Silent:1b,Invulnerable:1b,NoAI:1b,CanPickUpLoot:0b,AbsorptionAmount:2147483647f,Health:2147483647f,Rotation:[180F,0F],Tags:["mazeescape","fish"],Attributes:[{Name:generic.max_health,Base:2147483647}]}

# Floating Text
# Farm
summon armor_stand -13.5 59.5 -30.5 {CustomNameVisible:1b,NoGravity:1b,Invulnerable:1b,Marker:1b,Invisible:1b,Tags:["mazeescape","floating_text"],CustomName:'{"text":"Farm","color":"green","bold":true}'}
summon armor_stand -13.5 59 -30.5 {CustomNameVisible:1b,NoGravity:1b,Invulnerable:1b,Marker:1b,Invisible:1b,Tags:["mazeescape","floating_text"],CustomName:'{"text":"Find some seeds to grow some crops","color":"white"}'}
# Desert
summon armor_stand 26.5 61.5 -16.5 {CustomNameVisible:1b,NoGravity:1b,Invulnerable:1b,Marker:1b,Invisible:1b,Tags:["mazeescape","floating_text"],CustomName:'{"text":"Desert","color":"green","bold":true}'}
summon armor_stand 26.5 61 -16.5 {CustomNameVisible:1b,NoGravity:1b,Invulnerable:1b,Marker:1b,Invisible:1b,Tags:["mazeescape","floating_text"],CustomName:'{"text":"Find a shovel to dig for treasure","color":"white"}'}
# Puzzle House
summon armor_stand 30.5 59.5 42.5 {CustomNameVisible:1b,NoGravity:1b,Invulnerable:1b,Marker:1b,Invisible:1b,Tags:["mazeescape","floating_text"],CustomName:'{"text":"Puzzle House","color":"green","bold":true}'}
summon armor_stand 30.5 59 42.5 {CustomNameVisible:1b,NoGravity:1b,Invulnerable:1b,Marker:1b,Invisible:1b,Tags:["mazeescape","floating_text"],CustomName:'{"text":"Solve the puzzles and find the trophy","color":"white"}'}
# Forest
summon armor_stand -12.5 59.5 13.5 {CustomNameVisible:1b,NoGravity:1b,Invulnerable:1b,Marker:1b,Invisible:1b,Tags:["mazeescape","floating_text"],CustomName:'{"text":"Forest","color":"green","bold":true}'}
summon armor_stand -12.5 59 13.5 {CustomNameVisible:1b,NoGravity:1b,Invulnerable:1b,Marker:1b,Invisible:1b,Tags:["mazeescape","floating_text"],CustomName:'{"text":"Find barrels to find tools that can help on your journey","color":"white"}'}
# Mine
summon armor_stand -34.5 59.5 30.5 {CustomNameVisible:1b,NoGravity:1b,Invulnerable:1b,Marker:1b,Invisible:1b,Tags:["mazeescape","floating_text"],CustomName:'{"text":"Mine","color":"green","bold":true}'}
summon armor_stand -34.5 59 30.5 {CustomNameVisible:1b,NoGravity:1b,Invulnerable:1b,Marker:1b,Invisible:1b,Tags:["mazeescape","floating_text"],CustomName:'{"text":"Find the pickaxe and mine for ores","color":"white"}'}