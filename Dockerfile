FROM amazoncorretto:17

WORKDIR /app

COPY assets /app/assets

COPY jaskoracing-1.0.0.jar /app/api-v1.jar

ENTRYPOINT ["java", "-jar", "/api-v1.jar"]