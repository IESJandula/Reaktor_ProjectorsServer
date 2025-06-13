<div align="center">
  <table border="1" cellpadding="10" cellspacing="0">
    <tr>
      <td colspan="9" align="center"><strong>📚 Navegación del Proyecto</strong></td>
    </tr>
    <tr>
      <td align="center"><a href="./README.md">🏠<br><strong>Inicio</strong></a></td>
      <td align="center"><a href="./acerca-del-proyecto.md">ℹ️<br><strong>Acerca de</strong></a></td>
      <td align="center"><a href="./arquitectura-y-flujo.md">🛠️<br><strong>Arquitectura</strong></a></td>
      <td align="center"><a href="./esquema-y-tablas.md">🛢️<br><strong>Esquema BBDD</strong></a></td>
      <td align="center"><a href="./interfaz-grafica-y-roles.md">🎨<br><strong>Interfaz</strong></a></td>
      <td align="center"><a href="./api-rest.md">📡<br><strong>API REST</strong></a></td>
      <td align="center"><a href="./hardware-especial.md">🧰<br><strong>Hardware</strong></a></td>
      <td align="center"><a href="./codificacion.md">📟<br><strong>Codificación</strong></a></td>
      <td align="center"><a href="./instrucciones-de-uso.md">📄<br><strong>Instrucciones</strong></a></td>
    </tr>
  </table>

</div>


<a name="api-rest"></a>   
# 📡 Documentación API REST - Gestión Remota de Proyectores
Esta sección describe los endpoints disponibles del servidor backend desarrollado en Spring Boot para la gestión remota de proyectores.
   
---
   
<a name="ProjectorRemoteAgentController"></a>   
## 1. ProjectorRemoteAgentController
Endpoints exclusivos para los agentes remotos (ESP32).
   
### 🔵 `GET /projectors/server-events`
- **Descripción**: Devuelve la acción a ejecutar por un proyector.
- **Parámetros requeridos**:
  - `aula` (string): Identificador del aula.
  - `estado` (string): Estado del proyector en el momento de realizar la petición.
- **Acceso**: Cliente Proyector
- **Respuestas**:
  - `200 OK`: Acción encontrada
  - `204 No Content`: Sin acción
  - `404 Not Found`: Proyector no registrado
  - `500 Internal Server Error`: Error interno
   
---
   
### 🟡 `PUT /projectors/server-events`
- **Descripción**: Actualiza el estado de un evento.
- **Parámetros requeridos**:
  - `eventId` (string): Id del evento a actualizar.
  - `classroom` (string): Aula en la que se encuentra el proyector.
  - `rarc` (string): Remote Agent Response Code - Codigo que el proyector proporciona al Agente Remoto.
- **Acceso**: Cliente Proyector
- **Respuestas**:
  - `200 OK`: Éxito
  - `404 Not Found`: Evento no encontrado
  - `500 Internal Server Error`: Error interno
 
### 🔵 `GET /projectors/config-params`
- **Descripción**: Permite recuperar parametros de configuración en modo dinamico y adaptable en futuro a las necesidades del agente remoto o proyector.
- **Parámetros requeridos**:
  - `projectorClassroom` (string): Aula en la que se encuentra el proyector.
- **Acceso**: Cliente Proyector
- **Respuestas**:
  - `200 OK`: Éxito
  - `404 Not Found`: Evento no encontrado
  - `500 Internal Server Error`: Error interno

---

<a name="ProjectorCommonsController"></a>   
## 2. ProjectorCommonsController

Endpoints compartidos por administradores y usuarios.

### 🟢 `POST /projectors/server-events`
- **Descripción**: Obtiene eventos del servidor filtrados.
- **Cuerpo**: `EventFilterObject`
- **Respuestas**: `200 OK`

#### 🔸 EventFilterObject

Objeto de transferencia de datos (DTO) utilizado para filtrar eventos del servidor. Representa el conjunto de criterios por los cuales los eventos pueden ser ordenados o filtrados en la página.

