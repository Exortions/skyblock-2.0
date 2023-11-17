WE ARE NOT PROVIDING COMPILED JARS - Compile it yourself using the instructions. If the build fails, make an issue with screenshots. Any dry issues like "my build failed" will receive no assistance.

# Credit
Made by Exortions, OptimusChen, and UltraBlackLinux

Used mob loot tables from the BlueCommander Hypixel Skyblock Remake

Used Skyblock item JSON files from the NotEnoughItems Repository

# Work in Progress

Please note that this project is a work in progress - there are still many bugs, and many features are still not
implemented. This version of skyblock very difficult to progress in until we release new features, so stay tuned!

# Skyblock

This is a Hypixel Skyblock recreation of Skyblock before the Dungeons update. After we complete this, we will start
adding features after Dungeons.

# Features

Here are a list of some features that we currently have added:

    - Auction House
    - Slayer
    - Fairy Souls
    - Crystals
    - Private Islands
    - Mining System
    - Crafting System
    - All items before dungeons
    - Bags (Accessory bag, etc.)
    - Enchantments
    - Reforges
    - Merchants
    - Locations
    - Collections
    - Custom Mobs
    - Launch Pads
    - Minions
    - Quests
    - Pets
    - Economy
    - Custom Mob Spawning
    - Dragons

# Coming Soon

Here is a list of features that will be added soon:

    - Bazaar

# Installation

To install this plugin, you must have a Spigot server running 1.8.8. You can download the latest version of Spigot here:
https://www.spigotmc.org/wiki/buildtools/

Once you have a Spigot server running, compile the plugin using Maven:

`$ git clone https://github.com/Exortions/skyblock-2.0`

`$ cd skyblock-2.0`

`$ mvn package`

Make sure that you are in the root directory of the project while executing these commands.

Once you have compiled the plugin, you can find the jar file in the `target` folder. Copy this jar file into your server's `plugins` folder.

Create a new directory called `Skyblock` in your server's `plugins` folder. This is where all the plugin's data will be stored.

It is important that you drag the `items` and `mobLoot` folder from the `dependencies/Skyblock` (https://github.com/Exortions/skyblock-2.0/tree/master/dependencies/Skyblock) into your server's `plugins/Skyblock` folder.

Add all plugins in the `dependencies/Plugins` folder to your server's `plugins` folder.

Drag the `world` and `deep_caverns` worls from the `dependencies` folder into your server's main folder.

Once this is done, your server file structure should look something like this:
```
deep_caverns
plugins
├── Skyblock
│   ├── items
│   └── mobLoot
├── ActionBarAPI-1.15.4.jar
├── ArmorEventPlugin.jar
├── Citizens-2.0.30-b2765.jar
├── item-nbt-api-plugin-2.6.0.jar
├── skyblock-<version>.jar
├── worldedit-bukkit-6.1.jar
world
server.jar
```

When running your server, make sure to have at least 4GB of RAM allocated to it. This is because the plugin loads a lot of data (over 1,200 items) into memory.
It is recommended to have 8GB of RAM allocated to your server for the best performance.

`java -Xmx4096M -Xms4096M -jar server.jar`
