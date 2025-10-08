# Origins (NeoForge)

This repository contains the NeoForge build logic for the **Origins** mod. The project
is configured with the Stonecutter multi-version plugin and targets Minecraft
**1.21.1** through **1.21.10** on the matching NeoForge **21.1.x** → **21.10.x** line.
Builds continue to use Gradle **8.13** with a Java **21** toolchain.

## Requirements

* Java Development Kit 21 (the project configures the toolchain automatically, but a JDK
  installation is still required for Gradle to download the matching version).
* Gradle Wrapper (included); invoke the wrapper scripts instead of a system Gradle.

## Selecting a Minecraft/NeoForge variant

Stonecutter maintains the Minecraft/NeoForge matrix described in `stonecutter.json`.
Use the helper task to pick a target before building or running the project:

```bash
./gradlew stonecutterSwitchTo1.21.1-neoforge
./gradlew --console=plain build
```

To build against the latest entry in the matrix run:

```bash
./gradlew stonecutterSwitchTo1.21.10-neoforge
./gradlew --console=plain build
```

Once a variant is selected you can invoke any other Gradle goal (for example
`./gradlew runClient`) and Stonecutter will reuse the active configuration.
If you prefer the interactive CLI (`./gradlew stonecutter use <variant>`), be
aware that Groovy DSL builds occasionally report ambiguous task names—use the
explicit `stonecutterSwitchTo…` tasks shown above if that occurs.

## Building from source

1. Clone the repository.
2. (Optional) Adjust the default variant or metadata by editing `stonecutter.json` and
   `gradle.properties`.
3. Run the build using the Gradle wrapper (after selecting a variant as shown above):

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

1. Download the latest Fabric Origins datapack zip and drop it into
   `external/fabric-origins-zips/`. The Gradle task automatically extracts the newest zip
   in that folder into `run/datapacks/origins-fabric/`.
2. Launch the audit-enabled dev instance:

   ```powershell
   # Drop latest zip into external/fabric-origins-zips/
   .\gradlew.bat runAudit
   /reload
   /origins debug parity
   /origins debug todo
   ```

   The `runAudit` configuration sets the username to `DevAudit`, enables the
   `origins.debugAudit` system property, and uses the shared `run/` workspace so datapacks
   are detected automatically.
   On non-Windows platforms you can use `./gradlew runAudit` and run the debug commands
   manually once the game starts.
3. Once the game finishes loading, execute the `/reload`, `/origins debug parity`, and
   `/origins debug todo` commands in-game or from the dedicated server console. The debug
   commands create `run/debug/parity_report.json` and `run/debug/parity_todo.json`.
4. The build automatically archives any generated parity reports into
   `reports/parity/` once `runAudit` exits. Review the refreshed JSON files and commit
   them alongside your changes so reviewers can analyse the outstanding compatibility
   work.

## Updating Minecraft or NeoForge versions

The Stonecutter matrix in `stonecutter.json` controls which Minecraft/NeoForge pairs are
available. Add a new entry (or update an existing one) with the desired `mcVersion` and
`loaderVersion`, then rerun `./gradlew stonecutter use <variant>` to regenerate the
Gradle properties and resources.

`gradle.properties` now stores token placeholders that Stonecutter resolves per variant.
Only the metadata values such as `mod_id` or `mod_version` should be edited manually.
Resource files that depend on versioned values live alongside `.stonecutter` templates in
`src/main/resources`.
