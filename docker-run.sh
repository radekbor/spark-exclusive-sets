docker run --name my-spark-app-from-cmd -v ~/spark-apps:/opt/spark-apps -v ~/spark-data:/opt/spark-data -e ENABLE_INIT_DAEMON=false -e SPARK_APPLICATION_ARGS="--txt-source /opt/spark-data/input.txt --txt-expected /opt/spark-data/expected.txt --target-dir /opt/spark-data/out" --net spark-sets_default --link spark-master:spark-master -d bde/spark-app