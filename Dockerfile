FROM adoptopenjdk/openjdk8:jre8u-alpine-nightly

VOLUME /tmp
COPY ./build/libs/app.jar app.jar

ENV SEARCH_CUT_OFF_SCORE 0.05

RUN apk add --no-cache fontconfig ttf-dejavu

ENTRYPOINT ["java","-Djava.security.egd=file:/dev/./urandom", "-Dspring.profiles.active=dev","-jar","/app.jar"]
