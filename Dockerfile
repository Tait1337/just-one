############################################################################
# Eclipse Temurin Open JRE 17 Build
#
# build from project root dir with: docker build -t just-one:1.0.0-SNAPSHOT .
# run with: docker run --env-file .env -p 8080:8080 -d just-one:1.0.0-SNAPSHOT
############################################################################
FROM eclipse-temurin:17-jre-alpine
LABEL maintainer="tait1337"

# App
WORKDIR /app
COPY ./target/just-one-1.0.0-SNAPSHOT.jar ./app.jar
EXPOSE $PORT
ENTRYPOINT ["java", "-Xmx512m", "-jar", "app.jar"]