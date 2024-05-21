package Utils;

import com.github.andrewoma.dexx.collection.Pair;
import org.apache.jena.atlas.json.JSON;
import org.apache.jena.ontology.*;
import org.apache.jena.rdf.model.Model;
import org.apache.jena.rdf.model.ModelFactory;
import org.apache.jena.rdf.model.ResIterator;
import org.apache.jena.rdf.model.Resource;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

public class JSON2RDF_PokeLabels
{
    static String wd = "http://www.wikidata.org/entity/";
    static String wdt = "http://www.wikidata.org/prop/direct/";
    static String wikibase = "http://wikiba.se/ontology#";
    static String p = "http://www.wikidata.org/prop/";
    static String ps = "http://www.wikidata.org/prop/statement/";
    static String pq = "http://www.wikidata.org/prop/qualifier/";
    static String bd = "http://www.bigdata.com/rdf#";

    static String pkmn = "https://pokemon.com#";

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

    static String gender_differences [] = {"Venusaur", "Butterfree", "Rattata", "Raticate", "Pikachu", "Raichu", "Nidoran♀", "Nidoran♂", "Zubat", "Golbat", "Gloom", "Vileplume", "Kadabra", "Alakazam", "Doduo", "Dodrio", "Hypno", "Rhyhorn", "Rydon", "Goldeen", "Seaking", "Scyther", "Magikarp", "Gyarados", "Eevee", "Meganium", "Ledyba", "Ledian", "Xatu", "Sudowoodo", "Politoed", "Aipom", "Wooper", "Quagsire", "Murkrow", "Wobbuffet", "Girafarig", "Gligar", "Steelix", "Scizor", "Heracross", "Sneasel", "Hisuian Sneasel", "Ursaring", "Piloswine", "Octillery", "Houndoom", "Donphan", "Torchic", "Combusken", "Blaziken", "Beautifly", "Dustox", "Ludicolo", "Nuzleaf", "Shiftry", "Meditite", "Medicham", "Roselia", "Gulpin", "Swalot", "Numel", "Camerupt", "Cacturne", "Milotic", "Relicanth", "Starly", "Staravia", "Staraptor", "Bidoof", "Bibarel", "Kricketot", "Kricketune", "Shinx", "Luxio", "Luxray", "Roserade", "Combee", "Pachirisu", "Buizel", "Floatzel", "Ambipom", "Gible", "Gabite", "Garchomp", "Hippopotas", "Hippowdon", "Croagunk", "Toxicroak", "Finneon", "Lumineon", "Snover", "Abomasnow", "Weavile", "Rhyperior", "Tangrowth", "Mamoswine", "Unfezant", "Frillish", "Jellicent", "Pyroar", "Meowstic", "Indeedee", "Basculegion", "Oinkologne"};

    static String always_male_list [] = {"Nidoran♂", "Nidorino", "Nidoking", "Hitmonlee", "Hitmonchan", "Tauros", "Hitmontop", "Volbeat", "Mothim", "Gallade", "Throh", "Sawk", "Rufflet", "Braviary", "Impidimp", "Morgrem", "Grimmsnarl", "Ursaluna", "Tyrogue", "Latios", "Tornadus", "Thundurus", "Landorus", "Greninja", "Battle Bond Greninja", "Okidogi", "Munkidori", "Fezandipiti"};
    static String always_female_list [] = {"Nidoran♀", "Chansey", "Kangaskhan", "Jynx", "Miltank", "Blissey", "Illumise", "Wormadam", "Vespiquen", "Froslass", "Petilil", "Lilligant", "Vullaby", "Mandibuzz", "Flabébé", "Floette", "Florges", "Salazzle", "Bounsweet", "Steenee", "Tsareena", "Hatenna", "Hattrem", "Hatterene", "Milcery", "Alcremie", "Tinkatink", "Tinkatuff", "Tinkaton", "Nidorina", "Nidoqueen", "Pichu", "Spiky-eared Pichu", "Smoochum", "Latias", "Happiny", "Cresselia", "Enamorus", "Ogerpon"};

    static String genderless_list [] = {"Iron Boulder", "Suicune", "Raikou", "Volcanion", "Azelf", "Miraidon", "Shaymin", "Tapu Lele", "Brute Bonnet", "Necrozma", "Porygon", "Sinistcha", "Registeel", "Rayquaza", "Jirachi", "Iron Bundle", "Voltorb", "Walking Wake", "Cobalion", "Scream Tail", "Calyrex", "Pheromosa", "Guzzlord", "Rotom", "Zapdos", "Magneton", "Cosmog", "Reshiram", "Iron Hands", "Dracovish", "Celesteela", "Manaphy", "Zacian", "Kartana", "Phione", "Dhelmise", "Regirock", "Iron Jugulis", "Zamazenta", "Claydol", "Hoopa", "Moltres", "Minior", "Iron Thorns", "Wo-Chien", "Uxie", "Polteageist", "Golett", "Porygon2", "Meloetta", "Baltoy", "Koraidon", "Iron Moth", "Glastrier", "Spectrier", "Poltchageist", "Beldum", "Roaring Moon", "Poipole", "Solgaleo", "Bronzong", "Terrakion", "Magnemite", "Darkrai", "Arctozolt", "Kyogre", "Dracozolt", "Naganadel", "Regice", "Sinistea", "Celebi", "Cryogonal", "Solrock", "Buzzwole", "Lunala", "Gholdengo", "Palkia", "Tapu Bulu", "Pecharunt", "Porygon-Z", "Bronzor", "Diancie", "Dialga", "Gouging Fire", "Cosmoem", "Chien-Pao", "Deoxys", "Mewtwo", "Stakataka", "Tapu Koko", "Zeraora", "Flutter Mane", "Kyurem", "Silvally", "Maushold", "Keldeo", "Blacephalon", "Iron Leaves", "Lunatone", "Groudon", "Klang", "Magnezone", "Arceus", "Marshadow", "Victini", "Ho-Oh", "Yveltal", "Iron Crown", "Staryu", "Melmetal", "Metang", "Chi-Yu", "Falinks", "Regidrago", "Zarude", "Golurk", "Iron Treads", "Entei", "Ting-Lu", "Articuno", "Starmie", "Gimmighoul", "Type: Null", "Unown", "Electrode", "Shedinja", "Mew", "Arctovish", "Iron Valiant", "Great Tusk", "Zygarde", "Mesprit", "Regieleki", "Slither Wing", "Xerneas", "Tandemaus", "Virizion", "Raging Bolt", "Meltan", "Ditto", "Sandy Shocks", "Metagross", "Tapu Fini", "Nihilego", "Regigigas", "Magearna", "Carbink", "Genesect", "Xurkitree", "Lugia", "Giratina", "Klink", "Zekrom", "Klinklang", "Eternatus"};

    static HashMap<String,String []> form_differences = new HashMap<>();

    static OntClass pokemonGenerationCls, pokedexCls, pokemonCls, pokemonTypeCls, pokemonEFamilyCls, pokemonMoveCls,
                    nationalPokedex, pokemonGenderRatioCls, pokemonEggGroupCls, pokemonAbilityCls,
                    regionCls, regionFormCls, megaEvolutionCls, alternateForm,
                    megaStoneCls, itemCls, genderCls, pokemonRouteCls, pokemonLocationCls,
                    moveTypeCls, moveDamageCls, moveContestCls;
    static Individual genI, genII, genIII, genIV, genV, genVI, genVII, genVIII, genIX,
    KantoRegion, JohtoRegion, HoennRegion, SinnohRegion, AlolaRegion, GalarRegion, HisuiRegion,
    KalosRegion, KitakamiRegion, OrreRegion, PaldeaRegion, SeviiRegion, UnovaRegion, UnknownRegion,
            AlolanForm, GalarianForm, HisuianForm, PaldeanForm, female, male, unknown,
            alwaysMale, alwaysFemale, genderNeutral;
    static ObjectProperty
            firstAppearance, instanceOf, presentInWork, partOf, hasParts,
            follows, followedBy, uses, hasPokedexNumber, hasPokemonType,  hasGenderRatio,
            catalogsRegion, hasAlternatePokedexEntry, from, hasAbility, hasMoveType, hasGender,
            inEvolFamily, hatches, hasMegaEvolution, hasAlternativeForm, hasRegionalForm, hasSignatureAbility, isSignatureAbilityOf,
            isSuperEffective, hasNoEffect, hasNormalEffect, hasHalfEffect, bordersWith, locatedIn;
    static DatatypeProperty hasFacet, hasShape, hasName, hasNumber, hasColor, hasHeight, hasWeight, hasValue;

    public static void initCommons()
    {
        Configs configs = new Configs();
        theModel        = ModelFactory.createOntologyModel();

        isSuperEffective = theModel.createObjectProperty(pkmn + "isSuperEffectiveAgainst");
        hasNoEffect      = theModel.createObjectProperty(pkmn + "hasNoEffectAgainst");
        hasNormalEffect  = theModel.createObjectProperty(pkmn + "hasNormalEffectAgainst");
        hasHalfEffect    = theModel.createObjectProperty(pkmn + "hasHalfEffectAgainst");

        isSuperEffective.addLabel("is super effective against", "en");
        hasNoEffect     .addLabel("has no effect against", "en");
        hasNormalEffect .addLabel("has normal effect against", "en");
        hasHalfEffect   .addLabel("is not very effective against", "en");



        regionCls = theModel.createClass(wd + "Q15830"); // a pokemon region
        regionCls.addLabel("Pokemon Region", "en");

        regionFormCls = theModel.createClass(wd + "Q56707607"); // a pokemon regional variant
        regionFormCls.addLabel("Regional Variant", "en");

        pokemonMoveCls = theModel.createClass(wd + "Q15141195"); // a pokemon move
        pokemonMoveCls.addLabel("Pokemon Move", "en");

        pokedexCls = theModel.createClass(wd + "Q1250520"); // the pokedex class
        pokedexCls.addLabel("Pokedex", "en");

        hasFacet = theModel.createDatatypeProperty(pkmn + "hasFacet");
        hasFacet.addLabel("has Facet", "en");
        hasShape = theModel.createDatatypeProperty(pkmn + "hasShape");
        hasShape.addLabel("has Shape", "en");

        follows = theModel.createObjectProperty(pkmn + "follows");
        follows.addLabel("follows", "en");

        followedBy = theModel.createObjectProperty(pkmn + "followedBy");
        followedBy.addLabel("followed By", "en");

        partOf = theModel.createObjectProperty(pkmn + "partOf");
        partOf.addLabel("part Of", "en");

        hasParts = theModel.createObjectProperty(pkmn + "hasPart");
        hasParts.addLabel("has Part", "en");

        pokemonGenerationCls = theModel.createClass(wd + "Q3759600" ); //a pokemon generation
        pokemonGenerationCls.addLabel("Pokemon Generation", "en");

        firstAppearance        = theModel.createObjectProperty(pkmn + "introducedIn"); // first appearance property
        firstAppearance.addLabel("introduced in", "en");

        presentInWork        = theModel.createObjectProperty(pkmn + "presentIn"); // present in property
        presentInWork   .addLabel("present in", "en");
        firstAppearance.setSuperProperty(presentInWork);

        pokemonCls  = theModel.createClass(wd + "Q3966183" ); //a pokemon
        pokemonCls  .addLabel("Pokemon", "en");

        megaEvolutionCls  = theModel.createClass(wd + "Q16577590" ); //a mega evolution
        megaEvolutionCls  .addLabel("Mega Pokemon", "en");
        megaEvolutionCls  .addSuperClass(pokemonCls);

        pokemonTypeCls  = theModel.createClass(wd + "Q115980997" ); //a pokemon type
        pokemonTypeCls  .addLabel("Pokemon Type", "en");

        pokemonEFamilyCls   = theModel.createClass(wd + "Q15795637" ); //a pokemon evolutionary family
        pokemonEFamilyCls   .addLabel("Pokemon Evolutionary Line", "en");

        hasPokedexNumber = theModel.createObjectProperty(pkmn + "hasPokedexEntry");
        hasNumber        = theModel.createDatatypeProperty(pkmn + "hasPokedexNumber");

        hasPokedexNumber.addLabel("has PokeDex Entry", "en");
        hasNumber       .addLabel("has Pokedex Number", "en");

        hasName = theModel.createDatatypeProperty(pkmn + "hasName");
        hasName.addLabel("has Name", "en");
        hasColor = theModel.createDatatypeProperty(pkmn + "hasColor");
        hasColor.addLabel("has Color", "en");
        hasHeight = theModel.createDatatypeProperty(pkmn + "hasHeight");
        hasHeight.addLabel("has Height", "en");
        hasWeight = theModel.createDatatypeProperty(pkmn + "hasWeight");
        hasWeight.addLabel("has Weight", "en");

        instanceOf = theModel.createObjectProperty(pkmn + "instanceOf");
        hasValue   = theModel.createDatatypeProperty(pkmn + "hasValue");
        instanceOf.addLabel("instance Of", "en");
        hasValue.addLabel("value", "en");

        hasPokemonType = theModel.createObjectProperty(pkmn + "pokemonType");
        hasPokemonType.addLabel("has Type", "en");

        catalogsRegion = theModel.createObjectProperty(pkmn + "catalogsRegion");
        catalogsRegion.addLabel("catalogs Region", "en");

        inEvolFamily = theModel.createObjectProperty(pkmn + "evolutionGroup");
        inEvolFamily.addLabel("in evolution group", "en");

        from = theModel.createObjectProperty(pkmn + "from");
        from.addLabel("from", "en");

        hasAbility = theModel.createObjectProperty(pkmn + "hasAbility");
        hasAbility.addLabel("uses ability", "en");

        hatches = theModel.createObjectProperty(pkmn + "hatches");
        hatches.addLabel("hatches", "en");

        hasMegaEvolution = theModel.createObjectProperty(pkmn + "hasMegaEvolution");
        hasMegaEvolution.addLabel("has MegaEvolution", "en");

        hasAlternativeForm = theModel.createObjectProperty(pkmn + "hasAltForm");
        hasAlternativeForm.addLabel("has Alternative Form", "en");

        hasRegionalForm = theModel.createObjectProperty(pkmn + "hasRegionalForm");
        hasRegionalForm.addLabel("has Regional Variant", "en");

        hasGenderRatio = theModel.createObjectProperty(pkmn + "hasGenderRatio");
        hasGenderRatio.addLabel("known Gender Ratio", "en");

        hasAlternatePokedexEntry = theModel.createObjectProperty(pkmn + "alternateDexEntry");
        hasAlternatePokedexEntry.addLabel("as in other Pokédex", "en");

        genI = pokemonGenerationCls.createIndividual(wd + "Q27118928"); // gen I pokemon Generation
        genI.addLabel("Generation I", "en");

        nationalPokedex = theModel.createClass(wd + "Q20005020");
        nationalPokedex.addLabel("National Pokédex", "en");
        nationalPokedex.addProperty(firstAppearance, genI);
        nationalPokedex.addSuperClass(pokedexCls);

        moveTypeCls = theModel.createClass(wd + "Q1266830"); // type category
        moveTypeCls.addSuperClass(pokemonMoveCls);
        moveTypeCls.addLabel("Move Type Category", "en");

        moveContestCls = theModel.createClass(wd + "Q26012310"); //contest category
        moveContestCls.addSuperClass(pokemonMoveCls);
        moveContestCls.addLabel("Move Contest Category", "en");

        moveDamageCls = theModel.createClass(wd + "Q26001247"); // damage category
        moveDamageCls.addSuperClass(pokemonMoveCls);
        moveDamageCls.addLabel("Move Damage Category", "en");

        hasMoveType = theModel.createObjectProperty(pkmn + "hasMoveType");
        hasMoveType.addLabel("has move type", "en");

        hasRegionalForm .addSuperProperty(hasAlternativeForm);
        hasMegaEvolution.addSuperProperty(hasAlternativeForm);

        bordersWith = theModel.createObjectProperty(pkmn + "bordersWith");
        bordersWith.addLabel("borders with", "en");

        locatedIn = theModel.createObjectProperty(pkmn + "locatedIn");
        locatedIn.addLabel("located in", "en");

        pokemonRouteCls    = theModel.createClass(wd + "Q25991640");
        pokemonLocationCls = theModel.createClass(wd + "Q32860792");

        pokemonRouteCls     .addLabel("Pokemon Route",    "en");
        pokemonLocationCls  .addLabel("Pokemon Location", "en");

        pokemonRouteCls.addSuperClass(pokemonLocationCls);

        hasSignatureAbility = theModel.createObjectProperty(pkmn + "hasSignatureAbility");
        hasSignatureAbility.addLabel("has signature Ability", "en");

        isSignatureAbilityOf = theModel.createObjectProperty(pkmn + "isSignatureAbilityOf");
        isSignatureAbilityOf.addLabel("is signature Ability of", "en");

    }


