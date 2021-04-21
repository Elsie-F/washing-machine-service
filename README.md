#### Test assignment
A backend service with REST API to control washing machine.

To run this application install Maven and JDK 11, then move to the project directory and run:

`$ mvn package`

`$ java -jar java -jar washing-machine-service-0.0.1-SNAPSHOT.jar`

To invoke REST API you can use, e.g., curl or Postman. Here is the list of endpoints:

`GET localhost:8080/programs/all` - view all available programs

`GET localhost:8080/programs/all` - view all available appliances

`GET localhost:8080/washes/all` - view all washes, including past, scheduled for future and canceled

`POST localhost:8080/washes` - create a new wash; sample request body:

`{
    "appliance" : 1,
    "program" : 2,
    "startTime" : "2021-04-21T13:00:00"
}`

`POST localhost:8080/washes/cancel/{id}` - cancel wash with specified id

