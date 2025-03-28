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


# Dia 19 - 28/02/25    
- Integrado spinner en front end para formularios.
- Progreso en pantalla de control remoto.
- Rediseño de logica de ordenes a proyectores.
- Comienzo del trabajo en nuevo endpoint de orden por lotes a listado de proyectores.
- Control de situación y anotación de cambios a implementar.
- Oros avanzes menores. 
 
**TIEMPO DEDICADO: 7 horas**

# Dia 19 - 01/03/25    
- Comienzo en pagina de envio de ordenes masivos.
- Diagnosticado problema con el borrado de modelos y proyectores en cascada.
    - Investigado modo como solventar esto, propuesta de servicio y proveedor de entidades por defecto en sostitución a cascada (requiere mas pruebas.)
- Terminada primera versión de pantalla de envio de ordenes masivo.
- Reorganizados algunos endpoints en controlador REST.
- Documentado y comentado controlador REST en los endpoints modificados.
- Modificados botones de paginación y de ordenamiento y añadido selector de registros por paginas.
- Otros cambios menores.

Notas: 
- Identificada la necesidad de alterar endpoint de recuperación de proyectores a favor de uno filtrado.
- Identificado el malfuncionamiento de la funcionalidad de 'seleccionar todo' debido al uso de paginación.

**TIEMPO DEDICADO: 7.5 horas**


# Dia 20 - 02/03/25    
- Finalizado pagina envios masivos.
- Comienzo trabajo en pagina eventos.
- Creado nuevo endpoint para la gestión del a modificacion del estado de eventos.
- Creados nuevos componentes y ficheros de configuración en front end.

**TIEMPO DEDICADO: 6.2 horas**


# Dia 21 - 03/03/25    
- Comienzo desarrollo mecanismo registros verticales.
- Primer diseño de pruebas de registro vertical.

**TIEMPO DEDICADO: 2 horas**

# Dia 22 - 04/03/25    
- Breve revisión en frio del diseño realizado.

**TIEMPO DEDICADO: 1 horas**

# Dia 23 - 05/03/25    
- Configuración de los colores de estado para las tarjetas de evento.
- Configuración de la responsividad de la navbar.
- Configurada la responsividad de las tarjetas/registros de eventos.
- Comienzo del desarrollo de las herramientas de paginación y filtrado de registros.

**TIEMPO DEDICADO: 5 horas**


# Dia 24 - 06/03/25    
- terminado de configurar el filtro de ubicacion de la tabla de eventos.
- implementando filtro de estado de accion.
- Actualizando endpoint para observar estados de accion.
- Introducido filtro de estado de acción.

**TIEMPO DEDICADO: 3.5 horas**


# Dia 25 - 08/03/25    
- Implementado formulario de elminación de acciones.
- Implementado formulario de elminación de comandos.
- Creados componenetes de tabla seleccionables para los arriba mencionados.
- Creados los endpoints necesarios para el funcionamiento de lo arriba mencionado.
- Revisión de estado y anotación de tareas.

**TIEMPO DEDICADO: 5 horas**

# Dia 26 - 10/03/25    
- Pruebas de identificación de Microcontrolador hacia servidor.
- Arregladas referencias hardcodeadas por constantes en frontend.
- Cambios a endpoint de servido de tareas para que actualize los estados.
    - Mecanismo de actualización de estado de eventos servidor.
- Unificación de funciones de prueba en un unico guion para el microcontrolador.
    - Consolidación del codigo en unico fichero.
    - Pruebas de conexión y funcionamiento con backend.
    - Diagnostico de error `HTTPClientSecure`.

**TIEMPO DEDICADO: 7 horas**

# Dia 27 - 12/03/25    
- Limpieza en script ESP32.
- Reordenacion de las operaciones en base a nuevas exigencias.
- Añadido bloque de codigo para el fichero de configuracion wifi externo.
- Parametrizado de funciones de comparacion y copia.
- Modularizacion del codigo para una mejor legibilidad, mantenimiento.
- Mediante funciones, reutilizacion de codigo para gestion de ficheros de certificado y configuracion.
- Mejora de mensajes de DEBUG.
- Mejora de gestion de memoria en solicitud a servidor.

