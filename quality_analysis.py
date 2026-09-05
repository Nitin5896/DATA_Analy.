# Quality & Operations Analytics
# Run from the python/ folder or update DATA_PATH accordingly.

import pandas as pd
import matplotlib.pyplot as plt

DATA_PATH = "../data/production_quality_data.csv"
df = pd.read_csv(DATA_PATH, parse_dates=["Date"])

# KPI calculation
total_units = df["Units_Produced"].sum()
total_defects = df["Defects"].sum()
defect_rate = total_defects / total_units
fpy = df["Good_Units"].sum() / total_units
copq = df["Cost_of_Poor_Quality"].sum()

print("Total units:", total_units)
print("Total defects:", total_defects)
print("Defect rate:", round(defect_rate*100, 2), "%")
print("First pass yield:", round(fpy*100, 2), "%")
print("COPQ:", round(copq, 2))

# Line analysis
line = (df.groupby("Production_Line")
        .agg(Units=("Units_Produced","sum"),
             Defects=("Defects","sum"),
             Downtime=("Downtime_Hours","sum"),
             COPQ=("Cost_of_Poor_Quality","sum"))
        .reset_index())
line["Defect_Rate"] = line["Defects"] / line["Units"]
line["FPY"] = 1 - line["Defect_Rate"]
print("\nLine performance:\n", line.sort_values("Defect_Rate", ascending=False))

# Root-cause analysis
cause = df.groupby("Root_Cause", as_index=False)["Defects"].sum()
cause["Share"] = cause["Defects"] / cause["Defects"].sum()
print("\nRoot causes:\n", cause.sort_values("Defects", ascending=False))

# Monthly trend
monthly = (df.assign(Month=df["Date"].dt.to_period("M").astype(str))
             .groupby("Month")
             .agg(Units=("Units_Produced","sum"),
                  Defects=("Defects","sum"),
                  COPQ=("Cost_of_Poor_Quality","sum"))
             .reset_index())
monthly["Defect_Rate"] = monthly["Defects"] / monthly["Units"]

# Save analysis outputs
line.to_csv("../data/line_performance.csv", index=False)
cause.to_csv("../data/root_cause_summary.csv", index=False)
monthly.to_csv("../data/monthly_quality_summary.csv", index=False)

# Chart 1: defect rate by line
plt.figure(figsize=(9,5))
plt.bar(line["Production_Line"], line["Defect_Rate"]*100)
plt.title("Defect Rate by Production Line")
plt.ylabel("Defect Rate (%)")
plt.xlabel("Production Line")
plt.tight_layout()
plt.savefig("../screenshots/defect_rate_by_line.png", dpi=180)
plt.show()

# Chart 2: monthly defect rate
plt.figure(figsize=(11,5))
plt.plot(monthly["Month"], monthly["Defect_Rate"]*100, marker="o")
plt.title("Monthly Defect Rate Trend")
plt.ylabel("Defect Rate (%)")
plt.xlabel("Month")
plt.xticks(rotation=45)
plt.tight_layout()
plt.savefig("../screenshots/monthly_defect_rate.png", dpi=180)
plt.show()
