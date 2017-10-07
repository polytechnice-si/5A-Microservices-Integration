#!/bin/sh

FEATURES="camel-csv camel-http camel-saxon camel-spring-ws camel-servlet"

echo "Customizing ServiceMix at image build time"

echo "Kindly asking Karaf to start"
./bin/start
echo "Sleeping 10 seconds to let Karaf start"
sleep 10
echo -ne "Karaf is now "
./bin/status

echo "Installing / Upgrading OSGi dependencies"
./bin/client -h localhost -l 2 -- feature:install -u ${FEATURES}
echo "End of OSGi dependencies installation"

echo "Installing HawtIO monitoring"
./bin/client -h localhost -l 2 -- feature:repo-add hawtio 1.5.4
./bin/client -h localhost -l 2 -- feature:install hawtio
echo "End of HawtIO monitoring installation"

echo "Kindly asking Karaf to stop"
./bin/stop
echo "Sleeping 10 seconds to let Karaf stop"
sleep 10

echo "End of ServiceMix customization process at build time"