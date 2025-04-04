# USE Monitor

This plugin adds runtime monitoring capabilities to USE.

## Instructions

### 1. Build the JAR with Maven

The following command will build the `MonitorPlugin.jar` in the target directory:
```
mvn clean package
```

### 2. Copy the MonitorPlugin.jar and its Dependencies Over to USE

- Copy `MonitorPlugin.jar` over to USE under `${USE_DIR}/lib/plugins`
- Copy `lablib-checkboxtree-3.2.jar` over to USE under `${USE_DIR}/lib`
- Copy `guava-12.0.jar` over to USE under `${USE_DIR}/lib`

### 3. Modify the USE Start Script

Add the following argument to the java command in the USE start script for your OS under `${USE_DIR}/bin`.
```
--add-exports jdk.jdi/com.sun.tools.jdi=ALL-UNNAMED
```
