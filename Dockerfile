FROM openjdk:17-jdk-slim
COPY target/efd-api-0.0.1-SNAPSHOT.jar efd-api-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/efd-api-0.0.1-SNAPSHOT.jar"]