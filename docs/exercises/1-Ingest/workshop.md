# Exercise 1: Data Ingestion with Apache NiFi

### Objective:
Use Apache NiFi to ingest data from a source (S3 bucket in our case), transform it, and store the data in our platform
(another S3 bucket in our case).

[Nifi](https://nifi.dev1.kubelake.com)

## Introduction to Apache NiFi

Apache NiFi is an open-source data integration tool designed to automate the flow of data between systems. 
It is particularly powerful for building scalable data pipelines with minimal coding. 
NiFi offers a user-friendly web interface and provides powerful data transformation and routing capabilities.
Here are some key benefits of NiFi:

1. **User-Friendly GUI**: NiFi’s drag-and-drop interface makes it easy to design and manage data pipelines.
2. **Real-Time Data Processing**: Process data in real-time or batch mode with flexible scheduling.
3. **Data Provenance**: NiFi tracks every piece of data, providing complete visibility of the data flow.
4. **Supports Multiple Data Sources**: NiFi can connect with various data sources like file-systems, databases, cloud services, APIs, etc.
5. **Extensible**: You can easily extend its capabilities with custom processors or scripts.

In this exercise, let's see how to configure NiFi to read data from an MinIO bucket (S3-compatible), 
perform minor modifications on the data, and write the data back to another MinIO location.

The input data consists of random generated multi-line .json files with information about
a certain stock in a day (lets say APPLE) and if in that day:

- there were any significant Events
- the Event type if there were any (Social/Governance/Climate)
- the impact factor

Example:


      [{
      "date": "2023-12-04",
      "open": 108.4,
      "high": 115.07,
      "low": 100.15,
      "close": 101.53,
      "volume": 1728881,
      "event": "Mass workforce strike",
      "event_type": "Social Event",
      "impact_factor": -0.04 //This value represented the expected effect of the event on the stock price, either positive or negative as a procentage
      }..]

A day with no events would look like this

      [{
      "date": "2023-12-09",
      "open": 105.0,
      "high": 110.65,
      "low": 106.97,
      "close": 107.09,
      "volume": 3470925,
      "event": null,
      "event_type": null,
      "impact_factor": 0 // This value represented the expected effect of the event on the stock price, either positive or negative as a procentage
      }..]

!!! info "Fair Warning PSA"
      This exercise exceeds to a certain extent what is usually recommended to do with Nifi (Extract / Load).
      We are going to modify the data a bit (Transform) just to play around with Nifi's capabilities.
      For Big Data any transformation should be done outside Nifi (although even Nifi can scale as it runs in cluster mode)

### Exercise Goals:

- Fetch JSON data from an bucket (.json files).
- Split the files into individual JSON's.
- Modify the JSON data (e.g., add a new field or change a value).
- Store the processed data into 2 locations
- One location for archiving (after we add some metadata)
- Another location for later processing but keeping the data as at the source (/bronze)

---

## Step 0: Overview of our NiFi Environment

### Prerequisites:
1. Apache NiFi installed and running (already done).
2. Access a data source (data is already saved in MinIO, but we could also test a public API if we want).
3. Basic knowledge of JSON format.

### NiFi Components Overview
The core component in NiFi is a **Processor** (processors are like LEGO pieces we can combine).

-  fetch data
-  modify data
-  writing data to a destination.

Nifi offers pre-built processors (so you won’t need to write any custom code).

In this exercise, we'll use the following NiFi processors:

- **ListS3**: To list objects in an S3 bucket.
- **FetchS3Object**: To retrieve the data from the bucket.
- **SplitJson**: To split the .json files.
- **JoltTransformJSON**: To perform minor modifications on the JSON data.
- **PutS3Object**: To save the modified data to the MinIO bucket.
- **Etc.**

### NiFi teacher pass-through DEMO
[Nifi](https://nifi.dev1.kubelake.com)


## DIY the rest (20 min / 35 min)

- Log into [Nifi](https://nifi.dev1.kubelake.com)
- credentials : demo@kubelake.com - ask me for the password

## Step 1: Very Important  -> Create your own working environment
1. Drag and drop a new process group from the left (4th button to the right of the logo)
2. Add you name to the process group name or whatever you want to name it (please do not interfere with other people's work)


## Step 2: Fetch Data from S3


### Configure ListS3 Processor:
1. Drag and drop the **ListS3** processor onto the NiFi canvas.
2. Right-click on the processor and select **Configure**.
3. In the **Properties** tab:
     - Set the **Bucket Name** to the name of the MinIO bucket (*datalake*)
     - Configure **Region** to match the region of your MinIO bucket (*EU Frankfurt*)
     - Set your **MinIO Credentials** (select the *MinioCredentialsProviderControllerService* already connected to MinIO).
     - Set the Endpoint Override URL because this processor usually connects to S3, but now we are using it to connect to MinIO (*http://minio.kubelake-storage*).
     - Set the **Prefix** to where our files are (*ingest/stock_data/*)
4. This processor will list all objects in the specified MinIO bucket.
5. Click right to run it once and to run it again click right -> view state -> clear state, then you can run it again
   (this processor stores the timestamp of the read files so that we can keep it running forever and just read new or updated files)

### Configure FetchS3Object Processor:
1. Drag and drop the **FetchS3Object** processor.
2. Connect the **success** relationship from **ListS3** to **FetchS3Object** 
3. The **failure** relationship we can redirect to the same FetchS3Object processor (if our relationship fails we can sometimes try again, right??)
4. In **FetchS3Object**, configure the **MinIO Bucket** to the same bucket as in **ListS3**.
5. Set **Key** to the `${filename}` attribute, which references the files listed by **ListS3**.

---

## Step 3: Split Json files
1. Drag and drop the **SplitJson** processor.
2. Connect the **success** relationship from **FetchS3Object** to **SplitJson**.
3. The **failure** relationship we can redirect to the same SplitJson processor (if our relationship fails we can sometimes try again, right??)
4. Set JsonPath Expression to **$.***

## Step 4: Modify the JSON Data

### Configure JoltTransformJSON Processor:
1. Drag and drop the **JoltTransformJSON** processor onto the canvas.
2. Connect the **success** relationship from **SplitJson** to **JoltTransformJSON**.
3. In **JoltTransformJSON**:
      - Set the **Jolt Specification** (Jolt spec) to a simple transformation. For example, you could add a new field:

```
[
   {
      "operation": "default",
      "spec": {
         "processed_by": "demo_user"
      }
   }
]
```

4. This will modify your JSON data by adding a new field called "newField" with the value "NiFi is awesome!".

## Step 5: Store the archived Data in MinIO

### Configure PutS3Object Processor:

1. Drag and drop the **PutS3Object** processor onto the canvas.
2. Connect the **split** relationship from **JoltTransformJSON** to **PutS3Object**.
3. Configure **PutS3Object** the same as **ListS3**:
     - Set the **Bucket** to *datalake*
     - Configure **Region** to *EU Frankfurt* (or set it to Use 's3.region' Attribute)
     - Set your **MinIO Credentials** to the *MinioCredentialsProviderControllerService* 
     - Set the Endpoint Override to *http://minio.kubelake-storage*.
     - The **Object Key** will be different, set it to /archive/{your_name_or_project_name}/2024-10-15/${UUID()}.json

---
## Step 6: Store the original Data in MinIO in another bucket

### Configure PutS3Object Processor:

1. Drag and drop the **PutS3Object** processor onto the canvas.
2. Connect the **original** relationship from **JoltTransformJSON** to **PutS3Object**.
3. Configure **PutS3Object** the same as **ListS3**:
    - Set the **Bucket** to *datalake*
    - Configure **Region** to *EU Frankfurt* (or set it to Use 's3.region' Attribute)
    - Set your **MinIO Credentials** to the *MinioCredentialsProviderControllerService*
    - Set the Endpoint Override to *http://minio.kubelake-storage*.
    - The **Object Key** will be different, set it to /bronze/{your_name_or_project_name}/2024-10-15/${UUID()}.json

---
## Step 7: Finalize and Run

### Create Connections and Run:

1. Connect the **success** relationship of each processor as outlined above.
2. Right-click on each Nifi processor and select **Start**.
3. NiFi will now fetch files from the MinIO bucket, transform the JSON, and save the modified files in another MinIO location.
4. If for any reason you fail to get the running flow, you can drag and drop a template with the running flow from the
left top part of the nifi interface (Template -> Drag and Drop -> Choose Demo Ingest)
5. Then just go through the flow and alter the MinIO path and run the flow

### Monitor Data Flow:

1. Use NiFi’s built-in **Data Provenance** to track the data's journey from start to finish.
2. You can view data at each step by right-clicking on a connection and selecting **List Queue**.

---

## Step 7: Review and Stop running processors

1. Review the processed data in your MinIO bucket.
- Log into [MinIO]( https://storage.dev1.kubelake.com)
- credentials ->  demo@kubelake.com : ask me for the password
2. **Stop** the NiFi flow when you’re finished (click right on the canvas and STOP)

---

## Summary

In this exercise, we have (hopefully) learned how to:

- Configure NiFi to interact with MinIO.
- Use processors like **ListS3**, **FetchS3Object**, **JoltTransformJSON**, and **PutS3Object** to create a simple data ingest pipeline.
- Modify a little bit our data.
- Store the data in our data platform.

What did you think was the hardest part about Nifi?
