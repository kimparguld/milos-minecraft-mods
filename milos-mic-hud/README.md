# Milo's Mic HUD

Minecraft mod that shows a small HUD icon for your
[Simple Voice Chat](https://modrinth.com/mod/simple-voice-chat) microphone
state: unmuted, muted, disabled, or disconnected.

Simple Voice Chat is required for the icon to show anything — without it
installed, the mod loads normally but stays invisible, since there is no
mic state to report. Position, anchor, and icon size can be adjusted via
the config screen (through Mod Menu, if installed) or the config file.

## Custom icons

Each state's icon can be replaced with your own PNG by dropping a file
into `config/milos-mic-hud/icons/`, using these exact filenames:

- `unmuted.png`
- `muted.png`
- `disabled.png`
- `disconnected.png`

Only PNG files are supported. The folder is created automatically on
first launch. Changes are picked up automatically — no restart needed —
and if a file is missing or not a valid PNG, the mod falls back to its
bundled default icon for that state.

This mod is made for [Fabric Loader](https://fabricmc.net/use/ "Fabric").
If [Mod Menu](https://www.curseforge.com/minecraft/mc-mods/modmenu "Mod Menu")
is installed, the mod can be configured using a config screen accessible
through the mods menu.
