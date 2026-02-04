############################################################################
# Eclipse Temurin Open JRE 17 Build
#
# build from project root dir with: docker build -t just-one:1.0.0-SNAPSHOT .
# run with: docker run --env-file .env -p 8080:8080 -d just-one:1.0.0-SNAPSHOT
############################################################################
FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="tait1337"

# Stage 1: Build
FROM maven:3.8.6-eclipse-temurin-17 as builder
WORKDIR /build
COPY . .
RUN mvn clean package

# Stage 2: Runtime
WORKDIR /app
COPY --from=builder ./target/just-one-1.0.0-SNAPSHOT.jar ./app.jar
EXPOSE $PORT
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]