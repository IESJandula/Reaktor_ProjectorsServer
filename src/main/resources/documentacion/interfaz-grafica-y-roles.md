<div align="center">
  <table border="1" cellpadding="10" cellspacing="0">
    <tr>
      <td colspan="9" align="center"><strong>📚 Navegación del Proyecto</strong></td>
    </tr>
    <tr>
      <td align="center"><a href="../../../../README.md">🏠<br><strong>Inicio</strong></a></td>
      <td align="center"><a href="./acerca-del-proyecto.md">ℹ️<br><strong>Acerca de</strong></a></td>
      <td align="center"><a href="./arquitectura-y-flujo.md">🛠️<br><strong>Arquitectura</strong></a></td>
      <td align="center"><a href="./esquema-y-tablas.md">🛢️<br><strong>Esquema BBDD</strong></a></td>
      <!-- <td align="center"><a href="./interfaz-grafica-y-roles.md">🎨<br><strong>Interfaz</strong></a></td> -->
      <td align="center"><a href="./api-rest.md">📡<br><strong>API REST</strong></a></td>
      <td align="center"><a href="./hardware-especial.md">🧰<br><strong>Hardware</strong></a></td>
      <td align="center"><a href="./codificacion.md">📟<br><strong>Codificación</strong></a></td>
      <td align="center"><a href="./instrucciones-de-uso.md">📄<br><strong>Instrucciones</strong></a></td>
    </tr>
  </table>

</div>

<a name="interfaz"></a>   
# 🎨 Interfaz Gráfica y Roles de Usuario
>[!NOTE]
>Aunque la interfaz gráfica es un proyecto independiente que queda fuera del alcance de este desarrollo y no está incluida en este repositorio, en esta sección se ofrecen algunos datos útiles sobre cómo el sistema está diseñado para integrarse con dicha interfaz.

La aplicación está pensada para ser utilizada por tres tipos de usuarios, cada uno con roles y permisos específicos:
    
<a name="administrador"></a>   
### 📋 Administrador.
El administrador tiene acceso a funcionalidades avanzadas para la gestión del sistema, como la carga de datos, la eliminación de registros y otras operaciones desde el panel de control.

<a name="usuario"></a>   
### 👱 Usuario
Los usuarios estándar disponen de las funcionalidades básicas de la aplicación, tales como el control remoto de los proyectores y la consulta del estado de los eventos.

<a name="agentes-remotos"></a>   
### 🛰️ Agentes remotos
Los agentes remotos (microcontroladores asociados a los proyectores) cuentan únicamente con permisos para consultar las tareas disponibles para su proyector asociado, recuperar parametros de configuración dinamicos y reportar al servidor el éxito o fallo de las instrucciones ejecutadas.

<a name="diseño-actual-de-la-interfaz"></a>   
## 🖌️ Diseño actual de la interfaz.
<table border="1" style="width: 100%; table-layout: fixed;">
  <thead>
    <tr>
      <th>DESCRIPCIÓN</th>
      <th>ACCESO</th>
      <th>IMAGEN</th>
    </tr>
  </thead>
  <tbody>
    <!-- Eventos -->
    <!-- Control Remoto -->
    <tr>
      <td style="padding: 10px; vertical-align: top;">
        <strong>Panel de control remoto & historial de eventos</strong><br>
      Esta vista permite al administrador y al usuario gestionar el envío de órdenes a los proyectores del centro. Se pueden filtrar por ubicación y modelo, seleccionar proyectores y enviar órdenes (con un intervalo mínimo de 20 segundos). Además, se muestra un historial de eventos con su estado actual, junto con herramientas de búsqueda y paginación para facilitar la navegación.
      </td>
      <td style="text-align: center; vertical-align: top;">Administrador<br/>Usuario</td>
      <td style="text-align: center;">
        <a href="https://github.com/user-attachments/assets/0bfa32ae-2771-4b4c-ac0d-2c54afe61c08" target="_blank">
          <img src="https://github.com/user-attachments/assets/0bfa32ae-2771-4b4c-ac0d-2c54afe61c08" alt="Control Remoto">
        </a>
      </td>
    </tr>    
    <!-- Administración -->
    <tr>
      <td style="padding: 10px; vertical-align: top;">
        <strong>Panel de administración</strong><br>
        En esta vista, el administrador podrá gestionar la carga de datos por ficheros CSV y eliminación de registros del servidor, así como acceder a una vista rápida del estado de los eventos y la cantidad total de registros activos.
      </td>
      <td style="text-align: center; vertical-align: top;">Administrador</td>
      <td style="text-align: center;">
        <a href="https://github.com/user-attachments/assets/6424556c-013c-4cb4-9eda-1474585af597" target="_blank">
          <img src="https://github.com/user-attachments/assets/6424556c-013c-4cb4-9eda-1474585af597" alt="Administración">
        </a>
      </td>
    </tr>
  </tbody>
