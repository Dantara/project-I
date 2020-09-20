# Project-I

Project-I is compiler for generic Pascal-like imperative language 
with execution on top of Java Virtual Machine. 

*NOTE*: This application is created for education purpose.

### Prerequisites

This is project build with [Maven](https://maven.apache.org/).

You can download and install Maven fromm [here](https://maven.apache.org/download.cgi)
or use default package manager of your system.

## Build

To build this project simply run

```
mvn compile
```

This will fetch all dependencies, compile the project and generate one additional 
folder (`target`) in the root of the project.

## Run

To run this project, you have two ways.

Either you can create `.jar` file and execute it

``` 
mvn package
java -jar target/<FILE_NAME>.jar
```

Or you can use `Exec` Maven Plugin and simply type:

``` 
mvn exec:java
```

## Test

To run tests simply type:

``` 
mvn test
```

## File structure

Project has the following file structure:

```
.
├── pom.xml
├── README.md
└── src
    ├── main
    │   └── java
    │       └── projectI
    │           └── AST
    │           └── Lexer
    │           └── Parser
    │           └── SemanticAnalysis
    └── test
        └── java
            └── projectI
                └── Lexer
                └── Parser
                └── SemanticAnalysis
```

All code is placed in `src` folder. There are two subfolders: main and test. 
For code itself and tests respectively.
