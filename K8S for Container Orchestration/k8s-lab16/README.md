# Lab 16 – Kubernetes Init Container for Pre-Deployment Database Setup

## 📌 Overview

In this lab, an **Init Container** was added to the existing Node.js Kubernetes Deployment to perform database initialization before the main application container starts.

The Init Container waits until MySQL is ready, creates the `ivolve` database, creates the application user, and grants the required privileges.

---

## 🎯 Objectives

* Modify the existing Node.js Deployment.
* Add an Init Container using the `mysql:5.7` image.
* Wait for MySQL to become ready before starting the application.
* Create the `ivolve` database if it does not already exist.
* Create the `ivolve_user` application user.
* Grant the user full privileges on the `ivolve` database.
* Verify the database, user, and privileges.

---

## 📁 Project Structure

```text
k8s-lab16/
├── nodejs_deployment.yaml
└── screenshots/
    ├── nodejs_deployment_yaml.png
    └── sql_check.png
```

---

## ⚙️ Kubernetes Configuration

The Node.js Deployment was updated with an Init Container named:

```yaml
db-init
```

The Init Container uses:

```yaml
image: mysql:5.7
```

Database connection parameters are provided using the existing Kubernetes **ConfigMap** and **Secret** resources.

### ConfigMap

The Init Container retrieves:

* `DB_HOST`
* `DB_USER`

from:

```text
mysql-config
```

### Secret

The following sensitive values are retrieved from:

```text
mysql-secret
```

* `DB_PASSWORD`
* `MYSQL_ROOT_PASSWORD`

No passwords are stored directly inside the Deployment manifest.

---

## 🔄 Init Container Workflow

The Init Container performs the following steps:

1. Waits for the MySQL server to become available.
2. Uses `mysqladmin ping` to check MySQL readiness.
3. Creates the `ivolve` database if it does not exist.
4. Creates the `ivolve_user` account if it does not exist.
5. Grants all pri

