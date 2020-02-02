FROM openjdk:8-jre-alpine

RUN addgroup -S spring && adduser -S spring -G spring
USER spring:spring

ARG service_name

RUN mkdir -p /home/spring/url-shortening
WORKDIR /home/spring/url-shortening

COPY ./$service_name/build/libs/$service_name.jar ./

CMD java -jar ${SERVICE_NAME_ENV}.jar --spring.profiles.active=docker
