# Campfire Keeps Food

Stop chasing your food around the campfire.

In vanilla, a campfire throws food off the moment it is cooked. With Campfire Keeps Food, cooked food stays on the campfire, right where it was cooking, until you come and take it with an empty hand.

The mod runs on the server only. Players join with a plain vanilla client and still see the food on the campfire.

Available for NeoForge, Forge and Fabric.

## Features

- **Food stays put.** When food finishes cooking, the result stays in its spot on the campfire and is not cooked again.
- **Take it all at once.** Right-click the campfire with an empty main hand to collect every cooked item on it.
- **Campfires and soul campfires.** Both work the same way.
- **Saved with the world.** Cooked food on a campfire survives restarts and chunk reloads.
- **Cooking in stages (optional).** Recipes from mods or datapacks that turn one result into the next can cook on the same campfire, stage after stage, until the final result stays. Off by default, see [Configuration](#configuration).

The mod has one small server config file.

## How it works

- **Cooking.** Food cooks as in vanilla, with the same recipes and times. When it is done, the result stays in that slot and looks like food that is still cooking, including the smoke the game draws above anything lying on a lit campfire.
- **Taking food.** Right-click with an empty main hand. If at least one cooked item is on the campfire, all cooked items go into your inventory, you hear the item pickup sound and your arm swings. Food that is still raw stays and keeps cooking. If nothing is cooked yet, the click does what it does in vanilla.
- **Full inventory.** Whatever does not fit is dropped near you.
- **Off hand.** Hands are used in the vanilla order, main hand first. An item in your off hand still does its normal action on the same click, so raw food in your off hand goes onto the campfire right after you took the cooked food.
- **Sneaking.** As in vanilla, sneaking while holding something in either hand skips the campfire. Sneaking with both hands empty still takes the food.
- **Slots.** A campfire has four slots and cooked food keeps its slot. New food can only go into an empty slot, so take the cooked food to make room.
- **Results that can be cooked again.** If another mod or a datapack has a campfire recipe for the result, that result is thrown off the campfire as in vanilla, so you can put it back on. This is the default; with `cookInStages = true` it stays and cooks again instead.
- **Cooking in stages.** With `cookInStages = true`, a result that has a campfire recipe stays in its slot and starts cooking again right away, with the cooking time of its own recipe. This repeats until a result has no campfire recipe or the item reaches `maxStages`; that last result stays as cooked food. A chain that loops back on itself stops the same way.
- **Taking food between stages.** An empty-hand click also takes results that are cooking their next stage, together with the cooked food. Their cooking progress is lost. Food you put on the campfire yourself is never taken before its first stage is done, as in vanilla.

What stays vanilla:

- **Breaking the campfire** drops everything on it, cooked or not.
- **Putting the fire out** with a shovel, water or a splash potion removes nothing. Lighting it again continues where it was, and cooked food can be taken from an unlit campfire too.
- **Hoppers** do not interact with campfires.
- **Fire damage, smoke, signal smoke, recipes and cooking times** are unchanged. Campfires give no experience, as in vanilla.

## Requirements

| | Forge / NeoForge | Fabric |
|---|---|---|
| Minecraft | 1.20.1 | 1.20 or 1.20.1 |
| Loader | Forge 47.1.0 or newer, or NeoForge 1.20.1 (47.1.x) | Fabric Loader 0.14.21 or newer |
| Jar | `campfirekeepsfood-forge-1.20.1-<version>.jar` | `campfirekeepsfood-fabric-1.20-1.20.1-<version>.jar` |

On Minecraft 1.20.1, NeoForge still runs Forge mods, so the Forge jar is used for both.

The Fabric version does not need Fabric API.

### Client and server

Install the mod on the server only, on every loader. It adds no items, blocks, entities, sounds, network messages or registries, so the server has nothing a client needs to know about:

- **Forge and NeoForge:** vanilla clients and Forge or NeoForge clients without the mod can join. The mod is marked as server-only, so a client without it still shows the server as compatible in the server list.
- **Fabric:** vanilla clients and Fabric clients without the mod can join.

Players without the mod see the cooked food on the campfire like everyone else. Installing it on a client as well does no harm.

In singleplayer, install it in your own game; the built-in server runs it.

## Configuration

### Server: `config/campfirekeepsfood-server.toml`

Created with comments on the first server or world start:

```toml
# Cook results that still have a campfire recipe again on the same campfire, stage after stage,
# until a result has no campfire recipe left. False throws such results off, as in vanilla.
cookInStages = false

# Most cooking stages per item placed on the campfire, counting the first one. Whole number from 1 to 64.
# The result of the last stage stays on the campfire as cooked food. Only used when cookInStages is true.
maxStages = 8
```

- With `cookInStages = false` the mod works exactly as before stages existed, and `maxStages` is ignored.
- Vanilla Minecraft has no campfire recipe whose result can be cooked again. Stages only matter with mods or datapacks that add such recipes.
- With `maxStages = 1`, every first result stays as cooked food, even if it has another campfire recipe.
- The stage of each slot is saved with the campfire, so cooking continues after a restart.
- Changes take effect after a server restart, or after closing and reopening a singleplayer world.
- Mistakes do not crash the server. An invalid value uses the default for that setting and is logged as a warning. If the file cannot be read at all (for example a TOML syntax error), all defaults are used, the file is left untouched and the log says why.

## Removing the mod

The world opens normally. Cooked food is stored in the campfire's normal slots; the mod only adds the stage of each slot to the campfire's data, which vanilla ignores. Without the mod, vanilla throws anything off a burning campfire when it finishes "cooking", and items that have no recipe finish right away, so cooked food pops off a lit campfire as soon as the campfire ticks. On an unlit campfire it stays until you light the campfire again or break it.

## Compatibility

- Mods that change how campfires cook, or that replace the campfire's block entity, can conflict with this mod. Campfires from other mods are covered only when they use the vanilla campfire block and block entity code.
- Mods that act on right-clicking a campfire can conflict. A mod that cancels the click first, such as a protection mod, also stops the food from being taken.
- Mods that read the contents of a campfire see cooked food as an item in a slot.

## Building from source

```
./gradlew build
```

The jars are written to `forge/build/libs/` and `fabric/build/libs/`.

## License

MIT.

The Fabric jar bundles unmodified NightConfig core and toml 3.8.0 under LGPL-3.0, with notice, source links and LGPL/GPL texts in `META-INF/`. NeoForge and Forge use their loader-provided NightConfig libraries.
