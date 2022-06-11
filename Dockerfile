FROM openjdk:8-alpine

COPY target/uberjar/clj-blog.jar /clj-blog/app.jar

EXPOSE 3010

CMD ["java", "-jar", "/clj-blog/app.jar"]
