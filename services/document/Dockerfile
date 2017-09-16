FROM tomee:8-jre-7.0.3-plus
MAINTAINER Sébastien Mosser (mosser@i3s.unice.fr)

# Build with : docker build -t petitroll/tcs-doc .
# Publish with: docker push petitroll/tcs-doc

WORKDIR /usr/local/tomee/

COPY ./target/tcs-service-document.war ./webapps/.

EXPOSE 8080
