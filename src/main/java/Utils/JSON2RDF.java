package Utils;

import java.io.FileReader;

import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.ModelFactory;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class JSON2RDF
{
    static String wd = "http://www.wikidata.org/entity/";
    static String wdt = "http://www.wikidata.org/prop/direct/";
    static String wikibase = "http://wikiba.se/ontology#";
    static String p = "http://www.wikidata.org/prop/";
    static String ps = "http://www.wikidata.org/prop/statement/";
    static String pq = "http://www.wikidata.org/prop/qualifier/";
    static String bd = "http://www.bigdata.com/rdf#";

    static String base = "C:\\Users\\shiza" +
            "\\OneDrive - Instituto Superior de Engenharia do Porto\\Documentos\\GitHub" +
            "\\TICO\\PopulatedDatasets\\" +
            "pokemon outputs\\";
    static OntModel theModel;

    static String genIII_abilities_list [] = { "Air Lock", "Arena Trap", "Battle Armor", "Blaze", "Cacophony", "Chlorophyll", "Clear Body", "Cloud Nine", "Color Change", "Compound Eyes", "Cute Charm", "Damp", "Drizzle", "Drought", "Early Bird", "Effect Spore", "Flame Body", "Flash Fire", "Forecast", "Guts", "Huge Power", "Hustle", "Hyper Cutter",  "Illuminate", "Immunity", "Inner Focus", "Insomnia", "Intimidate", "Keen Eye", "Levitate", "Lightning Rod", "Limber", "Liquid Ooze", "Magma Armor", "Magnet Pull", "Marvel Scale", "Minus", "Natural Cure", "Oblivious", "Overgrow", "Own Tempo", "Pickup", "Plus", "Poison Point", "Pressure", "Pure Power", "Rain Dish", "Rock Head", "Rough Skin", "Run Away", "Sand Stream", "Sand Veil", "Serene Grace", "Shadow Tag", "Shed Skin", "Shell Armor", "Shield Dust", "Soundproof", "Speed Boost", "Static", "Stench", "Sticky Hold", "Sturdy", "Suction Cups", "Swarm", "Swift Swim", "Synchronize", "Thick Fat", "Torrent", "Trace", "Truant", "Vital Spirit", "Volt Absorb", "Water Absorb", "Water Veil", "White Smoke", "Wonder Guard" };
    static String genIV_abilities_list [] = { "Adaptability", "Aftermath", "Anger Point", "Anticipation", "Bad Dreams", "Download", "Dry Skin", "Filter", "Flower Gift", "Forewarn", "Frisk", "Gluttony", "Heatproof", "Honey Gather", "Hydration", "Ice Body", "Iron Fist", "Klutz", "Leaf Guard", "Magic Guard", "Mold Breaker", "Motor Drive", "Multitype", "No Guard", "Normalize", "Poison Heal", "Quick Feet", "Reckless", "Rivalry", "Scrappy", "Simple", "Skill Link", "Slow Start", "Sniper", "Snow Cloak", "Snow Warning", "Solar Power", "Solid Rock", "Stall", "Steadfast", "Storm Drain", "Super Luck", "Tangled Feet", "Technician", "Tinted Lens", "Unaware", "Unburden"};
    static String genV_abilities_list [] = {"Analytic","Big Pecks", "Contrary", "Cursed Body", "Defeatist", "Defiant", "Flare Boost", "Friend Guard", "Harvest", "Healer", "Heavy Metal", "Illusion", "Imposter", "Infiltrator", "Iron Barbs", "Justified", "Light Metal", "Magic Bounce", "Moody", "Moxie", "Multiscale", "Mummy", "Overcoat", "Pickpocket", "Poison Touch", "Prankster", "Rattled", "Regenerator", "Sand Force", "Sand Rush", "Sap Sipper", "Sheer Force", "Telepathy", "Teravolt", "Toxic Boost", "Turboblaze", "Unnerve", "Victory Star", "Weak Armor", "Wonder Skin", "Zen Mode"};
    static String genVI_moves_list [] = {"Aromatic Mist","Baby-Doll Eyes", "Belch", "Boomburst", "Celebrate", "Confide", "Crafty Shield", "Dazzling Gleam", "Diamond Storm", "Disarming Voice", "Dragon Ascent", "Draining Kiss", "Eerie Impulse", "Electric Terrain", "Electrify", "Fairy Lock", "Fairy Wind", "Fell Stinger", "Flower Shield", "Flying Press", "Forest's Curse", "Freeze-Dry", "Geomancy", "Grassy Terrain", "Happy Hour", "Hold Back", "Hold Hands", "Hyperspace Fury", "Hyperspace Hole", "Infestation", "Ion Deluge", "King's Shield", "Land's Wrath", "Light of Ruin", "Magnetic Flux", "Mat Block", "Misty Terrain", "Moonblast", "Mystical Fire", "Noble Roar", "Nuzzle", "Oblivion Wing", "Origin Pulse", "Parabolic Charge", "Parting Shot", "Petal Blizzard", "Phantom Force", "Play Nice", "Play Rough", "Powder", "Power-Up Punch", "Precipice Blades", "Rototiller", "Spiky Shield", "Steam Eruption", "Sticky Web", "Thousand Arrows", "Thousand Waves", "Topsy-Turvy", "Trick-or-Treat", "Venom Drench", "Water Shuriken"};
    static String genVI_abilities_list [] = {
            "Aerilate", "Aroma Veil", "Aura Break", "Bulletproof", "Cheek Pouch", "Competitive", "Dark Aura", "Delta Stream", "Desolate Land", "Fairy Aura", "Flower Veil", "Fur Coat", "Gale Wings", "Gooey", "Grass Pelt", "Magician", "Mega Launcher", "Parental Bond", "Pixilate", "Primordial Sea", "Protean", "Refrigerate", "Stance Change", "Strong Jaw", "Sweet Veil", "Symbiosis", "Tough Claws"};
    static String genI_moves_list [] = { "Absorb", "Acid", "Acid Armor", "Agility", "Amnesia", "Aurora Beam", "Barrage", "Barrier", "Bide", "Bind", "Bite", "Blizzard", "Body Slam", "Bone Club", "Bonemerang", "Bubble", "Bubble Beam", "Clamp", "Comet Punch", "Confuse Ray", "Confusion", "Constrict", "Conversion", "Counter", "Crabhammer", "Cut", "Defense Curl", "Dig", "Disable", "Dizzy Punch", "Double Kick", "Double Slap", "Double Team", "Double-Edge", "Dragon Rage", "Dream Eater", "Drill Peck", "Earthquake", "Egg Bomb", "Ember", "Explosion", "Fire Blast", "Fire Punch", "Fire Spin", "Fissure", "Flamethrower", "Flash", "Fly", "Focus Energy", "Fury Attack", "Fury Swipes", "Glare", "Growl", "Growth", "Guillotine", "Gust", "Harden", "Haze", "Headbutt", "High Jump Kick", "Horn Attack", "Horn Drill", "Hydro Pump", "Hyper Beam", "Hyper Fang", "Hypnosis", "Ice Beam", "Ice Punch", "Jump Kick", "Karate Chop", "Kinesis", "Leech Life", "Leech Seed", "Leer", "Lick", "Light Screen", "Lovely Kiss", "Low Kick", "Meditate", "Mega Drain", "Mega Kick", "Mega Punch", "Metronome", "Mimic", "Minimize", "Mirror Move", "Mist", "Night Shade", "Pay Day", "Peck", "Petal Dance", "Pin Missile", "Poison Gas", "Poison Powder", "Poison Sting", "Pound", "Psybeam", "Psychic", "Psywave", "Quick Attack", "Rage", "Razor Leaf", "Razor Wind", "Recover", "Reflect", "Rest", "Roar", "Rock Slide", "Rock Throw", "Rolling Kick", "Sand Attack", "Scratch", "Screech", "Seismic Toss", "Self-Destruct", "Sharpen", "Sing", "Skull Bash", "Sky Attack", "Slam", "Slash", "Sleep Powder", "Sludge", "Smog", "Smokescreen", "Soft-Boiled", "Solar Beam", "Sonic Boom", "Spike Cannon", "Splash", "Spore", "Stomp", "Strength", "String Shot", "Struggle", "Stun Spore", "Submission", "Substitute", "Super Fang", "Supersonic", "Surf", "Swift", "Swords Dance", "Tackle", "Tail Whip", "Take Down", "Teleport", "Thrash", "Thunder", "Thunder Punch", "Thunder Shock", "Thunder Wave", "Thunderbolt", "Toxic", "Transform", "Tri Attack", "Twineedle", "Vine Whip", "Vise Grip", "Water Gun", "Waterfall", "Whirlwind", "Wing Attack", "Withdraw", "Wrap"};

    static String genII_moves_list [] = {"Aeroblast","Ancient Power","Attract","Baton Pass","Beat Up","Belly Drum","Bone Rush","Charm","Conversion 2","Cotton Spore","Cross Chop","Crunch","Curse","Destiny Bond","Detect","Dragon Breath","Dynamic Punch","Encore","Endure","Extreme Speed","False Swipe","Feint Attack","Flail","Flame Wheel","Foresight","Frustration","Fury Cutter","Future Sight","Giga Drain","Heal Bell","Hidden Power","Icy Wind","Iron Tail","Lock-On","Mach Punch","Magnitude","Mean Look","Megahorn","Metal Claw","Milk Drink","Mind Reader","Mirror Coat","Moonlight","Morning Sun","Mud-Slap","Nightmare","Octazooka","Outrage","Pain Split","Perish Song","Powder Snow","Present","Protect","Psych Up","Pursuit","Rain Dance","Rapid Spin","Return","Reversal","Rock Smash","Rollout","Sacred Fire","Safeguard","Sandstorm","Scary Face","Shadow Ball","Sketch","Sleep Talk","Sludge Bomb","Snore","Spark","Spider Web","Spikes","Spite","Steel Wing","Sunny Day","Swagger","Sweet Kiss","Sweet Scent","Synthesis","Thief","Triple Kick","Twister","Vital Throw","Whirlpool","Zap Cannon" };
    static String genIII_moves_list [] = { "Aerial Ace",  "Air Cutter",  "Arm Thrust",  "Aromatherapy",  "Assist",  "Astonish",  "Blast Burn",  "Blaze Kick",  "Block",  "Bounce",  "Brick Break",  "Bulk Up",  "Bullet Seed",  "Calm Mind",  "Camouflage",  "Charge",  "Cosmic Power",  "Covet",  "Crush Claw",  "Dive",  "Doom Desire",  "Dragon Claw",  "Dragon Dance",  "Endeavor",  "Eruption",  "Extrasensory",  "Facade",  "Fake Out",  "Fake Tears",  "Feather Dance",  "Flatter",  "Focus Punch",  "Follow Me",  "Frenzy Plant",  "Grass Whistle",  "Grudge",  "Hail",  "Heat Wave",  "Helping Hand",  "Howl",  "Hydro Cannon",  "Hyper Voice",  "Ice Ball",  "Icicle Spear",  "Imprison",  "Ingrain",  "Iron Defense",  "Knock Off",  "Leaf Blade",  "Luster Purge",  "Magic Coat",  "Magical Leaf",  "Memento",  "Metal Sound",  "Meteor Mash",  "Mist Ball",  "Mud Shot",  "Mud Sport",  "Muddy Water",  "Nature Power",  "Needle Arm",  "Odor Sleuth",  "Overheat",  "Poison Fang",  "Poison Tail",  "Psycho Boost", "Recycle",  "Refresh",  "Revenge",  "Rock Blast",  "Rock Tomb",  "Role Play",  "Sand Tomb",  "Secret Power",  "Shadow Blast",  "Shadow Blitz",  "Shadow Bolt",  "Shadow Break",  "Shadow Chill",  "Shadow Down",  "Shadow End",  "Shadow Fire",  "Shadow Half",  "Shadow Hold",  "Shadow Mist",  "Shadow Panic",  "Shadow Punch",  "Shadow Rave",  "Shadow Rush",  "Shadow Shed",  "Shadow Sky",  "Shadow Storm",  "Shadow Wave",  "Sheer Cold",  "Shock Wave",  "Signal Beam",  "Silver Wind",  "Skill Swap",  "Sky Uppercut",  "Slack Off",  "Smelling Salts",  "Snatch",  "Spit Up",  "Stockpile",  "Superpower",  "Swallow",  "Tail Glow",  "Taunt",  "Teeter Dance",  "Tickle",  "Torment",  "Trick",  "Uproar",  "Volt Tackle",  "Water Pulse",  "Water Sport",  "Water Spout",  "Weather Ball",  "Will-O-Wisp",  "Wish", "Yawn" };
    static String genIV_moves_list [] = {"Acupressure",  "Air Slash",  "Aqua Jet",  "Aqua Ring",  "Aqua Tail",  "Assurance",  "Attack Order",  "Aura Sphere",  "Avalanche",  "Brave Bird",  "Brine",  "Bug Bite",  "Bug Buzz",  "Bullet Punch",  "Captivate",  "Charge Beam",  "Chatter",  "Close Combat",  "Copycat",  "Cross Poison",  "Crush Grip",  "Dark Pulse",  "Dark Void",  "Defend Order",  "Defog",  "Discharge",  "Double Hit",  "Draco Meteor",  "Dragon Pulse",  "Dragon Rush",  "Drain Punch",  "Earth Power",  "Embargo",  "Energy Ball",  "Feint",  "Fire Fang",  "Flare Blitz",  "Flash Cannon",  "Fling",  "Focus Blast",  "Force Palm",  "Gastro Acid",  "Giga Impact",  "Grass Knot",  "Gravity",  "Guard Swap",  "Gunk Shot",  "Gyro Ball",  "Hammer Arm",  "Head Smash",  "Heal Block",  "Heal Order",  "Healing Wish",  "Heart Swap",  "Ice Fang",  "Ice Shard",  "Iron Head",  "Judgment",  "Last Resort",  "Lava Plume",  "Leaf Storm",  "Lucky Chant",  "Lunar Dance",  "Magma Storm",  "Magnet Bomb",  "Magnet Rise",  "Me First",  "Metal Burst",  "Miracle Eye",  "Mirror Shot",  "Mud Bomb",  "Nasty Plot",  "Natural Gift",  "Night Slash",  "Ominous Wind",  "Payback",  "Pluck",  "Poison Jab",  "Power Gem",  "Power Swap",  "Power Trick",  "Power Whip",  "Psycho Cut",  "Psycho Shift",  "Punishment",  "Roar of Time",  "Rock Climb",  "Rock Polish",  "Rock Wrecker",  "Roost",  "Seed Bomb",  "Seed Flare",  "Shadow Claw",  "Shadow Force",  "Shadow Sneak",  "Spacial Rend",  "Stealth Rock",  "Stone Edge",  "Sucker Punch",  "Switcheroo",  "Tailwind",  "Thunder Fang",  "Toxic Spikes",  "Trick Room",  "Trump Card",  "U-turn",  "Vacuum Wave",  "Wake-Up Slap",  "Wood Hammer",  "Worry Seed",  "Wring Out",  "X-Scissor",  "Zen Headbutt"};
    static String genV_moves_list[] = { "Acid Spray", "Acrobatics", "After You", "Ally Switch", "Autotomize", "Bestow", "Blue Flare", "Bolt Strike", "Bulldoze", "Chip Away", "Circle Throw", "Clear Smog", "Coil", "Cotton Guard", "Dragon Tail", "Drill Run", "Dual Chop", "Echoed Voice", "Electro Ball", "Electroweb", "Entrainment","Fiery Dance", "Final Gambit", "Fire Pledge", "Flame Burst", "Flame Charge", "Foul Play", "Freeze Shock", "Frost Breath", "Fusion Bolt", "Fusion Flare", "Gear Grind", "Glaciate", "Grass Pledge", "Guard Split", "Head Charge", "Heal Pulse", "Heart Stamp", "Heat Crash", "Heavy Slam", "Hex", "Hone Claws", "Horn Leech", "Hurricane",  "Ice Burn", "Icicle Crash", "Incinerate", "Inferno", "Leaf Tornado", "Low Sweep", "Magic Room", "Night Daze", "Power Split", "Psyshock", "Psystrike", "Quash", "Quick Guard", "Quiver Dance", "Rage Powder", "Razor Shell", "Reflect Type", "Relic Song", "Retaliate", "Round", "Sacred Sword", "Scald", "Searing Shot", "Secret Sword", "Shell Smash", "Shift Gear", "Simple Beam", "Sky Drop", "Sludge Wave", "Smack Down", "Snarl", "Soak", "Steamroller", "Stored Power", "Storm Throw", "Struggle Bug", "Synchronoise", "Tail Slap", "Techno Blast", "Telekinesis", "V-create", "Venoshock", "Volt Switch", "Water Pledge", "Wide Guard", "Wild Charge", "Wonder Room", "Work Up"};

    static String genVII_moves_list [] = {    "10,000,000 Volt Thunderbolt", "Accelerock", "Acid Downpour", "All-Out Pummeling", "Anchor Shot", "Aurora Veil", "Baddy Bad", "Baneful Bunker", "Beak Blast", "Black Hole Eclipse", "Bloom Doom", "Bouncy Bubble", "Breakneck Blitz", "Brutal Swing", "Burn Up", "Buzzy Buzz", "Catastropika", "Clanging Scales", "Clangorous Soulblaze", "Continental Crush", "Core Enforcer", "Corkscrew Crash", "Darkest Lariat", "Devastating Drake", "Double Iron Bash", "Dragon Hammer", "Extreme Evoboost", "Fire Lash", "First Impression", "Fleur Cannon", "Floaty Fall", "Floral Healing", "Freezy Frost", "Gear Up", "Genesis Supernova", "Gigavolt Havoc", "Glitzy Glow", "Guardian of Alola", "High Horsepower", "Hydro Vortex", "Ice Hammer", "Inferno Overdrive", "Instruct", "Laser Focus", "Leafage", "Let's Snuggle Forever", "Light That Burns the Sky", "Liquidation", "Lunge", "Malicious Moonsault", "Menacing Moonraze Maelstrom", "Mind Blown", "Moongeist Beam", "Multi-Attack", "Nature's Madness", "Never-Ending Nightmare", "Oceanic Operetta", "Photon Geyser", "Pika Papow", "Plasma Fists", "Pollen Puff", "Power Trip", "Prismatic Laser", "Psychic Fangs", "Psychic Terrain", "Pulverizing Pancake", "Purify", "Revelation Dance", "Sappy Seed", "Savage Spin-Out", "Searing Sunraze Smash", "Shadow Bone", "Shattered Psyche", "Shell Trap", "Shore Up", "Sinister Arrow Raid", "Sizzly Slide", "Smart Strike", "Solar Blade", "Soul-Stealing 7-Star Strike", "Sparkling Aria", "Sparkly Swirl", "Spectral Thief", "Speed Swap", "Spirit Shackle", "Splintered Stormshards", "Splishy Splash", "Spotlight", "Stoked Sparksurfer", "Stomping Tantrum", "Strength Sap", "Subzero Slammer", "Sunsteel Strike", "Supersonic Skystrike", "Tearful Look", "Tectonic Rage", "Throat Chop", "Toxic Thread", "Trop Kick", "Twinkle Tackle", "Veevee Volley", "Zing Zap", "Zippy Zap"};
    static String genVII_abilities_list [] = {"Battery", "Battle Bond", "Beast Boost", "Berserk", "Comatose", "Corrosion", "Dancer", "Dazzling", "Disguise", "Electric Surge", "Emergency Exit",  "Fluffy", "Full Metal Body",  "Galvanize", "Grassy Surge",  "Innards Out", "Liquid Voice", "Long Reach", "Merciless", "Misty Surge",  "Neuroforce",  "Power Construct", "Power of Alchemy", "Prism Armor", "Psychic Surge",  "Queenly Majesty",  "Receiver", "RKS System", "Schooling", "Shadow Shield", "Shields Down", "Slush Rush", "Soul-Heart", "Stakeout", "Stamina", "Steelworker", "Surge Surfer", "Tangling Hair", "Triage", "Water Bubble", "Water Compaction", "Wimp Out"};

    static String genVIII_moves_list [] = {"Apple Acid", "Astral Barrage", "Aura Wheel", "Barb Barrage", "Behemoth Bash", "Behemoth Blade", "Bitter Malice", "Bleakwind Storm", "Body Press", "Bolt Beak", "Branch Poke", "Breaking Swipe", "Burning Jealousy", "Ceaseless Edge", "Chloroblast", "Clangorous Soul", "Coaching", "Corrosive Gas", "Court Change", "Decorate", "Dire Claw", "Dragon Darts", "Dragon Energy", "Drum Beating", "Dual Wingbeat", "Dynamax Cannon", "Eerie Spell", "Esper Wing", "Eternabeam", "Expanding Force", "False Surrender", "Fiery Wrath", "Fishious Rend", "Flip Turn", "Freezing Glare", "G-Max Befuddle", "G-Max Cannonade", "G-Max Centiferno", "G-Max Chi Strike", "G-Max Cuddle", "G-Max Depletion", "G-Max Drum Solo", "G-Max Finale", "G-Max Fireball", "G-Max Foam Burst", "G-Max Gold Rush", "G-Max Gravitas", "G-Max Hydrosnipe", "G-Max Malodor", "G-Max Meltdown", "G-Max One Blow", "G-Max Rapid Flow", "G-Max Replenish", "G-Max Resonance", "G-Max Sandblast", "G-Max Smite", "G-Max Snooze", "G-Max Steelsurge", "G-Max Stonesurge", "G-Max Stun Shock", "G-Max Sweetness", "G-Max Tartness", "G-Max Terror", "G-Max Vine Lash", "G-Max Volcalith", "G-Max Volt Crash", "G-Max Wildfire", "G-Max Wind Rage", "Glacial Lance", "Grassy Glide", "Grav Apple", "Headlong Rush", "Infernal Parade", "Jaw Lock", "Jungle Healing", "Lash Out", "Life Dew", "Lunar Blessing", "Magic Powder", "Max Airstream", "Max Darkness", "Max Flare", "Max Flutterby", "Max Geyser", "Max Guard", "Max Hailstorm", "Max Knuckle", "Max Lightning", "Max Mindstorm", "Max Ooze", "Max Overgrowth", "Max Phantasm", "Max Quake", "Max Rockfall", "Max Starfall", "Max Steelspike", "Max Strike", "Max Wyrmwind", "Meteor Assault", "Meteor Beam", "Misty Explosion", "Mountain Gale", "Mystical Power", "No Retreat", "Obstruct", "Octolock", "Overdrive", "Poltergeist", "Power Shift", "Psyshield Bash", "Pyro Ball", "Raging Fury", "Rising Voltage", "Sandsear Storm", "Scale Shot", "Scorching Sands", "Shell Side Arm", "Shelter", "Skitter Smack", "Snap Trap", "Snipe Shot", "Spirit Break", "Springtide Storm", "Steel Beam", "Steel Roller", "Stone Axe", "Strange Steam", "Stuff Cheeks", "Surging Strikes", "Take Heart", "Tar Shot", "Teatime", "Terrain Pulse", "Thunder Cage", "Thunderous Kick", "Triple Arrows", "Triple Axel", "Victory Dance", "Wave Crash", "Wicked Blow", "Wildbolt Storm"};
    static String genVIII_abilities_list [] = {"As One", "Ball Fetch", "Chilling Neigh", "Cotton Down", "Curious Medicine", "Dauntless Shield", "Dragon's Maw", "Gorilla Tactics", "Grim Neigh", "Gulp Missile", "Hunger Switch", "Ice Face", "Ice Scales", "Intrepid Sword", "Libero", "Mimicry", "Mirror Armor", "Neutralizing Gas", "Pastel Veil", "Perish Body", "Power Spot", "Propeller Tail", "Punk Rock", "Quick Draw", "Ripen", "Sand Spit", "Screen Cleaner", "Stalwart", "Steam Engine", "Steely Spirit", "Transistor", "Unseen Fist", "Wandering Spirit"};

    static String genIX_moves_list [] = {"Alluring Voice", "Aqua Cutter", "Aqua Step", "Armor Cannon", "Axe Kick", "Bitter Blade", "Blazing Torque", "Blood Moon", "Burning Bulwark", "Chilling Water", "Chilly Reception", "Collision Course", "Combat Torque", "Comeuppance", "Doodle", "Double Shock", "Dragon Cheer", "Electro Drift", "Electro Shot", "Fickle Beam", "Fillet Away", "Flower Trick", "Gigaton Hammer", "Glaive Rush", "Hard Press", "Hydro Steam", "Hyper Drill", "Ice Spinner", "Ivy Cudgel", "Jet Punch", "Kowtow Cleave", "Last Respects", "Lumina Crash", "Magical Torque", "Make It Rain", "Malignant Chain", "Matcha Gotcha", "Mighty Cleave", "Mortal Spin", "Noxious Torque", "Order Up", "Population Bomb", "Pounce", "Psyblade", "Psychic Noise", "Rage Fist", "Raging Bull", "Revival Blessing", "Ruination", "Salt Cure", "Shed Tail", "Silk Trap", "Snowscape", "Spicy Extract", "Spin Out", "Supercell Slam", "Syrup Bomb", "Tachyon Cutter", "Temper Flare", "Tera Blast", "Tera Starstorm", "Thunderclap", "Tidy Up", "Torch Song", "Trailblaze", "Triple Dive", "Twin Beam", "Upper Hand", "Wicked Torque"};
    static String genIX_abilities_list [] = {"Anger Shell", "Armor Tail", "Beads of Ruin", "Commander", "Costar", "Cud Chew", "Earth Eater", "Electromorphosis", "Embody Aspect", "Good as Gold", "Guard Dog", "Hadron Engine", "Hospitality", "Lingering Aroma", "Mind's Eye", "Mycelium Might", "Opportunist", "Orichalcum Pulse", "Poison Puppeteer", "Protosynthesis", "Purifying Salt", "Quark Drive", "Rocky Payload", "Seed Sower", "Sharpness", "Supersweet Syrup", "Supreme Overlord", "Sword of Ruin", "Tablets of Ruin", "Tera Shell", "Tera Shift", "Teraform Zero", "Thermal Exchange", "Toxic Chain", "Toxic Debris", "Vessel of Ruin", "Well-Baked Body", "Wind Power", "Wind Rider", "Zero to Hero"};


    static OntClass pokemonGenerationCls, pokedexCls, pokemonCls, pokemonTypeCls, pokemonEFamilyCls, pokemonMoveCls,
                    nationalPokedex, pokemonGenderRatioCls, pokemonEggGroupCls, pokemonAbilityCls, megaStoneCls, itemCls;
    static Individual genI, genII, genIII, genIV, genV, genVI, genVII, genVIII, genIX;
    static ObjectProperty firstAppearance, instanceOf, presentInWork, partOf, hasParts,
                          follows, followedBy, uses, hasPokedexNumber;
    static DatatypeProperty hasFacet, hasShape, hasName, hasNumber, hasColor, hasHeight, hasWeight, hasValue;

    public static void initCommons()
    {
        Configs configs = new Configs();
        theModel        = ModelFactory.createOntologyModel();

        pokemonMoveCls = theModel.createClass(wd + "Q15141195"); // a pokemon move
        pokemonMoveCls.addLabel("Pokemon Move", "en");

        pokedexCls = theModel.createClass(wd + "Q1250520"); // the pokedex class
        pokedexCls.addLabel("Pokedex", "en");

        hasFacet = theModel.createDatatypeProperty(ps + "P1269");
        hasFacet.addLabel("has Facet", "en");
        hasShape = theModel.createDatatypeProperty(wdt + "P1419");
        hasShape.addLabel("has Shape", "en");

        follows = theModel.createObjectProperty(p + "P155");
        follows.addLabel("follows", "en");

        followedBy = theModel.createObjectProperty(p + "P156");
        followedBy.addLabel("followed By", "en");

        partOf = theModel.createObjectProperty(wdt + "P361");
        partOf.addLabel("part Of", "en");

        hasParts = theModel.createObjectProperty(wdt + "P527");
        hasParts.addLabel("has Part", "en");

        pokemonGenerationCls = theModel.createClass(wd + "Q3759600" ); //a pokemon generation
        pokemonGenerationCls.addLabel("Pokemon Generation", "en");

        firstAppearance        = theModel.createObjectProperty(p + "P4584"); // first appearance property
        firstAppearance.addLabel("introduced in", "en");

        presentInWork        = theModel.createObjectProperty(p + "P1441"); // present in property
        presentInWork   .addLabel("present in", "en");
        firstAppearance.setSuperProperty(presentInWork);

        pokemonCls  = theModel.createClass(wd + "Q3966183" ); //a pokemon
        pokemonCls  .addLabel("Pokemon", "en");

        pokemonTypeCls  = theModel.createClass(wd + "Q115980997" ); //a pokemon type
        pokemonTypeCls  .addLabel("Pokemon Type", "en");

        pokemonEFamilyCls   = theModel.createClass(wd + "Q15795637" ); //a pokemon evolutionary family
        pokemonEFamilyCls   .addLabel("Pokemon Evolutionary Line", "en");

        hasPokedexNumber = theModel.createObjectProperty(ps + "P1685");
        hasNumber        = theModel.createDatatypeProperty(ps + "P972");

        hasPokedexNumber.addLabel("Pokemon Dex Number", "en");
        hasNumber       .addLabel("has Pokedex Number", "en");

        hasName = theModel.createDatatypeProperty(ps + "name");
        hasName.addLabel("has Name", "en");
        hasColor = theModel.createDatatypeProperty(ps + "P462");
        hasColor.addLabel("has Color", "en");
        hasHeight = theModel.createDatatypeProperty(ps + "P2048");
        hasHeight.addLabel("has Height", "en");
        hasWeight = theModel.createDatatypeProperty(ps + "P2067");
        hasWeight.addLabel("has Weight", "en");

        instanceOf = theModel.createObjectProperty(p + "P31");
        hasValue   = theModel.createDatatypeProperty(ps + "P31");
        instanceOf.addLabel("instance Of", "en");
        hasValue.addLabel("value", "en");

        genI = pokemonGenerationCls.createIndividual(wd + "Q27118928"); // gen I pokemon Generation
        genI.addLabel("Generation I", "en");

        nationalPokedex = theModel.createClass(wd + "Q20005020");
        nationalPokedex.addLabel("National Pokédex", "en");
        nationalPokedex.addProperty(firstAppearance, genI);
        nationalPokedex.addSuperClass(pokedexCls);
    }


    public static void main(String[] args)
    {
        initCommons();

        convertPokedexGenI();
        convertPokemonGenI();
        convertMovesGenI();

        Utils.OntologyUtils.writeModeltoFile(theModel, base + "genI.ttl");

        genII = pokemonGenerationCls.createIndividual(wd + "Q27118900"); // gen II pokemon Generation
        genII.addLabel("Generation II", "en");
        genI .addProperty(followedBy, genII);
        genII.addProperty(follows,    genI);

        //gen II introduces Egg and Gender

        pokemonGenderRatioCls = theModel.createClass(wd + "Q116753925" ); //a pokemon gender ratio
        pokemonEggGroupCls    = theModel.createClass(wd + "Q26037540" ); //a pokemon egg group

        pokemonGenderRatioCls.addLabel("Pokemon Gender Ratio", "en");
        pokemonEggGroupCls   .addLabel("Pokemon Egg Group", "en");

        convertPokedexGenII();
        convertPokemonGenII();
        convertMovesGenII();

        Utils.OntologyUtils.writeModeltoFile(theModel, base + "genII.ttl");

        genIII = pokemonGenerationCls.createIndividual(wd + "Q27118889"); // gen III pokemon Generation
        genIII.addLabel("Generation III", "en");
        genII .addProperty(followedBy, genIII);
        genIII.addProperty(follows,    genII);

        // gen III introduces abilities

        uses = theModel.createObjectProperty(p + "P2283");
        uses.addLabel("uses", "en");

        pokemonAbilityCls = theModel.createClass(wd + "Q12640000"); // a pokemon ability
        pokemonAbilityCls.addLabel("Pokemon Ability", "en");

        convertAbilitiesGenIII();
        convertMovesGenIII();
        convertPokedexGenIII();
        convertPokemonGenIII();

        Utils.OntologyUtils.writeModeltoFile(theModel, base + "genIII.ttl");

        genIV = pokemonGenerationCls.createIndividual(wd + "Q27118795"); // gen IV pokemon Generation
        genIV .addLabel("Generation IV", "en");
        genIII.addProperty(followedBy, genIV);
        genIV .addProperty(follows,    genIII);

        convertMovesGenIV();
        convertAbilitiesGenIV();
        convertPokedexGenIV();
        convertPokemonGenIV();

        Utils.OntologyUtils.writeModeltoFile(theModel, base + "genIV.ttl");

        genV = pokemonGenerationCls.createIndividual(wd + "Q27118381"); // gen V pokemon Generation
        genV .addLabel("Generation V", "en");
        genIV.addProperty(followedBy, genV);
        genV .addProperty(follows,    genIV);

        convertMovesGenV();
        convertAbilitiesGenV();
        convertPokedexGenV();
        convertPokemonGenV();

        Utils.OntologyUtils.writeModeltoFile(theModel, base + "genV.ttl");

        // generation VI introduces mega evolution
        megaStoneCls    = theModel.createClass(wd + "Q56676211" ); // a pokemon megastone
        itemCls         = theModel.createClass(wd + "Q27302146" ); // a pokemon item

        itemCls     .addLabel("Pokémon Item", "en");
        megaStoneCls.addLabel("Mega Stone",   "en");
        megaStoneCls.addSuperClass(itemCls);

        genVI = pokemonGenerationCls.createIndividual(wd + "Q27065429"); // gen VI pokemon Generation
        genVI.addLabel("Generation VI", "en");
        genV .addProperty(followedBy, genVI);
        genVI.addProperty(follows,    genV);

        convertMovesGenVI();
        convertAbilitiesGenVI();
        convertPokedexGenVI();
        convertPokemonGenVI();

        Utils.OntologyUtils.writeModeltoFile(theModel, base + "genVI.ttl");

        genVII = pokemonGenerationCls.createIndividual(wd + "Q26945334"); // gen VII pokemon Generation
        genVII.addLabel("Generation VII", "en");
        genVI .addProperty(followedBy, genVII);
        genVII.addProperty(follows,    genVI);

        convertMovesGenVII();
        convertAbilitiesGenVII();
        convertPokedexGenVII();
        convertPokemonGenVII();

        Utils.OntologyUtils.writeModeltoFile(theModel, base + "genVII.ttl");

        genVIII = pokemonGenerationCls.createIndividual(wd + "Q61951126"); // gen VIII pokemon Generation
        genVIII.addLabel("Generation VIII", "en");
        genVII .addProperty(followedBy, genVIII);
        genVIII.addProperty(follows,    genVII);

        convertMovesGenVIII();
        convertAbilitiesGenVIII();
        convertPokedexGenVIII();
        convertPokemonGenVIII();

        Utils.OntologyUtils.writeModeltoFile(theModel, base + "genVIII.ttl");

        genIX = pokemonGenerationCls.createIndividual(wd + "Q111033398"); // gen IX pokemon Generation
        genIX  .addLabel("Generation IX", "en");
        genVIII.addProperty(followedBy, genIX);
        genIX  .addProperty(follows,    genVIII);

        convertMovesGenIX();
        convertAbilitiesGenIX();
        convertPokedexGenIX();
        convertPokemonGenIX();

        Utils.OntologyUtils.writeModeltoFile(theModel, base + "genIX.ttl");

    }


    /*
        GENERATION I KANTO
     */

    static boolean isInGenIMoveList(String moveName)
    {
        for(String move : genI_moves_list)
        {
            if(move.equalsIgnoreCase(moveName))
                return true;
        }
        return false;
    }

    public static void convertMovesGenI()
    {
        String movesGenI  = base + "pokemonTypeMoveCategory.json";
        JSONParser parser = new JSONParser();JSONArray a;

        try { a = (JSONArray) parser.parse(new FileReader(movesGenI)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID            = (String) pokemon.get("pokeMove");
            String moveLabel         = (String) pokemon.get("pokeMoveLabel");
            String moveTypeStmt      = (String) pokemon.get("typeMoveStmt");
            String moveTypeStmtLabel = (String) pokemon.get("typeMoveStmtLabel");

            if(!JSON2RDF.isInGenIMoveList(moveLabel)) continue;

            Individual moveTypeIndividual    = pokemonMoveCls.createIndividual(moveTypeStmt); //the move type
            moveTypeIndividual.addLabel("Move Type " + moveTypeStmtLabel, "en");
            moveTypeIndividual.addProperty(instanceOf, pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue, moveTypeStmtLabel);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel("Move " + moveLabel, "en");
            pokemonMove.addProperty(hasValue, moveLabel);

            pokemonMove.addProperty(instanceOf, moveTypeIndividual);

            String typeString = moveTypeStmtLabel.replace("Pokémon move", "").replace("-type", "").trim();

            pokemonMove.addProperty(firstAppearance, genI);
            pokemonMove.addProperty(presentInWork, genI);

            moveTypeIndividual.addProperty(hasFacet, typeString);
        }

    }
    public static void convertPokemonGenI()
    {
        String pokemonGenI       = base + "Pokemons Gen I.json";

        JSONParser parser = new JSONParser(); JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(pokemonGenI)); }
        catch (Exception e) {  System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID     = (String) pokemon.get("pokemon");
            String pokemonLabel  = (String) pokemon.get("pokemonLabel");
            String pokedexNumber = (String) pokemon.get("pokedexNumber");

            int dexno = Integer.parseInt(pokedexNumber);
            if(dexno > 151 || pokemonLabel.toLowerCase().contains("alolan"))
                continue;

            pokedexNumber = String.format("%04d", dexno);

            Individual thePoke = theModel.createIndividual(pokemonID, pokemonCls);

            thePoke.addLabel(pokedexNumber + " " + pokemonLabel, "en");
            thePoke.addProperty(hasName, pokemonLabel);

            String evolFamilyStmt      = (String) pokemon.get("evolFamilyStmt");
            String evolFamilyStmtLabel = (String) pokemon.get("evolFamilyStmtLabel");

            if(evolFamilyStmtLabel != null && !evolFamilyStmtLabel.isEmpty() && evolFamilyStmt != null && !evolFamilyStmt.isEmpty())
            {
                evolFamilyStmtLabel = evolFamilyStmtLabel
                        .replace("Pichu", "Pikachu")
                        .replace("Igglybuff", "Jigglypuff")
                        .replace("Cleffa", "Clefairy"); // some evol lines have names that mention pokemons from other Gens!!

                Individual pokemonEvolutioanryFamily = pokemonEFamilyCls.createIndividual(evolFamilyStmt);
                pokemonEvolutioanryFamily.addLabel("EvolLine " + evolFamilyStmtLabel, "en");

                pokemonEvolutioanryFamily.addProperty(hasName, evolFamilyStmtLabel);
                thePoke.addProperty(partOf, pokemonEvolutioanryFamily);
                pokemonEvolutioanryFamily.addProperty(hasParts, thePoke);
            }

            String typeLabel   = (String) pokemon.get("typeLabel");
            String type        = (String) pokemon.get("type");
            String colorLabel  = (String) pokemon.get("colorLabel");
            String weightLabel = (String) pokemon.get("weightLabel");
            String heightLabel = (String) pokemon.get("heightLabel");

            if(typeLabel != null && !typeLabel.isEmpty())
            {
                Individual pokemonType = pokemonTypeCls.createIndividual(type);
                pokemonType.addLabel("Type " + typeLabel, "en");
                pokemonType.addProperty(hasValue, typeLabel);

                thePoke.addProperty(instanceOf, pokemonType);

                String typeString = typeLabel.replace("Pokémon", "").replace("-type", "").trim();

                pokemonType.addProperty(hasFacet, typeString);
            }

            if(colorLabel !=null && !colorLabel.isEmpty())
                thePoke.addProperty(hasColor, colorLabel);

            if(weightLabel !=null && !weightLabel.isEmpty())
                thePoke.addProperty(hasWeight, weightLabel);

            if(heightLabel !=null && !heightLabel.isEmpty())
                thePoke.addProperty(hasHeight, heightLabel);

            thePoke.addProperty(firstAppearance, genI);
            thePoke.addProperty(presentInWork,   genI);

        }
    }

    public static void convertPokedexGenI()
    {
        String pokedexGenII       = base + "gen II pokedex numbers.json";

        JSONParser parser = new JSONParser(); JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(pokedexGenII)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID     = (String) pokemon.get("pokemon");
            String pokemonLabel  = (String) pokemon.get("pokemonLabel");

            String national_pokedex_no  = (String) pokemon.get("pokedexNumber");
            String national_pokedexStmt = (String) pokemon.get("statement");

            int dexno = Integer.parseInt(national_pokedex_no);
            national_pokedex_no = String.format("%04d", dexno);

            if(dexno > 151) continue;
            if(pokemonLabel.toLowerCase().contains("alolan"))  continue;

            Individual nationalPokedexEntry = nationalPokedex.createIndividual(national_pokedexStmt);
            nationalPokedexEntry.addLabel("National Pokedex Entry  " + national_pokedex_no, "en");
            nationalPokedexEntry.addProperty(hasNumber, national_pokedex_no);

            Individual thePoke = theModel.createIndividual(pokemonID, pokemonCls);
            thePoke.addProperty(hasPokedexNumber, nationalPokedexEntry);

            thePoke.addLabel(national_pokedex_no + " " + pokemonLabel, "en");
            thePoke.addProperty(hasName, pokemonLabel);

            nationalPokedexEntry   .addProperty(partOf, nationalPokedex);
            nationalPokedex        .addProperty(hasParts, nationalPokedexEntry);

        }
    }

    /*
            GENERATION II JOHTO
     */

    static boolean isInGenIIMoveList(String moveName)
    {
        for(String move : genII_moves_list)
        {
            if(move.equalsIgnoreCase(moveName))
                return true;
        }
        return false;
    }

    public static void convertMovesGenII()
    {
        String pokemonMovesFile       = base + "pokemonTypeMoveCategory.json";

        JSONParser parser = new JSONParser(); JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(pokemonMovesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID            = (String) pokemon.get("pokeMove");
            String moveLabel         = (String) pokemon.get("pokeMoveLabel");
            String moveTypeStmt      = (String) pokemon.get("typeMoveStmt");
            String moveTypeStmtLabel = (String) pokemon.get("typeMoveStmtLabel");

            if(!JSON2RDF.isInGenIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIMoveList(moveLabel))
                continue;

            Individual moveTypeIndividual    = pokemonMoveCls.createIndividual(moveTypeStmt); //the move type
            moveTypeIndividual.addLabel("Move Type " + moveTypeStmtLabel, "en");
            moveTypeIndividual.addProperty(instanceOf, pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue, moveTypeStmtLabel);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel("Move " + moveLabel, "en");
            pokemonMove.addProperty(hasValue, moveLabel);

            pokemonMove.addProperty(instanceOf, moveTypeIndividual);

            String typeString = moveTypeStmtLabel.replace("Pokémon move", "").replace("-type", "").trim();

            pokemonMove.addProperty(presentInWork, genII);

            if(JSON2RDF.isInGenIIMoveList(moveLabel))
                pokemonMove.addProperty(firstAppearance, genII);

            pokemonMove.addProperty(presentInWork, genII);

            moveTypeIndividual.addProperty(hasFacet, typeString);
        }
    }

    public static void convertPokemonGenII()
    {
        String generations [] = { base + "Pokemons Gen II.json", base + "Pokemons Gen I.json" };

        for(String pokeList : generations)
        {
            JSONParser parser = new JSONParser(); JSONArray a;
            try
            {
                a = (JSONArray) parser.parse(new FileReader(pokeList));
            } catch (Exception e)
            {
                System.out.println("OH NO . " + e.getMessage());
                return;
            }

            for (Object o : a) {
                JSONObject pokemon = (JSONObject) o;

                String pokemonID = (String) pokemon.get("pokemon");

                String pokemonLabel = (String) pokemon.get("pokemonLabel");
                String pokedexNumber = (String) pokemon.get("pokedexNumber");

                int dexno = Integer.parseInt(pokedexNumber);

                if (dexno > 251) continue;
                if (pokemonLabel.toLowerCase().contains("alolan")) continue;

                pokedexNumber = String.format("%04d", dexno);

                Individual thePoke = theModel.createIndividual(pokemonID, pokemonCls);

                thePoke.addLabel(pokedexNumber + " " + pokemonLabel, "en");
                thePoke.addProperty(hasName, pokemonLabel);

                String evolFamilyStmt = (String) pokemon.get("evolFamilyStmt");
                String evolFamilyStmtLabel = (String) pokemon.get("evolFamilyStmtLabel");

                if (evolFamilyStmtLabel != null && !evolFamilyStmtLabel.isEmpty() && evolFamilyStmt != null && !evolFamilyStmt.isEmpty()) {
                    Individual pokemonEvolutioanryFamily = pokemonEFamilyCls.createIndividual(evolFamilyStmt);
                    pokemonEvolutioanryFamily.addLabel("EvolLine " + evolFamilyStmtLabel, "en");

                    pokemonEvolutioanryFamily.addProperty(hasName, evolFamilyStmtLabel);
                    thePoke.addProperty(partOf, pokemonEvolutioanryFamily);
                    pokemonEvolutioanryFamily.addProperty(hasParts, thePoke);
                }

                String eggGroupStmt = (String) pokemon.get("eggGroupStmt");
                String eggGroupStmtLabel = (String) pokemon.get("eggGroupStmtLabel");

                if (eggGroupStmtLabel != null && !eggGroupStmtLabel.isEmpty() && eggGroupStmt != null && !eggGroupStmt.isEmpty()) {
                    Individual pokemonEggGroup = pokemonEggGroupCls.createIndividual(eggGroupStmt);
                    pokemonEggGroup.addLabel("Egg Group " + eggGroupStmtLabel, "en");

                    pokemonEggGroup.addProperty(hasName, eggGroupStmtLabel);
                    thePoke.addProperty(partOf, pokemonEggGroup);
                    pokemonEggGroup.addProperty(hasParts, thePoke);

                }

                String genderRatioStmt = (String) pokemon.get("genderRatioStmt");
                String genderRatioStmtLabel = (String) pokemon.get("grStmtLabel");

                if (genderRatioStmt != null && !genderRatioStmt.isEmpty() && genderRatioStmtLabel != null && !genderRatioStmtLabel.isEmpty()) {
                    Individual pokemonGenderRatio = pokemonGenderRatioCls.createIndividual(genderRatioStmt);
                    pokemonGenderRatio.addLabel("Gender Ratio " + genderRatioStmtLabel.replace("gender ratio", "").trim(), "en");

                    pokemonGenderRatio.addProperty(hasName, genderRatioStmtLabel);
                    thePoke.addProperty(partOf, pokemonGenderRatio);
                    pokemonGenderRatio.addProperty(hasParts, thePoke);
                }


                String typeLabel = (String) pokemon.get("typeLabel");
                String type = (String) pokemon.get("type");
                String colorLabel = (String) pokemon.get("colorLabel");
                String weightLabel = (String) pokemon.get("weightLabel");
                String heightLabel = (String) pokemon.get("heightLabel");
                String shapeLabel = (String) pokemon.get("shapeLabel");

                if (typeLabel != null && !typeLabel.isEmpty()) {
                    Individual pokemonType = pokemonTypeCls.createIndividual(type);
                    pokemonType.addLabel("Type " + typeLabel, "en");
                    pokemonType.addProperty(hasValue, typeLabel);

                    thePoke.addProperty(instanceOf, pokemonType);

                    String typeString = typeLabel.replace("Pokémon", "").replace("-type", "").trim();

                    pokemonType.addProperty(hasFacet, typeString);
                }

                if (colorLabel != null && !colorLabel.isEmpty())
                    thePoke.addProperty(hasColor, colorLabel);

                if (weightLabel != null && !weightLabel.isEmpty())
                    thePoke.addProperty(hasWeight, weightLabel);

                if (shapeLabel != null && !shapeLabel.isEmpty())
                    thePoke.addProperty(hasShape, shapeLabel);

                if (heightLabel != null && !heightLabel.isEmpty())
                    thePoke.addProperty(hasHeight, heightLabel);

                if (dexno > 151)
                    thePoke.addProperty(firstAppearance, genII);

                if (dexno <= 151) {
                    thePoke.addProperty(firstAppearance, genI);
                    thePoke.addProperty(presentInWork, genI);
                }

                thePoke.addProperty(presentInWork, genII);
            }
        }
    }

    public static void convertPokedexGenII()
    {
        String pokedexGenII       = base + "gen II pokedex numbers.json";


        OntClass johtoPokedex = theModel.createClass(wd + "Q11310550");
        johtoPokedex.addLabel("Johto Pokédex", "en");
        johtoPokedex.addProperty(firstAppearance, genII);

        johtoPokedex.addSuperClass(pokedexCls);

        JSONParser parser = new JSONParser(); JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(pokedexGenII)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID     = (String) pokemon.get("pokemon");
            String pokemonLabel  = (String) pokemon.get("pokemonLabel");

            String national_pokedex_no  = (String) pokemon.get("pokedexNumber");
            String national_pokedexStmt = (String) pokemon.get("statement");

            String johto_pokedex_no  = (String) pokemon.get("pokedexNumber2");
            String johto_pokedexStmt = (String) pokemon.get("statement2");

            int dexno = Integer.parseInt(national_pokedex_no);
            national_pokedex_no = String.format("%04d", dexno);

            if(dexno > 251) continue;
            if(pokemonLabel.toLowerCase().contains("alolan"))  continue;

            Individual nationalPokedexEntry = nationalPokedex.createIndividual(national_pokedexStmt);

            nationalPokedexEntry.addLabel("National Pokedex Entry  " + national_pokedex_no, "en");
            nationalPokedexEntry.addProperty(hasNumber, national_pokedex_no);

            Individual thePoke = theModel.createIndividual(pokemonID, pokemonCls);
            thePoke.addProperty(hasPokedexNumber, nationalPokedexEntry);

            thePoke.addLabel(national_pokedex_no + " " + pokemonLabel, "en");
            thePoke.addProperty(hasName, pokemonLabel);

            nationalPokedexEntry   .addProperty(partOf, nationalPokedex);
            nationalPokedex        .addProperty(hasParts, nationalPokedexEntry);

            if(johto_pokedex_no != null && !johto_pokedex_no.isEmpty() && johto_pokedexStmt != null && !johto_pokedexStmt.isEmpty() )
            {
                int dexno2       = Integer.parseInt(johto_pokedex_no);
                johto_pokedex_no = String.format("%04d", dexno2);

                Individual johtoPokedexEntry    = johtoPokedex.createIndividual(johto_pokedexStmt);

                johtoPokedexEntry.addLabel("Johto Pokedex Entry " + johto_pokedex_no, "en");

                johtoPokedexEntry   .addProperty(hasNumber, johto_pokedex_no);
                johtoPokedexEntry   .addProperty(partOf, johtoPokedex);
                johtoPokedex        .addProperty(hasParts, johtoPokedexEntry);
                thePoke             .addProperty(hasPokedexNumber, johtoPokedexEntry);
            }
        }
    }

    /*
        GEN III HOENN
     */

    static boolean isInGenIIIMoveList(String moveName)
    {
        for(String move : genIII_moves_list)
        {
            if(move.equalsIgnoreCase(moveName))
                return true;
        }
        return false;
    }


    static boolean isGenIIIAbility(String moveName)
    {
        for(String move : genIII_abilities_list)
        {
            if(move.equalsIgnoreCase(moveName))
                return true;
        }
        return false;
    }

    public static void convertAbilitiesGenIII()
    {

        String all_abilities         = base + "all pokemon abilities.json";

        JSONParser parser = new JSONParser(); JSONArray a;

        //all abilities in wikidata
        try { a = (JSONArray) parser.parse(new FileReader(all_abilities)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonAbilityID    = (String) pokemon.get("pokemonAbility");
            String pokemonAbilityLabel = (String) pokemon.get("pokemonAbilityLabel");

            if(!JSON2RDF.isGenIIIAbility(pokemonAbilityLabel)) continue;
            Individual thePokeAbility = pokemonAbilityCls.createIndividual(pokemonAbilityID);
            thePokeAbility.addLabel("Ability " + pokemonAbilityLabel, "en");
            thePokeAbility.addProperty(hasValue, pokemonAbilityLabel);

            thePokeAbility.addProperty(firstAppearance, genIII);
            thePokeAbility.addProperty(presentInWork, genIII);
        }
    }

    public static void convertPokemonGenIII()
    {

        String generations [] = { base + "Pokemons Gen III.json", base + "Pokemons Gen II.json", base + "Pokemons Gen I.json" };

        for(String pokeList : generations)
        {
        JSONParser parser = new JSONParser(); JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(pokeList));}
        catch (Exception e) {            System.out.println("OH NO . " + e.getMessage());return;        }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID     = (String) pokemon.get("pokemon");
            String pokemonLabel  = (String) pokemon.get("pokemonLabel");
            String pokedexNumber = (String) pokemon.get("pokedexNumber");

            int dexno = Integer.parseInt(pokedexNumber);

            if(dexno > 368) continue;
            if(pokemonLabel.toLowerCase().contains("alolan"))  continue;

            pokedexNumber = String.format("%04d", dexno);

            Individual thePoke = theModel.createIndividual(pokemonID, pokemonCls);

            thePoke.addLabel(pokedexNumber + " " + pokemonLabel, "en");
            thePoke.addProperty(hasName, pokemonLabel);

            String evolFamilyStmt      = (String) pokemon.get("evolFamilyStmt");
            String evolFamilyStmtLabel = (String) pokemon.get("evolFamilyStmtLabel");

            if(evolFamilyStmtLabel != null && !evolFamilyStmtLabel.isEmpty() && evolFamilyStmt != null && !evolFamilyStmt.isEmpty())
            {
                Individual pokemonEvolutioanryFamily = pokemonEFamilyCls.createIndividual(evolFamilyStmt);
                pokemonEvolutioanryFamily.addLabel("EvolLine " + evolFamilyStmtLabel, "en");

                pokemonEvolutioanryFamily.addProperty(hasName, evolFamilyStmtLabel);
                thePoke.addProperty(partOf, pokemonEvolutioanryFamily);
                pokemonEvolutioanryFamily.addProperty(hasParts, thePoke);
            }

            String eggGroupStmt      = (String) pokemon.get("eggGroupStmt");
            String eggGroupStmtLabel = (String) pokemon.get("eggGroupStmtLabel");

            if(eggGroupStmtLabel != null && !eggGroupStmtLabel.isEmpty() && eggGroupStmt != null && !eggGroupStmt.isEmpty())
            {
                Individual pokemonEggGroup = pokemonEggGroupCls.createIndividual(eggGroupStmt);
                pokemonEggGroup.addLabel("Egg Group " + eggGroupStmtLabel, "en");
                pokemonEggGroup.addProperty(hasName, eggGroupStmtLabel);
                thePoke.addProperty(partOf, pokemonEggGroup);
                pokemonEggGroup.addProperty(hasParts, thePoke);

            }

            String genderRatioStmt      = (String) pokemon.get("genderRatioStmt");
            String genderRatioStmtLabel = (String) pokemon.get("grStmtLabel");

            if(genderRatioStmt != null && !genderRatioStmt.isEmpty() && genderRatioStmtLabel != null && !genderRatioStmtLabel.isEmpty())
            {
                Individual pokemonGenderRatio = pokemonGenderRatioCls.createIndividual(genderRatioStmt);
                pokemonGenderRatio.addLabel("Gender Ratio " + genderRatioStmtLabel.replace("gender ratio", "").trim(), "en");

                pokemonGenderRatio.addProperty(hasName, genderRatioStmtLabel);
                thePoke.addProperty(partOf, pokemonGenderRatio);
                pokemonGenderRatio.addProperty(hasParts, thePoke);
            }


            String typeLabel   = (String) pokemon.get("typeLabel");
            String type        = (String) pokemon.get("type");
            String colorLabel  = (String) pokemon.get("colorLabel");
            String weightLabel = (String) pokemon.get("weightLabel");
            String heightLabel = (String) pokemon.get("heightLabel");
            String shapeLabel  = (String) pokemon.get("shapeLabel");

            if(typeLabel != null && !typeLabel.isEmpty())
            {
                Individual pokemonType = pokemonTypeCls.createIndividual(type);
                pokemonType.addLabel("Type " + typeLabel, "en");
                pokemonType.addProperty(instanceOf, typeLabel);
                thePoke.addProperty(hasValue, pokemonType);
                String typeString = typeLabel.replace("Pokémon", "").replace("-type", "").trim();
                pokemonType.addProperty(hasFacet, typeString);
            }

            if(colorLabel !=null && !colorLabel.isEmpty())
                thePoke.addProperty(hasColor, colorLabel);

            if(weightLabel !=null && !weightLabel.isEmpty())
                thePoke.addProperty(hasWeight, weightLabel);

            if(shapeLabel !=null && !shapeLabel.isEmpty())
                thePoke.addProperty(hasShape, shapeLabel);

            if(heightLabel !=null && !heightLabel.isEmpty())
                thePoke.addProperty(hasHeight, heightLabel);

            if(dexno > 251)
                thePoke.addProperty(firstAppearance, genIII);

            thePoke.addProperty(presentInWork,   genIII);
        }}
    }
    public static void convertMovesGenIII()
    {
        String movesFile       = base + "pokemonTypeMoveCategory.json";

        JSONParser parser = new JSONParser(); JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        OntClass pokemonMoveCls    = theModel.createClass(wd + "Q15141195"); // a pokemon move
        pokemonMoveCls.addLabel("Pokemon Move", "en");

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID            = (String) pokemon.get("pokeMove");
            String moveLabel         = (String) pokemon.get("pokeMoveLabel");
            String moveTypeStmt      = (String) pokemon.get("typeMoveStmt");
            String moveTypeStmtLabel = (String) pokemon.get("typeMoveStmtLabel");

            if(!JSON2RDF.isInGenIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIIMoveList(moveLabel)) continue;

            Individual moveTypeIndividual    = pokemonMoveCls.createIndividual(moveTypeStmt); //the move type
            moveTypeIndividual.addLabel("Move Type " + moveTypeStmtLabel, "en");
            moveTypeIndividual.addProperty(instanceOf, pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue, moveTypeStmtLabel);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel("Move " + moveLabel, "en");
            pokemonMove.addProperty(hasValue, moveLabel);

            pokemonMove.addProperty(instanceOf, moveTypeIndividual);

            String typeString = moveTypeStmtLabel.replace("Pokémon move", "").replace("-type", "").trim();

            pokemonMove.addProperty(presentInWork, genIII);

            if(JSON2RDF.isInGenIIIMoveList(moveLabel))
                pokemonMove.addProperty(firstAppearance, genIII);

            moveTypeIndividual.addProperty(hasFacet, typeString);
        }

        movesFile       = base + "pokemonContestMoveCategory.json";

        parser  = new JSONParser();
        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID                   = (String) pokemon.get("pokeMove");
            String moveLabel                = (String) pokemon.get("pokeMoveLabel");
            String moveContestTypeStmt      = (String) pokemon.get("categoryMoveStmt");
            String moveContestTypeStmtLabel = (String) pokemon.get("categoryMoveStmtLabel");

            if(!JSON2RDF.isInGenIMoveList(moveLabel) && !JSON2RDF.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIIMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            moveTypeIndividual.addLabel("Contest Move Type " + moveContestTypeStmtLabel, "en");
            moveTypeIndividual.addProperty(instanceOf, pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue, moveContestTypeStmtLabel);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel("Move " + moveLabel, "en");
            pokemonMove.addProperty(hasValue, moveLabel);

            pokemonMove.addProperty(instanceOf, moveTypeIndividual);

            String typeString = moveContestTypeStmtLabel.replace("move", "").trim();

            pokemonMove.addProperty(presentInWork, genIII);

            if(JSON2RDF.isInGenIIIMoveList(moveLabel))
                pokemonMove.addProperty(firstAppearance, genIII);

            moveTypeIndividual.addProperty(hasFacet, typeString);
        }
    }

    public static void convertPokedexGenIII()
    {

        String pokedexGenIII       = base + "all pokedex entries.json";

        OntClass hoennPokedex = theModel.createClass(wd + "Q18086665");
        hoennPokedex.addLabel("Hoenn Pokédex", "en");
        hoennPokedex.addProperty(firstAppearance, genIII);
        hoennPokedex.addSuperClass(pokedexCls);

        JSONParser parser = new JSONParser();
        JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(pokedexGenIII)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID     = (String) pokemon.get("pokemon");
            String pokemonLabel  = (String) pokemon.get("pokemonLabel");

            String national_pokedex_no  = (String) pokemon.get("nationalDexNumber");
            String national_pokedexStmt = (String) pokemon.get("nationalStmt");

            String hoenn_pokedex_no  = (String) pokemon.get("hoennDexNumber");
            String hoennn_pokedexStmt = (String) pokemon.get("hoennStmt");

            int dexno = Integer.parseInt(national_pokedex_no);
            national_pokedex_no = String.format("%04d", dexno);

            if(dexno > 386) continue;
            if(pokemonLabel.toLowerCase().contains("alolan"))  continue;

            Individual nationalPokedexEntry = nationalPokedex.createIndividual(national_pokedexStmt);

            nationalPokedexEntry.addLabel("National Pokedex Entry  " + national_pokedex_no, "en");
            nationalPokedexEntry.addProperty(hasNumber, national_pokedex_no);

            Individual thePoke = theModel.createIndividual(pokemonID, pokemonCls);
            thePoke.addProperty(hasPokedexNumber, nationalPokedexEntry);

            thePoke.addLabel(national_pokedex_no + " " + pokemonLabel, "en");
            thePoke.addProperty(hasName, pokemonLabel);

            nationalPokedexEntry   .addProperty(partOf, nationalPokedex);
            nationalPokedex        .addProperty(hasParts, nationalPokedexEntry);

            if(hoenn_pokedex_no != null && !hoenn_pokedex_no.isEmpty() && hoennn_pokedexStmt != null && !hoennn_pokedexStmt.isEmpty() )
            {
                int dexno2 = Integer.parseInt(hoenn_pokedex_no);
                hoenn_pokedex_no = String.format("%04d", dexno2);

                Individual hoennPokedexEntry    = hoennPokedex.createIndividual(hoennn_pokedexStmt);
                hoennPokedexEntry.addLabel("Hoenn Pokedex Entry " + hoenn_pokedex_no, "en");
                hoennPokedexEntry   .addProperty(hasNumber, hoenn_pokedex_no);
                hoennPokedexEntry   .addProperty(partOf, hoennPokedex);
                hoennPokedex        .addProperty(hasParts, hoennPokedexEntry);
                thePoke.addProperty(hasPokedexNumber, hoennPokedexEntry);
            }
        }
    }