    static boolean hasGenderDifference(String pokemonLabel)
    {
        for(String p : gender_differences)
            if(p.equalsIgnoreCase(pokemonLabel))
                return true;
        return false;
    }

    static JSONArray alternateFormsJSONArray;

    static boolean hasAlternateForm(String pokemonLabel)
    {
        boolean hasAlternateForm = false;

        String pokedexGenII = base + "pokemon alternate formes.json";

        if(alternateFormsJSONArray==null)
        {
            JSONParser parser = new JSONParser();
            try {
                alternateFormsJSONArray = (JSONArray) parser.parse(new FileReader(pokedexGenII));
            } catch (Exception e) {
                System.out.println("OH NO . " + e.getMessage());
                return false;
            }
        }

        if(alternateFormsJSONArray!=null)
        {
            for (Object o : alternateFormsJSONArray)
            {
                JSONObject pokemon = (JSONObject) o;

                String alternativeL = (String) pokemon.get("pokemon");
                if(alternativeL.equalsIgnoreCase(pokemonLabel))
                    return true;
            }
        }

        return hasAlternateForm;
    }

    static ArrayList<String> getAlternateForms(String pokemonLabel, String generationLabel)
    {
        ArrayList<String> alternativeForms = new ArrayList<>();

        if(hasAlternateForm(pokemonLabel))
        {
            for (Object o : alternateFormsJSONArray)
            {
                JSONObject pokemon  = (JSONObject) o;
                String alternativeL = (String) pokemon.get("pokemon");
                if(alternativeL.equalsIgnoreCase(pokemonLabel))
                {
                    if(pokemon.containsKey(generationLabel))
                    {
                        JSONArray genForms = (JSONArray) pokemon.get(generationLabel);
                        for(Object f : genForms)
                        {
                            String formJSON = (String) f;
                            alternativeForms.add(formJSON);
                        }
                    }
                }
            }
        }

        return alternativeForms;
    }

