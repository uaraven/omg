# OMG

### Object Matcher Generator

Java library to quickly match objects against a pattern declared at runtime with DSL or SQL-like language. `OMG` compiles patterns to bytecode to minimize performance penalty. If for some reason you cannot use bytecode compiled in runtime, reflection-based matchers are also available.

Supports matching properties of types:

- any numeric type + their boxed counterparts
- boolean and java.lang.Boolean
- String
- Enums
- Arbitrary objects 

Types not supported:
- collections
- arrays


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

See [OMG DSL](docs/dsl.md) for more details on how to use DSL and also some insights on how it works under the hood.

### Query language support

SQL-like language for defining conditions is recommended for practical use. OMQL (Object mapping query language) supports all operations that DSL supports, and does it in type-safe manner.

Previous examples written in OMQL syntax will look like

```sql
SELECT * FROM com.example.Person WHERE 
    lastName ~= "John.*on" OR lastName ~= "Smith" 
    AND firstName <> "Mary" AND age > 21 AND NOT friends < 1
```

List of fields in `SELECT` clause is ignored in current implementation. It is recommended to always use `SELECT *` to
ensure future compatibility.

If `FROM` should contain fully-qualified class name (or short name, if class was registered with OMQL parser). Conditions will be type-checked to validate that values can be used with the given properties. For example condition `age = "50"` 
will throw `TypeConversionException` during query parsing.
 
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
    passengerCapacity > 2 AND engine IN (SELECT * FROM Engine WHERE displacement < 3.2)
```

To create matcher from OMQL query use following code:
```
    final Condition condition = SqlParser.of("select * where ....").getCondition();

    final Pattern pattern = Patterns.compile(
        condition, 
        PatternCompiler.forClass(...));
```

For more details on how to use OMQL check [this](docs/omql.md) document. 

### Security Notice

Please note that OMQL allows whoever is writing the query to essentially **execute any method** with matching signature (public, no parameters non-void return type) on almost any public class on classpath.

See [OMQL documentation](docs/omql.md) for more details on security.

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

### Installation

#### Gradle
Add jitpack.io repository in your root build.gradle at the end of list of repositories:

	allprojects {
		repositories {
			...
			maven { url 'https://jitpack.io' }
		}
	}

Add the dependency

	dependencies {
	        implementation 'com.github.uaraven:omg:beta2'
	}
	
#### Maven

Add jitpack.io repository in your pom.xml

    <repositories>
		<repository>
		    <id>jitpack.io</id>
		    <url>https://jitpack.io</url>
		</repository>
	</repositories>

Add the dependency

	<dependency>
	    <groupId>com.github.uaraven</groupId>
	    <artifactId>omg</artifactId>
	    <version>beta2</version>
	</dependency>

### Build status

[![Build Status](https://travis-ci.org/uaraven/omg.svg?branch=master)](https://travis-ci.org/uaraven/omg) [![Coverage Status](https://coveralls.io/repos/github/uaraven/omg/badge.svg?branch=master)](https://coveralls.io/github/uaraven/omg?branch=master)
