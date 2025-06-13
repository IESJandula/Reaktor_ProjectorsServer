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
      <td align="center"><a href="./interfaz-grafica-y-roles.md">🎨<br><strong>Interfaz</strong></a></td>
      <td align="center"><a href="./api-rest.md">📡<br><strong>API REST</strong></a></td>
      <td align="center"><a href="./hardware-especial.md">🧰<br><strong>Hardware</strong></a></td>
      <td align="center"><a href="./codificacion.md">📟<br><strong>Codificación</strong></a></td>
      <!-- <td align="center"><a href="./instrucciones-de-uso.md">📄<br><strong>Instrucciones</strong></a></td> -->
    </tr>
  </table>

</div>

# 🗒️ Manual de instalación. - Servidor
El servidor alojado en este repositorio está diseñado para integrarse en el ecosistema Reaktor, el cual proporciona servicios clave como autenticación y autorización de usuarios, generación de tokens de identidad y gestión de dependencias. Estas funcionalidades no se implementan de forma local, sino que se delegan a componentes centrales del ecosistema, como el Reaktor Firebase Server. En esta sección se explica cómo obtener e integrar las piezas necesarias para que el proyecto funcione correctamente dentro de este entorno.   

> [!TIP]
> Pincha en los enlaces para clonar los repositorios.
   
