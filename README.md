# StartsWith-checker
A typechecker for enforcing that APIs start with string constants having specific substrings. An example application
is URLs where strings start with accepted protocols like "https", "file", "jar:https", and "jar:file".
For instance, "www.google.com" would get rejected while "https://www.google.com" would get accepted by a type system
that accepts strings which start with "https://".
The checker has four qualifiers:
* `@StartsWith(String[] acceptedStrings)`:
        An expression with this type represents a string that starts with the same substring as at least one string from
        `acceptedStrings`. The annotation takes an array of accepted strings as an argument to type check against.
        For example, to enforce that all URLs start with "https://", "file://", "jar:https// and "jar:file//":
        
```java
class URL{
   public URL(@StartsWith({"https://", "file://", "jar:https://", "jar:file://"}) String spec);
}
```

   `@StartsWith(String[] acceptedStrings)` annotation is the super type of `@StartsWithBottom` and the subtype of
   `@StartsWithUnknown`. `@StartsWith(a)` is a subtype of `@StartsWith(b)` if all strings in array `a` start with atleast
    one string from array `b`.
    For example,  `@StartsWith({"https://", "file://"})` is a subtype of `@StartsWith({"h", "f"})`.

* `@PolyStartsWith`:
        indicates qualifier polymorphism. For a description of qualifier polymorphism, 
        see (https://checkerframework.org/manual/#qualifier-polymorphism).

* `@StartsWithUnknown`:
        No information is known about which strings start with the same substring as the expression. This is the top
        type, and programmers should never need to write it.

* `@StartsWithBottom`:
        This is the bottom type and the programmers should never need to write it.
