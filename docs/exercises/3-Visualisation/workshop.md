# Exercise 3: Visualizing Data with Apache Superset

# Visualize Processed Data Stored in MinIO using Zeppelin and Apache Superset

## Introduction

In this plan, we will:

1. Visualize the processed data in **Zeppelin** for ad-hoc visualizations.
2. Later, create more formal dashboards using **Apache Superset** to visualize key metrics.

### Prerequisites

- **Zeppelin notebook** is configured with Spark to access the processed data stored in **MinIO**.
- **Apache Superset** is installed and configured with access to MinIO Delta tables.

---

## Step 1: Visualizations in Zeppelin

Zeppelin has built-in charting capabilities for basic visualizations.
After running the aggregation or a query, switch the result to visualizations using Zeppelin's built-in tools (z.show)
You can select from pie charts, bar charts, scatter plots, etc.

Example for Chart Visualization:

After performing the aggregation:

Click on Bar Chart, Line Chart, or Pie Chart options on the Zeppelin result window.
Select the columns you want to visualize (e.g., group by and sum column).
Adjust chart settings as needed.


Let's visualise stock Price over Time (as a bar chart)

```scala

val stockPriceOverTime = df.select("date", "close")
z.show(stockPriceOverTime)
```

## Step 2: Visualize Data Using Apache Superset

### Step 2.1: Create Dashboards in Superset

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
- Use Apache Superset to create more formal dashboards that visualize key metrics.

Happy visualizing!

<img src="/img/simbol_esolutions.png" alt="Logo" style="float: right; width: 150px;"/>
