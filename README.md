# StartsWith-checker
A typechecker for enforcing that APIs start with string constants having certain properties in Java. One example would
be URLs where strings could start with different protocols like "https", "file", "path", etc. The StartsWith checker
ensures that all restrictions on properties of strings are met. The StartsWith checker has four types of annotations :
StartsWithUnknown, StartsWith, StartsWithBottom, and PolyStartsWith. The qualifier hierarchy for the
StartsWith checker type system is as following: StartsWithUnknown is the topmost type and is the default type.
StartsWithBottom is the bottom most type in the heirarchy. Types like StartsWith({"a"}) and StartsWith({"b"}) are
distinct and at the same level. StartsWith({"a"}) and StartsWith({"b"}) are super types of StartsWithBottom and
subtypes of StartsWith({"a", "b"}) whereas StartsWith({"a", "b"}) is a subtype of StartsWithUnknown and a supertype of
StartsWith({"a"}) and StartsWith({"b"}).
