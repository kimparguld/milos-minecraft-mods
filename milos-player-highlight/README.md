# Milo's Player Highlight

Minecraft mod that highlights specific players whenever they're online on
the same server. Each tracked player gets a glowing outline, visible
through walls, in a color you choose.

With [Xaero's Minimap](https://modrinth.com/mod/xaeros-minimap) installed,
tracked players also show up as a dot in the same color on the minimap
radar. Without it, the mod still works and only draws the outline.

## Tracking players

Open the config screen (through Mod Menu, if installed) and use
**+ Add player** to add a row. Each row takes a player name (not
case-sensitive) and a color. The list is saved to
`config/milos-player-highlight.json`.

This mod is client-side only and made for
[Fabric Loader](https://fabricmc.net/use/ "Fabric"). It requires
[ukulib](https://modrinth.com/mod/ukulib).
