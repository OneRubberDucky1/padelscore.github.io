# Padel Score

A Wear OS watch app for keeping score during a tennis or padel match. It handles standard game/set/match scoring (including deuce and advantage), tracks who is serving, and supports configurable match formats (best of 1, 3, or 5 games per set / sets per match).

## Features

- **Tap-to-score** — one large button per side; tap to award a point to that side.
- **Full scoring rules** — points (0/15/30/40), deuce, and advantage are handled automatically, including the game, set, and match win conditions.
- **Server tracking** — the serving side is highlighted, and swaps automatically after each game.
- **Set history** — a row of pills shows the score of every set in the match, with the current set highlighted and completed sets marked with a winner indicator.
- **Undo** — step back through the last scoring action if you tap the wrong button.
- **Configurable match format** — choose games-per-set and sets-per-match (1, 3, or 5) from the start screen.
- **Team colours** — pick a colour pair to represent the two sides from the start screen.
- **Match over screen** — shows the winner and final set scores, with options to start a new match or undo the last point.

## Running the app

This is a standard Android/Wear OS Gradle project.

```bash
./gradlew installDebug
```

Deploy to a Wear OS emulator or a physical watch (min SDK 30). The app is standalone — it does not require a companion phone app to run.

See [ARCHITECTURE.md](ARCHITECTURE.md) for a walkthrough of how the codebase is organized and how the major features are implemented.
