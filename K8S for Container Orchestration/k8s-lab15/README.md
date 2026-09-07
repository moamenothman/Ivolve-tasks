# Lab 15 – Node.js Application Deployment

## Objective

Deploy a Node.js application on Kubernetes using a custom Docker image, configure the application using environment variables from a ConfigMap and Secret, mount persistent storage for application logs, and expose the application through a ClusterIP Service.

## Architecture

The lab contains:

* **Deployment:** `nodejs-app`
* **Replicas:** 2 configured
* **Namespace:** `ivolve`
* **Docker Image:** `moamenothman1/nodejs-app:v1`
* **ConfigMap:** `mysql-config`
* **Secret:** `mysql-secret`
* **PersistentVolumeClaim:** `app-logs-pvc`
* **Service:** `nodejs-service`
* **MySQL Service:** `mysql`
* **Application Port:** `3000`

> Only one Node.js pod is running because the `ivolve` namespace has a Pod ResourceQuota of 2 from Lab 11, while the MySQL StatefulSet already consumes one pod.

---

## Files Structure

```text
k8s-lab15/
├── mysql-service.yaml
├── nodejs-deployment.yaml
├── nodejs-service.yaml
└── screenshots/
    ├── curl.png
    ├── deployment-yaml.png
    ├── get-deployment.png
    ├── get-pv-pvc.png
    ├── get-svc.png
    ├── port-forward.png
    └── svc-yaml.png
```

---

## 1. Docker Image

The Node.js application image was pulled from Docker Hub, re-tagged under the user's Docker Hub account, and pushed as:

```text
moamenothman1/nodejs-app:v1
```

This image is used by the Kubernetes Deployment.

---

## 2. Node.js Deployment

The Deployment is defined in:

```text
nodejs-deployment.yaml
```

The Deployment is configured with:

* 2 replicas.
* Node.js application container.
* Container port `3000`.
* Toleration for the `node=worker:NoSchedule` taint.
* Environment variables from the ConfigMap and Secret.
* Persistent storage mounted at `/app/logs`.

### Environment Variables

The application receives:

From `mysql-config`:

```text
DB_HOST
DB_USER
```

From `mysql-secret`:

```text
DB_PASSWORD
```

### Persistent Storage

The existing PVC:

```text
app-logs-pvc
```

is mounted inside the container at:

```text
/app/logs
```

![Deployment YAML](screenshots/deployment-yaml.png)

---

## 3. Resource Quota

The `ivolve` namespace has a Pod ResourceQuota configured in Lab 11.

The quota allows a maximum of 2 pods.

Since the MySQL StatefulSet already runs:

```text
mysql-0
```

only one Node.js pod can be scheduled.

The Deployment still has:

```yaml
replicas: 2
```

but the second pod cannot be created because of the namespace Pod quota.

This behavior is expected and matches the lab requirement.

![Deployment Status](screenshots/get-deployment.png)

---

## 4. MySQL Connectivity

The Node.js application uses:

```text
DB_HOST=mysql
```

Therefore, a MySQL ClusterIP Service named `mysql` was created to provide DNS-based connectivity to the MySQL StatefulSet.

The application was successfully connected to MySQL and found the required:

```text
ivolve
```

database.

---

## 5. Node.js Service

A ClusterIP Service named:

```text
nodejs-service
```

was created to expose the Node.js application inside the Kubernetes cluster.

The Service configuration is:

```yaml
apiVersion: v1
kind: Service
metadata:
  name: nodejs-service
  namespace: ivolve
spec:
  type: ClusterIP
  selector:
    app: nodejs
  ports:
    - port: 3000
      targetPort: 3000
      protocol: TCP
```

The Service selects pods using:

```text
app=nodejs
```

and forwards traffic from port `3000` to the Node.js container on port `3000`.

![Service YAML](screenshots/svc-yaml.png)

![Services](screenshots/get-svc.png)

---

## 6. Persistent Volume and PVC

The Node.js application uses the PVC created in Lab 13:

```text
app-logs-pvc
```

The PVC is backed by the static PersistentVolume:

```text
app-logs-pv
```

![PV and PVC](screenshots/get-pv-pvc.png)

---

## 7. Application Testing

The Node.js Service was tested using Kubernetes port forwarding:

```bash
kubectl port-forward svc/nodejs-service 3000:3000 -n ivolve
```

The port forwarding successfully exposed the Service on:

```text
http://localhost:3000
```

![Port Forward](screenshots/port-forward.png)

The application was then tested using:

```bash
curl http://localhost:3000
```

The application returned a successful response, confirming that traffic was successfully routed through the Kubernetes Service to the Node.js application pod.

![Curl Test](screenshots/curl.png)

---

## 8. Useful Kubernetes Commands

### Check Deployment

```bash
kubectl get deployment -n ivolve
```

### Check Pods

```bash
kubectl get pods -n ivolve -o wide
```

### Check Services

```bash
kubectl get svc -n ivolve
```

### Check PVC

```bash
kubectl get pvc -n ivolve
```

### Check Service Endpoints

```bash
kubectl get endpoints nodejs-service -n ivolve
```

### Port Forward the Application

```bash
kubectl port-forward svc/nodejs-service 3000:3000 -n ivolve
```

### Test the Application

```bash
curl http://localhost:3000
```

---

## Result

Lab 15 was successfully completed.

The Node.js application was:

* ✅ Deployed using a custom Docker Hub image.
* ✅ Configured using ConfigMap and Secret environment variables.
* ✅ Connected successfully to the MySQL database.
* ✅ Configured with the required worker-node toleration.
* ✅ Configured to use the persistent application-log storage.
* ✅ Exposed through a Kubernetes ClusterIP Service.
* ✅ Successfully tested using port forwarding and `curl`.
* ✅ Integrated with the existing Kubernetes resources from previous labs.

