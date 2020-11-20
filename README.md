# rest-parsers
Some simple java parsers to make REST APIs powerful and concise

## Introduction
This library provides some useful parsers to help constructing powerful REST APIs. If you are trying to solve something similar to the following problems, you may find it useful:
* I store products in a database and want to offer an API to the frontends so they can read them by price ranges.
* I have a table of contents of whatever kind and want to offer an API so they can be read by date, with plenty of flexibility, such as.
    * All items created in the last 7 days.
    * All items which will be unpublished today.
    * All items created yesterday.


## Utilities included

### Date range parser

To be implemented...


### Numeric range parser

Parse a numeric range expression into an object which can be used to construct a Predicate or an SQL clause, or compared with others.

`MultipleNumericRange range = new NumericRangeFactory().getRangeFrom(String range)`

Example:

```
NumericRangeFactory factory = new NumericRangeFactory();
MultipleNumericRange range = factory.parse("(,0)[1,2](3.141592,4)[5,6)(7,8][9]");

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
range.contains(9); // returns true
range.contains(10); // returns false

range.overlaps(factory.parse("(-1,0]"))); // returns true
range.overlaps(factory.parse("[0,0.5]"))); // returns false
range.overlaps(factory.parse("[0,1]"))); // returns true
range.overlaps(factory.parse("[0,0.5][8,9)"))); // returns true

range.getRanges(); 
// Returns a List<NumericRange> where each NumericRange represents each of the components
// of the original expression.  

range.toString()
// Returns "(,0)[1,2](3,4)[5,6)(7,8][9]"

range.toString(MultipleNumericRange.ToStringCustomizer.sqlCustomizer("products.price_in_usd"))
// Returns "(products.price_in_usd<0) OR (products.price_in_usd>=1 AND products.price_in_usd<=2) OR (products.price_in_usd>3 AND products.price_in_usd<4) OR (products.price_in_usd>=5 AND products.price_in_usd<6) OR (products.price_in_usd>7 AND products.price_in_usd<=8) OR (products.price_in_usd=9)"

```

The typical usage of this parser is to provide numeric range based REST APIs and facilitate the translation into SQL queries or Predicates.

For example, suppose you have a database with a table of products with price and you want to expose a REST API to read products by price range.
Your API may look like:

` /api/products?fromPrice=<xxx>&toPrice=<yyy>`

This only allows an interval and all the validations, parsing and translation into SQL needs to be done. Instead, you may define your API as

` /api/products?priceRange=<zzz>`

And allow a numeric range to be passed, such as [0,1000) for products with a price less than 1000 or [0,100)(500,600) for products between 0 and 100 (exclusive) or between 500 and 600 (both excluded) . Validation, parsing and conversion are handled by NumericRangeFactory:

```
NumericRangeFactory factory = new NumericRangeFactory();
try {
    MultipleNumericRange range = factory.parse(priceRange); // priceRange="[0,100)(500,600)"
    String query = "select * from products where " 
             + range.toString(MultipleNumericRange.ToStringCustomizer.sqlCustomizer("products.price"))
    // This produces a safe query with all the clauses that the range defines:
    // select * from products where (products.price>=0 AND products.price<1000) OR (products.price>500 AND products.price<600) 
} catch (IllegalArgumentException iae) {
    // Respond with a 400 error.
}
```



 

