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
We will read the data we played with earlier.
Our data is in JSON format so here's how we can read it into a Spark DataFrame:

Scala Example:

```scala

val df = spark.read.option("multiLine", true).option("mode", "PERMISSIVE").json("s3a://datalake/bronze/{your_name_or_project_name}/2024-10-15/*")
df.show(10)
```
If you're using Python, the syntax is similar

## Step 3: Perform Data Analysis

Here are some interesting analyses you can test:


### 3.1 Event Frequency and Stock Movement Analysis:

Count the frequency of each event type and analyze how frequently different events occur and their average impact on stock price.

Hint:
You can group by event_type then count event_types as frequency and average of the impact_factor as average impact factor

??? success "Show Solution"
      
      ```
      val eventFrequency = 
      df
      .groupBy("event_type")
      .agg(count("event_type").as("frequency"), avg("impact_factor").as("avg_impact"))
      z.show(eventFrequency)
      ```

### 3.2 DIY Monthly average stock price_change

Calculate the monthly average stock price change (as a percentage calculated between close and open).

Hint: 
``` 
val stockDataWithMonth = df.withColumn("month_year", date_format(col("date"), "yyyy-MM"))
.withColumn("price_change", (col("close") - col("open")) / col("open") * 100)

```
??? success "Show Solution"
     
     ```
      val stockDataWithMonth = df.withColumn("month_year", date_format(col("date"), "yyyy-MM"))
      val stockChangeMonthlyAvg = stockDataWithMonth.groupBy("month_year")
      .agg(avg("price_change").as("monthly_avg_price_change"))
      .orderBy("month_year")
      z.show(monthly_avg)
      ```

### 3.3  General Correlation Between Event Severity and Stock Price Daily Change as a Procentage

??? success "What is Correlation?"

    Correlation measures the strength and direction of the linear relationship between two variables.
        Values close to +1: Strong positive correlation (as one variable increases, the other also increases).
        Values close to -1: Strong negative correlation (as one variable increases, the other decreases).
        Values close to 0: No or weak linear correlation (little to no linear relationship between the variables).

Let's analyze the correlation between impact factor (from ESG events) and the stock price change.
```
val correlation = df.withColumn("price_change", (col("close") - col("open")) / col("open") * 100)
.stat.corr("impact_factor", "close")
println(s"General correlation between event severity and stock price: $correlation")
```

### 3.4 DIY Determine if certain event types (Ex.: Governance events) have a more significant impact on stock prices than others.

Steps: 

- Calculate Stock Price Change (= the percentage change in the stock price using close and open prices)
- Filter out rows where there are no ESG events (filter(col("event").isNotNull))
- We're particularly interested in how the impact factor correlates with the price change
- Group the data by event type (Governance, Climate, Social) and calculate the average price change, the average impact
and calculate the correlation between the impact_factor and the price_change

??? success "Show Solution"
      ```         
      val dfWithChange = df.withColumn("price_change", (col("close") - col("open")) / col("open") * 100)

      val dfWithEvents = dfWithChange.filter(col("event").isNotNull)

      // Group by Event Type and Analyze Impact
      val avgImpactByEventType = dfWithEvents.groupBy("event_type")
      .agg(
      avg("price_change").as("avg_price_change"),
      avg("impact_factor").as("avg_impact_factor"),
      corr("impact_factor", "price_change").as("correlation")
      )
      .orderBy(desc("correlation"))  // Order by the strength of the correlation
      
      z.show(avgImpactByEventType)
      //closer to -1 suggests negative Governance Event are causing stock prices to drop. 
      //closer to 0 suggests Social Events are likely not causing that much of a move in the price

      ```
### 3.4 Play with the data if you have any other ideas


## Step 4: Save Some Aggregated Results as Delta Lake Table

Why Delta?

Delta Lake is open source software that extends Parquet data (column-oriented data storage format) files with a file-based transaction log for
**ACID** transactions and scalable metadata handling. 
Delta Lake is fully compatible with Apache Spark APIs, and was developed for tight integration with Structured Streaming,
allowing you to easily use a single copy of data for both batch and streaming operations and providing incremental processing at scale.

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
stockPriceOverTime.write.format("delta").mode("overwrite").saveAsTable("{yourName}GoldStockPrice")
```

## Step 6: Verify the Saved Data

After saving the data, you can verify the output by using the %sql interpreter which will pick-up our Delta Lake table from
the metadata store
```

%sql
select * from {yourName}GoldStockPrice limit 5
```

## Summary

In this exercise, we learned how to:

- Configure Spark to read data from MinIO. 
- Perform simple analysis using Spark’s DataFrame API.
- Save the results as Delta Lake format back to MinIO.
- Verify the saved data.

What did you think is the hardest part about processing?
