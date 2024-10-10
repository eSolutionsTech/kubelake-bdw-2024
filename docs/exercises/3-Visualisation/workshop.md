# Exercise 3: Visualizing Data with Apache Superset

# Visualize Processed Data Stored in MinIO using Zeppelin and Apache Superset

## Introduction

In this plan, we will:

1. Visualize the processed data in **Zeppelin** for ad-hoc visualizations.
2. Later, create more formal dashboards using **Apache Superset** to visualize key metrics.

### Prerequisites

- **Zeppelin notebook** is configured with Spark to access the processed data stored in **MinIO** ([Zeppelin](https://zeppelin.dev1.kubelake.com))
- **Apache Superset** is installed and configured with access to MinIO Delta tables([Superset](https://superset.dev1.kubelake.com))


---

## Step 1: Visualizations in Zeppelin

Zeppelin has built-in charting capabilities for basic visualizations.
After running the aggregation or a query, switch the result to visualizations using Zeppelin's built-in tools (z.show)
You can select from pie charts, bar charts, scatter plots, etc.

After performing the aggregation:
Click on Bar Chart, Line Chart, or Pie Chart options on the Zeppelin result window.

Let's visualise stock Price over Time (as a bar chart)

```scala

val stockPriceOverTime = df.select("date", "close")
z.show(stockPriceOverTime)
```

## Step 2: Visualize Data Using Apache Superset

Now, let's create visualizations and dashboards in Superset.
#### 2.1 Create Charts to Visualize Key Metrics

Let's log together into [Superset](https://superset.dev1.kubelake.com)

We'll use the SQL Lab to run some queries on our data 

- database **trino-lakehouse**
- schema **default**
- table **your_delta_table_previously_created**

DIY the Rest


Create the dataset on our data

- on the Datasets -> new Dataset
- select our database **trino-lakehouse** and schema **default**
- select  **your_delta_table_previously_created**
- create dataset and chart

Create the chart on our dataset

- Choose a Bar Chart.
- Set **date**  as the x-axis and **Sum(close)** as the metric.
- Add event_type as Dimension
- Adjust the chart settings and labels.


We should have a chart now on which we can see how the price evolved, and also we can filter by the event type.
![chart](../img/stock_price_chart.png)

## Summary

In this step, we learned how to:

- Use Zeppelin for quick, ad-hoc visualizations by loading data from our data lake and using its built-in charting capabilities.
- Use Apache Superset to create more formal dashboards that visualize key metrics.

Happy visualizing!
