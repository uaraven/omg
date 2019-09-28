## OMG Java DSL

Java DSL allows to create object matching patterns in runtime.

OMG works by defining tree of conditions which matching objects must satisfy and then
compiling those conditions to a `Pattern` which implements `Predicate` interface and can 
be used wherever `Predicate` can be used, for example in `.filter()` operation on a `Stream`.

Make no mistake, OMG DSL is a low level tool and one can easily shoot oneself in the foot with it. For all practical
purposes [OMQL](omql.md) is a better choice. JSON representation is more verbose and does not provide type-checking that OMQL 
provides and so is not recommended for production use.  

## Conditions

```java
import net.ninjacat.omg.conditions.Conditions;

...

Condition condition = Conditions.matcher()
    .property("firstName").eq("Sammy")
    .build();
```

Conditions builder can be created with a call to `Conditions.matcher()` method. From here
you can start adding property conditions or combine them with logical conditions.

Property conditions can be added with `.property(String)` method to set property name
and then chain call to a method defining operation and a matching value.

Multiple `.property()` calls can be chained together with implicit **AND** between them.

Logical conditions can be created on condition builder by calling one of the following
methods: `.and()`, `.or()` and `.not()`. These methods accept a lambda with one parameter
which is nested builder. New property conditions can be added to this builder.

For example to match objects where `firstName` is `Sam` or `Samuel` and age is between 45 and 65
one can use following code to build proper conditions:

```java
Condition condition = Conditions.matcher()
    .or(orCond -> 
        orCond.property("firstName").eq("Sam")
        orCond.property("firstName").eq("Samuel"))
    .and(andCond ->
         andCond.property("age").gt(45)
         andCond.property("age").lt(65))
    .build();
```
`.or()` and `.and()` calls are themselves combined using implicit AND operation.

Supported property operation are:
- `.eq(value)` - equal to
- `.neq(value)` - not equal to
- `.lt(value)` - less than
- `.gt(value)` - greater than
- `.in(List of values)` - value exist in list
- `.regex(value)` - regular expression match
- `.match(builder)` - matching on properties of the nested object.

Logical operations are:
- `.and()`
- `.or()`
- `.not()`. Note that while `.not()` accepts the same lambda with condition builder as parameter as `and` and `or` 
    operations it is an error to add more than one nested condition to that builder. Exception will be thrown when
    attempting to build `Condition` if `.not()` contains more than one nested condition. 

**Type safety**

Condition builder does not validate type of properties as it does not know what type these conditions will be applied to, 
it builds abstract tree of checks which is not tied to any specific class.

This has some unexpected outcomes. For instance one can build conditions that cannot be compiled to a Pattern. String
comparision only supports `equal`/`notEqual`/`regex` operations, but one can easily generate condition with `lessThan` 
operation and string operand (or any object operand really). Attempt to compile such condition will cause runtime 
exception to be thrown.

## Patterns

Conditions can be compiled into patterns for a specific class. `Pattern<T>` is an interface extending `Predicate<T>` 
interface and as such can be used anywhere where predicate is expected. `Pattern.matches()` (and `Predicate.test()`)
methods return `true` if provided object matches all the conditions for which pattern was compiled.
 

```java
import net.ninjacat.omg.patterns.Patterns;
import net.ninjacat.omg.patterns.PatternCompiler;

...

Pattern<Entity> pattern = Patterns.compile(
        condition, 
        PatternCompiler.forClass(Entity.class));

List<Entity> entities = ...

List<Entity> midAgeSams = entities.stream().filter(pattern).collect(Collectors.toList());

```

`Patterns.compile(condition, compiler)` compiles `condition` with `compiler`. Compiler is specific for a class, 
instances of which are to be tested. `PatternCompiler.forClass(Class<T>)` will return compiler specific for provided class.

There is an overloaded version of `PatternCompiler.forClass` method which accepts second parameter: `CompilingStrategy`.

OMG provides two compiler strategies: FAST and SAFE. Default strategy to use is FAST.
FAST generates bytecode optimized for a specific class and specific properties. SAFE strategy uses reflection to 
retrieve object property values. In testing bytecode-based Patterns showed to be 5-25 percent faster than reflection-based
ones. There is a big chance of improvement in performance as bytecode generator is improved.

Both SAFE and FAST strategies are identical in behavior. SAFE strategy is treated as a reference and new features are
first implemented and tested in reflection-based pattern compiler. 

Default strategy used by `PatternCompiler.forClass(Class<T>)` is `CompilingStrategy.FAST`.

At the time of compilation each condition is tested whether it can actually be applied to a property, based on property
type. If condition or condition value is incompatible with property, then a runtime exception will be thrown. 

For example following code defines condition priority > 4. As condition builder does not know anything about "priority"
it just builds condition tree for abstract "priority" property. You can consider conditions to be duck-typed at 
compilation time. 

When that condition is compiled for `Incident` class it works fine, as `getPriority()` method in `Incident` returns int.
Compiling the same condition for `Request` class fails, because `greaterThan` operation does not work for enum types.

```java
class Incident {
    public int getPriority();
}
enum Priority {
    LOW,
    MEDIUM,
    HIGH
}
class Request {
    public Priority getPriority();
};

Condition cond = Conditions.matcher().property("priority").gt(4).build();

Pattern<Incident> incidentPattern = Patterns.compile(cond, PatternCompiler.forClass(Incident.class)); // works
Pattern<Request> requestPattern = = Patterns.compile(cond, PatternCompiler.forClass(Request.class)); // fails. .gt() is not applicable to Enums

```

Properties in conditions are defined just as names of type string. When compiling conditions compiler looks for a 
0-arity method with the same name as a property name in condition and with a return type different from `void` or for a 
JavaBean-style getter for a property. 
For example for property `age` following methods will be considered (in this exact order):
- `age()`
- `getAge()`
- `isAge()`

There is no specific check that `is<Property>` should only be applicable for boolean properties. Only methods with public
access modifier are considered.
