# url-shortening

### Overview
**url-shortening** is a micro-service solution that allows storing short urls

### Architecture
Micro-services:
* api-service &#8722; service to create short urls and redirect by them
* id-service &#8722; service to identify api-services if multiple of them have been started
* hazelcast-service &#8722; IMDG service (aka cache) to store and retrieve urls 
* config-service &#8722; service to store all service configs
* eureka-service &#8722; service discovery
* zuul-service &#8722; entry-point service with LB

Storage:
* Hazelcast &#8722; IMDG solution included into hazelcast-service. Can be distributed, so all solution can be scaled easily

//TODO: add picture


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


For now convenient start (e.g via Docker) is not supported

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

###Improvements
##### Strict improvements:
* Integration tests
* Docker + docker-compose (add task to Gradle)
* Data storage under Hazelcast
* id-service must free IDs not in use
* Run services with tune memory

##### Optional improvements:
* Add NLB proxy before Zuul 
* Separate read and write operations into two services
* Set expiration time for urls
* Start of api-service must depend on heartbeats of id-service
* Add DeveloperConsole service
* Spring-boot-starters for common configs