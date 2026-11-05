# Epicentro Gourmet — TP Grupal OO2 Hito 2

Sistema de gestión de festivales gastronómicos hecho con Spring Boot, Thymeleaf y MySQL.

## Grupo 04


| Integrante | Usuario de GitHub |
|---|---|
| José Ignacio Aldonate | [ignacioAldonate1](https://github.com/ignacioAldonate1) |
| Maximiliano Omar Bruni | [Mabru01](https://github.com/Mabru01) |
| Rodrigo Gabriel Velazquez | [RodryVelazquez](https://github.com/RodryVelazquez) |
| Nicolás Alberto Zapata | [Zapatannicolas](https://github.com/Zapatannicolas) |

## Diagrama de clases

<img width="3563" height="1489" alt="Diagrama de Clase drawio" src="https://github.com/user-attachments/assets/d9b1fdc7-9c3b-45e5-b2c7-bd4237debbc7" />

## Cómo correr la aplicación

### Requisitos

- Java 21
- MySQL 8
- Maven: no hace falta instalarlo, el proyecto trae el wrapper `mvnw`.

### 1. Crear la base de datos

```sql
CREATE DATABASE festival;
```

### 2. Variables de entorno

La aplicación lee la conexión a la base y la cuenta de mail de variables de entorno. No hay contraseñas en el código.

| Variable | Ejemplo | Para qué |
|---|---|---|
| `DB_URL` | `jdbc:mysql://localhost:3306/festival` | Conexión a MySQL |
| `USERNAME` | `root` | Usuario de MySQL |
| `PASSWORD` | *(tu contraseña de MySQL)* | Contraseña de MySQL |
| `MAIL_USERNAME` | `cuenta@gmail.com` | Cuenta de Gmail que envía los mails |
| `MAIL_PASSWORD` | *(contraseña de aplicación de Gmail)* | Contraseña de aplicación de esa cuenta |

> **Windows:** `USERNAME` ya existe en Windows y contiene el usuario del sistema. Hay que definirla con el usuario de MySQL en la configuración de ejecución (en IntelliJ: *Run → Edit Configurations → Environment variables*).
>
> Las variables de mail son opcionales. Si no se cargan, la aplicación funciona igual, pero no se envía el mail con las credenciales al dar de alta un empleado.

### 3. Arrancar la aplicación una vez

```bash
./mvnw spring-boot:run
```

En Windows es `mvnw.cmd spring-boot:run`, o se puede ejecutar `TPGrupalOO2Hito2Grupo04Application` desde IntelliJ. En este primer arranque Hibernate crea las tablas.

### 4. Cargar los datos de prueba

Con las tablas ya creadas, ejecutar [`Carga_De_Datos.sql`](Carga_De_Datos.sql) sobre la base `festival`, por ejemplo desde MySQL Workbench. Después volver a arrancar la aplicación y entrar a http://localhost:8080.

### Usuarios de prueba

Todos los usuarios tienen la contraseña **`1234`**.

| Rol | Usuario | Detalle |
|---|---|---|
| Administrador | `jperez@gmail.com` | Ve todos los festivales y los reportes |
| Responsable | `cdiaz@gmail.com` | Unidad *Pollos*, Festival Uno (en curso) |
| Responsable | `mlopez@gmail.com` | Unidad del Festival Dos (próximo) |
| Empleado (cocinero) | `tvazquez@gmail.com` | Cocinero de *Pollos* |
| Empleado (cajero) | `pvance@gmail.com` | Cajero de *Refrigerios* |

Los datos incluyen tres festivales: uno **en curso**, uno **próximo** y uno **finalizado**.

## Casos de uso implementados y responsables

### José Ignacio Aldonate

| Caso de uso | Enlace |
|---|---|
| Clase `UnidadVenta` (FoodTruck y PuestoDesarmable) | [PR #1](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/1) |
| Estructura del proyecto y configuración para compilar y crear la base | [commit ccad93a](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/commit/ccad93a) · [commit 26bdb7a](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/commit/26bdb7a) |
| Alta de unidad de venta con su responsable | [PR #7](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/7) |
| Registro de unidades desde el inicio, sin sesión y solo en el festival activo | [PR #12](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/12) |
| Cálculo de canon | [PR #19](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/19) |
| Separación de vistas por rol y "Mi unidad" del responsable | [PR #20](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/20) |
| Modificación y baja de unidad de venta | [PR #24](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/24) |
| Reporte de unidades con mayor canon (`ReporteMayoresCanonDTO`) | [PR #26](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/26) |
| Ranking de unidades por recaudación | [PR #29](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/29) |

### Maximiliano Omar Bruni

| Caso de uso | Enlace |
|---|---|
| Clases `Staff`, `Cajero` y `Cocinero` | [PR #2](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/2) |
| Configuración de seguridad y login | [commit 84b11da](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/commit/84b11da) |
| Script de carga de datos y diagrama de clases | [commit 903a88a](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/commit/903a88a) · [commit d3bcda7](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/commit/d3bcda7) |
| ABM de empleados con baja lógica | [rama CUEmpleadosABMMaximilianoBruni](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/tree/CUEmpleadosABMMaximilianoBruni) · [merge 6f94237](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/commit/6f94237) |
| Envío de mail con usuario y contraseña al dar de alta un empleado | [rama CUEnvioMailsMaximilianoBruni](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/tree/CUEnvioMailsMaximilianoBruni) |
| Validaciones en los formularios de empleados | [commit ee33a7a](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/commit/ee33a7a) |
| Reactivar empleado dado de baja | [rama CUReactivarEmpleadoMaximilianoBruni](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/tree/CUReactivarEmpleadoMaximilianoBruni) |
| Perfil del empleado: sus datos, unidad y festival | [rama CUEmpleadoVerPerfilMaximilianoBruni](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/tree/CUEmpleadoVerPerfilMaximilianoBruni) |
| Cambiar contraseña del empleado | [rama CUEmpleadoCambiarContraseniaMaximilianoBruni](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/tree/CUEmpleadoCambiarContraseniaMaximilianoBruni) |
| Calendario de festivales | [rama CUCalendarioMaximilianoBruni](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/tree/CUCalendarioMaximilianoBruni) |
| Cierre de caja del cajero | [rama CUCierreCajaMaximilianoBruni](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/tree/CUCierreCajaMaximilianoBruni) |

### Rodrigo Gabriel Velazquez

| Caso de uso | Enlace |
|---|---|
| Clases `Pedido`, `ItemPedido` y `Plato` con sus DTO | [PR #4](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/4) · [PR #5](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/5) |
| Repositorios y servicios de pedidos y platos, y alta de platos | [PR #10](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/10) |
| ABM de platos (ordenados por unidad de venta, con validaciones) | [rama CUPlatoABMRodrigoVelazquez](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/tree/CUPlatoABMRodrigoVelazquez) · [merge d8db067](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/commit/d8db067) |
| ABM de pedidos | [rama CUPedidosABMRodrigoVelazquez](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/tree/CUPedidosABMRodrigoVelazquez) · [merge b2056ff](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/commit/b2056ff) |
| Plato estrella (`PlatoDTO` con más pedidos) | [rama CUPlatoEstrellaRodrigoVelazquez](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/tree/CUPlatoEstrellaRodrigoVelazquez) · [merge 2187535](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/commit/2187535) |
| Vistas de platos y pedidos según el rol (admin, responsable y empleado) | [commit ad1d9d8](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/commit/ad1d9d8) · [commit 46b88f4](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/commit/46b88f4) |
| Cambio de estado de pedido | [merge 084f3c2](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/commit/084f3c2) |
| Correcciones de pedidos (eliminación y errores) | [commit 6d9852a](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/commit/6d9852a) · [commit d834145](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/commit/d834145) |
| Baja lógica de platos y pedidos | [rama CUBajaLogicaPlato](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/tree/CUBajaLogicaPlato) · [merge 96e05b5](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/commit/96e05b5) |

### Nicolás Alberto Zapata

| Caso de uso | Enlace |
|---|---|
| Clase `Festival` | [PR #3](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/3) |
| Liquidación de sueldo según rol y duración del festival | [PR #6](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/6) |
| Página de inicio con los festivales | [PR #8](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/8) |
| Correcciones de develop: registro, navbar y script de carga | [PR #9](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/9) |
| Mejoras de login, logout e inicio según la sesión | [PR #13](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/13) |
| Recibo de sueldo en PDF | [PR #15](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/15) |
| Correcciones: platos y pedidos por unidad, variables de entorno para la base y el mail | [PR #23](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/23) |
| Reporte de recaudación de la unidad (`ReporteVentaDTO`) | [PR #27](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/27) |
| Mejoras de sesión: rol en el navbar, el responsable ve su festival, inicio después del login | [PR #28](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/28) |
| Alta, modificación y detalle de festival (administrador) | [PR #30](https://github.com/Mabru01/TPGrupalOO2Hito2Grupo04/pull/30) |

## Funcionalidades de la consigna

| Rol | Funcionalidad | Responsable |
|---|---|---|
| Inicio | Ver festivales | Nicolás |
| Inicio | Registrar unidad de venta con responsable | Ignacio |
| Inicio | Login de administrador, responsable y empleado | Maximiliano |
| Admin | Ver festivales y unidades de un festival | Nicolás · Ignacio |
| Admin | Alta, modificación y detalle de festival | Nicolás |
| Admin | Ranking de unidades por recaudación | Ignacio |
| Admin | Unidades con mayor canon | Ignacio |
| Responsable | ABM de empleados con mail y baja lógica | Maximiliano |
| Responsable | Cálculo de canon | Ignacio |
| Responsable | Liquidación de haberes | Nicolás |
| Responsable | Reporte de recaudación | Nicolás |
| Responsable | Plato estrella | Rodrigo |
| Responsable | ABM de platos y pedidos | Rodrigo |
| Responsable / Empleado | Vistas de platos y pedidos según el rol | Rodrigo |
| Empleado | Ver su información, festival y unidad actual | Maximiliano |
| Empleado | Sueldo y recibo en PDF | Nicolás |
| Empleado | Cerrar caja (cajero) | Maximiliano |
