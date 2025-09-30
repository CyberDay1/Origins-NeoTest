import os, re

root = "src/main/java"

replacements = {
    r"net\\.minecraftforge\\.": "net.neoforged.",
    r"cpw\\.mods\\.": "net.neoforged.fml."
}

for subdir, _, files in os.walk(root):
    for f in files:
        if f.endswith(".java"):
            path = os.path.join(subdir, f)
            with open(path, "r", encoding="utf-8") as fh:
                content = fh.read()
            original = content
            for pattern, repl in replacements.items():
                content = re.sub(pattern, repl, content)
            if content != original:
                with open(path, "w", encoding="utf-8") as fh:
                    fh.write(content)
                print(f"Updated {path}")
