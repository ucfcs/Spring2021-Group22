scoreboard players add @a[scores={glowingPathTime=1..}] glowingPathTime 1
tellraw @a[scores={glowingPathTime=600}] [{"text":"[Glowing Path] Path finished generating... it will clear in 30 seconds."}]
scoreboard players set @a[scores={glowingPathTime=600}] glowingPathTime 0

scoreboard players add @e[tag=glow_path] slimeAge 1
kill @e[tag=glow_path,scores={slimeAge=1200..}]