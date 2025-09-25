FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY . .

# gradlew 실행 준비
RUN apt-get update && apt-get install -y dos2unix \
    && dos2unix ./gradlew \
    && chmod +x ./gradlew

RUN ./gradlew bootJar --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]