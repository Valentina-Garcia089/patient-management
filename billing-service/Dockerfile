FROM maven:3.9.9-eclipse-temurin-21 AS builder

WORKDIR /app

COPY billing-service/pom.xml .

RUN mvn dependency:go-offline -B ##instala las dependencias del proyecto

COPY billing-service/src ./src

RUN mvn clean package -DskipTests

FROM amazoncorretto:21-alpine AS runner

WORKDIR /app

COPY --from=builder /app/target/billing-service-0.0.1-SNAPSHOT.jar ./billing-service.jar

EXPOSE 4001
EXPOSE 9001

ENTRYPOINT ["java", "-jar", "billing-service.jar"]