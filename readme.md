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
