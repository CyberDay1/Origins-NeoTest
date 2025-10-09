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

## NeoForge version overrides
Each Stonecutter variant can set a specific `NEOFORGE_VERSION`. The active default (`1.21.1-neoforge`) now resolves to `21.1.209`, while other variants declare their own NeoForge builds in `stonecutter.json`. To adjust a single variant, edit that entry's `"NEOFORGE_VERSION"` and rebuild after switching the variant.

## CI
GitHub Actions builds three representative variants on pushes and PRs:
1.21.1-neoforge, 1.21.5-neoforge, 1.21.10-neoforge.

## Maintainers
Primary maintainer: @org/maintainers

## CI artifacts
Pull requests run a build matrix for selected variants and attach jars as artifacts.
If an audit datapack is available the workflow uploads parity JSON reports.

## Pack format verification

The repository includes a sweep to verify `pack.mcmeta` `pack_format` across Stonecutter variants.

Expected values:

| Variant | pack_format |
|---|---|
| 1.21.10-neoforge | 88 |
| 1.21.9-neoforge | 88 |
| 1.21.8-neoforge | 81 |
| 1.21.7-neoforge | 81 |
| 1.21.6-neoforge | 80 |
| 1.21.5-neoforge | 71 |
| 1.21.4-neoforge | 61 |
| 1.21.3-neoforge | 57 |
| 1.21.2-neoforge | 57 |
| 1.21.1-neoforge | 48 |

Run locally:
```bash
chmod +x tools/verify-pack-format.sh
./tools/verify-pack-format.sh
```
