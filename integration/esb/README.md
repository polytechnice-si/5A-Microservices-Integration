# Custom ServiceMix Image

This dockerfile builds a customized ServiceMix installation as a turnkey artefact.


We first retrieve ServiceMix 7.0.1 on an official Apache Mirror, and deploy it in the `/servicemix` directory of an Alpine linux distribution with OpenJDK8. We then execute the `install-deps.sh` script, which starts the bus, dynamically install some features required by the integration flows and then stops the bus. When started in a container, the image executes the `./bin/servicemix` command, starting the ESB.
