-- QUALITY & OPERATIONS ANALYTICS
-- SQL dialect: PostgreSQL-style. Replace DATE_TRUNC/EXTRACT functions for other databases.

-- 01. Overall KPI
SELECT
    SUM(units_produced) AS total_units,
    SUM(defects) AS total_defects,
    ROUND(100.0 * SUM(defects) / NULLIF(SUM(units_produced),0), 2) AS defect_rate_pct,
    ROUND(100.0 * SUM(good_units) / NULLIF(SUM(units_produced),0), 2) AS first_pass_yield_pct,
    SUM(scrap_units) AS scrap_units,
    SUM(rework_units) AS rework_units,
    ROUND(SUM(downtime_hours),2) AS downtime_hours,
    ROUND(SUM(cost_of_poor_quality),2) AS copq
FROM production_quality_data;

-- 02. Monthly quality trend
SELECT
    DATE_TRUNC('month', date)::date AS month,
    SUM(units_produced) AS units_produced,
    SUM(defects) AS defects,
    ROUND(100.0 * SUM(defects) / NULLIF(SUM(units_produced),0),2) AS defect_rate_pct,
    ROUND(100.0 * SUM(good_units) / NULLIF(SUM(units_produced),0),2) AS fpy_pct
FROM production_quality_data
GROUP BY 1
ORDER BY 1;

-- 03. Production-line performance
SELECT
    production_line,
    SUM(units_produced) AS units_produced,
    SUM(defects) AS defects,
    ROUND(100.0 * SUM(defects) / NULLIF(SUM(units_produced),0),2) AS defect_rate_pct,
    ROUND(100.0 * SUM(good_units) / NULLIF(SUM(units_produced),0),2) AS fpy_pct,
    ROUND(SUM(downtime_hours),2) AS downtime_hours,
    ROUND(SUM(cost_of_poor_quality),2) AS copq
FROM production_quality_data
GROUP BY production_line
ORDER BY defect_rate_pct DESC;

-- 04. Root-cause Pareto
SELECT
    root_cause,
    SUM(defects) AS defects,
    ROUND(100.0 * SUM(defects) /
        SUM(SUM(defects)) OVER (),2) AS defect_share_pct
FROM production_quality_data
GROUP BY root_cause
ORDER BY defects DESC;

-- 05. Product performance
SELECT
    product,
    SUM(units_produced) AS units_produced,
    SUM(defects) AS defects,
    ROUND(100.0 * SUM(defects) / NULLIF(SUM(units_produced),0),2) AS defect_rate_pct,
    ROUND(SUM(cost_of_poor_quality),2) AS copq
FROM production_quality_data
GROUP BY product
ORDER BY defect_rate_pct DESC;

-- 06. Shift performance
SELECT
    shift,
    SUM(units_produced) AS units_produced,
    SUM(defects) AS defects,
    ROUND(100.0 * SUM(defects) / NULLIF(SUM(units_produced),0),2) AS defect_rate_pct,
    ROUND(100.0 * SUM(good_units) / NULLIF(SUM(units_produced),0),2) AS fpy_pct
FROM production_quality_data
GROUP BY shift
ORDER BY defect_rate_pct DESC;

-- 07. Machine downtime
SELECT
    machine_id,
    ROUND(SUM(downtime_hours),2) AS downtime_hours,
    SUM(defects) AS defects,
    ROUND(SUM(cost_of_poor_quality),2) AS copq
FROM production_quality_data
GROUP BY machine_id
ORDER BY downtime_hours DESC;

-- 08. Highest-risk production records
SELECT *
FROM production_quality_data
ORDER BY defect_rate DESC, cost_of_poor_quality DESC
LIMIT 20;

-- 09. Monthly COPQ
SELECT
    DATE_TRUNC('month', date)::date AS month,
    ROUND(SUM(cost_of_poor_quality),2) AS copq,
    SUM(scrap_units) AS scrap_units,
    SUM(rework_units) AS rework_units
FROM production_quality_data
GROUP BY 1
ORDER BY 1;

-- 10. Defect rate above 4%
SELECT
    production_line,
    product,
    ROUND(100.0 * SUM(defects)/NULLIF(SUM(units_produced),0),2) AS defect_rate_pct
FROM production_quality_data
GROUP BY production_line, product
HAVING 100.0 * SUM(defects)/NULLIF(SUM(units_produced),0) > 4
ORDER BY defect_rate_pct DESC;
