# Exercise 2: Processing with Apache Spark

[//]: # (### Objective:)

[//]: # (Use Spark Structured Streaming to process data from Kafka and save it into Minio delta table.)

[//]: # (TODO a more in depth spark + code examples + full code at the end)

[//]: # ()
[//]: # (### Steps:)

[//]: # (1. **Create a Spark Structured Streaming notebook in Zeppelin:**)

[//]: # (    - Configure Spark to read data from Kafka.)

[//]: # (    - Process the data and save it as Delta Table format in Minio&#40;using append mode&#41;.)

[//]: # ()
[//]: # (3. **Aggregate Data with Spark:**)

[//]: # (    - Create a second Spark notebook in Zeppelin to read files from Minio.)

[//]: # (    - Perform an aggregation &#40;e.g., sum, count&#41; on the data.)

[//]: # (    - Save the aggregated results as Delta Table format in Minio.)

[//]: # ()

## Apache Spark Exercise: Aggregate Data and Save as Delta Table in MinIO

## Introduction to Apache Spark

Apache Spark is a powerful, open-source data processing engine designed for large-scale data processing.
It provides an easy-to-use API for distributed data processing, making it suitable for tasks like batch processing, real-time streaming, machine learning, and graph processing.

In this exercise, you will learn how to:

- Read data from MinIO using Apache Spark.
- Perform an aggregation (e.g., sum, count) on the data.
- Save the aggregated results in **Delta Table** format back to MinIO.


---

## Prerequisites

1. A **Zeppelin notebook** configured with a Spark interpreter (we will each configure a new notebook with our name/project name).
2. Access to a **MinIO bucket** where the data will be read from and written to (the notebooks already have access to MinIO)
3. Basic knowledge of **Spark** and **Python** or **Scala** (depending on the language you want to use in the notebook).
4. **Delta Lake** dependencies configured in your Spark environment.

---
### Zeppelin teacher pass-through DEMO
[Zeppelin](https://zeppelin.{domain}.kubelake.com)


## Step 1: Set Up Your Zeppelin Notebook

1. Open Zeppelin and create a new notebook.
2. Set the interpreter to **spark**.
3. We will go to the next step to test access to the MinIO bucket to read our data files.

---

## Step 2: Read Data from MinIO

You can use Spark's `spark.read` method to load files from MinIO. Assuming your data is in CSV format, here's how you can read it into a Spark DataFrame:

Scala Example:

```scala

    val path = s"s3a://datalake/silver/user1/raw/"
    
    val files = spark.sparkContext
      .wholeTextFiles(path)
      .map { case (filePath, content) => filePath }
      .collect()
    
    files.foreach(println)
    
    dataDF.show()
```
If you're using Python, the syntax is similar

## Step 3: Perform Data Aggregation

Now that we have the data in a DataFrame, let’s perform some basic aggregation. For this example, let's assume the dataset contains a numeric column that we want to sum or count.

### Python Example:

```python
# Perform an aggregation (e.g., sum a numeric column or count rows)
aggregated_df = data_df.groupBy("group_column").agg({"numeric_column": "sum"})

# Show the aggregated result
aggregated_df.show()
```
Scala Example:

```scala

// Perform aggregation (e.g., sum a numeric column or count rows)
val aggregatedDF = dataDF.groupBy("group_column").agg(Map("numeric_column" -> "sum"))

// Show the aggregated result
aggregatedDF.show()
```

## Step 4: Save Aggregated Results as Delta Table in MinIO

To save the aggregated data in Delta format back to MinIO, you need to configure Spark to write in Delta Lake format.

Make sure Delta Lake dependencies are available in your Spark environment. Here's how you can save the aggregated data:
Python Example:

``` python

# Save the aggregated results as Delta Table format in MinIO
output_path = f"s3a://{bucket_name}/gold/user1/aggregated_results/"

aggregated_df.write.format("delta").mode("overwrite").save(output_path)
```
Scala Example:

``` scala

// Save the aggregated results as Delta Table format in MinIO
val outputPath = s"s3a://$bucketName/gold/user1/aggregated_results/"

aggregatedDF.write.format("delta").mode("overwrite").save(outputPath)
```

This will store your aggregated results as a Delta Table in the specified MinIO path.
## Step 5: Verify the Saved Data in MinIO

After saving the data, you can verify the output by using the spark.read function to load and inspect the Delta Table.
Python Example:

``` python

# Verify the saved Delta Table
delta_df = spark.read.format("delta").load(output_path)
delta_df.show()
```
Scala Example:
``` scala

// Verify the saved Delta Table
val deltaDF = spark.read.format("delta").load(outputPath)
```


## Summary

In this exercise, you learned how to:

- Configure Spark to read data from MinIO. 
- Perform a simple aggregation using Spark’s DataFrame API.
- Save the aggregated results as Delta format back to MinIO.
- Verify the saved data in Delta format.

Happy coding with Apache Spark!


<img src="/img/simbol_esolutions.png" alt="Logo" style="float: right; width: 150px;"/>



2. Analyzing the Data in Scala (Apache Zeppelin)

Once you’ve ingested the data into a database or a Kafka topic, you can use Scala in Zeppelin to do some insightful data analysis. Here are some interesting analyses you can perform:
Scala Zeppelin Use Cases:

    Stock Price Volatility by ESG Event Type:
        Use Scala to calculate the stock price volatility over time and analyze how different types of events (Climate, Social, Governance) impact volatility.
        Visualize stock price fluctuations before, during, and after major ESG events.

    scala

val esgEvents = df.filter($"event_type".isNotNull)
val volatility = esgEvents.groupBy("event_type")
.agg(stddev("close").as("price_volatility"))
display(volatility)

Correlation Between Event Severity and Stock Price Impact:

    Analyze the correlation between impact factor (from ESG events) and the stock price changes.
    Determine if certain event types (e.g., Governance events) have a more significant impact on stock prices than others.

scala

val correlation = df.stat.corr("impact_factor", "close")
println(s"Correlation between event severity and stock price: $correlation")

Event Frequency and Stock Movement Analysis:

    Count the frequency of each event type and analyze how frequently different ESG events occur and their average impact on stock price.
    You could further analyze whether a higher frequency of social events leads to more sustained stock price changes.

scala

val eventFrequency = df.groupBy("event_type")
.agg(count("event_type").as("frequency"), avg("impact_factor").as("avg_impact"))
display(eventFrequency)

Sentiment Analysis on ESG Events (if added in NiFi):

    If you used NiFi to add sentiment scores to the ESG event descriptions, you can perform sentiment analysis:
        Correlation between sentiment and stock price changes.
        Events with the highest negative or positive sentiment and their impacts on stock volatility.

scala

val sentimentCorrelation = df.stat.corr("sentiment_score", "close")
display(sentimentCorrelation)

ESG Event-Driven Price Prediction:

    Use machine learning libraries like Spark MLlib to create predictive models based on the historical ESG events and their impact on stock prices.
    This could predict future stock price movements when a new event occurs (e.g., if a new CEO resigns or a climate disaster happens).

You could train a model using LinearRegression or RandomForestRegressor from Spark MLlib.

scala

    import org.apache.spark.ml.regression.LinearRegression

    // Prepare data
    val features = df.select("impact_factor", "volume", "open", "close")
    val lr = new LinearRegression()
      .setLabelCol("close")
      .setFeaturesCol("features")

    val model = lr.fit(trainingData)
    val predictions = model.transform(testData)
    display(predictions)

3. Visualizations in Zeppelin

Visualizing these analyses can provide quick insights:

    Stock Price over Time with event annotations (use scatter plots to highlight key ESG events).
    Stock Price Volatility by Event Type: Show how volatility spikes during governance, climate, or social events.
    Volume of Trades During Major ESG Events: Highlight changes in trading volume during key events.

scala

val stockPriceOverTime = df.select("date", "close")
display(stockPriceOverTime)

scala

val eventVolatility = df.groupBy("event_type")
.agg(stddev("close").as("volatility"))
display(eventVolatility)
