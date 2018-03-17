# Redstone Multimeter Mod

## Overview and Goals

This is a Minecraft 1.12, 1.12.1, and 1.12.2 mod that lets you easily record and view when blocks are powered. The player can place virtual "meters" in the world. Each meter records whether it was powered or not on each gametick, and a summary of the log is displayed as an overlay for the player. The mod also correctly records the order that blocks become powered/unpowered within a single game tick (i.e., the update order). This makes this mod a great tool for getting into technical redstone that requires correct scheduling of events within a single gametick.

When a meter is placed on a block that gets pushed by a piston, the meter moves too. This makes it possible to use meters in mechanisms that move, like flying machines.

## Installation Instructions

This mod uses the [liteloader mod loader](http://www.liteloader.com/). Note that liteloader *is compatible with both forge and optifine.* To install forge and liteloader together, first install forge. When you install liteloader, you can choose which profile to extend from. Choose to extend from the forge profile. This will create a new profile that runs both forge and liteloader.

Download the most recent version of the mod from [here](https://github.com/NarcolepticFrog/RedstoneMultimeter/releases) and place the redstone multimeter mod in the `minecraft/mods/1.12.2/` directory (or `mods/1.12` or `mods/1.12.1`).

## Multiplayer Support

As of version 0.4, the Redstone Multiplayer mod has multiplayer support, enabling you to debug and experiment with friends! The necessary server-side modifications are included in the latest version of carpet mod. You can get the latest "snapshotty" version of carpet mod from the `#mods` channel on the SciCraft discord channel (https://discord.gg/4qaMFUN). To enable the redstone multimeter mod support in carpet, you will need to set the carpet rule `redstoneMultimeter` to `true` (e.g., by executing `/carpet redstoneMultimeter true`). See the Meter Groups section for more information about organizing meters in multiplayer.

## The User Interface

The following graphic shows the main components of the Redstone Multimeter user interface. You can add or remove a meter by looking at a block and pressing the `Toggle Meter` keybinding (default `m`). Holding control while placing a meter makes it unmovable (unmovable meters do not render a wireframe cube). You can pause/unpause the meters by pressing the `Pause Meters` keybinding (default `n`). While paused, you can scroll forwards and backwards through time by pressing the `Step Backward` and `Step Forward` keybindings (default `,` and `.`, respectively). Holding control while stepping forwards or backwards jumps 10 game ticks at a time.

![User Interface Overview](figures/UIOverview.png?raw=true)

- Each meter gets its own row in the UI, showing the meter name and a summary of that meter's power level for the last 60 gameticks.
- Each meter also has a corresponding 'highlight' showing which block the meter is monitoring. The color of the highlight matches the color of the corresponding row.
- For pulses that last longer than 5 gameticks, the duration of the pulse is also shown textually. This number is the *number of gameticks for which the meter was powered at the start*.
- When the meters are paused, the subtick ordering of any powering/unpowering events is shown to the right of the overview. Green and red rectangles correspond to the meter becoming powered or unpowered, respectively.
- If the meter is pushed by a piston, a horizontal line is drawn in the overlay to show that the block moved.

## Meter Groups

Meters can be organized into Meter Groups. Each meter group consists of a collection of meters in the world, together with a name. Each player is subscribed to exactly one meter group at a time, and the `Toggle Meter` keybinding, as well as the `/meter` command, both affect the player's current meter group. To subcribe to (or create) a new meter group, use the `/meter group <groupName>` command. To get a list of the current meter groups, use the `/meter listGroups` command.

Meter groups are especially helpful on multiplayer servers. Multiple players can subscribe to the same meter group so that they see the same meters. By default, when a player connects to a server for the first time, they are subscribed to a new meter group with the same name as the player.

## Keybindings:

- `Toggle Meter` Adds or removes a meter at the block the player is looking at.
- `Pause Meters` Pauses the meters for easy inspection.
- `Step Forward` While paused, move the display 1 tick ahead in time. Holding ctrl moves 10 ticks.
- `Step Backward` While paused, move the display 1 tick back in time. Holding ctrl moves 10 ticks.

## Available Commands:

- `/meter name <name>` renames the most recently placed meter.
- `/meter name <i> <name>` renames the `i`th meter (starting from 0 at the top).
- `/meter color <#RRGGBB>` changes the color of the most recently placed meter.
- `/meter color <i> <#RRGGBB>` changes the color of the `i`th meter (starting from 0 at the top).
- `/meter removeAll` removes all meters from the world.
- `/meter group <groupName>` subscribe to a new meter group.
- `/meter listGroups` lists the current set of meter groups.