</table>

>[!TIP]
>Pincha en la imagen para ampliarla.</p>
    
--- 

## 🎥 Estados de los proyectores.
En la aplicación, cada **proyector** puede encontrarse en uno de **cuatro estados posibles**. Dos de ellos reflejan el estado real del dispositivo, mientras que los otros dos son **pseudoestados**, utilizados para ofrecer una retroalimentación más precisa y en tiempo real al usuario durante la operación.   
Los estados son los siguientes:

- 🔴 **Apagado**: El proyector se encuentra apagado. Este estado se actualiza automáticamente a partir de la información proporcionada por los agentes remotos, que monitorean el estado real del dispositivo.

- 🟠 **Apagando**: El proyector está en proceso de apagado. Aunque la orden aún no se ha ejecutado por completo, para el usuario se indica que el dispositivo está en transición hacia el estado de apagado.

- 🟢 **Encendido**: El proyector se encuentra encendido. Esta información también es reportada por los agentes remotos que supervisan el estado operativo del dispositivo.

- 🟡 **Encendiendo**: El proyector está en proceso de encendido. Aunque aún no se ha completado el encendido, este estado indica al usuario que la acción está en curso.

<p align=center>
  <img src="https://github.com/user-attachments/assets/0baadd8f-c539-4130-b7a5-b6da42b770c9"/>
</p>
        
Las transiciones entre estos estados pueden ser provocadas por **acciones del usuario** o por **actualizaciones de estado enviadas por un agente remoto**.

---

## 🔄 Transiciones de Estado

### 🔵 Desde **Encendido**
- 🔻 Pasa a **Apagando** 🟠 si un **usuario solicita apagar** el proyector.
- 🔻 Pasa a **Apagado** 🔴 si un **agente remoto actualiza** el estado.
- 🚫 **No puede** pasar a **Encendiendo** 🟡 directamente.
    
<p align=center>
  <img src="https://github.com/user-attachments/assets/7683ddaf-2141-43bd-acfe-170dbe808993"/>
</p>


---

### 🔴 Desde **Apagado** 🔴
- 🔺 Pasa a **Encendiendo** 🔵   si un **usuario solicita encender** el proyector.
- 🔺 Pasa a **Encendido** 🟢 si un **agente remoto actualiza** el estado.
- 🚫 **No puede** pasar a **Apagando** 🟠 directamente.
   
<p align=center>
  <img src="https://github.com/user-attachments/assets/74abd5f8-5cd0-4f1d-b7af-282f37ac0de7"/>
</p>


---

### 🟠 Desde **Apagando** 🟠
- 🔺 Pasa a **Encendiendo** 🔵  si un **usuario solicita encender** el proyector.
- 🔁 Pasa a **Encendido** 🟢 si un **agente remoto actualiza** el estado.
- 🔻 Pasa a **Apagado** 🔴 si un **agente remoto actualiza** el estado.
    
<p align=center>
  <img src="https://github.com/user-attachments/assets/91c58d2c-3d02-40a8-ae8e-5455ea5faf7e"/>
</p>

---
   
### 🟡 Desde **Encendiendo**
- 🔻 Pasa a **Apagando** 🟠 si un **usuario solicita apagar** el proyector.
- 🔻 Pasa a **Apagado** 🔴 si un **agente remoto actualiza** el estado.
- 🔁 Pasa a **Encendido** 🟢 si un **agente remoto actualiza** el estado.

<p align=center>
  <img src="https://github.com/user-attachments/assets/23fefdf8-95c4-4db2-853f-9ddee160018b"/>
</p>

## Esquema de estados.
<p align=center>
  <img src="https://github.com/user-attachments/assets/419958dc-c25e-4b2f-a583-83906fbaedfe"/>
</p>
    
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
      <td align="center"><a href="./esquema-y-tablas.md">🛢️<br><strong>Esquema BBDD</strong></a></td>
      <!-- <td align="center"><a href="./interfaz-grafica-y-roles.md">🎨<br><strong>Interfaz</strong></a></td> -->
      <td align="center"><a href="./api-rest.md">📡<br><strong>API REST</strong></a></td>
      <td align="center"><a href="./hardware-especial.md">🧰<br><strong>Hardware</strong></a></td>
      <td align="center"><a href="./codificacion.md">📟<br><strong>Codificación</strong></a></td>
      <td align="center"><a href="./instrucciones-de-uso.md">📄<br><strong>Instrucciones</strong></a></td>
    </tr>
  </table>

</div>
