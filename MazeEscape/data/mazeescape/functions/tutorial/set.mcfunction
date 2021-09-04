# Floating Map Text
summon item_frame 01 43 2 {Facing:2b,Invulnerable:1b,Invisible:1b,Fixed:1b,Tags:["mazeescape","tutorial"],Item:{id:"minecraft:filled_map",Count:1b,tag:{map:0}}}
summon item_frame 00 43 2 {Facing:2b,Invulnerable:1b,Invisible:1b,Fixed:1b,Tags:["mazeescape","tutorial"],Item:{id:"minecraft:filled_map",Count:1b,tag:{map:1}}}
summon item_frame -1 43 2 {Facing:2b,Invulnerable:1b,Invisible:1b,Fixed:1b,Tags:["mazeescape","tutorial"],Item:{id:"minecraft:filled_map",Count:1b,tag:{map:2}}}

# Villager
summon villager -3.5 41 -7.5 {OnGround:1b,NoGravity:1b,Silent:1b,Invulnerable:1b,Glowing:0b,CustomNameVisible:1b,Team:"tutorial",FallFlying:0b,PersistenceRequired:1b,CanPickUpLoot:0b,AbsorptionAmount:2147483647f,Health:2147483647f,Willing:0b,Xp:2147483647,Tags:["mazeescape","tutorial"],CustomName:'{"text":"Market"}',Attributes:[{Name:generic.max_health,Base:2147483647},{Name:generic.follow_range,Base:2147483647},{Name:generic.knockback_resistance,Base:2147483647},{Name:generic.movement_speed,Base:-2147483646},{Name:generic.attack_damage,Base:2147483647},{Name:generic.armor,Base:2147483647},{Name:generic.armor_toughness,Base:2147483647},{Name:generic.attack_knockback,Base:2147483647}],VillagerData:{level:99,profession:"minecraft:librarian",type:"minecraft:plains"},Offers:{Recipes:[{rewardExp:0b,maxUses:2147483647,uses:0,xp:0,buy:{id:"minecraft:apple",Count:1b},sell:{id:"minecraft:milk_bucket",Count:1b}}]}}

# Farm
setblock 6 41 -10 minecraft:chest[facing=west]{Items:[{Slot: 13b, id: "minecraft:wheat_seeds", Count: 64b}]}