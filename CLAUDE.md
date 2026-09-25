# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Repository structure

`milos-minecraft-mods` is a monorepo of **independent** Minecraft Java Edition Fabric mods — there is no root Gradle build. Each top-level directory is its own self-contained Gradle project with its own `gradlew`, `settings.gradle.kts`, and `build.gradle.kts`; all commands below are run from inside a mod's directory, not the repo root.

Current mods:

- `milos-armor-hud` — HUD widget showing equipped armor + low-durability warning. Forked from `armor_hud`/`ukus-armor-hud`.
- `milos-fast-xp` — faster XP gain/pickup + auto-throw projectiles. Merge of two upstream mods (`fast-xp`, `fastexppickup`).
- `milos-mic-hud` — HUD icon for Simple Voice Chat mic state (mute/unmute/disabled/disconnected).
- `milos-player-highlight` — highlights specified players (glow/team color, Xaero's minimap radar dot).

The directory name for the armor HUD mod doesn't match its mod id (`milos-armor-hud`) or package (`se.guldbransen.milos.armorhud`) — it's a holdover from before the Milo fork/rename; don't assume directory name == mod id for that one.

For deep Fabric/mixin/ukulib conventions, read `.claude/agents/minecraft-fabric-modder.md` — it's the authoritative reference for how mods in this repo are built and should be treated as an extension of this file, not duplicated here.

## Commands

Run from inside the relevant mod directory (e.g. `cd milos-mic-hud`):

- Build: `./gradlew build` (compiles, remaps with official Mojang mappings, runs the mixin annotation processor — a bad mixin injection target fails the build here).
- Run all tests: `./gradlew test`
- Run a single test class: `./gradlew test --tests "se.guldbransen.milos.michud.icon.MicStateTest"`
- Manual/in-game testing: `./gradlew runClient` — there is no graphical Minecraft client available in this environment, so after a clean build hand the user a concrete manual test checklist rather than claiming a gameplay-visible feature works without their in-game confirmation.

There is no lint/format task configured in any of these projects.

## Architecture (shared across all mods)

Every mod in this repo follows the same skeleton, so understanding one makes the others fast to navigate:

- **Entry point**: a top-level `<Name>Mod` class implementing `ModInitializer` (e.g. `MicHudMod`, `FastXpMod`), holding `MOD_ID` and a static `ConfigManager<...>` instance. Client-only behavior goes in a separate `ClientModInitializer` (e.g. `FastXpClient`) rather than in the main entrypoint, so mods stay loadable in `environment: "*"`/server contexts where applicable.
- **Config**: a Lombok `Serializable` POJO (`<Name>Config`) persisted via `net.uku3lig.ukulib.config.ConfigManager.createDefault(...)`, paired with a `<Name>ConfigScreen extends AbstractConfigScreen<T>` and a `UkulibIntegration` class that exposes the screen through the `"ukulib"` fabric.mod.json entrypoint. Mod Menu/Cloth Config are never depended on directly — ukulib bridges to them when present. Config-file docs live in each mod's `config.md` where present.
- **Mixins**: under `<pkg>/mixin/`, declared in `src/main/resources/<mod-id>.mixins.json`, compiled at `defaultRequire: 1` so a broken injection target fails the build instead of silently no-opping.
- **Third-party integration is isolated**: optional/soft dependencies (Simple Voice Chat in `milos-mic-hud`, Xaero's minimap in `milos-player-highlight`) live behind a `compat/` package or a separately-loaded plugin class, so the mod still loads and works (minus that integration) when the soft dependency is absent.
- **Package root**: `se.guldbransen.milos.<shortname>`, matching `maven_group` in `gradle.properties`.
- **Design docs**: non-trivial features have a spec and plan under `docs/superpowers/specs/` and `docs/superpowers/plans/` inside the mod's own directory (not repo root) — check there for the "why" behind a feature before assuming intent from code alone.

## Scaffolding a new mod

Follow the pattern of the most recently added mod (currently `milos-mic-hud`) rather than the oldest (`milos-armor-hud`, which predates some conventions like the directory-name/mod-id split above). Read `.claude/agents/minecraft-fabric-modder.md` first — it covers mappings, Lombok, ukulib config/config-screen wiring, `fabric.mod.json` entrypoint conventions, and licensing (MIT, `LICENSE` copied into the jar as `LICENSE_<archivesName>`) in detail.
