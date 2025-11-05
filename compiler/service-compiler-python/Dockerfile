FROM docker.io/eclipse-temurin:21-jdk-alpine AS application

LABEL authors="DevDojo Team" \
      project="DevDojo" \
      service="compiler-python"

WORKDIR /app

# install Python and bash
RUN apk add --no-cache python3 py3-pip bash

# manually create user and group
RUN mkdir -p /home/application-user && \
    echo "application-user:x:1000:1000:application user:/home/application-user:/bin/sh" >> /etc/passwd && \
    echo "application-user:x:1000:" >> /etc/group && \
    mkdir -p /app && \
    chown -R 1000:1000 /app /home/application-user

# copy app JAR with correct access rights
COPY --chown=1000:1000 target/app.jar app.jar

# Set the user to avoid running as root
USER application-user

# run app
ENTRYPOINT ["java", "-jar", "app.jar"]