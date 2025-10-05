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

   > **Strict compilation:** The build enables full Java compiler linting and treats
   > deprecation and unchecked warnings as errors. Resolve any warnings locally before
   > pushing changes, as CI enforces the same checks.

During the build the `neoforge.mods.toml` file located in `src/main/resources` is
automatically expanded with the values defined in `gradle.properties`. This ensures the
published jar always advertises the correct mod id, version, and dependency ranges.

### Event bus subscriber guidelines

When adding event listeners with `@EventBusSubscriber`, prefer the defaults unless a
gameplay hook requires the global Forge bus. Only use `bus = EventBusSubscriber.Bus.FORGE`
for gameplay events such as command registration or in-world triggers. All other
subscribers (registry, datagen, networking, reload listeners, etc.) should omit the `bus`
parameter so they attach to the mod bus implicitly.

## Configuring built-in origins

When the game is launched a common NeoForge configuration file named
`origins-common.toml` is generated alongside the existing client and server configs.
In addition to the per-origin balance tweaks listed below, the root of the file exposes
the `debugAudit` toggle which enables verbose datapack parity logging. Any launch
configuration can force audit mode by setting the JVM system property
`-Dorigins.debugAudit=true`.
The file exposes the following options for the bundled powers:

| Category | Option | Description | Default |
|----------|--------|-------------|---------|
| `phantom` | `hungerDrainIntervalTicks` | Number of ticks between hunger drain while phantomized. | `80` |
| `phantom` | `hungerDrainPerInterval` | Hunger points removed every interval. | `1` |
| `phantom` | `allowWallPhasing` | Allow sneaking while phantomized to phase through blocks. | `true` |
| `avian` | `sleepMaxY` | Highest Y level Avians may sleep at. | `86` |
| `avian` | `slowFallingEnabled` | Toggle the passive slow falling effect. | `true` |
| `enderian` | `waterDamagePerSecond` | Damage per second applied while touching water. | `2.0` |
| `blazeborn` | `waterDamagePerSecond` | Damage per second applied while touching water. | `2.0` |
| `merling` | `swimSpeedMultiplier` | Swim speed multiplier while underwater. | `1.35` |
| `merling` | `underwaterVisionEnabled` | Enable the underwater night vision effect. | `true` |
| `feline` | `fallDamageReduction` | Fraction of fall damage prevented (1.0 = no damage). | `1.0` |
| `feline` | `moveSpeedMultiplier` | Base movement speed multiplier. | `1.10` |
| `elytrian` | `cancelFallDamage` | Cancel Elytrian fall damage entirely. | `true` |
| `elytrian` | `confinedSpaceChecks` | Apply Elytrian weakness/slowness in cramped spaces. | `false` |
| `shulk` | `chestArmorAllowed` | Allow Shulk players to wear chest armor. | `false` |

## Datapack parity auditing

The NeoForge port ships with a dedicated Gradle run configuration that enables the parity
audit tooling and collects reports for review. Follow this workflow to produce an audit:

1. Download the Fabric Origins and Apoli datapacks and extract them into
   `run/datapacks/`. See [`datapacks/README.md`](datapacks/README.md) for links and
   detailed instructions.
2. Launch the audit-enabled dev instance:

   ```bash
   ./gradlew runAudit
   ```

   The `runAudit` configuration sets the username to `DevAudit`, enables the
   `origins.debugAudit` system property, and uses the shared `run/` workspace so datapacks
   are detected automatically.
3. Once the game finishes loading, execute the following commands in-game or from the
   dedicated server console:

   ```bash
   /reload
   /origins debug parity
   /origins debug todo
   ```

   The debug commands create `run/debug/parity_report.json` and
   `run/debug/parity_todo.json`.
4. Copy the generated JSON files into the tracked `reports/parity/` directory before
   committing changes:

   ```bash
   cp run/debug/parity_report.json reports/parity/
   cp run/debug/parity_todo.json reports/parity/
   ```

   Upload the refreshed `parity_todo.json` along with your pull request so reviewers can
   analyse the outstanding compatibility work.

## Updating Minecraft or NeoForge versions

The default Minecraft and NeoForge versions are controlled by the `minecraftVersion` and
`neoForgeVersion` entries in `gradle.properties`. When bumping either value, also update
the corresponding version ranges (`minecraftVersionRange`, `neoForgeVersionRange`) so the
generated `neoforge.mods.toml` continues to match the desired compatibility matrix.

If you need to produce builds for multiple Minecraft releases, the `versions/` directory
contains per-version resource overrides. Add or adjust files within the appropriate
sub-directory to customise metadata or assets for that specific target before packaging
them separately.
