# Contributing Guidelines

Thank you for your interest in contributing to Origins NeoTest! This project relies on a clean build free of compiler or Gradle warnings. Please follow the guidelines below when preparing changes.

## Before You Open a Pull Request

1. Ensure your development environment uses JDK 21.
2. Run the full build locally with the strict warning configuration:
   ```bash
   ./gradlew clean build --warning-mode all
   ```
   This command surfaces all Gradle deprecation warnings and enforces the compiler lint settings configured in `build.gradle`.
3. Resolve any warnings produced by the build. Pull requests that surface compiler or Gradle warnings will be rejected by continuous integration.

## Submitting Your Pull Request

- Provide clear descriptions of the changes made and the testing performed.
- Keep your branch up to date with the main branch to minimize merge conflicts.
- CI will automatically run `./gradlew clean build --warning-mode all --console=plain --stacktrace` and upload a Gradle problems report artifact for review. Builds that produce warnings or deprecations will fail.

We appreciate your contributions and attention to build quality!
