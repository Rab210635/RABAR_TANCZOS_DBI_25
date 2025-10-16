FROM eclipse-temurin:23-jdk-alpine AS builder
WORKDIR /app
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} application.jar

RUN java -Djarmode=layertools -jar application.jar extract

FROM eclipse-temurin:23-jdk-alpine
WORKDIR /app

COPY --from=builder /app/dependencies/ ./
COPY --from=builder /app/spring-boot-loader/ ./
COPY --from=builder /app/snapshot-dependencies/ ./
COPY --from=builder /app/application/ ./

ENTRYPOINT ["java", "org.springframework.boot.loader.launch.JarLauncher"]
