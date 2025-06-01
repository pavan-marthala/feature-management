FROM openjdk:21
COPY target/feature-management-0.0.1-SNAPSHOT.jar /feature-management.jar
EXPOSE 8080
CMD ["java", "-jar", "/feature-management.jar"]
