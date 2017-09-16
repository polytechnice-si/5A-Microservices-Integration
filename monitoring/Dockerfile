FROM google/cadvisor
MAINTAINER Sébastien Mosser (mosser@i3s.unice.fr)
# fixing google official image (https://github.com/google/cadvisor/issues/1641)

# docker build -t petitroll/cadvisor .
# docker push petitroll/cadvisor

RUN apk add --update findutils && rm -rf /var/cache/apk/*