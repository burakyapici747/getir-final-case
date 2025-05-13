FROM eclipse-temurin:21-jdk AS builder
WORKDIR /app

COPY mvnw .
COPY .mvn .mvn
COPY pom.xml .

COPY library-service/pom.xml library-service/pom.xml
COPY library-service/src      library-service/src

RUN chmod +x mvnw \
 && ./mvnw clean package -pl library-service -am -DskipTests spring-boot:repackage

FROM eclipse-temurin:21-jre
WORKDIR /app

COPY --from=builder /app/library-service/target/library-service-*.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java","-jar","app.jar"]
