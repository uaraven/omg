# OMG

### Object Matcher Generator

Java library to quickly match objects against a pattern declared as JSON (or with DSL)

Supports matching properties of types:

- long, int, short, byte, char and boxed types
- double, float and boxed types
- String
- Enums
- Arbitrary objects 

Types not supported:
- collections
- arrays

`OMG` generates property-specific matchers and compiles them to bytecode to minimize performance penalty. 
Reflection-based matchers are also available.

Example:
```java
    @Value
    class Entity {
        private final String firstName;
        private final String lastName;
        private final int age;
    };

    final Condition condition = Conditions.matcher()
        .or(cond -> cond
            .property("lastName").regex("John.*on")
            .property("lastName").regex("Smith.*"))
        .property("firstName").neq("Mary")
        .property("age").gt(21)
        .not(n -> n.property("friends").lt(1))
        .build();


    final Pattern pattern = Patterns.compile(
        condition, 
        PatternCompiler.forClass(Entity.class));

    List<Entity> entities = ...;

    List<Entity> filtered = entities.stream()
        .filter(pattern)
        .collect(Collectors.toList());

```

JSON pattern for previous example:

```json

[
  {
   "op": "or",
   "value": [
    {
      "op": "regex",
      "property": "lastName",
      "value": "John.*on"
    },
    {
      "op": "regex",
      "property": "lastName",
      "value": "Smith.*"
    }
   ]
  },
  {
    "op": "neq",
    "property": "firstName",
    "value": "Mary"
  },
  {
    "op": "gt",
    "property": "age",
    "value": 21
  },
  {
    "op": "not",
    "value": {
      "op": "lt",
      "property": "friends",
      "value": 1
    }
  }
]  

```

### SQL support

Limited support for SQL-like queries is provided.
Previous examples written in SQL syntax will look like

```sql
SELECT * FROM Anything WHERE 
    lastName ~= "John.*on" OR lastName ~= "Smith" 
    AND firstName <> "Mary" AND age > 21 AND NOT friends < 1
```

List of fields in `SELECT` clause is ignored as well as `FROM` clause. In fact `FROM` can be dropped entirely.
Supported operations include:

- `=`, `!=`, `<>`, `>`, `<`, `>=`, `<=` - standard comparison operations
- `~=' - regular expression matching
- IN (list of elements)
- IN (subquery)

For `IN (list)` list is comma-separated list of literals. All literals must have the same type as the first element of the list.

`IN (subquery)` can be used to filter on fields containing other objects, for example
```java
  class Engine {
    double displacement;
    double numberOfCylinders;
  } 
  class Car {
    Engine engine;
    int passengerCapacity;
    double grossWeight;
  }   
```
```sql
SELECT * FROM Car WHERE 
    passengerCapacity > 2 AND engine IN (SELECT * WHERE displacement < 3.2)
```

To create matcher from SQL query use following code:
```
    final Condition condition = SqlParser.of("select * where ....").getCondition();

    final Pattern pattern = Patterns.compile(
        condition, 
        PatternCompiler.forClass(...));
```

**Notes**

SQL parser does not know types of fields it needs to match, so produced literals are converted into Java types using 
simple approach:

 - anything looking like floating point number is treated as `double`.
 - anything looking like integer number is treated as `int` or as `long`  if it cannot fit into `int`.
 - anything in single or double quotes is treated as `String`
 - anything else causes parsing exception. 

### Benchmark

Compiled patterns are 5-25% faster than reflection-based, depending on complexity of patterns and many other factors (as you may have guessed). This shows that JVM is actually pretty good at optimizing reflective calls.

Following benchmarks were executed with JMH version 1.21 and JDK 1.8.0_222, OpenJDK 64-Bit Server VM, 25.222-b10 on Ubuntu.

| Invocation Mode  | Ops/s                    | Speed |
|:-----------------|:-------------------------|------:|
| Reflection A     | 3413630.079 ±  52150.917 | 100%  |
| Bytecode A       | 3933886.521 ± 146567.294 | 115%  |
| Reflection B     | 4401189.267 ±  15632.381 | 100%  |
| Bytecode B       | 5675514.429 ± 225816.952 | 128%  |


Invocation mode A - using new object with different field values every time.

Invocation mode B - using new object every time but with the same field values.

Speed column shows speed of bytecode comparing to reflection of the same invocation mode (A or B).

Source code for benchmark is in `benchmark` folder.

### Build status

[![Build Status](https://travis-ci.org/uaraven/omg.svg?branch=master)](https://travis-ci.org/uaraven/omg) [![Coverage Status](https://coveralls.io/repos/github/uaraven/omg/badge.svg?branch=master)](https://coveralls.io/github/uaraven/omg?branch=master)
