## Configuration

Configuration file is a JSON file at `config/milos-fast-xp.json`. If any
parameter is missing, its default value is used. If the file is missing or
unreadable, it is rewritten with the defaults.

1. ##### `xpCreationEnabled`
    * Master switch for the XP Creation Speed feature.
    * Default value: `true`
1. ##### `xpCreationMultiplier`
    * Multiplies the XP amount granted by every orb-spawning event (mob
      kills, smelting, XP bottles, fishing). `1` reproduces vanilla exactly.
    * Range: `1`\-`10`
    * Default value: `1`
1. ##### `xpPickupEnabled`
    * Master switch for the XP Pickup Speed feature.
    * Default value: `true`
1. ##### `xpPickupOrbsPerTick`
    * How many nearby XP orbs are collected in a single tick. Note that no
      value reproduces vanilla pickup timing: this feature replaces vanilla's
      pickup handling entirely, including its two-tick pickup delay (vanilla
      collects one orb every *two* ticks), so pickup happens every tick and
      even the minimum of `1` is about twice as fast as vanilla. Only the XP
      *amount* per orb is untouched here — that is the `xpCreationMultiplier`
      slider's job.
    * Range: `1`\-`16`
    * Default value: `1`
1. ##### `autoThrowEnabled`
    * Master switch for the Auto-Throw Projectiles feature.
    * Default value: `true`
1. ##### `autoThrowDelayTicks`
    * How many ticks between automatic throws while right-click is held on
      a whitelisted item.
    * Range: `1`\-`20`
    * Default value: `2`
1. ##### `autoThrowProjectiles`
    * List of item ids that trigger auto-throw when held and right-clicked.
    * Default value: `["minecraft:snowball", "minecraft:egg", "minecraft:ender_pearl", "minecraft:splash_potion", "minecraft:lingering_potion", "minecraft:trident", "minecraft:experience_bottle"]`