**Dependencias ecosistema REAKTOR**
- [Base](https://github.com/IESJandula/Reaktor_Base)
- [Base Server](https://github.com/IESJandula/Reaktor_BaseServer) 
- [BaseClient](https://github.com/IESJandula/Reaktor_BaseClient) 
- [Dependencies](https://github.com/IESJandula/Reaktor_Dependencies) 
- [FirebaseServer](https://github.com/IESJandula/Reaktor_FirebaseServer) 

**Frente web**
- [Somos Jandula](https://github.com/IESJandula/somosjandula)
  
---

## 📍 Paso 1 - Preparar la base de datos.
Para trabajar de forma local con la base de datos, puedes optar por utilizar un contenedor Docker (recomendado por su facilidad y rapidez) o instalar MySQL de forma nativa en tu equipo.

>[!WARNING]
>Si optas por instalar el gestor de MySQL como aplicacion nativa al sistema operativo, ten en cuenta que en muchos casos, instalar un nuevo gestor de bases de datos en un sistema que ya tiene instancias locales activas puede generar conflictos o configuraciones innecesariamente complejas. Si ya dispones de un gestor de BBDD, puedes crear un esquema como indicado mas abajo y asegurarte de que exista un usuario `root` con contraseña `toor` o modificar el fichero de configuración yaml para que utilice los credenciales que tengas configurados para esa instancia.
>    
>![imagen](https://github.com/user-attachments/assets/b5ccbe2b-8c3e-42ea-be26-c1a4319729ec)

<p align=center > 
  <img src="https://github.com/user-attachments/assets/c3a30f5a-9297-4a97-a588-73c0bdc0177d">
</p>  
En un equipo que ya tenga Docker instalado y operativo, basta con ejecutar el siguiente comando:   
     
```cmd
docker run --name miServer -p 3306:3306 -e MYSQL_ROOT_PASSWORD=toor -d mysql
```

>[!TIP]
> Una vez creada la instancia, podremos acceder a la base de datos utilizando cualquier cliente compatible con MySQL, como [dBeaver](https://dbeaver.io/), [MySQL Workbench](https://www.mysql.com/products/workbench/) u otras herramientas similares, conectándose al puerto 3306 del host local. En este ejemplo usaré dBeaver.
    
<p align=center > 
  <img src="https://github.com/user-attachments/assets/830d665d-2582-4cbd-a388-95612fc9e567">
</p>  

**Conectar el visor a la instancia MySQL**    
Ahora que tenemos la instancia de MySQL creada debemos conectar nuestro programa visor de bases de datos. En mi caso mostraré el proceso don dBVeaver pero los fundamentos son los mismos para cualquier cliente.
   
Lo primero es descargar el conector desde este [enlace](https://dev.mysql.com/downloads/connector/j/). Selecciona la opción "platform independent" y descarga el fichero ZIP.    
   
![imagen](https://github.com/user-attachments/assets/07d3ac79-dc8d-497f-a949-0d3e8a0d3703)
   
   
**Abre dBeaver...**
Clic derecho en `Connections` y dentro del submenu de `Crear` seleccionamos `Connection`.  
    
![imagen](https://github.com/user-attachments/assets/a13c0968-4e91-421e-a30c-b25faeb8ddd0)

Seleccionamos MySQL o el motor que estemos usando.    
    
![imagen](https://github.com/user-attachments/assets/3c2f0aea-4511-4401-9643-ac0fa06aa9af)
   

Introducimos la contraseña `toor` para el usuario `root` y luego pinchamos en `Driver Settings`.
    
![imagen](https://github.com/user-attachments/assets/e358429e-812c-4791-9e6b-1f2c9b646f0c)
    
Pincha en la pestaña `Librerias` y selecciona `Añadir carpeta`. 
Busca el directorio donde se almacena el conector que has descargado y en el desplegable inferior selecciona la opción `com.mysql.cj.jdbc.Driver`.   
   
![imagen](https://github.com/user-attachments/assets/381c13e7-88e2-439d-85ff-7fe66ac35719)

Ahora pinchamos en 'Probar conexión...` y observamos el resultado, si dice "Conectado" podemos seguir adelante.      
      
![imagen](https://github.com/user-attachments/assets/38177080-78ea-41db-812a-500169823c60)

   
**Creación de los esquemas necesarios.**    
Ahora que tenemos la instancia debemos de crear los dos esquemas que necesitamos para `Reaktor Firebase Server` y `Reaktor Projector Server`.
Antes de proceder debemos crear 2 esquemas. 
### 🔸 reaktor_firebaseserver
Este esquema almacenará los usuarios que daremos de alta localmente en fase de desarrollo y pruebas a los cuales se otorgará el JWT par autenticarse.

### 🔸 reaktor_projectorsserver
Este esquema almacena la información necesaria para el funcionamiento del servidor de proyectores. Almacena las tablas documentas en la sección [esquema y tablas](esquema-y-tablas.md).

Clic derecho en "Localhost" (o el nombre que hayas puesto a la conexión) y selecciona `Database` en el menú de `Crear`.   
   
![imagen](https://github.com/user-attachments/assets/69dd678b-e1c4-40d1-b8a3-af9b50afc075)

Damos el nombre al esquema y pinchamos en `Aceptar`.    
   
![imagen](https://github.com/user-attachments/assets/c1e694a2-e48e-4975-8a91-cc7e7ca86656)

Sigue estos pasos para ambos esquemas.   
   
![imagen](https://github.com/user-attachments/assets/162194a7-0e56-4124-a8d3-45870bf7559e)
      
## 📍 Paso 2 - Configurar todos los componentes de Reaktor.
Descargamos los componentes y los ubicamos en el directorio donde deseas trabajar, junto al repositorio de este proyecto.
   
![imagen](https://github.com/user-attachments/assets/9874a35a-86f5-4817-9dd6-61fd81e82bd7)    
    
Importamos los proyectos de estos componentes en clipse.   
   
![imagen](https://github.com/user-attachments/assets/b20635c7-0747-4a48-b70f-08fc2c0ae5a5)

>[!CAUTION]
> Es necesario tener instalado Lombok. Sin él no podrás continuar. [Web oficial](https://github.com/user-attachments/assets/d2ef660a-934e-440c-b940-00eaf6e9761d))
    
Una vez importados los proyectos, aparecerán con un simbolo `X` de error. Esto se debe a que no se han resuelto las dependencias. Debemos hacer clic derecho en ellos y seleccionar la opción `Upate project`.    
    
![imagen](https://github.com/user-attachments/assets/a284144d-9fc0-485b-b9e5-0e4f05e564ad)
    
![imagen](https://github.com/user-attachments/assets/d2ef660a-934e-440c-b940-00eaf6e9761d)
    
- Pinchamos sobre el fichero `pom.xml` y seleccionamos `Update maven project`. Luego hacemos un `Clean` y un `Install`.    
    
>[!IMPORTANT]
> Es importante hacer esto en el orden adecuado. Las dependencias son en cascada, esto significa que un componente depende de otro, de este dependerá otro etc..   

El orden deberia ser el siguiente:    
1. Reaktor_dependencies.
2. Base.
3. Reaktor_base_server.
4. Reaktor_base_client.
5. Reaktor_firebaseserver
6. Reaktor_projectorserver.
    

![imagen](https://github.com/user-attachments/assets/066bf3ab-e29e-48ad-8328-e9d9b3c99fd4)




## 📍 Paso 3 - Proyecto de Firebase y clave privada.
Para trabajar con este ecosistema necesitarás un proyecto de firebase. Asegurate de haber activado el metodo de autenticación mediante cuenta de google, y genera una clave privada para configurarla en la aplicacion.

Modo de autenticación por cuenta de google.   
   
![imagen](https://github.com/user-attachments/assets/aa2d8b55-acf5-4342-a54f-7d09d1d1c8dd)

Para la clave privada sigue estos pasos.
1. Selecciona el proyecto y haz clic en el icono del engranaje y selecciona `Configuración del proyecto`.
2. Asegurate de que sea el proyecto correcto.
3. Pincha en `Cuentas de servicio`.
4. Genera una nueva clave.
          
![imagen](https://github.com/user-attachments/assets/7d461b6e-ab7c-410c-af67-9d494d628d69)


Una vez obtenida la clave, en el fichero `application.yaml` de **reaktor_firebaseserver** y **reaktor_projectorsserver**, configuraremos su ubicación.    
    
![imagen](https://github.com/user-attachments/assets/b1864b25-3625-4c79-8e3d-169dca8c4783)


## 📍 Paso 4 - Registros de roles.
Ahora que el sistema funciona, debemos introducir almenos un registro con el rol que deseamos probar.
Para ello rellenamos la base de datos de `reaktor_firebaseserver` con registros como estos.

![imagen](https://github.com/user-attachments/assets/32c4cb0d-58d7-419c-84fd-d2171267b2bb)



# 🗒️ Manual de instalación. - Front end
Para realizar pruebas en el front necesitamos `node.js` y por comodidad usaremos `vs code`.

## 📍 Paso 1 - Install node.js
Vamos a la [página oficial](https://nodejs.org/en/download) y pinchamos en descargar el instalador.   
   
![imagen](https://github.com/user-attachments/assets/7bdcc710-3deb-451c-bf9a-5208490d3d14)   

Una vez instalado node.j,  si aparece el error de politica de acceso restringida como en esta imagen:    
   
![imagen](https://github.com/user-attachments/assets/b530e757-8236-4640-9666-9bef1d54ea1c)
    
Deberemos abrir powershell y ejecutar el siguiente comando.       
    
```
Set-ExecutionPolicy Unrestricted -Scope CurrentUser
```
   
Confirmamos y reiniciamos VS Code.




## 📍 Paso 2 - Instalamos las dependencias del proyecto.
Con VS Code abierto, en la terminal introducimos el siguiente comando:   
   
```
npm install --force
```
   
Al completarse la operación obtendremos un informe de los paquetes descargados e instalados.    
   
![imagen](https://github.com/user-attachments/assets/b3c25718-36a6-4db5-855e-b947de2f67be)   

Introducimos el comando de lanzamiento para el front:   
```
npm run dev
```
   
![imagen](https://github.com/user-attachments/assets/98c4f821-e5df-485b-8f7b-1d4fad4fedff)





# 📁 Carga de datos al servidor.
Para cargar datos en el servidor, el administrador dispone de un formulario sencillo donde debe subir dos archivos CSV. Estos archivos contienen toda la información necesaria para que el sistema pueda:
- 🆔 Identificar los proyectores
- 📍 Ubicarlos correctamente
- 🔎 Filtrarlos según distintos criterios
- 🔗 Relacionarlos con las instrucciones disponibles

>[!CAUTION]
>Para que el proceso funcione correctamente, los archivos deben estar configurados y formateados de manera precisa.

 En esta sección se describen las normas y observaciones esenciales que deben seguirse para que la carga de datos se realice con éxito y los archivos sean interpretados correctamente por el sistema.

---
<a name="carga-datos-servidor"></a>
Escribir seccion de carga de datos.

    
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
      <td align="center"><a href="./interfaz-grafica-y-roles.md">🎨<br><strong>Interfaz</strong></a></td>
      <td align="center"><a href="./api-rest.md">📡<br><strong>API REST</strong></a></td>
      <td align="center"><a href="./hardware-especial.md">🧰<br><strong>Hardware</strong></a></td>
      <td align="center"><a href="./codificacion.md">📟<br><strong>Codificación</strong></a></td>
      <!-- <td align="center"><a href="./instrucciones-de-uso.md">📄<br><strong>Instrucciones</strong></a></td> -->
    </tr>
  </table>

</div>

---


   



