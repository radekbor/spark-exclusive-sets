# spark-exclusive-sets

Build jar using:

```
sbt clean assembly
```

Then build image:
```
docker build --rm=true -t bde/spark-app .
```

In docker compose file there are different combinations values of SPARK_APPLICATION_ARGS, uncomment at least one of it and run

```
docker-compose up
```
Another option is to remove `run` service and again `docker-compose up` but now in order to run spark job:

```
sh ./docker-run.sh
```


This app supports currently those arguments:

- --bits = value should be in range 1-32, this value affect how many keys will be in app 
- --print = collect and print data, default false
- --random-input = size of random input, 0 default, then no random input being used
- --txt-source = input file
- --txt-expected = result of computation will be compared with content of that file
- --target-dir = directory where csv file will be written
- --jdbc-source-url = input database 
- --jdbc-source-table = input table
- --jdbc-target-url = output database

TODO

- [ ] write spark job test
- [ ] reorganize argument parsing
- [ ] add enum for parsed arguments