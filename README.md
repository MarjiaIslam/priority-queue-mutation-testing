# Java Priority Queue Mutation Testing

JUnit and PIT mutation testing assignment for a simplified Java `PriorityQueue` implementation.

## Project Contents

- `code/PriorityQueue.java` - priority queue implementation
- `code/MyTestsPQ.java` - JUnit test suite
- `code/output/` - generated PIT mutation report
- `junit-4.11.jar` - JUnit dependency
- `pitest*.jar` - PIT mutation testing dependencies
- `project4-description.pdf` - assignment specification

## Requirements

Use JDK 24 or below.

## Run the Tests

From `code/` on Windows:

```powershell
javac -cp ".;..\junit-4.11.jar" *.java
java -cp ".;..\junit-4.11.jar" MyTestsPQ
```

On Linux or macOS:

```sh
javac -cp ".:../junit-4.11.jar" *.java
java -cp ".:../junit-4.11.jar" MyTestsPQ
```

## Run PIT Mutation Testing

From `code/` on Windows:

```powershell
java -cp ".;..\junit-4.11.jar;..\pitest.jar;..\pitest-cmd.jar;..\pitest-entry.jar" org.pitest.mutationtest.commandline.MutationCoverageReport --reportDir output --targetClasses PriorityQueue,PriorityQueue$Itr --targetTests MyTestsPQ --sourceDirs . --timestampedReports=false --mutators DEFAULTS,REMOVE_CONDITIONALS,INLINE_CONSTS,CONSTRUCTOR_CALLS,NON_VOID_METHOD_CALLS,REMOVE_INCREMENTS
```
