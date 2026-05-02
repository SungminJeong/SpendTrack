# 📱 SpendTrack
Aplicación de gestión de gastos personales (Offline • MVVM)

---

## 1. Descripción

SpendTrack es una aplicación móvil para Android que permite registrar, organizar y analizar gastos personales de forma sencilla.

La aplicación funciona completamente en local, sin necesidad de conexión a internet, garantizando privacidad y rendimiento.

---

## 2. Objetivos

- Registrar gastos de forma sencilla
- Organizar por categorías
- Filtrar datos fácilmente
- Visualizar estadísticas
- Analizar la evolución mensual

---

## 3. Funcionalidades

### Autenticación
- Registro de usuario
- Inicio de sesión
- Encriptación de contraseña (SHA-256)

### Gestión de gastos
- Crear gasto
- Editar gasto
- Eliminar gasto
- Visualizar lista

### Filtros
- Por categoría
- Por fecha
- Reset de filtros

### Estadísticas
- Gráfico por categoría (PieChart)
- Gráfico mensual (BarChart)
- Selección de mes

### Análisis
- Comparación mes actual vs anterior
- Variación porcentual
- Identificación de aumento/disminución

---

## 4. Arquitectura

Patrón utilizado: **MVVM (Model-View-ViewModel)**

Flujo de datos:


UI (Compose)

↓

ViewModel (StateFlow)

↓

Repository

↓

Room Database


---

## 5. Base de datos

Tecnología: **Room (SQLite)**

Tablas:
- `User`
- `Transaction`

Consultas:
- SUM (totales)
- GROUP BY (categoría / mes)


---

## 6. Tecnologías utilizadas

- Kotlin
- Jetpack Compose
- Room
- Flow / StateFlow
- MPAndroidChart

---

## 7. Ejecución

Clonar el repositorio:

```bash
git clone https://github.com/SungminJeong/SpendTrack
```
Pasos:

Abrir el proyecto en Android Studio
Ejecutar en un emulador o dispositivo físico

No requiere conexión a internet.

## 8. Estructura del proyecto

```
data/
 ├── local/
 ├── model/
 └── repository/

ui/
 ├── screen/
 ├── components/
 └── navigation/

viewmodel/
```
---
## 9. Seguridad


Contraseñas encriptadas con SHA-256

Datos almacenados localmente

---
## 10. Estado del proyecto
Aplicación funcional completada

Posibles mejoras:

- Presupuesto por categoría
- Exportación CSV/PDF
- Notificaciones de límite
---
## 11. Autor

Trabajo de Fin de Grado (TFG)

---

## 12. Características clave

- Aplicación completamente offline
- Interfaz moderna con Compose
- Arquitectura escalable (MVVM)
- Análisis de gastos en tiempo real