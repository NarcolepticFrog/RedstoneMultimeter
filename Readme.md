# Redstone Multimeter Mod

This is a minecraft 1.12.2 mod that lets you record when blocks are powered with gametick accuracy.
The idea is that it can be a useful debugging and design tool for redstone contraptions.
The mod lets you place virtual "meters" in the world which record the power level of a block for the last 60 game ticks.
This information is displayed using a simple GUI and can be paused for careful inspection.

Here's a short video explaining the mod and showing some examples of how to use it: https://youtu.be/RdrHvInW0O4.

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