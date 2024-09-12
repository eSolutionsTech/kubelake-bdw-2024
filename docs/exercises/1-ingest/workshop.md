# Exercise 1: Data Ingestion with Apache NiFi

### Objective:
Use Apache NiFi to ingest financial data from Yahoo Finance, transform it, and push it to Kafka.

### Steps:
1. **Configure Apache NiFi:**
    - Set up a flow to ingest data from an API (Ex.: Yahoo Finance).
    - Transform the data:
        - Rename a JSON field.
        - Filter data based on a specific field (e.g., `nyse`).
    - Push each event to Kafka.
    - Store in Minio every 10 events into a single file.

2. **Test the Data Flow:**
    - Ensure that the flow works by connecting to the Kafka topic
