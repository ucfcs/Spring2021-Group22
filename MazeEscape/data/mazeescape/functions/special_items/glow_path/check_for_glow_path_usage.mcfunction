execute as @e[type=item,nbt={Item:{id:"minecraft:prismarine_crystals",tag:{display:{Name:'[{"text":"Glowing Path","italic":false}]'}}}}] run execute store result score @s SpecialAge run data get entity @s Age
execute as @e[type=item,scores={SpecialAge=..5},nbt={Item:{id:"minecraft:prismarine_crystals",tag:{display:{Name:'[{"text":"Glowing Path","italic":false}]'}}}}] at @s run execute if entity @p[scores={sneak_time=1..},distance=..3] run function mazeescape:special_items/glow_path/glow_path_usage_callback