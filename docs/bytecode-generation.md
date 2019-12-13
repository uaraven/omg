# Compiled property matchers

To avoid performance hit of using reflection for accessing object properties, OMG can compile
property matchers for each condition at run time. This document intended for developers who want to 
understand/extend/improve bytecode generator.

## Structure

Code responsible for bytecode generation lives in `net.ninjacat.omg.bytecode` package. Classes of interest
are:
- `MatcherGenerator` - main compiler class producing instances of `Pattern`
- `PatternCompilerStrategy` - interface for different strategies used to generate code depending on condition and 
property type.

Packages `reference` and `primitive` contain strategies and helper classes used to generate patterns for
reference types (including all boxed types) and primitive types correspondingly.

Next sections will cover some of these classes in more detail

### `Pattern based matcher`

`...$Matcher` is generated matcher class implementing `PropertyPattern` interface.
Generated code has all the matching constants built-in the code (or synthetic helper methods). Generated constructor
does not accept any parameters.

`match(Object)` method from `PropertyPattern` interface is overwritten and first thing it does is casting its Object
parameter to target class and saving it in a local variable #2. Any implementation that needs to retrieve value of 
passed object should use `ALOAD 2` instruction.

#### Literals

Literals are the values that the object is matched against. In `MATCH ... WHERE age > 25 AND title ~ '.*manager.*'` *25* 
and *'.*manager.*'* are both literals. 

Primitive type literals and String literals used in simple =/!= type of comparison are created either in class constant 
pool or directly in bytecode (using *const, bipush/sipush or ldc/ldc_w instructions).
Primitive types may be boxed as necessary by call to the corresponding boxed type's `valueOf()` method.

When comparision is more complex than simple arithmetic or equals/not equals, like `~=` or `IN` operation then generating
literal becomes more complex.

For collections, a private final field of type `Collection<?>` is generated along with static private method that
creates unmodifiable Set from the element of collections. This static method is called inside constructor and its 
return value is stored in the field.

For regular expressions similar approach is used. Private final `java.regex.Pattern` field is added to the generated
class and it is initialized in constructor with a call to the `Pattern.compile(String)` method.

Any time literal is needed for comparison, it is retrieved from the field.

### `MatcherGenerator`

`MatcherGenerator` is an actual compiler which creates single class implementing `Pattern` interface. It's `match(Object)`
method contains all bytecode to compare object's properties against specified conditions.

`MatcherGenerator` aims to produce code with minimal code branching and method calls, for this all literals are 
injected directly into the code or class constant pool. In more complicated cases literals are stored in generated
class fields. Generated code is essentially a linear set of "if" checks unwrapped from condition tree.