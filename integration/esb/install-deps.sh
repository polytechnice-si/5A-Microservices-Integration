#!/bin/sh

FEATURES="camel-csv camel-http camel-saxon camel-spring-ws camel-servlet camel-jackson "
DELTA=10

echo "Customizing ServiceMix at image build time"

echo "Kindly asking Karaf to start"
./bin/start
echo "Sleeping ${DELTA} seconds to let Karaf start"
sleep ${DELTA}

echo -ne "Karaf is now "
./bin/status

echo "Installing / Upgrading OSGi dependencies"
echo "  ==>> " ${FEATURES}
./bin/client -h localhost -- feature:install -u ${FEATURES}
echo "End of OSGi dependencies installation"

echo "Installing HawtIO monitoring"
./bin/client -h localhost -- feature:repo-add hawtio 1.5.4
./bin/client -h localhost -- feature:install hawtio
echo "End of HawtIO monitoring installation"

echo "Kindly asking Karaf to stop"
./bin/stop
echo "Sleeping ${DELTA} seconds to let Karaf stop"
sleep ${DELTA}

echo "End of ServiceMix customization process at build time"