| Campo         | Tipo           | Descripción                             |
|---------------|----------------|---------------------------------------|
| eventId       | Long           | ID único del evento (por ejemplo, "1"). |
| actionName    | String         | Acción que debe realizar el evento.    |
| modelName     | String         | Modelo del proyector involucrado.      |
| classroomName | String         | Aula donde ocurre el evento.            |
| floorName     | String         | Piso donde ocurre el evento.            |
| user          | String         | Usuario que originó el evento.          |
| dateTime      | LocalDateTime  | Fecha y hora de creación del evento.   |
| actionStatus  | String         | Estado del evento.                      |

---

### 🟢 `POST /projectors/server-events-batch`
- **Descripción**: Crea múltiples eventos.
- **Cuerpo**: `ServerEventBatchDto`
- **Respuestas**:
  - `201 Created`
  - `400 Bad Request`
  - `500 Internal Server Error`

#### 🔸 ServerEventBatchDto

Objeto de transferencia de datos (DTO) utilizado para crear un evento en el servidor para una acción sobre una lista dada de proyectores. Simplifica la solicitud y el proceso de creación del evento en el servidor.

| Campo         | Tipo                | Descripción                                           |
|---------------|---------------------|-------------------------------------------------------|
| action        | String              | Acción que se realizará. Se refiere a la instrucción específica para cada proyector. |
| projectorList | List<ProjectorDto>   | Lista de proyectores a los que se enviará la acción. |

---

### 🔵 `GET /projectors/projector-models`
- **Descripción**: Lista todos los modelos de proyectores.
- **Respuestas**:
  - `200 OK`
  - `500 Internal Server Error`

---

### 🔵 `GET /projectors/floors`
- **Descripción**: Lista todas las plantas.
- **Respuestas**:
  - `200 OK`
  - `204 No Content`
  - `500 Internal Server Error`

---

### 🔵 `GET /projectors/event-states`
- **Descripción**: Devuelve todos los estados posibles de un evento.
- **Respuestas**: `200 OK`

---

### 🔵 `GET /projectors/classrooms`
- **Descripción**: Lista aulas de una planta específica.
- **Parámetros**: `floor` (string)
- **Respuestas**:
  - `200 OK`
  - `204 No Content`
  - `500 Internal Server Error`

---

### 🔵 `GET /projectors/actions`
- **Descripción**: Lista las acciones disponibles.
- **Respuestas**:
  - `200 OK`
  - `500 Internal Server Error`

---

<a name="ProjectorAdminController"></a>   
## 3. ProjectorAdminController

Endpoints exclusivos para el rol administrador.

### 🟢 `POST /projectors/parse-multifile`
- **Descripción**: Carga archivos CSV con comandos y proyectores.// Llama la activity.
startActivity(intent)
- **Parámetros**: `projectorsFile`, `commandsFile` (multipart/form-data)
- **Respuestas**:
  - `200 OK`
  - `500 Internal Server Error`

---

### 🟢 `POST /projectors/commands-page`
- **Descripción**: Lista comandos con filtros opcionales.
- **Parámetros**:
  - `modelName` (opcional)
  - `action` (opcional)
- **Respuestas**:
  - `200 OK`
  - `400 Bad Request`
  - `500 Internal Server Error`

---

### 🟢 `POST /projectors/actions-page`
- **Descripción**: Devuelve acciones paginadas.
- **Respuestas**:
  - `200 OK`
  - `500 Internal Server Error`

---

### 🔵 `GET /projectors/projectors`
- **Descripción**: Lista proyectores paginados y filtrados.
- **Parámetros**:
  - `criteria`, `classroom`, `floor`, `model` (opcional)
- **Respuestas**:
  - `200 OK`
  - `500 Internal Server Error`

---

