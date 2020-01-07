# Byte code generator v2 details

Main code is in `MatcherGenerator` class, that takes care of generating code for all supported types.
Code generation for each type is implemented by subclasses of `TypedCodeGenerator` interface. Generic parameters of this
interface are:

* *Target class* - class against which the matching is performed, denoted by **T** type parameter.
* *Property type* - type of the property (when generating code for matching this specific property). **P** type parameter.
* *Value type* - type of the value (should match **P** _or_ **Collection\<P\>**)

## General approach

### Short history lesson

Byte code generator v1 worked by matching logic of reflection-based matcher. It generated a separate class for each
property test, instantiated them as it would for reflection-based classes and then combined using logical `Pattern`s 
implemented in net.ninjacat.omg.patterns package. Each generated class subclassed base class specific for the given
property type and providing some methods to read property value, convert primitives to boxed types, etc.  
Aside from the fact that byte code was inefficient and hard to follow that also created a lot of method calls during
actual matching.

### Byte code generator v2

Byte code generator v2 is set to fix some of the problems: 
 
- avoid different code patterns for different types
- generate single `match` method for each class being matched with all the bytecode inside this method (essentially 
  inlining `match` methods of Patterns for various properties)

Bytecode generator v2 makes very simple bytecode which follows pattern:

- 1. put matching constant onto the stack
- 2. read property value to stack
- 3. compare them

This three simple steps are repeated for each condition, and as a result of these steps there is either 0 or 1 left on 
the stack (boolean `false` or `true`). AND and OR operations are injected into byte code as simple `IAND` or `IOR` 
bytecode operations (there is a possibility of improvement here to implement short-circuited logical operations).

At the end there is a single boolean value left on the stack and `IRETURN` is added to the bytecode.

#### Put matching constant onto the stack

Values to compare with are always constants, so they are loaded directly onto the stack.
    
- Numbers: *CONST, ILOAD, LLOAD, LDC for large numbers, etc
- Strings: LDC
- Enums: GETSTATIC for enum value
- Lists, regexes - see below

For the IN and REGEX operations compared values are a bit more complex and cannot be loaded with simple `LDC`.
Generator adds synthetic field of type `Collection` for lists or `Pattern` for regexes and a static method
which produces `Collection` or `Pattern` and adds call to this method to constructor.

This way at the time of execution of `matches` method fields are populated with compiled patterns and collections 
(backed by sets, for performance) and value loading is a simple `GETFIELD`.

#### Read property value to stack

Object to match is always stored in local #2, so this is as sximple as 

```
aload 2 // load instance
invokevirtual // get field value - puts field value on stack 
```

#### Comparing them

Comparision step is depending on the property type and comparision operator. It can be a call to `Object.equals()` or
`Collection.contains()` or a simple `DCMPG` followed by `IFEQ`. The main requirement is that result of the comparision
must be a single boolean on stack (i.e. integer 0 or 1).


### MATCH operation

`MATCH` is the only operation that does not get inlined. For sub-expressions a new Matcher class is generated, then
instantiated in step 1. Step 2 is the same as before and step 3 is a simple call to `Pattern.matches()` method.