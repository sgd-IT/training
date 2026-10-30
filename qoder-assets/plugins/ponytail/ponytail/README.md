# Ponytail

Makes your AI agent think like the laziest senior dev in the room.

## Source

- **Origin**: [DietrichGebert/ponytail](https://github.com/DietrichGebert/ponytail)
- **License**: MIT

## Included Skills

| Skill | Path |
|-------|------|
| ponytail | `skills/ponytail/SKILL.md` |

## What It Does

Enforces a "ladder of laziness" on every coding task:

1. Does this need to exist at all? (YAGNI)
2. Already in this codebase? → reuse it.
3. Stdlib does it? → use it.
4. Native platform feature covers it? → use that.
5. Already-installed dependency? → use it.
6. Can it be one line? → one line.
7. Only then: minimum code that works.

## Intensity Levels

- **lite** — build what's asked, name the lazier alternative.
- **full** *(default)* — ladder enforced, shortest diff.
- **ultra** — YAGNI extremist, challenge the requirement itself.

## Setup

No configuration required. The skill activates automatically on any coding task, or when the user says "ponytail", "be lazy", "lazy mode", "yagni", etc.
