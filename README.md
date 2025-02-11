# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## Phase 2: Server Diagram
[SequenceDiagram.org Presentation](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5kvDrco67F8H5LCBALnAWspqig5Q3Puh6wohPKouisTYgmhgumGbrlNO5iEHqtLlqO776K8SwmkS4YWhy3K8gagrCnaI6jDA5wwFUFGfqGNFup2WHlBY6KOs6BK4SyHq6pks7oCRbEoNRTICZGHLRjAsbBtofEqdKSaXrB+FBnJaBiYmyZQSWWmESA8SJNmuaYP+IIwSUVyvigszTqZ35Xq5hTZD2MD9oOvSed5JmNugS6cKu3h+IEXgoOge4Hr4zDHukmSYEFF5FNQ17SAAoruxX1MVzQtA+qhPt0PnRWZP5si5xl1o1zlWb+BVOvKMAIelvoBlFc6YXK2ESfxUlGCg3CZFpw3taNulmhGhSWjIs0UoYWkOq6+lueCMCKsq5nYZZQIlidKouYJ-kKkqKrNQZXY5GAfYDmYcWeAlG6Qrau7QjAADio6sllp65eezBudewPlVV9ijvVI3oO2UAFK1HGo2ZLkFEJMDkmAoOjKoi0zo1Y2wQU+2koTFIk2o5Omcpq10eUDExsqINgzA3RCmEcCpCgIANjyjjFRkwuwFUCDAJYHYxDAAByj1nZ2WOA7EjOqI5CB5l1d2FWmPRTEjpPjJU-TmygACS0iWwAjL2ADMAAsTwnpkBoVhMMzdH0Ohy6LPvAX7Tw28ro5+3sMCNH5xudnl70hQOfNJKbfQ26olsVNbo7207rse1MXv6qRowrP785B6ADah5X4dm6OUejDHcexSuP3roE2A+FA2DcPAMmGIzKTZWeb1sj+5Q3g0iPI8EONDpHo4J8cF2pm1FNzunpar6MkGXd1BOenqjPM41swH0p6s4VNdNEw3KCX8ttNreyHM8jGQbMWEDVzhWrRF6BNPJ30mnpOmwAbQXxvrMTyrNgHrQ5JpG0PNRjxnGhrLq5Q4Aj0ZnrA2x8jbuVLM3UYhdyjO3djAdeHYXrJw+k4PemcbaUJgNQt2tCvpdzXIlAICtlQQGSDAAAUhAHk6DDABFrqLKGU9YZpmqJSO8LQbYoyWugIcg95aUDgBABA0Br4F2kHQ7qWMAFaN6DohWUB9GGKgMYihpjOrEMOn1AAVhItAF9LFoHfHLWx9ijGExMVTXqE0ZCSUfhSZ+r90CINUsgr+vJdraAFtjTRZl34kKOmArCGponun6h0cRPIL4RRgDYvRBiQlsOkIk-SyS+RVGkAoCqUi9pFJIVdNWz1TiMNTk4Hh8Ue4BC8PLLsXpYDAGwIPIiBBEjj0hsnae91KglTKhVKqxh0YtRwa41MayjIwBANwPAsJwmiBpkU8oZzpmXKAUkz+m05o7V-uk7o0gtqZFYlAQUXyfmj1HF0h+EZFEPVOv0wK0MmEjKAA)


## 10k Architecture Overview

The application implements a multiplayer chess server and a command line chess client.

[![Sequence Diagram](10k-architecture.png)](https://sequencediagram.org/index.html#initialData=C4S2BsFMAIGEAtIGckCh0AcCGAnUBjEbAO2DnBElIEZVs8RCSzYKrgAmO3AorU6AGVIOAG4jUAEyzAsAIyxIYAERnzFkdKgrFIuaKlaUa0ALQA+ISPE4AXNABWAexDFoAcywBbTcLEizS1VZBSVbbVc9HGgnADNYiN19QzZSDkCrfztHFzdPH1Q-Gwzg9TDEqJj4iuSjdmoMopF7LywAaxgvJ3FC6wCLaFLQyHCdSriEseSm6NMBurT7AFcMaWAYOSdcSRTjTka+7NaO6C6emZK1YdHI-Qma6N6ss3nU4Gpl1ZkNrZwdhfeByy9hwyBA7mIT2KAyGGhuSWi9wuc0sAI49nyMG6ElQQA)

## Modules

The application has three modules.

- **Client**: The command line program used to play a game of chess over the network.
- **Server**: The command line program that listens for network requests from the client and manages users and games.
- **Shared**: Code that is used by both the client and the server. This includes the rules of chess and tracking the state of a game.

## Starter Code

As you create your chess application you will move through specific phases of development. This starts with implementing the moves of chess and finishes with sending game moves over the network between your client and server. You will start each phase by copying course provided [starter-code](starter-code/) for that phase into the source code of the project. Do not copy a phases' starter code before you are ready to begin work on that phase.

## IntelliJ Support

Open the project directory in IntelliJ in order to develop, run, and debug your code using an IDE.

## Maven Support

You can use the following commands to build, test, package, and run your code.

| Command                    | Description                                     |
| -------------------------- | ----------------------------------------------- |
| `mvn compile`              | Builds the code                                 |
| `mvn package`              | Run the tests and build an Uber jar file        |
| `mvn package -DskipTests`  | Build an Uber jar file                          |
| `mvn install`              | Installs the packages into the local repository |
| `mvn test`                 | Run all the tests                               |
| `mvn -pl shared test`      | Run all the shared tests                        |
| `mvn -pl client exec:java` | Build and run the client `Main`                 |
| `mvn -pl server exec:java` | Build and run the server `Main`                 |

These commands are configured by the `pom.xml` (Project Object Model) files. There is a POM file in the root of the project, and one in each of the modules. The root POM defines any global dependencies and references the module POM files.

## Running the program using Java

Once you have compiled your project into an uber jar, you can execute it with the following command.

```sh
java -jar client/target/client-jar-with-dependencies.jar

♕ 240 Chess Client: chess.ChessPiece@7852e922
```
