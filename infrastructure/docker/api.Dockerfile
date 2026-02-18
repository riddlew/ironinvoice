FROM maven:3.9.12-eclipse-temurin AS build

WORKDIR /app

COPY shared/pom.xml ./shared/pom.xml
COPY api/pom.xml ./api/pom.xml

COPY shared/src ./shared/src
COPY api/src ./api/src

RUN mvn -f shared/pom.xml -DskipTests clean install
RUN mvn -f api/pom.xml -DskipTests clean package

FROM eclipse-temurin:25

WORKDIR /app

COPY --from=build /app/api/target/*.jar api.jar

EXPOSE 8080

CMD ["java", "-jar", "api.jar"]