FROM openjdk:21-jdk-slim
ENV TZ=Asia/Seoul
RUN apt-get install -y tzdata
WORKDIR /app
COPY build/libs/echo-0.0.1-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
