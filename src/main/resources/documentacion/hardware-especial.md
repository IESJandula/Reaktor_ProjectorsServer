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


---

<a name="hardware-especial"></a> 
# 🧰 Hardware especial - Microcontroladores y modulos añadidos.
En esta sección, se ofrece una visión detallada del hardware utilizado en este proyecto, junto con observaciones relevantes recopiladas durante el proceso de desarrollo.
    
<a name="esp32"></a> 
## 📌 La placa principal - ESP32

La placa utilizada en el proyecto es una **Keyestudio ESP32-WROOM-32 Module Core Board**.  
La Keyestudio ESP32-WROOM-32 es una placa de desarrollo versátil diseñada para aplicaciones de IoT y hogares inteligentes. Integra el módulo ESP32-WROOM-32, que ofrece conectividad Wi-Fi y Bluetooth, y es compatible con el Arduino IDE.

**¿Qué lenguajes se pueden usar para desarrollar en esta placa?**  
El microcontrolador ESP32 se puede programar en varios lenguajes, dependiendo del entorno de desarrollo y del caso de uso.  
Entre las opciones más populares se encuentran:
- C / C++
- MicroPython
- JavaScript
- Lua
    
### 🔹 Especificaciones del Keyestudio ESP32-WROOM-32 XX0H32

Un aspecto importante a mencionar es que el ESP32 generalmente cuenta con un chip de memoria flash de 4MB integrado. Esta memoria flash se particiona para asignar espacio a diferentes propósitos: almacenamiento del programa (aplicación), SPIFFS (o LittleFS en el caso de este proyecto) para almacenamiento de archivos y, opcionalmente, una partición OTA para actualizaciones de firmware inalámbricas.

Es crucial configurar el esquema de particiones correctamente para asignar la cantidad adecuada de espacio a cada propósito, según las necesidades del proyecto.

<table>
    <tr>
        <th>Feature</th>
        <th>Details</th>
        <th>Images</th>
    </tr>
    <tr>
        <td>Microcontroller</td>
        <td>Módulo ESP-WROOM-32</td>
        <td rowspan="9"><img src="https://github.com/user-attachments/assets/7a27f529-b2a2-4130-9c9a-a6fa5d1f1406" alt="ESP32 Front View"></td>
    </tr>
    <tr>
        <td>USB to Serial Port Chip</td>
        <td>CP2102-GMR</td>
    </tr>
    <tr>
        <td>Operating Voltage</td>
        <td>DC 5V</td>
    </tr>
    <tr>
        <td>Operating Current</td>
        <td>80mA (promedio)</td>
    </tr>
    <tr>
        <td>Current Supply</td>
        <td>500mA (mínimo)</td>
    </tr>
    <tr>
        <td>Operating Temperature Range</td>
        <td>-40℃ ~ +85℃</td>
    </tr>
    <tr>
        <td>WiFi Mode</td>
        <td>Station / SoftAP / SoftAP+Station / P2P</td>
    </tr>
    <tr>
        <td>WiFi Protocol</td>
        <td>802.11 b/g/n/e/i (hasta 150 Mbps)</td>
    </tr>
    <tr>
        <td>WiFi Frequency Range</td>
        <td>2.4 GHz ~ 2.5 GHz</td>
    </tr>
    <tr>
        <td>Bluetooth Protocol</td>
        <td>Compatible con Bluetooth v4.2 BR/EDR y BLE</td>
        <td rowspan="8"><img src="https://github.com/user-attachments/assets/ded84c16-4fc8-44f1-9782-d8fbb186db21" alt="ESP32 Back View"></td>
    </tr>
    <tr>
        <td>Dimensions</td>
        <td>55mm*26mm*13mm</td>
    </tr>
    <tr>
        <td>Weight</td>
        <td>9.3g</td>
    </tr>
    <tr>
        <td>GPIO Pins</td>
        <td>34 (algunos con soporte ADC/DAC)</td>
    </tr>
    <tr>
        <td>ADC Channels</td>
        <td>18</td>
    </tr>
    <tr>
        <td>DAC Channels</td>
        <td>2</td>
    </tr>
    <tr>
        <td>PWM Channels</td>
        <td>Múltiples</td>
    </tr>
    <tr>
        <td>I2C / SPI / UART</td>
        <td>Sí (múltiples)</td>
    </tr>
</table>

