FROM openjdk:21 as buildstage
WORKDIR /app
COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .
COPY src src
RUN ./mvnw package

FROM openjdk:21
WORKDIR /app
COPY --from=buildstage /app/target/*.jar app.jar
ENTRYPOINT ["java", "-jar", "/app/app.jar"]