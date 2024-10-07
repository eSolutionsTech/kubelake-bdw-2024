# Redefine parameters for the new use case
esg_events = [
    {"type": "Climate Event", "description": "Hurricane damages key infrastructure", "impact": -0.05},
    {"type": "Climate Event", "description": "Breakthrough in green energy technology", "impact": 0.04},
    {"type": "Climate Event", "description": "Carbon tax implementation", "impact": -0.03},
    {"type": "Social Event", "description": "Mass workforce strike", "impact": -0.04},
    {"type": "Social Event", "description": "Successful DEI initiative", "impact": 0.03},
    {"type": "Social Event", "description": "Major labor law reform", "impact": 0.02},
    {"type": "Governance Event", "description": "CEO resignation amid scandal", "impact": -0.06},
    {"type": "Governance Event", "description": "Board restructuring with focus on sustainability", "impact": 0.05},
    {"type": "Governance Event", "description": "Hostile takeover attempt", "impact": -0.07},
    {"type": "Governance Event", "description": "Merger announcement", "impact": 0.04}
]

# Redefine the data generation with ESG events
data = []
current_price = initial_price
current_date = datetime.now() - timedelta(days=num_days)

for _ in range(num_days):
    # Start with normal price change
    daily_change = random.uniform(-volatility, volatility)

    # Randomly decide if an ESG event happens today
    event = None
    if random.random() < 0.1:  # 10% chance of an ESG event on any given day
        event = random.choice(esg_events)
        impact = event["impact"]
        daily_change += impact  # Adjust the daily change based on ESG event impact
    else:
        impact = 0

    # Update stock price
    current_price *= (1 + daily_change)

    # Append to data
    data.append({
        "date": current_date.strftime("%Y-%m-%d"),
        "open": round(current_price * random.uniform(0.98, 1.02), 2),
        "high": round(current_price * random.uniform(1.00, 1.05), 2),
        "low": round(current_price * random.uniform(0.95, 1.00), 2),
        "close": round(current_price, 2),
        "volume": random.randint(1000000, 5000000),
        "event": event["description"] if event else None,
        "event_type": event["type"] if event else None,
        "impact_factor": impact
    })

    # Move to the next day
    current_date += timedelta(days=1)

# Save data to multiple JSON files (same as before)
data_chunks = [data[i::num_files] for i in range(num_files)]  # Split into 5 chunks
zip_filename = "/mnt/data/esg_stock_data.zip"

with zipfile.ZipFile(zip_filename, 'w') as zf:
    for i, chunk in enumerate(data_chunks):
        json_filename = f"esg_stock_data_{i+1}.json"
        with open(json_filename, 'w') as json_file:
            json.dump(chunk, json_file, indent=4)
        zf.write(json_filename)

# Provide the path to the zip file
zip_filename