### 🔴 `DELETE /projectors/projectors`
- **Descripción**: Elimina proyectores seleccionados.
- **Cuerpo**: Lista de `ProjectorInfoDto`
- **Respuestas**:
  - `200 OK`
  - `500 Internal Server Error`

#### 🔸ProjectorInfoDto

Objeto de transferencia de datos (DTO) que representa información de un proyector. Proporciona una representación simplificada de la entidad proyector y sus detalles asociados. Usado para devolver resultados paginados.

| Campo     | Tipo   | Descripción                              |
|-----------|--------|------------------------------------------|
| model     | String | Modelo del proyector.                    |
| classroom | String | Aula donde se encuentra el proyector.   |
| floorname | String | Nombre del piso donde está ubicado el proyector. |

---

### 🔵 `GET /projectors/general-overview`
- **Descripción**: Devuelve conteo general registros del sistema.
- **Respuestas**:
  - `200 OK`
  - `500 Internal Server Error`

---

### 🔵 `GET /projectors/events-overview`
- **Descripción**: Devuelve estadísticas generales de eventos.
- **Respuestas**:
  - `200 OK`
  - `500 Internal Server Error`

---

### 🔴 `DELETE /projectors/actions`
- **Descripción**: Elimina acciones en lote.
- **Cuerpo**: Lista de `ActionDto`
- **Respuestas**:
  - `200 OK`
  - `400 Bad Request`
  - `500 Internal Server Error`

#### 🔸 ActionDto

Objeto de transferencia de datos (DTO) que representa una acción. Proporciona una representación simplificada de la entidad Action para su uso en el frontend.

| Campo      | Tipo   | Descripción                  |
|------------|--------|------------------------------|
| actionName | String | Nombre de la acción.          |

---

### 🔴 `DELETE /projectors/projectors-all`
- **Descripción**: Elimina todos los proyectores registrados.
- **Respuestas**:
  - `200 OK`
  - `500 Internal Server Error`

---

### 🔴 `DELETE /projectors/commands`
- **Descripción**: Elimina comandos específicos junto a sus eventos.
- **Cuerpo**: Lista de `CommandDto`
- **Respuestas**:
  - `200 OK`
  - `400 Bad Request`
  - `500 Internal Server Error`

#### 🔸 CommandDto

Objeto de transferencia de datos (DTO) que representa un comando. Proporciona una representación simplificada de la entidad comando para su uso en el frontend.

| Campo      | Tipo   | Descripción                                               |
|------------|--------|-----------------------------------------------------------|
| modelName  | String | Nombre del modelo del proyector al que pertenece el comando. |
| action     | String | Acción que realizará el comando.                           |
| command    | String | Instrucción literal, como un array de bytes o secuencia binaria. |

    
--- 
   
<div align="center">
  <table border="1" cellpadding="10" cellspacing="0">
    <tr>
      <td colspan="9" align="center"><strong>📚 Navegación del Proyecto</strong></td>
    </tr>
    <tr>
      <td align="center"><a href="./README.md">🏠<br><strong>Inicio</strong></a></td>
      <td align="center"><a href="./acerca-del-proyecto.md">ℹ️<br><strong>Acerca de</strong></a></td>
      <td align="center"><a href="./arquitectura-y-flujo.md">🛠️<br><strong>Arquitectura</strong></a></td>
      <td align="center"><a href="./esquema-y-tablas.md">🛢️<br><strong>Esquema BBDD</strong></a></td>
      <td align="center"><a href="./interfaz-grafica-y-roles.md">🎨<br><strong>Interfaz</strong></a></td>
      <td align="center"><a href="./api-rest.md">📡<br><strong>API REST</strong></a></td>
      <td align="center"><a href="./hardware-especial.md">🧰<br><strong>Hardware</strong></a></td>
      <td align="center"><a href="./codificacion.md">📟<br><strong>Codificación</strong></a></td>
      <td align="center"><a href="./instrucciones-de-uso.md">📄<br><strong>Instrucciones</strong></a></td>
    </tr>
  </table>

</div>

