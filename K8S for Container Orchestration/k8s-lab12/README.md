# Lab 12: Managing Configuration and Sensitive Data with ConfigMaps and Secrets

## Objective

In this lab, Kubernetes **ConfigMaps** and **Secrets** are used to manage application configuration and sensitive MySQL credentials.

The objectives of this lab are:

- Create a ConfigMap to store non-sensitive MySQL configuration.
- Store `DB_HOST` and `DB_USER` in the ConfigMap.
- Create a Secret to store sensitive MySQL credentials.
- Store `DB_PASSWORD` and `MYSQL_ROOT_PASSWORD` in the Secret.
- Encode Secret values using Base64.
- Verify the created ConfigMap and Secret resources.

---

## Prerequisites

- Kubernetes cluster running.
- `kubectl` installed and configured.
- `ivolve` namespace created.

Verify the namespace:

```bash
kubectl get namespace ivolve
