# Front / Given
data merge block 28 57 -22 {Items:[{Slot: 13b, id: "minecraft:iron_shovel", tag: {display: {Name: '{"text":"Sand Shovel","italic":false}', Lore: ['[{"text":"Use me to dig "},{"text":"Sand","color":"yellow","bold":true},{"text":" in the desert"}]', '[{"text":"Hold \'"},{"keybind":"key.attack"},{"text":"\' to break blocks"}]']}, Unbreakable: 1b, CanDestroy: ["minecraft:sand"], Damage: 0, HideFlags: 4}, Count: 1b}]}

#Given
data merge block 49 65 -57 {Items:[{Slot: 2b, id: "minecraft:lead", tag: {display: {Name: '[{"text":"Escape Rope","italic":false}]', Lore: ['[{"text":"Return to the home area.","italic":false}]', '[{"text":"Crouch and press \\""},{"keybind":"key.drop"},{"text":"\\" to use this item."}]']}}, Count: 1b}, {Slot: 15b, id: "minecraft:iron_boots", tag: {Damage: 0, display: {Name: '[{"text":"Boots","italic":false}]', Lore: ['[{"text":"Provides some shield to incoming damage","italic":false}]', '[{"text":"from hostile mobs.","italic":false}]', '[{"text":"Open your inventory and move this item","italic":false}]', '[{"text":"onto the appropriate armor slot.","italic":false}]']}}, Count: 1b}, {Slot: 21b, id: "minecraft:white_banner", tag: {display: {Lore: ['{"text":"Place these in the maze to mark "}', '{"text":"different paths or areas."}', '[{"text":"Use "},{"keybind":"key.use"},{"text":" to place."}]', '[{"text":"Use "},{"keybind":"key.attack"},{"text":" while holding"}]', '[{"text": "a white banner to remove."}]']}, CanPlaceOn: ["minecraft:stone_bricks", "minecraft:mossy_stone_bricks", "minecraft:cracked_stone_bricks"], CanDestroy: ["minecraft:white_banner"]}, Count: 16b}]} 
data merge block 69 69 -69 {Items:[{Slot: 2b, id: "minecraft:firework_rocket", tag: {CanPlaceOn: ["minecraft:stone_bricks", "minecraft:mossy_stone_bricks", "minecraft:cracked_stone_bricks", "minecraft:grass","minecraft:grass_block"], Fireworks: {Flight: 4b, Explosions: [{Type: 1b, FadeColors: [I; 1973019, 8073150, 15790320], Colors: [I; 11743532, 3887386, 14602026], Trail: 1b}, {Type: 2b, FadeColors: [I; 3887386, 14188952, 15435844], Colors: [I; 11743532, 12801229, 15435844], Flicker: 1b, Trail: 1b}]}, display: {Name: '[{"text":"Fireworks","italic":false}]', Lore: ['[{"text":"Just some fun fireworks.","italic":false}]']}}, Count: 8b}, {Slot: 8b, id: "minecraft:white_banner", tag: {display: {Lore: ['{"text":"Place these in the maze to mark "}', '{"text":"different paths or areas."}', '[{"text":"Use "},{"keybind":"key.use"},{"text":" to place."}]', '[{"text":"Use "},{"keybind":"key.attack"},{"text":" while holding"}]', '[{"text": "a white banner to remove."}]']}, CanPlaceOn: ["minecraft:stone_bricks", "minecraft:mossy_stone_bricks", "minecraft:cracked_stone_bricks"], CanDestroy: ["minecraft:white_banner"]}, Count: 16b}, {Slot: 14b, id: "minecraft:lead", tag: {display: {Name: '[{"text":"Escape Rope","italic":false}]', Lore: ['[{"text":"Return to the home area.","italic":false}]', '[{"text":"Crouch and press \\""},{"keybind":"key.drop"},{"text":"\\" to use this item."}]']}}, Count: 1b}, {Slot: 18b, id: "minecraft:iron_leggings", tag: {Damage: 0, display: {Name: '[{"text":"Leggings","italic":false}]', Lore: ['[{"text":"Provides some shield to incoming damage","italic":false}]', '[{"text":"from hostile mobs.","italic":false}]', '[{"text":"Open your inventory and move this item","italic":false}]', '[{"text":"onto the appropriate armor slot.","italic":false}]']}}, Count: 1b}]}

