FROM openjdk:17-jdk-slim AS builder
WORKDIR /app

# gradlew와 관련 파일 먼저 복사
COPY gradlew .
COPY gradle gradle

# 실행권한 부여
RUN chmod +x gradlew

# 나머지 소스 복사
COPY . .

RUN ./gradlew bootJar --no-daemon

FROM openjdk:17-jdk-slim
WORKDIR /app
COPY --from=builder /app/build/libs/*.jar app.jar
ENTRYPOINT ["java", "-jar", "app.jar"]