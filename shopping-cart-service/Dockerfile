FROM maven:3.9.5-eclipse-temurin-17 AS builder

WORKDIR /app

COPY pom.xml .

RUN mvn dependency:go-offline -B

COPY src ./src

RUN mvn clean package -DskipTests

FROM eclipse-temurin:17-jre-alpine AS runner

WORKDIR /app

COPY --from=builder ./app/target/shopping-cart-service-0.0.9.jar ./app.jar

EXPOSE 4006

ENTRYPOINT ["java", "-jar", "app.jar"]