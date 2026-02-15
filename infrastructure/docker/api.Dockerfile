FROM maven:3.9.12-eclipse-temurin AS build

WORKDIR /app

COPY api/pom.xml ./
COPY api/src ./src

RUN mvn -DskipTests clean package

FROM eclipse-temurin:25

WORKDIR /app

COPY --from=build /app/target/*.jar api.jar

EXPOSE 8080

CMD ["java", "-jar", "api.jar"]