![imagen](https://github.com/user-attachments/assets/7390d1e3-e9a5-4d1f-b9ea-9e96863f41ca)
    
---
       
<a name="rs232"></a> 
## 📌 La interfaz de comunicación - Convertidor RS232 a TTL

Un **convertidor RS232 a TTL** es un dispositivo utilizado para conectar dos tipos de protocolos de comunicación serial: **RS232** y **TTL (Transistor-Transistor Logic)**.  
Estos protocolos operan a diferentes niveles de voltaje y requieren conversión para comunicarse correctamente.

**Este dispositivo fue esencial en este proyecto para permitir la comunicación entre el ESP32 y el proyector que debía ser controlado.**  
El convertidor RS232 a TTL ajusta los niveles de voltaje de las señales RS232 a niveles compatibles con TTL y viceversa, permitiendo la comunicación fiable entre dispositivos con diferentes estándares.

<p>
     <img align="right" src="https://github.com/user-attachments/assets/2f611e96-63cf-4b75-9c10-582fe3969af4">
</p>

### Diferencias entre RS232 y TTL:

1. **RS232**:
   - Estándar clásico utilizado en la comunicación entre dispositivos como PCs, módems o impresoras.
   - Utiliza **voltajes altos** (±12V).
   - Lógica “1” = +12V, lógica “0” = -12V.

2. **TTL**:
   - Estándar lógico usado en sistemas embebidos y microcontroladores.
   - Usa **niveles bajos de voltaje** (0V = “0”, 3.3V o 5V = “1”).
   - Muy común en dispositivos modernos como el ESP32.

### Características destacadas:

- **Conversión RS232 a TTL**: Ajusta los niveles de voltaje entre ambos estándares.
- **Bidireccional**: Permite enviar y recibir datos.
- **Level shifting**: Asegura compatibilidad entre señales de diferente voltaje.

> [!Note]  
> Mi unidad específica cuenta con un chip **MAX3232 ESE+2416**, encargado de convertir los niveles de voltaje entre RS232 y TTL, garantizando una comunicación fiable con el proyector.  
> La distribución de pines en mi unidad se puede observar en la siguiente imagen:
   
![rs232](https://github.com/user-attachments/assets/2f9f8a77-e7d4-4c15-86b1-3b8c20906e02)
   
![rs232](https://github.com/user-attachments/assets/3cce1348-7e50-454f-9153-d7c20fea84fa)
    
---
    
<a name="microsd"></a> 
## 📌 Almacenamiento externo - Módulo MicroSD

Este módulo es ideal para agregar almacenamiento masivo al proyecto, permitiendo guardar y recuperar datos desde una tarjeta MicroSD.  
Está diseñado para usarse con microcontroladores y se comunica mediante una interfaz SPI (Serial Peripheral Interface) de 4 pines, ampliamente soportada por plataformas embebidas como el ESP32.

El módulo se conecta al ESP32 utilizando los pines SPI predeterminados.  
**Cada pin está claramente etiquetado, lo que facilita su conexión**, especialmente si se consulta la documentación del fabricante.

**Tanto el módulo como el ESP32 operan a 3.3V**, por lo que **no se requiere un adaptador de niveles de voltaje** (level shifter).

Este módulo es un **LC Technology MicroSD Module**, ampliamente utilizado y confiable, compatible con la mayoría de proyectos de tarjetas MicroSD.

> [!NOTE]  
> El módulo y el ESP32 operan ambos a 3.3V, por lo tanto no es necesario usar un adaptador de niveles (level shifter), lo cual sí suele ser necesario con otros microcontroladores como Arduino UNO.

<table>
  <tr>
    <th>MicroSD Card Module</th>
    <th>ESP32</th>
    <th>Top side</th>
    <th>Bottom side</th>
  </tr>
  <tr>
    <td>3V3</td>
    <td>3.3V</td>
    <td rowspan="6"><img src="https://github.com/user-attachments/assets/2d517703-273e-46cf-9843-b3f56b074c7a" alt="Image 1" width="300"></td>
    <td rowspan="6"><img src="https://github.com/user-attachments/assets/b9d3420f-26ef-4e08-9700-2a3ea5d3ee13" alt="Image 2" width="300"></td>
  </tr>
  <tr>
    <td>CS</td>
    <td>GPIO 5</td>
  </tr>
  <tr>
    <td>MOSI</td>
    <td>GPIO 23</td>
  </tr>
  <tr>
    <td>CLK</td>
    <td>GPIO 18</td>
  </tr>
  <tr>
    <td>MISO</td>
    <td>GPIO 19</td>
  </tr>
  <tr>
    <td>GND</td>
    <td>GND</td>
  </tr>
</table>
    
<a name="esquema-de-conexion"></a>    
## 📌 Esquema de conexión de componentes.
A continuación se muestra el esquema de conexión de los elementos conectados al ESP32.

### 🔹 Conexión de transceptor RS232 - Max 3232

<table>
     <tr>
          <td>
               Para enviar una señal desde un ESP32 a un dispositivo RS232, es necesario hacer coincidir la configuración de comunicación (velocidad en baudios, bits de datos, bits de parada) y asegurar que el la conexión a pines sea correcta. Dado que el ESP32 utiliza lógica de 3.3V y el RS232 utiliza niveles de voltaje más altos (±12V), debemos usar un convertidor de nivel o un convertidor de RS232 a TTL (como un <strong>MAX3232</strong>) para conectar ambos de forma segura y evitar dañar el ESP32.
          </td>
          <td width="250px">  <sub><p>Pincha para agrandar.</p><sub>
               <img align="right" src="https://github.com/user-attachments/assets/ee8df342-79c8-4e29-a25b-4f374f9fa3a2">
          </td>
     </tr>
</table>
   
---
    
### 🔹 Conexión de módulo Micro SD SPI

<table>
     <tr>
          <td>
El módulo lector de tarjetas Micro SD se comunica utilizando el protocolo de comunicación SPI ( Serial Peripheral Interface ). Podemos conectarlo a nuestro microcontrolador utilizando los pines por defecto destinados a este protocolo.
          </td>
          <td width="250px">  <sub><p>Pincha para agrandar.</p><sub>
               <img align="right" src="https://github.com/user-attachments/assets/57df47df-abae-4b8a-8825-c4d553268a1f">
          </td>
     </tr>
</table>
    
>[!CAUTION]
>Es posible re-asignar los pines pero no es recomendable ni necesario.
    
---

### 🔹 Indicadores led para pruebas en fase de desarrollo.
<table>
     <tr>
          <td>
Durante la fase de pruebas, el uso de indicadores LED resultó especialmente útil para visualizar el estado actual del programa en ejecución. Estos LEDs no están destinados para su uso en un entorno real, sino que fueron implementados exclusivamente para la etapa de desarrollo, con el objetivo de facilitar el proceso de depuración y verificar el correcto funcionamiento del código cargado en el microcontrolador.

En la versión final del programa, se han integrado cuatro LEDs con las siguientes funciones:

- LED Rojo: Parpadea mientras el agente intenta establecer una conexión con la red WiFi. Se apaga una vez que el proceso de conexión finaliza o se aborta. 


- LED Verde: Permanece encendido de forma continua cuando la conexión a la red se ha realizado con éxito.

- LED Azul: Se enciende de forma fija una vez que el sistema de archivos local ha sido montado correctamente.

- LED Blanco: Permanece encendido mientras la tarjeta SD está siendo utilizada por el microcontrolador.
          </td>
          <td width="250px">  <sub><p>Pincha para agrandar.</p><sub>
               <img align="right" src="https://github.com/user-attachments/assets/3fdcecec-5e09-48f7-acdf-073bc1d6f3a1">
          </td>
     </tr>
</table>
    
---
   
<a name="configuración-rs232"></a>    
## 📌 Configuración de las comunicaciones del puerto RS232.

Para enviar una señal desde un ESP32 a un dispositivo RS232, es necesario igualar los ajustes de comunicación (velocidad en baudios, bits de datos, bits de parada) y garantizar un cableado correcto. Dado que el ESP32 utiliza lógica de 3.3V y el RS232 utiliza niveles de voltaje más altos (±12V), se debe usar un convertidor de nivel o un convertidor de RS232 a TTL (como un MAX232) para interconectar ambos de manera segura y evitar dañar el ESP32.

**Entonces, ¿qué debemos hacer?**   
- Identificar los pines TX y RX en la placa para UART2.
- Recopilar información sobre la interfaz del dispositivo RS232.
- Asegurarnos de que nuestro módulo RS232 incluya un convertidor de nivel como el MAX232 o equivalente.
   
### 📍 0.1 - Identificar los pines TX y RX en la placa para UART2.

El RS232 debe conectarse a los pines TX y RX de la placa ESP32, cuyas ubicaciones pueden variar según el modelo específico. Para garantizar una conexión adecuada, es importante utilizar los pines UART correctos. Para la comunicación RS232, debemos usar UART2.

#### 🔸 ¿Qué son UART0, UART1 y UART2?

UART2 en el ESP32 es una de sus tres interfaces UART (Receptor-Transmisor Asíncrono Universal) disponibles, utilizadas para comunicación serie. Permite la transmisión y recepción de datos a través de los pines TX y RX, comúnmente utilizados para conectar periféricos como módulos GPS, sensores o adaptadores RS232. A diferencia de UART0 (usado para depuración) y UART1 (a menudo vinculado a operaciones de memoria flash), UART2 puede asignarse libremente a pines GPIO disponibles para aplicaciones personalizadas.

Los pines predeterminados para UART2 en el ESP32 son:

- **TX:** `Pin 17`
- **RX:** `Pin 16`
    
```c++
// Definiciones para la comunicación RS232.
#define txPinRS232 17  // Pin TX para RS232
#define rxPinRS232 16  // Pin RX para RS232
```

>[!IMPORTANT]
>Estos son los pines comúnmente utilizados para la comunicación UART2 en muchas placas ESP32 por defecto. Si estás utilizando un conjunto diferente de pines o los has reasignado, asegúrate de actualizar los números de pin correspondientes en el código.

**Imagen de referencia**   
![pinsOfInterestRS232](https://github.com/user-attachments/assets/b5001e0c-9e99-4562-a08e-230bf7694dca)

<!-- ![imagen](https://github.com/user-attachments/assets/7390d1e3-e9a5-4d1f-b9ea-9e96863f41ca) -->

### 📍 0.2 - Recopilar información sobre la interfaz del dispositivo RS232.
Esta información es necesaria para configurar el protocolo de comunicación. La información necesaria es:

- Velocidad en baudios predeterminada.
- Bits de datos.
- Bits de paridad.
- Bits de parada.
   
En mi caso, para mi dispositivo específico, tengo la siguiente información proporcionada por el fabricante.

#### Comunicación RS232 con el Proyector
##### Líneas de Conexión

<table>
   <tr>
      <th>Señal</th>
      <th>Función</th>
   </tr>
   <tr>
      <td><b>RxD</b></td>
      <td>Recibe datos desde el computador externo</td>
   </tr>
   <tr>
      <td><b>TxD</b></td>
      <td>Transmite datos al computador externo</td>
   </tr>
   <tr>
      <td><b>GND</b></td>
      <td>Tierra para señales de datos</td>
   </tr>
</table>

##### Configuración del Puerto COM

<table>
   <tr>
      <th>Parámetro</th>
      <th>Valor</th>
   </tr>
   <tr>
      <td><b>Velocidad en Baudios (Predeterminada)</b></td>
      <td>9600</td>
   </tr>
   <tr>
      <td><b>Bits de Datos</b></td>
      <td>8</td>
   </tr>
   <tr>
      <td><b>Paridad</b></td>
      <td>Ninguna</td>
   </tr>
   <tr>
      <td><b>Bit de Parada</b></td>
      <td>1</td>
   </tr>
</table>

>[!Note]
>**¿Por qué es importante esta información?**
>Para establecer una conexión confiable y comunicarse adecuadamente con el dispositivo, necesitamos configurar parámetros clave de comunicación. Estos ajustes definen la velocidad y estructura del intercambio de datos, garantizando compatibilidad entre dispositivos.
>
>En el siguiente ejemplo:
>```c++
>mySerialPort.begin(9600, SERIAL_8N1, 16, 17); // (Velocidad en baudios, configuración, RX, TX)
>```

### 📍 0.3 - Conversión de Nivel Lógico
El ESP32 opera a lógica TTL de 3.3V, mientras que el RS232 opera a niveles lógicos de ±12V. Conectarlos directamente dañará el ESP32.

Para convertir los niveles de señal, utiliza un adaptador/módulo de RS232 a TTL, como:
- Módulo MAX232 (común y económico)
- Módulo SP3232 (mejor para lógica de 3.3V)

Estos módulos cambiarán los niveles de voltaje de manera segura.

>[!TIP]
>En mi caso, usaré un MAX232. Para verificar tu propio chip, inspecciona tu módulo, encuentra el chip instalado y toma nota de su nombre.
>
>![imagen](https://github.com/user-attachments/assets/f043ed6b-b8a6-4fc0-8619-04fa217aed6c)

##### 🔸 ¿Qué hace el MAX232?

El MAX232 se utiliza para convertir niveles de voltaje entre los estándares de comunicación serie TTL (Lógica Transistor-Transistor) y RS-232.
- Lógica TTL (0V y 5V o 3.3V): Utilizada por microcontroladores (como Arduino, PIC o AVR).
- Señales RS-232 (-12V a +12V o -10V a +10V): Utilizadas para puertos serie en PCs y otros dispositivos de comunicación.

**Cómo Funciona:**
- El MAX232 toma una fuente de alimentación de 5V y genera internamente los voltajes positivos y negativos más altos necesarios para la comunicación RS-232 mediante bombas de carga.
- Tiene condensadores incorporados (se necesitan externos en algunas versiones) para la conversión de voltaje.
- Proporciona dos drivers y dos receptores, lo que significa que puede manejar dos líneas de transmisión y dos de recepción.

### 📍 1 - Conexiones de cableado.

Después de identificar los pines para transmisión y recepción de datos, podemos proceder a cablear el transceptor a la placa ESP32.
Para conectar correctamente, consulta las anotaciones en el módulo. **`TX`** significa Transmisión y **`RX`** significa Recepción.

![srs232](https://github.com/user-attachments/assets/3cce1348-7e50-454f-9153-d7c20fea84fa)

>[!TIP]
>El esquema de colores común es el representado en la imagen.
>- Negro para Tierra (GND).
>- Rojo para VCC o positivo (3.3v del ESP32).
>- Amarillo (o Naranja) para Transmisión (TX).
>- Verde para Recepción (RX).

Conecta los cables a los pines designados según los pasos anteriores. En total, hay cuatro conexiones que deben realizarse: `+3.3v`, `GND`, `TX` y `RX`.

![circuito](https://github.com/user-attachments/assets/ee62e48a-1c9b-4037-a5cf-2968279c8803)

### 📍 2 - Configurar la variable en el programa.

Ahora es el momento de definir las sustituciones basadas en texto para los pines y configuraciones de puerto que hemos anotado hasta ahora.

```c++
// Biblioteca para comunicación RS232
#include <HardwareSerial.h>
// Definiciones para comunicación RS232.
#define txPinRS232 17  // Pin TX para RS232
#define rxPinRS232 16  // Pin RX para RS232
// Configuración de comunicación serie del proyector
#define projectorRate 9600  // Velocidad en baudios: 9600
// Bits de Datos: 8
// Paridad: Ninguna
// Bits de Parada: 1
```

### 📍 3 - Inicializar la clase HardwareSerial con un tipo UART.

La clase `HardwareSerial` permite utilizar los UARTs de hardware del ESP32.

```c++
// Usar UART2 en el ESP32
HardwareSerial mySerialPort(2);
```

>[!Note]
>UART (Receptor-Transmisor Asíncrono Universal) es un protocolo de comunicación serie utilizado en el ESP32 para permitir la comunicación entre dispositivos como sensores, microcontroladores y computadoras. Es una forma simple y eficiente de enviar y recibir datos sin requerir una señal de reloj.

**Explicación:**

El ESP32 tiene tres UARTs de hardware:
- UART0 → Se utiliza para depuración (Serial predeterminado).
- UART1 → Normalmente conectado a la memoria flash integrada (evita usarlo).
- UART2 → Disponible para uso general (mejor opción para comunicación RS232).

Por lo tanto, al usar `HardwareSerial mySerialPort(2);` le estoy indicando al ESP32 que use UART2 para comunicación serie.

Por defecto, UART2 está asignado a:
- TX → GPIO17
- RX → GPIO16

Sin embargo, estos pines se pueden cambiar usando:

```c++
mySerial.begin(9600, SERIAL_8N1, txPinRS232, rxPinRS232);  // RX en GPIO16, TX en GPIO17
```

### 📍 4 - Realizar un handshake si es necesario.

En este punto, el puerto de comunicación serie se ha inicializado, pero aún necesitas establecer una conexión con el dispositivo (por ejemplo, un proyector). Esto generalmente implica enviar un "handshake" inicial o algún comando para verificar que la conexión está activa. En mi caso específico, no es necesario un handshake ya que el dispositivo no lo requiere.

### 📍 5 - Enviar datos al puerto.
Para enviar datos desde el ESP32 al dispositivo RS232, puedes usar la función `print()`, `println()` o `write()`. Estas funciones te permiten enviar cadenas o arreglos de bytes sobre la comunicación serie.
        
>[!TIP]
>La comunicación RS232 se realiza típicamente utilizando arreglos de bytes (también conocidos como datos binarios sin procesar). Los datos se transmiten como una secuencia de bytes, donde cada byte representa 8 bits de información.
>Esto transmitirá el arreglo de btyes hexadecimales `byteArray` al dispositivo RS232.

##### 🔸 Para escribir un arreglo de bytes, utiliza las siguientes instrucciones.
Para escribir el arreglo de bytes para este comando:

<table border="1">
   <tr>
      <th>Comando</th>
      <th>Hexadecimal</th>
      <th>Descripción</th>
   </tr>
   <tr>
      <td>* 0 IR 001\r</td>
      <td>2A 20 30 20 49 52 20 30 30 31 0D</td>
      <td>Encender</td>
   </tr>
</table>

```c++
// Definir el arreglo de bytes que corresponde a tu comando
byte comando[] = {
0x2A, 0x20, 0x30, 0x20, 0x49, 0x52, 0x20, 0x30, 0x33, 0x32, 0x0D
};

// Almacena la longitud final del arreglo.
int numBytes = 11;

// Escribe al puerto los datos especificando su longitud (implementacion iterada del write normal).
rs232Port.write(byteArray, numBytes);
}
```
    
---
    
<a name="configuración-spi"></a>
## 📌 Configuración de la tarjeta MicroSD con interfaz SPI
El módulo de tarjeta microSD se comunica mediante el protocolo SPI (Serial Peripheral Interface). Puedes conectarlo al ESP32 usando los pines SPI predeterminados.

### 📍 1 - Conexión del módulo al ESP32
La conexión del módulo está etiquetada y podemos identificar fácilmente el pin correspondiente en la placa con la imagen de referencia del fabricante.

>[!CAUTION]
>Estos pines pueden reasignarse usando HSPI y VSPI; sin embargo, **no se recomienda** en la mayoría de casos debido a posibles diferencias en rendimiento, compatibilidad con librerías existentes y optimizaciones de hardware predeterminadas.

#### 🔹 Colores de cableado
En configuraciones típicas para comunicación SPI:
- **MISO**: Amarillo
- **MOSI**: Verde
- **CLK**: Azul
- **CS**: Morado o naranja

Estos códigos de color ayudan a distinguir las señales SPI, facilitando la conexión correcta entre dispositivos. Siempre verifica la codificación de colores de tus componentes específicos.

| Pin del módulo | ESP32       | Color cable |
|----------------|-------------|-------------|
| 3V3            | 3.3V        | 🔴 ROJO     |
| CS             | GPIO 5      | 🟣 MORADO   |
| MOSI           | GPIO 23     | 🟢 VERDE    |
| CLK            | GPIO 18     | 🔵 AZUL     |
| MISO           | GPIO 19     | 🟡 AMARILLO |
| GND            | GND         | ⚫ NEGRO    |

![Circuito MicroSD](https://github.com/user-attachments/assets/1e3681d9-4a9f-4154-be5e-87181af45af2)

### 📍 2 - Definición de pines
Define las conexiones en el programa usando sustituciones de texto:
```c++
// Definiciones para tarjeta SD
#define sdCardMOSI 23
#define sdCardMISO 19
#define sdCardClock 18
#define sdCardChipSelect 5
```

**Explicación de las funciones de los pines**    
<table border="1">
  <tr>
    <th>Pin del módulo MicroSD</th>
    <th>Pin ESP32</th>
    <th>Descripción</th>
  </tr>
  <tr>
    <td>3V3</td>
    <td>3.3V</td>
    <td>Alimenta el módulo con 3.3V. El módulo microSD opera a 3.3V y el ESP32 proporciona este voltaje.</td>
  </tr>
  <tr>
    <td>CS</td>
    <td>GPIO 5</td>
    <td>Chip Select (CS): Selecciona la tarjeta SD para comunicación. Cuando CS está en BAJO (LOW), el módulo está activo y listo para comunicarse.</td>
  </tr>
  <tr>
    <td>MOSI</td>
    <td>GPIO 23</td>
    <td>Master Out Slave In (MOSI): Línea de datos donde el maestro (ESP32) envía datos a la tarjeta microSD.</td>
  </tr>
  <tr>
    <td>CLK</td>
    <td>GPIO 18</td>
    <td>Clock (CLK): Proporciona la señal de reloj para sincronizar la transmisión de datos entre el ESP32 y la tarjeta microSD.</td>
  </tr>
  <tr>
    <td>MISO</td>
    <td>GPIO 19</td>
    <td>Master In Slave Out (MISO): Línea de datos donde la tarjeta microSD envía datos al maestro (ESP32).</td>
  </tr>
  <tr>
    <td>GND</td>
    <td>GND</td>
    <td>Tierra (GND): Conecta la tierra del módulo microSD a la tierra del ESP32 para completar el circuito.</td>
  </tr>
</table>
    
### 📍 3 - Probar conexión con script de prueba
El IDE de Arduino incluye varios ejemplos que muestran cómo manejar archivos en tarjetas microSD usando el ESP32. Ve a `Archivo > Ejemplos > SD(esp32) > SD_Test`, o copia el código del script de prueba más abajo.

>[!CAUTION]
> **La tarjeta debe estar formateada en FAT32** y se deben incluir las librerías relevantes. Estas librerías requieren componentes específicos instalados en el IDE de Arduino para funcionar correctamente, especialmente para la interfaz con tarjetas SD y el bus SPI.

#### Script de prueba. 
<details>
  <summary> 👉 Click aquí para mostrar. </summary>
      
   ```cpp
   /*
    * pin 1 - not used          |  Micro SD card     |
    * pin 2 - CS (SS)           |                   /
    * pin 3 - DI (MOSI)         |                  |__
    * pin 4 - VDD (3.3V)        |                    |
    * pin 5 - SCK (SCLK)        | 8 7 6 5 4 3 2 1   /
    * pin 6 - VSS (GND)         | ▄ ▄ ▄ ▄ ▄ ▄ ▄ ▄  /
    * pin 7 - DO (MISO)         | ▀ ▀ █ ▀ █ ▀ ▀ ▀ |
    * pin 8 - not used          |_________________|
    *                             ║ ║ ║ ║ ║ ║ ║ ║
    *                     ╔═══════╝ ║ ║ ║ ║ ║ ║ ╚═════════╗
    *                     ║         ║ ║ ║ ║ ║ ╚══════╗    ║
    *                     ║   ╔═════╝ ║ ║ ║ ╚═════╗  ║    ║
    * Connections for     ║   ║   ╔═══╩═║═║═══╗   ║  ║    ║
    * full-sized          ║   ║   ║   ╔═╝ ║   ║   ║  ║    ║
    * SD card             ║   ║   ║   ║   ║   ║   ║  ║    ║
    * Pin name         |  -  DO  VSS SCK VDD VSS DI CS    -  |
    * SD pin number    |  8   7   6   5   4   3   2   1   9 /
    *                  |                                  █/
    *                  |__▍___▊___█___█___█___█___█___█___/
    *
    * Note:  The SPI pins can be manually configured by using `SPI.begin(sck, miso, mosi, cs).`
    *        Alternatively, you can change the CS pin and use the other default settings by using `SD.begin(cs)`.
    *
    * +--------------+---------+-------+----------+----------+----------+----------+----------+
    * | SPI Pin Name | ESP8266 | ESP32 | ESP32‑S2 | ESP32‑S3 | ESP32‑C3 | ESP32‑C6 | ESP32‑H2 |
    * +==============+=========+=======+==========+==========+==========+==========+==========+
    * | CS (SS)      | GPIO15  | GPIO5 | GPIO34   | GPIO10   | GPIO7    | GPIO18   | GPIO0    |
    * +--------------+---------+-------+----------+----------+----------+----------+----------+
    * | DI (MOSI)    | GPIO13  | GPIO23| GPIO35   | GPIO11   | GPIO6    | GPIO19   | GPIO25   |
    * +--------------+---------+-------+----------+----------+----------+----------+----------+
    * | DO (MISO)    | GPIO12  | GPIO19| GPIO37   | GPIO13   | GPIO5    | GPIO20   | GPIO11   |
    * +--------------+---------+-------+----------+----------+----------+----------+----------+
    * | SCK (SCLK)   | GPIO14  | GPIO18| GPIO36   | GPIO12   | GPIO4    | GPIO21   | GPIO10   |
    * +--------------+---------+-------+----------+----------+----------+----------+----------+
    *
    * For more info see file README.md in this library or on URL:
    * https://github.com/espressif/arduino-esp32/tree/master/libraries/SD
    */
   
   #include "FS.h"
   #include "SD.h"
   #include "SPI.h"
   
   /*
   Uncomment and set up if you want to use custom pins for the SPI communication
   #define REASSIGN_PINS
   int sck = -1;
   int miso = -1;
   int mosi = -1;
   int cs = -1;
   */
   
   void listDir(fs::FS &fs, const char *dirname, uint8_t levels) {
     Serial.printf("Listing directory: %s\n", dirname);
   
     File root = fs.open(dirname);
     if (!root) {
       Serial.println("Failed to open directory");
       return;
     }
     if (!root.isDirectory()) {
       Serial.println("Not a directory");
       return;
     }
   
     File file = root.openNextFile();
     while (file) {<a name="custom_anchor_name"></a>
       if (file.isDirectory()) {
         Serial.print("  DIR : ");
         Serial.println(file.name());
         if (levels) {
           listDir(fs, file.path(), levels - 1);
         }
       } else {
         Serial.print("  FILE: ");
         Serial.print(file.name());
         Serial.print("  SIZE: ");
         Serial.println(file.size());
       }
       file = root.openNextFile();
     }
   }
   
   void createDir(fs::FS &fs, const char *path) {
     Serial.printf("Creating Dir: %s\n", path);
     if (fs.mkdir(path)) {
       Serial.println("Dir created");
     } else {
       Serial.println("mkdir failed");
     }
   }
   
   void removeDir(fs::FS &fs, const char *path) {
     Serial.printf("Removing Dir: %s\n", path);
     if (fs.rmdir(path)) {
       Serial.println("Dir removed");
     } else {
       Serial.println("rmdir failed");
     }
   }
   
   void readFile(fs::FS &fs, const char *path) {
     Serial.printf("Reading file: %s\n", path);
   
     File file = fs.open(path);
     if (!file) {
       Serial.println("Failed to open file for reading");
       return;
     }
   
     Serial.print("Read from file: ");
     while (file.available()) {
       Serial.write(file.read());
     }
     file.close();
   }
   
   void writeFile(fs::FS &fs, const char *path, const char *message) {
     Serial.printf("Writing file: %s\n", path);
   
     File file = fs.open(path, FILE_WRITE);
     if (!file) {
       Serial.println("Failed to open file for writing");
       return;
     }
     if (file.print(message)) {
       Serial.println("File written");
     } else {
       Serial.println("Write failed");
     }
     file.close();
   }
   
   void appendFile(fs::FS &fs, const char *path, const char *message) {
     Serial.printf("Appending to file: %s\n", path);
   
     File file = fs.open(path, FILE_APPEND);
     if (!file) {
       Serial.println("Failed to open file for appending");
       return;
     }
     if (file.print(message)) {
       Serial.println("Message appended");
     } else {
       Serial.println("Append failed");
     }
     file.close();
   }
   
   void renameFile(fs::FS &fs, const char *path1, const char *path2) {
     Serial.printf("Renaming file %s to %s\n", path1, path2);
     if (fs.rename(path1, path2)) {
       Serial.println("File renamed");
     } else {
       Serial.println("Rename failed");
     }
   }
   
   void deleteFile(fs::FS &fs, const char *path) {
     Serial.printf("Deleting file: %s\n", path);
     if (fs.remove(path)) {
       Serial.println("File deleted");
     } else {
       Serial.println("Delete failed");
     }
   }
   
   void testFileIO(fs::FS &fs, const char *path) {
     File file = fs.open(path);
     static uint8_t buf[512];
     size_t len = 0;
     uint32_t start = millis();
     uint32_t end = start;
     if (file) {
       len = file.size();
       size_t flen = len;
       start = millis();
       while (len) {
         size_t toRead = len;
         if (toRead > 512) {
           toRead = 512;
         }
         file.read(buf, toRead);
         len -= toRead;
       }
       end = millis() - start;
       Serial.printf("%u bytes read for %lu ms\n", flen, end);
       file.close();
     } else {
       Serial.println("Failed to open file for reading");
     }
   
     file = fs.open(path, FILE_WRITE);
     if (!file) {
       Serial.println("Failed to open file for writing");
       return;
     }
   
     size_t i;
     start = millis();
     for (i = 0; i < 2048; i++) {
       file.write(buf, 512);
     }
     end = millis() - start;
     Serial.printf("%u bytes written for %lu ms\n", 2048 * 512, end);
     file.close();
   }
   
   void setup() {
     Serial.begin(115200);
   
   #ifdef REASSIGN_PINS
     SPI.begin(sck, miso, mosi, cs);
     if (!SD.begin(cs)) {
   #else
     if (!SD.begin()) {
   #endif
       Serial.println("Card Mount Failed");
       return;
     }
     uint8_t cardType = SD.cardType();
   
     if (cardType == CARD_NONE) {
       Serial.println("No SD card attached");
       return;
     }
   
     Serial.print("SD Card Type: ");
     if (cardType == CARD_MMC) {
       Serial.println("MMC");
     } else if (cardType == CARD_SD) {
       Serial.println("SDSC");
     } else if (cardType == CARD_SDHC) {
       Serial.println("SDHC");
     } else {
       Serial.println("UNKNOWN");
     }
   
     uint64_t cardSize = SD.cardSize() / (1024 * 1024);
     Serial.printf("SD Card Size: %lluMB\n", cardSize);
   
     listDir(SD, "/", 0);
     createDir(SD, "/mydir");
     listDir(SD, "/", 0);
     removeDir(SD, "/mydir");
     listDir(SD, "/", 2);
     writeFile(SD, "/hello.txt", "Hello ");
     appendFile(SD, "/hello.txt", "World!\n");
     readFile(SD, "/hello.txt");
     deleteFile(SD, "/foo.txt");
     renameFile(SD, "/hello.txt", "/foo.txt");
     readFile(SD, "/foo.txt");
     testFileIO(SD, "/test.txt");
     Serial.printf("Total space: %lluMB\n", SD.totalBytes() / (1024 * 1024));
     Serial.printf("Used space: %lluMB\n", SD.usedBytes() / (1024 * 1024));
   }
   
   void loop() {}
   
   ```
</details> 

    
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

