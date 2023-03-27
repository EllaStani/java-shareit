FROM amazoncorretto:11
VOLUME /tmp
ARG JAR_FILE=target/*.jar
COPY ${JAR_FILE} app.jar
ENTRYPOINT ["sh","-c","java ${JAR_OPTS} -jar /app.jar"]