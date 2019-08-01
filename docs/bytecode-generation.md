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
- `BasePropertyPattern` - class used as parent for most of generated `PropertyPattern` implementations

Packages `reference` and `primitive` contain strategies and helper classes used to generate patterns for
reference types (including all boxed types) and primitive types correspondingly.

Next sections will cover some of these classes in more detail

### `BasePropertyPattern`

`BasePropertyPattern` is an abstract class which is basis for almost all generated `PropertyPattern` implementations.
It provides constructor and getters for `property` and `matchingValue` fields. `property` field is generated from the
actual matched class and `matchingValue` is retrieved from the `Condition` being matched.
