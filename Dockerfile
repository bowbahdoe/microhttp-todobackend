#syntax=docker/dockerfile:1.5

FROM maven:3.9.4-amazoncorretto-21-al2023 AS build

COPY pom.xml pom.xml
COPY src src

RUN mvn clean compile jlink:jlink


FROM ubuntu:22.04

ENV PORT=80
COPY --from=build target/maven-jlink/classifiers/image /jre

ENTRYPOINT ["./jre/bin/server"]