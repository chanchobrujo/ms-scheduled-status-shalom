FROM openjdk:17

VOLUME /tmp
EXPOSE 8001
ARG FILE_NAME=target/ms-scheduled-status-0.3.jar
ADD ${FILE_NAME} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]