# Origins (NeoForge)

This repository contains the NeoForge build logic for the **Origins** mod. The project
targets Minecraft **1.21.1** on NeoForge **21.1.209** and is configured to build
with Gradle **8.13** and a Java **21** toolchain.

## Requirements

* Java Development Kit 21 (the project configures the toolchain automatically, but a JDK
  installation is still required for Gradle to download the matching version).
* Gradle Wrapper (included); invoke the wrapper scripts instead of a system Gradle.

## Building from source

1. Clone the repository.
2. (Optional) Update the values in `gradle.properties` to customise the mod metadata or
   target versions.
3. Run the build using the Gradle wrapper:

   ```bash
   ./gradlew --console=plain build
   ```

   The resulting NeoForge mod jar is written to `build/libs/`.

During the build the `neoforge.mods.toml` file located in `src/main/resources` is
automatically expanded with the values defined in `gradle.properties`. This ensures the
published jar always advertises the correct mod id, version, and dependency ranges.

## Updating Minecraft or NeoForge versions

The default Minecraft and NeoForge versions are controlled by the `minecraftVersion` and
`neoForgeVersion` entries in `gradle.properties`. When bumping either value, also update
the corresponding version ranges (`minecraftVersionRange`, `neoForgeVersionRange`) so the
generated `neoforge.mods.toml` continues to match the desired compatibility matrix.

If you need to produce builds for multiple Minecraft releases, the `versions/` directory
contains per-version resource overrides. Add or adjust files within the appropriate
sub-directory to customise metadata or assets for that specific target before packaging
them separately.
