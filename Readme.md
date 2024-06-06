# Microservices Demo Project
This project is a demonstration of the use of 4 Spring Boot APIs with a clean design, a Swagger documentation for each API, detailed and documented error handling, and access management.

## Technologies
### Spring Boot
Spring Boot is used to develop the 4 APIs that make up the project. It provides a simple and efficient way to create standalone, production-grade Spring-based applications.

### Vue.js
The frontend of the project was developed using Vue.js, a popular JavaScript framework. Vue.js was chosen for its simplicity and flexibility, and its ability to create a complete and exhaustive presentation of the API's features using libraries such as Bootstrap and Bootswatch.

### Eureka
Eureka is used as a naming service in the middle of the project. It allows services to register themselves and discover other services. This allows for dynamic service discovery and load balancing.

#### Why Eureka?
Eureka is a good choice for this project because it allows for dynamic service discovery and load balancing. This means that if a service is added or removed, the other services can automatically discover it or stop using it, without having to be manually configured.

### Spring Cloud Gateway
Spring Cloud Gateway is used as an API gateway to route requests between the frontend and the 4 APIs. It was chosen over Zuul because Zuul is no longer maintained, and the project that has officially replaced it is Spring Cloud Gateway.

#### Why Spring Cloud Gateway?
Spring Cloud Gateway is a good choice for this project because it is a modern and actively maintained API gateway. It provides features such as request routing, filtering, and load balancing, which are necessary for a project with multiple APIs.

### Spring Cloud Config
Spring Cloud Config is used to decentralize the configuration of the microservices. This allows for easy adjustments to be made to the configuration of the project, even remotely, using a repository.

#### Why Spring Cloud Config?
Spring Cloud Config is a good choice for this project because it allows for the configuration of the microservices to be decentralized and managed in a simple and efficient way. This can be particularly useful for a project with multiple microservices, as it allows for easy adjustments to be made to the configuration of the project as a whole, without having to make changes to each individual microservice.

## Authentication
The project uses a bearer token-based authentication system, where the tokens are hashes of the user object. This allows for the differentiation between different types of users, such as administrators.

## Deployment
The project is accessible at https://froome.robin-joseph.fr. You can create an account or use the following credentials to log in as an administrator:
- Email: robin.joseph@gmail.com
- Password: string

To run the project locally, you can clone the master branch and start the docker-compose. The project will then be available on port 8984. Ports 8980, 8981, 8982, and 8983 are also mapped to access the Swagger documentation of the different APIs via /swagger-ui/index.html. Port 8986 is also mapped for the Eureka console. These mappings are not necessary if you do not need to access documentation or monitoring information, so feel free to disable them in the docker-compose if there is a conflict.