# easy ish
data merge block 52 69 -54 {Items:[{Slot: 1b, id: "minecraft:potion", tag: {display: {Lore: ['{"text":"Restore health."}', '[{"text":"Hold "},{"keybind":"key.use"},{"text":" to drink the potion."}]']}, Potion: "minecraft:strong_healing"}, Count: 1b}, {Slot: 12b, id: "minecraft:firework_rocket", tag: {CanPlaceOn: ["minecraft:stone_bricks", "minecraft:mossy_stone_bricks", "minecraft:cracked_stone_bricks", "minecraft:grass","minecraft:grass_block"], Fireworks: {Flight: 4b, Explosions: [{Type: 1b, FadeColors: [I; 1973019, 8073150, 15790320], Colors: [I; 11743532, 3887386, 14602026], Trail: 1b}, {Type: 2b, FadeColors: [I; 3887386, 14188952, 15435844], Colors: [I; 11743532, 12801229, 15435844], Flicker: 1b, Trail: 1b}]}, display: {Name: '[{"text":"Fireworks","italic":false}]', Lore: ['[{"text":"Just some fun fireworks.","italic":false}]']}}, Count: 8b}, {Slot: 16b, id: "minecraft:iron_sword", tag: {Damage: 0, display: {Name: '[{"text":"Sword","italic":false}]', Lore: ['[{"text":"Attack and defeat hostile mobs.","italic":false}]', '[{"text":"Swing the sword with left-click.","italic":false}]']}}, Count: 1b}]}
data merge block 60 59 -65 {Items:[{Slot: 0b, id: "minecraft:book", tag: {display: {Name: '[{"text":"Hint #10","color":"light_purple"}]', Lore: ['[{"text":"Hold this item to reveal a path.","italic":false}]', '[{"text":"The path starts at the center","italic":false}]', '[{"text":"of the map.","italic":false}]']}}, Count: 1b}]}
data merge block 29 56 -43 {Items:[{Slot: 1b, id: "minecraft:bow", tag: {Damage: 0, display: {Lore: ['{"text":"Shoot arrows from your inventory."}', '[{"text":"Hold and release "},{"keybind":"key.use"},{"text":" to use this item."}]']}, Enchantments: [{id: "minecraft:power", lvl: 2s}]}, Count: 1b}, {Slot: 15b, id: "minecraft:arrow", Count: 64b}]}
data merge block 65 57 -19 {Items:[{Slot: 6b, id: "minecraft:iron_helmet", tag: {Damage: 0, display: {Name: '[{"text":"Helmet","italic":false}]', Lore: ['[{"text":"Provides some shield to incoming damage","italic":false}]', '[{"text":"from hostile mobs.","italic":false}]', '[{"text":"Open your inventory and move this item","italic":false}]', '[{"text":"onto the appropriate armor slot.","italic":false}]']}}, Count: 1b}, {Slot: 10b, id: "minecraft:prismarine_crystals", tag: {display: {Name: '[{"text":"Glowing Path","italic":false}]', Lore: ['[{"text":"Make a temporary glowing path where you run.","italic":false}]','[{"text":"The path will generate underfoot for 30 seconds.","italic":false}]','[{"text":"The whole path will be gone 30 seconds later.","italic":false}]','[{"text":"Crouch and press \\""},{"keybind":"key.drop"},{"text":"\\" to use this item."}]']}}, Count: 1b}, {Slot: 16b, id: "minecraft:white_banner", tag: {display: {Lore: ['{"text":"Place these in the maze to mark "}', '{"text":"different paths or areas."}', '[{"text":"Use "},{"keybind":"key.use"},{"text":" to place."}]', '[{"text":"Use "},{"keybind":"key.attack"},{"text":" while holding"}]', '[{"text": "a white banner to remove."}]']}, CanPlaceOn: ["minecraft:stone_bricks", "minecraft:mossy_stone_bricks", "minecraft:cracked_stone_bricks"], CanDestroy: ["minecraft:white_banner"]}, Count: 16b}, {Slot: 21b, id: "minecraft:firework_rocket", tag: {CanPlaceOn: ["minecraft:stone_bricks", "minecraft:mossy_stone_bricks", "minecraft:cracked_stone_bricks", "minecraft:grass","minecraft:grass_block"], Fireworks: {Flight: 4b, Explosions: [{Type: 1b, FadeColors: [I; 1973019, 8073150, 15790320], Colors: [I; 11743532, 3887386, 14602026], Trail: 1b}, {Type: 2b, FadeColors: [I; 3887386, 14188952, 15435844], Colors: [I; 11743532, 12801229, 15435844], Flicker: 1b, Trail: 1b}]}, display: {Name: '[{"text":"Fireworks","italic":false}]', Lore: ['[{"text":"Just some fun fireworks.","italic":false}]']}}, Count: 8b}]}
data merge block 42 58 -24 {Items:[{Slot: 8b, id: "minecraft:potion", tag: {display: {Lore: ['{"text":"Become hidden from players and mobs."}', '[{"text":"Hold "},{"keybind":"key.use"},{"text":" to drink the potion."}]']}, Potion: "minecraft:long_invisibility"}, Count: 1b}, {Slot: 12b, id: "minecraft:lantern", tag: {display: {Name: '[{"text":"Reveal Teammates","italic":false}]', Lore: ['[{"text":"Highlight all the players on the map for","italic":false}]', '[{"text":"a short period of time.","italic":false}]', '[{"text":"Crouch and press \\""},{"keybind":"key.drop"},{"text":"\\" to use this item."}]']}}, Count: 1b}, {Slot: 15b, id: "minecraft:diamond_leggings", tag: {Damage: 0, display: {Name: '[{"text":"Strong Leggings","italic":false}]', Lore: ['[{"text":"Provides some shield to incoming damage","italic":false}]', '[{"text":"from hostile mobs.","italic":false}]', '[{"text":"Open your inventory and move this item","italic":false}]', '[{"text":"onto the appropriate armor slot.","italic":false}]']}}, Count: 1b}]}
data merge block 43 53 -36 {Items:[{Slot: 4b, id: "minecraft:prismarine_crystals", tag: {display: {Name: '[{"text":"Glowing Path","italic":false}]', Lore: ['[{"text":"Make a temporary glowing path where you run.","italic":false}]','[{"text":"The path will generate underfoot for 30 seconds.","italic":false}]','[{"text":"The whole path will be gone 30 seconds later.","italic":false}]','[{"text":"Crouch and press \\""},{"keybind":"key.drop"},{"text":"\\" to use this item."}]']}}, Count: 1b}, {Slot: 17b, id: "minecraft:firework_rocket", tag: {CanPlaceOn: ["minecraft:stone_bricks", "minecraft:mossy_stone_bricks", "minecraft:cracked_stone_bricks", "minecraft:grass","minecraft:grass_block"], Fireworks: {Flight: 4, Explosions: [{Type: 1, FadeColors: [I; 1973019, 8073150, 15790320], Colors: [I; 11743532, 3887386, 14602026], Flicker: 0, Trail: 1}, {Type: 2, FadeColors: [I; 3887386, 14188952, 15435844], Colors: [I; 11743532, 12801229, 15435844], Flicker: 1, Trail: 1}]}, display: {Name: '[{"text":"Fireworks","italic":false}]', Lore: ['[{"text":"Just some fun fireworks.","italic":false}]']}}, Count: 8b}]}

