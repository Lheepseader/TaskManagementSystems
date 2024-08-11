FROM openjdk:21-jdk-slim

WORKDIR /app

COPY pom.xml ./
COPY src ./src

RUN ./mvnw clean package

COPY target/your-app.jar app.jar

ENTRYPOINT ["java", "-jar", "app.jar"]

EXPOSE 8080