FROM openjdk:17

VOLUME /tmp
EXPOSE 8000
ARG FILE_NAME=target/ms-scheduled-status-0.1.jar
ADD ${FILE_NAME} app.jar
ENTRYPOINT ["java", "-jar", "/app.jar"]