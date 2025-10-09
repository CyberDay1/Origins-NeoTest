# Stonecutter Matrix

The NeoForge release line now validates the following representative minors. Each
variant covers a range of upstream Minecraft patches according to the current
matrix policy.

| Variant            | Compatible versions        |
|--------------------|----------------------------|
| `1.21.1-neoforge`  | `1.21.1` *(baseline owner)* |
| `1.21.2-neoforge`  | `1.21.2` – `1.21.4`        |
| `1.21.5-neoforge`  | `1.21.5` – `1.21.7`        |
| `1.21.8-neoforge`  | `1.21.8` – `1.21.10`       |

Removed variants inherit the resources from their covering minor, reducing
redundant descriptors while keeping coverage explicit for downstream packs and
validation tooling. The owner build for `1.21.1` remains isolated so downstream
projects can continue targeting the original release without inheriting newer
runtime overlays.
