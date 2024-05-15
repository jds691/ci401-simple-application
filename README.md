# Tetris

As a part of my University coursework I was required to build a simple application that runs on JavaFX.

The provided sample codebases didn't particularly interest me so I decided to build my own, Tetris.

The main selling point of this system is that it runs on a custom game library I built called Twig which provided me
with
more robust and easy to use features for building a game.

## Building

For security reasons the data required to login to the leaderboard database is excluded from the source code. 
To fix this and allow the program to compile, create a file called LeaderboardConnectionData.java inside of
src/main/java/com/neo/game/leaderboard with the following contents:
```java
package com.neo.game.leaderboard;

public final class LeaderboardConnectionData {
    public static final String URL = "";
    public static final String USERNAME = "";
    public static final String PASSWORD = "";
}
```

Ensure that you have cloned and installed [Twig](https://github.com/jds691/Twig) to your local Maven repository.

Install the project's dependencies via:

```
mvn -U clean install
```

To run the project you can use your IDE's built-in build and run tools. Alternatively you can run:

```
mvn javafx:run -f pom.xml
```
