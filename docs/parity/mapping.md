# Origins Fabric Parity Mapping

| id | category | status | notes | class/file reference |
| --- | --- | --- | --- | --- |
| origins:elytra | power | Implemented | Elytra-style gliding handled by ElytraFlightPower. | `src/main/java/io/github/apace100/origins/power/impl/ElytraFlightPower.java` |
| origins:water_breathing | power | Implemented | Grants full aquatic breathing via AquaticPower hooks. | `src/main/java/io/github/apace100/origins/power/impl/AquaticPower.java` |
| origins:fire_immunity | power | Implemented | Negates fire damage through FireImmunityPower. | `src/main/java/io/github/apace100/origins/power/impl/FireImmunityPower.java` |
| origins:item | action | Implemented | Datapack-configured item grants wired through ItemAction. | `src/main/java/io/github/apace100/origins/power/action/impl/ItemAction.java` |
| origins:block | action | Implemented | Executes block mutations defined in datapack payloads. | `src/main/java/io/github/apace100/origins/power/action/impl/BlockAction.java` |
| origins:entity | action | Implemented | Handles entity-centric actions such as teleporting and health changes. | `src/main/java/io/github/apace100/origins/power/action/impl/EntityAction.java` |
| origins:world | action | Implemented | Dispatches global world actions resolved from datapacks. | `src/main/java/io/github/apace100/origins/power/action/impl/WorldAction.java` |
| origins:play_sound | action | Implemented | Plays configured sounds with validation of ids, pitch, volume, and position. | `src/main/java/io/github/apace100/origins/power/action/impl/PlaySoundAction.java` |
| origins:grant_advancement | action | Implemented | Awards advancements to players when triggered. | `src/main/java/io/github/apace100/origins/power/action/impl/GrantAdvancementAction.java` |
| origins:execute_command | action | Implemented | Runs a validated server command as the invoking player. | `src/main/java/io/github/apace100/origins/power/action/impl/ExecuteCommandAction.java` |
| origins:biome | condition | Implemented | Evaluates biome predicates against the player context. | `src/main/java/io/github/apace100/origins/power/condition/impl/BiomeCondition.java` |
| origins:dimension | condition | Implemented | Checks the invoking entity's dimension key. | `src/main/java/io/github/apace100/origins/power/condition/impl/DimensionCondition.java` |
| origins:time_of_day | condition | Implemented | Matches time windows within the current world. | `src/main/java/io/github/apace100/origins/power/condition/impl/TimeOfDayCondition.java` |
| origins:damage_source | condition | Implemented | Validates incoming damage sources against configured identifiers. | `src/main/java/io/github/apace100/origins/power/condition/impl/DamageSourceCondition.java` |
| origins:fluid | condition | Implemented | Confirms a block's fluid state matches the expected type. | `src/main/java/io/github/apace100/origins/power/condition/impl/FluidCondition.java` |
| origins:blockstate | condition | Implemented | Performs block state predicate checks defined in datapacks. | `src/main/java/io/github/apace100/origins/power/condition/impl/BlockStateCondition.java` |
| origins:equipped_item | condition | Implemented | Requires a specific item in the configured equipment slot. | `src/main/java/io/github/apace100/origins/power/condition/impl/EquippedItemCondition.java` |
| origins:food | condition | Implemented | Evaluates hunger and saturation ranges for players. | `src/main/java/io/github/apace100/origins/power/condition/impl/FoodCondition.java` |
