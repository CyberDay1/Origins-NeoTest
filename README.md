# NeoForge Multiversion (Stonecutter)

## Switch versions
Use Stonecutter's `use` task to select a Minecraft/NeoForge pair and then build the project.

```bash
./gradlew stonecutter use 1.21.1-neoforge
./gradlew clean build
```

Latest example:
```bash
./gradlew stonecutter use 1.21.10-neoforge
./gradlew clean build
```

## Notes
- Java 21 toolchain recommended.
- Vars available in templates: ${PACK_FORMAT}, ${LOADER_FILE}.

