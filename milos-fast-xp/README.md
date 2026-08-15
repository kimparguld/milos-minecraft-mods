# Milo's Fast XP

Minecraft mod that speeds up how fast you gain and pick up XP, with an optional
auto-throw for projectiles.

Merges two mods: `fast-xp` by Shikaru (whose actual feature is auto-throwing
held projectiles, not XP) and `fastexppickup` by Donbarz (batched XP orb
pickup). Merged, extended with a new XP-creation-speed multiplier, and
maintained by Milo.

Three independently toggleable features, each with its own slider, configurable
in-game through the Mods menu (or by hand in
[`config/milos-fast-xp.json`](./config.md)):

- **XP Creation Speed** — multiplies the XP granted by every orb-spawning
  event (mob kills, smelting, XP bottles, fishing). A multiplier of `1`
  reproduces vanilla exactly.
- **XP Pickup Speed** — collect several nearby XP orbs per tick. This
  replaces vanilla's pickup handling, including its two-tick pickup delay
  (vanilla collects one orb every *two* ticks), so pickup runs every tick and
  even the minimum setting of `1` is about twice as fast as vanilla; there is
  no setting that reproduces vanilla pickup timing exactly.
- **Auto-Throw Projectiles** — hold right-click on a whitelisted throwable
  (snowballs, eggs, ender pearls, tridents, potions, XP bottles) to
  auto-throw it repeatedly instead of clicking each time.

This mod is made for [Fabric Loader](https://fabricmc.net/use/) and requires
[Fabric API](https://www.curseforge.com/minecraft/mc-mods/fabric-api).
[ukulib](https://modrinth.com/mod/ukulib) is recommended but optional: it is a
client-only mod that provides the in-game config screen, and without it the
mod still works and reads the same config file, just without the screen. If
[Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu) is installed
alongside ukulib, the config screen is reachable from the Mods menu.

**Multiplayer note:** XP Creation Speed and XP Pickup Speed run on the
logical server. In singleplayer this is automatic, but on a dedicated server
the mod needs installing server-side too for those two features to apply.

**Server opt-out:** the mod provides a wire protocol server operators can use
to disable Auto-Throw for individual connected players — a client that joins
announces itself on the `milos-fast-xp:join` channel, and the server may reply
to that player with a `milos-fast-xp:opt_out` packet, which disables
Auto-Throw for them until they reconnect. The mod ships no policy of its own
here; sending that packet is up to server-side tooling.
