FROM docker.io/eclipse-temurin:25-jdk-alpine AS application

LABEL authors="DevDojo Team" \
      project="DevDojo" \
      service="compiler-javascript"

WORKDIR /app

RUN apk add --no-cache \
    nodejs
#    npm \ -> draußen lassen wegen securty
#    bash \ -> draußen lassen wegen securty, vielleicht braucne wir das

# manually create user and group
RUN mkdir -p /home/application-user && \
    echo "application-user:x:1000:1000:application user:/home/application-user:/bin/sh" >> /etc/passwd && \
    echo "application-user:x:1000:" >> /etc/group && \
    mkdir -p /app && \
    chown -R 1000:1000 /app /home/application-user

# copy app JAR with correct access rights
COPY --chown=1000:1000 target/app.jar app.jar
COPY --chown=1000:1000 src/main/resources/executor_bootstrap.js /app/src/main/resources/

# Set the user to avoid running as root
USER application-user

# run app
ENTRYPOINT ["java", "-jar", "app.jar"]