/*
    GEN IV SINNOH
 */
    public static void convertPokedexGenIV()
    {
        String pokedexFile   = base + "all pokedex entries.json";


        OntClass sinnohPokedex = theModel.createClass(wd + "Q18086666"); // sinnoh dex
        sinnohPokedex.addLabel("Sinnoh Pokédex", "en");
        sinnohPokedex.addProperty(firstAppearance, genIV);
        sinnohPokedex.addSuperClass(pokedexCls);

        JSONParser parser = new JSONParser();  JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(pokedexFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID     = (String) pokemon.get("pokemon");
            String pokemonLabel  = (String) pokemon.get("pokemonLabel");

            String national_pokedex_no  = (String) pokemon.get("nationalDexNumber");
            String national_pokedexStmt = (String) pokemon.get("nationalStmt");

            String sinnoh_pokedex_no  = (String) pokemon.get("sinnohDexNumber");
            String sinnoh_pokedexStmt = (String) pokemon.get("sinnohStmt");

            int dexno = Integer.parseInt(national_pokedex_no);
            national_pokedex_no = String.format("%04d", dexno);

            if(dexno > 493) continue;
            if(pokemonLabel.toLowerCase().contains("alolan"))  continue;

            Individual nationalPokedexEntry = nationalPokedex.createIndividual(national_pokedexStmt);

            nationalPokedexEntry.addLabel("National Pokedex Entry  " + national_pokedex_no, "en");
            nationalPokedexEntry.addProperty(hasNumber, national_pokedex_no);

            Individual thePoke = theModel.createIndividual(pokemonID, pokemonCls);
            thePoke.addProperty(hasPokedexNumber, nationalPokedexEntry);

            thePoke.addLabel(national_pokedex_no + " " + pokemonLabel, "en");
            thePoke.addProperty(hasName, pokemonLabel);

            nationalPokedexEntry   .addProperty(partOf, nationalPokedex);
            nationalPokedex        .addProperty(hasParts, nationalPokedexEntry);

            if(sinnoh_pokedex_no != null && !sinnoh_pokedex_no.isEmpty() && sinnoh_pokedexStmt != null && !sinnoh_pokedexStmt.isEmpty() )
            {
                int dexno2        = Integer.parseInt(sinnoh_pokedex_no);
                sinnoh_pokedex_no = String.format("%04d", dexno2);

                Individual sinnohPokedexEntry = sinnohPokedex.createIndividual(sinnoh_pokedexStmt);

                sinnohPokedexEntry   .addLabel("Sinnoh Pokedex Entry " + sinnoh_pokedex_no, "en");
                sinnohPokedexEntry   .addProperty(hasNumber, sinnoh_pokedex_no);
                sinnohPokedexEntry   .addProperty(partOf, sinnohPokedex);
                sinnohPokedex        .addProperty(hasParts, sinnohPokedexEntry);

                thePoke.addProperty(hasPokedexNumber, sinnohPokedexEntry);
            }
        }
    }

    public static void convertPokemonGenIV()
    {
        String generations [] = { base + "Pokemons Gen IV.json", base + "Pokemons Gen III.json", base + "Pokemons Gen II.json", base + "Pokemons Gen I.json" };

        for(String pokeList : generations)
        {
        JSONParser parser = new JSONParser(); JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(pokeList)); } catch (Exception e)
        { System.out.println("OH NO . " + e.getMessage()); return;        }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID = (String) pokemon.get("pokemon");

            String pokemonLabel  = (String) pokemon.get("pokemonLabel");
            String pokedexNumber = (String) pokemon.get("pokedexNumber");

            int dexno = Integer.parseInt(pokedexNumber);

            if(dexno > 493) continue;
            if(pokemonLabel.toLowerCase().contains("alolan"))  continue;

            pokedexNumber = String.format("%04d", dexno);

            Individual thePoke = theModel.createIndividual(pokemonID, pokemonCls);

            thePoke.addLabel(pokedexNumber + " " + pokemonLabel, "en");
            thePoke.addProperty(hasName, pokemonLabel);

            String evolFamilyStmt      = (String) pokemon.get("evolFamilyStmt");
            String evolFamilyStmtLabel = (String) pokemon.get("evolFamilyStmtLabel");

            if(evolFamilyStmtLabel != null && !evolFamilyStmtLabel.isEmpty() && evolFamilyStmt != null && !evolFamilyStmt.isEmpty())
            {
                Individual pokemonEvolutioanryFamily = pokemonEFamilyCls.createIndividual(evolFamilyStmt);
                pokemonEvolutioanryFamily.addLabel("EvolLine " + evolFamilyStmtLabel, "en");

                pokemonEvolutioanryFamily.addProperty(hasName, evolFamilyStmtLabel);
                thePoke.addProperty(partOf, pokemonEvolutioanryFamily);
                pokemonEvolutioanryFamily.addProperty(hasParts, thePoke);
            }

            String eggGroupStmt      = (String) pokemon.get("eggGroupStmt");
            String eggGroupStmtLabel = (String) pokemon.get("eggGroupStmtLabel");

            if(eggGroupStmtLabel != null && !eggGroupStmtLabel.isEmpty() && eggGroupStmt != null && !eggGroupStmt.isEmpty())
            {
                Individual pokemonEggGroup = pokemonEggGroupCls.createIndividual(eggGroupStmt);
                pokemonEggGroup.addLabel("Egg Group " + eggGroupStmtLabel, "en");

                pokemonEggGroup.addProperty(hasName, eggGroupStmtLabel);
                thePoke.addProperty(partOf, pokemonEggGroup);
                pokemonEggGroup.addProperty(hasParts, thePoke);
            }

            String genderRatioStmt      = (String) pokemon.get("genderRatioStmt");
            String genderRatioStmtLabel = (String) pokemon.get("grStmtLabel");

            if(genderRatioStmt != null && !genderRatioStmt.isEmpty() && genderRatioStmtLabel != null && !genderRatioStmtLabel.isEmpty())
            {
                Individual pokemonGenderRatio = pokemonGenderRatioCls.createIndividual(genderRatioStmt);

                pokemonGenderRatio.addLabel("Gender Ratio " + genderRatioStmtLabel.replace("gender ratio", "").trim(), "en");
                pokemonGenderRatio.addProperty(hasName, genderRatioStmtLabel);
                thePoke           .addProperty(partOf, pokemonGenderRatio);
                pokemonGenderRatio.addProperty(hasParts, thePoke);
            }

            String typeLabel   = (String) pokemon.get("typeLabel");
            String type        = (String) pokemon.get("type");
            String colorLabel  = (String) pokemon.get("colorLabel");
            String weightLabel = (String) pokemon.get("weightLabel");
            String heightLabel = (String) pokemon.get("heightLabel");
            String shapeLabel  = (String) pokemon.get("shapeLabel");

            if(typeLabel != null && !typeLabel.isEmpty())
            {
                String typeString = typeLabel.replace("Pokémon", "").replace("-type", "").trim();

                Individual pokemonType = pokemonTypeCls.createIndividual(type);

                pokemonType.addLabel("Type " + typeLabel, "en");
                pokemonType.addProperty(hasValue, typeLabel);
                thePoke    .addProperty(instanceOf, pokemonType);
                pokemonType.addProperty(hasFacet, typeString);
            }

            if(colorLabel !=null && !colorLabel.isEmpty())
                thePoke.addProperty(hasColor, colorLabel);

            if(weightLabel !=null && !weightLabel.isEmpty())
                thePoke.addProperty(hasWeight, weightLabel);

            if(shapeLabel !=null && !shapeLabel.isEmpty())
                thePoke.addProperty(hasShape, shapeLabel);

            if(heightLabel !=null && !heightLabel.isEmpty())
                thePoke.addProperty(hasHeight, heightLabel);

            if(dexno > 368)
                thePoke.addProperty(firstAppearance, genIV);
            thePoke.addProperty(presentInWork,   genIV);

        }}
    }


    public static void convertMovesGenIV()
    {
        String movesFile       = base + "pokemonTypeMoveCategory.json";

        JSONParser parser = new JSONParser(); JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }


        // check for move type
        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID            = (String) pokemon.get("pokeMove");
            String moveLabel         = (String) pokemon.get("pokeMoveLabel");
            String moveTypeStmt      = (String) pokemon.get("typeMoveStmt");
            String moveTypeStmtLabel = (String) pokemon.get("typeMoveStmtLabel");
            String typeString        = moveTypeStmtLabel.replace("Pokémon move", "").replace("-type", "").trim();

            if(!JSON2RDF.isInGenIMoveList(moveLabel) && !JSON2RDF.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIIMoveList(moveLabel) && !JSON2RDF.isInGenIVMoveList(moveLabel)) continue;

            Individual moveTypeIndividual    = pokemonMoveCls.createIndividual(moveTypeStmt); //the move type
            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID); // the move

            moveTypeIndividual.addLabel("Move Type " + moveTypeStmtLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,    pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,      moveTypeStmtLabel);

            pokemonMove       .addLabel("Move "      + moveLabel, "en");
            pokemonMove       .addProperty(hasValue,      moveLabel);
            pokemonMove       .addProperty(instanceOf,    moveTypeIndividual);
            pokemonMove       .addProperty(presentInWork, genIV);
            moveTypeIndividual.addProperty(hasFacet,      typeString);

            if(JSON2RDF.isInGenIVMoveList(moveLabel))
                pokemonMove.addProperty(firstAppearance, genIV);

        }

        // check for contest move type
        movesFile = base + "pokemonContestMoveCategory.json";
        parser    = new JSONParser();

        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID                   = (String) pokemon.get("pokeMove");
            String moveLabel                = (String) pokemon.get("pokeMoveLabel");
            String moveContestTypeStmt      = (String) pokemon.get("categoryMoveStmt");
            String moveContestTypeStmtLabel = (String) pokemon.get("categoryMoveStmtLabel");
            String typeString               = moveContestTypeStmtLabel.replace("move", "").trim();

            if(!JSON2RDF.isInGenIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIVMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            Individual pokemonMove         = pokemonMoveCls.createIndividual(moveID);

            moveTypeIndividual.addLabel("Contest Move Type " + moveContestTypeStmtLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,    pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,      moveContestTypeStmtLabel);
            pokemonMove       .addLabel("Move "      + moveLabel, "en");
            pokemonMove       .addProperty(hasValue,      moveLabel);
            pokemonMove       .addProperty(instanceOf,    moveTypeIndividual);
            pokemonMove       .addProperty(presentInWork, genIV);
            moveTypeIndividual.addProperty(hasFacet,      typeString);

            if(JSON2RDF.isInGenIVMoveList(moveLabel))
                pokemonMove.addProperty(firstAppearance, genIV);

        }

        // check for status/physical/special split

        movesFile = base + "pokemonDamageMoveCategory.json";
        parser    = new JSONParser();

        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID                   = (String) pokemon.get("pokeMove");
            String moveLabel                = (String) pokemon.get("pokeMoveLabel");
            String moveContestTypeStmt      = (String) pokemon.get("damageCategoryMoveStmt");
            String moveContestTypeStmtLabel = (String) pokemon.get("damageCategoryMoveStmtLabel");
            String typeString               = moveContestTypeStmtLabel.replace("move", "").trim();


            if(!JSON2RDF.isInGenIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIVMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual pokemonMove         = pokemonMoveCls.createIndividual(moveID); //the move
            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt);  //the move type

            moveTypeIndividual.addLabel("Damage Category Type " + moveContestTypeStmtLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,    pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,      moveContestTypeStmtLabel);
            pokemonMove       .addLabel("Move "      + moveLabel, "en");
            pokemonMove       .addProperty(hasValue,      moveLabel);
            pokemonMove       .addProperty(instanceOf,    moveTypeIndividual);
            moveTypeIndividual.addProperty(hasFacet,      typeString);
            pokemonMove       .addProperty(presentInWork, genIV);

            if(JSON2RDF.isInGenIVMoveList(moveLabel))
                pokemonMove.addProperty(firstAppearance, genIV);
        }
    }


    static boolean isGenIVAbility(String moveName)
    {
        for(String move : genIV_abilities_list)
            if(move.equalsIgnoreCase(moveName))
                return true;
        return false;
    }

    public static void convertAbilitiesGenIV()
    {

        String pokemonsWithAbilitiesFromWD       = base + "Pokemon Abilities.json";
        String all_abilities         = base + "all pokemon abilities.json";

        JSONParser parser = new JSONParser(); JSONArray a;

        try { a = (JSONArray) parser.parse(new FileReader(pokemonsWithAbilitiesFromWD)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }


        // abilities with pokemons associated in wikidata

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID           = (String) pokemon.get("pokemon");
            String pokemonLabel        = (String) pokemon.get("pokemonLabel");
            String pokemonAbilityID    = (String) pokemon.get("pokemonAbility");
            String pokemonAbilityLabel = (String) pokemon.get("pokemonAbilityLabel");

            if(!JSON2RDF.isGenIVAbility(pokemonAbilityLabel)) continue;

            Individual thePokeAbility = pokemonAbilityCls.createIndividual(pokemonAbilityID);
            Individual thePoke = pokemonCls.createIndividual(pokemonID);

            thePokeAbility.addLabel("Ability " + pokemonAbilityLabel, "en");
            thePokeAbility.addProperty(hasValue,    pokemonAbilityLabel);
            thePoke       .addLabel(pokemonLabel, "en");
            thePoke       .addProperty(uses,        thePokeAbility);
        }

        //all abilities in wikidata
        try { a = (JSONArray) parser.parse(new FileReader(all_abilities)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonAbilityID    = (String) pokemon.get("pokemonAbility");
            String pokemonAbilityLabel = (String) pokemon.get("pokemonAbilityLabel");

            if(JSON2RDF.isGenIVAbility(pokemonAbilityLabel) || JSON2RDF.isGenIIIAbility(pokemonAbilityLabel))
            {
                Individual thePokeAbility = pokemonAbilityCls.createIndividual(pokemonAbilityID);
                thePokeAbility.addLabel("Ability " + pokemonAbilityLabel, "en");
                thePokeAbility.addProperty(hasValue, pokemonAbilityLabel);
                thePokeAbility.addProperty(presentInWork, genIV);

                if(JSON2RDF.isGenIVAbility(pokemonAbilityLabel))
                    thePokeAbility.addProperty(firstAppearance, genIV);

            }
        }
    }
    static boolean isInGenIVMoveList(String moveName)
    {
        for(String move : genIV_moves_list)
        {
            if(move.equalsIgnoreCase(moveName))
                return true;
        }
        return false;
    }

    /*
        GEN V UNOVA
     */

    public static void convertPokemonGenV()
    {

        String generations [] = { base + "Pokemons Gen V.json", base + "Pokemons Gen IV.json", base + "Pokemons Gen III.json", base + "Pokemons Gen II.json", base + "Pokemons Gen I.json" };

        for(String pokeList : generations)
        {
        JSONParser parser = new JSONParser(); JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(pokeList)); } catch (Exception e)
        { System.out.println("OH NO . " + e.getMessage()); return;        }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID     = (String) pokemon.get("pokemon");
            String pokemonLabel  = (String) pokemon.get("pokemonLabel");
            String pokedexNumber = (String) pokemon.get("pokedexNumber");

            int dexno = Integer.parseInt(pokedexNumber);

            if(dexno > 649) continue;
            if(pokemonLabel.toLowerCase().contains("alolan"))  continue;

            pokedexNumber = String.format("%04d", dexno);

            Individual thePoke = theModel.createIndividual(pokemonID, pokemonCls);

            thePoke.addLabel(pokedexNumber + " " + pokemonLabel, "en");
            thePoke.addProperty(hasName, pokemonLabel);

            String evolFamilyStmt      = (String) pokemon.get("evolFamilyStmt");
            String evolFamilyStmtLabel = (String) pokemon.get("evolFamilyStmtLabel");

            if(evolFamilyStmtLabel != null && !evolFamilyStmtLabel.isEmpty() && evolFamilyStmt != null && !evolFamilyStmt.isEmpty())
            {
                Individual pokemonEvolutioanryFamily = pokemonEFamilyCls.createIndividual(evolFamilyStmt);
                pokemonEvolutioanryFamily.addLabel("EvolLine " + evolFamilyStmtLabel, "en");

                pokemonEvolutioanryFamily.addProperty(hasName, evolFamilyStmtLabel);
                thePoke.addProperty(partOf, pokemonEvolutioanryFamily);
                pokemonEvolutioanryFamily.addProperty(hasParts, thePoke);
            }

            String eggGroupStmt      = (String) pokemon.get("eggGroupStmt");
            String eggGroupStmtLabel = (String) pokemon.get("eggGroupStmtLabel");

            if(eggGroupStmtLabel != null && !eggGroupStmtLabel.isEmpty() && eggGroupStmt != null && !eggGroupStmt.isEmpty())
            {
                Individual pokemonEggGroup = pokemonEggGroupCls.createIndividual(eggGroupStmt);
                pokemonEggGroup.addLabel("Egg Group " + eggGroupStmtLabel, "en");

                pokemonEggGroup.addProperty(hasName, eggGroupStmtLabel);
                thePoke.addProperty(partOf, pokemonEggGroup);
                pokemonEggGroup.addProperty(hasParts, thePoke);
            }

            String genderRatioStmt      = (String) pokemon.get("genderRatioStmt");
            String genderRatioStmtLabel = (String) pokemon.get("grStmtLabel");

            if(genderRatioStmt != null && !genderRatioStmt.isEmpty() && genderRatioStmtLabel != null && !genderRatioStmtLabel.isEmpty())
            {
                Individual pokemonGenderRatio = pokemonGenderRatioCls.createIndividual(genderRatioStmt);
                pokemonGenderRatio.addLabel("Gender Ratio " + genderRatioStmtLabel.replace("gender ratio", "").trim(), "en");

                pokemonGenderRatio.addProperty(hasName, genderRatioStmtLabel);
                thePoke.addProperty(partOf, pokemonGenderRatio);
                pokemonGenderRatio.addProperty(hasParts, thePoke);
            }

            String typeLabel   = (String) pokemon.get("typeLabel");
            String type        = (String) pokemon.get("type");
            String colorLabel  = (String) pokemon.get("colorLabel");
            String weightLabel = (String) pokemon.get("weightLabel");
            String heightLabel = (String) pokemon.get("heightLabel");
            String shapeLabel  = (String) pokemon.get("shapeLabel");

            thePoke.addProperty(presentInWork,   genV);

            if(typeLabel != null && !typeLabel.isEmpty())
            {
                String typeString = typeLabel.replace("Pokémon", "").replace("-type", "").trim();

                Individual pokemonType = pokemonTypeCls.createIndividual(type);
                pokemonType.addLabel("Type "   + typeLabel, "en");
                pokemonType.addProperty(hasValue,   typeLabel);
                thePoke    .addProperty(instanceOf, pokemonType);
                pokemonType.addProperty(hasFacet,   typeString);
            }

            if(colorLabel !=null && !colorLabel.isEmpty())
                thePoke.addProperty(hasColor, colorLabel);

            if(weightLabel !=null && !weightLabel.isEmpty())
                thePoke.addProperty(hasWeight, weightLabel);

            if(shapeLabel !=null && !shapeLabel.isEmpty())
                thePoke.addProperty(hasShape, shapeLabel);

            if(heightLabel !=null && !heightLabel.isEmpty())
                thePoke.addProperty(hasHeight, heightLabel);

            if(dexno > 493)
                thePoke.addProperty(firstAppearance, genV);

        }}
    }

    public static void convertPokedexGenV()
    {
        String pokedexFile         = base + "all pokedex entries.json";
        String pokedexRegionName   = "Unova";

        OntClass newPokedex = theModel.createClass(wd + "Q18086667"); // unova dex
        newPokedex.addLabel(pokedexRegionName + " Pokédex", "en");
        newPokedex.addProperty(firstAppearance, genV);
        newPokedex.addSuperClass(pokedexCls);

        JSONParser parser = new JSONParser();  JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(pokedexFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID     = (String) pokemon.get("pokemon");
            String pokemonLabel  = (String) pokemon.get("pokemonLabel");

            String national_pokedex_no  = (String) pokemon.get("nationalDexNumber");
            String national_pokedexStmt = (String) pokemon.get("nationalStmt");

            String new_pokedex_no  = (String) pokemon.get("unovaDexNumber");
            String new_pokedexStmt = (String) pokemon.get("unovaStmt");

            int dexno = Integer.parseInt(national_pokedex_no);
            national_pokedex_no = String.format("%04d", dexno);

            if(dexno > 649) continue;
            if(pokemonLabel.toLowerCase().contains("alolan"))  continue;

            Individual nationalPokedexEntry = nationalPokedex.createIndividual(national_pokedexStmt);
            Individual thePoke = theModel.createIndividual(pokemonID, pokemonCls);

            nationalPokedexEntry   .addLabel("National Pokedex Entry  " + national_pokedex_no, "en");
            nationalPokedexEntry   .addProperty(hasNumber, national_pokedex_no);
            nationalPokedexEntry   .addProperty(partOf, nationalPokedex);
            nationalPokedex        .addProperty(hasParts, nationalPokedexEntry);
            thePoke                .addProperty(hasPokedexNumber, nationalPokedexEntry);
            thePoke                .addLabel(national_pokedex_no + " " + pokemonLabel, "en");
            thePoke                .addProperty(hasName, pokemonLabel);

            if(new_pokedex_no != null && !new_pokedex_no.isEmpty() && new_pokedexStmt != null && !new_pokedexStmt.isEmpty() )
            {
                int dexno2 = Integer.parseInt(new_pokedex_no);
                new_pokedex_no = String.format("%04d", dexno2);

                Individual newPokedexEntry    = newPokedex.createIndividual(new_pokedexStmt);

                newPokedexEntry   .addLabel(pokedexRegionName + " Pokedex Entry " + new_pokedex_no, "en");
                newPokedexEntry   .addProperty(hasNumber,        new_pokedex_no);
                newPokedexEntry   .addProperty(partOf,           newPokedex);
                newPokedex        .addProperty(hasParts,         newPokedexEntry);
                thePoke           .addProperty(hasPokedexNumber, newPokedexEntry);
            }
        }
    }

    public static void convertMovesGenV()
    {
        String movesFile       = base + "pokemonTypeMoveCategory.json";

        JSONParser parser = new JSONParser(); JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        OntClass pokemonMoveCls    = theModel.createClass(wd + "Q15141195"); // a pokemon move
        pokemonMoveCls.addLabel("Pokemon Move", "en");

        // check for move type
        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID            = (String) pokemon.get("pokeMove");
            String moveLabel         = (String) pokemon.get("pokeMoveLabel");
            String moveTypeStmt      = (String) pokemon.get("typeMoveStmt");
            String moveTypeStmtLabel = (String) pokemon.get("typeMoveStmtLabel");
            String typeString        = moveTypeStmtLabel.replace("Pokémon move", "").replace("-type", "").trim();

            if(!JSON2RDF.isInGenIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVMoveList(moveLabel)) continue;

            Individual moveTypeIndividual    = pokemonMoveCls.createIndividual(moveTypeStmt); //the move type
            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID); // the move

            moveTypeIndividual.addLabel("Move Type " + moveTypeStmtLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,    pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,      moveTypeStmtLabel);
            pokemonMove       .addLabel("Move "      + moveLabel, "en");
            pokemonMove       .addProperty(hasValue,      moveLabel);
            pokemonMove       .addProperty(instanceOf,    moveTypeIndividual);
            pokemonMove       .addProperty(presentInWork, genV);
            moveTypeIndividual.addProperty(hasFacet,      typeString);

            if(JSON2RDF.isInGenVMoveList(moveLabel))
                pokemonMove.addProperty(firstAppearance, genV);

        }

        // check for contest move type
        movesFile       = base + "pokemonContestMoveCategory.json";
        parser = new JSONParser();

        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID                   = (String) pokemon.get("pokeMove");
            String moveLabel                = (String) pokemon.get("pokeMoveLabel");
            String moveContestTypeStmt      = (String) pokemon.get("categoryMoveStmt");
            String moveContestTypeStmtLabel = (String) pokemon.get("categoryMoveStmtLabel");
            String typeString               = moveContestTypeStmtLabel.replace("move", "").trim();

            if(!JSON2RDF.isInGenIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            Individual pokemonMove         = pokemonMoveCls.createIndividual(moveID);

            moveTypeIndividual  .addLabel("Contest Move Type " + moveContestTypeStmtLabel, "en");
            moveTypeIndividual  .addProperty(instanceOf,            pokemonMoveCls);
            moveTypeIndividual  .addProperty(hasValue,              moveContestTypeStmtLabel);
            moveTypeIndividual  .addProperty(hasFacet,              typeString);

            pokemonMove         .addLabel("Move "      + moveLabel, "en");
            pokemonMove         .addProperty(hasValue,      moveLabel);
            pokemonMove         .addProperty(instanceOf,    moveTypeIndividual);
            pokemonMove         .addProperty(presentInWork, genV);

            if(JSON2RDF.isInGenVMoveList(moveLabel))
                pokemonMove.addProperty(firstAppearance, genV);

        }

        // check for status/physical/special split

        movesFile   = base + "pokemonDamageMoveCategory.json";
        parser      = new JSONParser();

        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID                   = (String) pokemon.get("pokeMove");
            String moveLabel                = (String) pokemon.get("pokeMoveLabel");
            String moveContestTypeStmt      = (String) pokemon.get("damageCategoryMoveStmt");
            String moveContestTypeStmtLabel = (String) pokemon.get("damageCategoryMoveStmtLabel");
            String typeString               = moveContestTypeStmtLabel.replace("move", "").trim();

            if(!JSON2RDF.isInGenIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual moveTypeIndividual   = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            Individual pokemonMove          = pokemonMoveCls.createIndividual(moveID);

            moveTypeIndividual  .addLabel("Damage Category Type " + moveContestTypeStmtLabel, "en");
            moveTypeIndividual  .addProperty(instanceOf,    pokemonMoveCls);
            moveTypeIndividual  .addProperty(hasValue,      moveContestTypeStmtLabel);
            moveTypeIndividual  .addProperty(hasFacet,      typeString);

            pokemonMove         .addLabel("Move " +      moveLabel, "en");
            pokemonMove         .addProperty(hasValue,      moveLabel);
            pokemonMove         .addProperty(instanceOf,    moveTypeIndividual);
            pokemonMove         .addProperty(presentInWork, genV);

            if(JSON2RDF.isInGenVMoveList(moveLabel))
                pokemonMove.addProperty(firstAppearance, genV);

        }

    }

    public static void convertAbilitiesGenV()
    {
        //all abilities in wikidata

        String all_abilities    = base + "all pokemon abilities.json";
        JSONParser parser       = new JSONParser(); JSONArray a;

        try { a = (JSONArray) parser.parse(new FileReader(all_abilities)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonAbilityID    = (String) pokemon.get("pokemonAbility");
            String pokemonAbilityLabel = (String) pokemon.get("pokemonAbilityLabel");

            if(JSON2RDF.isGenVAbility(pokemonAbilityLabel) || JSON2RDF.isGenIIIAbility(pokemonAbilityLabel))
            {
                Individual thePokeAbility = pokemonAbilityCls.createIndividual(pokemonAbilityID);

                thePokeAbility.addLabel("Ability " +     pokemonAbilityLabel, "en");
                thePokeAbility.addProperty(hasValue,        pokemonAbilityLabel);
                thePokeAbility.addProperty(presentInWork,   genV);

                if(JSON2RDF.isGenVAbility(pokemonAbilityLabel))
                    thePokeAbility.addProperty(firstAppearance, genV);
            }
        }
    }
    static boolean isInGenVMoveList(String moveName)
    {
        for(String move : genV_moves_list)
            if(move.equalsIgnoreCase(moveName))
                return true;
        return false;
    }

    static boolean isGenVAbility(String moveName)
    {
        for(String move : genV_abilities_list)
            if(move.equalsIgnoreCase(moveName))
                return true;
        return false;
    }


    /*
     GEN VI KALOS
     */

    public static void convertPokemonGenVI()
    {

        String generations [] = { base + "Pokemons Gen VI.json", base + "Pokemons Gen V.json", base + "Pokemons Gen IV.json", base + "Pokemons Gen III.json", base + "Pokemons Gen II.json", base + "Pokemons Gen I.json" };

        for(String pokeList : generations)
        {
        JSONParser parser = new JSONParser(); JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(pokeList)); } catch (Exception e)
        { System.out.println("OH NO . " + e.getMessage()); return;        }


        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID     = (String) pokemon.get("pokemon");
            String pokemonLabel  = (String) pokemon.get("pokemonLabel");
            String pokedexNumber = (String) pokemon.get("pokedexNumber");

            int dexno = Integer.parseInt(pokedexNumber);

            if(dexno > 721) continue;
            if(pokemonLabel.toLowerCase().contains("alolan"))  continue;

            pokedexNumber = String.format("%04d", dexno);

            Individual thePoke = theModel.createIndividual(pokemonID, pokemonCls);

            thePoke.addLabel(pokedexNumber + " " + pokemonLabel, "en");
            thePoke.addProperty(hasName,         pokemonLabel);
            thePoke.addProperty(presentInWork,   genVI);

            String megaEvolStoneStmt      = (String) pokemon.get("megaEvolStone");
            String megaEvolStoneStmtLabel = (String) pokemon.get("megaEvolStoneLabel");
            String evolFamilyStmt         = (String) pokemon.get("evolFamilyStmt");
            String evolFamilyStmtLabel    = (String) pokemon.get("evolFamilyStmtLabel");

            if(megaEvolStoneStmt != null && !megaEvolStoneStmt.isEmpty()
                    && megaEvolStoneStmtLabel != null && !megaEvolStoneStmtLabel.isEmpty())
            {
                Individual megaStoneUsed = megaStoneCls.createIndividual(megaEvolStoneStmt);

                megaStoneUsed   .addLabel("Item " +  megaEvolStoneStmtLabel , "en");
                megaStoneUsed   .addProperty(hasValue, megaEvolStoneStmtLabel);
                thePoke         .addProperty(uses,     megaStoneUsed);
            }

            if(evolFamilyStmtLabel != null && !evolFamilyStmtLabel.isEmpty()
                    && evolFamilyStmt != null && !evolFamilyStmt.isEmpty())
            {
                Individual pokemonEvolutioanryFamily = pokemonEFamilyCls.createIndividual(evolFamilyStmt);

                pokemonEvolutioanryFamily   .addLabel("EvolLine " + evolFamilyStmtLabel, "en");
                pokemonEvolutioanryFamily   .addProperty(hasName,   evolFamilyStmtLabel);
                thePoke                     .addProperty(partOf,    pokemonEvolutioanryFamily);
                pokemonEvolutioanryFamily   .addProperty(hasParts,  thePoke);
            }

            String eggGroupStmt      = (String) pokemon.get("eggGroupStmt");
            String eggGroupStmtLabel = (String) pokemon.get("eggGroupStmtLabel");

            if(eggGroupStmtLabel != null && !eggGroupStmtLabel.isEmpty() && eggGroupStmt != null && !eggGroupStmt.isEmpty())
            {
                Individual pokemonEggGroup = pokemonEggGroupCls.createIndividual(eggGroupStmt);

                pokemonEggGroup.addLabel("Egg Group " + eggGroupStmtLabel, "en");
                pokemonEggGroup.addProperty(hasName,    eggGroupStmtLabel);
                thePoke         .addProperty(partOf,    pokemonEggGroup);
                pokemonEggGroup.addProperty(hasParts,   thePoke);
            }

            String genderRatioStmt      = (String) pokemon.get("genderRatioStmt");
            String genderRatioStmtLabel = (String) pokemon.get("grStmtLabel");

            if(genderRatioStmt != null && !genderRatioStmt.isEmpty() && genderRatioStmtLabel != null && !genderRatioStmtLabel.isEmpty())
            {
                Individual pokemonGenderRatio = pokemonGenderRatioCls.createIndividual(genderRatioStmt);

                pokemonGenderRatio.addLabel("Gender Ratio " + genderRatioStmtLabel.replace("gender ratio", "").trim(), "en");
                pokemonGenderRatio.addProperty(hasName,     genderRatioStmtLabel);
                thePoke           .addProperty(partOf,      pokemonGenderRatio);
                pokemonGenderRatio.addProperty(hasParts,    thePoke);
            }

            String typeLabel   = (String) pokemon.get("typeLabel");
            String type        = (String) pokemon.get("type");
            String colorLabel  = (String) pokemon.get("colorLabel");
            String weightLabel = (String) pokemon.get("weightLabel");
            String heightLabel = (String) pokemon.get("heightLabel");
            String shapeLabel  = (String) pokemon.get("shapeLabel");

            if(typeLabel != null && !typeLabel.isEmpty())
            {
                String typeString = typeLabel.replace("Pokémon", "").replace("-type", "").trim();

                Individual pokemonType = pokemonTypeCls.createIndividual(type);

                pokemonType .addLabel("Type " +      typeLabel, "en");
                pokemonType .addProperty(hasValue,      typeLabel);
                thePoke     .addProperty(instanceOf,    pokemonType);
                pokemonType .addProperty(hasFacet,      typeString);
            }

            if(colorLabel !=null && !colorLabel.isEmpty())
                thePoke.addProperty(hasColor, colorLabel);

            if(weightLabel !=null && !weightLabel.isEmpty())
                thePoke.addProperty(hasWeight, weightLabel);

            if(shapeLabel !=null && !shapeLabel.isEmpty())
                thePoke.addProperty(hasShape, shapeLabel);

            if(heightLabel !=null && !heightLabel.isEmpty())
                thePoke.addProperty(hasHeight, heightLabel);

            if(dexno > 650)
                thePoke.addProperty(firstAppearance, genVI);

        }}
    }

    public static void convertPokedexGenVI()
    {
        String pokedexFile         = base + "all pokedex entries.json";
        String pokedexRegionName   = "Kalos";

        OntClass newPokedex = theModel.createClass(wd + "Q55521408"); // kalos dex
        newPokedex.addLabel(pokedexRegionName + " Pokédex", "en");
        newPokedex.addProperty(firstAppearance, genVI);
        newPokedex.addSuperClass(pokedexCls);

        JSONParser parser = new JSONParser();  JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(pokedexFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID     = (String) pokemon.get("pokemon");
            String pokemonLabel  = (String) pokemon.get("pokemonLabel");

            String national_pokedex_no  = (String) pokemon.get("nationalDexNumber");
            String national_pokedexStmt = (String) pokemon.get("nationalStmt");

            String new_pokedex_no  = (String) pokemon.get("kalosDexNumber");
            String new_pokedexStmt = (String) pokemon.get("kalosStmt");

            int dexno = Integer.parseInt(national_pokedex_no);
            national_pokedex_no = String.format("%04d", dexno);

            if(dexno > 721) continue;
            if(pokemonLabel.toLowerCase().contains("alolan"))  continue;

            Individual nationalPokedexEntry = nationalPokedex.createIndividual(national_pokedexStmt);

            nationalPokedexEntry.addLabel("National Pokedex Entry  " + national_pokedex_no, "en");
            nationalPokedexEntry.addProperty(hasNumber, national_pokedex_no);

            Individual thePoke = theModel.createIndividual(pokemonID, pokemonCls);
            thePoke.addProperty(hasPokedexNumber, nationalPokedexEntry);

            thePoke.addLabel(national_pokedex_no + " " + pokemonLabel, "en");
            thePoke.addProperty(hasName, pokemonLabel);

            nationalPokedexEntry   .addProperty(partOf, nationalPokedex);
            nationalPokedex        .addProperty(hasParts, nationalPokedexEntry);

            if(new_pokedex_no != null && !new_pokedex_no.isEmpty() && new_pokedexStmt != null && !new_pokedexStmt.isEmpty() )
            {
                int dexno2     = Integer.parseInt(new_pokedex_no);
                new_pokedex_no = String.format("%04d", dexno2);

                Individual newPokedexEntry    = newPokedex.createIndividual(new_pokedexStmt);

                newPokedexEntry.addLabel(pokedexRegionName + " Pokedex Entry " + new_pokedex_no, "en");
                newPokedexEntry   .addProperty(hasNumber,        new_pokedex_no);
                newPokedexEntry   .addProperty(partOf,           newPokedex);
                newPokedex        .addProperty(hasParts,         newPokedexEntry);
                thePoke           .addProperty(hasPokedexNumber, newPokedexEntry);
            }
        }
    }

    public static void convertMovesGenVI()
    {
        String movesFile       = base + "pokemonTypeMoveCategory.json";

        JSONParser parser = new JSONParser(); JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        OntClass pokemonMoveCls    = theModel.createClass(wd + "Q15141195"); // a pokemon move
        pokemonMoveCls.addLabel("Pokemon Move", "en");

        // check for move type
        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID            = (String) pokemon.get("pokeMove");
            String moveLabel         = (String) pokemon.get("pokeMoveLabel");
            String moveTypeStmt      = (String) pokemon.get("typeMoveStmt");
            String moveTypeStmtLabel = (String) pokemon.get("typeMoveStmtLabel");
            String typeString        = moveTypeStmtLabel.replace("Pokémon move", "").replace("-type", "").trim();

            if(!JSON2RDF.isInGenIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVIMoveList(moveLabel)) continue;

            Individual moveTypeIndividual   = pokemonMoveCls.createIndividual(moveTypeStmt); //the move type
            Individual pokemonMove          = pokemonMoveCls.createIndividual(moveID);

            moveTypeIndividual  .addLabel("Move Type " + moveTypeStmtLabel, "en");
            moveTypeIndividual  .addProperty(instanceOf,    pokemonMoveCls);
            moveTypeIndividual  .addProperty(hasValue,      moveTypeStmtLabel);
            pokemonMove         .addLabel("Move " +      moveLabel, "en");
            pokemonMove         .addProperty(hasValue,      moveLabel);
            pokemonMove         .addProperty(instanceOf,    moveTypeIndividual);
            pokemonMove         .addProperty(presentInWork, genVI);
            moveTypeIndividual  .addProperty(hasFacet,      typeString);

            if(JSON2RDF.isInGenVIMoveList(moveLabel))
                pokemonMove.addProperty(firstAppearance, genVI);
        }

        // check for contest move type
        movesFile       = base + "pokemonContestMoveCategory.json";
        parser = new JSONParser();

        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID                   = (String) pokemon.get("pokeMove");
            String moveLabel                = (String) pokemon.get("pokeMoveLabel");
            String moveContestTypeStmt      = (String) pokemon.get("categoryMoveStmt");
            String moveContestTypeStmtLabel = (String) pokemon.get("categoryMoveStmtLabel");
            String typeString               = moveContestTypeStmtLabel.replace("move", "").trim();

            if(!JSON2RDF.isInGenIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVIMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            Individual pokemonMove         = pokemonMoveCls.createIndividual(moveID);

            moveTypeIndividual  .addLabel("Contest Move Type " + moveContestTypeStmtLabel, "en");
            moveTypeIndividual  .addProperty(instanceOf,    pokemonMoveCls);
            moveTypeIndividual  .addProperty(hasValue,      moveContestTypeStmtLabel);
            pokemonMove         .addLabel("Move " +      moveLabel, "en");
            pokemonMove         .addProperty(hasValue,      moveLabel);
            pokemonMove         .addProperty(instanceOf,    moveTypeIndividual);
            pokemonMove         .addProperty(presentInWork, genVI);
            moveTypeIndividual  .addProperty(hasFacet,      typeString);

            if(JSON2RDF.isInGenVIMoveList(moveLabel))
                pokemonMove.addProperty(firstAppearance, genVI);

        }

        // check for status/physical/special split

        movesFile       = base + "pokemonDamageMoveCategory.json";
        parser = new JSONParser();

        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID                   = (String) pokemon.get("pokeMove");
            String moveLabel                = (String) pokemon.get("pokeMoveLabel");
            String moveContestTypeStmt      = (String) pokemon.get("damageCategoryMoveStmt");
            String moveContestTypeStmtLabel = (String) pokemon.get("damageCategoryMoveStmtLabel");
            String typeString               = moveContestTypeStmtLabel.replace("move", "").trim();

            if(!JSON2RDF.isInGenIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVIMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual moveTypeIndividual   = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            Individual pokemonMove          = pokemonMoveCls.createIndividual(moveID);

            moveTypeIndividual  .addLabel("Damage Category Type " + moveContestTypeStmtLabel, "en");
            moveTypeIndividual  .addProperty(instanceOf,    pokemonMoveCls);
            moveTypeIndividual  .addProperty(hasValue,      moveContestTypeStmtLabel);
            pokemonMove         .addLabel("Move " +      moveLabel, "en");
            pokemonMove         .addProperty(hasValue,      moveLabel);
            pokemonMove         .addProperty(instanceOf,    moveTypeIndividual);
            pokemonMove         .addProperty(presentInWork, genVI);
            moveTypeIndividual  .addProperty(hasFacet,      typeString);

            if(JSON2RDF.isInGenVIMoveList(moveLabel))
                pokemonMove.addProperty(firstAppearance, genVI);

        }
    }

    public static void convertAbilitiesGenVI()
    {

        String pokemonAbilitiesFile  = base + "Pokemon Abilities.json";
        String all_abilities         = base + "all pokemon abilities.json";

        JSONParser parser = new JSONParser(); JSONArray a;

        try { a = (JSONArray) parser.parse(new FileReader(pokemonAbilitiesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        // abilities with pokemons associated in wikidata

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID           = (String) pokemon.get("pokemon");
            String pokemonLabel        = (String) pokemon.get("pokemonLabel");
            String pokemonAbilityID    = (String) pokemon.get("pokemonAbility");
            String pokemonAbilityLabel = (String) pokemon.get("pokemonAbilityLabel");

            if(!JSON2RDF.isGenVIAbility(pokemonAbilityLabel)) continue;

            Individual thePokeAbility = pokemonAbilityCls.createIndividual(pokemonAbilityID);
            thePokeAbility.addLabel("Ability " + pokemonAbilityLabel, "en");
            thePokeAbility.addProperty(hasValue, pokemonAbilityLabel);

            Individual thePoke = pokemonCls.createIndividual(pokemonID);
            thePoke.addLabel(pokemonLabel, "en");
            thePoke.addProperty(uses, thePokeAbility);
        }

        //all abilities in wikidata
        try { a = (JSONArray) parser.parse(new FileReader(all_abilities)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonAbilityID    = (String) pokemon.get("pokemonAbility");
            String pokemonAbilityLabel = (String) pokemon.get("pokemonAbilityLabel");

            Individual thePokeAbility = pokemonAbilityCls.createIndividual(pokemonAbilityID);

            thePokeAbility.addLabel("Ability " + pokemonAbilityLabel, "en");
            thePokeAbility.addProperty(hasValue,        pokemonAbilityLabel);
            thePokeAbility.addProperty(presentInWork,   genVI);

            if(JSON2RDF.isGenVIAbility(pokemonAbilityLabel))
                thePokeAbility.addProperty(firstAppearance, genVI);

        }
    }
    static boolean isInGenVIMoveList(String moveName)
    {
        for(String move : genVI_moves_list)
            if(move.equalsIgnoreCase(moveName))
                return true;
        return false;
    }

    static boolean isGenVIAbility(String moveName)
    {
        for(String move : genVI_abilities_list)
            if(move.equalsIgnoreCase(moveName))
                return true;
        return false;
    }

       /*
     GEN VII ALOLA
     */

    public static void convertPokemonGenVII()
    {

        String generations [] = { base + "Pokemons Gen VII.json", base + "Pokemons Gen VI.json", base + "Pokemons Gen V.json", base + "Pokemons Gen IV.json", base + "Pokemons Gen III.json", base + "Pokemons Gen II.json", base + "Pokemons Gen I.json" };

        for(String pokeList : generations)
        {

            JSONParser parser = new JSONParser(); JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(pokeList)); } catch (Exception e)
        { System.out.println("OH NO . " + e.getMessage()); return;        }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID = (String) pokemon.get("pokemon");

            String pokemonLabel  = (String) pokemon.get("pokemonLabel");
            String pokedexNumber = (String) pokemon.get("pokedexNumber");

            int dexno = Integer.parseInt(pokedexNumber);

            if(dexno > 809) continue;

            pokedexNumber = String.format("%04d", dexno);

            Individual thePoke = theModel.createIndividual(pokemonID, pokemonCls);

            thePoke.addProperty(presentInWork, genVII);
            thePoke.addLabel(pokedexNumber + " " + pokemonLabel, "en");
            thePoke.addProperty(hasName, pokemonLabel);

            String megaEvolStoneStmt      = (String) pokemon.get("megaEvolStone");
            String megaEvolStoneStmtLabel = (String) pokemon.get("megaEvolStoneLabel");

            if(megaEvolStoneStmt != null && !megaEvolStoneStmt.isEmpty() && megaEvolStoneStmtLabel != null && !megaEvolStoneStmtLabel.isEmpty())
            {
                Individual megaStoneUsed = megaStoneCls.createIndividual(megaEvolStoneStmt);
                megaStoneUsed.addLabel("Item " + megaEvolStoneStmtLabel , "en");
                megaStoneUsed.addProperty(hasValue, megaEvolStoneStmtLabel);
                thePoke.addProperty(uses, megaStoneUsed);
            }

            String evolFamilyStmt      = (String) pokemon.get("evolFamilyStmt");
            String evolFamilyStmtLabel = (String) pokemon.get("evolFamilyStmtLabel");

            if(evolFamilyStmtLabel != null && !evolFamilyStmtLabel.isEmpty() && evolFamilyStmt != null && !evolFamilyStmt.isEmpty())
            {
                Individual pokemonEvolutioanryFamily = pokemonEFamilyCls.createIndividual(evolFamilyStmt);
                pokemonEvolutioanryFamily.addLabel("EvolLine " + evolFamilyStmtLabel, "en");

                pokemonEvolutioanryFamily.addProperty(hasName, evolFamilyStmtLabel);
                thePoke.addProperty(partOf, pokemonEvolutioanryFamily);
                pokemonEvolutioanryFamily.addProperty(hasParts, thePoke);
            }

            String eggGroupStmt      = (String) pokemon.get("eggGroupStmt");
            String eggGroupStmtLabel = (String) pokemon.get("eggGroupStmtLabel");

            if(eggGroupStmtLabel != null && !eggGroupStmtLabel.isEmpty() && eggGroupStmt != null && !eggGroupStmt.isEmpty())
            {
                Individual pokemonEggGroup = pokemonEggGroupCls.createIndividual(eggGroupStmt);

                pokemonEggGroup .addLabel("Egg Group " + eggGroupStmtLabel, "en");
                pokemonEggGroup .addProperty(hasName,   eggGroupStmtLabel);
                thePoke         .addProperty(partOf,    pokemonEggGroup);
                pokemonEggGroup .addProperty(hasParts,  thePoke);
            }

            String genderRatioStmt      = (String) pokemon.get("genderRatioStmt");
            String genderRatioStmtLabel = (String) pokemon.get("grStmtLabel");

            if(genderRatioStmt != null && !genderRatioStmt.isEmpty() && genderRatioStmtLabel != null && !genderRatioStmtLabel.isEmpty())
            {
                Individual pokemonGenderRatio = pokemonGenderRatioCls.createIndividual(genderRatioStmt);

                pokemonGenderRatio  .addLabel("Gender Ratio " + genderRatioStmtLabel.replace("gender ratio", "").trim(), "en");
                pokemonGenderRatio  .addProperty(hasName,   genderRatioStmtLabel);
                thePoke             .addProperty(partOf,    pokemonGenderRatio);
                pokemonGenderRatio  .addProperty(hasParts,  thePoke);
            }

            String typeLabel   = (String) pokemon.get("typeLabel");
            String type        = (String) pokemon.get("type");
            String colorLabel  = (String) pokemon.get("colorLabel");
            String weightLabel = (String) pokemon.get("weightLabel");
            String heightLabel = (String) pokemon.get("heightLabel");
            String shapeLabel  = (String) pokemon.get("shapeLabel");

            if(typeLabel != null && !typeLabel.isEmpty())
            {
                String typeString = typeLabel.replace("Pokémon", "").replace("-type", "").trim();
                Individual pokemonType = pokemonTypeCls.createIndividual(type);

                pokemonType .addLabel("Type " + typeLabel, "en");
                pokemonType .addProperty(hasValue,      typeLabel);
                thePoke     .addProperty(instanceOf,    pokemonType);
                pokemonType .addProperty(hasFacet,      typeString);
            }

            if(colorLabel !=null && !colorLabel.isEmpty())
                thePoke.addProperty(hasColor, colorLabel);

            if(weightLabel !=null && !weightLabel.isEmpty())
                thePoke.addProperty(hasWeight, weightLabel);

            if(shapeLabel !=null && !shapeLabel.isEmpty())
                thePoke.addProperty(hasShape, shapeLabel);

            if(heightLabel !=null && !heightLabel.isEmpty())
                thePoke.addProperty(hasHeight, heightLabel);

            if(dexno > 721)
                thePoke.addProperty(firstAppearance, genVII);
        }}
    }

    public static void convertPokedexGenVII()
    {
        String pokedexFile         = base + "all pokedex entries.json";
        String pokedexRegionName   = "Alola";

        OntClass newPokedex = theModel.createClass(wd + "Q25336664"); // alola dex
        newPokedex.addLabel(pokedexRegionName + " Pokédex", "en");
        newPokedex.addProperty(firstAppearance, genVII);

        newPokedex.addSuperClass(pokedexCls);

        JSONParser parser = new JSONParser();  JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(pokedexFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID     = (String) pokemon.get("pokemon");
            String pokemonLabel  = (String) pokemon.get("pokemonLabel");

            String national_pokedex_no  = (String) pokemon.get("nationalDexNumber");
            String national_pokedexStmt = (String) pokemon.get("nationalStmt");

            String new_pokedex_no  = (String) pokemon.get("kalosDexNumber");
            String new_pokedexStmt = (String) pokemon.get("kalosStmt");

            int dexno = Integer.parseInt(national_pokedex_no);
            national_pokedex_no = String.format("%04d", dexno);

            if(dexno > 809) continue;

            Individual nationalPokedexEntry = nationalPokedex.createIndividual(national_pokedexStmt);

            nationalPokedexEntry.addLabel("National Pokedex Entry  " + national_pokedex_no, "en");
            nationalPokedexEntry.addProperty(hasNumber, national_pokedex_no);

            Individual thePoke = theModel.createIndividual(pokemonID, pokemonCls);
            thePoke.addProperty(hasPokedexNumber, nationalPokedexEntry);

            thePoke.addLabel(national_pokedex_no + " " + pokemonLabel, "en");
            thePoke.addProperty(hasName, pokemonLabel);

            nationalPokedexEntry   .addProperty(partOf, nationalPokedex);
            nationalPokedex        .addProperty(hasParts, nationalPokedexEntry);

            if(new_pokedex_no != null && !new_pokedex_no.isEmpty() && new_pokedexStmt != null && !new_pokedexStmt.isEmpty() )
            {
                int dexno2     = Integer.parseInt(new_pokedex_no);
                new_pokedex_no = String.format("%04d", dexno2);

                Individual newPokedexEntry    = newPokedex.createIndividual(new_pokedexStmt);
                newPokedexEntry   .addLabel(pokedexRegionName + " Pokedex Entry " + new_pokedex_no, "en");
                newPokedexEntry   .addProperty(hasNumber,        new_pokedex_no);
                newPokedexEntry   .addProperty(partOf,           newPokedex);
                newPokedex        .addProperty(hasParts,         newPokedexEntry);
                thePoke           .addProperty(hasPokedexNumber, newPokedexEntry);
            }
        }
    }

    public static void convertMovesGenVII()
    {
        String movesFile       = base + "pokemonTypeMoveCategory.json";

        JSONParser parser = new JSONParser(); JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        // check for move type
        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID            = (String) pokemon.get("pokeMove");
            String moveLabel         = (String) pokemon.get("pokeMoveLabel");
            String moveTypeStmt      = (String) pokemon.get("typeMoveStmt");
            String moveTypeStmtLabel = (String) pokemon.get("typeMoveStmtLabel");
            String typeString        = moveTypeStmtLabel.replace("Pokémon move", "").replace("-type", "").trim();

            if(!JSON2RDF.isInGenIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVIMoveList(moveLabel)
                    && !JSON2RDF.isInGenVIIMoveList(moveLabel)) continue;

            Individual moveTypeIndividual    = pokemonMoveCls.createIndividual(moveTypeStmt); //the move type
            moveTypeIndividual.addLabel("Move Type " + moveTypeStmtLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,    moveTypeStmtLabel);
            moveTypeIndividual.addProperty(hasFacet,    typeString);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel("Move " + moveLabel, "en");
            pokemonMove.addProperty(hasValue,       moveLabel);
            pokemonMove.addProperty(instanceOf,     moveTypeIndividual);
            pokemonMove.addProperty(presentInWork, genVII);

            if(JSON2RDF.isInGenVIIMoveList(moveLabel))
                pokemonMove.addProperty(firstAppearance, genVII);

        }

        // check for contest move type
        movesFile = base + "pokemonContestMoveCategory.json";
        parser    = new JSONParser();

        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID                   = (String) pokemon.get("pokeMove");
            String moveLabel                = (String) pokemon.get("pokeMoveLabel");
            String moveContestTypeStmt      = (String) pokemon.get("categoryMoveStmt");
            String moveContestTypeStmtLabel = (String) pokemon.get("categoryMoveStmtLabel");
            String typeString               = moveContestTypeStmtLabel.replace("move", "").trim();

            if(!JSON2RDF.isInGenIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVIMoveList(moveLabel)
                    && !JSON2RDF.isInGenVIIMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            moveTypeIndividual.addLabel("Contest Move Type " + moveContestTypeStmtLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,    moveContestTypeStmtLabel);
            moveTypeIndividual.addProperty(hasFacet,    typeString);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel("Move " + moveLabel, "en");
            pokemonMove.addProperty(hasValue,       moveLabel);
            pokemonMove.addProperty(instanceOf,     moveTypeIndividual);
            pokemonMove.addProperty(presentInWork, genVII);

            if(JSON2RDF.isInGenVIIMoveList(moveLabel))
                pokemonMove.addProperty(firstAppearance, genVII);
        }

        // check for status/physical/special split

        movesFile       = base + "pokemonDamageMoveCategory.json";
        parser = new JSONParser();

        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID                   = (String) pokemon.get("pokeMove");
            String moveLabel                = (String) pokemon.get("pokeMoveLabel");
            String moveContestTypeStmt      = (String) pokemon.get("damageCategoryMoveStmt");
            String moveContestTypeStmtLabel = (String) pokemon.get("damageCategoryMoveStmtLabel");
            String typeString               = moveContestTypeStmtLabel.replace("move", "").trim();

            if(!JSON2RDF.isInGenIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVIMoveList(moveLabel)
                    && !JSON2RDF.isInGenVIIMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            moveTypeIndividual.addLabel("Damage Category Type " + moveContestTypeStmtLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,    moveContestTypeStmtLabel);
            moveTypeIndividual.addProperty(hasFacet,    typeString);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel("Move " + moveLabel, "en");
            pokemonMove.addProperty(hasValue,       moveLabel);
            pokemonMove.addProperty(instanceOf,     moveTypeIndividual);
            pokemonMove.addProperty(presentInWork, genVII);

            if(JSON2RDF.isInGenVIIMoveList(moveLabel))
                pokemonMove.addProperty(firstAppearance, genVII);
        }
    }

    public static void convertAbilitiesGenVII()
    {

        String all_abilities       = base + "all pokemon abilities.json";

        JSONParser parser = new JSONParser(); JSONArray a;

        //all abilities in wikidata
        try { a = (JSONArray) parser.parse(new FileReader(all_abilities)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonAbilityID    = (String) pokemon.get("pokemonAbility");
            String pokemonAbilityLabel = (String) pokemon.get("pokemonAbilityLabel");

            Individual thePokeAbility = pokemonAbilityCls.createIndividual(pokemonAbilityID);
            thePokeAbility.addLabel("Ability " + pokemonAbilityLabel, "en");
            thePokeAbility.addProperty(hasValue,      pokemonAbilityLabel);
            thePokeAbility.addProperty(presentInWork, genVII);

            if(JSON2RDF.isGenVIIAbility(pokemonAbilityLabel))
                thePokeAbility.addProperty(firstAppearance, genVII);

        }
    }
    static boolean isInGenVIIMoveList(String moveName)
    {
        for(String move : genVII_moves_list)
            if(move.equalsIgnoreCase(moveName))
                return true;
        return false;
    }

    static boolean isGenVIIAbility(String moveName)
    {
        for(String move : genVII_abilities_list)
            if(move.equalsIgnoreCase(moveName))
                return true;
        return false;
    }


     /*
     GEN VIII GALAR & HISUI
     */
    public static void convertPokemonGenVIII()
    {


        String generations [] = { base + "Pokemons Gen VIII.json", base + "Pokemons Gen VII.json", base + "Pokemons Gen VI.json", base + "Pokemons Gen V.json", base + "Pokemons Gen IV.json", base + "Pokemons Gen III.json", base + "Pokemons Gen II.json", base + "Pokemons Gen I.json" };

        for(String pokeList : generations)
        {

            JSONParser parser = new JSONParser(); JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(pokeList)); } catch (Exception e)
        { System.out.println("OH NO . " + e.getMessage()); return;        }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID     = (String) pokemon.get("pokemon");
            String pokemonLabel  = (String) pokemon.get("pokemonLabel");
            String pokedexNumber = (String) pokemon.get("pokedexNumber");

            int dexno = Integer.parseInt(pokedexNumber);

            if(dexno > 905) continue;

            pokedexNumber = String.format("%04d", dexno);

            Individual thePoke = theModel.createIndividual(pokemonID, pokemonCls);

            thePoke.addLabel(pokedexNumber + " " + pokemonLabel, "en");
            thePoke.addProperty(hasName,        pokemonLabel);
            thePoke.addProperty(presentInWork,  genVIII);


            String evolFamilyStmt      = (String) pokemon.get("evolFamilyStmt");
            String evolFamilyStmtLabel = (String) pokemon.get("evolFamilyStmtLabel");

            if(evolFamilyStmtLabel != null && !evolFamilyStmtLabel.isEmpty() && evolFamilyStmt != null && !evolFamilyStmt.isEmpty())
            {
                Individual pokemonEvolutioanryFamily = pokemonEFamilyCls.createIndividual(evolFamilyStmt);
                pokemonEvolutioanryFamily.addLabel("EvolLine " + evolFamilyStmtLabel, "en");

                pokemonEvolutioanryFamily.addProperty(hasName,  evolFamilyStmtLabel);
                thePoke                  .addProperty(partOf,   pokemonEvolutioanryFamily);
                pokemonEvolutioanryFamily.addProperty(hasParts, thePoke);
            }

            String eggGroupStmt      = (String) pokemon.get("eggGroupStmt");
            String eggGroupStmtLabel = (String) pokemon.get("eggGroupStmtLabel");

            if(eggGroupStmtLabel != null && !eggGroupStmtLabel.isEmpty() && eggGroupStmt != null && !eggGroupStmt.isEmpty())
            {
                Individual pokemonEggGroup = pokemonEggGroupCls.createIndividual(eggGroupStmt);
                pokemonEggGroup .addLabel("Egg Group " + eggGroupStmtLabel, "en");
                pokemonEggGroup .addProperty(hasName,   eggGroupStmtLabel);
                thePoke         .addProperty(partOf,    pokemonEggGroup);
                pokemonEggGroup .addProperty(hasParts,  thePoke);
            }

            String genderRatioStmt      = (String) pokemon.get("genderRatioStmt");
            String genderRatioStmtLabel = (String) pokemon.get("grStmtLabel");

            if(genderRatioStmt != null && !genderRatioStmt.isEmpty() && genderRatioStmtLabel != null && !genderRatioStmtLabel.isEmpty())
            {
                Individual pokemonGenderRatio = pokemonGenderRatioCls.createIndividual(genderRatioStmt);
                pokemonGenderRatio  .addLabel("Gender Ratio " + genderRatioStmtLabel.replace("gender ratio", "").trim(), "en");
                pokemonGenderRatio  .addProperty(hasName,   genderRatioStmtLabel);
                thePoke             .addProperty(partOf,    pokemonGenderRatio);
                pokemonGenderRatio  .addProperty(hasParts,  thePoke);
            }

            String typeLabel   = (String) pokemon.get("typeLabel");
            String type        = (String) pokemon.get("type");
            String colorLabel  = (String) pokemon.get("colorLabel");
            String weightLabel = (String) pokemon.get("weightLabel");
            String heightLabel = (String) pokemon.get("heightLabel");
            String shapeLabel  = (String) pokemon.get("shapeLabel");

            if(typeLabel != null && !typeLabel.isEmpty())
            {
                String typeString       = typeLabel.replace("Pokémon", "").replace("-type", "").trim();
                Individual pokemonType  = pokemonTypeCls.createIndividual(type);
                pokemonType .addLabel("Type " + typeLabel, "en");
                pokemonType .addProperty(hasValue,      typeLabel);
                thePoke     .addProperty(instanceOf,    pokemonType);
                pokemonType .addProperty(hasFacet,      typeString);
            }

            if(colorLabel !=null && !colorLabel.isEmpty())
                thePoke.addProperty(hasColor, colorLabel);

            if(weightLabel !=null && !weightLabel.isEmpty())
                thePoke.addProperty(hasWeight, weightLabel);

            if(shapeLabel !=null && !shapeLabel.isEmpty())
                thePoke.addProperty(hasShape, shapeLabel);

            if(heightLabel !=null && !heightLabel.isEmpty())
                thePoke.addProperty(hasHeight, heightLabel);

            if(dexno > 810)
                thePoke.addProperty(firstAppearance, genVIII);

        }}
    }

    public static void convertPokedexGenVIII()
    {
        String pokedexFile         = base + "all pokedex entries.json";

        OntClass GalarPokedex = theModel.createClass(wd + "Q75387698"); // galar dex
        GalarPokedex.addLabel("Galar Pokédex", "en");
        GalarPokedex.addProperty(firstAppearance, genVIII);

        OntClass HisuiPokedex = theModel.createClass(wd + "Q111148855"); // hisui dex
        HisuiPokedex.addLabel("Hisui Pokédex", "en");
        HisuiPokedex.addProperty(firstAppearance, genVIII);

        HisuiPokedex.addSuperClass(pokedexCls);
        GalarPokedex.addSuperClass(pokedexCls);

        JSONParser parser = new JSONParser();  JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(pokedexFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID     = (String) pokemon.get("pokemon");
            String pokemonLabel  = (String) pokemon.get("pokemonLabel");

            String national_pokedex_no  = (String) pokemon.get("nationalDexNumber");
            String national_pokedexStmt = (String) pokemon.get("nationalStmt");

            String hisui_pokedex_no  = (String) pokemon.get("hisuiDexNumber");
            String hisui_pokedexStmt = (String) pokemon.get("hisuiStmt");

            String galar_pokedex_no  = (String) pokemon.get("galarDexNumber");
            String galar_pokedexStmt = (String) pokemon.get("galarStmt");

            int dexno = Integer.parseInt(national_pokedex_no);
            national_pokedex_no = String.format("%04d", dexno);

            if(dexno > 905) continue;

            Individual nationalPokedexEntry = nationalPokedex.createIndividual(national_pokedexStmt);
            Individual thePoke              = theModel.createIndividual(pokemonID, pokemonCls);

            nationalPokedexEntry.addLabel("National Pokedex Entry  " + national_pokedex_no, "en");
            nationalPokedexEntry.addProperty(hasNumber, national_pokedex_no);

            thePoke.addProperty(hasPokedexNumber, nationalPokedexEntry);
            thePoke.addLabel(national_pokedex_no + " " + pokemonLabel, "en");
            thePoke.addProperty(hasName, pokemonLabel);

            nationalPokedexEntry   .addProperty(partOf,     nationalPokedex);
            nationalPokedex        .addProperty(hasParts,   nationalPokedexEntry);

            if(hisui_pokedex_no != null && !hisui_pokedex_no.isEmpty() && hisui_pokedexStmt != null && !hisui_pokedexStmt.isEmpty() )
            {
                int dexno2 = Integer.parseInt(hisui_pokedex_no);
                hisui_pokedex_no = String.format("%04d", dexno2);

                Individual newPokedexEntry    = HisuiPokedex.createIndividual(hisui_pokedexStmt);
                newPokedexEntry     .addLabel( "Hisui Pokedex Entry " + hisui_pokedex_no, "en");
                newPokedexEntry     .addProperty(hasNumber,         hisui_pokedex_no);
                newPokedexEntry     .addProperty(partOf,            HisuiPokedex);
                HisuiPokedex        .addProperty(hasParts,          newPokedexEntry);
                thePoke             .addProperty(hasPokedexNumber,  newPokedexEntry);
            }

            if(galar_pokedex_no != null && !galar_pokedex_no.isEmpty() && galar_pokedexStmt != null && !galar_pokedexStmt.isEmpty() )
            {
                int dexno2 = Integer.parseInt(galar_pokedex_no);
                galar_pokedex_no = String.format("%04d", dexno2);

                Individual newPokedexEntry    = GalarPokedex.createIndividual(galar_pokedexStmt);
                newPokedexEntry     .addLabel("Galar Pokedex Entry " + galar_pokedex_no, "en");
                newPokedexEntry     .addProperty(hasNumber,         galar_pokedex_no);
                newPokedexEntry     .addProperty(partOf,            GalarPokedex);
                GalarPokedex        .addProperty(hasParts,          newPokedexEntry);
                thePoke             .addProperty(hasPokedexNumber,  newPokedexEntry);
            }

        }
    }

    public static void convertMovesGenVIII()
    {
        String movesFile       = base + "pokemonTypeMoveCategory.json";

        JSONParser parser = new JSONParser(); JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        // check for move type
        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID            = (String) pokemon.get("pokeMove");
            String moveLabel         = (String) pokemon.get("pokeMoveLabel");
            String moveTypeStmt      = (String) pokemon.get("typeMoveStmt");
            String moveTypeStmtLabel = (String) pokemon.get("typeMoveStmtLabel");

            boolean isGen = JSON2RDF.isInGenVIIIMoveList(moveLabel);

            if(!JSON2RDF.isInGenIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVIMoveList(moveLabel)
                    && !JSON2RDF.isInGenVIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenVIIIMoveList(moveLabel)) continue;

            String typeString = moveTypeStmtLabel.replace("Pokémon move", "").replace("-type", "").trim();


            Individual moveTypeIndividual    = pokemonMoveCls.createIndividual(moveTypeStmt); //the move type
            moveTypeIndividual.addLabel("Move Type " + moveTypeStmtLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,    moveTypeStmtLabel);
            moveTypeIndividual.addProperty(hasFacet,    typeString);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel("Move " + moveLabel, "en");
            pokemonMove.addProperty(hasValue,       moveLabel);
            pokemonMove.addProperty(instanceOf,     moveTypeIndividual);
            pokemonMove.addProperty(presentInWork,  genVIII);

            if(isGen)
                pokemonMove.addProperty(firstAppearance, genVIII);
        }

        // check for contest move type
        movesFile   = base + "pokemonContestMoveCategory.json";
        parser      = new JSONParser();

        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID                   = (String) pokemon.get("pokeMove");
            String moveLabel                = (String) pokemon.get("pokeMoveLabel");
            String moveContestTypeStmt      = (String) pokemon.get("categoryMoveStmt");
            String moveContestTypeStmtLabel = (String) pokemon.get("categoryMoveStmtLabel");

            boolean isGen = JSON2RDF.isInGenVIIIMoveList(moveLabel);

            if(!JSON2RDF.isInGenIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVIMoveList(moveLabel)
                    && !JSON2RDF.isInGenVIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenVIIIMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            String typeString = moveContestTypeStmtLabel.replace("move", "").trim();

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            moveTypeIndividual.addLabel("Contest Move Type " + moveContestTypeStmtLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,    moveContestTypeStmtLabel);
            moveTypeIndividual.addProperty(hasFacet,    typeString);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel("Move " + moveLabel, "en");
            pokemonMove.addProperty(hasValue,       moveLabel);
            pokemonMove.addProperty(instanceOf,     moveTypeIndividual);
            pokemonMove.addProperty(presentInWork,  genVIII);

            if(isGen)
                pokemonMove.addProperty(firstAppearance, genVIII);

        }

        // check for status/physical/special split

        movesFile       = base + "pokemonDamageMoveCategory.json";
        parser = new JSONParser();

        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID                   = (String) pokemon.get("pokeMove");
            String moveLabel                = (String) pokemon.get("pokeMoveLabel");
            String moveContestTypeStmt      = (String) pokemon.get("damageCategoryMoveStmt");
            String moveContestTypeStmtLabel = (String) pokemon.get("damageCategoryMoveStmtLabel");

            boolean isGen = JSON2RDF.isInGenVIIIMoveList(moveLabel);

            if(!JSON2RDF.isInGenIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVMoveList(moveLabel)
                    && !JSON2RDF.isInGenVIIMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            String typeString = moveContestTypeStmtLabel.replace("move", "").trim();

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            moveTypeIndividual.addLabel("Damage Category Type " + moveContestTypeStmtLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,    moveContestTypeStmtLabel);
            moveTypeIndividual.addProperty(hasFacet,    typeString);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel("Move " + moveLabel, "en");
            pokemonMove.addProperty(hasValue,   moveLabel);
            pokemonMove.addProperty(instanceOf, moveTypeIndividual);
            pokemonMove.addProperty(presentInWork, genVIII);

            if(isGen)
                pokemonMove.addProperty(firstAppearance, genVIII);
        }

    }

    public static void convertAbilitiesGenVIII()
    {
        String all_abilities         = base + "all pokemon abilities.json";

        JSONParser parser = new JSONParser();
        JSONArray a;

        //all abilities in wikidata
        try { a = (JSONArray) parser.parse(new FileReader(all_abilities)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonAbilityID    = (String) pokemon.get("pokemonAbility");
            String pokemonAbilityLabel = (String) pokemon.get("pokemonAbilityLabel");

            Individual thePokeAbility = pokemonAbilityCls.createIndividual(pokemonAbilityID);
            thePokeAbility.addLabel("Ability " +     pokemonAbilityLabel, "en");
            thePokeAbility.addProperty(hasValue,        pokemonAbilityLabel);
            thePokeAbility.addProperty(presentInWork,   genVIII);

            if(JSON2RDF.isGenVIIIAbility(pokemonAbilityLabel))
                thePokeAbility.addProperty(firstAppearance, genVIII);

        }
    }
    static boolean isInGenVIIIMoveList(String moveName)
    {
        for(String move : genVIII_moves_list)
            if(move.equalsIgnoreCase(moveName))
                return true;
        return false;
    }

    static boolean isGenVIIIAbility(String moveName)
    {
        for(String move : genVIII_abilities_list)
            if(move.equalsIgnoreCase(moveName))
                return true;
        return false;
    }

    /*
GEN VIII Paldea & Kitakami
*/
    public static void convertPokemonGenIX()
    {

        String generations [] = { base + "Pokemons Gen IX.json", base + "Pokemons Gen VIII.json", base + "Pokemons Gen VII.json", base + "Pokemons Gen VI.json", base + "Pokemons Gen V.json", base + "Pokemons Gen IV.json", base + "Pokemons Gen III.json", base + "Pokemons Gen II.json", base + "Pokemons Gen I.json" };

        for(String pokeList : generations)
        {

            JSONParser parser = new JSONParser(); JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(pokeList)); } catch (Exception e)
        { System.out.println("OH NO . " + e.getMessage()); return;        }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID     = (String) pokemon.get("pokemon");
            String pokemonLabel  = (String) pokemon.get("pokemonLabel");
            String pokedexNumber = (String) pokemon.get("pokedexNumber");

            int dexno = Integer.parseInt(pokedexNumber);

            pokedexNumber = String.format("%04d", dexno);

            Individual thePoke = theModel.createIndividual(pokemonID, pokemonCls);

            if(dexno > 906)
                thePoke.addProperty(firstAppearance, genIX);

            thePoke.addLabel(pokedexNumber + " " + pokemonLabel, "en");
            thePoke.addProperty(hasName,        pokemonLabel);
            thePoke.addProperty(presentInWork,  genIX);

            String evolFamilyStmt      = (String) pokemon.get("evolFamilyStmt");
            String evolFamilyStmtLabel = (String) pokemon.get("evolFamilyStmtLabel");

            if(evolFamilyStmtLabel != null && !evolFamilyStmtLabel.isEmpty() && evolFamilyStmt != null && !evolFamilyStmt.isEmpty())
            {
                Individual pokemonEvolutioanryFamily = pokemonEFamilyCls.createIndividual(evolFamilyStmt);
                pokemonEvolutioanryFamily.addLabel("EvolLine " + evolFamilyStmtLabel, "en");
                pokemonEvolutioanryFamily.addProperty(hasName,  evolFamilyStmtLabel);
                thePoke                  .addProperty(partOf,   pokemonEvolutioanryFamily);
                pokemonEvolutioanryFamily.addProperty(hasParts, thePoke);
            }

            String eggGroupStmt      = (String) pokemon.get("eggGroupStmt");
            String eggGroupStmtLabel = (String) pokemon.get("eggGroupStmtLabel");

            if(eggGroupStmtLabel != null && !eggGroupStmtLabel.isEmpty() && eggGroupStmt != null && !eggGroupStmt.isEmpty())
            {
                Individual pokemonEggGroup = pokemonEggGroupCls.createIndividual(eggGroupStmt);
                pokemonEggGroup .addLabel("Egg Group " + eggGroupStmtLabel, "en");
                pokemonEggGroup .addProperty(hasName,   eggGroupStmtLabel);
                thePoke         .addProperty(partOf,    pokemonEggGroup);
                pokemonEggGroup .addProperty(hasParts,  thePoke);
            }

            String genderRatioStmt      = (String) pokemon.get("genderRatioStmt");
            String genderRatioStmtLabel = (String) pokemon.get("grStmtLabel");

            if(genderRatioStmt != null && !genderRatioStmt.isEmpty() && genderRatioStmtLabel != null && !genderRatioStmtLabel.isEmpty())
            {
                Individual pokemonGenderRatio = pokemonGenderRatioCls.createIndividual(genderRatioStmt);
                pokemonGenderRatio  .addLabel("Gender Ratio " + genderRatioStmtLabel.replace("gender ratio", "").trim(), "en");
                pokemonGenderRatio  .addProperty(hasName,   genderRatioStmtLabel);
                thePoke             .addProperty(partOf,    pokemonGenderRatio);
                pokemonGenderRatio  .addProperty(hasParts,  thePoke);
            }

            String typeLabel   = (String) pokemon.get("typeLabel");
            String type        = (String) pokemon.get("type");
            String colorLabel  = (String) pokemon.get("colorLabel");
            String weightLabel = (String) pokemon.get("weightLabel");
            String heightLabel = (String) pokemon.get("heightLabel");
            String shapeLabel  = (String) pokemon.get("shapeLabel");

            if(typeLabel != null && !typeLabel.isEmpty())
            {
                String typeString = typeLabel.replace("Pokémon", "").replace("-type", "").trim();

                Individual pokemonType = pokemonTypeCls.createIndividual(type);
                pokemonType .addLabel("Type " + typeLabel, "en");
                pokemonType .addProperty(hasValue,      typeLabel);
                thePoke     .addProperty(instanceOf,    pokemonType);
                pokemonType .addProperty(hasFacet,      typeString);
            }

            if(colorLabel !=null && !colorLabel.isEmpty())
                thePoke.addProperty(hasColor, colorLabel);

            if(weightLabel !=null && !weightLabel.isEmpty())
                thePoke.addProperty(hasWeight, weightLabel);

            if(shapeLabel !=null && !shapeLabel.isEmpty())
                thePoke.addProperty(hasShape, shapeLabel);

            if(heightLabel !=null && !heightLabel.isEmpty())
                thePoke.addProperty(hasHeight, heightLabel);

        }}
    }

    public static void convertPokedexGenIX()
    {
        String pokedexFile         = base + "all pokedex entries.json";

        OntClass PaldeaPokedex = theModel.createClass(wd + "Q116698491"); // paldea dex
        PaldeaPokedex.addLabel("Paldea Pokédex", "en");
        PaldeaPokedex.addProperty(firstAppearance, genIX);

        OntClass KitakamiPokedex = theModel.createClass(wd + "Q122674304"); // kitakami dex
        KitakamiPokedex.addLabel("Kitakami Pokédex", "en");
        KitakamiPokedex.addProperty(firstAppearance, genIX);

        KitakamiPokedex.addSuperClass(pokedexCls);
        PaldeaPokedex.addSuperClass(pokedexCls);

        JSONParser parser = new JSONParser();  JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(pokedexFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID     = (String) pokemon.get("pokemon");
            String pokemonLabel  = (String) pokemon.get("pokemonLabel");

            String national_pokedex_no  = (String) pokemon.get("nationalDexNumber");
            String national_pokedexStmt = (String) pokemon.get("nationalStmt");

            String kitakami_pokedex_no  = (String) pokemon.get("kitakamiDexNumber");
            String kitakami_pokedexStmt = (String) pokemon.get("kitakamiStmt");

            String paldea_pokedex_no  = (String) pokemon.get("paldeaDexNumber");
            String paldea_pokedexStmt = (String) pokemon.get("paldeaStmt");

            int dexno = Integer.parseInt(national_pokedex_no);
            national_pokedex_no = String.format("%04d", dexno);

            if(dexno > 905) continue;

            Individual nationalPokedexEntry = nationalPokedex.createIndividual(national_pokedexStmt);
            Individual thePoke = theModel.createIndividual(pokemonID, pokemonCls);

            nationalPokedexEntry    .addLabel("National Pokedex Entry  " + national_pokedex_no, "en");
            nationalPokedexEntry    .addProperty(hasNumber, national_pokedex_no);
            nationalPokedexEntry    .addProperty(partOf,    nationalPokedex);
            nationalPokedex         .addProperty(hasParts,  nationalPokedexEntry);

            thePoke.addProperty(hasName,            pokemonLabel);
            thePoke.addProperty(hasPokedexNumber,   nationalPokedexEntry);
            thePoke.addLabel(national_pokedex_no + " " + pokemonLabel, "en");

            if(kitakami_pokedex_no != null && !kitakami_pokedex_no.isEmpty() && kitakami_pokedexStmt != null && !kitakami_pokedexStmt.isEmpty() )
            {
                int dexno2 = Integer.parseInt(kitakami_pokedex_no);
                kitakami_pokedex_no = String.format("%04d", dexno2);

                Individual newPokedexEntry    = KitakamiPokedex.createIndividual(kitakami_pokedexStmt);
                newPokedexEntry         .addLabel( "Kitakami Pokedex Entry " + kitakami_pokedex_no, "en");
                newPokedexEntry         .addProperty(hasNumber,         kitakami_pokedex_no);
                newPokedexEntry         .addProperty(partOf,            KitakamiPokedex);
                KitakamiPokedex         .addProperty(hasParts,          newPokedexEntry);
                thePoke                 .addProperty(hasPokedexNumber,  newPokedexEntry);
            }

            if(paldea_pokedex_no != null && !paldea_pokedex_no.isEmpty()
                    && paldea_pokedexStmt != null && !paldea_pokedexStmt.isEmpty() )
            {
                int dexno2          = Integer.parseInt(paldea_pokedex_no);
                paldea_pokedex_no   = String.format("%04d", dexno2);

                Individual newPokedexEntry    = PaldeaPokedex.createIndividual(paldea_pokedexStmt);
                newPokedexEntry     .addLabel("Paldea Pokedex Entry " + paldea_pokedex_no, "en");
                newPokedexEntry     .addProperty(hasNumber,         paldea_pokedex_no);
                newPokedexEntry     .addProperty(partOf,            PaldeaPokedex);
                PaldeaPokedex      .addProperty(hasParts,           newPokedexEntry);
                thePoke             .addProperty(hasPokedexNumber,  newPokedexEntry);
            }
        }
    }

    public static void convertMovesGenIX()
    {
        String movesFile  = base + "pokemonTypeMoveCategory.json";

        JSONParser parser = new JSONParser(); JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }


        // check for move type
        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID            = (String) pokemon.get("pokeMove");
            String moveLabel         = (String) pokemon.get("pokeMoveLabel");
            String moveTypeStmt      = (String) pokemon.get("typeMoveStmt");
            String moveTypeStmtLabel = (String) pokemon.get("typeMoveStmtLabel");
            String typeString        = moveTypeStmtLabel.replace("Pokémon move", "").replace("-type", "").trim();

            boolean isGen = JSON2RDF.isInGenIXMoveList(moveLabel);

            Individual moveTypeIndividual    = pokemonMoveCls.createIndividual(moveTypeStmt); //the move type
            moveTypeIndividual.addLabel("Move Type " + moveTypeStmtLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,    moveTypeStmtLabel);
            moveTypeIndividual.addProperty(hasFacet,    typeString);


            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel("Move " +        moveLabel, "en");
            pokemonMove.addProperty(hasValue,       moveLabel);
            pokemonMove.addProperty(instanceOf,     moveTypeIndividual);
            pokemonMove.addProperty(presentInWork,  genIX);

            if(isGen)
                pokemonMove.addProperty(firstAppearance, genIX);
        }

        // check for contest move type
        movesFile = base + "pokemonContestMoveCategory.json";
        parser    = new JSONParser();

        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID                   = (String) pokemon.get("pokeMove");
            String moveLabel                = (String) pokemon.get("pokeMoveLabel");
            String moveContestTypeStmt      = (String) pokemon.get("categoryMoveStmt");
            String moveContestTypeStmtLabel = (String) pokemon.get("categoryMoveStmtLabel");
            String typeString               = moveContestTypeStmtLabel.replace("move", "").trim();

            boolean isGen = JSON2RDF.isInGenIXMoveList(moveLabel);

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            moveTypeIndividual.addLabel("Contest Move Type " + moveContestTypeStmtLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,    moveContestTypeStmtLabel);
            moveTypeIndividual.addProperty(hasFacet,    typeString);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel("Move " + moveLabel, "en");
            pokemonMove.addProperty(hasValue,       moveLabel);
            pokemonMove.addProperty(instanceOf,     moveTypeIndividual);
            pokemonMove.addProperty(presentInWork,  genIX);

            if(isGen)
                pokemonMove.addProperty(firstAppearance, genIX);
        }

        // check for status/physical/special split

        movesFile = base + "pokemonDamageMoveCategory.json";
        parser    = new JSONParser();

        try { a = (JSONArray) parser.parse(new FileReader(movesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID                   = (String) pokemon.get("pokeMove");
            String moveLabel                = (String) pokemon.get("pokeMoveLabel");
            String moveContestTypeStmt      = (String) pokemon.get("damageCategoryMoveStmt");
            String moveContestTypeStmtLabel = (String) pokemon.get("damageCategoryMoveStmtLabel");
            String typeString               = moveContestTypeStmtLabel.replace("move", "").trim();

            boolean isGen = JSON2RDF.isInGenIXMoveList(moveLabel);

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            moveTypeIndividual.addLabel("Damage Category Type " + moveContestTypeStmtLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,    moveContestTypeStmtLabel);
            moveTypeIndividual.addProperty(hasFacet,    typeString);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel("Move " +       moveLabel, "en");
            pokemonMove.addProperty(hasValue,       moveLabel);
            pokemonMove.addProperty(instanceOf,     moveTypeIndividual);
            pokemonMove.addProperty(presentInWork,  genIX);

            if(isGen)
                pokemonMove.addProperty(firstAppearance, genIX);
        }
    }

    public static void convertAbilitiesGenIX()
    {
        String all_abilities         = base + "all pokemon abilities.json";

        JSONParser parser = new JSONParser(); JSONArray a;

        //all abilities in wikidata
        try { a = (JSONArray) parser.parse(new FileReader(all_abilities)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonAbilityID    = (String) pokemon.get("pokemonAbility");
            String pokemonAbilityLabel = (String) pokemon.get("pokemonAbilityLabel");

            Individual thePokeAbility = pokemonAbilityCls.createIndividual(pokemonAbilityID);
            thePokeAbility.addLabel("Ability " +     pokemonAbilityLabel, "en");
            thePokeAbility.addProperty(hasValue,        pokemonAbilityLabel);
            thePokeAbility.addProperty(presentInWork,   genIX);

            if(JSON2RDF.isGenIXAbility(pokemonAbilityLabel))
                thePokeAbility.addProperty(firstAppearance, genIX);
        }
    }
    static boolean isInGenIXMoveList(String moveName)
    {
        for(String move : genIX_moves_list)
            if(move.equalsIgnoreCase(moveName))
                return true;
        return false;
    }

    static boolean isGenIXAbility(String moveName)
    {
        for(String move : genIX_abilities_list)
            if(move.equalsIgnoreCase(moveName))
                return true;
        return false;
    }



}
