Clock Service 
=============

Clock Service provide a basic RESTful API to:

* punch when starting working
* punch when stoping working 
* provide a report of starting/stoping hours for each employee

## Techs

* Finch (https://finagle.github.io/finch/)
* Circe (https://circe.github.io/circe/)
* Cats Effects (https://typelevel.org/cats-effect/)

## Usage

```bash
$ sbt run
```

or 

```
$ bloop run root
```

The service will be locally accessible on the port `8081`

## API

### Insert a punching line:

Required:
* employee (if does not exist will be created)
* date 

Optional:
* time in
* time out

```
POST /line  
```

with following JSON

Param:

```json
{
"employee": "bob",
"date": "2021-01-01",
"in": "08:00",
"out": "16:30"
}
```

`in` and `out` are optional

Result

```json
{}
```

or an HTTP Error/Status


### Request a report:

Required:
* employee 
* year 
* month

```
POST /report/monthly
```

Param:

```json
{
  "employee": "bob",
	"year": 2021,
	"month": "JANUARY"
}
```

Result

```json
{
    "employee": "bob",
    "month": "JANUARY",
    "punchings": [
        {
            "date": "2021-01-01",
            "in": "08:00",
            "out": "16:30"
        }
    ],
    "workingHours": 30600,
    "year": 2021
}
```

or an HTTP Error/Status

## TIME

In all the JSON (param/result) the time is produce from / parsed from `java.time.*` abstraction:

* date: `LocalDate`
* time: `LocalTime`
* duration: `Duration`

So any date/time expressed as parsable by these entities will succeed being processed.

Report give duration of workingHours in seconds (purposely).

