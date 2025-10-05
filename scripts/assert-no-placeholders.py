#!/usr/bin/env python3
"""Fail the build if placeholder PNG assets are present in the repository.

The guard intentionally runs in CI as well as locally to ensure that temporary
textures do not ship in releases. It scans the repository for PNG files whose
name contains the word "placeholder" (case-insensitive) outside of ignored
build directories.
"""

from __future__ import annotations

import sys
from pathlib import Path

IGNORED_DIR_NAMES = {".git", "build", ".gradle", "runs", "out", "dist"}


def should_ignore(path: Path) -> bool:
    """Return True if *path* is located within an ignored directory."""

    return any(part in IGNORED_DIR_NAMES for part in path.parts)


def main() -> int:
    repo_root = Path(__file__).resolve().parents[1]
    violations: list[Path] = []

    for png in repo_root.rglob("*.png"):
        if should_ignore(png.relative_to(repo_root)):
            continue

        if "placeholder" in png.stem.lower() or "placeholder" in png.name.lower():
            violations.append(png.relative_to(repo_root))

    if violations:
        print("Placeholder PNG assets detected:")
        for file_path in violations:
            print(f" - {file_path}")
        print(
            "Please replace these textures with finalized assets before committing "
            "or add them to the ignore list if they are generated."
        )
        return 1

    print("No placeholder PNG assets detected.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
