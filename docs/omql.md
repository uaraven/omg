# Object matching query language

OMQL is declarative language for generating patterns for matching POJOs. It follows general style of SQL queries with its
own quirks.

```
    SELECT * FROM com.example.MyObject WHERE name ~= "John.*" AND friendsCount > 3 
```

