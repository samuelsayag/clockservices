Clock Service
=============


Clock Service provide a basic RESTful API to:

* punch when starting working
		* if the endpoint is called with any String it should return an error
		* if the endpoint is called with nothing is should take the current 
		* if the endpoint is called with any String it should return an error
		* 
* punch when stoping working 
* provide a report of starting/stoping hours for each employee


# Enpoints

Method: POST

```
/clockservice/${[in|out]}/${employee}

{
 "timestamp":  "2021-02-15T19:08:22+00:00"
}

```

