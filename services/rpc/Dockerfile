FROM tomee:8-jre-7.0.3-plus
MAINTAINER Sébastien Mosser (mosser@i3s.unice.fr)

# Build with [docker build -t petitroll/tcs-rpc .]

WORKDIR /usr/local/tomee/

COPY ./target/tcs-service-rpc.war ./webapps/.
COPY ./tomee-user.xml ./conf/tomcat-users.xml
COPY ./tomee_manager-context.xml ./webapps/manager/META-INF/context.xml

EXPOSE 8080
