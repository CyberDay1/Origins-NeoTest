# Compatibility Overview

This branch restores the optional runtime companions that Immersive Portals expects
when advanced rendering and configuration features are present. The build now
downloads the following releases from Modrinth during configuration so they are
available on the compile and runtime classpaths:

- **Sodium** `mc1.21.8-0.7.0-neoforge`
- **Iris** `1.9.5+1.21.8-neoforge`
- **Yet Another Config Lib (YACL)** `3.8.0+1.21.9-neoforge`
- **Cloth Config** `20.0.148+neoforge`

Each jar is treated as `compileOnly` and `runtimeOnly`, ensuring the sources build
against the latest public APIs without packaging the mods inside the release jar.
The new `validateCompileMatrix`, `validateRuntimeMatrix`, and
`validatePortalFunctionMatrix` tasks gate CI so we do not regress optional
integration coverage.
