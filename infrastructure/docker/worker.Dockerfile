FROM maven:3.9.12-eclipse-temurin AS build

WORKDIR /app

COPY shared/pom.xml ./shared/pom.xml
COPY worker/pom.xml ./worker/pom.xml

COPY shared/src ./shared/src
COPY worker/src ./worker/src

RUN mvn -f shared/pom.xml -DskipTests clean install
RUN mvn -f worker/pom.xml -DskipTests clean package

FROM eclipse-temurin:25

WORKDIR /app

COPY --from=build /app/worker/target/*.jar worker.jar

EXPOSE 8080

CMD ["java", "-jar", "worker.jar"]