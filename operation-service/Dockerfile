FROM maven:3.9.8-eclipse-temurin-21
WORKDIR /app
COPY . /app
EXPOSE 8081
RUN mvn clean package
CMD ["mvn", "spring-boot:run", "-Dspring-boot.run.jvmArguments=-Xms1024m -Xmx2048m"]