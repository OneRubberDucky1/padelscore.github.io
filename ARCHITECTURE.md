# Architecture

Padel Score is a single-module Wear OS app built with Kotlin and Jetpack Compose for Wear (`androidx.wear.compose.material`). There is no networking, persistence, or background service — the entire app is one `Activity` holding in-memory UI state for the duration of a match.

## Module layout

```
app/src/main/java/com/example/padelscore/presentation/
├── MainActivity.kt        # Activity entry point + top-level screen switch
├── AppViewModel.kt         # Owns navigation/session state (screen, match format, team colours)
├── SplashScreen.kt         # Start screen: match format + team colour selection
├── ScoreScreen.kt          # Live scoring screen (buttons, set pills, server indicator)
├── GameOverScreen.kt       # End-of-match summary
├── ScoreViewModel.kt       # All scoring rules and match state transitions
├── GameState.kt            # Immutable state data classes
└── theme/                  # Colours, spacing/sizing, typography, Wear MaterialTheme
    ├── Color.kt
    ├── Dimension.kt
    ├── Type.kt
    ├── Shape.kt
    └── Theme.kt
```

## How the pieces relate

```
MainActivity
 └─ PadelScoreApp (Composable, reads AppViewModel.screen: SPLASH | GAME)
     ├─ AppViewModel                                  # created via viewModel()
     │    ├─ owns screen, gamesFormat, setsFormat, teamColors
     │    └─ owns matchId, bumped on every startGame() call
     ├─ SplashScreen ──(games, sets, colours)──▶ AppViewModel.startGame(...) → screen = GAME
     └─ ScoreScreen(gamesFormat, setsFormat, matchId)
         └─ ScoreViewModel(gamesFormat, setsFormat)   # viewModel(key = "match-$matchId", factory = ...)
             ├─ owns GameState (current match state)
             ├─ owns history: List<GameState>          # for undo
             └─ exposes derived UI values (leftScore, rightScore, setScores, ...)
         ├─ renders ScoreButton x2, SetPillsWithServer, UtilityButtons
         └─ when isMatchOver → renders GameOverScreen instead
```

