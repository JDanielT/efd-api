FROM openjdk:17-jdk-slim
COPY target/efd-client-0.0.1-SNAPSHOT.jar efd-client-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","/efd-client-0.0.1-SNAPSHOT.jar"]