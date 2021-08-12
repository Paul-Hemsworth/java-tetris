# java-tetris
This is a personal project to re-create the game Tetris using Java SE 15 and JavaFX 11.

<img src="https://github.com/Paul-Hemsworth/java-tetris/blob/master/images/tetris-start.png" alt="main app window" width="300px"></img>
<img src="https://github.com/Paul-Hemsworth/java-tetris/blob/master/images/tetris-game.png" alt="tetris game" width="300px"></img>

## Building
Currently, the only build in the distribution (dist) folder of this repository is for Linux computers.
You may try to execute the Ant build target on Windows or OS X, but I can't guarantee it will work at this time.
The Ant build file "build.xml" creates a standalone application placed in a folder called "dist." 
The paths to the base Java and JavaFX jmods must be correct for the Ant build to work on another computer.

## Running
Copy the "dist" directory somewhere on your computer. A launcher script called "tetris" in the "bin" directory of the Linux build will start the program.
From the "dist" directory and in a command prompt, the program may be run by entering `./bin/tetris`
