############################################################################
# Adopt Open JDK 11 Build (379 MB)
#
# build from project root dir with: docker build -t just-one:1.0.0-SNAPSHOT .
# run with: docker run --env-file .env -p 8080:8080 -d just-one:1.0.0-SNAPSHOT
############################################################################
FROM adoptopenjdk/openjdk11:x86_64-alpine-jdk-11.0.9.1_1
LABEL maintainer="tait1337"

# App
WORKDIR /app
COPY ./target/just-one-1.0.0-SNAPSHOT.jar ./app.jar
EXPOSE $PORT
ENTRYPOINT ["java", "-jar", "app.jar"]