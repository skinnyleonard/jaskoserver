FROM amazoncorretto:17-alpine-jdk

COPY jaskoracing-1.0.0.jar /api-v1.jar

ENTRYPOINT ["java", "-jar", "/api-v1.jar"]