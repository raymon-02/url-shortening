# url-shortening

### Overview
**url-shortening** is a micro-service solution that allows storing short urls

### Architecture
Micro-services:
* **api-service** &#8722; service to create short urls and redirect by them
* **id-service** &#8722; service to identify api-services if multiple of them have been started
* **hazelcast-service** &#8722; IMDG service (aka cache) to store and retrieve urls 
* **config-service** &#8722; service to store all service configs
* **eureka-service** &#8722; service discovery
* **zuul-service** &#8722; entry-point service with LB

Storage:
* [Hazelcast](https://hazelcast.com/) &#8722; IMDG solution included into hazelcast-service. Can be distributed, so all solution can be scaled easily

TODO: add picture


### API
If you run solution at `localhost` with default properties then entry-point url will be `localhost:8090`
All communication between services and API is REST
* Request: 
    ```
    GET /as/<shortUrl>
    ```  
  Response: 
    ```
    302 Found 
    Location: <long_url>
    ```
* Request: 
    ```
    POST /as 
    {
        "url" : "<long_url_here>""
    }
    ``` 
  Response: 
    ```
    200 OK 
    <shortUrl>
    ```


### Build and start
###### Requirements
* java >= 1.8 (make sure java is added to _$PATH_ environment)
* docker + docker-compose (optional: for convenient start only)

###### Building
Run the following command from the project root:
 ```bash
./gradlew cleand build
```

###### Starting
To start any service go the according folder (e.g. api-service) 
```bash
cd api-service/build/libs
```
and run
```bash
java -jar api-service.jar
```

To run multiple instances of service you should change the start-up port:
```bash
java -jar api-service.jar --server.port=[port]
```

###### Building docker images
Build the whole project as described in build section and then run the following command:
```bash
docker-compose build
```

###### Starting docker-compose
Project has _docker-compose.yml_ for example with the following service configuration:
* eureke-service - 1 instance
* config-service - 1 instance
* zuul-service - 1 instance
* hazelcast-service - 3 instances
* id-service - 1 instance
* api-service - 2 instances

As for now there is no dependencies between services they should be started in the order they mentioned above. 
So the start will look like:
```bach
docker-compose -d up eureka-service
docker-compose -d up config-service
docker-compose -d up zuul-service
docker-compose -d up hazelcast-service-1 hazelcast-service-2 hazelcast-service-3
docker-compose -d up id-service
docker-compose -d up api-service-1 api-service-2
```

Request API stays the same


### Load testing
For simple load testing [_ab_](https://httpd.apache.org/docs/2.4/programs/ab.html) utility can be used:
```bash
ab -p ./post.json -T application/json -n 100000 -c 4 -k http://localhost:8090/as
```
where _post.json_ file contains request body.
Read request:
```bash
ab -n 100000 -c 4 -k http://localhost:8090/as/<short_url>
```
where _<short_url>_ &#8722; one of short urls generated at previous step

Notice. As _docker-compose_ example has only one zuul-service it becomes a bottleneck.
So load request can be run against one instance of api-service to have approximate view about api-service performance.
However it can't present performance of all solution so to reduce zuul-service bottleneck more instances of it must be added.  


###Improvements
##### Strict improvements:
* Data storage under Hazelcast
* id-service must free IDs not in use
* Run services with tune memory
* Start some services must depend on other ones

##### Optional improvements:
* Integration tests
* Docker build images via Gradle task
* Add NLB proxy before Zuul 
* Separate read and write operations into two services
* Set expiration time for urls
* Add DeveloperConsole service
* Spring-boot-starters for common configs