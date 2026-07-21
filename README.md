# MP3 Player

A lightweight desktop MP3/audio player built with **Java and JavaFX**. Supports playlist management (add/delete songs), play/pause/restart, skip forward/back, adjustable playback speed, volume control, and a live progress bar.

---

## Features

- **Song library** — automatically loads audio files from a local `songs/` folder next to the app
- **Add songs** — file picker to import `.mp3`, `.wav`, `.m4a`, or `.flac` files, copied into the `songs/` folder (with an overwrite-confirmation prompt if a file already exists)
- **Delete songs** — remove tracks from your library from the song list screen, with confirmation and safe file-lock handling
- **Playback controls** — play, pause, restart, next, previous
- **Auto-advance** — automatically loops back to the first track after the last one finishes
- **Speed control** — adjustable playback rate (25% – 200%)
- **Volume control** — live volume slider
- **Progress bar** — updates every second, synced to the current track's playback position
- **Two-screen UI** — main player screen and a separate song-library management screen

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| UI Framework | JavaFX (Controls, FXML, Media) — SDK 24.0.2 |
| UI Definition | FXML (`MainScene.fxml`, `SongsPage.fxml`) |
| Build/Run | Plain `javac`/`java` via VS Code (no Maven/Gradle) |
| Packaging | Runnable `.jar`, and a Windows `.exe` via [Launch4j](https://launch4j.sourceforge.net/) |

---

## Prerequisites

- **JDK** (matching the JavaFX SDK version you use, e.g. JDK 21+)
- **JavaFX SDK 24.0.2** — [download here](https://gluonhq.com/products/javafx/), since JavaFX is no longer bundled with the JDK
- VS Code with the **Java Extension Pack** (recommended, since this project isn't Maven/Gradle-based)

---

## Project Structure

```
Mp3player/
├── src/
│   ├── App.java                  # JavaFX application entry point
│   ├── controller.java           # Main player screen logic
│   ├── songPageController.java   # Song library management screen logic
│   ├── PlayerRegistry.java       # Tracks the active MediaPlayer so it can be
│   │                              # safely stopped/disposed (e.g. before deleting a file)
│   ├── MainScene.fxml            # Main player UI layout
│   ├── SongsPage.fxml            # Song library UI layout
│   └── resources/
│       ├── icon.png
│       └── mp3icon.ico
├── bin/                          # Compiled output (mirrors src/ layout + FXML/resources)
├── Mp3player.jar                 # Prebuilt runnable JAR
├── Mp3player.exe                 # Prebuilt Windows executable (via Launch4j)
└── .vscode/
    ├── launch.json               # Run configuration (sets JavaFX module path)
    └── settings.json             # Source paths + referenced JavaFX libraries
```

---

## Getting Started

### Option 1: Run the prebuilt executable (Windows)

Just run `Mp3player.exe` — no Java setup required if a compatible JRE is bundled/available.

### Option 2: Run the JAR

```bash
java --module-path "path/to/javafx-sdk-24.0.2/lib" --add-modules javafx.controls,javafx.fxml,javafx.media -jar Mp3player.jar
```

### Option 3: Run from source in VS Code

1. Clone the repo:
   ```bash
   git clone https://github.com/khuslee0409/MP3player.git
   cd MP3player/Mp3player
   ```
2. Download the [JavaFX SDK](https://gluonhq.com/products/javafx/) and update the paths in `.vscode/settings.json` and `.vscode/launch.json` to match where you extracted it.
3. Open the folder in VS Code with the Java Extension Pack installed.
4. Run the **"App"** launch configuration (defined in `.vscode/launch.json`).

### Adding music

On first run, create a `songs` folder in the same directory as the JAR/EXE (or use the **Add** button on the song library screen) and drop your `.mp3`/`.wav`/`.m4a` files in. The player scans this folder on startup.

---

## Known Notes

- Playback file paths currently assume a `songs/` folder located **relative to the working directory** the app is launched from — running the JAR/EXE from a different directory may not find your songs.
- This is a plain `javac`-based VS Code project rather than Maven/Gradle, so dependency paths (JavaFX SDK location) are hardcoded in `.vscode/settings.json` and will need updating for other machines/environments.
- The `bin/` folder (compiled output) is committed to the repo — consider adding it to `.gitignore` if you don't intend to track build artifacts.

---

## License

No license specified yet.
