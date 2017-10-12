FROM petitroll/esb

# Build: docker build -t petitroll/tcs-bus .
# Run:   docker run -v ./_in:/servicemix/camel/input -v ./_out:/servicemix/camel/output petitroll/tcs-bus
# Overriding: docker run -it --entrypoint "sh" petitroll/tcs-bus

RUN mkdir -p camel/input camel/output

# Deploying technical flows
COPY flows/target/flows-1.0.jar ./deploy/.

