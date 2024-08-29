FROM maven:3.9.8-eclipse-temurin-21
WORKDIR /app
COPY . /app
EXPOSE 9000
RUN mvn clean package
CMD ["mvn", "spring-boot:run"]