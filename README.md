# Scrabble

Desktop **Scrabble-style word game** written in **Java** with **JavaFX**. 
Uses the official [Tournament Word List](https://norvig.com/ngrams/TWL06.txt) dictionary and builds the tile bag from **`letters.csv`** (letter, count per tile type, point value).

## Running the program

### Windows

Download and run the `.msi` installer from the [latest release](https://github.com/DMFriends/scrabble/releases/latest).

When you run the latest `.msi`, it uninstalls previous versions of the app from your device.

### macOS and Linux

Linux and macOS packages are built by GitHub Actions when a GitHub Release is
published. The release workflow produces:

- Linux: `.deb`
- macOS: `.dmg`

You can download those packages from the [latest release](https://github.com/DMFriends/scrabble/releases/latest). You may need to manually uninstall previous versions.