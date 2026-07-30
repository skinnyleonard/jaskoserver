FROM amazoncorretto:17

COPY jaskoracing-1.0.0.jar /api-v1.jar

ENTRYPOINT ["java", "-jar", "/api-v1.jar"]