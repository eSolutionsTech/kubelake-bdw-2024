# Exercise 2: Processing with Apache Spark

## Apache Spark Exercise: Aggregate Data and Save as Delta Table in MinIO

## Introduction to Apache Spark

Apache Spark is a powerful, open-source data processing engine designed for large-scale data processing.
It provides an easy-to-use API for distributed data processing, making it suitable for tasks like batch processing,
real-time streaming, machine learning, and graph processing.

In this exercise, you will learn how to:

- Read data from MinIO using Apache Spark.
- Perform an aggregation (e.g., sum, count) on the data.
- Save the aggregated results in **Delta Table** format back to MinIO.


---

## Prerequisites

1. A **Zeppelin notebook** configured with a Spark interpreter (we will each configure a new notebook with our name/project name).
2. Access to a **MinIO bucket** where the data will be read from and written to (the notebooks already have access to MinIO)
3. Basic knowledge of **Spark** and **Scala** or **Python** (depending on the language you want to use in the notebook).
4. **Delta Lake** dependencies configured in your Spark environment (already done).

---
### Zeppelin teacher pass-through DEMO then DIY for the REST

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

```scala

val esgEvents = df.filter($"event_type".isNotNull)
val volatility = esgEvents.groupBy("event_type")
.agg(stddev("close").as("price_volatility"))
z.show(volatility)
```

### 3.2  Correlation Between Event Severity and Stock Price Impact:

Analyze the correlation between impact factor (from ESG events) and the stock price changes.
Determine if certain event types (Ex.: Governance events) have a more significant impact on stock prices than others.

``` scala

val correlation = df.stat.corr("impact_factor", "close")
println(s"Correlation between event severity and stock price: $correlation")
```
### 3.2 Event Frequency and Stock Movement Analysis:

Count the frequency of each event type and analyze how frequently different events occur and their average impact on stock price.
You could further analyze whether a higher frequency of social events leads to more sustained stock price changes.

``` scala
val eventFrequency = df.groupBy("event_type")
.agg(count("event_type").as("frequency"), avg("impact_factor").as("avg_impact"))
z.show(eventFrequency)
```

## Step 5: Save Aggregated Results as Delta Table in MinIO

To save the data in Delta format back to MinIO, you need to configure Spark to write in Delta Lake format.
Why Delta?

Saving data in Delta format makes managing and working with large amounts of data: 

- easier
- faster
- more reliable (preventing issues like incomplete updates or data getting mixed up)
- great for real-time analytics or machine learning tasks ( update or delete specific data efficiently )
- benefit of time-travel (you can track changes and even go back to earlier versions of your data if needed)


``` scala

// Save the aggregated results as Delta Table
val stockPriceOverTime = df.select("date", "close")
// saveAsTable so that we can use this later with Hive -> Trino
stockPriceOverTime.write.format("delta").mode("overwrite").saveAsTable("yourNameStockPriceOverTime")
```

This will store the results as a Delta Table in the specified MinIO path.

## Step 6: Verify the Saved Data in MinIO

After saving the data, you can verify the output by using the %sql interpretor which will pick-up our Delta Table from
the metadata store
``` scala

%sql
select * from yourNameStockPriceOverTime limit 5
```


## Summary

In this exercise, we learned how to:

- Configure Spark to read data from MinIO. 
- Perform simple analysis using Spark’s DataFrame API.
- Save the results as Delta format back to MinIO.
- Verify the saved data in Delta format.

Happy coding with Apache Spark!
