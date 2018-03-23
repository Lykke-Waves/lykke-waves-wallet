FROM anapsix/alpine-java:jdk8 as builder

RUN apk add --no-cache --update ca-certificates wget git && \
    update-ca-certificates

RUN wget -O - https://github.com/sbt/sbt/releases/download/v1.1.0/sbt-1.1.0.tgz \
    | gunzip \
    | tar -x -C /usr/local

ENV PATH="/usr/local/sbt/bin:${PATH}"

RUN git clone https://github.com/Lykke-Waves/lykke-waves-common.git \
    && cd lykke-waves-common \
    && git checkout 0.0.1 \
    && sbt clean publishLocal

RUN git clone https://github.com/Lykke-Waves/lykke-waves-wallet.git \
    && cd lykke-waves-wallet \
    && git checkout 0.0.1 \
    && sbt clean assembly

RUN mv `find /lykke-waves-wallet/target/scala-2.12 -name *.jar` /lykke-waves-wallet.jar && chmod -R 744 /lykke-waves-wallet.jar

FROM openjdk:9-jre-slim
MAINTAINER Sergey Tolmachev <tolsi.ru@gmail.com>
ENV ENV_INFO=dev
WORKDIR /app
COPY --from=builder /lykke-waves-wallet.jar /app

EXPOSE 8081

RUN useradd -s /bin/false lykke-waves-wallet
USER lykke-waves-wallet

COPY config-for-docker.json /app/config.json
ENV ENV_INFO=dev
ENV SettingsUrl=file:///app/config.json

CMD ["/usr/bin/java", "-jar", "/app/lykke-waves-wallet.jar"]