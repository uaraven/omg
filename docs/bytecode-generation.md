# Compiled property matchers

To avoid performance hit of using reflection for accessing object properties, OMG can compile
property matchers for each condition at run time. This document intended for developers who want to 
understand/extend/improve bytecode generator.

## Structure

Code responsible for bytecode generation lives in `net.ninjacat.omg.bytecode` package. Classes of interest
are:
- `PropertyPatternGenerator` - main compiler class producing instances of `PropertyPattern`
- `PatternCompilerStrategy` - interface for different strategies used to generate code depending on condition and 
property type.
- `BasePropertyPattern` - class used as parent for most of generated `PropertyPattern` implementations (Not anymore)

Packages `reference` and `primitive` contain strategies and helper classes used to generate patterns for
reference types (including all boxed types) and primitive types correspondingly.

Next sections will cover some of these classes in more detail

### `PropertyPattern based matcher`

`...$Matcher` is generated matcher class implementing `PropertyPattern` interface.
Generated code has all the matching constants built-in the code (or synthetic helper methods). Generated constructor
does not accept any parameters.

All the actual logic of matching resides in static `_match` method, accepting one parameter of the target class.
`match(Object)` method from `PropertyPattern` interface is overwritten and all it does it casts its Object parameter
to target class and calls `_match` method.



### `PropertyPatternGenerator`

`PropertyPatternGenerator` is an actual compiler which creates, loads and instantiates implementation of `PropertyPattern` 
for a specific property of specific object.

`PropertyPatternGenerator` creates a new subclass of `BasePropertyPattern` (or similar) and overrides `mathes`
method, creating type-specific bytecode to retrieve value from object property and compare it to value in
`Condition`.

Depending on type, different code can be generated as governed by `PatternCompilerStrategy`

### `PatternCompilerStrategy`

`PatternCompilerStrategy` is an interface defining methods used by `PropertyPatternGenerator` to customize code 
generation depending on property type.