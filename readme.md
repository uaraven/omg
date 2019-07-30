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
```
    @Value
    class Entity {
        private final String firstName;
        private final String lastName;
        private final int age;
    }

    final Condition condition = Conditions.start()
        .or(cond -> cond
            .property("lastName").regex("John.*on")
            .property("lastName").regex("Smith.*"))
        .property("firstName").neq("Mary")
        .property("age").gt(21)
        .build();


    final Pattern pattern = Patterns.compile(condition, ReflectPatternCompiler.forClass(Entity.class));

    List<Entity> entities = ...;

    List<Entity> filtered = entities.stream().filter(pattern).collect(Collectors.toList());


```