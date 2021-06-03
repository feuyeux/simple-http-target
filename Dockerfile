# First stage: complete build environment
FROM maven:3.8.1-openjdk-17 AS builder

# add pom.xml and source code
ADD ./pom.xml pom.xml
ADD ./src src/

# package jar
RUN mvn clean package

FROM openjdk:17-jdk

COPY --from=builder target/http-target-0.0.1-SNAPSHOT.jar http-target-0.0.1-SNAPSHOT.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","http-target-0.0.1-SNAPSHOT.jar"]