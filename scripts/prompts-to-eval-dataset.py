#!/usr/bin/env python3
"""
Parse prompts.txt into a JSONL file for external LLM eval tools (Promptfoo, Langfuse, custom runners).

Each line: {"operation","prompt","requiredKeys":[]}

Usage (from repo root):
  python3 scripts/prompts-to-eval-dataset.py -o evals/dataset.jsonl
"""
from __future__ import annotations

import argparse
import json
import re
from pathlib import Path

REPO = Path(__file__).resolve().parents[1]
DEFAULT_IN = REPO / "prompts.txt"


def parse_prompts(path: Path) -> list[dict]:
    text = path.read_text(encoding="utf-8")
    records: list[dict] = []
    op = None
    prompt = None
    keys_line = None

    def flush() -> None:
        nonlocal op, prompt, keys_line
        if not op or prompt is None:
            return
        keys: list[str] = []
        if keys_line and "no required keys" not in keys_line.lower():
            for m in re.finditer(r'"([a-zA-Z0-9_]+)"', keys_line):
                keys.append(m.group(1))
        records.append(
            {
                "operation": op,
                "prompt": prompt,
                "requiredKeys": keys,
            }
        )

    for line in text.splitlines():
        if line.startswith("## "):
            flush()
            op = line[3:].strip()
            prompt = None
            keys_line = None
            continue
        if op is None:
            continue
        if line.startswith("Prompt: "):
            prompt = line[len("Prompt: ") :].strip()
        elif line.startswith("Required JSON keys: "):
            keys_line = line[len("Required JSON keys: ") :].strip()
    flush()
    return records


def main() -> int:
    ap = argparse.ArgumentParser()
    ap.add_argument("-i", "--input", type=Path, default=DEFAULT_IN)
    ap.add_argument("-o", "--output", type=Path, default=REPO / "evals/dataset.jsonl")
    args = ap.parse_args()
    rows = parse_prompts(args.input)
    args.output.parent.mkdir(parents=True, exist_ok=True)
    with args.output.open("w", encoding="utf-8") as f:
        for r in rows:
            f.write(json.dumps(r, ensure_ascii=False) + "\n")
    print(f"Wrote {len(rows)} rows to {args.output}")
    return 0


if __name__ == "__main__":
    raise SystemExit(main())
