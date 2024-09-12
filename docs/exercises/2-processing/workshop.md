# Exercise 2: Processing with Apache Spark

### Objective:
Use Spark Structured Streaming to process data from Kafka and save it into Minio delta table.

### Steps:
1. **Create a Spark Structured Streaming notebook in Zeppelin:**
    - Configure Spark to read data from Kafka.
    - Process the data and save it as Delta Table format in Minio(using append mode).

3. **Aggregate Data with Spark:**
    - Create a second Spark notebook in Zeppelin to read files from Minio.
    - Perform an aggregation (e.g., sum, count) on the data.
    - Save the aggregated results as Delta Table format in Minio.
