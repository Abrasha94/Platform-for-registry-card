FROM sebp/elk

RUN rm /etc/logstash/conf.d/30-output.conf
COPY 30-output.conf /etc/logstash/conf.d/30-output.conf

RUN rm /etc/logstash/conf.d/02-beats-input.conf
COPY 02-beats-input.conf /etc/logstash/conf.d/02-beats-input.conf