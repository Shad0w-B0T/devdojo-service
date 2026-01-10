FROM docker.io/eclipse-temurin:25-jre-alpine as application

LABEL authors="DevDojo Team" \
      project="DevDojo" \
      service="service-api"

WORKDIR /app

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

EXPOSE 80/tcp

# run app
ENTRYPOINT ["java", "-jar", "app.jar"]