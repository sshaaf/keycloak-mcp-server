#!/usr/bin/env python3
"""
Regenerate prompts.txt: one block per KeycloakOperation with prompt text
(getDescription) and required JSON keys from *Command.java implementations.
Run from repo root: python3 scripts/generate-prompts-txt.py
"""
import re
from pathlib import Path

REPO = Path(__file__).resolve().parents[1]
COMMANDS = REPO / "src/main/java/dev/shaaf/keycloak/mcp/server/commands"
ENUM_JAVA = REPO / "src/main/java/dev/shaaf/keycloak/mcp/server/KeycloakOperation.java"
OUT = REPO / "prompts.txt"


def load_command_meta():
    data = {}
    for f in sorted(COMMANDS.rglob("*Command.java")):
        text = f.read_text(encoding="utf-8")
        m = re.search(r"return KeycloakOperation\.(\w+);", text)
        if not m:
            continue
        op = m.group(1)
        dm = re.search(
            r"getDescription\(\)\s*\{[^}]*?return\s+\"([^\"]+)\";",
            text,
            re.DOTALL,
        )
        desc = dm.group(1) if dm else f"Run {op}"
        rm = re.search(
            r"getRequiredParams\(\)\s*\{[^}]*?return\s+new String\[\]\s*\{([^}]+)\}",
            text,
            re.DOTALL,
        )
        if rm:
            params = re.findall(r"\"(\w+)\"", rm.group(1))
        else:
            params = []
        data[op] = (desc, params)
    return data


def enum_order():
    """Constant names in declaration order. Brace-counting the raw file is unsafe (e.g. `{@link`)."""
    t = ENUM_JAVA.read_text(encoding="utf-8")
    lines = t.splitlines()
    start = next(
        (i for i, ln in enumerate(lines) if re.search(r"public enum KeycloakOperation\s*\{", ln)),
        -1,
    )
    if start < 0:
        raise SystemExit("KeycloakOperation enum not found")
    ordered = []
    for line in lines[start + 1 :]:
        s = line.strip()
        if s == "}":
            break
        if not s or s.startswith("//"):
            continue
        m = re.match(r"^([A-Z][A-Z0-9_]+)\s*[,;]?\s*(\/\/.*)?$", s)
        if m and m.group(1):
            ordered.append(m.group(1))
    return ordered


def main():
    data = load_command_meta()
    ordered = enum_order()

    lines = [
        "# Keycloak MCP — copy/paste prompts for `executeKeycloakOperation`",
        "# Regenerate: python3 scripts/generate-prompts-txt.py",
        "# Use: operation = <NAME>, params = JSON object (see required keys).",
        "",
    ]
    missing = []
    for op in ordered:
        if op not in data:
            missing.append(op)
            desc, params = "(unmatched in commands — fix generator or add command)", []
        else:
            desc, params = data[op]
        req = (
            ", ".join(f'"{p}"' for p in params)
            if params
            else "(no required keys; see server validation)"
        )
        lines.append(f"## {op}")
        lines.append(f"Prompt: {desc}")
        lines.append(f"Required JSON keys: {req}")
        lines.append("")

    if missing:
        lines.insert(
            4, f"# WARNING: unmatched operations: {', '.join(missing)}"
        )
        lines.insert(5, "")

    OUT.write_text("\n".join(lines) + "\n", encoding="utf-8")
    print(f"Wrote {OUT} ({len(ordered)} operations)")
    if missing:
        print("Missing:", missing)
        return 1
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