# med
data merge block 47 61 -57 {Items:[{Slot: 7b, id: "minecraft:iron_pickaxe", tag: {Damage: 0, display: {Name: '[{"text":"Nether Brick Breaker","italic":false}]', Lore: ['[{"text":"Can destroy walls that are made","italic":false}]', '[{"text":"of dark red bricks.","italic":false}]', '[{"text":"Hold "},{"keybind":"key.attack"},{"text":" to break blocks.","italic":false}]']}, CanDestroy: ["minecraft:nether_bricks", "minecraft:cracked_nether_bricks"]}, Count: 1b}, {Slot: 13b, id: "minecraft:written_book", tag: {pages: ['{"text":"There are 2 main entrances to the maze on the sides of the center square. However, there are 2 more entrances hidden behind red bricks on the other sides. These blocks can be removed with a certain item."}'], display: {Lore: ['[{"text":"Use "},{"keybind":"key.use"},{"text":" to view hint."}]']}, author: "N/A", title: "Hint #12"}, Count: 1b}]}
data merge block 53 58 -35 {Items:[{Slot: 13b, id: "minecraft:arrow", Count: 64b}, {Slot: 16b, id: "minecraft:bow", tag: {Damage: 0, display: {Lore: ['{"text":"Shoot arrows from your inventory."}', '[{"text":"Hold and release "},{"keybind":"key.use"},{"text":" to use this item."}]']}, Enchantments: [{id: "minecraft:power", lvl: 2s}]}, Count: 1b}, {Slot: 20b, id: "minecraft:torch", tag: {display: {Lore: ['{"text":"Place these in the maze to prevent "}', '{"text":"enemies from spawning nearby."}', '[{"text":"Use "},{"keybind":"key.use"},{"text":" to place."}]', '[{"text":"Use "},{"keybind":"key.attack"},{"text":" while holding"}]', '[{"text": "a torch to remove."}]']}, CanPlaceOn: ["minecraft:mossy_stone_bricks", "minecraft:cracked_stone_bricks", "minecraft:stone_bricks"], CanDestroy: ["minecraft:torch", "minecraft:wall_torch"]}, Count: 8b}]}

