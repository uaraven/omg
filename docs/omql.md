# Object matching query language


## OMQL description

OMQL is declarative language for generating patterns for matching POJOs. It follows general style of SQL queries with its
own quirks.

```
    SELECT * FROM com.example.MyObject WHERE name ~= "John.*" AND friendsCount > 3 
```

Only supported type of queries is `SELECT` and queries must follow followin pattern:

SELECT * FROM `<class-name>` WHERE `<list-of-conditions>`

 In current implementation only `*` can appear in SELECT clause.
 
 `<class-name>` is either a fully-qualified Java class name or a short name of registered class (see below).
 
 `<list-of-conditions>` is a list (tree, to be precise) of conditions consisting of field comparison combined with logical 
 operators. List of supported operators depends on field type.
 
 |  Data type          | Operators supported                    |
 |:-------------------:|:---------------------------------------|
 | Numeric types       | `>`, `<`, `=`,  `!=`, `>=`, `<=`, `IN (list)` |
 | Boolean             | `=`, `!=`                              |
 | String              | `=`, `!=`, `~=`, `IN (list)`           |
 | Enum                | `=`, `!=`, `~=`, `IN (list)`           |
 | Object              | `=`, `!=`, `~=`, `IN (subquery)`       |
 
  
 ## Integrating queries into your code
 
 Queries are processed by `QueryCompiler` and result of the compilation is a `Condition` object that can be further
 compiled to either bytecode or reflective pattern. 
 
 ```java
    import net.ninjacat.omg.omql.QueryCompiler;
    import net.ninjacat.omg.patterns.Patterns;
    import net.ninjacat.omg.patterns.PatternCompiler;
    .
    .
    .
    final Condition condition = QueryCompiler.of("SELECT * FROM POJO WHERE things > 3000 AND otherThings = 0", POJO.class);
    final Pattern pattern = Patterns.compile(condition, PatternCompiler.forClass(POJO.class)); 
``` 

To compile a query into a condition one must provide list of classes used in that query. `QueryCompiler` uses this 
information to build type-correct conditions, also this list used as white list for classes that can be used in the query.

**Security notice**

Using OMQL poses a security risk, as query execution will execute any method matching properties mentioned in the query.
For example query `SELECT * FROM PasswdReader where file ~= 'user_name'` will look for one of following methods in `PasswdReader` class:
 - `String file()`
 - `String getFile()`
 - `String isFile()`
 
if any of these methods exist it will be executed. If this method is not a simple getter, but actually reads, for example, 
`/etc/passwd`, then this query can be used to check if login `user_name` exists on that machine.

Given this, starting from version 0.2.0, OMQL only supports queries on classes that were explicitly registered. 

For simplicity `QueryCompiler` can register all properties' types of explicitly provided classes. This greatly 
simplifies usage fields of object properties in queries.
 
`QueryCompiler` accepts `OmqlSettings` as last optional parameter. `OmqlSetting` can be configured for either allowing
automatic registration of properties' types or for disabling it. There are two shortcut methods: `OmqlSettings.easy()` 
for simplest usage experience at the cost of security and `OmqlSettings.strict()` for maximum security with more involved
configuration. Default is `OmqlSettings.strict()`.
   
Classes in `java.*` packages are exempt from registration requirements.

For example for classes in example below, only `Data` class has to be registered, `Subclass` class can be discovered 
automatically and this will allow queries on `subdata` field.


```java
    class Subclass {
        String getCode();
    }

    class Data {
        Subclass getSubdata();
    }
```

```java
    final Condition condition = QueryCompiler.of("SELECT * FROM Data WHERE subdata IN (SELECT * FROM Subclass WHERE code = '1533')", Data.class, OmqlSettings.easy()); // Works
    final Condition condition = QueryCompiler.of("SELECT * FROM Data WHERE subdata IN (SELECT * FROM Subclass WHERE code = '1533')", Data.class, OmqlSettings.strict()); // Will not work, Subclass is not whitelisted
    final Condition condition = QueryCompiler.of("SELECT * FROM SomeOtherData", Data.class, OmqlSettings.easy()); // Will not work, SomeOtherData must be registered explicitly 
    final Condition condition = QueryCompiler.of("SELECT * FROM com.example.SomeOtherData"); // Will not work  
```
All registered classes can be referenced in `FROM` clause using either their simple name or fully-qualified name. 
If two classes have the same simple name (i.e. `com.example.Pojo` and `com.example.other.Pojo`), then only the
first one can be accessed using simple name, other class(es) must be referenced using fully-qualified name. 

## Type-specific comparisons

### String

String support equality operations `=` and `!=` as well as regular expression operation `~=`. Strings can also be matched
against a list of possible values.
String literals must be enclosed in either single or double quotes.

```sql
    SELECT * FROM Object WHERE strField = 'SomeValue'
    SELECT * FROM Object WHERE strField != "SomeValue"
    SELECT * FROM Object WHERE strField ~= "(Some|Other)[vV]alue.*"
    SELECT * FROM Object WHERE strField IN ('A', 'B', 'C', 'D')
```

### Enums

Enum comparison support equality operations `=` and `!=` and find in list `IN` operator. Enum values must be enclosed
in single or double quotes and must match Enum values in Java enum class.

```sql
    SELECT * FROM Object WHERE weekDay = 'MONDAY'
    SELECT * FROM Object WHERE weekDay != "TUESDAY"
    SELECT * FROM Object WHERE weekDay ~= "S.*DAY"
    SELECT * FROM Object WHERE weekDay IN ('MONDAY', 'TUESDAY', 'FRIDAY')
```

As can be seen from example above enums can also be matched against regular expressions. This ability is not specific to
enums, any object can be matched to a regular expression. In that case object's `toString()` method is called and
its return value is matched against a regular expression.

### Objects

Object comparision support operators `IN` and `~=`. 

When using `IN` operator subquery is used instead of list. `FROM` expression in that subquery must contain type of the
object, `SELECT` expression is completely ignored and `IN` is considered to be matching when subquery returns a match.

For example following query will match objects where `person.homeAddress.city.equals("Toronto")` is true
```sql
    SELECT * FROM Person WHERE homeAddress IN (SELECT * FROM Address WHERE city = 'Toronto')
```

Starting from version 0.1.5 more natural syntax is supported for nested objects. Previous query can be rewritten as
 
```sql
    SELECT * FROM Person WHERE homeAddress.city = 'Toronto'
```

Query parser will rewrite the latter query as the former while processing conditions.
Multiple levels of nested objects is supported as well as any combinations with logical operators:

```sql
    SELECT * FROM Person WHERE homeAddress.city.name = 'Windsor' OR (
        homeAddress.city.name = 'Toronto' AND
        homeAddress.city.districtName IN ('Etobicoke', 'Scarborough'))
```
 