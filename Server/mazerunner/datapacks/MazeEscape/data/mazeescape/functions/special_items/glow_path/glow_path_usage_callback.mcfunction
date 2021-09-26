execute as @p[scores={sneak_time=1..},distance=..3] at @s run summon armor_stand ~ ~ ~ {NoGravity:1b,Invulnerable:1b,Small:1b,Marker:1b,Invisible:1b,Tags:["_custom_action"],CustomName:'{"text":"use_glow_path"}'}
scoreboard players set @p[scores={sneak_time=1..},distance=..3] glowingPathTime 1
kill @s