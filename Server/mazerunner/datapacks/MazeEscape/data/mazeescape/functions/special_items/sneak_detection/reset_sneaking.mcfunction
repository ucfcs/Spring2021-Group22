scoreboard players operation @s sneak_temp = @s sneak_time
scoreboard players operation @s sneak_temp -= @s sneak_match
execute if entity @s[scores={sneak_temp=0}] run scoreboard players set @s sneak_time 0
scoreboard players operation @s sneak_match = @s sneak_time