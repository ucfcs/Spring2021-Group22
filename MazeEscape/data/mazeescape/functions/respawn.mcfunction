# If died in maze, they respawn below maze with a penalty

effect give @s minecraft:blindness 30 1 true
tp @s 4.5 56 0.5

title @s title {"text":"You Died outside the walls"}
title @s subtitle {"text":"You are now blind for 30 seconds as a result"}