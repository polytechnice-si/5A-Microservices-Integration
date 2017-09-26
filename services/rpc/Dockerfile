FROM tomee:8-jre-7.0.3-plus
MAINTAINER Sébastien Mosser (mosser@i3s.unice.fr)

# Build with : docker build -t petitroll/tcs-rpc .
# Publish with: docker push petitroll/tcs-rpc

WORKDIR /usr/local/tomee/

COPY ./target/tcs-service-rpc.war ./webapps/.

HEALTHCHECK --interval=5s CMD curl --fail http://localhost:8080/ || exit 1

EXPOSE 8080
