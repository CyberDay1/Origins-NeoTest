# Contributing Guidelines

Thank you for your interest in contributing to Origins NeoTest! This project relies on a clean build free of compiler or Gradle warnings. Please follow the guidelines below when preparing changes.

## Before You Open a Pull Request

1. Ensure your development environment uses JDK 21.
2. Run the full build locally with the strict warning configuration:
   ```bash
   ./gradlew clean build --warning-mode all
   ```
   This command surfaces all Gradle deprecation warnings and enforces the compiler lint settings configured in `build.gradle`.
3. Validate mixin compatibility and ensure no placeholder textures are present before pushing:
   ```bash
   ./gradlew validateMixinCompatLevel
   python3 scripts/assert-no-placeholders.py
   ```
   These commands ensure that mixin definitions declare `JAVA_21` compatibility and that no temporary placeholder PNGs slip into commits.
4. Resolve any warnings or validation failures locally. Pull requests that surface compiler warnings, mixin compatibility issues, or placeholder assets will be rejected by continuous integration.

## Submitting Your Pull Request

- Provide clear descriptions of the changes made and the testing performed.
- Keep your branch up to date with the main branch to minimize merge conflicts.
- CI will automatically run `./gradlew clean build --warning-mode all --console=plain --stacktrace`, `./gradlew validateMixinCompatLevel`, and `python3 scripts/assert-no-placeholders.py`, uploading a Gradle problems report artifact for review. Builds that produce warnings, violate mixin compatibility, or commit placeholder assets will fail.

We appreciate your contributions and attention to build quality!
