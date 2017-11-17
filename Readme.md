# Redstone Multimeter Mod

## Overview and Goals

This is a Minecraft 1.12.2 mod that lets you easily record and view when blocks
are powered. The player can place virtual "meters" in the world. Each meter
records whether it was powered or not on each gametick, and a summary of the log
is displayed as an overlay for the player. The mod also correctly records the
order that blocks become powered/unpowered within a single game tick. This makes
this mod a great tool for getting into technical redstone that requires correct
scheduling of events within a single gametick.

When a meter is placed on a block that gets pushed by a piston, the meter moves
too. This makes it possible to

## Installation Instructions

This mod uses the [liteloader mod loader](http://www.liteloader.com/). Note that
liteloader *is compatible with both forge and optifine.* To install forge and
liteloader together, first install forge. When you install liteloader, you can
choose which profile to extend from. Choose to extend from the forge profile.
This will create a new profile that runs both forge and liteloader.

Download the most recent version of the mod from
[here](https://github.com/NarcolepticFrog/RedstoneMultimeter/releases) and place
the redstone multimeter mod in the `minecraft/mods/1.12.2/` directory.

## The User Interface

The following graphic shows the main components of the Redstone Multimeter user
interface. You can add or remove a meter by looking at a block and pressing the
`Toggle Meter` keybinding (default `m`). You can pause/unpause the meters by
pressing the `Pause Meters` keybinding (default `n`). While paused, you can
scroll forwards and backwards through time by pressing the `Step Backward` and
`Step Forward` keybindings (default `,` and `.`, respectively).

![User Interface Overview](figures/UIOverview.png?raw=true)

- Each meter gets its own row in the UI, showing the meter name and a summary of that meter's power level for the last 60 gameticks.
- Each meter also has a corresponding 'highlight' showing which block the meter is monitoring. The color of the highlight matches the color of the corresponding row.
- For pulses that last longer than 5 gameticks, the duration of the pulse is also shown textually. This number is the *number of gameticks for which the meter was powered at the start*.
- When the meters are paused, the subtick ordering of any powering/unpowering events is shown to the right of the overview. Green and red rectangles correspond to the meter becoming powered or unpowered, respectively. 

## Keybindings:

- `Toggle Meter` Adds or removes a meter at the block the player is looking at.
- `Pause Meters` Pauses the meters for easy inspection.
- `Step Forward` While paused, move the display 10 ticks ahead in time.
- `Step Backward` While paused, move the display 10 ticks back in time.

## Available Commands:

- `/meter name <name>` renames the most recently placed meter.
- `/meter name <i> <name>` renames the `i`th meter (starting from 0 at the top).
- `/meter removeAll` removes all meters from the world.
- `/meter duration <duration>` sets the number of ticks shown on the overlay.
