# Fabric datapacks for parity audits

Place the official Fabric **Origins** and **Apoli** datapacks inside `run/datapacks/` before running the NeoForge parity audit. This `datapacks/` folder documents the process and provides download links so they are easy to locate when preparing an audit run. You can obtain the latest datapack zips from the respective mod releases:

* Origins: https://github.com/apace100/origins/releases
* Apoli: https://github.com/apace100/apoli/releases

Download each archive, extract the contained datapack folder (usually named `origins` or `apoli`), and copy it into `run/datapacks/` so the game can load it as part of the `/reload` command. The parity tooling reads every pack present in that directory when `/origins debug parity` or `/origins debug todo` is executed.
