# Employee Api Flow



When the application is running on the port ( eg 8080) Jetty is the embedded server which actively listens the http request on that port and when it receives the request for the port 8080 ,It redirects the url to the Jersey .

Jersey handles the url and does the routing managing , routing to the correct path in the resource class which inturn calls the employeeManager class for the Business logic .

Inside the EmployeeManager class  , validation of the input is written and once the validation is succesfull , we add the data to the dataBase using the employeeDAO class which communicates with the DB.

Also Jaskson is used to serialize and deserialize JSON to Kotlin objects


