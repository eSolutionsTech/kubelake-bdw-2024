# Streaming module

## Kafka
Apache Kafka is an open-source distributed streaming system used for stream processing,
real-time data pipelines, and data integration at scale. See more at [https://kafka.apache.org](https://kafka.apache.org)

## Spark Structured Streaming
Apache Spark Structured Streaming is a real-time data processing framework that provides 
a declarative API and offers end-to-end fault-tolerance guarantees.
See more at [Structured Streaming Programming Guide](https://spark.apache.org/docs/latest/structured-streaming-programming-guide.html).

### Usage 
A SparkApplication can be created from a YAML file storing the `SparkApplication` specification using the `kubectl apply -f <YAML file path>` command.

Example:

- Kafka topic
- 
```
apiVersion: kafka.strimzi.io/v1beta2
kind: KafkaTopic
metadata:
  name: nyc-avro-topic
  labels:
    strimzi.io/cluster: cluster
spec:
  partitions: 10
  replicas: 3
```

- Python streaming app (main-streaming-spark-consumer.py)
```
import os
from pyspark import SparkConf
from pyspark import SparkContext
from pyspark.sql import SparkSession
from pyspark.sql.avro.functions import from_avro
from pyspark.sql.types import StructType, StructField, LongType, DoubleType, StringType
import json

conf = (
    SparkConf()
    .set("spark.sql.streaming.checkpointFileManagerClass", "io.minio.spark.checkpoint.S3BasedCheckpointFileManager")
)

spark = SparkSession.builder.config(conf=conf).getOrCreate()

def load_config(spark_context: SparkContext):
    spark_context._jsc.hadoopConfiguration().set("fs.s3a.access.key", "<Your-MinIO-AccessKey>")
    spark_context._jsc.hadoopConfiguration().set("fs.s3a.secret.key", "<Your-MinIO-SecretKey>")


load_config(spark.sparkContext)

schema = StructType([
    StructField('VendorID', LongType(), True),
    StructField('tpep_pickup_datetime', StringType(), True),
    StructField('tpep_dropoff_datetime', StringType(), True),
    StructField('passenger_count', DoubleType(), True),
    StructField('trip_distance', DoubleType(), True),
    StructField('RatecodeID', DoubleType(), True),
    StructField('store_and_fwd_flag', StringType(), True),
    StructField('PULocationID', LongType(), True),
    StructField('DOLocationID', LongType(), True),
    StructField('payment_type', LongType(), True),
    StructField('fare_amount', DoubleType(), True),
    StructField('extra', DoubleType(), True),
    StructField('mta_tax', DoubleType(), True),
    StructField('tip_amount', DoubleType(), True),
    StructField('tolls_amount', DoubleType(), True),
    StructField('improvement_surcharge', DoubleType(), True),
    StructField('total_amount', DoubleType(), True)])

value_schema_dict = {
    "type": "record",
    "name": "nyc_avro_test",
    "fields": [
        {
            "name": "VendorID",
            "type": "long"
        },
        {
            "name": "tpep_pickup_datetime",
            "type": "string"
        },
        {
            "name": "tpep_dropoff_datetime",
            "type": "string"
        },
        {
            "name": "passenger_count",
            "type": "double"
        },
        {
            "name": "trip_distance",
            "type": "double"
        },
        {
            "name": "RatecodeID",
            "type": "double"
        },
        {
            "name": "store_and_fwd_flag",
            "type": "string"
        },
        {
            "name": "PULocationID",
            "type": "long"
        },
        {
            "name": "DOLocationID",
            "type": "long"
        },
        {
            "name": "payment_type",
            "type": "long"
        },
        {
            "name": "fare_amount",
            "type": "double"
        },
        {
            "name": "extra",
            "type": "double"
        },
        {
            "name": "mta_tax",
            "type": "double"
        },
        {
            "name": "tip_amount",
            "type": "double"
        },
        {
            "name": "tolls_amount",
            "type": "double"
        },
        {
            "name": "improvement_surcharge",
            "type": "double"
        },
        {
            "name": "total_amount",
            "type": "double"
        },
    ]
}

stream_df = spark.readStream.format("kafka") \
    .option("kafka.bootstrap.servers", 
            os.getenv("KAFKA_BOOTSTRAP_SERVER","cluster-kafka-bootstrap..svc.cluster.local:9092")) \
    .option("subscribe", "nyc-avro-topic") \
    .option("startingOffsets", "earliest") \
    .option("failOnDataLoss", "false") \
    .option("minPartitions", "10") \
    .option("mode", "PERMISSIVE") \
    .option("truncate", False) \
    .option("newRows", 100000) \
    .load()

taxi_df = stream_df.select(from_avro("value", json.dumps(value_schema_dict)).alias("data")).select("data.*")

taxi_df.writeStream \
    .format("parquet") \
    .outputMode("append") \
    .trigger(processingTime='1 second') \
    .option("path", "s3a://spark-stream/") \
    .option("checkpointLocation", "s3a://checkpoint") \
    .start() \
    .awaitTermination()
```
- Spark kubernetes application 
```
apiVersion: "sparkoperator.k8s.io/v1beta2"
kind: SparkApplication
metadata:
  name: spark-stream-optimized
spec:
  type: Python
  pythonVersion: "3"
  mode: cluster
  image: "sparkjob-demo:0.0.1"
  imagePullPolicy: Always
  mainApplicationFile: local:///app/main-streaming-spark-consumer.py
  sparkVersion: "3.5.1"
  restartPolicy:
    type: OnFailure
    onFailureRetries: 1
    onFailureRetryInterval: 10
    onSubmissionFailureRetries: 5
    onSubmissionFailureRetryInterval: 20
  driver:
    cores: 3
    memory: "2048m"
    labels:
      version: 3.5.1
    serviceAccount: my-release-spark
    env:
      - name: AWS_ACCESS_KEY_ID
        value: <Your-MinIO-AccessKey>
      - name: AWS_SECRET_ACCESS_KEY
        value: <Your-MinIO-SecretKey>
  executor:
    cores: 1
    instances: 10
    memory: "1024m"
    labels:
      version: 3.5.1
    env:
      - name: AWS_ACCESS_KEY_ID
        value: <Your-MinIO-AccessKey>
      - name: AWS_SECRET_ACCESS_KEY
        value: <Your-MinIO-SecretKey>
```

