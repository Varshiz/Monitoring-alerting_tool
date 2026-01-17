
# 🚨 JVM Resource Monitoring & Alerting System 

## 📌 Overview

This project is a **production-style Proof of Concept** for **monitoring JVM-based applications** (Spring Boot) running inside **Docker**, with **real-time CPU & RAM monitoring**, **spike detection**, **threshold breach alerts**, **JVM-down detection**, and **email notifications**.



This system ensures **early warning, observability, and reliability** without embedding alerting logic inside the application itself.

---

## 🎯 Objectives

- Monitor **CPU usage** of a Spring Boot JVM
- Monitor **JVM heap (RAM) usage**
- Detect:
  - Sudden CPU spikes
  - Sustained CPU threshold breaches
  - Sudden RAM spikes
  - RAM threshold breaches
  - JVM crashes / stops
- Send **email alerts automatically**
- Run everything in **Docker**
- Keep the application **alert-logic-free** (clean architecture)

---

## 🧠 Key Design Philosophy

> **The application only exposes metrics.  
> Monitoring systems decide what is wrong.  
> Notification systems decide whom to notify.**

This separation ensures:
- Clean code
- Production-grade observability
- Resilience even when JVM crashes

---

## 🏗️ Technology Stack

| Layer | Technology |
|-----|-----------|
| Application | Spring Boot |
| Metrics | Spring Boot Actuator |
| Containerization | Docker |
| Metrics Collection | Prometheus |
| Visualization | Grafana |
| Alerting Engine | Prometheus Alert Rules |
| Notification | Alertmanager |
| Notification Channel | Email (SMTP) |

---

## 🧱 High-Level Architecture

```
+--------------------+
|  Spring Boot App   |
|  (Actuator Metrics)|
+---------+----------+
          |
          | /actuator/prometheus
          v
+--------------------+
|    Prometheus      |
| (Scrape + Rules)   |
+---------+----------+
          |
          | Alerts
          v
+--------------------+
|  Alertmanager      |
| (Routing + Email)  |
+---------+----------+
          |
          | SMTP
          v
+--------------------+
|   Email Inbox      |
+--------------------+

Optional:
Prometheus ---> Grafana (Visualization)
```

---

## 🔁 End-to-End Flow (Sequence)

```
1. JVM runs inside Docker
2. Actuator exposes metrics
3. Prometheus scrapes metrics every 2s
4. Alert rules evaluate:
   - CPU spike
   - CPU threshold breach
   - RAM spike
   - RAM threshold breach
   - JVM down
5. If alert fires:
   -> Alertmanager receives it
   -> Alertmanager sends email
6. User receives notification
```

---

## 📂 Project Structure

```
alertingsys/
├── Dockerfile
├── pom.xml
├── prometheus.yml
├── alert-rules.yml
├── alertmanager.yml
├── README.md
└── target/
    └── alertingsys-0.0.1-SNAPSHOT.jar
```

---

## ⚙️ Step-by-Step Setup Guide

### 1️⃣ Build Spring Boot Application

```bash
mvn clean package
```

This generates the runnable JAR inside `target/`.

---

### 2️⃣ Dockerize Spring Boot App

```bash
docker build -t monitoring-alert-tool .
```

Run the container with CPU & RAM limits:

```bash
docker run   --name monitoring-tool   --network monitoring-net   --cpus="1.0"   --memory="512m"   -p 8080:8080   monitoring-alert-tool
```

---

### 3️⃣ Create Docker Network

```bash
docker network create monitoring-net
```

This allows containers to communicate using names.

---

### 4️⃣ Prometheus Configuration

#### `prometheus.yml`

```yaml
global:
  scrape_interval: 2s
  evaluation_interval: 2s

rule_files:
  - /etc/prometheus/alert-rules.yml

alerting:
  alertmanagers:
    - static_configs:
        - targets:
            - "alertmanager:9093"

scrape_configs:
  - job_name: "spring-boot-app"
    metrics_path: "/actuator/prometheus"
    static_configs:
      - targets:
          - "monitoring-tool:8080"
```

---

### 5️⃣ Alert Rules

#### `alert-rules.yml`

Includes alerts for:

- JVM Down
- CPU Spike
- CPU Threshold Breach
- RAM Spike
- RAM Threshold Breach

These alerts are evaluated **every 2 seconds**.

---

### 6️⃣ Run Prometheus

```bash
MSYS_NO_PATHCONV=1 docker run   --name prometheus   --network monitoring-net   -p 9090:9090   -v "$(pwd)/prometheus.yml:/etc/prometheus/prometheus.yml"   -v "$(pwd)/alert-rules.yml:/etc/prometheus/alert-rules.yml"   prom/prometheus   --config.file=/etc/prometheus/prometheus.yml
```

Verify:
- http://localhost:9090
- Status → Targets
- Status → Rules

---

### 7️⃣ Grafana (Visualization)

```bash
docker run   --name grafana   --network monitoring-net   -p 3000:3000   grafana/grafana
```

- URL: http://localhost:3000
- Default login: admin / admin
- Add Prometheus datasource:
  - URL: http://prometheus:9090

---

### 8️⃣ Alertmanager Configuration

#### `alertmanager.yml`

```yaml
global:
  smtp_smarthost: 'smtp.gmail.com:587'
  smtp_from: 'your-email@gmail.com'
  smtp_auth_username: 'your-email@gmail.com'
  smtp_auth_password: 'your-app-password'
  smtp_require_tls: true

route:
  receiver: 'email-notifications'

receivers:
- name: 'email-notifications'
  email_configs:
  - to: 'receiver-email@gmail.com'
    send_resolved: true
```

---

### 9️⃣ Run Alertmanager

```bash
MSYS_NO_PATHCONV=1 docker run   --name alertmanager   --network monitoring-net   -p 9093:9093   -v "$(pwd)/alertmanager.yml:/etc/alertmanager/alertmanager.yml"   prom/alertmanager   --config.file=/etc/alertmanager/alertmanager.yml
```

Verify:
- http://localhost:9093

---

## 🧪 Testing Scenarios

### CPU Spike
```bash
curl -X POST "http://localhost:8080/stress/cpu/start?threads=2"
```

### RAM Spike
```bash
curl -X POST "http://localhost:8080/stress/memory/allocate?mb=50"
```

### JVM Down
```bash
docker stop monitoring-tool
```

Emails should be received for each case.

---

## Important JVM Behavior 

- JVM **does NOT release heap memory back to OS**
- RAM usage staying high after deallocation is **expected**
- RAM alerts are **one-way signals**
- Recovery strategy = **restart JVM/container**




---


---




