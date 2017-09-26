FROM tomee:8-jre-7.0.3-plus
MAINTAINER Sébastien Mosser (mosser@i3s.unice.fr)

# Build with : docker build -t petitroll/tcs-rest .
# Publish with: docker push petitroll/tcs-rest

WORKDIR /usr/local/tomee/

COPY ./target/tcs-service-rest.war ./webapps/.

HEALTHCHECK --interval=5s CMD curl --fail http://localhost:8080/ || exit 1


EXPOSE 8080
