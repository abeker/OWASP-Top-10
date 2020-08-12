FROM openjdk:8-jdk-alpine
EXPOSE 8761
COPY entrypoint.sh /entrypoint.sh
COPY target/Eureka-0.0.1-SNAPSHOT.jar eureka-1.0.0.jar
RUN chmod +x /entrypoint.sh
CMD ["/entrypoint.sh"]