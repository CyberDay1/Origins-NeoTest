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

## Version matrix
The repository is configured for Stonecutter variants 1.21.1 through 1.21.10.

| Variant | Command |
| --- | --- |
| 1.21.1 | `./gradlew stonecutter use 1.21.1-neoforge && ./gradlew build` |
| 1.21.5 | `./gradlew stonecutter use 1.21.5-neoforge && ./gradlew build` |
| 1.21.10 | `./gradlew stonecutter use 1.21.10-neoforge && ./gradlew build` |

## Notes
- Java 21 toolchain recommended.
- Vars available in templates: ${PACK_FORMAT}, ${LOADER_FILE}.

