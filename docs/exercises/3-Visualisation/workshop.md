# Exercise 3: Visualizing Data with Apache Superset

[//]: # (### Objective:)

[//]: # (Visualize the processed data stored in Minio using Apache Superset.)

[//]: # ()
[//]: # ()
[//]: # (### Steps:)

[//]: # (1. **Set Up Apache Superset:**)

[//]: # (    - Connect Superset to Minio Delta tables &#40;already done&#41;.)

[//]: # ()
[//]: # (2. **Create Dashboards:**)

[//]: # (    - Build charts to visualize key financial metrics.)

[//]: # (    - Set up a dashboard to display real-time data processing results.)


# Visualize Processed Data Stored in MinIO using Zeppelin and Apache Superset

## Introduction

In this plan, we will:

1. Visualize the processed data in **Zeppelin** for ad-hoc visualizations.
2. Later, create more formal dashboards using **Apache Superset** to visualize key metrics.

### Prerequisites

- **Zeppelin notebook** is configured with Spark to access the processed data stored in **MinIO**.
- **Apache Superset** is installed and configured with access to MinIO Delta tables.

---

## Step 1: Visualize Data in Zeppelin

### Objective

Use **Zeppelin** for quick, ad-hoc visualizations of the processed data stored in MinIO, before transitioning to Superset for more complex dashboarding.

### Steps:

### 1.1 Load Data in Zeppelin

Before visualizing the data, make sure you can access the Delta table stored in MinIO from Zeppelin.

#### Python Example:

```python

# Load the Delta Table data
delta_df = spark.read.format("delta").load(output_path)

# Show the DataFrame
delta_df.show()
```         
### 1.2 Perform Basic Aggregations

You can use Zeppelin to visualize some key metrics.
Python Example:

```python

# Perform an aggregation (e.g., calculate the sum of a numeric column)
aggregated_df = delta_df.groupBy("group_column").agg({"numeric_column": "sum"})

# Show the aggregated data
z.show(aggregated_df)
```
### 1.3 Visualize Data Using Zeppelin's Built-in Charting

Zeppelin has built-in charting capabilities for basic visualizations.

    After running the aggregation or a query, switch the result to visualizations using Zeppelin's built-in tools (z.show)
    You can select from pie charts, bar charts, scatter plots, etc.

Example for Chart Visualization:

After performing the aggregation:

    Click on Bar Chart, Line Chart, or Pie Chart options on the Zeppelin result window.
    Select the columns you want to visualize (e.g., group by and sum column).
    Adjust chart settings as needed.

## Step 2: Visualize Data Using Apache Superset
Objective

Once you’ve explored the data in Zeppelin, move to Apache Superset for more complex visualizations, dashboards, and real-time data monitoring.
### Step 2.1: Set Up Apache Superset

Make sure Apache Superset is properly connected to MinIO Delta tables (this is already done)


### Step 2.2: Create Dashboards in Superset

Now, let's create visualizations and dashboards in Superset for monitoring the key financial metrics and real-time processing results.
#### 2.2.1 Create Charts to Visualize Key Metrics

 Log into Superset:
     Access the Superset UI using the designated URL.

 Create a New Chart:
     From the Superset homepage, click on Charts and then + Chart.
     Select the MinIO Delta table as your data source.
     Choose the appropriate chart type (e.g., bar chart, pie chart, line chart).
     Define the necessary metrics and dimensions (e.g., sum of a numeric column grouped by category).

 Customize the Chart:
     Add necessary filters, group by dimensions, and select metrics.
     Set up visual properties like color, labels, and axis settings.
     Preview the chart to ensure the data is displayed correctly.

Example of Financial Metrics:

For example, to visualize a financial metric like total revenue by region:

 - Choose a Bar Chart.
 - Select the Delta table.
 - Set Region as the x-axis and Sum(Revenue) as the metric.
 - Adjust the chart settings and labels.


## Summary

In this step, we learned how to:

- Use Zeppelin for quick, ad-hoc visualizations by loading data from MinIO and using its built-in charting capabilities.
- Use Apache Superset to create more formal dashboards that visualize key financial metrics and display real-time data processing results.

Happy visualizing!

<img src="/img/simbol_esolutions.png" alt="Logo" style="float: right; width: 150px;"/>