- **`MainActivity`** installs the splash screen, enables `FLAG_KEEP_SCREEN_ON` (so the watch face doesn't sleep mid-match), and sets the Compose content to `PadelScoreApp`.
- **`AppViewModel`** owns all session/navigation state — which screen is showing, the chosen match format, the chosen team colours, and a `matchId` counter. `PadelScoreApp` itself holds no state of its own; it just renders whichever screen `AppViewModel.screen` currently points to. This keeps that state alive across recompositions the same way `ScoreViewModel` keeps match state alive, rather than it living in a `remember` that Compose can discard.
- **`PadelScoreApp`** switches between `SplashScreen` and `ScoreScreen` based on `AppViewModel.screen`. Starting a match calls `AppViewModel.startGame(games, sets, colours)`, which records the format/colours, increments `matchId`, and flips to `Screen.GAME`. Returning home calls `AppViewModel.returnHome()`, which just flips back to `Screen.SPLASH` — see below for how a fresh match state is guaranteed without needing to clear the whole `ViewModelStore`.
- **`ScoreViewModel`** is where all the domain logic lives. The Compose screens are otherwise "dumb" — they render `GameState`/derived properties and forward taps to the view model.
- **`GameState`** is a plain immutable data class. Every mutation in the view model produces a new `GameState` via `.copy(...)` rather than mutating in place, which is what makes undo trivial (just push the previous `GameState` onto a stack before each change). `completedSets` is a `kotlinx.collections.immutable.PersistentList` rather than a plain `List` — Compose's compiler can't otherwise prove a `List` is safe to skip recomposition over, so a plain list would force every composable reading it (the set-score pills, the game-over breakdown) to re-render on every recomposition regardless of whether the sets actually changed.
- **`theme/`** centralises all Wear-specific styling so screens don't hardcode colours, sizes, or fonts. Dimensions are derived as fractions of screen width (`LocalConfiguration.current.screenWidthDp`) so the layout scales across the small-round and large-round Wear OS form factors.

## Major features and where they're implemented

### Scoring engine (`ScoreViewModel.kt`, `GameState.kt`)
- Points are tracked as counters (`counterLeft`/`counterRight`, indices into `scoreArray = [0, 15, 30, 40]`), not raw point values.
- `handleIncrement()` is the core rule engine:
  - Below 40-40, an increment just advances the counter, or wins the game if the side is already at 40 and the opponent isn't.
  - At 40-40 ("deuce"), it tracks who currently has `advantage` (`-1` = deuce, `0` = left, `1` = right) and awards the game once a side wins two points in a row from deuce.
- `onGameWon()` increments the game score for the set, flips `isLeftServing`, then delegates to `checkSetWin()` and `resetPoints()`.
- `checkSetWin()` compares games won against `gamesToWin = (gamesFormat + 1) / 2` (e.g. best-of-3 games needs 2). On a set win it appends a `SetScore` to `completedSets`, bumps `setScoreLeft`/`setScoreRight`, resets the game score to 0-0, and calls `checkMatchWin()`.
- `checkMatchWin()` uses the same "majority" formula against `setsFormat` to set `isMatchOver = true`.
- `displayScore()` converts the internal counter/advantage state into the strings shown on screen (`"0"/"15"/"30"/"40"/"AD"/"D"`).

### Server tracking (`ScoreViewModel.kt`, `ScoreScreen.kt`)
- `state.isLeftServing` flips every time a game is won (`onGameWon`).
- `ScoreScreen` passes `isServing` into `ScoreButton` (which changes its gradient fill for the serving side) and into `ImagePill`/`SetPillsWithServer` (which shows a small ball icon next to the serving side, alongside the set-score pills).

### Undo (`ScoreViewModel.kt`)
- Before every point (`incrementLeft`/`incrementRight`), the current `GameState` is pushed onto a local `history` stack.
- `undo()` pops the last state back off the stack and restores it — this naturally undoes points, game wins, set wins, and even the match-over transition, since all of those are just fields on `GameState`.
- Undo is available both mid-match (`ScoreScreen`'s back button) and from the `GameOverScreen`, letting you correct the final point of a match.

### Match format selection (`SplashScreen.kt`)
- `matchOptions = listOf(1, 3, 5)` backs two `MatchFormatSelector` pill controls — one for games-per-set, one for sets-per-match — each an animated sliding indicator (`animateFloatAsState` + custom `Canvas` path) over three tappable segments. The indicator's `Path` and gradient colour list are `remember`ed and reused across animation frames rather than reallocated on every frame.
- The selected `(games, sets)` pair (along with the chosen team colours) is passed to `onStartGame`, which calls `AppViewModel.startGame(...)`. That increments `AppViewModel.matchId`, and `ScoreScreen` uses it as the key for `viewModel(key = "match-$matchId", factory = ScoreViewModel.factory(gamesFormat, setsFormat))` — guaranteeing a brand-new `ScoreViewModel` (and so a fresh `GameState`) for every match, without needing to explicitly clear anything.

### Team colours (`SplashScreen.kt`, `theme/Color.kt`)
- `teamColorPairs` defines four preset left/right colour pairings (e.g. cobalt light/dark, red/green). `ColorPairPill` renders the current pair as two dots inside a pill button, which opens `TeamColorPickerOverlay` — a full-screen grid of the available pairs — when tapped.
- `selectedPairIndex` is local `Composable` state in `SplashScreen` — that's appropriate, since it's just tracking the in-progress picker selection before the match starts. Once "Start" is tapped, the chosen pair is handed to `AppViewModel.startGame(...)` and stored as `AppViewModel.teamColors`, which `ScoreScreen` and `GameOverScreen` both read (`leftTeam`/`rightTeam`) for button fills, the serving indicator, and the "`<NAME>` WINS" summary.

### Match summary (`GameOverScreen.kt`)
- Rendered by `ScoreScreen` in place of the live scoring UI once `viewModel.isMatchOver` is true.
- Shows the winning side (`matchWinnerIsLeft`, derived from comparing `setScoreLeft`/`setScoreRight`), the number of sets won by each side, and reuses `NumberOfScorePill` (also used on the live scoring screen) to show the full set-by-set breakdown.
- "Who won a given set" is a single rule — `SetScore.winnerIsLeft` (`left > right`), defined once on the data model in `GameState.kt` — rather than being recomputed separately in `GameOverScreen` and `NumberOfScorePill`.
- Offers the same "return home" and "undo" actions as the live screen.

## Theming (`theme/`)
- **`Color.kt`** — the raw colour palette (cobalt accent, translucent surface layers, player-chip colours, team colours).
- **`Dimension.kt`** — `AppDimensions`, a set of `Dp` sizes computed as fractions of screen width so layouts scale consistently between the small-round and large-round Wear OS previews/devices; exposed app-wide via `LocalAppDimensions`.
- **`Type.kt`** — equivalent composition-local for font sizes/weights used across screens.
- **`Shape.kt`** — shared corner-radius/shape definitions.
- **`Theme.kt`** — `PadelScoreTheme`, the root composable that wires the above into Wear Compose's `MaterialTheme` and provides the dimension/typography composition locals used throughout the screens.
