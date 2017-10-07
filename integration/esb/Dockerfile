FROM openjdk:8-jdk-alpine

# Build:                docker build -t petitroll/esb . # Consider --no-cache if necessary
# Interactive run:      docker run -it --entrypoint "sh" petitroll/esb

# Installing service mix from the Apache French Mirror
RUN wget http://apache.mirrors.ovh.net/ftp.apache.org/dist/servicemix/servicemix-7/7.0.1/apache-servicemix-7.0.1.zip \
        && unzip -q apache-servicemix-7.0.1.zip \
        && ln -s apache-servicemix-7.0.1 servicemix

# Moving to the servicemix installation directory
WORKDIR /servicemix

# Installing OSGi dependencies at build time
COPY ./install-deps.sh .
RUN chmod u+x ./install-deps.sh && ./install-deps.sh

# Port exposition
EXPOSE 8181 8101 1099 61616 44444

# Starting ServiceMix
ENTRYPOINT ["./bin/servicemix"]