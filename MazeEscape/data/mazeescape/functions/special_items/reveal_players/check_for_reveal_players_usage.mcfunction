execute as @e[type=item,nbt={Item:{id:"minecraft:lantern",tag:{display:{Name:'[{"text":"Reveal Teammates","italic":false}]'}}}}] run execute store result score @s SpecialAge run data get entity @s Age
execute as @e[type=item,scores={SpecialAge=..5},nbt={Item:{id:"minecraft:lantern",tag:{display:{Name:'[{"text":"Reveal Teammates","italic":false}]'}}}}] at @s run execute if entity @p[scores={sneak_time=1..},distance=..3] run function mazeescape:special_items/reveal_players/reveal_players_usage_callback