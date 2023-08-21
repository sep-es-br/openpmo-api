FROM adoptopenjdk/openjdk8:jre8u-alpine-nightly

VOLUME /tmp
COPY ./build/libs/app.jar app.jar

ENV SEARCH_CUT_OFF_SCORE 0.05
ENV NEO4J_URL bolt://localhost:7687
ENV NEO4J_USERNAME neo4j
ENV NEO4J_PASSWORD 12345678
ENV NEO4J_MAX_CONNECTION_POOL_SIZE 350

RUN apk add --no-cache fontconfig ttf-dejavu

ENTRYPOINT [ "sh", "-c", "java -Djava.security.egd=file:/dev/./urandom -Dspring.profiles.active=release -Duser.region=BR -Duser.language=pt ${JAVA_OPTS} -jar /app.jar" ]
