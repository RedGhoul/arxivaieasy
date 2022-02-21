FROM ubuntu:20.04
USER root
RUN apt update && \
    apt -y install nodejs && \
    apt -y install npm && \
    apt -y install wget

RUN wget https://github.com/graalvm/graalvm-ce-builds/releases/download/vm-20.0.0/graalvm-ce-java11-linux-amd64-20.0.0.tar.gz
RUN mkdir "graalvm-ce-java11-linux-amd64-20.0.0"
RUN tar -xf graalvm-ce-java11-linux-amd64-20.0.0.tar.gz -C /graalvm-ce-java11-linux-amd64-20.0.0
RUN mv /graalvm-ce-java11-linux-amd64-20.0.0 /usr/lib/jvm/
RUN update-alternatives --install /usr/bin/java java /usr/lib/jvm/graalvm-ce-java11-20.0.0/bin/java 2
RUN update-alternatives --config java
RUN export GRAALGU=/usr/lib/jvm/graalvm-ce-java11-20.0.0/bin
RUN export PATH=$JAVA_HOME/BIN:$GRAALGU:$PATH
RUN apt -y install maven
EXPOSE 8080
ENV JAVA_OPTIONS=-Xmx512m
ENV SPRING_PROFILES_ACTIVE=prod,api-docs
ENV JHIPSTER_SLEEP=30
COPY . ./app
WORKDIR "/app"
RUN mvn package -Pprod -DskipTests -q -e
RUN ls
ENTRYPOINT ["java" ,"-jar", "/app/target/ar-xiv-ai-0.0.1-SNAPSHOT.jar"]

