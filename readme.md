# OMG

### Object Matching Gimmick

Java library to quickly match objects against a pattern declared as JSON (or with DSL)

Supports matching properties of types:

- long, int, short, byte, char and boxed types
- double, float and boxed types
- String
- Enums
- Arbitrary objects 

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
        .build();


    final Pattern pattern = Patterns.compile(
        condition, 
        ReflectPatternCompiler.forClass(Entity.class));

    List<Entity> entities = ...;

    List<Entity> filtered = entities.stream()
        .filter(pattern)
        .collect(Collectors.toList());

```

JSON pattern for previous example:

```json

[
  {
   "op": "and",
   "conditions": [
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
  } 
]  

```
