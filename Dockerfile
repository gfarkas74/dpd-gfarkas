FROM maven:3-openjdk-17 AS java-builder

WORKDIR /dpd-gfarkas
COPY ./src /dpd-gfarkas/src
COPY ./pom.xml /dpd-gfarkas

RUN mvn clean package

COPY ./target/gfarkas-0.0.1-SNAPSHOT.jar /dpd-gfarkas/gfarkas.jar

CMD ["java","-Dspring.profiles.active=docker","-jar","gfarkas.jar"]
