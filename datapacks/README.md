# Fabric datapacks for parity audits

Place the official Fabric **Origins** and **Apoli** datapacks in this directory before running the NeoForge parity audit. You can obtain the latest datapack zips from the respective mod releases:

* Origins: https://github.com/apace100/origins/releases
* Apoli: https://github.com/apace100/apoli/releases

Download each archive, extract the contained datapack folder (usually named `origins` or `apoli`), and copy it here so the game can load it as part of the `/reload` command. The parity tooling reads every pack present in this directory when `/origins debug parity` or `/origins debug todo` is executed.