**TIEMPO DEDICADO: 7.5 horas**

# Dia 28 - 14/03/25    
- Refactor de las entidades existentes para reflejar las nuevas indicaciones.
- Adaptación del controlador para su correcto funcionamiento con el refactor actual.
- Adaptación de los parseadores para su correcto funcionamiento con el refactor actual.
- Adaptación de las queries de los distintos repositorios y todas las referencias a las entidades eliminadas y a los nuevos atributos.
- Cambios en pantalla "Eventos servidor" para reflejar las nuevas indicaciones.
    - Eliminados iconos de estado.
    - Insertado desplegable para filtro de estados.
    - Eliminados campos innecesarios de la tabla y reordenados.

- Fusión de vista resumen y carga de datos en nueva vista Administrar.
    - El refactor actual elimina las entidades Classroom y Floor y las absorbe en su lugar la entidad Projector.
Además, desconecta Projector de la entidad ProjectorModel, ya que esta también será refactorizada en el futuro.

BACK
- Agrega nuevo endpoint para servir posibles estados.
- Modificado método de parseo de ficheros múltiples para dejar de lado el fichero de clases, creada gestión de respuesta de ficheros por separado respecto al estado.
- Mejorado el DTO de RichResponseDto para poder administrar estados de respuestas de manera independiente.
- Readapta todos los endpoints relacionados a Classroom y Floor para que sirvanl os mismos Dtos esperados por el front a partir del repositorio de Proyectores.

FRONT END
- Rediseña página "Eventos servidor".
        - Elimina iconos de estado bajo petición del cliente, introduce un desplegable en su lugar.
        - Elimina campos innecesarios y los reordena en la tabla de registro de eventos.
- Rediseño de página de panel de control.
        - Cambiado nombre a 'Administrar'.
        - Añadido formulario de carga de datos.
        - Cambiada la gestión de la respuesta de los formularios de carga para representar mejor el resultado de las operaciones por separado.
        - Borra componente innecesario FloorModelFilter → Pasado a componente padre.
Rediseña vista registro eventos para incorporar componentes y poder eliminar clases.
- Añade modal de información al usuario de la orden enviada.
- Añade función de deshabilitado de botón tras pulsar en acción.
- Muestra al usuario cuánto queda para la próxima acción.
- Refactorización de componentes y vistas de la parte front end.
    - Refactorizados distintos componentes que debido al nuevo diseño ven un menor uso y pueden ser implementados directamente en la vista.
    - Eliminados hasta ahora 5 componentes refactorizados dejando un total de 9 componentes.     

**TIEMPO DEDICADO: 11 horas**


# Dia 28 - 18/03/25    
- Refactor de los modales para reducir codigo repetido.
- Elimina funcion fetchModelsOverview y respectivo modulo no utilizado debido a nuevo diseño.
- Modifica componentes vue de SelectableCommandTable y SelectableActionTable para incluir banners de alerta.
- Identificado un problema en la actualizacion de la lista de acciones seleccionadas al elminar una accion.

TODO: Encontrar origen problema de sincronizacion.

**TIEMPO DEDICADO: 3 horas**


# Dia 29 - 19/03/25    

- Arregladas las referencias bidireccionales en los formularios de borrado de acción y de comandos.
- Añade subtitulos a los FormBoxes para control remoto y carga de ficheros CSV.
- Añade tooltips para los iconos de los estado de evento.  
- Deshabilitado temporalmente el div colapsable debido a malfuncionamiento.
- Añade mensaje de cuenta atrás para disponibilidad de comando.

TODO: 
- Finalizar los modales de borrado.
- Revisar mecanismo de parseo en servidor.
- Añadir campo de descripción para errores o estados en registro eventos.
- Añadir tooltip para descripcion registro eventos.
- Controlar spinners en modales.
- Controlar comportamiento de borrado de acciones y comandos cuando existen registros relacionados al elemento a borrar.
- Replicar estilo de carga de modal de accion y comandos en modal de proyectores.
- Equiparar botones efecto hover.

**TIEMPO DEDICADO: 3 horas**
