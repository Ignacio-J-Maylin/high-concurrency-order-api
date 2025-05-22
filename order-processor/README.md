# Order Processing API - v1

Este proyecto es una simulación de un sistema de procesamiento de pedidos con alta concurrencia, desarrollado en Java con Spring Boot. Forma parte de un portafolio de desarrollador Senior Backend orientado a rendimiento, concurrencia y escalabilidad.

---

## ✅ Características de la versión 1

### 🚀 API REST
- Endpoint principal: `POST /processOrder`
- Recibe pedidos en formato JSON:
  ```json
  {
    "orderId": "uuid-123",
    "customerId": "client-456",
    "orderAmount": 129.99,
    "orderItems": ["item1", "item2"]
  }

## ⚙️ Concurrencia y rendimiento
- Procesamiento asíncrono con CompletableFuture.

- Simulación de lógica de negocio con retardo aleatorio de 100 a 500 ms.

- Uso de ExecutorService con pool de hilos dimensionado según el hardware.

- Almacenamiento en memoria con ConcurrentHashMap (thread-safe).

- Logs informativos (info, debug, warn) usando SLF4J.

## 📈 Pruebas de carga
- Simulaciones de usuarios concurrentes con Gatling.

- Parametrización con profiles (USERS, DURATION).

- Reportes HTML automáticos de métricas.

## 🧪 Cómo probar
1. Levantar el backend (puerto 8080 por defecto).

2. Ejecutar pruebas leyendo el readme de Gatling

3. Ver el reporte generado en: /gatling/target/gatling/<nombre-de-simulacion-fecha>/index.html
