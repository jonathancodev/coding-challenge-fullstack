FROM maven:3.9.8-eclipse-temurin-21
WORKDIR /app
COPY . /app
EXPOSE 8080
RUN mvn clean package
CMD ["mvn", "spring-boot:run"]