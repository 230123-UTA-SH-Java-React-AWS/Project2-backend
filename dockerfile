FROM openjdk:8-jre-alpine

COPY ./target/project2-backend-0.0.1-SNAPSHOT.jar app.jar

ENV url=$url
ENV pguser=$pguser
ENV pgpassword=$pgpassword

EXPOSE 4798
ENTRYPOINT ["java", "-jar", "app.jar"]