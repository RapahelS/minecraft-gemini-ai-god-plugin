# Architecture overview

This document summarizes how the plugin is structured, how the game modes work, and exactly how to build and deploy the plugin.

## quick build & deploy

- JDK: 21
- Build with the Gradle wrapper:
  - Windows/macOS/Linux: `./gradlew clean shadowJar`
- Output JAR (fat/shaded): `build/libs/gptgodmc-<version>-all.jar`
  - Deploy the `-all.jar` to your Paper server `plugins` folder.
  - Remove any thin jar (without `-all`) to avoid missing dependency errors (e.g., jtokkit).

Notes
- Gradle wrapper: 8.7 (wrapper is included in the repo)
- Paper repo used by build: `https://repo.papermc.io/repository/maven-public/`

## configuration

File: `src/main/resources/config.yml`
- Core:
  - `geminiKey`, `speechify-key`, `language`, `enabled`
  - `Rounds: true|false` toggles the round system
  - `startingWorld`: world ID from `plugins/gptgodmc/worlds`
  - `gamemode`: `SANDBOX` or `DEATHMATCH` (case sensitive)
- Models:
  - `model-name` (primary), `secondary-model-name` (transcription/utility)
  - `model-tempurature`, token limits, `rate` (call cadence), `transcription-rate`
- TTS:
  - `tts: true|false`, `speechify-voice`

## game modes

Defined at: `src/main/java/net/bigyous/gptgodmc/enums/GptGameMode.java`
- `SANDBOX`
- `DEATHMATCH`

Selected via `gamemode` in `config.yml`. Read during startup:
- `GPTGOD.gameMode = GptGameMode.valueOf(getConfig().getString("gamemode"));`

### DEATHMATCH specifics
- Teams & scoreboard created in `GPTGOD.java` when mode is `DEATHMATCH`.
- Player assignment, spawns, and round logic in `RoundSystem.java`:
  - Red/Blue teams; colored leather helm on respawn
  - Fixed spawn vectors in-file: `RED_SPAWN`, `BLUE_SPAWN`
  - On death, players become spectators; round ends when a team has no living players
  - `reset()` resets world, structures, logs, scoreboard, and restarts the game loop

## runtime flow

- Entry point: `src/main/java/net/bigyous/gptgodmc/GPTGOD.java`
  - Registers voice chat plugin (`VoiceMonitorPlugin`) if Simple Voice Chat API is present
  - Loads config, commands (`/try`, `/nickname`, `/givecamera`, `/aitest`)
  - If `Rounds` + `startingWorld` set, registers the `RoundSystem` and loads the map via `WorldManager`
  - Registers listeners: logging (`LoggableEventHandler`), camera, structures, and a join/quit listener to start/stop the loop

- Game loop & AI:
  - Start/stop: `GameLoop.init()` on first player join; `GameLoop.stop()` when empty or during resets
  - GPT integration and tools under `src/main/java/net/bigyous/gptgodmc/GPT/`:
    - `GptAPI`, `GptActions`, `Prompts`, `Personality`, `GoogleVision`, `Transcription*`, `Speechify`, `TextToSpeech`
  - Event capture/context under `loggables/*` with aggregator helpers:
    - `LoggableEventHandler`, `EventLogger`, `ServerInfoSummarizer`

- Worlds:
  - `WorldManager` loads maps from `plugins/gptgodmc/worlds`, teleports players, and resets between rounds

- Voice:
  - `VoiceMonitorPlugin` hooks Simple Voice Chat, bundles mic audio, calls transcription on the secondary model

- Structures & imagery:
  - `Structure`, `StructureManager`; camera item & rendering under `cameraitem/*` and `image_maps/*`

## important locations

- Main plugin class: `src/main/java/net/bigyous/gptgodmc/GPTGOD.java` (declared in `resources/plugin.yml`)
- Game loop: `src/main/java/net/bigyous/gptgodmc/GameLoop.java`
- Round system: `src/main/java/net/bigyous/gptgodmc/RoundSystem.java`
- Worlds: `src/main/java/net/bigyous/gptgodmc/WorldManager.java`
- Enums (modes): `src/main/java/net/bigyous/gptgodmc/enums/GptGameMode.java`
- GPT/AI: `src/main/java/net/bigyous/gptgodmc/GPT/`
- Event logging: `src/main/java/net/bigyous/gptgodmc/loggables/`
- Structures: `src/main/java/net/bigyous/gptgodmc/Structure*.java`
- Camera & imagery: `src/main/java/net/bigyous/gptgodmc/cameraitem/*`, `image_maps/*`
- Config & plugin metadata: `src/main/resources/config.yml`, `src/main/resources/plugin.yml`
- Build output: `build/libs/gptgodmc-<version>-all.jar`

## commands

Registered in code (`GPTGOD.onEnable`):
- `/try` (debug function executor)
- `/nickname`
- `/givecamera`
- `/aitest`

## build internals (for maintainers)

- Build file: `build.gradle`
  - Uses Shadow plugin to produce shaded jar; includes runtime deps (e.g., `jtokkit`)
  - Targets Java 21 bytecode (`options.release = 21`)
- Wrapper: `gradle/wrapper/gradle-wrapper.properties` (Gradle 8.7)

If the server logs a missing class (NoClassDefFoundError), ensure you deployed the `-all.jar`.