# Floating Text
kill @e[tag=floating_text]

# Fishing Hut
data merge block -27 55 4 {Items:[]}

# Villagers
schedule clear mazeescape:villagers/itemguard

# Windmill Trophy Access
fill -47 61 -58 -47 64 -58 minecraft:air
execute as @e[tag=PreventPlace] run tp @s ~ -10 ~
execute as @e[tag=PreventPlace] run data merge entity @s {Size:0}
execute as @e[tag=PreventPlace] run kill @s
