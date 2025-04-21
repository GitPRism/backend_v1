# 🔹 1. 빌드 단계 (Builder Stage)
FROM openjdk:17-jdk-slim as builder
WORKDIR /app

COPY gradlew .
COPY gradle/ gradle/
COPY build.gradle settings.gradle ./
RUN chmod +x gradlew
RUN ./gradlew dependencies --no-daemon || true
COPY . .
RUN ./gradlew bootJar --no-daemon

# 🔹 2. 실행 단계 (Final Stage)
FROM openjdk:17-jdk-slim
WORKDIR /app

COPY --from=builder /app/build/libs/*.jar app.jar

# ✅ JAR 외부에서 application.yml을 override하도록 복사
COPY src/main/resources/application.yml ./config/application.yml

# ✅ 포트 노출
EXPOSE 8080

# ✅ 🔥 최종 실행 명령
CMD ["java", "-jar", "app.jar"]

