# Lab 8: Custom Docker Network for Microservices

## Overview

In this lab, we built Docker images for two Python microservices (frontend and backend), created a custom Docker network, and demonstrated how containers communicate when connected to the same network. We also verified that containers on different networks cannot communicate directly.

---

## Prerequisites

* Docker installed
* Git installed
* Basic knowledge of Docker images and networks

---

## Project Structure

```text
Docker-lab8
├── backend
│   ├── Dockerfile
│   └── app.py
├── frontend
│   ├── Dockerfile
│   ├── app.py
│   └── requirements.txt
└── screenshots
```

---

## Step 1: Clone the Repository

```bash
git clone https://github.com/Ibrahim-Adel15/Docker5.git
cd Docker5
```

---

## Step 2: Build Docker Images

### Build Backend Image
```bash
FROM python:3.12-slim

WORKDIR /app

RUN pip install flask

COPY . .

EXPOSE 5000

CMD ["python", "app.py"]
```
```bash
cd backend
docker build -t backend-image .
```

Screenshot:

![Backend Image](screenshots/docker-backend.png)

### Build Frontend Image
```bash
FROM python:3.12-slim

WORKDIR /app

COPY requirements.txt .

RUN pip install --no-cache-dir -r requirements.txt

COPY . .

EXPOSE 5000

CMD ["python", "app.py"]
```
```bash
cd ../frontend
docker build -t frontend-image .
```

Screenshot:

![Frontend Image](screenshots/docker-frontend.png)

---

## Step 3: Create a Custom Network

Create a Docker bridge network named **ivolve-network**.

```bash
docker network create ivolve-network
docker network ls
```

Screenshot:

![Network List](screenshots/network-ls.png)

---

## Step 4: Run the Containers

### Backend

```bash
docker run -d --name backend --network ivolve-network backend-image
```

Screenshot:

![Backend Container](screenshots/backend-net.png)

### Frontend 1 (Custom Network)

```bash
docker run -d --name frontend1 -p 5001:5000 --network ivolve-network frontend-image
```

Screenshot:

![Frontend1](screenshots/frontend1-met.png)

### Frontend 2 (Default Bridge Network)

```bash
docker run -d --name frontend2 -p 5002:5000 frontend-image
```

Screenshot:

![Frontend2](screenshots/frontend2.png)

---

## Step 5: Inspect the Network

Verify that only **backend** and **frontend1** are attached to the custom network.

```bash
docker network inspect ivolve-network
```

Screenshot:

![Network Inspect](screenshots/net-inspect.png)

---

## Step 6: Verify Communication

### Frontend1 → Backend

Since both containers are connected to the same custom network, communication succeeds using the backend container name.

Screenshot:

![Frontend1 to Backend](screenshots/connect-frontend1-backend.png)

### Frontend2 → Backend

Since **frontend2** is running on the default bridge network, it cannot communicate with the backend container.

Screenshot:

![Frontend2 to Backend](screenshots/frontend2-backend.png)

---

## Cleanup

Stop and remove the containers:

```bash
docker rm -f frontend1 frontend2 backend
```

Remove the custom network:

```bash
docker network rm ivolve-network
```

---

## Key Concepts Learned

* Building Docker images for multiple services.
* Creating custom Docker bridge networks.
* Running containers on specific networks.
* Container-to-container communication using container names.
* Docker DNS-based service discovery.
* Network isolation between different Docker bridge networks.

---

## Technologies Used

* Docker
* Docker Networking
* Python
* Flask
* Git
