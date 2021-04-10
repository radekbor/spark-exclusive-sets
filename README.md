# spark-exclusive-sets

Build jar using:

```
sbt clean assembly
```

Then build image:
```
docker build --rm=true -t bde/spark-app .
```

This app supports currently those arguments:

- --bits = value should be in range 1-32, this value affect how many keys will be in app 
- --txt-source = input file
- --txt-expected = result of computation will be compared with content of that file
- --target-dir = directory where csv file will be written (optional)