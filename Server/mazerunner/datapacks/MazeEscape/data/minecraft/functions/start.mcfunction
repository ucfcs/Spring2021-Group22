# Load Map
function mazeescape:setup

# TP all players to spawn
execute as @a[team=!tester] run function mazeescape:spawn

# Start Timer
setblock 0 47 4 minecraft:redstone_block