# 🚀 High-Concurrency Order Processing API

Este proyecto simula un sistema de procesamiento de pedidos con foco en **alta concurrencia, escalabilidad y observabilidad**, desarrollado como parte del portafolio profesional de **Ignacio J. Maylin – Senior Java Backend Developer**.

---

## 🧩 Módulos del proyecto

| Módulo                                | Descripción                                                                 |
|---------------------------------------|------------------------------------------------------------------------------|
| [`order-processor`](./order-processor) | Microservicio Spring Boot para procesar pedidos de forma asíncrona.         |
| [`gatling`](./gatling)                | Simulador de carga en Java DSL para testear concurrencia sobre la API REST.|
| [`helm`](.devops/helm-order-service)  | Helm Chart para desplegar `order-processor` en Kubernetes con HPA.          |

---

## 🔧 Tecnologías usadas

- Java 21 + Spring Boot 3.2
- ExecutorService + CompletableFuture
- Gatling + Java DSL
- Docker & Helm
- Kubernetes (con autoscaling vía HPA)
- Prometheus (para métricas y tuning)

---

## 📦 order-processor

Servicio backend que expone una API REST para procesar pedidos.

### Endpoints principales

```http
POST /processOrder
GET  /processOrder/status/{orderId}
```
## Lógica destacada
- **Persistencia real con PostgreSQL vía Spring Data JPA**
- Simulación de retardo controlado por etapas (validación, stock, precio)
- Procesamiento distribuido vía `CompletableFuture.allOf(...)`
- Logs estructurados (info, warn, error) con SLF4J para trazabilidad

🧪 Leer más en order-processor/README.md

## 📈 gatling
- **Pruebas de carga y stress testing de la API /processOrder.**

- Simulación de cientos/miles de pedidos concurrentes

- Personalización vía gatling.properties

- Reportes HTML con métricas clave

- Perfiles liviano (light) y pesado (heavy)

▶️ Ejecutar:

```bash
cd gatling
mvn clean gatling:test -Dgatling.profile=light
```
📊 Leer más en gatling/README.md

## ☁️ helm
-  **Despliegue automatizado en Kubernetes con soporte para escalado automático.**

- Deployment, Service, HPA templados

- Variables configurables vía values.yaml

- Métricas disponibles para Prometheus (CPU, memoria)

☸️ Instalar:

```bash
cd helm/order-service
helm upgrade --install order ./order-service
```
📊 Leer más en helm/order-service/README.md

## 🎯 Cómo correr el proyecto
1. Levantar el backend

```bash
cd order-processor
mvn clean spring-boot:run
```
2. Ejecutar pruebas de carga

```bash
cd gatling
mvn gatling:test -Dgatling.profile=light
```
3. Desplegar en Kubernetes
```bash
   cd helm/order-service
   helm upgrade --install order ./order-service
```
## 📊 Observabilidad
-  **Con Prometheus instalado, podés monitorear métricas de:**

-  Uso de CPU por pod

-  Uso de memoria absoluta y relativa

-  Número de réplicas del HPA

Comandos útiles:
```bash
kubectl get pods
kubectl get hpa
kubectl port-forward svc/prometheus-server -n monitoring 9090:80
```
## 📁 Estructura

```bash
.
├── order-processor/        # API REST y lógica de procesamiento
├── gatling/                # Simulador de carga
├── devops/helm-order-service/     # Chart Helm para Kubernetes
└── README.md               # Este archivo
```

🧑‍💻 Autor
Ignacio J. Maylin
Proyecto de portafolio – 2025
📫 Contacto: ignaciomaylin.@gmail.com

