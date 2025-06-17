<div align="center">
  <table border="1" cellpadding="10" cellspacing="0">
    <tr>
      <td colspan="9" align="center"><strong>📚 Navegación del Proyecto</strong></td>
    </tr>
    <tr>
      <td align="center"><a href="../../../../README.md">🏠<br><strong>Inicio</strong></a></td>
      <!-- <td align="center"><a href="./src/main/resources/documentacion/acerca-del-proyecto.md">ℹ️<br><strong>Acerca de</strong></a></td> -->
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



<a name="introduccion"></a>
# ℹ️ Más información sobre el proyecto
En el centro educativo IES Jándula, numerosas aulas, salas de conferencias, el auditorio y otros espacios habilitados están equipados con proyectores audiovisuales montados en el techo o cielo raso. Debido a la gran cantidad de estos dispositivos, supervisar su estado de funcionamiento de manera manual supone un desafío considerable y una gran inversión de tiempo para el profesorado y el personal administrativo.
   
Actualmente, el encendido y apagado de estos proyectores se realiza mediante mandos a distancia por radiofrecuencia o infrarrojos, según el modelo. Esto implica que, si un mando se extravía, el proyector quedaría inutilizable hasta que el centro apruebe la adquisición de un reemplazo para un mando compatible. Esta situación no solo limita la disponibilidad del equipo, sino que también dificulta la labor docente y, en consecuencia, puede afectar o impactar directamente la calidad de la enseñanza.

Además, cuando existe la duda de si un proyector ha quedado encendido, un docente debe recorrer las aulas para comprobarlo y apagarlo manualmente. Esto no solo supone una pérdida de tiempo para el profesorado y los administradores, sino que también puede derivar en un consumo innecesario de energía.

Para solucionar estos problemas, surge la necesidad de implementar un sistema de monitoreo y gestión remota de proyectores, que permita supervisar su estado en tiempo real y controlarlos de forma eficiente, optimizando así el uso de los recursos tecnológicos del centro.

<br/>
   
![454596296-f3ff5cdf-d5f6-40ef-8f32-79f4095c8fc1](https://github.com/user-attachments/assets/1b2b12c5-25fe-47c1-94ed-262035c8f825)



<br/>

---
    
<a name="descripción-del-proyecto"></a>   
# 📑 Descripción del proyecto.
Este proyecto tiene como objetivo desarrollar una solución para supervisar en tiempo real el estado de los proyectores y permitir su encendido y apagado de forma remota. Con esta implementación, se optimizará la gestión de estos dispositivos, se reducirá el consumo energético causado por olvidos y se facilitará el trabajo del personal autorizado.

## Componentes.
Para su desarrollo, el proyecto integra diversas tecnologías de hardware y software, entre las que destacan:
- **Servidor**: Implementado con Spring Boot, será el encargado de recibir y gestionar las solicitudes de los usuarios.
- **Microcontroladores ESP32**: Actuarán como agentes remotos, encargados de ejecutar las órdenes recuperadas desde el servidor.
- **Cliente web**: Desarrollado en Vue.js, servirá como interfaz para los usuarios autenticados y autorizados.

## Objetivos.
Los objetivos principales de este proyecto son:
**Centralizar la gestión de los proyectores:**
El proyecto proporcionará una plataforma central con acceso limitado desde la cual los usuarios podrán gestionar determinadas acciones para los proyectores, sin necesidad de estar en el mismo aula donde se encuentra el proyector.   
- **Proporcionar una visión global del estado de los proyectores asociados**, permitiendo un monitoreo eficiente. 
- **Facilitar la gestión remota mediante una interfaz intuitiva y fácil de usar**, que permita el envío de órdenes de encendido y apagado de manera rápida y sencilla.
- **Mejorar la eficiencia operativa del personal docente y administrativo**, eliminando la necesidad de revisar manualmente cada proyector.
- **Sentar las bases** para futuras mejoras y la posible expansión de funcionalidades según estas se presenten.

## Funcionamiento general.

<img src="https://github.com/user-attachments/assets/84572c1c-3dee-4434-871e-6e8360f8310b" alt="usuario" width="80px" align="right">

**1. Interacción del Usuario.**    
A través de la interfaz web, los usuarios podrán enviar órdenes para encender o apagar los proyectores registrados en la base de datos. Podrán decidir si seleccionar una o más unidades fisicas a las que hacer llegar la orden.   
    
<hr style="border:1px solid gray">
         
<img src="https://github.com/user-attachments/assets/db47f6bf-03bf-4516-adad-64f6158c717d" alt="usuario" width="80px" align="right">

**2. Procesamiento en el Servidor.**   
El servidor Spring Boot recibirá las solicitudes y verificará los permisos del usuario.     
Si la solicitud es válida, almacenará la orden en la base de datos para su posterior consulta y ejecución.   

    
<hr style="border: 1px solid #ccc;"/>

<img src="https://github.com/user-attachments/assets/3b4ab769-8221-4381-9a90-b37e3f16dc60" alt="usuario" width="80px" align="right">

**3. Ejecución por el Microcontrolador.**    
El ESP32 (Agente Remoto) consultará periódicamente la base de datos del servidor.   
Si hay tareas pendientes asociadas a su proyector, enviará la orden correspondiente al proyector para que este la ejecute.    
Una vez ejecutada la orden recibida, responderá con el resultado de la operación.    
    
Gracias a este sistema, se logrará una gestión eficiente y automatizada de los proyectores, asegurando su uso óptimo y evitando consumos innecesarios de energía.

<br/>
    
--- 
   
<div align="center">
  <table border="1" cellpadding="10" cellspacing="0">
    <tr>
      <td colspan="9" align="center"><strong>📚 Navegación del Proyecto</strong></td>
    </tr>
    <tr>
      <td align="center"><a href="../../../../README.md">🏠<br><strong>Inicio</strong></a></td>
      <!-- <td align="center"><a href="./src/main/resources/documentacion/acerca-del-proyecto.md">ℹ️<br><strong>Acerca de</strong></a></td> -->
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





