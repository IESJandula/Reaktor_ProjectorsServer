<div align="center">
  <table border="1" cellpadding="10" cellspacing="0">
    <tr>
      <td colspan="9" align="center"><strong>📚 Navegación del Proyecto</strong></td>
    </tr>
    <tr>
      <td align="center"><a href="../../../../README.md">🏠<br><strong>Inicio</strong></a></td>
      <td align="center"><a href="./acerca-del-proyecto.md">ℹ️<br><strong>Acerca de</strong></a></td>
      <td align="center"><a href="./arquitectura-y-flujo.md">🛠️<br><strong>Arquitectura</strong></a></td>
      <!-- <td align="center"><a href="./esquema-y-tablas.md">🛢️<br><strong>Esquema BBDD</strong></a></td> -->
      <td align="center"><a href="./interfaz-grafica-y-roles.md">🎨<br><strong>Interfaz</strong></a></td>
      <td align="center"><a href="./api-rest.md">📡<br><strong>API REST</strong></a></td>
      <td align="center"><a href="./hardware-especial.md">🧰<br><strong>Hardware</strong></a></td>
      <td align="center"><a href="./codificacion.md">📟<br><strong>Codificación</strong></a></td>
      <td align="center"><a href="./instrucciones-de-uso.md">📄<br><strong>Instrucciones</strong></a></td>
    </tr>
  </table>

</div>


---

<a name="esquema"></a>   
# 🛢️ Esquema de información de base de datos.
En esta sección se describe el sistema de información utilizado en el proyecto para la gestión y control remoto de proyectores en el centro, utilizando una arquitectura basada en eventos.

**Diseño inicial**.   
Aunque este diseño inicial no es el que finalmente se ha implementado, sirve para entender el funcionamiento general del sistema, el cual luego se simplificó en el modelo que se describe a continuación.

<p align="center">
  <img src="https://github.com/user-attachments/assets/afabca63-7e8b-44a2-9f1a-52c682dd4ab7" alt="esquema">
</p>

**Diseño final simplificado**.    
Este esquema simplificado permite gestionar proyectores de manera eficiente, asegurando un control centralizado y un historial detallado de las acciones realizadas. 
    
<p align="center">
  <!-- img src="https://github.com/user-attachments/assets/8a23f8fd-33fc-410b-a67b-321d6bb740e7" alt="esquema" -->
   <img src="https://github.com/user-attachments/assets/fb395e7f-1bce-4257-94a9-b37ee9aa9ea6" alt="esquema">
</p>

