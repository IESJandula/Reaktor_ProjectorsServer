# Dia 1 - 08/02/25
- Creado repositorio.
- Recogidos datos sobre la placa ESP32 y el interfaz RS232
- Configurado el entorno de desarrollo -> Depuirados problemas.
- Primeras pruebas de conexión a wifi.    
**TIEMPO DEDICADO: 4 horas.**

# Dia 2 - 09/02/25
- Averiguado pinout adaptador SR232
- Creada aplicacion base con dependencias de proyecto necesarias.
- Revisión y normalización de modelo relacional.
- Creado esquema de base de datos en aplicacion base.
- Documentadas entidades Java con comentarios Javadoc.

![imagen](https://github.com/user-attachments/assets/58db3fd5-9e9a-4899-8846-53eda922b04a)

**TIEMPO DEDICADO: 5 horas**

# Dia 3 - 10/02/25
- Finalizado el mecanismo de parseo de comandos.
- Creados los logs del mecanismo de parseo de comandos.
- Redactadas las primeras tareas o "roadmap".
- Documentación de clases de parseo.
- Finalizado el mecanismo de parseo de unidades fisicas proyectores.
- Añadido mecanismo de parseo de modelos de proyector.
- Añadidos endpoints de testeo para los mecanismos de parseo.

**TIEMPO DEDICADO: 5 horas**

# Dia 4 - 11/02/25
- Mejora de mecanismo de parseo implementando manejo de errores.
- Mejora de puntos de ataque implementando manejo de errores.
- Modulariza la validacion de los ficheros CSV de los puntos de ataque.
- Arregla configuración de registro de eventos (log).
- Añade documento de referencia para excepciones personalizadas (exceptions-document.txt).
- Añadidos comentarios y javadoc en controlador REST y actualizados comentarios de implementaciones parseadores con nuevas caracteristicas.
- Creado primer draft del mecanismo de creación de eventos en BBDD.

**TIEMPO DEDICADO: 4 horas**

# Dia 5 - 12/02/25
- Se añaden métodos toString() personalizados en todas las entidades para evitar llamadas recurrentes y excepciones de puntero nulo.
- Se corrige el ID en la entidad ServerEvent para usar el tipo envolvente Long con valor autogenerado.
- Se corrigen los comentarios Javadoc en la clase CommandId para mayor consistencia y claridad.
- Se actualiza el archivo exceptions-document.txt con las nuevas excepciones definidas.
- Se reactiva el repositorio ServerEventRepository, que estaba previamente desactivado debido a todas las líneas comentadas.
- Se reorganizan los métodos de los endpoints en el controlador de Projector para mejorar la claridad y estructura.
- Se eliminan comentarios en español innecesarios y obsoletos para mejorar la legibilidad.
- Se introduce el primer borrador del método createServerEvent en el endpoint /server-events.
- Se redactan dos endpoints adicionales para las acciones del MicroController con comentarios bosquejo.
- Se introducen nuevos constantes como ejemplo temporal para los estados de los eventos. (por definir de manera final).

**TIEMPO DEDICADO: 3 horas**


# Dia 6 - 13/02/25
- Redacción de la introducción para el anteproyecto.
- Elaboración de esquemas y material explicativo complementario para el documento del proyecto.
- Redacción preliminar del análisis del proyecto.
- Definición inicial de los objetivos del proyecto.
- Recopilación de datos sobre las conexiones del dispositivo.
- Investigación del mecanismo de comunicación entre UART y RS232.
- Análisis del módulo transceptor y del chip MAX232.
- Estudio de las funcionalidades y el pinout de la placa ESP32.
- Inicio de configuración para la comunicación en el sketch arduino ESP32.
- Redaccion de apuntes sobre la comunicacion de UART y RS232. 
- Sketch ejemplo de conexión y comunicacion (sin pruebas).
- Actualización de informacion sobre le hardware y correccion sobre algunos apuntes de RS232.
- Creación de primera version del endpoint de respuesta a Micro con tarea.
- Diseño de la query de recuperacion de tareas para proyectores.
- Creacion de nuevo DTO simplificado para eventos servidor.

**TIEMPO DEDICADO: 10 horas**

# Dia 7 - 14/02/25
- Investigar protocolo SPI para comunicacion de SD con ESP.
- Apuntes sobre interfaz SPI y hardware modulo MicroSD.
- Actualizacion de tareas.
- Elaboración esquema de cableado.
- Elaboración sketch de prueba interfaz SPI
- Pruebas de funcionamiento con tarjeta SD mediante test-sketch arduino.
- Apuntes varios y estudio de conceptos.
- Pruebas de funcionamiento de almacenamiento interno.
- Pruebas de funcionamiento de almacenamiento SD.

**TIEMPO DEDICADO: 7 horas**


# Dia 8 - 15/02/25
- Diagrama de flujo de bloque de control de certificados en Agente remoto.
- Redacción de la información del hardware en documento anteproyecto.
- Pruebas para gestion de certificados SSL en dispositivo.
- Encontrado problema con milisegundos epoch ESP32.
- Rediseño de diagrama de flujo con ultima versión del sketch funcional.

**TIEMPO DEDICADO: 8 horas**

# Dia 9 - 16/02/25
- Redacción de boceto de información acerca de las tecnolgías empleadas en el proyecto y los lenguajes de desarrollo involucrados.

**TIEMPO DEDICADO: 2 horas**

# Dia 10 - 17/02/25
- Comienzo del desarrollo del frente web para el proyecto.
- Pruebas de comunicacion entre front y back.
- Creación de endpoints de prueba y metodos de recuperación de datos de BBDD.

**TIEMPO DEDICADO: 8 horas**

# Dia 11 - 19/02/25
- Implementado el sistema de navegación en front end vue.
- Mejoras esteticas en front end vue.
- Comienzo proceso modularización del front en componentes.   
    
**TIEMPO DEDICADO: 4 horas**

# Dia 12 - 20/02/25
- Trabajo en front end.
  - Modulariza tabla proyectores.
  - Modulariza tabla acciones.
  - Crea nuevo componente tabla eventos servidor
  - Mejoras esteticas en front end vue.
- Trabajo en back.
  - Crea endpoints que alimentan nuevas tablas en front.
  - Introduce nuevo DTO para Tabla Eventos servidor.
  - Crea metodo en repositorio para el DTO mencionado anteriormente.      
    
**TIEMPO DEDICADO: 5 horas**

# Dia 13 - 21/02/25
- Trabajo en front end
  - Agrega pagina formularios csv y otros.
  - Agrega formulario carga ficheros csv.
  - Agrega formulario borrado modelo.
  - Agrega formulario carga modelo.
  - Agregar componente del modal.
  - Agregar componente de modelo tabla.
  - Agregar formulario asignación proyector a clase.
  - Configuración de aviso info/error formulario registro modelo.

- Trabajo en back end
  - Agrga nuevo DTO para peticiones de modelo proyector.
  - Agrega nuevo DTO para respuestas peticion.
  - Agrege nuevo endpoint para peticion de alta de modelo proyector.
  - Agrega nuevo endpoint para peticion ded baja modelo proyector.
  - Agrega endpoint para recuperar listado modelos en servidor.
  - Revision de endpoints.
  - Comentados endpoints nuevos.
  - Cambiadas respuestas por dto respuesta.
  - Boceto de funcionamiento endpoint de asignación de proyectores a clases.

**TIEMPO DEDICADO: 8 horas**

# Dia 14 - 22/02/25    
- Arreglado el layout para los formularios utilizando grid.
- Simplificado documento html de la plantilla de los formularios.
- Eliminando redundancias en codigo html bootstrap
- Mejoras esteticas aplicadas a pagina Formularios.
- Arregla mensajes commit repositorio para seguir convenio kraken.
    
**TIEMPO DEDICADO: 5 horas**


# Dia 15 - 24/02/25    

- Introducidos cambios en el esquema del proyecto para incluir dos nuevas entidades.
    - Aula
    - Planta
    
![imagen](https://github.com/user-attachments/assets/911dcf85-026e-47cd-a45d-014ecd2cce7f)

    
- Rediseño de la logica del endpoint de parseo de ficheros multiples.
- Creado nuevo DTO para respuesta multiple mensajes.
- Implementada funcionalidad de parseo en front-end
- Arregla nombre repositorio acciones.
- Agrega entidad Floor (planta) con su respectivo repositorio.
- Agrega entidad Classroom (aula) con su respectivo repositorio.
- Agrega cambios al parseador de proyectores para que tengan en cuenta las nuevas entidades.
- Actualiza Endpoint de recuperacion de plantas para que use el repositorio de Floors.
- Actualiza Endpoint de recuperacion de aulas para que use el repositorio de Classrooms.
- Actualiza el endpoint de parseo de proyectores para incluir el manejo de las nuevas entidades.
- Actualiza todas las clases acopladas a la clase Proyector debido a los cambios relacionados al as entidades introducidas.
- Actualiza la logica del front end haciendo uso de los nuevos endpoints modificados, teniendo en cuenta la estructura de las nuevas respuestas recibidas.
- Crea nuevos componentes para combobox de planta y aula.

IMPORTANTE:
- Investigado funcionamiento de modelos reactivos de VUE. -> Es necesario refactorizar pagina de carga de datos para optimizar codigo (actualmente 710 lineas, implementación incompleta.)
    
TODO: 
- Revisar to-string de las entidades.

**TIEMPO DEDICADO: 7 horas**


# Dia 16 - 25/02/25    
- Arreglados mensajes que devuelve el servidor eliminando errores tipográficos.
- Comienzo modularización de front-end. Extrae 'cajas formularios' de la pagina de carga optimizando asi codigo y funciones.
- Atrapado un error en el lanzamiento de excepciones para nombres vacios para modelos de proyector.
- Modularizado formulario CSV
- Modularizado formulario registro modelos.
- Comienzo modularizacion formulario registro proyectores.
    
**TIEMPO DEDICADO: 6 horas**

# Dia 17 - 26/02/25    
- Terminado endpoint de asignacion de proyectores.
- Terminado formulario frontend de asignacion de proyectores con funcionalidad enlazada a backedn.
- Actualizado endpoint de borrado de proyectores con limitaciones de integridad aplicadas
- Añadido nuevo DTO para informacion compelta proyectores.
- Añadido 2 nuevos metodos en repositorio proyectores para recuprar listado de proyectores ordenados.
- añadido nuevo entpoind que devuelve una pagina (paginable) de proyectores con criterio.
- Inicio trabajo en frontend para la creacion de la tabla de proyectores interactiva.

- Creación de las funciones basicas de la tabla.
- Creación de los botones de ordenamiento de la tabla (incompleto)
- Botones de paginacion (incompleto) y boton de llamada a borrado asingación proyector (incompleto).

Por hacer:
- Conectar boton de desasignar con llamada a servidor.
- Hacer la función de Toggle de los botones de ordenamiento y paginacion.
- Revisitar logica peticion ordenamiento tabla
- Revisitar logica de paginacón.

**TIEMPO DEDICADO: 11.5 horas**

    
**TIEMPO DEDICADO: 6 horas**

# Dia 18 - 27/02/25    
- Creado endpoint de des-asignación de proyectores.
- Renombrados enpoints relacionados a los proyectores para mantener consistencia.
- Añadida la función de Toggle de los botones de ordenamiento y paginacion.
- Añadida funcionalidad al boton de borrado de asignación de proyector.
- Añadido recuento de registro en tabla.
- Conectado cartel de alerta con funcion de recuperacion de registros para tabla proyectores. 

- Creada interfaz e implementación de parseo de aulas y plantas.
- Creado endpoint para el parseo de aulas y plantas.
- Integrado en el endpoint de multifichero el parseo de las aulas.
- Integrado en el frontend un nuevo mecanismo de mensajes para las alertas de parseo.
- Modificado RichResponseDto para implementar el nuevo mensaje.
- Cambiados los mensajes de resultado de los parseadores para eliminar títulos en frontend, reduciendo el número de variables involucradas y simplificando el componente FormBox.
- Crea nueva versión inicial formulario de busqueda de proyector para comandos.

**TIEMPO DEDICADO: 9 horas**

**NOTAS**
- Actualmente el sistema no permite dar de alta mas de un mismo modelo en el mismo aula, el ID de un proyector fisico es su (aula + modelo). Si deseamos permitir el uso de mas de un mismo modelo en un aula debemos modificar este comportamiento.
- Revisar errores devueltos por el servidor para reutilizar mas codigos de manera mas generica pero mas categorizada.
- Revisar los mensajes de debug y los niveles aplicados. Recordar aplicar INFO, WARN, ERROR, DEBUG en backend.
- Redactar los mensajes de ayuda en formulario gestion datos.
- Redactar subtitulos de formularios gestion datos.
- Arreglar presentacion y proporciones formulario desasignar proyector.
- Insertar descripcion en entidad acciones y modificar parseador en consecuencia.

