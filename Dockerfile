FROM bde2020/spark-submit:3.0.2-hadoop3.2

COPY template.sh /

ENV SPARK_APPLICATION_MAIN_CLASS dev.borowiecki.sets.ReadAndValidateApp

COPY target/scala-2.12 /
COPY input.txt /

CMD ["/template.sh"]