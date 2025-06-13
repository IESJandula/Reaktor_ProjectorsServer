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
        <a href="https://github.com/user-attachments/assets/8f888cc9-3110-4461-a947-c005eeef7761" target="_blank">
          <img src="https://github.com/user-attachments/assets/8f888cc9-3110-4461-a947-c005eeef7761" alt="Control Remoto">
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
        <a href="https://github.com/user-attachments/assets/ee8d4575-7c86-4abe-bd53-643ff9048670" target="_blank">
          <img src="https://github.com/user-attachments/assets/ee8d4575-7c86-4abe-bd53-643ff9048670" alt="Administración">
        </a>
      </td>
    </tr>
  </tbody>
</table>
   
>[!TIP]
>Pincha en la imagen para ampliarla.</p>
    
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
