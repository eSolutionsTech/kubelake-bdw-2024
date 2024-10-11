# Exercise 2: Processing with Apache Spark

## Apache Spark Exercise: Aggregate Data and Save as Delta Table in MinIO

## Introduction to Apache Spark

Apache Spark is a powerful, open-source data processing engine designed for large-scale data processing. 
It offers a unified engine that efficiently processes data at scale across a cluster of machines, making it ideal for tasks such as batch processing, real-time data streaming, machine learning, and graph processing.

### Key Features of Apache Spark:
1. **Unified Data Processing**:
   Spark supports multiple workloads like batch processing, real-time streaming, SQL queries,
machine learning, and graph computation — all within a single, cohesive framework.
This means you can build complex big data pipelines that span multiple types of data processing tasks without switching tools.

2. **In-Memory Computing**:
   One of Spark’s most distinguishing features is its ability to process data in memory.
It minimizes the need to read and write to disk, making it much faster than traditional MapReduce systems, especially for iterative algorithms or interactive data analysis.

3. **Scalability**:
   Spark is designed to scale seamlessly from a single server to thousands of nodes.
It works well with distributed systems like Kubernetes, making it an ideal choice for big data pipelines that require massive scalability.

4. **Ease of Use**:
   Spark provides an easy-to-use API in multiple languages, including **Scala**, 
**Python (PySpark)**, **Java**, and **R**. This flexibility allows developers and data engineers to use the tools they are most comfortable with while building robust pipelines.

5. **Fault Tolerance**:
   Spark is resilient to failures and can recover data and computations using its 
lineage graph. This built-in fault tolerance ensures that big data pipelines can run reliably across distributed environments, where failures are more common.

6. **Optimized for Distributed Data**:
   Spark can process massive datasets by splitting them across clusters and parallelizing the work.
It handles data partitioning automatically, so large datasets can be processed in a highly efficient manner.

### Spark in Big Data Pipelines

With Spark, you can efficiently manage data workflows that:

- Ingest raw data from multiple sources.
- Process or transform the data in real-time or batch.
- Store the final or intermediate results in formats like **Delta Lake** for further analysis or querying.

### Spark's Ecosystem Components:
- **Spark SQL**: Enables querying data using SQL syntax and is highly optimized for large-scale data queries.
- **Spark Streaming**: Handles real-time data streams for continuous processing, making it suitable for building pipelines that require real-time analytics.
- **MLlib**: Spark’s machine learning library that supports large-scale machine learning algorithms, making Spark pipelines suitable for AI/ML tasks.
- **GraphX**: A library for graph-based computations that integrates with Spark for graph processing at scale.

In this exercise, we will learn how to:

- Read data from MinIO using Apache Spark.
- Perform some analysis on the data.
- Save the results in **Delta Table** format back to MinIO.


---

## Prerequisites

1. A **Zeppelin notebook** configured with a Spark interpreter (we will each configure a new notebook with our name/project name).
2. Access to a **MinIO bucket** where the data will be read from and written to (the notebooks already have access to MinIO)
3. Basic knowledge of **Spark** and **Scala** or **Python** (depending on the language you want to use in the notebook).
4. **Delta Lake** dependencies configured in your Spark environment (already done).

---
### Zeppelin teacher pass-through DEMO then DIY for the REST (10 - 20 min)

[Zeppelin](https://zeppelin.dev1.kubelake.com)


## Step 1: Set Up Your Zeppelin Notebook

1. Open Zeppelin and create a new notebook with our name (no credentials required).
2. Set the interpreter to **spark**.
3. We will go to the next step to test access to the MinIO bucket to read our data files.

---

## Step 2: Read Data from MinIO

We can use Spark's `spark.read` method to load files from MinIO. 
Our data is in JSON format so here's how we can read it into a Spark DataFrame:

Scala Example:

```scala

val df = spark.read.json("s3a://datalake/bronze/{your_name_or_project_name}/2024-10-15/*")
z.show(df)
```
If you're using Python, the syntax is similar

## Step 3: Perform Data Analysis

Here are some interesting analyses you can perform:
### 3.1 Stock Price Volatility by ESG Event Type
Use Scala to calculate the stock price volatility over time and analyze how different types of events (Climate, Social, Governance) impact volatility.
Visualize stock price fluctuations before, during, and after major ESG events.

```

val esgEvents = df.filter($"event_type".isNotNull)
val volatility = esgEvents.groupBy("event_type")
.agg(stddev("close").as("price_volatility"))
z.show(volatility)
```

### 3.2  Correlation Between Event Severity and Stock Price Impact:

Analyze the correlation between impact factor (from ESG events) and the stock price changes.
Determine if certain event types (Ex.: Governance events) have a more significant impact on stock prices than others.

```

val correlation = df.stat.corr("impact_factor", "close")
println(s"Correlation between event severity and stock price: $correlation")
```
### 3.3 Event Frequency and Stock Movement Analysis:

Count the frequency of each event type and analyze how frequently different events occur and their average impact on stock price.
You could further analyze whether a higher frequency of social events leads to more sustained stock price changes.

```
val eventFrequency = df.groupBy("event_type")
.agg(count("event_type").as("frequency"), avg("impact_factor").as("avg_impact"))
z.show(eventFrequency)
```

### 3.4 DIY Monthly average stock price

Calculate the monthly average stock price for each stock and visualize the trend over time to identify any patterns or anomalies.
Hint: 
``` 
val stockDataWithMonth = df.withColumn("month_year", date_format(col("date"), "yyyy-MM"))
```


## Step 4: Save Some Aggregated Results as Delta Lake Table

Why Delta?
Saving data in Delta Lake format makes managing and working with large amounts of data: 

- easier
- faster
- more reliable (preventing issues like incomplete updates or data getting mixed up)
- great for real-time analytics or machine learning tasks ( update or delete specific data efficiently )
- benefit of time-travel (you can track changes and even go back to earlier versions of your data if needed)


```

// Save the aggregated results as Delta Table
val stockPriceOverTime = df.select("date", "close","event_type")
// saveAsTable so that we can use this later with Hive -> Trino
stockPriceOverTime.write.format("delta").mode("overwrite").saveAsTable("yourNameStockPriceOverTime")
```

## Step 6: Verify the Saved Data

After saving the data, you can verify the output by using the %sql interpretor which will pick-up our Delta Lake table from
the metadata store
```

%sql
select * from yourNameStockPriceOverTime limit 5
```

## Summary

In this exercise, we learned how to:

- Configure Spark to read data from MinIO. 
- Perform simple analysis using Spark’s DataFrame API.
- Save the results as Delta Lake format back to MinIO.
- Verify the saved data.

Happy coding with Apache Spark!
