# MEditor

A simple desktop text editor written in Java 21 and Swing.

## Features
- Type, delete, select, cut/copy/paste
- Search bar with live highlighting (Cmd+F)
- Find & Replace dialog (Cmd+Shift+F)

## Requirements
- JDK 21, Maven 3.9+

## Build and run
```
mvn package
java -jar target/MEditor-1.0-SNAPSHOT.jar
```
Or: `mvn compile exec:java`. Run tests with `mvn test`.

## macOS app
```
scripts/package-mac.sh
open target/dist/MEditor.app
```
