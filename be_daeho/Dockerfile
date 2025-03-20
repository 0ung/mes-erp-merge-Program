FROM openjdk:17-jdk AS build

WORKDIR /app/backend

COPY build/libs/daehoInt-1.0.1-SNAPSHOT.jar /app/backend/app.jar

EXPOSE 8080

ENTRYPOINT [ "java", "-Dspring.profiles.active=prod", "-jar", "/app/backend/app.jar" ]
