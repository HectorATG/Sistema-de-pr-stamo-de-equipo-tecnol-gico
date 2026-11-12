# Sistema de préstamo de equipo tecnológico

Prototipo funcional mínimo de la **Evidencia 6 · Caso 1** (Arquitecturas de Software, Unidad II).
Cubre el flujo obligatorio: consultar disponibilidad → solicitar → validar → autorizar → actualizar estado → consultar préstamo.

| Carpeta | Contenido |
| --- | --- |
| `backend/` | API REST con Spring Boot 3 (Web, Data JPA, Security, Validation) y H2 en memoria |
| `frontend/` | Aplicación Angular 19 |
| `postman/` | Colección de Postman con el flujo, seguridad y validaciones |
| `docs/` | Ejercicio 1: análisis, arquitectura, diagramas UML y matriz inicial |

## Requisitos

- Java 17 o superior y Maven 3.9 o superior
- Node.js 18.19 o superior (probado con Node 22) y npm

## Cómo ejecutarlo

Abrir dos terminales.

**1. Backend (puerto 8080)**

```bash
cd backend
mvn spring-boot:run
```

Está listo cuando la consola muestra `Started PrestamosApplication`.

**2. Frontend (puerto 4200)**

```bash
cd frontend
npm install
npm start
```

Abrir <http://localhost:4200>.

## Usuarios de prueba

Se cargan al arrancar el backend (`DatosIniciales.java`), junto con seis equipos.

| Usuario | Contraseña | Rol |
| --- | --- | --- |
| `ana` | `ana123` | SOLICITANTE |
| `luis` | `luis123` | SOLICITANTE |
| `rosa` | `rosa123` | RESPONSABLE |

## Demostración del flujo en la aplicación

1. Entrar como `ana`: se muestran los **equipos disponibles** (paso 1).
2. Pulsar **Solicitar** en un equipo, elegir fechas y enviar (paso 2). El préstamo queda SOLICITADO.
3. Salir, entrar como `luis` y solicitar **el mismo equipo**.
4. Salir y entrar como `rosa`: aparecen las dos solicitudes.
5. **Autorizar** la de Ana (pasos 3 y 4): pasa a AUTORIZADO.
6. Intentar **Autorizar** la de Luis: el sistema lo impide con un mensaje (regla RN1, respuesta 409).
7. **Registrar entrega** (paso 5): el préstamo pasa a ENTREGADO y el equipo a PRESTADO; ya no aparece en equipos disponibles.
8. **Registrar devolución**: el préstamo pasa a DEVUELTO y el equipo vuelve a DISPONIBLE.
9. Entrar como `ana` y abrir **Mis préstamos** (paso 6): se ve el historial con fechas y estado.

## Pruebas con Postman

1. Importar `postman/Evidencia6_Prestamos.postman_collection.json`.
2. Reiniciar el backend (así los identificadores empiezan en 1).
3. Abrir **Run collection** en Postman y ejecutar una iteración con todas las peticiones en el orden original.
4. Revisar el resumen de pruebas y guardar una captura para la matriz de correspondencia.

Las pruebas verifican los códigos HTTP y los estados del flujo de préstamo. Los casos de seguridad y validación pasan cuando reciben el código de rechazo esperado. Las credenciales de prueba vienen configuradas en *Authorization*.

## Endpoints

| Método | Ruta | Rol | Qué hace |
| --- | --- | --- | --- |
| GET | `/api/auth/me` | Ambos | Usuario autenticado y su rol |
| GET | `/api/equipos?estado=DISPONIBLE` | Ambos | Equipos disponibles |
| POST | `/api/prestamos` | Solicitante | Crea la solicitud (201) |
| PUT | `/api/prestamos/{id}/autorizar` | Responsable | Valida disponibilidad y autoriza (409 si no se puede) |
| PUT | `/api/prestamos/{id}/rechazar` | Responsable | Rechaza la solicitud |
| PUT | `/api/prestamos/{id}/entregar` | Responsable | Registra la entrega; equipo a PRESTADO |
| PUT | `/api/prestamos/{id}/devolver` | Responsable | Registra la devolución; equipo a DISPONIBLE |
| GET | `/api/prestamos` | Responsable | Todos los préstamos (historial) |
| GET | `/api/prestamos/mios` | Solicitante | Préstamos del usuario |
| GET | `/api/prestamos/{id}` | Ambos | Un préstamo |

Códigos de error: 400 datos inválidos, 401 sin credenciales, 403 rol sin permiso, 404 no existe, 409 regla de negocio.

## Consola de H2

<http://localhost:8080/h2-console> — JDBC URL `jdbc:h2:mem:prestamosdb`, usuario `sa`, sin contraseña.
Sirve como evidencia de las tablas `USUARIO`, `EQUIPO` y `PRESTAMO`.

## Dónde está cada elemento del modelo

| Elemento modelado | Diagrama | Archivo |
| --- | --- | --- |
| `PrestamoController` | Componentes | `backend/.../controller/PrestamoController.java` |
| `PrestamoService` y «validar disponibilidad» | Componentes, secuencia | `backend/.../service/PrestamoService.java` (`autorizar`, `validarDisponibilidad`) |
| `Prestamo`, `EstadoPrestamo` | Clases | `backend/.../model/Prestamo.java`, `EstadoPrestamo.java` |
| `Equipo` y cambio de estado | Clases, secuencia | `backend/.../model/Equipo.java`; `PrestamoService.entregar` y `devolver` |
| Seguridad | Componentes | `backend/.../security/SecurityConfig.java` |
| Paquete `repository` | Paquetes | `backend/.../repository/` |
| Aplicación Angular | Despliegue | `frontend/src/app/components/` y `services/api.service.ts` |
| Servidor con H2 | Despliegue | `backend/src/main/resources/application.properties` |

## Commits sugeridos

Subir el proyecto por etapas para que GitHub muestre la evolución:

1. `feat: entidades y repositorios` — `backend/pom.xml`, `model/`, `repository/`, `PrestamosApplication.java`, `application.properties`
2. `feat: consulta de equipos disponibles` — `DatosIniciales.java`, `EquipoService.java`, `EquipoController.java`
3. `feat: reglas de préstamo` — `PrestamoService.java`, `dto/`, `exception/`
4. `feat: endpoints de préstamos` — `PrestamoController.java`
5. `feat: seguridad por rol` — `security/`, `AuthController.java`
6. `test: colección postman` — `postman/`
7. `feat: frontend angular` — `frontend/`
8. `docs: readme y ejercicio 1` — `README.md`, `docs/`
