When the user enters the url , jetty is the web listerner who will immediately  , send the json to  the class JAX-RS containing the jakarta rs , , jersey / jackson is responsible 
for converting your kson to employee object . Jersey helps to route to the correct functions . once the appropiate path is oruted to the method , the method calls and 
inturn it execites the employee service class which has the main business logic . This class will validate adn it will call the employeeDao class which handles the query and communicates
with the DB . As a result the db sends the number of rows f success else sql exception.
