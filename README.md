# Use Plugins

This repository contains plugins for [USE - UML-based Specification Environment](https://github.com/useocl/use).

## Requirements

This project relies on the compiled `use-core` and `use-gui` modules of the `use` project. Build them and save them
to your local Maven repository with the following command:
```
mvn clean install
```

## Maven Build

The following command will build all the plugins in this project that have respective pom.xml files:
```
mvn clean package
```

## Maven Support

Currently, only the `Monitor Plugin` can be built with Maven. If you require any other plugins, you will need to 
use the .jardesc files on the Eclipse IDE or convert them yourself to Maven pom.xml files.
