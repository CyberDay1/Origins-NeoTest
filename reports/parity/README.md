# Parity audit reports

The automated parity audit run copies its JSON output into this directory so the results
can be checked into source control:

* `parity_report.json` – Comprehensive list of datapack elements and their parity status.
* `parity_todo.json` – Condensed backlog grouped by context for follow-up work.

After running `./gradlew runAudit` in the development environment and executing the
in-game `/origins debug parity` and `/origins debug todo` commands, copy the generated
files from `run/debug/` into this folder before committing changes.
