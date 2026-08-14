# Stopwatch — Java + XML

Internship Task 5 stopwatch application.

## Features
- HH:MM:SS display
- Start
- Pause/Stop
- Reset
- Visual button states
- Lifecycle-aware timing with onPause/onResume
- Bonus Lap feature with scrollable lap list
- Uses SystemClock.elapsedRealtime() for accurate elapsed time

## Run
Open this folder in Android Studio, sync Gradle, select an emulator/device, and press Run.

## Test
1. Start -> timer runs.
2. Pause -> timer freezes.
3. Start -> resumes from paused time.
4. Reset -> 00:00:00.
5. Lap -> adds current time to list.
6. Navigate away and return while running -> elapsed time remains correct.
