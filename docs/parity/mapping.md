# Origins Fabric Parity Mapping

| id | category | status | notes | class/file reference |
| --- | --- | --- | --- | --- |
| origins:elytra | power | Implemented | Elytra-style gliding handled by ElytraFlightPower. | `src/main/java/io/github/apace100/origins/power/impl/ElytraFlightPower.java` |
| origins:water_breathing | power | Implemented | Grants full aquatic breathing via AquaticPower hooks. | `src/main/java/io/github/apace100/origins/power/impl/AquaticPower.java` |
| origins:fire_immunity | power | Implemented | Negates fire damage through FireImmunityPower. | `src/main/java/io/github/apace100/origins/power/impl/FireImmunityPower.java` |
| origins:item | action | TODO | NeoForge action adapters not yet implemented. | — |
| origins:block | action | TODO | Requires block action dispatcher parity. | — |
| origins:entity | action | TODO | Needs entity action codec + executor. | — |
| origins:world | action | TODO | Awaiting world action bridge. | — |
| origins:biome | condition | TODO | Needs biome predicate codec + evaluation. | — |
| origins:dimension | condition | TODO | Requires dimension predicate registry. | — |
| origins:time_of_day | condition | TODO | Implement Fabric-compatible time check. | — |
| origins:damage_source | condition | TODO | Pending damage source predicate support. | — |
| origins:fluid | condition | TODO | Needs fluid state predicate integration. | — |
| origins:blockstate | condition | TODO | Block state predicate bridge outstanding. | — |
| origins:equipped_item | condition | TODO | Awaiting equipment predicate loader. | — |
| origins:food | condition | TODO | Food predicate parity not implemented. | — |
