execute as @e[type=item,nbt={Item:{id:"minecraft:lead",tag:{display:{Name:'[{"text":"Escape Rope","italic":false}]'}}}}] at @s run execute if entity @p[scores={sneak_time=1..},distance=..3] run function mazeescape:special_items/escape_rope/rope_usage_callback