# hard
data merge block 30 53 -67 {Items:[{Slot: 1b, id: "minecraft:prismarine_crystals", tag: {display: {Name: '[{"text":"Glowing Path","italic":false}]', Lore: ['[{"text":"Make a temporary glowing path where you run.","italic":false}]','[{"text":"The path will generate underfoot for 30 seconds.","italic":false}]','[{"text":"The whole path will be gone 30 seconds later.","italic":false}]','[{"text":"Crouch and press \\""},{"keybind":"key.drop"},{"text":"\\" to use this item."}]']}}, Count: 1b}, {Slot: 8b, id: "minecraft:lead", tag: {display: {Name: '[{"text":"Escape Rope","italic":false}]', Lore: ['[{"text":"Return to the home area.","italic":false}]', '[{"text":"Crouch and press \\""},{"keybind":"key.drop"},{"text":"\\" to use this item."}]']}}, Count: 1b}, {Slot: 12b, id: "minecraft:book", tag: {display: {Name: '[{"text":"Hint #9","color":"light_purple"}]', Lore: ['[{"text":"Hold this item to reveal a path.","italic":false}]', '[{"text":"The path starts at the center","italic":false}]', '[{"text":"of the map.","italic":false}]']}}, Count: 1b}, {Slot: 19b, id: "minecraft:diamond_chestplate", tag: {Damage: 0, display: {Name: '[{"text":"Strong Chestplate","italic":false}]', Lore: ['[{"text":"Provides some shield to incoming damage","italic":false}]', '[{"text":"from hostile mobs.","italic":false}]', '[{"text":"Open your inventory and move this item","italic":false}]', '[{"text":"onto the appropriate armor slot.","italic":false}]']}}, Count: 1b}, {Slot: 25b, id: "minecraft:potion", tag: {display: {Lore: ['{"text":"Move quicker for some time."}', '[{"text":"Hold "},{"keybind":"key.use"},{"text":" to drink the potion."}]']}, Potion: "minecraft:long_swiftness"}, Count: 1b}]}

