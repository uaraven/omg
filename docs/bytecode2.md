

* *Target class* - class against which the matching is performed, denoted by **T** type parameter.
* *Property type* - type of the property (when generating code for matching this specific property). **P** type parameter.
* *Value type* - type of the value (should match **P** _or_ **Collection\<P\>**) 

Approach to generate code

- field retrieved with simple call to getter
```
aload 0 // load this
invokevirtual // get field value - puts field value on stack 
```
- for IN SELECT, it is call to getter and then call to getter
   
```        
aload 0 // load this
invokevirtual // get field value - puts field value on stack 
invokevirtual // get sub-field value
```

Values to compare with are constants, so they are loaded directly on stack
    
- Numbers: *CONST, ILOAD, LLOAD, LDC for large numbers, etc
- Strings: LDC

For complex matching, like regex 

    
    