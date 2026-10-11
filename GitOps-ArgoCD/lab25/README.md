# Lab 25: GitOps with Argo CD

## Overview

This lab demonstrates a GitOps workflow using Argo CD to deploy and manage a Kubernetes application from a GitHub repository.

The application was initially deployed in the `default` namespace. During the lab, the configuration was updated to use a dedicated `lab25` namespace, and the Deployment and Service manifests were separated. Argo CD was then used to track repository changes and synchronize the application with the desired state.

## Objectives

- Install and access Argo CD.
- Configure an Argo CD Application to track a GitHub repository.
- Move the application configuration from `default` to `lab25`.
- Separate the Deployment and Service manifests.
- Configure application access through NodePort.
- Verify synchronization and Kubernetes resource status.
- Understand the role of Jenkins Shared Libraries in the CI/CD workflow.

## Repository Structure

```text
lab25/
├── Jenkinsfile
├── README.md
├── argocd/
│   └── lab25-application.yaml
├── deployment.yaml
├── service.yaml
└── screenshots/
    ├── application_in_argo_cd_ui_before_change_in_the_repo.png
    ├── argocd_autosynced.png
    ├── argocd_out_of_sync.png
    ├── change_argo_server_nodeport_to_access_ui.png
    ├── deployment_yaml.png
    ├── get_application.png
    ├── get_pods_argocd_ns.png
    ├── get_pods_service_after_sync.png
    ├── install_argocd.png
    └── lab25_app_yaml.png
```

## 1. Install Argo CD

Created the `argocd` namespace and installed Argo CD in the Kubernetes cluster.

```bash
kubectl create namespace argocd

kubectl apply -n argocd \
  -f https://raw.githubusercontent.com/argoproj/argo-cd/stable/manifests/install.yaml

kubectl get pods -n argocd
```

![Argo CD installation](screenshots/install_argocd.png)

## 2. Access the Argo CD UI

Configured NodePort access for the Argo CD server to access its web interface.

![Argo CD server NodePort configuration](screenshots/change_argo_server_nodeport_to_access_ui.png)

## 3. Configure the Initial Application

Created the Argo CD Application resource to track the GitHub repository and deploy the Kubernetes manifests.

Repository configuration:

- **Repository:** `https://github.com/moamenothman/Ivolve-tasks.git`
- **Branch:** `main`
- **Path:** `GitOps-ArgoCD/lab25`

The application was initially configured with its Deployment in the `default` namespace.

![Initial application state in Argo CD](screenshots/application_in_argo_cd_ui_before_change_in%20_the_repo.png)

![Argo CD Application manifest](screenshots/lab25_app_yaml.png)

## 4. Move the Application to the lab25 Namespace

Updated the deployment configuration to use the dedicated `lab25` namespace instead of `default`.

The configuration was also reorganized into separate manifests:

- `deployment.yaml`
- `service.yaml`

The Argo CD Application destination was aligned with the intended application namespace.

![Updated Deployment configuration](screenshots/deployment_yaml.png)

## 5. Synchronize Changes with Argo CD

Committed and pushed the updated configuration to GitHub, allowing Argo CD to detect and reconcile the changes.

### OutOfSync State

Verified that Argo CD detects differences between the Git configuration and the live Kubernetes resources.

![Argo CD OutOfSync state](screenshots/argocd_out_of_sync.png)

### Automatic Synchronization

Verified the application's automatic synchronization after the configuration changes.

![Argo CD automatic synchronization](screenshots/argocd_autosynced.png)

## 6. Verify the Final Deployment

Verified the Argo CD Application and its resources after synchronization.

**Argo CD Application:**

```bash
kubectl get applications -n argocd
```

![Argo CD Application verification](screenshots/get_application.png)

**Argo CD components:**

```bash
kubectl get pods -n argocd
```

![Argo CD namespace Pods](screenshots/get_pods_argocd_ns.png)

**Application resources in `lab25`:**

```bash
kubectl get deployments,pods,services -n lab25
```

![Application Pods and Service after synchronization](screenshots/get_pods_service_after_sync.png)

## 7. Jenkins Shared Library

The lab also uses a Jenkins Shared Library to support reusable pipeline logic.

Shared Libraries help centralize common pipeline steps, reduce duplication, and maintain consistency across Jenkins projects. Jenkins handles the CI pipeline and image publishing, while Argo CD manages deployment synchronization from Git.

## Conclusion

This lab demonstrates a GitOps workflow with Argo CD, including:

- Installing and accessing Argo CD.
- Tracking Kubernetes manifests from GitHub.
- Moving the application from `default` to `lab25`.
- Separating Deployment and Service manifests.
- Verifying `OutOfSync` and automatic synchronization.
- Confirming the final application resources in Kubernetes.

**Technologies:** Kubernetes, Argo CD, Jenkins, Jenkins Shared Libraries, Docker, GitHub, YAML, and Minikube.

