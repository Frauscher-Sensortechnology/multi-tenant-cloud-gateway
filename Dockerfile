FROM gradle:7.4-jdk17-alpine AS GRADLE_TOOL_CHAIN

WORKDIR /home/gradle/src

COPY --chown=gradle:gradle . /home/gradle/src
RUN gradle --no-daemon --refresh-dependencies assemble

FROM gcr.io/distroless/java17-debian11:nonroot

WORKDIR /home/platform

COPY --from=GRADLE_TOOL_CHAIN /home/gradle/src/build/libs/auth-gateway-0.0.1.jar /home/platform/application.jar

EXPOSE 8000 8080

CMD ["/home/platform/application.jar", "-Djava.security.egd=file:/dev/./urandom"]
