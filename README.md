# Tetris

As a part of my University coursework I was required to build a simple application that runs on JavaFX.

The provided sample codebases didn't particularly interest me so I decided to build my own, Tetris.

The main selling point of this system is that it runs on a custom game library I built called Twig which provided me
with
more robust and easy to use features for building a game.

> [!NOTE]
> This branch was created for the [BSU IO Society](https://github.com/Brighton-IO) to be used at open days. The codebase
> contains references to this fact.
> The codebase will not be modified to include a generic version.

## Building

Ensure that you have cloned and installed [Twig](https://github.com/jds691/Twig) to your local Maven repository.

Install the project's dependencies via:

```
mvn -U clean install
```

To run the project you can use your IDE's built-in build and run tools. Alternatively you can run:

```
mvn javafx:run -f pom.xml
```

This project was built using IntelliJ so your millage may vary with other IDEs.

### Building a .jar

The game uses the [Maven Shade Plugin](https://maven.apache.org/plugins/maven-shade-plugin/) to build the .jar. This occurs in the package phase.

```
mvn package
```

Running the above command will produce a game-{version}.jar and a copy of the resources folder in the target directory.

By default, the game expects the two to be located within the same directory. To change the path of the resources folder, change the 
ResourcesConfig.resourceDirectory in GameApplication.java to your desired location.