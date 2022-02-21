FROM ubuntu:20.04
USER root
RUN apt update && \
    apt -y install nodejs && \
    apt -y install npm && \
    apt -y install openjdk-11-jdk && \
    apt -y install maven

RUN export JAVA_HOME=/usr/lib/jvm/java-1.11.0-openjdk-amd64
EXPOSE 8080
ENV JAVA_OPTIONS=-Xmx512m
ENV SPRING_PROFILES_ACTIVE=prod,api-docs
ENV JHIPSTER_SLEEP=30
COPY . ./app
WORKDIR "/app"
RUN mvn package -Pprod -DskipTests -q -e
RUN ls
ENTRYPOINT ["java" ,"-jar", "/app/target/ar-xiv-ai-0.0.1-SNAPSHOT.jar"]

