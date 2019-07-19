# StartsWith-checker
A typechecker for enforcing that APIs start with string constants having specific substrings. An example application
is URLs where strings could start with different accepted protocols like "https", "file", "path", etc.
For instance, "www.google.com" would get rejected while "https://www.google.com" would get accepted by a type system
that accepts strings which start with "https".
The StartsWith checker has four types of annotations :
* @StartsWith(String[] acceptedStrings):
        An expression with this type represents a string that starts with the same substring as at least one string from
        acceptedStrings. The annotation takes an argument of an array of accepted strings to type check against.
        For example, to enforce all URLs to only accept strings that start with "https", "file", and "path":
            `class URL{
                public URL(@StartsWith({"https", "file", "path"}) String spec);
            }`
        @StartsWith(String[] acceptedStrings) annotation is the super type of @StartsWithBottom and the subtype of
        @StartsWithUnknown. @StartsWith(a) is a subtype of @StartsWith(b) if all strings in array a start with atleast
        one string from array b.
        For example, `@StartsWith({"https", "path"})` is a subtype of `@StartsWith({"h", "b"})`.

* @PolyStartsWith:
        indicates qualifier polymorphism.

* @StartsWithUnknown:
        No information is known about which strings start with the same substring as the expression. This is the top
        type, and programmers should never need to write it.

* @StartsWithBottom:
        This is the bottom type and the programmers should never need to write it.