    static ArrayList<String> getSuperEffectiveAgainst(String type)
    {
        type                                = type.replace(wd,"");

        ArrayList<String> supereffective    = new ArrayList<>();
        String jsonFile                     = base + "type effectiveness codes.json";
        JSONParser parser                   = new JSONParser(); JSONArray a;

        try { a = (JSONArray) parser.parse(new FileReader(jsonFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return supereffective; }

        for (Object o : a)
        {
            JSONObject typeObject = (JSONObject) o;
            String alternativeL   = (String) typeObject.get("type");

            if(type.equalsIgnoreCase(alternativeL))
            {
                JSONArray types = (JSONArray) typeObject.get("super effective");
                for(Object o1 : types)
                {
                    String type1 = (String) o1;
                    supereffective.add(type1);
                }
            }
        }

        return supereffective;
    }

    static ArrayList<String> getNormalEffectiveAgainst(String type)
    {
        type                                = type.replace(wd,"");

        ArrayList<String> supereffective    = new ArrayList<>();
        String jsonFile                     = base + "type effectiveness codes.json";
        JSONParser parser                   = new JSONParser(); JSONArray a;

        try { a = (JSONArray) parser.parse(new FileReader(jsonFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return supereffective; }

        for (Object o : a)
        {
            JSONObject typeObject = (JSONObject) o;
            String alternativeL   = (String) typeObject.get("type");

            if(type.equalsIgnoreCase(alternativeL))
            {
                JSONArray types = (JSONArray) typeObject.get("normal effectiveness");
                for(Object o1 : types)
                {
                    String type1 = (String) o1;
                    supereffective.add(type1);
                }
            }
        }

        return supereffective;
    }

    static ArrayList<String> getHalfEffectiveAgainst(String type)
    {
        type                                = type.replace(wd,"");

        ArrayList<String> supereffective    = new ArrayList<>();
        String jsonFile                     = base + "type effectiveness codes.json";
        JSONParser parser                   = new JSONParser(); JSONArray a;

        try { a = (JSONArray) parser.parse(new FileReader(jsonFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return supereffective; }

        for (Object o : a)
        {
            JSONObject typeObject = (JSONObject) o;
            String alternativeL   = (String) typeObject.get("type");

            if(type.equalsIgnoreCase(alternativeL))
            {
                JSONArray types = (JSONArray) typeObject.get("not very effective");
                for(Object o1 : types)
                {
                    String type1 = (String) o1;
                    supereffective.add(type1);
                }
            }
        }

        return supereffective;
    }

    static ArrayList<String> getNotEffectiveAgainst(String type)
    {
        type                                = type.replace(wd,"");

        ArrayList<String> supereffective    = new ArrayList<>();
        String jsonFile                     = base + "type effectiveness codes.json";
        JSONParser parser                   = new JSONParser(); JSONArray a;

        try { a = (JSONArray) parser.parse(new FileReader(jsonFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return supereffective; }

        for (Object o : a)
        {
            JSONObject typeObject = (JSONObject) o;
            String alternativeL   = (String) typeObject.get("type");

            if(type.equalsIgnoreCase(alternativeL))
            {
                JSONArray types = (JSONArray) typeObject.get("no effect");
                for(Object o1 : types)
                {
                    String type1 = (String) o1;
                    supereffective.add(type1);
                }
            }
        }

        return supereffective;
    }

    static boolean isAlwaysFemale(String pokemonLabel)
    {
        for(String s : always_female_list)
            if(s.equalsIgnoreCase(pokemonLabel))
                return true;
        return false;
    }

    static boolean isAlwaysMale(String pokemonLabel)
    {
        for(String s : always_male_list)
            if(s.equalsIgnoreCase(pokemonLabel))
                return true;
        return false;
    }

    static boolean isGenderless(String pokemonLabel)
    {
        for(String s : genderless_list)
            if(s.equalsIgnoreCase(pokemonLabel))
                return true;
        return false;
    }

    public static void main(String[] args)
    {

        initCommons();

        KantoRegion = regionCls.createIndividual(wd + "Q1657833");
        KantoRegion.addLabel("Kanto", "en");
        KantoRegion.addProperty(presentInWork, genI);

        nationalPokedex.addProperty(catalogsRegion, KantoRegion);

        convertPokedexGenI();
        convertPokemonGenI();
        convertMovesGenI();
        convertLocations("Kanto");


  //      OntologyUtils.writeModeltoFile(theModel, base + "genI.ttl");

        genII = pokemonGenerationCls.createIndividual(wd + "Q27118900"); // gen II pokemon Generation
        genII.addLabel("Generation II", "en");
        genI .addProperty(followedBy, genII);
        genII.addProperty(follows,    genI);

        // gen II introduces Johto Region, but allows playing Kanto too

        JohtoRegion = regionCls.createIndividual(wd + "Q1657833");
        JohtoRegion.addLabel("Johto", "en");
        JohtoRegion.addProperty(presentInWork, genII);
        KantoRegion.addProperty(presentInWork, genII);
        nationalPokedex.addProperty(catalogsRegion, JohtoRegion);

        //gen II introduces Egg and Gender

        genderCls = theModel.createClass(wd + "Q116752947");
        genderCls.addLabel("gender listing", "en");

        hasGender = theModel.createObjectProperty(pkmn + "hasGender");

        female  = genderCls.createIndividual(wd + "Q6581072");
        female.addLabel("female", "en");
        male    = genderCls.createIndividual(wd + "Q6581097");
        male.addLabel("male", "en");
        unknown = genderCls.createIndividual(wd + "Q116741172");
        unknown.addLabel("gender unknown", "en");

        pokemonGenderRatioCls = theModel.createClass(wd + "Q116753925" ); //a pokemon gender ratio
        pokemonEggGroupCls    = theModel.createClass(wd + "Q26037540" ); //a pokemon egg group

        pokemonGenderRatioCls.addLabel("Pokemon Gender Ratio", "en");
        pokemonEggGroupCls   .addLabel("Pokemon Egg Group", "en");

        alwaysMale    = pokemonGenderRatioCls.createIndividual(pkmn + "AlwaysMale");
        alwaysFemale  = pokemonGenderRatioCls.createIndividual(pkmn + "AlwaysFemale");
        genderNeutral = pokemonGenderRatioCls.createIndividual(pkmn + "GenderUnknown");

        alwaysMale   .addLabel("Always Male", "en");
        alwaysFemale .addLabel("Always Female", "en");
        genderNeutral.addLabel("Gender Unknown", "en");

        convertPokedexGenII();
        convertPokemonGenII();
        convertMovesGenII();
        convertLocations("Johto");

        OntologyUtils.writeModeltoFile(theModel, base + "genII.ttl");
/*
        genIII = pokemonGenerationCls.createIndividual(wd + "Q27118889"); // gen III pokemon Generation
        genIII.addLabel("Generation III", "en");
        genII .addProperty(followedBy, genIII);
        genIII.addProperty(follows,    genII);

        // gen III introduces Hoenn, Orre and Sevii

        OrreRegion = regionCls.createIndividual(wd + "Q3629882");
        HoennRegion = regionCls.createIndividual(wd + "Q872285");
        SeviiRegion = regionCls.createIndividual(wd + "Q2454900");

        OrreRegion .addLabel("Orre", "en");
        HoennRegion.addLabel("Hoenn", "en");
        SeviiRegion.addLabel("Sevii Islands", "en");

        SeviiRegion.addProperty(presentInWork, genIII);
        HoennRegion.addProperty(presentInWork, genIII);
        OrreRegion .addProperty(presentInWork, genIII);

        nationalPokedex.addProperty(catalogsRegion, OrreRegion);
        nationalPokedex.addProperty(catalogsRegion, HoennRegion);
        nationalPokedex.addProperty(catalogsRegion, SeviiRegion);

        // gen III introduces abilities

        uses = theModel.createObjectProperty(p + "P2283");
        uses.addLabel("uses", "en");

        pokemonAbilityCls = theModel.createClass(wd + "Q12640000"); // a pokemon ability
        pokemonAbilityCls.addLabel("Pokemon Ability", "en");

        convertAbilitiesGenIII();
        convertMovesGenIII();
        convertPokedexGenIII();
        convertPokemonGenIII();
        convertLocations("Hoenn");
        convertLocations("Orre");
        convertLocations("Sevii");

        OntologyUtils.writeModeltoFile(theModel, base + "genIII.ttl");

        genIV = pokemonGenerationCls.createIndividual(wd + "Q27118795"); // gen IV pokemon Generation
        genIV .addLabel("Generation IV", "en");
        genIII.addProperty(followedBy, genIV);
        genIV .addProperty(follows,    genIII);

        // gen IV introduces Sinnoh
        SinnohRegion = regionCls.createIndividual(wd + "Q603624");
        SinnohRegion .addLabel("Sinnoh", "en");
        SinnohRegion .addProperty(presentInWork, genIV);
        nationalPokedex.addProperty(catalogsRegion, SinnohRegion);

        // gen IV introduces female and male dyphormism

        alternateForm = theModel.createClass(wd + "Q56707539");
        alternateForm.addLabel("Alternative Form", "en");
        regionFormCls.addSuperClass(alternateForm);


        removeSignatureAbilities();
        convertMovesGenIV();
        convertAbilitiesGenIV();
        convertPokedexGenIV();
        convertPokemonGenIV();
        convertLocations("Sinnoh");

        OntologyUtils.writeModeltoFile(theModel, base + "genIV.ttl");

        genV = pokemonGenerationCls.createIndividual(wd + "Q27118381"); // gen V pokemon Generation
        genV .addLabel("Generation V", "en");
        genIV.addProperty(followedBy, genV);
        genV .addProperty(follows,    genIV);

        // gen V introduces UNOVA

        UnovaRegion = regionCls.createIndividual(wd + "Q4843341");
        UnovaRegion .addLabel("Unova", "en");
        UnovaRegion .addProperty(presentInWork, genV);
        nationalPokedex.addProperty(catalogsRegion, UnovaRegion);

        removeSignatureAbilities();
        convertMovesGenV();
        convertAbilitiesGenV();
        convertPokedexGenV();
        convertPokemonGenV();
        convertLocations("Unova");

        OntologyUtils.writeModeltoFile(theModel, base + "genV.ttl");

        genVI = pokemonGenerationCls.createIndividual(wd + "Q27065429"); // gen VI pokemon Generation
        genVI.addLabel("Generation VI", "en");
        genV .addProperty(followedBy, genVI);
        genVI.addProperty(follows,    genV);

        // generation VI introduces mega evolution
        megaStoneCls    = theModel.createClass(wd + "Q56676211" ); // a pokemon megastone
        itemCls         = theModel.createClass(wd + "Q27302146" ); // a pokemon item

        itemCls     .addLabel("Pokémon Item", "en");
        megaStoneCls.addLabel("Mega Stone",   "en");
        megaStoneCls.addSuperClass(itemCls);

        // gen VI introduces KALOS but includes the Gen III remakes

        KalosRegion = regionCls.createIndividual(wd + "Q15132899");
        KalosRegion .addLabel("Kalos", "en");
        KalosRegion .addProperty(presentInWork, genVI);
        nationalPokedex.addProperty(catalogsRegion, KalosRegion);

        SeviiRegion.addProperty(presentInWork, genVI);
        HoennRegion.addProperty(presentInWork, genVI);
        OrreRegion .addProperty(presentInWork, genVI);

        removeSignatureAbilities();
        convertMovesGenVI();
        convertAbilitiesGenVI();
        convertPokedexGenVI();
        convertPokemonGenVI();
        convertLocations("Kalos");

        OntologyUtils.writeModeltoFile(theModel, base + "genVI.ttl");

        genVII = pokemonGenerationCls.createIndividual(wd + "Q26945334"); // gen VII pokemon Generation
        genVII.addLabel("Generation VII", "en");
        genVI .addProperty(followedBy, genVII);
        genVII.addProperty(follows,    genVI);

        AlolaRegion = regionCls.createIndividual(wd + "Q25594375");
        AlolaRegion .addLabel("Alola", "en");
        AlolaRegion .addProperty(presentInWork, genVII);
        nationalPokedex.addProperty(catalogsRegion, AlolaRegion);

        AlolanForm = regionFormCls.createIndividual(wd + "Q56707595");
        AlolanForm.addLabel("Alolan Form", "en");
        AlolanForm.addProperty(from, AlolaRegion);

        removeSignatureAbilities();
        convertMovesGenVII();
        convertAbilitiesGenVII();
        convertPokedexGenVII();
        convertPokemonGenVII();
        convertLocations("Alola");

        OntologyUtils.writeModeltoFile(theModel, base + "genVII.ttl");

        genVIII = pokemonGenerationCls.createIndividual(wd + "Q61951126"); // gen VIII pokemon Generation
        genVIII.addLabel("Generation VIII", "en");
        genVII .addProperty(followedBy, genVIII);
        genVIII.addProperty(follows,    genVII);

        // gen VII introduces Galar and Hisui (which is sameAs Sinnoh)

        GalarRegion = regionCls.createIndividual(wd + "Q61951161");
        GalarRegion .addLabel("Galar", "en");
        GalarRegion .addProperty(presentInWork, genVIII);
        nationalPokedex.addProperty(catalogsRegion, GalarRegion);

        HisuiRegion = regionCls.createIndividual(wd + "Q108151641");
        HisuiRegion .addLabel("Hisui", "en");
        HisuiRegion .addProperty(presentInWork, genVIII);
        nationalPokedex.addProperty(catalogsRegion, HisuiRegion);
        HisuiRegion.addSameAs(HoennRegion);

        GalarianForm = regionFormCls.createIndividual(wd + "Q72918110");
        GalarianForm.addLabel("Galarian Form", "en");
        GalarianForm.addProperty(from, GalarRegion);

        HisuianForm = regionFormCls.createIndividual(wd + "Q108152260");
        HisuianForm.addLabel("Galarian Form", "en");
        HisuianForm.addProperty(from, HisuiRegion);

        removeSignatureAbilities();
        convertMovesGenVIII();
        convertAbilitiesGenVIII();
        convertPokedexGenVIII();
        convertPokemonGenVIII();
        convertLocations("Galar");
        convertLocations("Hisui");

        OntologyUtils.writeModeltoFile(theModel, base + "genVIII.ttl");

        genIX = pokemonGenerationCls.createIndividual(wd + "Q111033398"); // gen IX pokemon Generation
        genIX  .addLabel("Generation IX", "en");
        genVIII.addProperty(followedBy, genIX);
        genIX  .addProperty(follows,    genVIII);

        // gen VII introduces Paldea and Kitakami

        PaldeaRegion = regionCls.createIndividual(wd + "Q61951161");
        PaldeaRegion .addLabel("Paldea", "en");
        PaldeaRegion .addProperty(presentInWork, genIX);
        nationalPokedex.addProperty(catalogsRegion, PaldeaRegion);

        PaldeanForm = regionFormCls.createIndividual(wd + "Q113403839");
        PaldeanForm.addLabel("Paldean Form", "en");
        PaldeanForm.addProperty(from, PaldeaRegion);

        KitakamiRegion = regionCls.createIndividual(wd + "Q122674318");
        KitakamiRegion .addLabel("Kitakami", "en");
        KitakamiRegion .addProperty(presentInWork, genIX);

        removeSignatureAbilities();
        convertMovesGenIX();
        convertAbilitiesGenIX();
        convertPokedexGenIX();
        convertPokemonGenIX();
        convertLocations("Paldea");
        convertLocations("Kitakami");


        OntologyUtils.writeModeltoFile(theModel, base + "genIX.ttl"); */

    }


    static HashMap<String, String> getAlternativeForms(String location)
    {
        HashMap<String,String> forms = new HashMap<>();
        String fileName = base + "alolan forms.json";

        JSONParser parser = new JSONParser();JSONArray a;

        if(location.equalsIgnoreCase("alola"))
            fileName = base + "alolan forms.json";
        if(location.equalsIgnoreCase("galar"))
            fileName = base + "galarian forms.json";
        if(location.equalsIgnoreCase("hisui"))
            fileName = base + "hisuian forms.json";
        if(location.equalsIgnoreCase("paldea"))
            fileName = base + "paldean forms.json";

        try { a = (JSONArray) parser.parse(new FileReader(fileName));}
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return forms; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID    = (String) pokemon.get("pokemon");
            String pokemonLabel = (String) pokemon.get("pokemonLabel");

            forms.put(pokemonID, pokemonLabel);
        }

        return forms;
    }



    static void convertLocations(String mainArea)
    {
        String file  = base + "pokemon routes.json";
        JSONParser parser = new JSONParser(); JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(file)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonRouteID       = (String) pokemon.get("pokemonRoute");
            String pokemonRouteLabel    = (String) pokemon.get("pokemonRouteLabel");
            String regionID             = (String) pokemon.get("region");
            String regionLabel          = (String) pokemon.get("regionLabel");
            String bordersStmt          = (String) pokemon.get("borders");
            String bordersLabel         = (String) pokemon.get("bordersLabel");

            if(regionLabel!=null && !regionLabel.equalsIgnoreCase(mainArea)) continue; //ignore areas not in game

            Individual pokemonRoute = pokemonRouteCls.createIndividual(pokemonRouteID);
            pokemonRoute.addLabel(pokemonRouteLabel, "en");

            if(regionID != null && !regionID.isEmpty())
            {
                Individual pokemonRegion = pokemonLocationCls.createIndividual(regionID);
                pokemonRegion.addLabel(regionLabel, "en");
                pokemonRoute.addProperty(locatedIn, pokemonRegion);
            }

            if(bordersStmt!=null && !bordersStmt.isEmpty())
            {
                Individual pokemonAdjacentLocation = pokemonLocationCls.createIndividual(bordersStmt);
                pokemonAdjacentLocation.addLabel(bordersLabel, "en");
                pokemonRoute.addProperty(bordersWith, pokemonAdjacentLocation);
            }

        }

        file    = base + "pokemon cities and locations.json";
        parser  = new JSONParser();
        try { a = (JSONArray) parser.parse(new FileReader(file)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonAreaID       = (String) pokemon.get("pokearea");
            String pokemonAreaLabel    = (String) pokemon.get("pokeareaLabel");
            String regionID             = (String) pokemon.get("region");
            String regionLabel          = (String) pokemon.get("regionLabel");
            String bordersStmt          = (String) pokemon.get("borders");
            String bordersLabel         = (String) pokemon.get("bordersLabel");

            if(regionLabel!=null && !regionLabel.equalsIgnoreCase(mainArea)) continue; //ignore areas not in game

            Individual pokemonArea = pokemonLocationCls.createIndividual(pokemonAreaID);
            pokemonArea.addLabel(pokemonAreaLabel, "en");

            if(regionID != null && !regionID.isEmpty())
            {
                Individual pokemonRegion = pokemonLocationCls.createIndividual(regionID);
                pokemonRegion.addLabel(regionLabel, "en");
                pokemonArea.addProperty(locatedIn, pokemonRegion);
            }

            if(bordersStmt!=null && !bordersStmt.isEmpty())
            {
                Individual pokemonAdjacentLocation = pokemonLocationCls.createIndividual(bordersStmt);
                pokemonAdjacentLocation.addLabel(bordersLabel, "en");
                pokemonArea.addProperty(bordersWith, pokemonAdjacentLocation);
            }

        }


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

            if(!JSON2RDF_PokeLabels.isInGenIMoveList(moveLabel)) continue;

            String theLabel = moveTypeStmtLabel.replace("Pokémon move", "").replace("-type", "").trim();

            Individual moveTypeIndividual = pokemonMoveCls.createIndividual(moveTypeStmt); //the move type
            moveTypeIndividual.addLabel(theLabel, "en");
            moveTypeIndividual.addProperty(instanceOf, pokemonMoveCls);
            moveTypeIndividual.addOntClass(moveTypeCls);
            moveTypeIndividual.addProperty(hasValue, moveTypeStmtLabel);

            for(String s : getSuperEffectiveAgainst(moveTypeStmt))
                moveTypeIndividual.addProperty(isSuperEffective, pokemonMoveCls.createIndividual( wd + s) );

            for(String s : getNormalEffectiveAgainst(moveTypeStmt))
                moveTypeIndividual.addProperty(hasNormalEffect, pokemonMoveCls.createIndividual( wd + s) );

            for(String s : getHalfEffectiveAgainst(moveTypeStmt))
                moveTypeIndividual.addProperty(hasHalfEffect, pokemonMoveCls.createIndividual( wd + s) );

            for(String s : getNotEffectiveAgainst(moveTypeStmt))
                moveTypeIndividual.addProperty(hasNoEffect, pokemonMoveCls.createIndividual( wd + s) );

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel(moveLabel, "en");
            pokemonMove.addProperty(hasValue,    moveLabel);
            pokemonMove.addProperty(hasMoveType, moveTypeIndividual);
            pokemonMove.addProperty(firstAppearance, genI);
            pokemonMove.addProperty(presentInWork, genI);
            moveTypeIndividual.addProperty(hasFacet, theLabel);
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

                if(!evolFamilyStmtLabel.contains("Magby")
                        && !evolFamilyStmtLabel.contains("Tyrogue")
                        && !evolFamilyStmtLabel.contains("Elekid")
                        && !evolFamilyStmtLabel.contains("Munchlax")
                        && !evolFamilyStmtLabel.contains("Lickilicky")
                        && !evolFamilyStmtLabel.contains("Mime Jr.")
                        && !evolFamilyStmtLabel.contains("Jynx")
                        && !evolFamilyStmtLabel.contains("Tangrowth"))
                {

                    Individual pokemonEvolutioanryFamily = pokemonEFamilyCls.createIndividual(evolFamilyStmt);
                    pokemonEvolutioanryFamily.addLabel("EvolLine " + evolFamilyStmtLabel, "en");

                    pokemonEvolutioanryFamily.addProperty(hasName, evolFamilyStmtLabel);
                    thePoke.addProperty(inEvolFamily, pokemonEvolutioanryFamily);
                    pokemonEvolutioanryFamily.addProperty(hasParts, thePoke);

                }
            }

            String typeLabel   = (String) pokemon.get("typeLabel");
            String type        = (String) pokemon.get("type");
            String colorLabel  = (String) pokemon.get("colorLabel");
            String weightLabel = (String) pokemon.get("weightLabel");
            String heightLabel = (String) pokemon.get("heightLabel");
            String typeString  = typeLabel.replace("Pokémon", "").replace("-type", "").trim();

            if(typeLabel != null && !typeLabel.isEmpty())
            {
                Individual pokemonType = pokemonTypeCls.createIndividual(type);
                pokemonType .addLabel("Type " + typeLabel, "en");
                pokemonType .addProperty(hasValue, typeLabel);
                thePoke     .addProperty(hasPokemonType, pokemonType);
                pokemonType .addProperty(hasFacet, typeString);
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
        String pokedexGenII = base + "gen II pokedex numbers.json";
        JSONParser parser   = new JSONParser(); JSONArray a;
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
            if(move.equalsIgnoreCase(moveName))
                return true;
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

            if(!JSON2RDF_PokeLabels.isInGenIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIMoveList(moveLabel))
                continue;

            String theLabel = moveTypeStmtLabel.replace("Pokémon move", "").replace("-type", "").trim();

            Individual moveTypeIndividual    = pokemonMoveCls.createIndividual(moveTypeStmt); //the move type
            moveTypeIndividual.addLabel(theLabel, "en");
            moveTypeIndividual.addProperty(instanceOf, pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue, moveTypeStmtLabel);
            moveTypeIndividual.addOntClass(moveTypeCls);
            moveTypeIndividual.addProperty(hasFacet, theLabel);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel(moveLabel, "en");
            pokemonMove.addProperty(hasValue,    moveLabel);
            pokemonMove.addProperty(hasMoveType, moveTypeIndividual);
            pokemonMove.addProperty(presentInWork, genII);

            if(JSON2RDF_PokeLabels.isInGenIIMoveList(moveLabel))
                pokemonMove.addProperty(firstAppearance, genII);

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

            for (Object o : a)
            {
                JSONObject pokemon = (JSONObject) o;

                String pokemonID     = (String) pokemon.get("pokemon");
                String pokemonLabel  = (String) pokemon.get("pokemonLabel");
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

                if (evolFamilyStmtLabel != null && !evolFamilyStmtLabel.isEmpty() && evolFamilyStmt != null && !evolFamilyStmt.isEmpty())
                {
                    if(!evolFamilyStmtLabel.contains("Tangrowth") && !evolFamilyStmtLabel.contains("Misdreavus") && !evolFamilyStmtLabel.contains("Munchlax")
                            && !evolFamilyStmtLabel.contains("Farfetch") && !evolFamilyStmtLabel.contains("Girafarig") && !evolFamilyStmtLabel.contains("Lickilicky") && !evolFamilyStmtLabel.contains("Mantine")
                            && !evolFamilyStmtLabel.contains("Mime") && !evolFamilyStmtLabel.contains("Murkrow") && !evolFamilyStmtLabel.contains("Sneasel") && !evolFamilyStmtLabel.contains("Yanma") && !evolFamilyStmtLabel.contains("Misdreavus")
                            && !evolFamilyStmtLabel.contains("Aipom") && !evolFamilyStmtLabel.contains("Bonsly") && !evolFamilyStmtLabel.contains("Chingling") && !evolFamilyStmtLabel.contains("Budew") && !evolFamilyStmtLabel.contains("Munchlax")
                            && !evolFamilyStmtLabel.contains("Farfetch") && !evolFamilyStmtLabel.contains("Gliglar") && !evolFamilyStmtLabel.contains("Lickilicky") && !evolFamilyStmtLabel.contains("Mantine") && !evolFamilyStmtLabel.contains("Mime Jr.") && !evolFamilyStmtLabel.contains("Murkrow") && !evolFamilyStmtLabel.contains("Nosepass") && !evolFamilyStmtLabel.contains("Sneasel") && !evolFamilyStmtLabel.contains("Stantler") && !evolFamilyStmtLabel.contains("Wailmer")
                            && !evolFamilyStmtLabel.contains("Yanma"))
                    {
                        Individual pokemonEvolutioanryFamily = pokemonEFamilyCls.createIndividual(evolFamilyStmt);

                        pokemonEvolutioanryFamily   .addLabel("EvolLine " + evolFamilyStmtLabel, "en");
                        pokemonEvolutioanryFamily   .addProperty(hasName,       evolFamilyStmtLabel);
                        thePoke                     .addProperty(inEvolFamily,  pokemonEvolutioanryFamily);
                        pokemonEvolutioanryFamily   .addProperty(hasParts,      thePoke);
                    }
                }

                if(isAlwaysFemale(pokemonLabel))
                    thePoke.addProperty(hasGenderRatio, alwaysFemale);
                else if(isAlwaysMale(pokemonLabel))
                    thePoke.addProperty(hasGenderRatio, alwaysMale);
                else if(isGenderless(pokemonLabel))
                    thePoke.addProperty(hasGenderRatio, genderNeutral);

                String eggGroupStmt      = (String) pokemon.get("eggGroupStmt");
                String eggGroupStmtLabel = (String) pokemon.get("eggGroupStmtLabel");

                if (eggGroupStmtLabel != null && !eggGroupStmtLabel.isEmpty() && eggGroupStmt != null && !eggGroupStmt.isEmpty()) {
                    Individual pokemonEggGroup = pokemonEggGroupCls.createIndividual(eggGroupStmt);
                    pokemonEggGroup     .addLabel("Egg Group " + eggGroupStmtLabel, "en");
                    pokemonEggGroup     .addProperty(hasName, eggGroupStmtLabel);
                    thePoke             .addProperty(hatches, pokemonEggGroup);
                    pokemonEggGroup     .addProperty(hatches, thePoke);
                }

                String genderRatioStmt      = (String) pokemon.get("genderRatioStmt");
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

        johtoPokedex.addProperty(catalogsRegion, JohtoRegion);
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

                johtoPokedexEntry   .addProperty(hasAlternatePokedexEntry, nationalPokedexEntry);
                nationalPokedexEntry.addProperty(hasAlternatePokedexEntry, johtoPokedexEntry);

            }
        }
    }

    /*
        GEN III HOENN
     */

    static boolean isInGenIIIMoveList(String moveName)
    {
        for(String move : genIII_moves_list)
            if(move.equalsIgnoreCase(moveName))
                return true;
        return false;
    }


    static boolean isGenIIIAbility(String moveName)
    {
        for(String move : genIII_abilities_list)
            if(move.equalsIgnoreCase(moveName))
                return true;
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

            if(!JSON2RDF_PokeLabels.isGenIIIAbility(pokemonAbilityLabel)) continue;
            Individual thePokeAbility = pokemonAbilityCls.createIndividual(pokemonAbilityID);
            thePokeAbility.addLabel("Ability " + pokemonAbilityLabel, "en");
            thePokeAbility.addProperty(hasValue, pokemonAbilityLabel);

            thePokeAbility.addProperty(firstAppearance, genIII);
            thePokeAbility.addProperty(presentInWork, genIII);
        }
    }

    public static String getSignatureAbility(String pokemonLabel, String generation)
    {
        String abilitiesFile = base + "signature abilities.json";
        JSONParser parser    = new JSONParser();
        JSONArray a;
        try { a = (JSONArray) parser.parse(new FileReader(abilitiesFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return null;}

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;
            String pokemonName = (String) pokemon.get("pokemon");

            if(pokemonName.equalsIgnoreCase(pokemonLabel))
            {
                String ability = (String) pokemon.get(generation);
                return ability;
            }
        }

        return null;
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

            String evolFamilyStmt           = (String) pokemon.get("evolFamilyStmt");
            String evolFamilyStmtLabel      = (String) pokemon.get("evolFamilyStmtLabel");
            boolean hasEvolutionaryFamily   = false;

            if(evolFamilyStmtLabel != null && !evolFamilyStmtLabel.isEmpty()
                    && evolFamilyStmt != null && !evolFamilyStmt.isEmpty()
                    && !evolFamilyStmtLabel.contains("Tangrowth")
                    && !evolFamilyStmtLabel.contains("Misdreavus")
                    && !evolFamilyStmtLabel.contains("Aipom")
                    && !evolFamilyStmtLabel.contains("Bonsly")
                    && !evolFamilyStmtLabel.contains("Chingling")
                    && !evolFamilyStmtLabel.contains("Budew")
                    && !evolFamilyStmtLabel.contains("Munchlax")
                    && !evolFamilyStmtLabel.contains("Farfetch")
                    && !evolFamilyStmtLabel.contains("Gliglar")
                    && !evolFamilyStmtLabel.contains("Lickilicky")
                    && !evolFamilyStmtLabel.contains("Mantine")
                    && !evolFamilyStmtLabel.contains("Mime Jr.")
                    && !evolFamilyStmtLabel.contains("Murkrow")
                    && !evolFamilyStmtLabel.contains("Nosepass")
                    && !evolFamilyStmtLabel.contains("Sneasel")
                    && !evolFamilyStmtLabel.contains("Stantler")
                    && !evolFamilyStmtLabel.contains("Wailmer")
                    && !evolFamilyStmtLabel.contains("Yanma")
            )
            {


                Individual pokemonEvolutioanryFamily = pokemonEFamilyCls.createIndividual(evolFamilyStmt);
                pokemonEvolutioanryFamily.addLabel("EvolLine " + evolFamilyStmtLabel, "en");

                pokemonEvolutioanryFamily.addProperty(hasName, evolFamilyStmtLabel);
                thePoke.addProperty(inEvolFamily, pokemonEvolutioanryFamily);
                pokemonEvolutioanryFamily.addProperty(hasParts, thePoke);

                hasEvolutionaryFamily = true;
                String signatureAbilityLabel = getSignatureAbility(pokemonLabel, "Generation III");
                if(signatureAbilityLabel!=null && !signatureAbilityLabel.isEmpty())
                {
                    String abilityCode = getAbilityCode(signatureAbilityLabel);
                    if(abilityCode!=null && !abilityCode.isEmpty())
                    {
                        Individual pokeAbilities = pokemonAbilityCls.createIndividual(abilityCode);
                        pokeAbilities.addLabel("Ability " + signatureAbilityLabel,"en");
                        pokemonEvolutioanryFamily.addProperty(hasSignatureAbility, pokeAbilities);
                        pokeAbilities.addProperty(isSignatureAbilityOf, pokemonEvolutioanryFamily);
                    }
                }
            }

            if(!hasEvolutionaryFamily)
            {
                String signatureAbilityLabel = getSignatureAbility(pokemonLabel, "Generation III");
                if(signatureAbilityLabel!=null && !signatureAbilityLabel.isEmpty())
                {
                    String abilityCode = getAbilityCode(signatureAbilityLabel);
                    if(abilityCode!=null && !abilityCode.isEmpty())
                    {
                        Individual pokeAbilities = pokemonAbilityCls.createIndividual(abilityCode);
                        pokeAbilities.addLabel(signatureAbilityLabel,"en");
                        thePoke.addProperty(hasSignatureAbility, pokeAbilities);
                        pokeAbilities.addProperty(isSignatureAbilityOf, thePoke);

                    }
                }
            }

            if(isAlwaysFemale(pokemonLabel))
                thePoke.addProperty(hasGenderRatio, alwaysFemale);
            else if(isAlwaysMale(pokemonLabel))
                thePoke.addProperty(hasGenderRatio, alwaysMale);
            else if(isGenderless(pokemonLabel))
                thePoke.addProperty(hasGenderRatio, genderNeutral);

            String eggGroupStmt      = (String) pokemon.get("eggGroupStmt");
            String eggGroupStmtLabel = (String) pokemon.get("eggGroupStmtLabel");

            if(eggGroupStmtLabel != null && !eggGroupStmtLabel.isEmpty() && eggGroupStmt != null && !eggGroupStmt.isEmpty())
            {
                Individual pokemonEggGroup = pokemonEggGroupCls.createIndividual(eggGroupStmt);
                pokemonEggGroup.addLabel("Egg Group " + eggGroupStmtLabel, "en");
                pokemonEggGroup.addProperty(hasName, eggGroupStmtLabel);
                thePoke.addProperty(hatches, pokemonEggGroup);
                pokemonEggGroup.addProperty(hatches, thePoke);
            }

            String genderRatioStmt      = (String) pokemon.get("genderRatioStmt");
            String genderRatioStmtLabel = (String) pokemon.get("grStmtLabel");

            if(genderRatioStmt != null && !genderRatioStmt.isEmpty() && genderRatioStmtLabel != null && !genderRatioStmtLabel.isEmpty())
            {
                Individual pokemonGenderRatio = pokemonGenderRatioCls.createIndividual(genderRatioStmt);
                pokemonGenderRatio  .addLabel("Gender Ratio " + genderRatioStmtLabel.replace("gender ratio", "").trim(), "en");

                pokemonGenderRatio  .addProperty(hasName,           genderRatioStmtLabel);
                thePoke             .addProperty(hasGenderRatio,    pokemonGenderRatio);
                pokemonGenderRatio  .addProperty(hasParts,          thePoke);
            }


            String typeLabel   = (String) pokemon.get("typeLabel");
            String type        = (String) pokemon.get("type");
            String colorLabel  = (String) pokemon.get("colorLabel");
            String weightLabel = (String) pokemon.get("weightLabel");
            String heightLabel = (String) pokemon.get("heightLabel");
            String shapeLabel  = (String) pokemon.get("shapeLabel");
            String typeString  = typeLabel.replace("Pokémon", "").replace("-type", "").trim();

            if(typeLabel != null && !typeLabel.isEmpty())
            {
                Individual pokemonType = pokemonTypeCls.createIndividual(type);
                pokemonType .addLabel("Type " + typeLabel, "en");
                pokemonType .addProperty(hasPokemonType,    typeLabel);
                thePoke     .addProperty(hasValue,          pokemonType);
                pokemonType .addProperty(hasFacet,          typeString);
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

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String moveID            = (String) pokemon.get("pokeMove");
            String moveLabel         = (String) pokemon.get("pokeMoveLabel");
            String moveTypeStmt      = (String) pokemon.get("typeMoveStmt");
            String moveTypeStmtLabel = (String) pokemon.get("typeMoveStmtLabel");

            if(!JSON2RDF_PokeLabels.isInGenIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIIMoveList(moveLabel)) continue;

            String theLabel = moveTypeStmtLabel.replace("Pokémon move", "").replace("-type", "").trim();

            Individual moveTypeIndividual    = pokemonMoveCls.createIndividual(moveTypeStmt); //the move type
            moveTypeIndividual.addLabel(theLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,    moveTypeStmtLabel);
            moveTypeIndividual.addOntClass(moveTypeCls);
            moveTypeIndividual.addProperty(hasFacet,    theLabel);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel(moveLabel, "en");
            pokemonMove.addProperty(hasValue,       moveLabel);
            pokemonMove.addProperty(hasMoveType,    moveTypeIndividual);
            pokemonMove.addProperty(presentInWork,  genIII);

            if(JSON2RDF_PokeLabels.isInGenIIIMoveList(moveLabel))
                pokemonMove.addProperty(firstAppearance, genIII);

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

            if(!JSON2RDF_PokeLabels.isInGenIMoveList(moveLabel) && !JSON2RDF_PokeLabels.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIIMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            String theLabel = moveContestTypeStmtLabel.replace("move", "").trim();

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            moveTypeIndividual.addLabel(theLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addOntClass(moveContestCls);
            moveTypeIndividual.addProperty(hasValue,    moveContestTypeStmtLabel);
            moveTypeIndividual.addProperty(hasFacet,    theLabel);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel(moveLabel, "en");
            pokemonMove.addProperty(hasValue,       moveLabel);
            pokemonMove.addProperty(hasMoveType,    moveTypeIndividual);
            pokemonMove.addProperty(presentInWork,  genIII);

            if(JSON2RDF_PokeLabels.isInGenIIIMoveList(moveLabel))
                pokemonMove.addProperty(firstAppearance, genIII);

        }
    }

    public static void convertPokedexGenIII()
    {

        String pokedexGenIII       = base + "all pokedex entries.json";

        OntClass hoennPokedex = theModel.createClass(wd + "Q18086665");
        hoennPokedex.addLabel("Hoenn Pokédex", "en");
        hoennPokedex.addProperty(firstAppearance, genIII);
        hoennPokedex.addSuperClass(pokedexCls);

        hoennPokedex.addProperty(catalogsRegion, HoennRegion);

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

                hoennPokedex        .addProperty(hasAlternatePokedexEntry, nationalPokedexEntry);
                nationalPokedexEntry.addProperty(hasAlternatePokedexEntry, hoennPokedex);
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
        sinnohPokedex.addProperty(catalogsRegion, SinnohRegion);

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
                thePoke              .addProperty(hasPokedexNumber, sinnohPokedexEntry);

                sinnohPokedexEntry  .addProperty(hasAlternatePokedexEntry, nationalPokedexEntry);
                nationalPokedexEntry.addProperty(hasAlternatePokedexEntry, sinnohPokedexEntry);
            }
        }
    }


    public static void removeSignatureAbilities()
    {

        ResIterator resIterator = theModel.listSubjectsWithProperty(hasSignatureAbility);
        while(resIterator.hasNext())
        {
            Resource next = resIterator.next();
            Resource propertyResourceValue = next.getPropertyResourceValue(hasSignatureAbility);
            theModel.remove(next, hasSignatureAbility, propertyResourceValue);
        }

        resIterator = theModel.listSubjectsWithProperty(isSignatureAbilityOf);
        while(resIterator.hasNext())
        {
            Resource next = resIterator.next();
            Resource propertyResourceValue = next.getPropertyResourceValue(isSignatureAbilityOf);
            theModel.remove(next, isSignatureAbilityOf, propertyResourceValue);
        }

        System.out.println("Removed");
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

            String pokemonID     = (String) pokemon.get("pokemon");
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

            boolean hasEvolutionaryFamily = false;
            if(evolFamilyStmtLabel != null && !evolFamilyStmtLabel.isEmpty() && evolFamilyStmt != null && !evolFamilyStmt.isEmpty())
            {
                Individual pokemonEvolutioanryFamily = pokemonEFamilyCls.createIndividual(evolFamilyStmt);
                pokemonEvolutioanryFamily.addLabel("EvolLine " + evolFamilyStmtLabel, "en");

                pokemonEvolutioanryFamily.addProperty(hasName, evolFamilyStmtLabel);
                thePoke.addProperty(inEvolFamily, pokemonEvolutioanryFamily);
                pokemonEvolutioanryFamily.addProperty(hasParts, thePoke);

                hasEvolutionaryFamily = true;
                String signatureAbilityLabel = getSignatureAbility(pokemonLabel, "Generation IV");
                if(signatureAbilityLabel!=null && !signatureAbilityLabel.isEmpty())
                {
                    String abilityCode = getAbilityCode(signatureAbilityLabel);
                    if(abilityCode!=null && !abilityCode.isEmpty())
                    {
                        Individual pokeAbilities = pokemonAbilityCls.createIndividual(abilityCode);
                        pokeAbilities.addLabel("Ability " + signatureAbilityLabel,"en");
                        pokemonEvolutioanryFamily.addProperty(hasSignatureAbility, pokeAbilities);
                        pokeAbilities.addProperty(isSignatureAbilityOf, pokemonEvolutioanryFamily);

                    }
                }
            }

            if(!hasEvolutionaryFamily)
            {
                String signatureAbilityLabel = getSignatureAbility(pokemonLabel, "Generation IV");
                if(signatureAbilityLabel!=null && !signatureAbilityLabel.isEmpty())
                {
                    String abilityCode = getAbilityCode(signatureAbilityLabel);
                    if(abilityCode!=null && !abilityCode.isEmpty())
                    {
                        Individual pokeAbilities = pokemonAbilityCls.createIndividual(abilityCode);
                        pokeAbilities.addLabel(signatureAbilityLabel,"en");
                        thePoke.addProperty(hasSignatureAbility, pokeAbilities);
                        pokeAbilities.addProperty(isSignatureAbilityOf, thePoke);

                    }
                }
            }



            if(isAlwaysFemale(pokemonLabel))
                thePoke.addProperty(hasGenderRatio, alwaysFemale);
            else if(isAlwaysMale(pokemonLabel))
                thePoke.addProperty(hasGenderRatio, alwaysMale);
            else if(isGenderless(pokemonLabel))
                thePoke.addProperty(hasGenderRatio, genderNeutral);

            String eggGroupStmt      = (String) pokemon.get("eggGroupStmt");
            String eggGroupStmtLabel = (String) pokemon.get("eggGroupStmtLabel");

            if(eggGroupStmtLabel != null && !eggGroupStmtLabel.isEmpty() && eggGroupStmt != null && !eggGroupStmt.isEmpty())
            {
                Individual pokemonEggGroup = pokemonEggGroupCls.createIndividual(eggGroupStmt);
                pokemonEggGroup.addLabel("Egg Group " + eggGroupStmtLabel, "en");

                pokemonEggGroup.addProperty(hasName, eggGroupStmtLabel);
                thePoke.addProperty(hatches, pokemonEggGroup);
                pokemonEggGroup.addProperty(hatches, thePoke);
            }

            String genderRatioStmt      = (String) pokemon.get("genderRatioStmt");
            String genderRatioStmtLabel = (String) pokemon.get("grStmtLabel");

            if(genderRatioStmt != null && !genderRatioStmt.isEmpty() && genderRatioStmtLabel != null && !genderRatioStmtLabel.isEmpty())
            {
                Individual pokemonGenderRatio = pokemonGenderRatioCls.createIndividual(genderRatioStmt);

                pokemonGenderRatio.addLabel("Gender Ratio " + genderRatioStmtLabel.replace("gender ratio", "").trim(), "en");
                pokemonGenderRatio.addProperty(hasName,         genderRatioStmtLabel);
                thePoke           .addProperty(hasGenderRatio,  pokemonGenderRatio);
                pokemonGenderRatio.addProperty(hasParts,        thePoke);
            }

            if(hasGenderDifference(pokemonLabel) && !pokemonLabel.contains("Nidoran"))
            {
                Individual malePokeVersion   = theModel.createIndividual(pokemonID+"_maleForm",   pokemonCls);
                Individual femalePokeVersion = theModel.createIndividual(pokemonID+"_femaleForm", pokemonCls);

                malePokeVersion.addLabel(pokemonLabel + " male form", "en");
                malePokeVersion.addOntClass(alternateForm);
                malePokeVersion.addProperty(firstAppearance, genIV);
                malePokeVersion.addProperty(hasAlternativeForm, thePoke);
                malePokeVersion.addProperty(hasGender, male);
                thePoke.addProperty(hasAlternativeForm, malePokeVersion);

                femalePokeVersion.addLabel(pokemonLabel + " female form", "en");
                femalePokeVersion.addProperty(firstAppearance, genIV);
                femalePokeVersion.addOntClass(alternateForm);
                femalePokeVersion.addProperty(hasGender, female);
                femalePokeVersion.addProperty(hasAlternativeForm, thePoke);
                thePoke.addProperty(hasAlternativeForm, femalePokeVersion);
            }

            ArrayList<String> alternateForms = getAlternateForms(pokemonLabel, "Generation IV");
            for(String alternativeForm : alternateForms)
            {
                Individual alternatePoke = alternateForm.createIndividual(alternativeForm.toLowerCase().replace(" ","_"));
                alternatePoke.addLabel(alternativeForm, "en");
                alternatePoke.addOntClass(pokemonCls);
                alternatePoke.addProperty(firstAppearance, genIV);
                alternatePoke.addProperty(hasAlternativeForm, thePoke);
                thePoke.addProperty(hasAlternativeForm, alternatePoke);
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
                thePoke    .addProperty(hasPokemonType, pokemonType);
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
            String theLabel          = moveTypeStmtLabel.replace("Pokémon move", "").replace("-type", "").trim();

            if(!JSON2RDF_PokeLabels.isInGenIMoveList(moveLabel) && !JSON2RDF_PokeLabels.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIIMoveList(moveLabel) && !JSON2RDF_PokeLabels.isInGenIVMoveList(moveLabel)) continue;

            Individual moveTypeIndividual   = pokemonMoveCls.createIndividual(moveTypeStmt); //the move type
            Individual pokemonMove          = pokemonMoveCls.createIndividual(moveID); // the move

            moveTypeIndividual.addLabel(theLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,    pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,      moveTypeStmtLabel);
            moveTypeIndividual.addOntClass(moveTypeCls);

            pokemonMove       .addLabel( moveLabel, "en");
            pokemonMove       .addProperty(hasValue,      moveLabel);
            pokemonMove       .addProperty(hasMoveType,   moveTypeIndividual);
            pokemonMove       .addProperty(presentInWork, genIV);
            moveTypeIndividual.addProperty(hasFacet,      theLabel);

            if(JSON2RDF_PokeLabels.isInGenIVMoveList(moveLabel))
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
            String theLabel                 = moveContestTypeStmtLabel.replace("move", "").trim();

            if(!JSON2RDF_PokeLabels.isInGenIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIVMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            Individual pokemonMove         = pokemonMoveCls.createIndividual(moveID);

            moveTypeIndividual.addLabel(theLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,    pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,      moveContestTypeStmtLabel);
            moveTypeIndividual.addOntClass(moveContestCls);
            pokemonMove       .addLabel(moveLabel, "en");
            pokemonMove       .addProperty(hasValue,      moveLabel);
            pokemonMove       .addProperty(hasMoveType,   moveTypeIndividual);
            pokemonMove       .addProperty(presentInWork, genIV);
            moveTypeIndividual.addProperty(hasFacet,      theLabel);

            if(JSON2RDF_PokeLabels.isInGenIVMoveList(moveLabel))
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
            String theLabel               = moveContestTypeStmtLabel.replace("move", "").trim();

            if(!JSON2RDF_PokeLabels.isInGenIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIVMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual pokemonMove         = pokemonMoveCls.createIndividual(moveID); //the move
            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt);  //the move type

            moveTypeIndividual.addOntClass(moveDamageCls);
            moveTypeIndividual.addLabel(theLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,    pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,      moveContestTypeStmtLabel);
            pokemonMove       .addLabel(moveLabel, "en");
            pokemonMove       .addProperty(hasValue,      moveLabel);
            pokemonMove       .addProperty(hasMoveType,   moveTypeIndividual);
            moveTypeIndividual.addProperty(hasFacet,      theLabel);
            pokemonMove       .addProperty(presentInWork, genIV);

            if(JSON2RDF_PokeLabels.isInGenIVMoveList(moveLabel))
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

            if(!JSON2RDF_PokeLabels.isGenIVAbility(pokemonAbilityLabel)) continue;

            Individual thePokeAbility = pokemonAbilityCls.createIndividual(pokemonAbilityID);
            Individual thePoke = pokemonCls.createIndividual(pokemonID);

            thePokeAbility.addLabel("Ability " + pokemonAbilityLabel, "en");
            thePokeAbility.addProperty(hasValue,    pokemonAbilityLabel);
            thePoke       .addLabel(pokemonLabel, "en");
            thePoke       .addProperty(hasAbility,        thePokeAbility);
        }

        //all abilities in wikidata
        try { a = (JSONArray) parser.parse(new FileReader(all_abilities)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonAbilityID    = (String) pokemon.get("pokemonAbility");
            String pokemonAbilityLabel = (String) pokemon.get("pokemonAbilityLabel");

            if(JSON2RDF_PokeLabels.isGenIVAbility(pokemonAbilityLabel) || JSON2RDF_PokeLabels.isGenIIIAbility(pokemonAbilityLabel))
            {
                Individual thePokeAbility = pokemonAbilityCls.createIndividual(pokemonAbilityID);
                thePokeAbility.addLabel("Ability " + pokemonAbilityLabel, "en");
                thePokeAbility.addProperty(hasValue, pokemonAbilityLabel);
                thePokeAbility.addProperty(presentInWork, genIV);

                if(JSON2RDF_PokeLabels.isGenIVAbility(pokemonAbilityLabel))
                    thePokeAbility.addProperty(firstAppearance, genIV);

            }
        }
    }

    public static String getAbilityCode(String abilityLabel)
    {
        String all_abilities         = base + "all pokemon abilities.json";

        JSONParser parser = new JSONParser(); JSONArray a;

        //all abilities in wikidata
        try { a = (JSONArray) parser.parse(new FileReader(all_abilities)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return null; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonAbilityID    = (String) pokemon.get("pokemonAbility");
            String pokemonAbilityLabel = (String) pokemon.get("pokemonAbilityLabel");

            if(abilityLabel.equalsIgnoreCase(pokemonAbilityLabel))
                return pokemonAbilityID;
        }

        return null;
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

            boolean hasEvolutionaryFamily = false;

            if(evolFamilyStmtLabel != null && !evolFamilyStmtLabel.isEmpty() && evolFamilyStmt != null && !evolFamilyStmt.isEmpty())
            {
                Individual pokemonEvolutioanryFamily = pokemonEFamilyCls.createIndividual(evolFamilyStmt);
                pokemonEvolutioanryFamily.addLabel("EvolLine " + evolFamilyStmtLabel, "en");

                pokemonEvolutioanryFamily.addProperty(hasName, evolFamilyStmtLabel);
                thePoke.addProperty(inEvolFamily, pokemonEvolutioanryFamily);
                pokemonEvolutioanryFamily.addProperty(hasParts, thePoke);

                hasEvolutionaryFamily = true;
                String signatureAbilityLabel = getSignatureAbility(pokemonLabel, "Generation V");
                if(signatureAbilityLabel!=null && !signatureAbilityLabel.isEmpty())
                {
                    String abilityCode = getAbilityCode(signatureAbilityLabel);
                    if(abilityCode!=null && !abilityCode.isEmpty())
                    {
                        Individual pokeAbilities = pokemonAbilityCls.createIndividual(abilityCode);
                        pokeAbilities.addLabel("Ability " + signatureAbilityLabel,"en");
                        pokemonEvolutioanryFamily.addProperty(hasSignatureAbility, pokeAbilities);
                        pokeAbilities.addProperty(isSignatureAbilityOf, pokemonEvolutioanryFamily);

                    }
                }
            }
            if(!hasEvolutionaryFamily)
            {
                String signatureAbilityLabel = getSignatureAbility(pokemonLabel, "Generation V");
                if(signatureAbilityLabel!=null && !signatureAbilityLabel.isEmpty())
                {
                    String abilityCode = getAbilityCode(signatureAbilityLabel);
                    if(abilityCode!=null && !abilityCode.isEmpty())
                    {
                        Individual pokeAbilities = pokemonAbilityCls.createIndividual(abilityCode);
                        pokeAbilities.addLabel(signatureAbilityLabel,"en");
                        thePoke.addProperty(hasSignatureAbility, pokeAbilities);
                        pokeAbilities.addProperty(isSignatureAbilityOf, thePoke);

                    }
                }
            }




            if(hasGenderDifference(pokemonLabel) && !pokemonLabel.contains("Nidoran"))
            {
                Individual malePokeVersion   = theModel.createIndividual(pokemonID+"_maleForm",   pokemonCls);
                Individual femalePokeVersion = theModel.createIndividual(pokemonID+"_femaleForm", pokemonCls);

                malePokeVersion.addLabel(pokemonLabel + " male form", "en");
                malePokeVersion.addOntClass(alternateForm);
                malePokeVersion.addProperty(firstAppearance, genIV);
                malePokeVersion.addProperty(hasAlternativeForm, thePoke);
                malePokeVersion.addProperty(hasGender, male);
                thePoke.addProperty(hasAlternativeForm, malePokeVersion);

                femalePokeVersion.addLabel(pokemonLabel + " female form", "en");
                femalePokeVersion.addProperty(firstAppearance, genIV);
                femalePokeVersion.addOntClass(alternateForm);
                femalePokeVersion.addProperty(hasGender, female);
                femalePokeVersion.addProperty(hasAlternativeForm, thePoke);
                thePoke.addProperty(hasAlternativeForm, femalePokeVersion);
            }

            if(isAlwaysFemale(pokemonLabel))
                thePoke.addProperty(hasGenderRatio, alwaysFemale);
            else if(isAlwaysMale(pokemonLabel))
                thePoke.addProperty(hasGenderRatio, alwaysMale);
            else if(isGenderless(pokemonLabel))
                thePoke.addProperty(hasGenderRatio, genderNeutral);

            ArrayList<String> alternateForms = getAlternateForms(pokemonLabel, "Generation V");
            for(String alternativeForm : alternateForms)
            {
                Individual alternatePoke = alternateForm.createIndividual(alternativeForm.toLowerCase().replace(" ","_"));
                alternatePoke.addLabel(alternativeForm, "en");
                alternatePoke.addOntClass(pokemonCls);
                alternatePoke.addProperty(firstAppearance, genV);
                thePoke.addProperty(hasAlternativeForm, alternatePoke);
                alternatePoke.addProperty(hasAlternativeForm, thePoke);

            }

            String eggGroupStmt      = (String) pokemon.get("eggGroupStmt");
            String eggGroupStmtLabel = (String) pokemon.get("eggGroupStmtLabel");

            if(eggGroupStmtLabel != null && !eggGroupStmtLabel.isEmpty() && eggGroupStmt != null && !eggGroupStmt.isEmpty())
            {
                Individual pokemonEggGroup = pokemonEggGroupCls.createIndividual(eggGroupStmt);
                pokemonEggGroup.addLabel("Egg Group " + eggGroupStmtLabel, "en");

                pokemonEggGroup.addProperty(hasName, eggGroupStmtLabel);
                thePoke.addProperty(hatches, pokemonEggGroup);
                pokemonEggGroup.addProperty(hatches, thePoke);
            }

            String genderRatioStmt      = (String) pokemon.get("genderRatioStmt");
            String genderRatioStmtLabel = (String) pokemon.get("grStmtLabel");

            if(genderRatioStmt != null && !genderRatioStmt.isEmpty() && genderRatioStmtLabel != null && !genderRatioStmtLabel.isEmpty())
            {
                Individual pokemonGenderRatio = pokemonGenderRatioCls.createIndividual(genderRatioStmt);
                pokemonGenderRatio  .addLabel("Gender Ratio " + genderRatioStmtLabel.replace("gender ratio", "").trim(), "en");
                pokemonGenderRatio  .addProperty(hasName,           genderRatioStmtLabel);
                thePoke             .addProperty(hasGenderRatio,    pokemonGenderRatio);
                pokemonGenderRatio  .addProperty(hasParts,          thePoke);
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
                pokemonType.addProperty(hasValue,       typeLabel);
                thePoke    .addProperty(hasPokemonType, pokemonType);
                pokemonType.addProperty(hasFacet,       typeString);
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
        newPokedex.addProperty(catalogsRegion, UnovaRegion);

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

                newPokedexEntry  .addProperty(hasAlternatePokedexEntry, nationalPokedexEntry);
                nationalPokedexEntry.addProperty(hasAlternatePokedexEntry, newPokedexEntry);
            }
        }
    }

    public static void convertMovesGenV()
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
            String theLabel          = moveTypeStmtLabel.replace("Pokémon move", "").replace("-type", "").trim();

            if(!JSON2RDF_PokeLabels.isInGenIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVMoveList(moveLabel)) continue;

            Individual moveTypeIndividual   = pokemonMoveCls.createIndividual(moveTypeStmt); //the move type
            Individual pokemonMove          = pokemonMoveCls.createIndividual(moveID); // the move

            moveTypeIndividual.addOntClass(moveTypeCls);
            moveTypeIndividual.addLabel(theLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,    pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,      moveTypeStmtLabel);
            pokemonMove       .addLabel(                  moveLabel, "en");
            pokemonMove       .addProperty(hasValue,      moveLabel);
            pokemonMove       .addProperty(hasMoveType,   moveTypeIndividual);
            pokemonMove       .addProperty(presentInWork, genV);
            moveTypeIndividual.addProperty(hasFacet,      theLabel);

            if(JSON2RDF_PokeLabels.isInGenVMoveList(moveLabel))
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
            String theLabel                 = moveContestTypeStmtLabel.replace("move", "").trim();

            if(!JSON2RDF_PokeLabels.isInGenIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            Individual pokemonMove         = pokemonMoveCls.createIndividual(moveID);

            moveTypeIndividual  .addOntClass(moveContestCls);
            moveTypeIndividual  .addLabel(theLabel, "en");
            moveTypeIndividual  .addProperty(instanceOf,            pokemonMoveCls);
            moveTypeIndividual  .addProperty(hasValue,              moveContestTypeStmtLabel);
            moveTypeIndividual  .addProperty(hasFacet,              theLabel);

            pokemonMove         .addLabel( moveLabel, "en");
            pokemonMove         .addProperty(hasValue,      moveLabel);
            pokemonMove         .addProperty(hasMoveType,   moveTypeIndividual);
            pokemonMove         .addProperty(presentInWork, genV);

            if(JSON2RDF_PokeLabels.isInGenVMoveList(moveLabel))
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
            String theLabel               = moveContestTypeStmtLabel.replace("move", "").trim();

            if(!JSON2RDF_PokeLabels.isInGenIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual moveTypeIndividual   = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            Individual pokemonMove          = pokemonMoveCls.createIndividual(moveID);

            moveTypeIndividual  .addOntClass(moveDamageCls);
            moveTypeIndividual  .addLabel(theLabel, "en");
            moveTypeIndividual  .addProperty(instanceOf,    pokemonMoveCls);
            moveTypeIndividual  .addProperty(hasValue,      moveContestTypeStmtLabel);
            moveTypeIndividual  .addProperty(hasFacet,      theLabel);

            pokemonMove         .addLabel(   moveLabel, "en");
            pokemonMove         .addProperty(hasValue,      moveLabel);
            pokemonMove         .addProperty(hasMoveType,   moveTypeIndividual);
            pokemonMove         .addProperty(presentInWork, genV);

            if(JSON2RDF_PokeLabels.isInGenVMoveList(moveLabel))
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

            if(JSON2RDF_PokeLabels.isGenVAbility(pokemonAbilityLabel) || JSON2RDF_PokeLabels.isGenIIIAbility(pokemonAbilityLabel))
            {
                Individual thePokeAbility = pokemonAbilityCls.createIndividual(pokemonAbilityID);

                thePokeAbility.addLabel("Ability " +     pokemonAbilityLabel, "en");
                thePokeAbility.addProperty(hasValue,        pokemonAbilityLabel);
                thePokeAbility.addProperty(presentInWork,   genV);

                if(JSON2RDF_PokeLabels.isGenVAbility(pokemonAbilityLabel))
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

            boolean hasEvolutionaryFamily = false;
            if(evolFamilyStmtLabel != null && !evolFamilyStmtLabel.isEmpty()
                    && evolFamilyStmt != null && !evolFamilyStmt.isEmpty())
            {
                Individual pokemonEvolutioanryFamily = pokemonEFamilyCls.createIndividual(evolFamilyStmt);

                pokemonEvolutioanryFamily   .addLabel("EvolLine " + evolFamilyStmtLabel, "en");
                pokemonEvolutioanryFamily   .addProperty(hasName,       evolFamilyStmtLabel);
                thePoke                     .addProperty(inEvolFamily,  pokemonEvolutioanryFamily);
                pokemonEvolutioanryFamily   .addProperty(hasParts,      thePoke);

                hasEvolutionaryFamily = true;
                String signatureAbilityLabel = getSignatureAbility(pokemonLabel, "Generation VI");
                if(signatureAbilityLabel!=null && !signatureAbilityLabel.isEmpty())
                {
                    String abilityCode = getAbilityCode(signatureAbilityLabel);
                    if(abilityCode!=null && !abilityCode.isEmpty())
                    {
                        Individual pokeAbilities = pokemonAbilityCls.createIndividual(abilityCode);
                        pokeAbilities.addLabel("Ability " + signatureAbilityLabel,"en");
                        pokemonEvolutioanryFamily.addProperty(hasSignatureAbility, pokeAbilities);
                        pokeAbilities.addProperty(isSignatureAbilityOf, pokemonEvolutioanryFamily);
                    }
                }
            }

            if(!hasEvolutionaryFamily)
            {
                String signatureAbilityLabel = getSignatureAbility(pokemonLabel, "Generation VI");
                if(signatureAbilityLabel!=null && !signatureAbilityLabel.isEmpty())
                {
                    String abilityCode = getAbilityCode(signatureAbilityLabel);
                    if(abilityCode!=null && !abilityCode.isEmpty())
                    {
                        Individual pokeAbilities = pokemonAbilityCls.createIndividual(abilityCode);
                        pokeAbilities.addLabel(signatureAbilityLabel,"en");
                        thePoke.addProperty(hasSignatureAbility, pokeAbilities);
                        pokeAbilities.addProperty(isSignatureAbilityOf, thePoke);

                    }
                }
            }

            if(hasGenderDifference(pokemonLabel) && !pokemonLabel.contains("Nidoran"))
            {
                Individual malePokeVersion   = theModel.createIndividual(pokemonID+"_maleForm",   pokemonCls);
                Individual femalePokeVersion = theModel.createIndividual(pokemonID+"_femaleForm", pokemonCls);

                malePokeVersion.addLabel(pokemonLabel + " male form", "en");
                malePokeVersion.addOntClass(alternateForm);
                malePokeVersion.addProperty(firstAppearance, genIV);
                malePokeVersion.addProperty(hasAlternativeForm, thePoke);
                malePokeVersion.addProperty(hasGender, male);
                thePoke.addProperty(hasAlternativeForm, malePokeVersion);

                femalePokeVersion.addLabel(pokemonLabel + " female form", "en");
                femalePokeVersion.addProperty(firstAppearance, genIV);
                femalePokeVersion.addOntClass(alternateForm);
                femalePokeVersion.addProperty(hasGender, female);
                femalePokeVersion.addProperty(hasAlternativeForm, thePoke);
                thePoke.addProperty(hasAlternativeForm, femalePokeVersion);
            }

            if(isAlwaysFemale(pokemonLabel))
                thePoke.addProperty(hasGenderRatio, alwaysFemale);
            else if(isAlwaysMale(pokemonLabel))
                thePoke.addProperty(hasGenderRatio, alwaysMale);
            else if(isGenderless(pokemonLabel))
                thePoke.addProperty(hasGenderRatio, genderNeutral);

            ArrayList<String> alternateForms = getAlternateForms(pokemonLabel, "Generation VI");
            for(String alternativeForm : alternateForms)
            {
                Individual alternatePoke = alternateForm.createIndividual(alternativeForm.toLowerCase().replace(" ","_"));
                alternatePoke.addLabel(alternativeForm, "en");
                alternatePoke.addOntClass(pokemonCls);
                alternatePoke.addProperty(firstAppearance, genVI);
                alternatePoke.addProperty(hasAlternativeForm, thePoke);
                thePoke      .addProperty(hasAlternativeForm, alternatePoke);
            }

            String eggGroupStmt      = (String) pokemon.get("eggGroupStmt");
            String eggGroupStmtLabel = (String) pokemon.get("eggGroupStmtLabel");

            if(eggGroupStmtLabel != null && !eggGroupStmtLabel.isEmpty() && eggGroupStmt != null && !eggGroupStmt.isEmpty())
            {
                Individual pokemonEggGroup = pokemonEggGroupCls.createIndividual(eggGroupStmt);

                pokemonEggGroup.addLabel("Egg Group " + eggGroupStmtLabel, "en");
                pokemonEggGroup.addProperty(hasName,    eggGroupStmtLabel);
                thePoke         .addProperty(hatches,   pokemonEggGroup);
                pokemonEggGroup.addProperty(hatches,    thePoke);
            }

            String genderRatioStmt      = (String) pokemon.get("genderRatioStmt");
            String genderRatioStmtLabel = (String) pokemon.get("grStmtLabel");

            if(genderRatioStmt != null && !genderRatioStmt.isEmpty() && genderRatioStmtLabel != null && !genderRatioStmtLabel.isEmpty())
            {
                Individual pokemonGenderRatio = pokemonGenderRatioCls.createIndividual(genderRatioStmt);

                pokemonGenderRatio.addLabel("Gender Ratio " + genderRatioStmtLabel.replace("gender ratio", "").trim(), "en");
                pokemonGenderRatio.addProperty(hasName,             genderRatioStmtLabel);
                thePoke           .addProperty(hasGenderRatio,      pokemonGenderRatio);
                pokemonGenderRatio.addProperty(hasParts,            thePoke);
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

                pokemonType .addLabel("Type " +          typeLabel, "en");
                pokemonType .addProperty(hasValue,          typeLabel);
                thePoke     .addProperty(hasPokemonType,    pokemonType);
                pokemonType .addProperty(hasFacet,          typeString);
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

        OntClass kalosPokedex = theModel.createClass(wd + "Q55521408"); // kalos dex
        kalosPokedex.addLabel(pokedexRegionName + " Pokédex", "en");
        kalosPokedex.addProperty(firstAppearance, genVI);
        kalosPokedex.addSuperClass(pokedexCls);
        kalosPokedex.addProperty(catalogsRegion, KalosRegion);

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

                Individual newPokedexEntry    = kalosPokedex.createIndividual(new_pokedexStmt);

                newPokedexEntry.addLabel(pokedexRegionName + " Pokedex Entry " + new_pokedex_no, "en");
                newPokedexEntry   .addProperty(hasNumber,        new_pokedex_no);
                newPokedexEntry   .addProperty(partOf,           kalosPokedex);
                kalosPokedex      .addProperty(hasParts,         newPokedexEntry);
                thePoke           .addProperty(hasPokedexNumber, newPokedexEntry);

                newPokedexEntry     .addProperty(hasAlternatePokedexEntry, nationalPokedexEntry);
                nationalPokedexEntry.addProperty(hasAlternatePokedexEntry, newPokedexEntry);

            }
        }

        // get pokemons with mega evolutions

        pokedexFile = base + "pokemon mega forms.json";

        parser = new JSONParser();
        try { a = (JSONArray) parser.parse(new FileReader(pokedexFile)); }
        catch (Exception e) { System.out.println("OH NO . " + e.getMessage()); return; }

        for (Object o : a)
        {
            JSONObject pokemon = (JSONObject) o;

            String pokemonID         = (String) pokemon.get("pokemon");
            String pokemonLabel      = (String) pokemon.get("pokemonLabel");

            String megaPokemonID     = (String) pokemon.get("megapokemon");
            String megaPokemonLabel  = (String) pokemon.get("megapokemonLabel");

            String megaStoneID       = (String) pokemon.get("megaStone");
            String megaStoneLabel    = (String) pokemon.get("megaStoneLabel");

            Individual theMegaPoke = theModel.createIndividual(megaPokemonID,   pokemonCls);
            Individual thePoke     = theModel.createIndividual(pokemonID,       pokemonCls);
            Individual megaStone   = theModel.createIndividual(megaStoneID,     megaStoneCls);

            theMegaPoke .addLabel(megaPokemonLabel, "en");
            thePoke     .addProperty(hasMegaEvolution, theMegaPoke);
            megaStone   .addLabel("Item " + megaStoneLabel, "en");
            thePoke     .addProperty(uses,     megaStone);
            theMegaPoke .addProperty(uses,     megaStone);
            theMegaPoke .addProperty(from,     thePoke);

            theMegaPoke.addOntClass(megaEvolutionCls);

        }

    }

    public static void convertMovesGenVI()
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
            String theLabel        = moveTypeStmtLabel.replace("Pokémon move", "").replace("-type", "").trim();

            if(!JSON2RDF_PokeLabels.isInGenIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVIMoveList(moveLabel)) continue;

            Individual moveTypeIndividual   = pokemonMoveCls.createIndividual(moveTypeStmt); //the move type
            Individual pokemonMove          = pokemonMoveCls.createIndividual(moveID);

            moveTypeIndividual  .addOntClass(moveTypeCls);
            moveTypeIndividual  .addLabel(theLabel, "en");
            moveTypeIndividual  .addProperty(instanceOf,    pokemonMoveCls);
            moveTypeIndividual  .addProperty(hasValue,      moveTypeStmtLabel);
            pokemonMove         .addLabel(                  moveLabel, "en");
            pokemonMove         .addProperty(hasValue,      moveLabel);
            pokemonMove         .addProperty(hasMoveType,   moveTypeIndividual);
            pokemonMove         .addProperty(presentInWork, genVI);
            moveTypeIndividual  .addProperty(hasFacet,      theLabel);

            if(JSON2RDF_PokeLabels.isInGenVIMoveList(moveLabel))
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
            String theLabel                 = moveContestTypeStmtLabel.replace("move", "").trim();

            if(!JSON2RDF_PokeLabels.isInGenIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVIMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            Individual pokemonMove         = pokemonMoveCls.createIndividual(moveID);

            moveTypeIndividual  .addOntClass(moveContestCls);
            moveTypeIndividual  .addLabel(theLabel, "en");
            moveTypeIndividual  .addProperty(instanceOf,    pokemonMoveCls);
            moveTypeIndividual  .addProperty(hasValue,      moveContestTypeStmtLabel);
            pokemonMove         .addLabel(               moveLabel, "en");
            pokemonMove         .addProperty(hasValue,      moveLabel);
            pokemonMove         .addProperty(hasMoveType,   moveTypeIndividual);
            pokemonMove         .addProperty(presentInWork, genVI);
            moveTypeIndividual  .addProperty(hasFacet,      theLabel);

            if(JSON2RDF_PokeLabels.isInGenVIMoveList(moveLabel))
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
            String theLabel                 = moveContestTypeStmtLabel.replace("move", "").trim();

            if(!JSON2RDF_PokeLabels.isInGenIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVIMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual moveTypeIndividual   = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            Individual pokemonMove          = pokemonMoveCls.createIndividual(moveID);

            moveTypeIndividual  .addOntClass(moveDamageCls);
            moveTypeIndividual  .addLabel(theLabel, "en");
            moveTypeIndividual  .addProperty(instanceOf,    pokemonMoveCls);
            moveTypeIndividual  .addProperty(hasValue,      moveContestTypeStmtLabel);
            pokemonMove         .addLabel(                moveLabel, "en");
            pokemonMove         .addProperty(hasValue,      moveLabel);
            pokemonMove         .addProperty(hasMoveType,   moveTypeIndividual);
            pokemonMove         .addProperty(presentInWork, genVI);
            moveTypeIndividual  .addProperty(hasFacet,      theLabel);

            if(JSON2RDF_PokeLabels.isInGenVIMoveList(moveLabel))
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

            if(!JSON2RDF_PokeLabels.isGenVIAbility(pokemonAbilityLabel)) continue;

            Individual thePokeAbility = pokemonAbilityCls.createIndividual(pokemonAbilityID);
            thePokeAbility.addLabel("Ability " + pokemonAbilityLabel, "en");
            thePokeAbility.addProperty(hasValue, pokemonAbilityLabel);

            Individual thePoke = pokemonCls.createIndividual(pokemonID);
            thePoke.addLabel(pokemonLabel, "en");
            thePoke.addProperty(hasAbility, thePokeAbility);
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

            if(JSON2RDF_PokeLabels.isGenVIAbility(pokemonAbilityLabel))
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

            if(hasGenderDifference(pokemonLabel) && !pokemonLabel.contains("Nidoran"))
            {
                Individual malePokeVersion   = theModel.createIndividual(pokemonID+"_maleForm",   pokemonCls);
                Individual femalePokeVersion = theModel.createIndividual(pokemonID+"_femaleForm", pokemonCls);

                malePokeVersion.addLabel(pokemonLabel + " male form", "en");
                malePokeVersion.addOntClass(alternateForm);
                malePokeVersion.addProperty(firstAppearance, genIV);
                malePokeVersion.addProperty(hasAlternativeForm, thePoke);
                malePokeVersion.addProperty(hasGender, male);
                thePoke.addProperty(hasAlternativeForm, malePokeVersion);

                femalePokeVersion.addLabel(pokemonLabel + " female form", "en");
                femalePokeVersion.addProperty(firstAppearance, genIV);
                femalePokeVersion.addOntClass(alternateForm);
                femalePokeVersion.addProperty(hasGender, female);
                femalePokeVersion.addProperty(hasAlternativeForm, thePoke);
                thePoke.addProperty(hasAlternativeForm, femalePokeVersion);
            }

            if(isAlwaysFemale(pokemonLabel))
                thePoke.addProperty(hasGenderRatio, alwaysFemale);
            else if(isAlwaysMale(pokemonLabel))
                thePoke.addProperty(hasGenderRatio, alwaysMale);
            else if(isGenderless(pokemonLabel))
                thePoke.addProperty(hasGenderRatio, genderNeutral);

            ArrayList<String> alternateForms = getAlternateForms(pokemonLabel, "Generation VII");
            for(String alternativeForm : alternateForms)
            {
                Individual alternatePoke = alternateForm.createIndividual(alternativeForm.toLowerCase().replace(" ","_"));
                alternatePoke.addLabel(alternativeForm, "en");
                alternatePoke.addOntClass(pokemonCls);
                alternatePoke.addProperty(firstAppearance, genVII);
                thePoke.addProperty(hasAlternativeForm, alternatePoke);
                alternatePoke.addProperty(hasAlternativeForm, thePoke);

            }

            String evolFamilyStmt      = (String) pokemon.get("evolFamilyStmt");
            String evolFamilyStmtLabel = (String) pokemon.get("evolFamilyStmtLabel");

            boolean hasEvolutionaryFamily = false;
            if(evolFamilyStmtLabel != null && !evolFamilyStmtLabel.isEmpty() && evolFamilyStmt != null && !evolFamilyStmt.isEmpty())
            {
                Individual pokemonEvolutioanryFamily = pokemonEFamilyCls.createIndividual(evolFamilyStmt);
                pokemonEvolutioanryFamily.addLabel("EvolLine " + evolFamilyStmtLabel, "en");

                pokemonEvolutioanryFamily.addProperty(hasName, evolFamilyStmtLabel);
                thePoke.addProperty(inEvolFamily, pokemonEvolutioanryFamily);
                pokemonEvolutioanryFamily.addProperty(hasParts, thePoke);

                hasEvolutionaryFamily = true;
                String signatureAbilityLabel = getSignatureAbility(pokemonLabel, "Generation VII");
                if(signatureAbilityLabel!=null && !signatureAbilityLabel.isEmpty())
                {
                    String abilityCode = getAbilityCode(signatureAbilityLabel);
                    if(abilityCode!=null && !abilityCode.isEmpty())
                    {
                        Individual pokeAbilities = pokemonAbilityCls.createIndividual(abilityCode);
                        pokeAbilities.addLabel("Ability " + signatureAbilityLabel,"en");
                        pokemonEvolutioanryFamily.addProperty(hasSignatureAbility, pokeAbilities);
                        pokeAbilities.addProperty(isSignatureAbilityOf, pokemonEvolutioanryFamily);

                    }
                }
            }

            if(!hasEvolutionaryFamily)
            {
                String signatureAbilityLabel = getSignatureAbility(pokemonLabel, "Generation III");
                if(signatureAbilityLabel!=null && !signatureAbilityLabel.isEmpty())
                {
                    String abilityCode = getAbilityCode(signatureAbilityLabel);
                    if(abilityCode!=null && !abilityCode.isEmpty())
                    {
                        Individual pokeAbilities = pokemonAbilityCls.createIndividual(abilityCode);
                        pokeAbilities.addLabel(signatureAbilityLabel,"en");
                        thePoke.addProperty(hasSignatureAbility, pokeAbilities);
                        pokeAbilities.addProperty(isSignatureAbilityOf, thePoke);
                    }
                }
            }

            String eggGroupStmt      = (String) pokemon.get("eggGroupStmt");
            String eggGroupStmtLabel = (String) pokemon.get("eggGroupStmtLabel");

            if(eggGroupStmtLabel != null && !eggGroupStmtLabel.isEmpty() && eggGroupStmt != null && !eggGroupStmt.isEmpty())
            {
                Individual pokemonEggGroup = pokemonEggGroupCls.createIndividual(eggGroupStmt);

                pokemonEggGroup .addLabel("Egg Group " + eggGroupStmtLabel, "en");
                pokemonEggGroup .addProperty(hasName,    eggGroupStmtLabel);
                thePoke         .addProperty(hatches,    pokemonEggGroup);
                pokemonEggGroup .addProperty(hatches,    thePoke);
            }

            String genderRatioStmt      = (String) pokemon.get("genderRatioStmt");
            String genderRatioStmtLabel = (String) pokemon.get("grStmtLabel");

            if(genderRatioStmt != null && !genderRatioStmt.isEmpty() && genderRatioStmtLabel != null && !genderRatioStmtLabel.isEmpty())
            {
                Individual pokemonGenderRatio = pokemonGenderRatioCls.createIndividual(genderRatioStmt);

                pokemonGenderRatio  .addLabel("Gender Ratio " + genderRatioStmtLabel.replace("gender ratio", "").trim(), "en");
                pokemonGenderRatio  .addProperty(hasName,           genderRatioStmtLabel);
                thePoke             .addProperty(hasGenderRatio,    pokemonGenderRatio);
                pokemonGenderRatio  .addProperty(hasParts,          thePoke);
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
                pokemonType .addProperty(hasValue,          typeLabel);
                thePoke     .addProperty(hasPokemonType,    pokemonType);
                pokemonType .addProperty(hasFacet,          typeString);
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
        newPokedex.addProperty(catalogsRegion, AlolaRegion);

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

            String new_pokedex_no  = (String) pokemon.get("alolaDexNumber");
            String new_pokedexStmt = (String) pokemon.get("alolaStmt");

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

            //if alolan form is available, add that to the pokedex too
            HashMap<String, String> altForms = getAlternativeForms("alolan");
            for(Object s : altForms.keySet())
            {
                String altPokemonID    = (String) s;
                String altPokemonLabel = (String) altForms.get(altPokemonID);

                //pokemon has alternate form!
                if(altPokemonLabel.contains(pokemonLabel))
                {
                    Individual alternateForm = pokemonCls.createIndividual(altPokemonID);
                    alternateForm   .addLabel(altPokemonLabel,   "en");
                    alternateForm   .addProperty(from, AlolaRegion);
                    alternateForm   .addProperty(hasRegionalForm, thePoke);
                    thePoke         .addProperty(hasRegionalForm, alternateForm);
                    thePoke         .addProperty(presentInWork, genVII);
                    alternateForm   .addProperty(hasPokedexNumber, nationalPokedexEntry);
                    break;
                }
            }


            if(new_pokedex_no != null && !new_pokedex_no.isEmpty() && new_pokedexStmt != null && !new_pokedexStmt.isEmpty() )
            {
                try {
                    int dexno2     = Integer.parseInt(new_pokedex_no);
                    new_pokedex_no = String.format("%04d", dexno2);

                } catch(NumberFormatException e)
                {
                    new_pokedex_no = "Unknown";
                }

                Individual newPokedexEntry    = newPokedex.createIndividual(new_pokedexStmt);
                newPokedexEntry   .addLabel(pokedexRegionName + " Pokedex Entry " + new_pokedex_no, "en");
                newPokedexEntry   .addProperty(hasNumber,        new_pokedex_no);
                newPokedexEntry   .addProperty(partOf,           newPokedex);
                newPokedex        .addProperty(hasParts,         newPokedexEntry);
                thePoke           .addProperty(hasPokedexNumber, newPokedexEntry);

                newPokedexEntry  .addProperty(hasAlternatePokedexEntry,    nationalPokedexEntry);
                nationalPokedexEntry.addProperty(hasAlternatePokedexEntry, newPokedexEntry);
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
            String theLabel        = moveTypeStmtLabel.replace("Pokémon move", "").replace("-type", "").trim();

            if(!JSON2RDF_PokeLabels.isInGenIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVIIMoveList(moveLabel)) continue;

            Individual moveTypeIndividual    = pokemonMoveCls.createIndividual(moveTypeStmt); //the move type
            moveTypeIndividual.addOntClass(moveTypeCls);
            moveTypeIndividual.addLabel(theLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,    moveTypeStmtLabel);
            moveTypeIndividual.addProperty(hasFacet,    theLabel);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel( moveLabel, "en");
            pokemonMove.addProperty(hasValue,       moveLabel);
            pokemonMove.addProperty(hasMoveType,    moveTypeIndividual);
            pokemonMove.addProperty(presentInWork, genVII);

            if(JSON2RDF_PokeLabels.isInGenVIIMoveList(moveLabel))
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
            String theLabel               = moveContestTypeStmtLabel.replace("move", "").trim();

            if(!JSON2RDF_PokeLabels.isInGenIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVIIMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            moveTypeIndividual.addOntClass(moveContestCls);
            moveTypeIndividual.addLabel(theLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,    moveContestTypeStmtLabel);
            moveTypeIndividual.addProperty(hasFacet,    theLabel);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel(moveLabel, "en");
            pokemonMove.addProperty(hasValue,       moveLabel);
            pokemonMove.addProperty(hasMoveType,    moveTypeIndividual);
            pokemonMove.addProperty(presentInWork, genVII);

            if(JSON2RDF_PokeLabels.isInGenVIIMoveList(moveLabel))
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
            String theLabel               = moveContestTypeStmtLabel.replace("move", "").trim();

            if(!JSON2RDF_PokeLabels.isInGenIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVIIMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            moveTypeIndividual.addLabel(theLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,    moveContestTypeStmtLabel);
            moveTypeIndividual.addProperty(hasFacet,    theLabel);
            moveTypeIndividual.addOntClass(moveDamageCls);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel(moveLabel, "en");
            pokemonMove.addProperty(hasValue,       moveLabel);
            pokemonMove.addProperty(hasMoveType,    moveTypeIndividual);
            pokemonMove.addProperty(presentInWork, genVII);

            if(JSON2RDF_PokeLabels.isInGenVIIMoveList(moveLabel))
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

            if(JSON2RDF_PokeLabels.isGenVIIAbility(pokemonAbilityLabel))
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

                boolean hasEvolutionaryFamily = false;
                if(evolFamilyStmtLabel != null && !evolFamilyStmtLabel.isEmpty() && evolFamilyStmt != null && !evolFamilyStmt.isEmpty())
                {
                    Individual pokemonEvolutioanryFamily = pokemonEFamilyCls.createIndividual(evolFamilyStmt);
                    pokemonEvolutioanryFamily.addLabel("EvolLine " + evolFamilyStmtLabel, "en");

                    pokemonEvolutioanryFamily.addProperty(hasName,        evolFamilyStmtLabel);
                    thePoke                  .addProperty(inEvolFamily,   pokemonEvolutioanryFamily);
                    pokemonEvolutioanryFamily.addProperty(hasParts,       thePoke);

                    hasEvolutionaryFamily = true;
                    String signatureAbilityLabel = getSignatureAbility(pokemonLabel, "Generation VIII");
                    if(signatureAbilityLabel!=null && !signatureAbilityLabel.isEmpty())
                    {
                        String abilityCode = getAbilityCode(signatureAbilityLabel);
                        if(abilityCode!=null && !abilityCode.isEmpty())
                        {
                            Individual pokeAbilities = pokemonAbilityCls.createIndividual(abilityCode);
                            pokeAbilities.addLabel("Ability " + signatureAbilityLabel,"en");
                            pokemonEvolutioanryFamily.addProperty(hasSignatureAbility, pokeAbilities);
                            pokeAbilities.addProperty(isSignatureAbilityOf, pokemonEvolutioanryFamily);
                        }
                    }
                }

                if(!hasEvolutionaryFamily)
                {
                    String signatureAbilityLabel = getSignatureAbility(pokemonLabel, "Generation VIII");
                    if(signatureAbilityLabel!=null && !signatureAbilityLabel.isEmpty())
                    {
                        String abilityCode = getAbilityCode(signatureAbilityLabel);
                        if(abilityCode!=null && !abilityCode.isEmpty())
                        {
                            Individual pokeAbilities = pokemonAbilityCls.createIndividual(abilityCode);
                            pokeAbilities.addLabel(signatureAbilityLabel,"en");
                            thePoke.addProperty(hasSignatureAbility, pokeAbilities);
                            pokeAbilities.addProperty(isSignatureAbilityOf, thePoke);
                        }
                    }
                }

                if(hasGenderDifference(pokemonLabel) && !pokemonLabel.contains("Nidoran"))
                {
                    Individual malePokeVersion   = theModel.createIndividual(pokemonID+"_maleForm",   pokemonCls);
                    Individual femalePokeVersion = theModel.createIndividual(pokemonID+"_femaleForm", pokemonCls);

                    malePokeVersion.addLabel(pokemonLabel + " male form", "en");
                    malePokeVersion.addOntClass(alternateForm);
                    malePokeVersion.addProperty(firstAppearance, genIV);
                    malePokeVersion.addProperty(hasAlternativeForm, thePoke);
                    malePokeVersion.addProperty(hasGender, male);
                    thePoke.addProperty(hasAlternativeForm, malePokeVersion);

                    femalePokeVersion.addLabel(pokemonLabel + " female form", "en");
                    femalePokeVersion.addProperty(firstAppearance, genIV);
                    femalePokeVersion.addOntClass(alternateForm);
                    femalePokeVersion.addProperty(hasGender, female);
                    femalePokeVersion.addProperty(hasAlternativeForm, thePoke);
                    thePoke.addProperty(hasAlternativeForm, femalePokeVersion);
                }

                if(isAlwaysFemale(pokemonLabel))
                    thePoke.addProperty(hasGenderRatio, alwaysFemale);
                else if(isAlwaysMale(pokemonLabel))
                    thePoke.addProperty(hasGenderRatio, alwaysMale);
                else if(isGenderless(pokemonLabel))
                    thePoke.addProperty(hasGenderRatio, genderNeutral);

                ArrayList<String> alternateForms = getAlternateForms(pokemonLabel, "Generation VIII");
                for(String alternativeForm : alternateForms)
                {
                    Individual alternatePoke = alternateForm.createIndividual(alternativeForm.toLowerCase().replace(" ","_"));
                    alternatePoke.addLabel(alternativeForm, "en");
                    alternatePoke.addOntClass(pokemonCls);
                    alternatePoke.addProperty(firstAppearance, genVIII);
                    thePoke.addProperty(hasAlternativeForm, alternatePoke);
                }

                String eggGroupStmt      = (String) pokemon.get("eggGroupStmt");
                String eggGroupStmtLabel = (String) pokemon.get("eggGroupStmtLabel");

                if(eggGroupStmtLabel != null && !eggGroupStmtLabel.isEmpty() && eggGroupStmt != null && !eggGroupStmt.isEmpty())
                {
                    Individual pokemonEggGroup = pokemonEggGroupCls.createIndividual(eggGroupStmt);
                    pokemonEggGroup .addLabel("Egg Group " + eggGroupStmtLabel, "en");
                    pokemonEggGroup .addProperty(hasName,   eggGroupStmtLabel);
                    thePoke         .addProperty(hatches,    pokemonEggGroup);
                    pokemonEggGroup .addProperty(hatches,  thePoke);
                }

                String genderRatioStmt      = (String) pokemon.get("genderRatioStmt");
                String genderRatioStmtLabel = (String) pokemon.get("grStmtLabel");

                if(genderRatioStmt != null && !genderRatioStmt.isEmpty() && genderRatioStmtLabel != null && !genderRatioStmtLabel.isEmpty())
                {
                    Individual pokemonGenderRatio = pokemonGenderRatioCls.createIndividual(genderRatioStmt);
                    pokemonGenderRatio  .addLabel("Gender Ratio " + genderRatioStmtLabel.replace("gender ratio", "").trim(), "en");
                    pokemonGenderRatio  .addProperty(hasName,           genderRatioStmtLabel);
                    thePoke             .addProperty(hasGenderRatio,    pokemonGenderRatio);
                    pokemonGenderRatio  .addProperty(hasParts,          thePoke);
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
                    pokemonType .addProperty(hasValue,       typeLabel);
                    thePoke     .addProperty(hasPokemonType, pokemonType);
                    pokemonType .addProperty(hasFacet,       typeString);
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
        GalarPokedex.addProperty(catalogsRegion, GalarRegion);

        OntClass HisuiPokedex = theModel.createClass(wd + "Q111148855"); // hisui dex
        HisuiPokedex.addLabel("Hisui Pokédex", "en");
        HisuiPokedex.addProperty(firstAppearance, genVIII);
        HisuiPokedex.addProperty(catalogsRegion, HisuiRegion);

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

            HashMap<String, String> altForms = getAlternativeForms("galar");
            for(Object s : altForms.keySet())
            {
                String altPokemonID    = (String) s;
                String altPokemonLabel = (String) altForms.get(altPokemonID);

                //pokemon has alternate form!
                if(altPokemonLabel.contains(pokemonLabel))
                {
                    Individual alternateForm = pokemonCls.createIndividual(altPokemonID);
                    alternateForm   .addLabel(altPokemonLabel,   "en");
                    alternateForm   .addProperty(from, GalarRegion);
                    alternateForm   .addProperty(hasRegionalForm, thePoke);
                    thePoke         .addProperty(hasRegionalForm, alternateForm);
                    thePoke         .addProperty(presentInWork, genVIII);
                    alternateForm   .addProperty(hasPokedexNumber, nationalPokedexEntry);
                    break;
                }
            }

            altForms = getAlternativeForms("hisui");
            for(Object s : altForms.keySet())
            {
                String altPokemonID    = (String) s;
                String altPokemonLabel = (String) altForms.get(altPokemonID);

                //pokemon has alternate form!
                if(altPokemonLabel.contains(pokemonLabel))
                {
                    Individual alternateForm = pokemonCls.createIndividual(altPokemonID);
                    alternateForm   .addLabel(altPokemonLabel,   "en");
                    alternateForm   .addProperty(from, HisuiRegion);
                    alternateForm   .addProperty(hasRegionalForm, thePoke);
                    thePoke         .addProperty(hasRegionalForm, alternateForm);
                    thePoke         .addProperty(presentInWork, genVIII);
                    alternateForm   .addProperty(hasPokedexNumber, nationalPokedexEntry);
                    break;
                }
            }


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

                newPokedexEntry  .addProperty(hasAlternatePokedexEntry,    nationalPokedexEntry);
                nationalPokedexEntry.addProperty(hasAlternatePokedexEntry, newPokedexEntry);

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

                newPokedexEntry  .addProperty(hasAlternatePokedexEntry,    nationalPokedexEntry);
                nationalPokedexEntry.addProperty(hasAlternatePokedexEntry, newPokedexEntry);
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

            boolean isGen = JSON2RDF_PokeLabels.isInGenVIIIMoveList(moveLabel);

            if(!JSON2RDF_PokeLabels.isInGenIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVIIIMoveList(moveLabel)) continue;

            String theLabel = moveTypeStmtLabel.replace("Pokémon move", "").replace("-type", "").trim();

            Individual moveTypeIndividual    = pokemonMoveCls.createIndividual(moveTypeStmt); //the move type
            moveTypeIndividual.addLabel(theLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,    moveTypeStmtLabel);
            moveTypeIndividual.addProperty(hasFacet,    theLabel);
            moveTypeIndividual.addOntClass(moveTypeCls);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel(moveLabel, "en");
            pokemonMove.addProperty(hasValue,       moveLabel);
            pokemonMove.addProperty(hasMoveType,    moveTypeIndividual);
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

            boolean isGen = JSON2RDF_PokeLabels.isInGenVIIIMoveList(moveLabel);

            if(!JSON2RDF_PokeLabels.isInGenIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVIIIMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            String theLabel = moveContestTypeStmtLabel.replace("move", "").trim();

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            moveTypeIndividual.addLabel(theLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,    moveContestTypeStmtLabel);
            moveTypeIndividual.addProperty(hasFacet,    theLabel);
            moveTypeIndividual.addOntClass(moveContestCls);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel(moveLabel, "en");
            pokemonMove.addProperty(hasValue,       moveLabel);
            pokemonMove.addProperty(hasMoveType,    moveTypeIndividual);
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

            boolean isGen = JSON2RDF_PokeLabels.isInGenVIIIMoveList(moveLabel);

            if(!JSON2RDF_PokeLabels.isInGenIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIIIMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenIVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVMoveList(moveLabel)
                    && !JSON2RDF_PokeLabels.isInGenVIIMoveList(moveLabel)) continue;

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            String theLabel = moveContestTypeStmtLabel.replace("move", "").trim();

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            moveTypeIndividual.addLabel(theLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,    moveContestTypeStmtLabel);
            moveTypeIndividual.addProperty(hasFacet,    theLabel);
            moveTypeIndividual.addOntClass(moveDamageCls);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel(moveLabel, "en");
            pokemonMove.addProperty(hasValue,    moveLabel);
            pokemonMove.addProperty(hasMoveType, moveTypeIndividual);
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

            if(JSON2RDF_PokeLabels.isGenVIIIAbility(pokemonAbilityLabel))
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

            boolean hasEvolutionaryFamily = false;
            if(evolFamilyStmtLabel != null && !evolFamilyStmtLabel.isEmpty() && evolFamilyStmt != null && !evolFamilyStmt.isEmpty())
            {
                Individual pokemonEvolutioanryFamily = pokemonEFamilyCls.createIndividual(evolFamilyStmt);
                pokemonEvolutioanryFamily.addLabel("EvolLine " + evolFamilyStmtLabel, "en");
                pokemonEvolutioanryFamily.addProperty(hasName,        evolFamilyStmtLabel);
                thePoke                  .addProperty(inEvolFamily,   pokemonEvolutioanryFamily);
                pokemonEvolutioanryFamily.addProperty(hasParts,       thePoke);

                hasEvolutionaryFamily = true;
                String signatureAbilityLabel = getSignatureAbility(pokemonLabel, "Generation IX");
                if(signatureAbilityLabel!=null && !signatureAbilityLabel.isEmpty())
                {
                    String abilityCode = getAbilityCode(signatureAbilityLabel);
                    if(abilityCode!=null && !abilityCode.isEmpty())
                    {
                        Individual pokeAbilities = pokemonAbilityCls.createIndividual(abilityCode);
                        pokeAbilities.addLabel("Ability " + signatureAbilityLabel,"en");
                        pokemonEvolutioanryFamily.addProperty(hasSignatureAbility, pokeAbilities);
                        pokeAbilities.addProperty(isSignatureAbilityOf, pokemonEvolutioanryFamily);
                    }
                }
            }

            if(!hasEvolutionaryFamily)
            {
                String signatureAbilityLabel = getSignatureAbility(pokemonLabel, "Generation IX");
                if(signatureAbilityLabel!=null && !signatureAbilityLabel.isEmpty())
                {
                    String abilityCode = getAbilityCode(signatureAbilityLabel);
                    if(abilityCode!=null && !abilityCode.isEmpty())
                    {
                        Individual pokeAbilities = pokemonAbilityCls.createIndividual(abilityCode);
                        pokeAbilities.addLabel(signatureAbilityLabel,"en");
                        thePoke.addProperty(hasSignatureAbility, pokeAbilities);
                        pokeAbilities.addProperty(isSignatureAbilityOf, thePoke);
                    }
                }
            }
            if(hasGenderDifference(pokemonLabel) && !pokemonLabel.contains("Nidoran"))
            {
                Individual malePokeVersion   = theModel.createIndividual(pokemonID+"_maleForm",   pokemonCls);
                Individual femalePokeVersion = theModel.createIndividual(pokemonID+"_femaleForm", pokemonCls);

                malePokeVersion.addLabel(pokemonLabel + " male form", "en");
                malePokeVersion.addOntClass(alternateForm);
                malePokeVersion.addProperty(firstAppearance, genIV);
                malePokeVersion.addProperty(hasAlternativeForm, thePoke);
                malePokeVersion.addProperty(hasGender, male);
                thePoke.addProperty(hasAlternativeForm, malePokeVersion);

                femalePokeVersion.addLabel(pokemonLabel + " female form", "en");
                femalePokeVersion.addProperty(firstAppearance, genIV);
                femalePokeVersion.addOntClass(alternateForm);
                femalePokeVersion.addProperty(hasGender, female);
                femalePokeVersion.addProperty(hasAlternativeForm, thePoke);
                thePoke.addProperty(hasAlternativeForm, femalePokeVersion);
            }

            if(isAlwaysFemale(pokemonLabel))
                thePoke.addProperty(hasGenderRatio, alwaysFemale);
            else if(isAlwaysMale(pokemonLabel))
                thePoke.addProperty(hasGenderRatio, alwaysMale);
            else if(isGenderless(pokemonLabel))
                thePoke.addProperty(hasGenderRatio, genderNeutral);

            ArrayList<String> alternateForms = getAlternateForms(pokemonLabel, "Generation IX");
            for(String alternativeForm : alternateForms)
            {
                Individual alternatePoke = alternateForm.createIndividual(alternativeForm.toLowerCase().replace(" ","_"));
                alternatePoke.addLabel(alternativeForm, "en");
                alternatePoke.addOntClass(pokemonCls);
                alternatePoke.addProperty(firstAppearance, genIX);
                thePoke.addProperty(hasAlternativeForm, alternatePoke);
                alternatePoke.addProperty(hasAlternativeForm, thePoke);
            }

            String eggGroupStmt      = (String) pokemon.get("eggGroupStmt");
            String eggGroupStmtLabel = (String) pokemon.get("eggGroupStmtLabel");

            if(eggGroupStmtLabel != null && !eggGroupStmtLabel.isEmpty() && eggGroupStmt != null && !eggGroupStmt.isEmpty())
            {
                Individual pokemonEggGroup = pokemonEggGroupCls.createIndividual(eggGroupStmt);
                pokemonEggGroup .addLabel("Egg Group " + eggGroupStmtLabel, "en");
                pokemonEggGroup .addProperty(hasName,   eggGroupStmtLabel);
                thePoke         .addProperty(hatches,   pokemonEggGroup);
                pokemonEggGroup .addProperty(hatches,   thePoke);
            }

            String genderRatioStmt      = (String) pokemon.get("genderRatioStmt");
            String genderRatioStmtLabel = (String) pokemon.get("grStmtLabel");

            if(genderRatioStmt != null && !genderRatioStmt.isEmpty() && genderRatioStmtLabel != null && !genderRatioStmtLabel.isEmpty())
            {
                Individual pokemonGenderRatio = pokemonGenderRatioCls.createIndividual(genderRatioStmt);
                pokemonGenderRatio  .addLabel("Gender Ratio " + genderRatioStmtLabel.replace("gender ratio", "").trim(), "en");
                pokemonGenderRatio  .addProperty(hasName,           genderRatioStmtLabel);
                thePoke             .addProperty(hasGenderRatio,    pokemonGenderRatio);
                pokemonGenderRatio  .addProperty(hasParts,          thePoke);
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
                pokemonType .addProperty(hasValue,          typeLabel);
                thePoke     .addProperty(hasPokemonType,    pokemonType);
                pokemonType .addProperty(hasFacet,          typeString);
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
        PaldeaPokedex.addProperty(catalogsRegion, PaldeaRegion);
        PaldeaPokedex.addSuperClass(pokedexCls);

        OntClass KitakamiPokedex = theModel.createClass(wd + "Q122674304"); // kitakami dex
        KitakamiPokedex.addLabel("Kitakami Pokédex", "en");
        KitakamiPokedex.addProperty(firstAppearance, genIX);
        KitakamiPokedex.addProperty(catalogsRegion, KitakamiRegion);
        KitakamiPokedex.addSuperClass(pokedexCls);

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

            HashMap<String, String> altForms = getAlternativeForms("paldea");
            for(Object s : altForms.keySet())
            {
                String altPokemonID    = (String) s;
                String altPokemonLabel = (String) altForms.get(altPokemonID);

                //pokemon has alternate form!
                if(altPokemonLabel.contains(pokemonLabel))
                {
                    Individual alternateForm = pokemonCls.createIndividual(altPokemonID);
                    alternateForm   .addLabel(altPokemonLabel,   "en");
                    alternateForm   .addProperty(from, PaldeaRegion);
                    alternateForm   .addProperty(hasRegionalForm, thePoke);
                    thePoke         .addProperty(hasRegionalForm, alternateForm);
                    thePoke         .addProperty(presentInWork, genIX);
                    alternateForm   .addProperty(hasPokedexNumber, nationalPokedexEntry);
                    break;
                }
            }



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

                newPokedexEntry     .addProperty(hasAlternatePokedexEntry, nationalPokedexEntry);
                nationalPokedexEntry.addProperty(hasAlternatePokedexEntry, newPokedexEntry);
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

                newPokedexEntry  .addProperty(hasAlternatePokedexEntry,    nationalPokedexEntry);
                nationalPokedexEntry.addProperty(hasAlternatePokedexEntry, newPokedexEntry);
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
            String theLabel        = moveTypeStmtLabel.replace("Pokémon move", "").replace("-type", "").trim();

            boolean isGen = JSON2RDF_PokeLabels.isInGenIXMoveList(moveLabel);

            Individual moveTypeIndividual    = pokemonMoveCls.createIndividual(moveTypeStmt); //the move type
            moveTypeIndividual.addLabel(theLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,    moveTypeStmtLabel);
            moveTypeIndividual.addProperty(hasFacet,    theLabel);
            moveTypeIndividual.addOntClass(moveTypeCls);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel(                 moveLabel, "en");
            pokemonMove.addProperty(hasValue,       moveLabel);
            pokemonMove.addProperty(hasMoveType,    moveTypeIndividual);
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
            String theLabel                 = moveContestTypeStmtLabel.replace("move", "").trim();

            boolean isGen = JSON2RDF_PokeLabels.isInGenIXMoveList(moveLabel);

            if(moveContestTypeStmt == null || moveContestTypeStmtLabel == null ) continue;

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveContestTypeStmt); //the move type
            moveTypeIndividual.addLabel(theLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,    moveContestTypeStmtLabel);
            moveTypeIndividual.addProperty(hasFacet,    theLabel);
            moveTypeIndividual.addOntClass(moveContestCls);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel(                   moveLabel, "en");
            pokemonMove.addProperty(hasValue,       moveLabel);
            pokemonMove.addProperty(hasMoveType,    moveTypeIndividual);
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

            String moveID                    = (String) pokemon.get("pokeMove");
            String moveLabel                 = (String) pokemon.get("pokeMoveLabel");
            String moveCategoryTypeStmt      = (String) pokemon.get("damageCategoryMoveStmt");
            String moveCategoryTypeStmtLabel = (String) pokemon.get("damageCategoryMoveStmtLabel");
            String theLabel                = moveCategoryTypeStmtLabel.replace("move", "").trim();

            boolean isGen = JSON2RDF_PokeLabels.isInGenIXMoveList(moveLabel);

            if(moveCategoryTypeStmt == null || moveCategoryTypeStmtLabel == null ) continue;

            Individual moveTypeIndividual  = pokemonMoveCls.createIndividual(moveCategoryTypeStmt); //the move type
            moveTypeIndividual.addLabel   (theLabel, "en");
            moveTypeIndividual.addProperty(instanceOf,  pokemonMoveCls);
            moveTypeIndividual.addProperty(hasValue,    moveCategoryTypeStmtLabel);
            moveTypeIndividual.addProperty(hasFacet,    theLabel);
            moveTypeIndividual.addOntClass(moveDamageCls);

            Individual pokemonMove = pokemonMoveCls.createIndividual(moveID);
            pokemonMove.addLabel(                   moveLabel, "en");
            pokemonMove.addProperty(hasValue,       moveLabel);
            pokemonMove.addProperty(hasMoveType,    moveTypeIndividual);
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

            if(JSON2RDF_PokeLabels.isGenIXAbility(pokemonAbilityLabel))
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
