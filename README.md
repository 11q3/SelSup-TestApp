# SelSup-TestApp

It is necessary to implement a class in Java (you can use
version 17) to work with the Fair Sign API. The class must be
thread-safe and maintain a limit on the number of
API requests. The limit is specified in the constructor in the form of the number
of requests in a certain time interval. 

For example: public CrptApi(TimeUnit TimeUnit, int requestLimit)
TimeUnit – specifies the time interval – second, minute, etc
. requestLimit is a positive value that determines
the maximum number of requests in this time interval.

If the request limit is exceeded, the call must be blocked
so as not to exceed the maximum number of API requests and
continue execution, without throwing an exception when
the limit on the number of API calls is not exceeded as a
result of this call. In any situation, it is forbidden for the method to exceed the limit on
the number of requests.
The only method that needs to be implemented is the creation of a document for
the entry into circulation of goods produced in the Russian Federation. The document and signature
must be passed to the method as a Java object and string
, respectively.

The following URL is called via HTTPS POST method:
https://ismp.crpt.ru/api/v3/lk/documents/create
The request body contains a document in JSON format: 

{"description":
{ "participantInn": "string" }, "doc_id": "string", "doc_status": "string",
"doc_type": "LP_INTRODUCE_GOODS", 109 "importRequest": true,
"owner_inn": "string", "participant_inn": "string", "producer_inn":
"string", "production_date": "2020-01-23", "production_type": "string",
"products": [ { "certificate_document": "string",
"certificate_document_date": "2020-01-23",
"certificate_document_number": "string",
"owner_inn": "string",
"producer_inn": "string",
"production_date": "2020-01-23",
"tnved_code": "string",
"uit_code": "string",
"uitu_code": "string" } ],
"reg_date": "2020-01-23",
"reg_number": "string"}

When implementing, you can use the HTTP client libraries,
JSON serialization. The implementation should be as
convenient as possible for further expansion of the functionality.
The solution should be designed as a single file
CrptApi.java. All additional classes that are used
must be internal.
You can send a link to the file to GitHub.
In the task, you just need to make a call to the specified method,
the real API should not be of interest.
