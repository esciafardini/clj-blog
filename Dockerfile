FROM openjdk:8-alpine

COPY target/uberjar/clj-blog.jar /clj-blog/app.jar

COPY prod-config.edn /clj-blog/config.edn

EXPOSE 3010

CMD ["java", "-jar", "-Dconf=/clj-blog/config.edn", "/clj-blog/app.jar"]
