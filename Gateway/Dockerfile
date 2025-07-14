FROM openjdk:22-jdk-slim-buster as build
WORKDIR /app

#Build jar
COPY . .
RUN chmod +x mvnw
RUN ./mvnw clean package -DskipTests

#Run the application
FROM openjdk:22-jdk-slim-buster
WORKDIR /app
COPY --from=build /app/target/*.jar program.jar
ENTRYPOINT ["java", "-jar", "program.jar"]