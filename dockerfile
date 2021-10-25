FROM openjdk:12
USER root
ENV JAVA_OPTIONS=-Xmx512m
ENV SPRING_PROFILES_ACTIVE=prod,api-docs
ENV JHIPSTER_SLEEP=30
COPY ./target/portfolio-0.0.1-SNAPSHOT.jar .
COPY ./entry.sh .
EXPOSE 8080
RUN ["chmod", "+x", "/entry.sh"]
ENTRYPOINT "./entry.sh"
