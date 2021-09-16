execute as @p[scores={sneak_time=1..},distance=..3] at @s run summon armor_stand ~ ~ ~ {NoGravity:1b,Invulnerable:1b,Small:1b,Marker:1b,Invisible:1b,Tags:["_custom_action"],CustomName:'{"text":"use_reveal_players"}'}
effect give @a minecraft:glowing 20 1 true
kill @s