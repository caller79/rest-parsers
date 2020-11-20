# rest-parsers
Some simple java parsers to make REST APIs powerful and concise

## Utilities included

### Numeric range parser

Parse a numeric range expression into an object which can be used to construct a Predicate or a SQL clause.

`MultipleNumericRange range = new NumericRangeFactory().getRangeFrom(String range)`

Example:

```
NumericRangeFactory factory = new NumericRangeFactory();
MultipleNumericRange range = factory.parse("(,0)[1,2](3,4)[5,6)(7,8]");

range.contains(-1); // returns true
range.contains(0); // returns false
range.contains(1); // returns true
range.contains(1.5); // returns true
range.contains(2); // returns true
range.contains(3); // returns false
range.contains(3.5); // returns true
range.contains(4); // returns false
range.contains(5); // returns true
range.contains(6); // returns false
range.contains(7); // returns false
range.contains(8); // returns true
range.contains(9); // returns false

range.overlaps(factory.parse("(-1,0]"))); // returns true
range.overlaps(factory.parse("[0,0.5]"))); // returns false
range.overlaps(factory.parse("[0,1]"))); // returns true
range.overlaps(factory.parse("[0,0.5][8,9)"))); // returns true
```

