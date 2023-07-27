# Calculator App Overview

The app basically consists of typical layered architecture application with a standard ui application written in react and a backend application written in Java/Spring. The high level architecture of the application looks as the following:

![Diagram](src/main/resources/images/high_level_arch.png)

As it can be seen the application is deployed now in AWS, where the calculator ui part is deployed in a S3 bucket that hosts static websites and has a public ip. On the other hand the calculator api is deployed in a elastic beanstalk container where the application is encapsulated in a EC2 instance that is tied to an autoscaling group. The static content of that calculator api is being stored in a S3 bucket. The database that is being used now is a PostgreSQL database hosted in AWS RDS. It is also important to mention that the application api is only a monolith that holds all the functionality from the authentication, the users management and the operations which is the core of the application. The ui currently is storing the authentication state using redux to preserve the sessions. Here is a summary of how the application works under the hood:

![Diagram](src/main/resources/images/components_relationship.png)


The database model can be seen in the following diagram:
![Diagram](src/main/resources/images/model_design.png)

It is also important to mention that the app is currently being deployed to the cloud using the github actions pipelines.

## Calculator api - How to use it
###Live version:
* The live public endpoint for this api can be found [here](http://caculator-app-env.eba-zjmug9a6.us-east-1.elasticbeanstalk.com).
* There is already a user created; **_username_**: `admin@yopmail.com`, **_password_**: `password`
* There is a postman collection that can be used for the different endpoints.

###Local Environment:
* Clone the following repo https://github.com/davidgalvis95/calculator-api
* Run `mvn clean install`
* In the terminal go to the root folder of the repository and run `docker-compose up` to start the database.
* Run the following command `mvn spring-boot:run -Dspring-boot.run.profiles=local` to run the api.

With these simple steps you are all set up.



