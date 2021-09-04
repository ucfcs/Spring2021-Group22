execute as @e[scores={glowingPathTime=1..}] run execute at @s run summon slime ~ ~-1.5 ~ {Tags:["glow_path"],ActiveEffects:[{Id:14b,Amplifier:1b,Duration:99999,ShowParticles:0b}],NoGravity:1b,Silent:1b,Invulnerable:1b,Glowing:1b,NoAI:1b,Size:0}
schedule function mazeescape:special_items/glow_path/spawn_glowing_path_markers 0.75s replace
