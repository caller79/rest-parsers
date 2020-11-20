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


### Date range parser

Parse a date range expression into a DateRange object which can be used to construct queries, calculate intervals, etc.
A date range expression is a combination of up to two date "moments", where every moment can be specified as an absolute or relative date.
For example, absolute dates can be expressed as:
* yyyy-MM-dd hh:mm:ss, for example 2019-02-23 00:30:00 
* yyyy-MM-dd, which is equivalent to yyyy-MM-dd 00:00:00

Relative dates can be expressed as:
* [number]m, for example 3m (meaning 3 minutes from now) or -3d (meaning 3 minutes ago)
* [number]h, for example 3h (meaning 3 hours from now) or -3d (meaning 3 hours ago)
* [number]d, for example 3d (meaning 3 days from now) or -3d (meaning 3 days ago)

The default relative unit, if omitted is "d", for days.

Relative dates can be truncated by adding character | at the end, for example 
* [number]m|, for example 3m| (meaning 3 minutes from now, rounded to the second. If now it is 16:58 with 15 seconds, "3m|" means 17:01:00 while "3m" means 17:01:15
* [number]h|, for example 3h| (meaning 3 hours from now, rounded to the second. If now it is 16:58 with 15 seconds, "3h|" means 19:00:00 while "3m" means 19:58:15
* [number]d|, for example 1d| (meaning 1 days from now, rounded to the second, in other words, the end of the day). If now it is Feb 23rd, "1d|" means Feb 24th at 00:00:00, no matter the time of the day it is.  

Ranges are formed by combining two date "moments", comma separated. For example:
* 0,1d| Represents the date interval between "now" and the end of the day.
* -7,0  Represents the last 7 days
* 1979-02-23 00:30:00,0 Represents the date interval between I was born and now.

The class is agnostic of timezones, unless for the methods that assume UTC.

A DateRange object can be obtained from an expression by parsing it:
```
DateRangeFactory dateRangeFactory = DateRangeFactory.builder().build();
// Can be customized specifying what is "now", otherwise uses current timestamp. 
// Notice this is "frozen" at the moment of the DateRangeFactory creation and used in all subsequent operations. 

DateRange range = dateRangeFactory.parseRange("0,30d");

Instant start = range.getStart(ZoneId.of("UTC")); // Returns now (the moment where the dateRangeFactory was created) in UTC. 
Instant end = range.getEnd(ZoneId.of("UTC")); // Returns now (the moment where the dateRangeFactory was created) + 30 days in UTC.
```
Operations range.contains() and range.overlaps() can be used to determine if this range includes a specific moment in time or overlaps it.

Suppose we have an event that starts in Chicago on 2021-02-22 19:28:00  
```
LocalDateTime eventDate = LocalDateTime.of(2021, 2, 22, 19, 28, 0);
```
Is this event happening in the next 30 days?
```
dateRangeFactory.parseRange("0,30d").contains(eventDate, ZoneId.of("America/Chicago"));
```
Is this event happening today, so I can still go?
```
dateRangeFactory.parseRange("0,1d|").contains(eventDate, ZoneId.of("America/Chicago"));
```
Is this event happening today, so I can still go, considering I am 1h away from there? In other words, is it starting at least 1h from now and is it today?
```
dateRangeFactory.parseRange("1h,1d|").contains(eventDate, ZoneId.of("America/Chicago"));
``` 

Christmas day 2021 is represented as "2021-12-25,2021-12-26". An event is starting in Tokyo now and it will last for 2 hours. 
```
DateRange christmas = dateRangeFactory.parseRange("2021-12-25,2021-12-26")
DateRange eventRange = dateRangeFactory.parseRange("0,2h")

ZoneId eventTimezone = ZoneId.of("Asia/Tokyo");
LocalDateTime eventStart = eventRange.getStart(eventTimezone).atZone(eventTimezone).toLocalDateTime();
LocalDateTime eventEnd = eventRange.getEnd(eventTimezone).atZone(eventTimezone).toLocalDateTime();

// Does this event, or part of it, happen during christmas day?
christmas.overlaps(eventStart, eventEnd, eventTimezone);
``` 

The typical usage of this parser is to provide date range based REST APIs and facilitate the translation into SQL queries or Predicates.

For example, suppose you have a database with a table of news articles with a publishing date and you want to expose a REST API to read news by publishDate.
Your API may look like:

` /api/news?fromDate=<xxx>&toDate=<yyy>`

This forces the frontend to know the server timezone for certain use cases. Instead, we can do 

` /api/news?dateRange=<zzz>`

And allow a date range to be passed, such as "-7,0" for "news published in the last 7 days".  Validation, parsing and conversion are handled by NumericRangeFactory:

```
DateRangeFactory dateRangeFactory = DateRangeFactory.builder().build();

try {
    DateRange range = dateRangeFactory.parseRange(dateRange); // dateRange="-7,0" for example
    Instant start = range.getStart(ZoneId.of(UTC)); // Assuming the server timezone is in UTC
    Instant end = range.getEnd(ZoneId.of(UTC)); 
    // ... query news articles between start and end and return them ...
} catch (IllegalArgumentException iae) {
    // Respond with a 400 error.
}
```

