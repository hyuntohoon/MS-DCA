FROM openjdk:17-jdk-slim AS builder
WORKDIR /app
COPY . .

# gradlew 실행 권한만 주기
RUN chmod +x ./gradlew

RUN ./gradlew bootJar --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]