<!-- ![image](https://github.com/user-attachments/assets/f0f5a0a9-de9e-4274-9b68-e391594e61f7)  -->

<!-- ![imagen](https://github.com/user-attachments/assets/523b70d0-0418-48b6-a1b4-c2621059d7ef) -->

<p align="center">
   <img src="https://github.com/user-attachments/assets/d1f32038-b1ad-44b7-b6d2-f8507ed0724b" alt="esquema">
</p>


---
<a name="tablas"></a> 
## :card_index: **Tabla `Projector`**  
<img width=240px src="https://github.com/user-attachments/assets/0e458134-ac0c-4830-b1ef-1e426030550e" alt="proyectores" align="right">
    
Representa una unidad física de proyector dentro del centro educativo.  

### **Campos:**  
- `classroom` (**PK**) → Identificador único del aula donde se encuentra el proyector.  
- `floor` → Planta en la que se encuentra el aula.  
- `model` → Modelo del proyector instalado.
- `projector` → Representa el estado de "Encendido" o "Apagado" del proyector.

### **Relaciones:**  
- Relación **uno a muchos** con `ServerEvent`, permitiendo registrar eventos asociados a cada proyector.  

🗒️ **Descripción:**  
Cada aula tiene un solo proyector, identificado de manera única por el aula donde está instalado.  

---

## :card_index: **Tabla `Command`**  
<img width=240px src="https://github.com/user-attachments/assets/40cea64f-8e3f-467f-97e3-e24458399989" alt="comandos" align="right">

Almacena las instrucciones necesarias para interactuar con los proyectores.  

### **Campos:**  
- `modelName` (**PK**) → Modelo del proyector al que aplica el comando.  
- `action` (**PK**) → Acción que se desea ejecutar (encender, apagar, cambiar fuente, etc.).  
- `command` → Código de la instrucción que debe enviarse al dispositivo.  

### **Relaciones:**  
- Relación **uno a muchos** con `ServerEvent`, permitiendo registrar eventos asociados a cada comando ejecutado.  

🗒️ **Descripción:**  
Permite mapear cada modelo de proyector con sus respectivas acciones y las instrucciones necesarias para ejecutarlas.  

---

## :card_index: **Tabla `ServerEvent`**  
<img width=240px src="https://github.com/user-attachments/assets/a787179b-b319-43ba-8ec6-31c13243227b" alt="eventos" align="right">


Registra las solicitudes de los usuarios para ejecutar comandos sobre los proyectores. 

### **Campos:**  
- `eventId` (**PK**) → Identificador único del evento.  
- `command` (**FK**) → Comando asociado a la acción solicitada.  
- `projector` (**FK**) → Proyector sobre el cual se ejecuta la acción.  
- `user` → Usuario autor que ha realizado la solicitud.  
- `dateTime` → Fecha y hora en que se registró la solicitud.  
- `actionStatus` → Estado de la solicitud ([ver sección estados](#clipboard-posibles-estados-de-los-eventos)).  

### **Relaciones:**  
- Relación **muchos a uno** con `Command` (cada evento está asociado con un comando específico).  
- Relación **muchos a uno** con `Projector` (cada evento está asociado a un proyector específico).  

🗒️ **Descripción:**  
Esta tabla actúa como un 'buffer' de eventos. Es una tabla intermedia que se ve afectada por el borrado de registros de las tablas **`Command`** y **`Actions`**. Su objetivo es formar el registro que luego viene copiado en la tabla **`Server Event History`**.  

**Posibles estados de los eventos**  
Los eventos que el servidor gestiona pueden darse en 5 posibles estados distintos.  
- **PENDING**: La acción ha sido creada, pero no enviada.  
- **SERVED**: La acción ha sido enviada al proyector.  
- **EXECUTED**: La acción se ejecutó correctamente.  
- **CANCELED**: La solicitud fue cancelada antes de ejecutarse.  
- **ERROR**: Ocurrió un error en la ejecución de la acción.  

---

## :card_index: **Tabla `ServerEventHistory`**  
<img width=240px src="https://github.com/user-attachments/assets/4ee8c396-4d07-48e3-aff4-f6d067a64e2d" alt="historial" align="right">


Almacena las ordenes que son servidas a los agentes remotos. Es la tabla que contiene los registros que realmente cuentan.

### **Campos:**  
- `eventId` (**PK**) → Identificador único del evento.
- `accion` → Nombre de la acción a realizar.
- `actionStatus` → Estado de la solicitud ([ver sección estados](#clipboard-posibles-estados-de-los-eventos)).
- `classroom` → Aula en la que se encuentra el proyector.
- `command` → Comando asociado a la acción solicitada.
- `dateTime` → Fecha y hora en que se registró la solicitud.
- `floor` → Planta donde se encuentra el aula.
- `model_name` → Nombre del modelo del proyector. 
- `user` → Usuario autor que ha realizado la solicitud.  

🗒️ **Descripción:**  
Esta tabla actúa como una cola de eventos para gestionar la ejecución de acciones sobre los proyectores. Es la tabla desde la cual se recuperan las tareas de los microcontroladores y donde vendrán realmente actualizados. Es la tabla cuyos registros son visibles a los usuarios desde el front web. Su objetivo es almacenar, recuperar, mostrar y actualizar los registros que vienen servidos a los agentes remotos.

---

<a name="relaciones-sistema"></a>   
## :left_right_arrow: **Relaciones del Sistema**  
- `Projector` se relaciona con `ServerEvent` a través del aula (`classroom`).  
- `Command` se relaciona con `ServerEvent` mediante `modelName` y `action`.  
- `ServerEvent` gestiona las interacciones entre `Projector` y `Command`.  

>[!IMPORTANT]
> Aunque la tabla `ServerEventHistory` no tiene una relación directa con `ServerEvent`, su contenido depende completamente de esta última a nivel programático. Cada vez que se inserta un nuevo registro en `ServerEvent`, el servidor crea automáticamente una copia idéntica en `ServerEventHistory`.
>Sin embargo, si un registro de `ServerEvent` es eliminado, su correspondiente copia en `ServerEventHistory` permanece intacta. Esta estrategia permite conservar un historial de eventos registrado, independientemente de eliminaciones en cascada que puedan producirse desde las tablas `Command` o `Projector` hacia `ServerEvent`.
      
---


<div align="center">
  <table border="1" cellpadding="10" cellspacing="0">
    <tr>
      <td colspan="9" align="center"><strong>📚 Navegación del Proyecto</strong></td>
    </tr>
    <tr>
      <td align="center"><a href="../../../../README.md">🏠<br><strong>Inicio</strong></a></td>
      <td align="center"><a href="./acerca-del-proyecto.md">ℹ️<br><strong>Acerca de</strong></a></td>
      <td align="center"><a href="./arquitectura-y-flujo.md">🛠️<br><strong>Arquitectura</strong></a></td>
      <!-- <td align="center"><a href="./esquema-y-tablas.md">🛢️<br><strong>Esquema BBDD</strong></a></td> -->
      <td align="center"><a href="./interfaz-grafica-y-roles.md">🎨<br><strong>Interfaz</strong></a></td>
      <td align="center"><a href="./api-rest.md">📡<br><strong>API REST</strong></a></td>
      <td align="center"><a href="./hardware-especial.md">🧰<br><strong>Hardware</strong></a></td>
      <td align="center"><a href="./codificacion.md">📟<br><strong>Codificación</strong></a></td>
      <td align="center"><a href="./instrucciones-de-uso.md">📄<br><strong>Instrucciones</strong></a></td>
    </tr>
  </table>

</div>
