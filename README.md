# ♕ BYU CS 240 Chess

This project demonstrates mastery of proper software design, client/server architecture, networking using HTTP and WebSocket, database persistence, unit testing, serialization, and security.

## Phase 2: Server Diagram
[![SequenceDiagram.orgPresentation](diagram.png)](https://sequencediagram.org/index.html?presentationMode=readOnly#initialData=IYYwLg9gTgBAwgGwJYFMB2YBQAHYUxIhK4YwDKKUAbpTngUSWDABLBoAmCtu+hx7ZhWqEUdPo0EwAIsDDAAgiBAoAzqswc5wAEbBVKGBx2ZM6MFACeq3ETQBzGAAYAdAE5M9qBACu2GADEaMBUljAASij2SKoWckgQaIEA7gAWSGBiiKikALQAfOSUNFAAXDAA2gAKAPJkACoAujAA9D4GUAA6aADeAETtlMEAtih9pX0wfQA0U7jqydAc45MzUyjDwEgIK1MAvpjCJTAFrOxclOX9g1AjYxNTs33zqotQyw9rfRtbO58HbE43FgpyOonKUCiMUyUAAFJForFKJEAI4+NRgACUh2KohOhVk8iUKnU5XsKDAAFUOrCbndsYTFMo1Kp8UYdKUAGJITgwamURkwHRhOnAUaYRnElknUG4lTlNA+BAIHEiFRsyXM0kgSFyFD8uE3RkM7RS9Rs4ylBQcDh8jqM1VUPGnTUk1SlHUoPUKHxgVKw4C+1LGiWmrWs06W622n1+h1g9W5U6Ai5lCJQpFQSKqJVYFPAmWFI6XGDXDp3SblVZPQN++oQADW6ErU32jsohfgyHM5QATE4nN0y0MxWMYFXHlNa6l6020C3Vgd0BxTF5fP4AtB2OSYAAZCDRJIBNIZLLdvJF4ol6p1JqtAzqBJoIei0azF5vDgHYsgwr5kvDrco67F8H5LCBALnAWspqig5Q3Puh6wohPKouisTYgmhgumGbrlNO5iEHqtLlqO776K8SwmkS4YWhy3K8gagrCnaI6jDA5wwFUFGfqGNFup2WHlEx2iOs6BK4SyHq6pks7oCRbEoNRTICZGHLRjAsbBqJrrSkml6wfhQZyWgYmJsmUEllphEgPEiTZrmmD-iCMElFcr4oLM04md+V4uYU2Q9jA-aDr0HlecZjboEunCrt4fiBF4KDoHuB6+Mwx7pJkmCBReRTUNe0gAKK7kV9RFc0LQPqoT7dN5UWmT+bLOUZdYNU5lm-vlTryjACFpb6AaRXOmFythEn8VJ5JgFpjJDW1I18SpemFJaMAMTGQYOrp5r6d14KaVtolYRqkmkkYKDcJkWnzTODXKWaEarRyMiXRShizTpZ0Rq5B2KsqZnYRZQIlv9KrOYJflXL5BX+V2ORgH2A5mLFnjxRukK2ru0IwAA4qOrKZaeOXnswrnXrjZWVfYo51cN6DtlABQtRx9Omc5BRCTA0346Mqi3SZo2wadk3ndNn3yAL91LY9dHlBth1xl9os-QZPVwYr2nyIDBQ7e63MUrzahS4tety+tPIxsqeOjtt32Q4Ze44xAABmNujPGY2diz2OxEbqgOQgeadQ7bmllMNN8+MlT9JHKAAJLSNHACMvYAMwACxPCemQGhWEwzN0fQ6AgoANnnwEF08ccAHKjgXewwI0MPHHtuWI8FA4wN04d9HHqjRxUsejonKfp1nUw5-qpGjCshfziXZcV7PVcR6OdejA3TcxSuaProE2A+FA2DcPAMmGEbKRZWeCNsj+5Q3g01O08EbNDrXo4tx2wOpq1d1zt3JIPQP6jEgiDLqXNPR6iNibdAswQFKUBiLZaYsKQS2ALAtAD1aJqXlpbTWzEwj1TnDLHBv1eroKQThFW+EbQwIQdg1Sz1ygaSNnbFWocDpsOOl7PaLM4DnyNoHYO4DQ4ATXqMUe5RU6ZxgF-OG7ckZOEAb3OOUiYAyIznIlGu81wJQCJYS6CAIDJBgAAKQgDyd2hgAiLxAA2Emt9yZpmqJSO8LQ450wWugIcJ9gCGKgHACAxioDwJHtIeRzVOp-xMr40uASgkhLCZIiJHVRHkI1gAK0sWgGBxC4EwD8Qk4J0BkkJ2kELdWyDHpkjQUdSW+SsGkKYeyPBvJKFmz2lzShJ1qEoP1jzW22hYRqIqc0larSLa8m4drTpGTSgzOAFQia-T4IdAsTyGB4VCnxMoIk0p3NwmMImWtSkVRpAKHKtY9h-TOG9TBjrZm0SHkQ30vfUskS26kyUTouK+8AheH8V2L0sBgDYBPkRAgiQr7E3bnfKGlRiqlXKpVYwjMongNKGk1M8LHYgG4HgWElTRC62+uUfFIKiXjN2sw16V0Pr1OAIA6Qb1MisSgIKboLL6XXOVrcrpY0FRKhVL0n+wIhUA1ec46G6KvkIx+cuTAQA)


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
