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


# 📟 Codificación de Agentes Remotos
En esta sección se presenta la descripción, codificación y diagramas de flujo que ilustran la lógica empleada en el funcionamiento de los agentes remotos.

## 📌 void setup() 
Esta función `setup` se ejecuta una vez al iniciar el dispositivo para realizar la configuración inicial necesaria.

**Funcionamiento**

- Inicializa la comunicación serial a 115200 baudios y espera que esté lista.  
- Configura los pines de los LEDs indicadores y los apaga por defecto.  
- Inicializa el sistema de archivos LittleFS y la tarjeta SD, con una pausa breve entre procesos.  
- Si la SD está inicializada, compara y sincroniza archivos de configuración y de identificación del proyector entre SD y almacenamiento local, luego desmonta la SD y apaga su LED indicador.  
- Carga las configuraciones y la información del proyector desde los archivos locales.  
- Se conecta a la red WiFi con los datos cargados.  
- Sincroniza el reloj interno con un servidor NTP.  
- Inicializa el puerto RS232 con la configuración adecuada.  
- Solicita al servidor la cadena de consulta del estado de la lámpara.  
- Realiza una consulta inicial del estado del proyector para preparar futuras tareas.  

    
<details>
  <summary> <h2>👉🖱️ Mostrar código función.</h2> </summary>
  
```cpp
// ---------------------------------------------
// Setup Function (Runs Once at Startup)
// ---------------------------------------------
void setup() {

    // -------------------------------
    // Initialize Serial Monitor
    // -------------------------------
    Serial.begin(115200);
    while (!Serial) { ; }  // Wait for Serial to be ready

    printInterfaceSentenceBox("INFO: Serial monitor connected.");

    // -------------------------------
    // Initialize LED Status Indicators
    // -------------------------------
    pinMode(offlinePin, OUTPUT);
    pinMode(onlinePin, OUTPUT);
    pinMode(littleFSLed, OUTPUT);
    pinMode(sdFSLed, OUTPUT);

    // Set default LED states
    digitalWrite(offlinePin, LOW);
    digitalWrite(onlinePin, LOW);
    digitalWrite(littleFSLed, LOW);
    digitalWrite(sdFSLed, LOW);

    // -------------------------------
    // Initialize Internal File System (LittleFS)
    // -------------------------------
    littleFSInitialized = initializeLittleFS();

    delay(200);

    // -------------------------------
    // Initialize SD Card
    // -------------------------------
    sdCardInitialized = initializeSDCard();

    delay(200);

    // If the SD Card is initialized then procceed to file comparisons.
    if (sdCardInitialized) {
        // -------------------------------
        // Compare and copy the most recent Configuration File
        // -------------------------------
        compareAndCopy(wifiConfigFilePath, wifiConfigMetadataFilePath, "Configuration File");

        // -------------------------------
        // Compare and copy the most recent Projector Idenfity File
        // -------------------------------
        compareAndCopy(projectorClassroomFilePath, projectorClassroomMetadataFilePath, "Projector Identity File");

        // Unmount the SD card after processing.
        SD.end();
        // Turn SD card indicator off.
        digitalWrite(sdFSLed, LOW);
    }

    delay(200);

    // -------------------------------
    // Load configuration from files.
    // -------------------------------

    delay(200);

    // Loads the configuration stored in the file of the local FS.
    loadConfigFromFile(wifiConfigFilePath);

    delay(200);

    // Loads the projector identification string in the file of the local FS
    loadProjectorInfoFromFile(projectorClassroomFilePath);

    delay(200);

    // -------------------------------
    // Connect to WiFi
    // -------------------------------
    connectToWifi();

    delay(200);

    // -------------------------------
    // Configure device RTC block (needs network)
    // -------------------------------
    syncTimeToNtpServer();

    delay(200);

    // -------------------------------
    // Initializing RS232 port.
    // -------------------------------
    MySerial.begin(RS232BaudRate, SERIAL_8N1, rxRS232Port, txRS232Port);  // baudrate, config, RX pin, TX pin

    // -------------------------------
    // Ask the server for the lamp status inquiry command.
    // Wait half a second after the function completes, try again if failed.
    // -------------------------------
    getLampStatusInquiryCommand();

    // Initial lamp status inquiry for first task request params.
    getProjectorStatus();  // ---------------------------------------------------------------------------------------  TODO comprobar si aqui la recoge con el proyector.



    // -------------------------------
    // End of Setup
    // -------------------------------
}
```
</details>

>[!CAUTION]
>POR REPRESENTAR
    
---
    
## 📌 void loop() 
Esta función `loop` se ejecuta continuamente y maneja diferentes comportamientos según el modo de operación (`RUNMODE`).

**Funcionamiento**

- Si `RUNMODE` es 1 (modo debug):  
  - Escucha entradas por puerto serial para ejecutar comandos:  
    - `'r'`: reinicia la placa.  
    - `'v'`: muestra los valores almacenados de variables importantes.  
    - `'t'`: fuerza una consulta al servidor para obtener tareas e instrucciones, las procesa y envía la instrucción al puerto serie, luego actualiza el evento en la base de datos.  
    - `'u'`: actualiza el evento con un código fijo.  
    - `'l'`: obtiene y muestra el estado del proyector.  

- Si `RUNMODE` no es 1 (modo normal):  
  - Ejecuta automáticamente la consulta al servidor, procesa la respuesta igual que en modo debug para enviar instrucciones y actualizar eventos.  
  - Espera 20 segundos antes de repetir el ciclo.

En ambos modos, se gestionan las respuestas del servidor para extraer el ID del evento y la instrucción a ejecutar, transmitiéndola por puerto serie y actualizando el registro correspondiente.

    
<details>
  <summary> <h2>👉🖱️ Mostrar código función.</h2> </summary>
  
```cpp
// ---------------------------------------------
// Loop Function (Runs Continuously)
// ---------------------------------------------
void loop() {
    // Listen for serial input to trigger a reboot

    // If the board is in debug mode, it will wait for instructions and display debug messages..
    if (RUNMODE == 1) {
        if (Serial.available()) {
            char received = Serial.read();

            // Order to reboot the board.
            if (received == 'r') {
                printInterfaceSentenceBox("Rebooting now...");
                esp_restart();
            }

            // Prints the stored values of all the variables.
            if (received == 'v') {
                printInterfaceTitle("SHOWING STORED VARIABLES VALUES.");
                printInterfaceSentences("WifiSSID - ", WifiSSID);
                printInterfaceSentences("WifiPassword - ", WifiPassword);
                printInterfaceSentences("urlProjectors - ", urlProjectors);
                printInterfaceSentences("urlFirebase - ", urlFirebase);
                printInterfaceSentences("xClientId - ", xClientId);
                printInterfaceSentences("projectorClassroom - ", projectorClassroom);
                printInterfaceSentences("lampStatusInquireCommand - ", lampStatusInquireCommand);
                printInterfaceSentences("lampStatus - ", lampStatus);
                printInterfaceSentences("eventId - ", eventId);
                printInterfaceSentences("commandInstruction - ", commandInstruction);
                printInterfaceBottomLine();
            }

            // Inquires for tasks.
            if (received == 't') {

                // -------------------------------------------------
                // FORCE SEND REQUEST TO SERVER
                // -------------------------------------------------
                printInterfaceTitle("Inquiring for tasks...");

                // Reset the variables.
                responseCode = 0;
                responseData = "";

                getProjectorStatus();  // Recovers the lamp status.  // ----------------------------------------------------------   obtener el estado TODO
                //lampStatus = "Lamp 1";  // -------------------------------------------------------------------------------------------  BORRAR

                printInterfaceSentences("Lamp current status: ", lampStatus);

                // Uses the value of lamp status to update projector status.
                callServer(responseCode, responseData);

                // If the response is OKAY.
                if (responseCode == 200) {

                    // -------------------------------------------------
                    // VALUES EXTRACTION FROM JSON RESPONSE
                    // -------------------------------------------------

                    // Saca el ID del evento
                    eventId = "UNSET";  // Reinicia valores.
                    eventId = getValueFromString(responseData, "eventId");

                    // Saca la instrucción.
                    commandInstruction = "UNSET";  // Reinicia valores.
                    commandInstruction = getValueFromString(responseData, "commandInstruction");

                    // -------------------------------------------------
                    // CONVERSION DE CADENA A ARRAY HEXA
                    // -------------------------------------------------

                    if (commandInstruction.length() > 0) {

                        // -------------------------------------------------
                        // TRANSMISION DE LOS VALORES AL PUERTO SERIE.
                        // -------------------------------------------------

                        String deviceResponseCode = "";

                        deviceResponseCode = writeToSerialPort(commandInstruction);  // Importante recibir en formato texto. No hexadecimal.

                        // -------------------------------------------------
                        // Actualización del registro en BBDD con el resultado de la respuestaOrden.
                        // -------------------------------------------------
                        updateEvent(eventId, deviceResponseCode);
                        //updateEvent(eventId, "*000");


                    } else {
                        printInterfaceSentence("WARNING: Code 200 but no valid instruction received.");
                    }
                }

                if (responseCode != 200) {
                    printInterfaceSentence("WARNING: HTTP Response code: " + String(responseCode));
                }
                printInterfaceBottomLine();
            }

            if (received == 'u') {
                updateEvent(eventId, "*000");
            }
            if (received == 'l') {
                getProjectorStatus();
                debugln(lampStatus);
            }
        }
    }
    // -------------------------------------------------
    // Block that runs when the board is NOT in DEBUG mode.
    // -------------------------------------------------
    else {
        // -------------------------------------------------
        // FORCE SEND REQUEST TO SERVER
        // -------------------------------------------------
        printInterfaceTitle("Inquiring for tasks...");

        // Reset the variables.
        responseCode = 0;
        responseData = "";

        getProjectorStatus();  // Recovers the lamp status.  // ----------------------------------------------------------   obtener el estado TODO
        //lampStatus = "Lamp 1";  // -------------------------------------------------------------------------------------------  BORRAR

        printInterfaceSentences("Lamp current status: ", lampStatus);

        // Uses the value of lamp status to update projector status.
        callServer(responseCode, responseData);

        // If the response is OKAY.
        if (responseCode == 200) {

            // -------------------------------------------------
            // VALUES EXTRACTION FROM JSON RESPONSE
            // -------------------------------------------------

            // Saca el ID del evento
            eventId = "UNSET";  // Reinicia valores.
            eventId = getValueFromString(responseData, "eventId");

            // Saca la instrucción.
            commandInstruction = "UNSET";  // Reinicia valores.
            commandInstruction = getValueFromString(responseData, "commandInstruction");

            // -------------------------------------------------
            // CONVERSION DE CADENA A ARRAY HEXA
            // -------------------------------------------------

            if (commandInstruction.length() > 0) {

                // -------------------------------------------------
                // TRANSMISION DE LOS VALORES AL PUERTO SERIE.
                // -------------------------------------------------

                String deviceResponseCode = "";

                deviceResponseCode = writeToSerialPort(commandInstruction);  // Importante recibir en formato texto. No hexadecimal.

                // -------------------------------------------------
                // Actualización del registro en BBDD con el resultado de la respuestaOrden.
                // -------------------------------------------------
                updateEvent(eventId, deviceResponseCode);
                //updateEvent(eventId, "*000");


            } else {
                printInterfaceSentence("WARNING: Code 200 but no valid instruction received.");
            }
        }

        if (responseCode != 200) {
            printInterfaceSentence("WARNING: HTTP Response code: " + String(responseCode));
        }
        printInterfaceBottomLine();

        delay(20000);
    }
}
```
</details>

### Loop en modo de Pruebas (RUNMODE == 1)
![imagen](https://github.com/user-attachments/assets/f0f24be1-01db-4d11-a287-f9e3bca67d89)

### Loop en modo de producción (RUNMODE == 0)
![imagen](https://github.com/user-attachments/assets/432f637d-1270-497a-a5dc-79f9807cdea6)

    
---
    
## 📌 bool beginWithRetry(HTTPClient& http, const String& url, int maxRetries = 5)
Esta función intenta iniciar una conexión HTTP con reintentos en caso de fallo.

**Funcionamiento**

- Recibe una referencia al objeto `HTTPClient`, una URL y un número máximo de reintentos (por defecto 5).  
- Intenta iniciar la conexión HTTP con `http.begin(url)`.  
- Si la conexión es exitosa, retorna `true`.  
- Si falla, imprime un mensaje indicando el intento fallido y espera 500 ms antes de reintentar.  
- Repite hasta alcanzar el máximo de reintentos.  
- Si no se logra conectar tras todos los intentos, imprime un mensaje de error y retorna `false`.

    
```cpp
// Funcion que permite reintentar una conexión por si falla.
bool beginWithRetry(HTTPClient& http, const String& url, int maxRetries = 5) {
    int attempts = 0;
    while (attempts < maxRetries) {
        if (http.begin(url)) {
            return true;
        }
        printInterfaceSentences("Intento fallido al iniciar conexión HTTP. Reintentando... intento ", String(attempts + 1));
        attempts++;
        delay(500);  // Espera entre reintentos
    }

    printInterfaceSentence("ERROR: No se pudo establecer conexión HTTP después de varios intentos.");
    return false;
}
```

![beginWithRetries](https://github.com/user-attachments/assets/18aa91a4-c0e6-4dea-a692-db73357c94c5)

    
---
    
## 📌 bool initializeLittleFS() 
Esta función intenta montar el sistema de archivos LittleFS; si falla, formatea y vuelve a intentar montar.

**Funcionamiento**

- Imprime la línea superior de la interfaz.  
- Intenta montar LittleFS con `LittleFS.begin()`.  
- Si falla, imprime un error y trata de formatear y montar nuevamente con `LittleFS.begin(true)`.  
- Si el formateo falla, imprime un error y retorna `false`.  
- Si el formateo es exitoso, imprime una advertencia informando que se formateó correctamente.  
- Si el montaje (inicial o tras formateo) es exitoso, imprime un mensaje de éxito y enciende el LED indicador de LittleFS.  
- Imprime la línea inferior de la interfaz.  
- Retorna `true` si LittleFS fue montado o formateado con éxito.

    
```cpp
// ---------------------------------------------
// Attempts to mount LittleFS; formats and retries if initial mount fails.
// Returns true if successful, and activates the status LED.
// ---------------------------------------------
bool initializeLittleFS() {

    printInterfaceTopLine();

    if (!LittleFS.begin()) {
        printInterfaceSentence("ERROR: Failed to mount LittleFS. Attempting format...");

        // Attempt to format and mount
        if (!LittleFS.begin(true)) {
            printInterfaceSentence("ERROR: LittleFS formatting failed.");
            return false;  // Return false if formatting also fails
        } else {
            printInterfaceSentence("WARNING: LittleFS formatted successfully.");
        }
    }

    printInterfaceSentence("SUCCESS: LittleFS mounted successfully.");
    digitalWrite(littleFSLed, HIGH);  // Turn on LittleFS LED



    printInterfaceBottomLine();

    return true;  // Return true if successfully mounted or formatted
}
```


![initializeLittleFS](https://github.com/user-attachments/assets/c837ccba-d5f5-47f1-8db2-958db3ac0292)
    
---
    
## 📌 String getConfigParamsFromServer() 
Esta función realiza una solicitud HTTP para obtener parámetros de configuración desde un servidor para un proyector específico.

**Funcionamiento**

- Verifica que las variables globales `urlProjectors`, `urlFirebase` y `xClientId` estén definidas; si alguna falta, imprime un error y termina la función.  
- Comprueba que el dispositivo esté conectado a WiFi; si no, imprime un error y termina la función.  
- Imprime mensajes en la interfaz informando sobre la solicitud de token JWT.  
- Obtiene un token JWT llamando a `getJwt()`.  
- Si no se obtiene el token, imprime error y termina la función.  
- Construye la URL completa para la petición usando `urlProjectors` y el identificador de aula `projectorClassroom`.  
- Intenta abrir la conexión HTTP varias veces con `beginWithRetry()`. Si falla, termina la función.  
- Añade el encabezado de autorización con el token JWT.  
- Envía la petición HTTP GET y captura el código de respuesta.  
- Si la respuesta es exitosa (códigos 200-299):  
  - Imprime mensajes de éxito y muestra parte de la respuesta recibida.  
  - Si la respuesta es 204 (sin contenido), muestra una advertencia.  
- Si la respuesta indica error, imprime el código HTTP de error.  
- Libera recursos con `http.end()`.  
- Devuelve la cadena con la respuesta del servidor (JSON u otra) o cadena vacía en caso de fallo.

    
```cpp

// ---------------------------------------------
// Retrieves configuration parameters from the server for a specific projector.
// ---------------------------------------------
String getConfigParamsFromServer() {

    // ---------------------------------------------
    // PRELIMINARY VALIDATIONS
    // ---------------------------------------------
    if (urlProjectors.length() == 0) {
        printInterfaceSentence("ERROR: 'urlProjectors' is not set. Aborting request.");
        return "";
    }

    if (urlFirebase.length() == 0) {
        printInterfaceSentence("ERROR: 'urlFirebase' is not set. Aborting request.");
        return "";
    }

    if (xClientId.length() == 0) {
        printInterfaceSentence("ERROR: 'xClientId' is not set. Aborting request.");
        return "";
    }

    if (WiFi.status() != WL_CONNECTED) {
        printInterfaceSentence("ERROR: No WiFi connection. Aborting request.");
        return "";
    }

    // ---------------------------------------------
    // REQUEST SETUP
    // ---------------------------------------------

    printInterfaceSeparator();
    printInterfaceSentence("REQUESTING JWT.");
    printInterfaceSeparator();

    String authToken = getJwt();

    HTTPClient http;         // HTTP client for the request.
    http.setTimeout(20000);  // configura una ventana de tiempo mas amplia para la respuesta.

    // Reinicia las variables referenciadas.
    String httpResponseData = "";
    int httpResponseCode = 0;

    if (authToken.length() == 0) {
        printInterfaceSentence("ERROR: Cannot get JWT from the server during SIQ query.");
        return "";
    }

    delay(200);

    printInterfaceSeparator();
    printInterfaceSentence("CONFIG PARAMS REQUEST TO SERVER.");
    printInterfaceSeparator();

    String completeURL = urlProjectors + "/config-params?projectorClassroom=" + projectorClassroom;  // Configures the complete request URL.

    printInterfaceSentence("Request details: ");
    printInterfaceSentences("-- Server Address: ", urlProjectors);
    printInterfaceSentences("-- Specific endpoint: ", "/config-params");
    printInterfaceSentences("-- Projector ID: ", projectorClassroom);
    printInterfaceSeparator();

    //http.begin(completeURL);

    // Llamada a funcion que intenta varias veces la conexión controlando resultado.
    if (!beginWithRetry(http, completeURL)) {
        return "";  // Falló incluso tras varios intentos
    }

    // uso temporal hasta que se pruebe el certificado.
    String bearer = "Bearer " + authToken;  // Authorization header.
    http.addHeader("Authorization", bearer);

    httpResponseCode = http.GET();  // Send the request.

    if (httpResponseCode >= 200 && httpResponseCode < 300) {

        printInterfaceSentence("CONFIG PARAMS: Response received.");

        printInterfaceSentences("- HTTP Response Code:", String(httpResponseCode));

        if (httpResponseCode == 204) {

            printInterfaceSentence("WARNING: No Content in response body.");

        } else {
            httpResponseData = http.getString();
            printInterfaceSentence("- Server Response:");
            String sub = String(httpResponseData).substring(0, debugInterfaceLength - 7);
            printInterfaceSentences("-- ", String(sub));

            printInterfaceSeparator();
            printInterfaceSentence("CONFIG PARAMS: Server inquiry process completed.");
            printInterfaceSeparator();
        }

    } else {
        printInterfaceSeparator();
        printInterfaceSentences("CONFIG PARAMS ERROR: HTTP request failed. Code: ", String(httpResponseCode));
        printInterfaceSeparator();
    }

    http.end();  // Free memory resources.

    return httpResponseData;
}

```

<!-- ![getConfigParamsFromServer](https://github.com/user-attachments/assets/9eb52863-4c57-4778-bc63-99515a1af00b) -->

![getConfigParamsFromServer](https://github.com/user-attachments/assets/45655030-65dc-4895-b91b-04d4cb5087fb)

    
---
    
## 📌 void getLampStatusInquiryCommand() 
Esta función solicita al servidor la instrucción para consultar el estado de la lámpara (lamp status) y la almacena en una variable global. Extrae la cadena desde la respuesta JSON recibida.
   
**Funcionamiento**

- Inicializa una cadena vacía `jsonResponse` para almacenar la respuesta del servidor.  
- Imprime un título informativo en la interfaz.  
- Llama a `getConfigParamsFromServer()` para obtener la respuesta JSON del servidor.  
- Si la respuesta está vacía, imprime un mensaje de error y finaliza la función.  
- Extrae el valor asociado a la clave `"command"` desde `jsonResponse` y lo asigna a la variable global `lampStatusInquireCommand`.  
- Si el valor extraído está vacío, imprime un mensaje de error.  
- Si la extracción fue exitosa, imprime la instrucción obtenida.  
- Imprime una línea final decorativa en la interfaz.

    
```cpp
// ---------------------------------------------
// Sends a request to the server to retrieve the lamp status command.
// Calls getConfigParamsFromServer() to obtain a JSON response,
// extracts the 'command' field containing the instruction string,
// ---------------------------------------------
void getLampStatusInquiryCommand() {

    int retries = 0;
    const int maxRetries = 5;

    String jsonResponse = "";  // Stores the json response from the server.

    printInterfaceTitle("Status Inquiry Command request.");

    // Recupera la respuesta del servidor para sacar luego la cadena equivalente a la instruccion de SIC
    jsonResponse = getConfigParamsFromServer();

    // Check if the response is empty.
    if (jsonResponse.length() == 0) {
        printInterfaceSentence("ERROR: Empty SIC jsonResponse string.");
        printInterfaceBottomLine();
        return;
    }

    // Extract from jsonResponse the value of key 'command': contains instruction string.
    lampStatusInquireCommand = getValueFromString(jsonResponse, "command");

    if (lampStatusInquireCommand.length() == 0) {
        printInterfaceSentenceBox("ERROR: Failed to retrieve lamp status command after multiple attempts.");
    } else {
        printInterfaceSentences("SUCCESS: Instruction retreived: ", lampStatusInquireCommand);
    }
    printInterfaceBottomLine();
}
```
   
![getLampStatusInquiryCommand](https://github.com/user-attachments/assets/a77f9cb1-51cf-4ff4-b71e-7608ec7e60e0)
       
---
    
## 📌 bool initializeSDCard()
Esta función inicializa la tarjeta SD utilizando la interfaz SPI y verifica su accesibilidad intentando abrir su directorio raíz. Retorna `true` si la operación es exitosa; de lo contrario, imprime mensajes de error y retorna `false`.

**Funcionamiento**

- Inicializa la comunicación SPI con los pines configurados para la tarjeta SD.  
- Intenta montar la tarjeta SD con `SD.begin()`.  
  - Si falla, imprime una advertencia y retorna `false`.  
- Si el montaje es exitoso, imprime un mensaje de confirmación.  
- Intenta abrir el directorio raíz (`/`) de la tarjeta SD para validar su accesibilidad.  
  - Si falla, imprime un mensaje de error, desmonta la tarjeta SD y retorna `false`.  
- Si el acceso es exitoso, imprime mensaje de éxito.  
- Enciende un LED indicador del estado de la tarjeta SD.  
- Retorna `true` indicando que la tarjeta fue inicializada correctamente.

    
```cpp
// ---------------------------------------------
// Initializes the SD card via SPI and checks for accessibility by opening its root directory.
// Returns true if successful; otherwise, prints errors and returns false.
// ---------------------------------------------
bool initializeSDCard() {

    SPI.begin(sdCardClock, sdCardMISO, sdCardMOSI, sdCardChipSelect);

    printInterfaceTopLine();

    if (!SD.begin(sdCardChipSelect)) {
        printInterfaceSentence("WARNING: No SD card detected.");
        printInterfaceBottomLine();
        return false;  // Return false if SD card is not detected
    }

    printInterfaceSentence("SUCCESS: SD Card mounted successfully.");

    // Verify SD accessibility by opening root directory
    File testFile = SD.open("/");
    if (!testFile) {
        printInterfaceSentence("ERROR: SD Card not accessible.");
        printInterfaceBottomLine();
        SD.end();      // SD Close
        return false;  // Return false if SD card is not accessible
    }
    testFile.close();

    printInterfaceSentence("SUCCESS: SD Card is accessible.");
    printInterfaceBottomLine();

    // Turn on SD card LED indicator
    digitalWrite(sdFSLed, HIGH);
    delay(1000);

    return true;  // Return true if SD card is successfully initialized
}

```

![initializeSdCard](https://github.com/user-attachments/assets/a703dbd3-976a-41f0-8916-253b415809a1)
    
---
    
## 📌 void compareAndCopy(String filePath, String metadataFilePath, String fileName)
Esta función verifica la existencia de un archivo tanto en el almacenamiento local (LittleFS) como en una tarjeta SD, compara sus fechas de modificación y copia el archivo desde la SD al almacenamiento local si el archivo en la SD es más reciente o si el archivo local no existe.

**Funcionamiento**

- Comprueba si el archivo existe localmente y en la tarjeta SD.  
- Imprime mensajes informativos sobre la existencia de los archivos en ambas ubicaciones.  
- Si el archivo local existe:  
  - Si también existe en la SD:  
    - Abre el archivo en la SD para obtener su fecha de última modificación.  
    - Lee la fecha de última modificación almacenada localmente.  
    - Compara ambas fechas e imprime sus valores.  
    - Si el archivo en la SD es más reciente, sobrescribe el archivo local copiándolo desde la SD.  
    - Si el archivo local es más reciente o tienen la misma fecha, no realiza cambios.  
- Si el archivo local no existe pero sí está en la SD, copia el archivo desde la SD al almacenamiento local.  
- Si el archivo no existe en ninguna ubicación, muestra una advertencia.  
- Finaliza con mensajes de estado y cierra los archivos abiertos.

    
```cpp
// ---------------------------------------------
// Checks if a file exists locally and on the SD card, compares their timestamps,
// and copies from SD to local if the SD file is newer or local is missing.
// ---------------------------------------------
void compareAndCopy(String filePath, String metadataFilePath, String fileName) {

    bool localFileExists = LittleFS.exists(filePath);
    bool sdFileExists = SD.exists(filePath);
    printInterfaceTitle(fileName);

    String localSentence = localFileExists ? "SUCCESS: Local " + fileName + " found." : "WARNING: Local" + fileName + " NOT FOUND.";

    printInterfaceSentence(localSentence);

    String externalSentence = sdFileExists ? "SUCCESS: External " + fileName + " found." : "WARNING: External" + fileName + " NOT FOUND.";

    printInterfaceSentence(externalSentence);

    // CASE 1: Local config file exists
    if (localFileExists) {
        // ... now checking SD card.
        if (sdFileExists) {
            printInterfaceSentence("- Comparing " + fileName + " timestamps...");

            // Open both the SD files to compare modification timestamps.
            File sdFile = SD.open(filePath, "r");

            if (!sdFile) {
                printInterfaceSentence("ERROR: Failed to open " + fileName + " from SD Card.");
                sdFile.close();  // Close the local file if SD file failed to open.
                return;          // return here to avoid further code execution
            }

            // Get modification timestamps for both files.
            unsigned long localFileModStamp = readTimestampFromFile(metadataFilePath);
            unsigned long sdFileModStamp = sdFile.getLastWrite();

            // Print Local config file last modification time
            String newSentence = "  * Local file last modified: " + getTimestampAsDate(localFileModStamp);
            printInterfaceSentence(newSentence);

            // Print SD config file last modification time
            String newSentence2 = "  * External file last modified: " + getTimestampAsDate(sdFileModStamp);
            printInterfaceSentence(newSentence2);

            // Close file after reading timestamps.
            sdFile.close();

            // CASE 1A: SD certificate is newer -> Overwrite local copy
            if (sdFileModStamp > localFileModStamp) {
                printInterfaceSeparator();
                printInterfaceSentence("RESULT: External " + fileName + " is more recent. Overwriting local copy.");
                copyFileFromSDToLocalFS(filePath, metadataFilePath, fileName);
            }
            // CASE 1B: Local certificate is already the latest -> No update needed
            else if (sdFileModStamp < localFileModStamp) {
                printInterfaceSeparator();
                printInterfaceSentence("RESULT: Local " + fileName + " is more recent. No changes applied.");
            }
            // CASE 1C: Both certificates have the same timestamp -> No action needed
            else {
                printInterfaceSeparator();
                printInterfaceSentence("RESULT: Local '" + fileName + "' is up to date. No changes applied.");
            }
        }
    }
    // CASE 2: Local certificate does not exist -> Copy from SD if available
    else {
        if (sdFileExists) {
            printInterfaceSentence("INFO: No local " + fileName + " found. Copying " + fileName + " from SD card...");
            copyFileFromSDToLocalFS(filePath, metadataFilePath, fileName);
        }
        // CASE 3: No certificates available in either location -> Warning
        else {
            printInterfaceSentence("WARNING: No " + fileName + " found in local storage nor SD card!.");
        }
    }

    printInterfaceBottomLine();
    debugln();
}
```
   
![compareAndCopy](https://github.com/user-attachments/assets/b602249c-39f8-4e1d-b19b-e2ddaee7fc60)
    
---
    
## 📌 void loadConfigFromFile(String configFilePath)
Esta función lee configuración de WiFi y servidor desde un archivo en LittleFS línea por línea y asigna valores a variables globales.

**Funcionamiento**    

- Abre el archivo ubicado en `configFilePath` para lectura.  
- Si no puede abrir el archivo, imprime un mensaje de error y termina la función.  
- Mientras haya líneas por leer:  
  - Lee cada línea hasta el salto de línea y elimina espacios en blanco al inicio y final.  
  - Si la línea empieza con `"SSID="`, extrae el valor que sigue y lo asigna a la variable global `WifiSSID`.  
  - Si la línea empieza con `"PASSWORD="`, extrae el valor que sigue y lo asigna a la variable global `WifiPassword`.  
  - Si la línea empieza con `"URL_PROJECTORS="`, extrae el valor que sigue y lo asigna a la variable global `urlProjectors`.  
  - Si la línea empieza con `"URL_FIREBASE="`, extrae el valor que sigue y lo asigna a la variable global `urlFirebase`.  
- Cierra el archivo después de leer todo.  

    
```cpp
// ---------------------------------------------
// Reads WiFi and server settings from a config file on LittleFS line by line.
// Extracts and sets SSID, password, and server address variables.
// ---------------------------------------------
void loadConfigFromFile(String configFilePath) {

    File configFile = LittleFS.open(configFilePath, "r");

    if (!configFile) {
        printInterfaceSentence("ERROR: Failed to open Configuration file for reading.");
        return;  // return here to avoid further code execution
    }

    while (configFile.available()) {
        String line = configFile.readStringUntil('\n');
        line.trim();

        if (line.startsWith("SSID=")) {
            WifiSSID = line.substring(5);
        } else if (line.startsWith("PASSWORD=")) {
            WifiPassword = line.substring(9);
        } else if (line.startsWith("URL_PROJECTORS=")) {
            urlProjectors = line.substring(15);
        } else if (line.startsWith("URL_FIREBASE=")) {
            urlFirebase = line.substring(13);
        }
    }
    configFile.close();
}
```

![loadConfigFromFile](https://github.com/user-attachments/assets/66c38e3a-9556-490d-b9c8-e1ffc18c6cf6)
    
---
    
## 📌 void loadProjectorInfoFromFile(String projectorFilePath)
Esta función lee información de un proyector desde un archivo en LittleFS línea por línea y asigna valores a variables globales.

**Funcionamiento**    

- Abre el archivo ubicado en `projectorFilePath` para lectura.  
- Si no puede abrir el archivo, imprime un mensaje de error y termina la función.  
- Mientras haya líneas por leer:  
  - Lee cada línea hasta el salto de línea y elimina espacios en blanco al inicio y final.  
  - Si la línea empieza con `"projectorClassroom="`, extrae el valor que sigue y lo asigna a la variable global `projectorClassroom`.  
  - Si la línea empieza con `"x-client-id="`, extrae el valor que sigue y lo asigna a la variable global `xClientId`.  
- Cierra el archivo después de leer todo.  

    
```cpp
// ---------------------------------------------
// Reads projector classroom and client ID info from a file on LittleFS.
// Sets global variables 'projectorClassroom' and 'xClientId'.
// ---------------------------------------------
void loadProjectorInfoFromFile(String projectorFilePath) {
    File file = LittleFS.open(projectorFilePath, "r");
    if (!file) return;

    while (file.available()) {
        String line = file.readStringUntil('\n');
        line.trim();

        if (line.startsWith("projectorClassroom=")) {
            projectorClassroom = line.substring(19);
        } else if (line.startsWith("x-client-id=")) {
            xClientId = line.substring(12);
        }
    }
    file.close();
}
```
   
![loadProjectorInfoFromFile](https://github.com/user-attachments/assets/32155565-ada4-43b9-9db7-32beb1113e0b)
       
---
    
## 📌 void connectToWifi()
Esta función intenta conectar el dispositivo a una red WiFi y proporciona retroalimentación visual y textual sobre el estado de la conexión.

**Funcionamiento**    

- Muestra mensajes de inicio de conexión en la interfaz.  
- Intenta conectarse a la red WiFi usando SSID y, si está disponible, la contraseña.  
- Realiza hasta 20 intentos (aprox. 5 segundos) para establecer la conexión, durante los cuales parpadea un LED indicando intento de conexión.  
- Si la conexión es exitosa:  
  - Enciende un LED indicando conexión en línea y apaga el LED de desconexión.  
  - Muestra en la interfaz la dirección IP y la dirección MAC del dispositivo.  
- Si falla la conexión después de los intentos:  
  - Deja encendido el LED de desconexión y muestra un mensaje de error indicando modo offline.  
- Finalmente imprime líneas y separadores para organización visual en la interfaz.  

    
```cpp
// ---------------------------------------------
// Attempts to connect to a WiFi network and provides visual and textual feedback.
// Turns on the appropriate status LED based on connection success or failure.
// ---------------------------------------------
void connectToWifi() {

    printInterfaceTopLine();
    printInterfaceSentence("INFO: Connecting to WiFi...");
    debug("│ ");

    // Conectar sin password mediante filtrado mac.
    if (WifiPassword.length() == 0) {
        WiFi.begin(WifiSSID.c_str());
    } else {
        WiFi.begin(WifiSSID.c_str(), WifiPassword.c_str());
    }

    int attempt = 0;
    const int maxAttempts = 20;  // 20 attempts (~5 sec)

    debug("");

    while (WiFi.status() != WL_CONNECTED && attempt < maxAttempts) {
        digitalWrite(offlinePin, HIGH);
        debug("█");
        delay(250);
        digitalWrite(offlinePin, LOW);
        delay(250);
        attempt++;
    }

    printRepeatedChar("▒", debugInterfaceLength - attempt - 4);
    debugln(" │");
    printInterfaceSeparator();

    if (WiFi.status() == WL_CONNECTED) {
        digitalWrite(offlinePin, LOW);
        digitalWrite(onlinePin, HIGH);
        printInterfaceSentence("SUCCESS: WiFi connected.");
        printInterfaceSentences("- IP Address: ", WiFi.localIP().toString());
        printInterfaceSentences("- MAC Address: ", WiFi.macAddress());
    } else {
        printInterfaceSentence("ERROR: WiFi connection failed. Running in offline mode.");
    }

    printInterfaceBottomLine();
}
```

![connectToWifi](https://github.com/user-attachments/assets/73f2e1a9-aa19-4712-85c4-d9515d5b01f2)
    
---
    
## 📌 void setFileContentToStringVariable(String& variable, const String& filePath, const String& fileName)
Esta función lee el contenido completo de un archivo y lo asigna a una variable `String`, eliminando espacios y saltos de línea.  

**Funcionamiento**    

- Abre el archivo especificado en modo lectura usando LittleFS.  
- Si no puede abrir el archivo o es un directorio, imprime un mensaje de error y termina la función.  
- Lee todo el contenido del archivo y lo asigna a la variable pasada por referencia.  
- Aplica `trim()` para eliminar espacios y saltos de línea al inicio y final del texto.  
- Cierra el archivo para liberar recursos.  
- Imprime un mensaje de éxito mostrando el contenido cargado.  

    
```cpp
// ---------------------------------------------
// Reads entire content of a file into a given String variable, trimming whitespace.
// Logs success or error based on file access.
// ---------------------------------------------
void setFileContentToStringVariable(String& variable, const String& filePath, const String& fileName) {

    // Open a file with the parameter path.
    File file = LittleFS.open(filePath, "r");
    if (!file || file.isDirectory()) {
        Serial.println("ERROR: Failed to open " + filePath);
        return;
    }

    variable = file.readString();
    variable.trim();  // Remove newline or spaces
    file.close();

    debugln("SUCCESS: " + fileName + " loaded:");
    printInterfaceSentence(variable);
}
```

![setFileContentToStringVariable](https://github.com/user-attachments/assets/56481fd6-dd5f-4baf-8a17-b0e4c891c758)
    
---
    
## 📌 void syncTimeToNtpServer() 
Esta función sincroniza la hora del sistema ESP32 con servidores NTP y muestra el resultado.  

**Funcionamiento**    

- Configura la zona horaria y los servidores NTP mediante `configTzTime()`.  
- Intenta obtener la hora local usando `getLocalTime()` hasta un máximo de 5 intentos.  
- Si la sincronización es exitosa:  
  - Formatea la fecha y hora obtenida en `"YYYY-MM-DD HH:MM:SS"`.  
  - Imprime un mensaje indicando que la hora se estableció correctamente.  
- Si después de 5 intentos no se logra obtener la hora, imprime un mensaje de error.  
    
```cpp
// ---------------------------------------------
// Synchronizes the ESP32 system time with NTP servers and prints the result.
// Displays an error message if time synchronization fails.
// ---------------------------------------------
void syncTimeToNtpServer() {
    printInterfaceTitle("Network Time Protocol");
    configTzTime(TZ_INFO, "pool.ntp.org", "time.nist.gov", "time.google.com");  // Configurar NTP
    printInterfaceSentence("INFO: Synchronizing with NTP...");

    struct tm timeinfo;
    const int maxRetries = 5;
    int attempt = 0;

    while (attempt < maxRetries) {
        if (getLocalTime(&timeinfo)) {
            // Éxito: imprimir hora formateada y salir
            char formattedDate[30];
            strftime(formattedDate, sizeof(formattedDate), "%Y-%m-%d %H:%M:%S", &timeinfo);
            printInterfaceSentences("- Date and time set successfully: ", formattedDate);
            printInterfaceBottomLine();
            return;
        }

        // Fallo: mostrar intento fallido y esperar un poco
        printInterfaceSentences("WARNING: Attempt ", String(attempt + 1) + " failed. Retrying...");
        delay(1000);  // Espera 1 segundo antes de intentar otra vez
        attempt++;
    }

    // Si llegamos aquí, fallaron todos los intentos
    printInterfaceSentence("ERROR: Failed to obtain date and time from NTP server after multiple attempts.");
    printInterfaceBottomLine();
}
```

![syncTimeToNtp](https://github.com/user-attachments/assets/cb38f930-6b49-469d-8001-ad50baf807cb)
    
---
    
## 📌 void copyFileFromSDToLocalFS(String filePath, String metadataFilePath, String fileName)
Esta función copia un archivo desde la tarjeta SD al sistema de archivos LittleFS y guarda su fecha de última modificación en un archivo de metadatos. Además, registra el progreso y errores durante el proceso, asegurando el correcto manejo de los archivos.  

**Funcionamiento**    

- Abre el archivo en la tarjeta SD (`filePath`) en modo lectura.  
- Si no puede abrir el archivo en SD, registra un error y termina la función.  
- Abre el archivo local en LittleFS en modo escritura para sobrescribir o crear el archivo destino.  
- Si no puede abrir el archivo local, registra un error, cierra el archivo SD y termina la función.  
- Copia byte a byte el contenido del archivo SD al archivo local.  
- Registra un mensaje indicando que la copia fue exitosa.  
- Obtiene la fecha de última modificación del archivo en SD con `getLastWrite()`.  
- Escribe esta fecha en un archivo de metadatos usando `writeTimestampToFile()`.  
- Lee la fecha guardada y la muestra en formato legible usando `getTimestampAsDate()` y `readTimestampFromFile()`.  
- Cierra ambos archivos para liberar recursos.  

    
```cpp
// ---------------------------------------------
// Copies a file from the SD card to LittleFS and saves its last modification timestamp.
// Logs progress and errors, ensuring proper file handling throughout the process.
// ---------------------------------------------
void copyFileFromSDToLocalFS(String filePath, String metadataFilePath, String fileName) {

    // Open files for reading from SD and writing to LittleFS
    File sdFile = SD.open(filePath, "r");

    if (!sdFile) {
        debugln("ERROR: Failed to open SD " + fileName + " for copying.");
        return;  // Exit if SD file can't be opened
    } else {
        // Open local file for writing (using the same filePath).
        File localFile = LittleFS.open(filePath, "w");

        if (!localFile) {
            debugln("ERROR: Failed to open local " + fileName + " for writing.");
            sdFile.close();  // Close SD file if writing fails.
            return;          // Exit if local file can't be opened
        } else {
            // Copy file from SD to local storage.
            while (sdFile.available()) {
                localFile.write(sdFile.read());
            }

            printInterfaceSentence("INFO: " + fileName + " copied from SD to local storage successfully.");

            // Store the original file's timestamp for future reference.
            unsigned long sdFileTimestamp = sdFile.getLastWrite();

            writeTimestampToFile(sdFileTimestamp, metadataFilePath);

            printInterfaceSentence("- " + fileName + " last modification date: ");
            printInterfaceSentence(getTimestampAsDate(readTimestampFromFile(metadataFilePath)));

            // Close both files after copying.
            sdFile.close();
            localFile.close();
        }
    }
}
```
    
![copyFileFromSDToLocalFS](https://github.com/user-attachments/assets/c3598d5c-df40-4a30-b204-5d73ab9fe74b)
    
---
    
## 📌 void writeTimestampToFile(unsigned long timestamp, String filePath)
Esta función escribe un timestamp proporcionado en un archivo dentro del sistema de archivos LittleFS, sobrescribiendo cualquier contenido previo.  

**Funcionamiento**    

- Abre el archivo especificado por `filePath` en modo escritura (`"w"`), lo que borra el contenido anterior si existe.  
- Si no se puede abrir el archivo, registra un error y termina la función.  
- Escribe el timestamp en el archivo seguido de un salto de línea.  
- Cierra el archivo para guardar los cambios y liberar recursos.  
- Imprime un mensaje informando que el timestamp fue guardado correctamente.  

    
```cpp
// ---------------------------------------------
// Writes a given timestamp to a file on LittleFS, overwriting any existing content.
// Logs success or error depending on the file operation result.
// ---------------------------------------------
void writeTimestampToFile(unsigned long timestamp, String filePath) {
    // Open file in write mode, which will overwrite existing data
    File file = LittleFS.open(filePath, "w");

    if (!file) {
        debugln("ERROR: Failed to open file '" + filePath + "' for writing.");
        return;
    }

    // Write the timestamp to the file
    file.println(timestamp);

    // Close the file
    file.close();

    printInterfaceSentences("INFO: Timestamp successfully written to file '", filePath);
}
```
    
![writeTimestampToFile](https://github.com/user-attachments/assets/d805b2d6-d0e3-4e1c-ae55-e52668db003c)
       
## 📌 String getTimestampAsDate(unsigned long timestamp) 
Esta función convierte un timestamp UNIX en una cadena de texto con formato de fecha y hora legible.  
    
**Funcionamiento**    

- Convierte el valor `unsigned long` del timestamp a un tipo `time_t`.  
- Utiliza `localtime()` para convertir el timestamp en una estructura `tm` con los componentes de fecha y hora.  
- Usa `strftime()` para formatear la fecha y hora en el formato `"YYYY-MM-DD HH:MM:SS"`.  
- Devuelve la cadena formateada como resultado.  
          
```cpp
// ---------------------------------------------
// Converts a UNIX timestamp to a formatted date-time string.
// Returns the string in "YYYY-MM-DD HH:MM:SS" format.
// ---------------------------------------------
String getTimestampAsDate(unsigned long timestamp) {
    time_t timeStamp = (time_t)timestamp;  // Cast the unsigned long to time_t
    struct tm* timeinfo;
    timeinfo = localtime(&timeStamp);  // Convert timestamp to struct tm

    // Print formatted date & time
    char formattedDate[30];
    strftime(formattedDate, sizeof(formattedDate), "%Y-%m-%d %H:%M:%S", timeinfo);  // Format the time
    return formattedDate;                                                           // Print formatted date and time
}
```
    
![getTimestampAsDate](https://github.com/user-attachments/assets/39e8e711-beb3-47a1-a7cc-e80b42cb7318)
     
## 📌 unsigned long readTimestampFromFile(String filePath)
Esta función lee un timestamp almacenado en un archivo dentro del sistema de archivos LittleFS y lo devuelve como un valor `unsigned long`. En caso de error o si el timestamp no se encuentra, retorna 0. También registra mensajes de error o advertencia según corresponda.  

**Funcionamiento**    

- Inicializa la variable `timestamp` en 0, que será el valor retornado si no se encuentra un timestamp válido.  
- Intenta abrir el archivo especificado por `filePath` en modo lectura.  
- Si el archivo no puede abrirse, registra un error y devuelve 0.  
- Si el archivo está disponible para lectura, utiliza `parseInt()` para leer el valor entero (timestamp) de la primera línea.  
- Cierra el archivo para liberar recursos.  
- Si no se encontró un timestamp válido (valor 0), registra una advertencia.  
- Retorna el timestamp leído o 0 si no se encontró.  
    
```cpp
// ---------------------------------------------
// Reads and returns a timestamp from a file on LittleFS; returns 0 if not found or on error.
// Logs appropriate messages based on the outcome of the read operation.
// ---------------------------------------------
unsigned long readTimestampFromFile(String filePath) {
    // Default value for timestamp (0 indicates not found)
    unsigned long timestamp = 0;

    // Open the provided file path in read mode
    File file = LittleFS.open(filePath, "r");

    if (!file) {
        debugln("ERROR: Failed to open file '" + filePath + "' for reading.");
        return timestamp;
    }

    // Read the timestamp (only one line in the file)
    if (file.available()) {
        // Read the integer value (timestamp)
        timestamp = file.parseInt();
    }

    // Close the file
    file.close();

    // Return the timestamp (0 if not found)
    if (timestamp == 0) {
        debugln("WARNING: No timestamp found in file '" + filePath + "'.");
    }
    return timestamp;
}
```

![readTimestampFromFile](https://github.com/user-attachments/assets/b55762c4-40f3-4712-98f7-a4247efe1c8c)
    
   
## 📌 String writeToSerialPort(String instruction)
Esta función envía una cadena de texto al puerto serie `MySerial` y luego lee la respuesta que recibe.  

**Funcionamiento**    

- Primero, ajusta la cadena `instruction` usando la función `replaceEndingNewline()` para asegurar que los caracteres de nueva línea estén en el formato esperado.  
- Espera hasta que el puerto serie esté listo para escribir, verificando con `MySerial.availableForWrite()`. Mientras no esté listo, imprime "W: not ready yet" y espera un tiempo definido por `WAIT_TIME`.  
- Envía la cadena ajustada al puerto serie usando `MySerial.write()`.  
- Después de enviar, llama a `readFromSerialPort()` para leer y devolver la respuesta recibida (por ejemplo, un ACK o un código de error).  
    
```cpp
// ---------------------------------------------
// Funcion que escribe una cadena al puerto serie.
// ---------------------------------------------
String writeToSerialPort(String instruction) {
    instruction = replaceEndingNewline(instruction);

    /*while (MySerial.available()) {
     *   Serial.println("Discarding bytes: " + MySerial.readStringUntil('\r'));
}*/

    // Condición de guardia para la comunicacion.
    while (!MySerial.availableForWrite()) {
        Serial.println("W: not ready yet");
        delay(WAIT_TIME);
    }

    // Envía la instrucción recibida en formato cadena..
    MySerial.write(instruction.c_str());

    //Leer respuesta a la instruccion ACK / ERR-REF
    return readFromSerialPort();
}
```
   
![writeToSerialPort](https://github.com/user-attachments/assets/66f1f40e-3849-4110-b778-6a85fca5617f)
      
## 📌 String readFromSerialPort()
Esta función espera hasta que haya datos disponibles en el puerto serie `MySerial`, luego lee y devuelve la cadena recibida hasta encontrar un retorno de carro (`\r`).  

**Funcionamiento**    

- Entra en un bucle esperando que haya datos disponibles en `MySerial`. Mientras no haya datos, imprime "R: not ready yet" y espera un tiempo definido por `WAIT_TIME`.  
- Cuando detecta datos disponibles, lee la cadena desde el puerto serie hasta encontrar el carácter de retorno de carro (`\r`).  
- Devuelve la cadena leída.  

    
```cpp
// ---------------------------------------------
// Waits until data is available on the MySerial port, then
// reads and returns the incoming string until a carriage return.
// ---------------------------------------------
String readFromSerialPort() {
    while (!MySerial.available()) {
        Serial.println("R: not ready yet");
        delay(WAIT_TIME);
    }
    return MySerial.readStringUntil('\r');
}
```

![readFromSerialPort](https://github.com/user-attachments/assets/bef68f3c-f7be-44a9-9f0c-1a89084f05a9)


## 📌 void getProjectorStatus()
Esta función envía un comando al proyector para consultar el estado de su lámpara, espera una respuesta y almacena el resultado en una variable global.  

**Funcionamiento**    

- Envía el comando definido en `lampStatusInquireCommand` al proyector a través del puerto serie utilizando `writeToSerialPort()`.  
- Espera la respuesta del proyector leyendo desde el puerto serie con `readFromSerialPort()` y guarda el valor recibido en la variable global `lampStatus`.  
- Si no se recibe respuesta (cadena vacía), se imprime un mensaje de error indicando la falta de respuesta.  
- Si se recibe una respuesta válida, se imprime el estado de la lámpara recibido desde el proyector.  
    
```cpp
// ---------------------------------------------
// Sends a command to the projector to inquire about its lamp
// status, waits for a response, and stores the result.
// ---------------------------------------------
void getProjectorStatus() {
    // Sends the "lamp status?" instruction.
    writeToSerialPort(lampStatusInquireCommand);

    // Reads the response from the unit.
    lampStatus = readFromSerialPort();

    // En caso de no recibir respuesta dentro del tiempo límite.
    if (lampStatus == "") {
        Serial.println("ERROR: No response from the projector.");
        return;
    } else {
        Serial.println("Estado de la lampara: " + lampStatus);
    }
}
```       
       
![getProjectorStatus](https://github.com/user-attachments/assets/c4cbddbb-0732-4357-8111-a53350627a5c)
      


## 📌 void callServer(int& httpResponseCode, String& httpResponseData)
Esta función realiza una solicitud HTTPS de tipo GET a un servidor, incluyendo un encabezado de autorización con JWT, para recuperar información sobre el estado de un proyector. Gestiona validaciones previas, construcción de URL, respuesta HTTP y posibles errores.  

**Funcionamiento**    

- Realiza validaciones para asegurarse de que los parámetros necesarios estén definidos (`projectorClassroom`, `urlProjectors`, `urlFirebase`, `xClientId`) y que haya conexión WiFi. Si alguna validación falla, la función termina.  
- Obtiene un token JWT mediante la función `getJwt()`. Si no se logra obtener, la solicitud se cancela.  
- Inicializa un cliente HTTP y limpia las variables de salida `httpResponseCode` y `httpResponseData`.  
- Obtiene el estado actual del proyector mediante `getProjectorStatus()` y lo codifica para su inclusión segura en la URL.  
- Construye la URL de la solicitud con los parámetros requeridos (`projectorClassroom` y `projectorStatus`).  
- Muestra en la interfaz los detalles de la solicitud que se va a realizar.  
- Usa `beginWithRetry()` para establecer conexión con el servidor, intentando varias veces si es necesario. Si falla, se termina el proceso.  
- Agrega el encabezado `Authorization` con el JWT en formato Bearer.  
- Envía la solicitud GET al servidor.  
- Si el código HTTP recibido está entre 200 y 299:  
  - Se informa que la respuesta fue recibida.  
  - Si el código es 204 (sin contenido), se indica específicamente.  
  - Si hay contenido, se guarda en `httpResponseData` y se imprime parcialmente para depuración.  
  - Se indica que el proceso de consulta fue completado.  
- Si la respuesta no es exitosa, se imprime el código de error correspondiente.  
- Finaliza la conexión HTTP liberando los recursos utilizados.  
    
```cpp
// ---------------------------------------------
// Sends an HTTPS GET request to a server with authorization and retrieves the response.
// Performs basic validations, builds the URL, and handles the HTTP response and errors.
// ---------------------------------------------
void callServer(int& httpResponseCode, String& httpResponseData) {

    // ---------------------------------------------
    // PRELIMINARY VALIDATIONS
    // ---------------------------------------------
    if (projectorClassroom.length() == 0) {
        printInterfaceSentence("ERROR: 'projectorClassroom' is not set. Aborting request.");
        return;
    }

    if (urlProjectors.length() == 0) {
        printInterfaceSentence("ERROR: 'urlProjectors' is not set. Aborting request.");
        return;
    }

    if (urlFirebase.length() == 0) {
        printInterfaceSentence("ERROR: 'urlFirebase' is not set. Aborting request.");
        return;
    }

    if (xClientId.length() == 0) {
        printInterfaceSentence("ERROR: 'xClientId' is not set. Aborting request.");
        return;
    }

    if (WiFi.status() != WL_CONNECTED) {
        printInterfaceSentence("ERROR: No WiFi connection. Aborting request.");
        return;
    }

    // ---------------------------------------------
    // GET JWT TOKEN FOR REQUEST
    // ---------------------------------------------
    String authToken = getJwt();

    authToken.trim();

    if (authToken.length() == 0) {
        printInterfaceSentence("ERROR: Could not get JWT from the server");
        return;
    }

    // ---------------------------------------------
    // REQUEST SETUP
    // ---------------------------------------------
    HTTPClient http;  // HTTP client for the request.
    httpResponseCode = 0;
    httpResponseData = "";

    getProjectorStatus();

    String encodedLampStatus = urlEncode(lampStatus.c_str());

    String completeURL = urlProjectors + "/server-events?projectorClassroom=" + projectorClassroom + "&projectorStatus=" + encodedLampStatus;  // Configures the complete request URL.

    printInterfaceSentence("Request details: ");
    printInterfaceSentences("-- Server Address: ", urlProjectors);
    printInterfaceSentences("-- Specific endpoint: ", "/server-events");
    printInterfaceSentences("-- Projector ID: ", projectorClassroom);
    printInterfaceSentences("-- Projector Status: ", lampStatus);
    printInterfaceSeparator();

    //http.begin(completeURL);  ------------------------------------------------------------------------------ Sostituido a favor de un mecanismo con 5 reintentos de conexion.

    // Llamada a funcion que intenta varias veces la conexión.
    if (!beginWithRetry(http, completeURL)) {
        return;  // Falló incluso tras varios intentos
    }

    String bearer = "Bearer " + authToken;  // Authorization header.

    http.addHeader("Authorization", bearer);

    httpResponseCode = http.GET();  // Send the request.

    if (httpResponseCode >= 200 && httpResponseCode < 300) {

        printInterfaceSentence("TASK: Response received.");

        printInterfaceSentences("- HTTP Response Code:", String(httpResponseCode));

        // Check if the body of the response has content.
        if (httpResponseCode == 204) {
            printInterfaceSentence("WARNING: No Content in response body.");
        } else {
            httpResponseData = http.getString();

            printInterfaceSentence("- Server Response:");
            String sub = String(httpResponseData).substring(0, debugInterfaceLength - 7);
            printInterfaceSentences("-- ", String(sub));

            printInterfaceSeparator();
            printInterfaceSentence("TASK: Server inquiry process completed.");
            printInterfaceBottomLine();
        }
    } else {
        printInterfaceSentences("ERROR: HTTP request failed. Code: ", String(httpResponseCode));
        printInterfaceBottomLine();
    }

    http.end();  // Free memory resources.
}
```
     
![callServer](https://github.com/user-attachments/assets/314f2053-a690-4f3a-b9b4-eb3dc1673638)
        
## 📌 String updateEvent(String eventId, String deviceResponseCode)
Esta función envía una solicitud HTTPS de tipo PUT al servidor para actualizar el estado de un evento. Realiza validaciones previas, construye la URL con los parámetros necesarios y devuelve la respuesta del servidor como una cadena de texto.  

**Funcionamiento**    

- Muestra un título en la interfaz indicando el inicio del proceso de actualización del evento.  
- Realiza una serie de validaciones críticas para asegurar que todos los parámetros requeridos estén definidos (`projectorClassroom`, `urlProjectors`, `urlFirebase`, `xClientId`, conectividad WiFi, `eventId`, y `deviceResponseCode`). Si alguno falta, se aborta la operación.  
- Solicita un JWT llamando a `getJwt()`. Si la solicitud falla, el proceso también se detiene.  
- Codifica los parámetros `deviceResponseCode` y `projectorClassroom` para que puedan enviarse de forma segura en la URL.  
- Construye la URL final combinando el endpoint base con los parámetros codificados como parte de la query string.  
- Intenta conectar con el servidor usando `beginWithRetry()`. Si no se logra, la función termina.  
- Agrega el encabezado de autorización con el JWT obtenido.  
- Envía una solicitud PUT vacía (la API requiere un cuerpo aunque sea nulo).  
- Si el servidor responde con un código HTTP exitoso (2xx):  
  - Informa que la respuesta fue recibida.  
  - Extrae y muestra la respuesta del servidor (hasta un límite definido para depuración).  
- Si la respuesta indica error, imprime el código correspondiente.  
- Libera los recursos usados por el cliente HTTP.  
- Informa que el proceso de consulta al servidor ha finalizado.  
- Devuelve el contenido de la respuesta del servidor.  


```cpp
// ---------------------------------------------
// Sends an HTTPS PUT request to update an event’s status on the server.
// Validates inputs, builds the URL, and returns the server's response string.
// ---------------------------------------------
String updateEvent(String eventId, String deviceResponseCode) {

    printInterfaceTitle("Update event status request.");

    // ---------------------------------------------
    // PRELIMINARY VALIDATIONS
    // ---------------------------------------------
    if (projectorClassroom.length() == 0) {
        printInterfaceSentence("ERROR: 'projectorClassroom' is not set. Aborting request.");
        return "";
    }

    if (urlProjectors.length() == 0) {
        printInterfaceSentence("ERROR: 'urlProjectors' is not set. Aborting request.");
        return "";
    }

    if (urlFirebase.length() == 0) {
        printInterfaceSentence("ERROR: 'urlFirebase' is not set. Aborting request.");
        return "";
    }

    if (xClientId.length() == 0) {
        printInterfaceSentence("ERROR: 'xClientId' is not set. Aborting request.");
        return "";
    }

    if (WiFi.status() != WL_CONNECTED) {
        printInterfaceSentence("ERROR: No WiFi connection. Aborting request.");
        return "";
    }

    if (eventId.length() == 0) {
        debugln("ERROR: Event Id is not set. Aborting request.");
        return "";
    }

    if (deviceResponseCode.length() == 0) {
        debugln("ERROR: Device Response Code is not set. Aborting request.");
        return "";
    }

    printInterfaceSeparator();
    printInterfaceSentence("REQUESTING JWT.");
    printInterfaceSeparator();
    String authToken = getJwt();

    delay(200);

    if (authToken.length() == 0) {
        printInterfaceSentence("ERROR: Cannot get JWT from the server");
        return "";
    }

    printInterfaceSeparator();
    printInterfaceSentence("UPDATE REQUEST TO SERVER.");
    printInterfaceSeparator();

    String httpResponseData = "";
    HTTPClient httpUpdate;
    httpUpdate.setTimeout(20000);

    String encodedResponseCode = urlEncode(deviceResponseCode.c_str());
    String encodedProjectorClassroom = urlEncode(projectorClassroom.c_str());

    String completeURL = urlProjectors + "/server-events?eventId=" + eventId + "&rarc=" + encodedResponseCode + "&classroom=" + encodedProjectorClassroom;
    Serial.println("GET: " + completeURL);

    // Llamada a funcion que intenta varias veces la conexión.
    if (!beginWithRetry(httpUpdate, completeURL)) {
        return "";  // Falló incluso tras varios intentos
    }

    String bearer = "Bearer " + authToken;

    httpUpdate.addHeader("Authorization", bearer);

    int httpResponseCode = httpUpdate.PUT("");  // Este parguela requiere un parametro minimo aunque sea nulo (ver docu ofi).

    if (httpResponseCode >= 200 && httpResponseCode < 300) {
        printInterfaceSentence("UPDATE EVENT: Response received.");
        printInterfaceSentence("- HTTP Response Code:");

        httpResponseData = httpUpdate.getString();
        if (httpResponseData.length() > 0) {

            printInterfaceSentence("- Server Response:");
            String sub = String(httpResponseData).substring(0, debugInterfaceLength - 7);
            printInterfaceSentences("-- ", String(sub));

        } else {
            debugln("WARNING: Received an empty response from the server.");
        }

    } else {
        printInterfaceSentences("ERROR: HTTP request failed. Code: ", String(httpResponseCode));
    }

    httpUpdate.end();  // Free memory resources.

    printInterfaceSentence("UPDATE EVENT: Server inquiry process finished.");

    return httpResponseData;
}
```
    
![updateEvent - 2](https://github.com/user-attachments/assets/fee914cc-405e-486c-8efb-ec326eba44b6)
    
    
## 📌 String getJwt() 
Esta función realiza una solicitud HTTP POST a un servidor para obtener un token JWT (JSON Web Token), utilizado normalmente para autenticación con un servicio como Firebase.  

**Funcionamiento**    

- Crea y configura un cliente HTTP (`HTTPClient httpJwt`) con un tiempo de espera extendido de 20 segundos para evitar fallos por respuestas lentas.  
- Muestra por interfaz los detalles de la solicitud, como la dirección del servidor y el identificador del cliente (`X-CLIENT-ID`).  
- Intenta establecer conexión con el servidor llamando a `beginWithRetry()`, que maneja reintentos en caso de fallos. Si no se logra conectar, la función retorna una cadena vacía.  
- Añade al encabezado de la solicitud el campo `X-CLIENT-ID`.  
- Envía una solicitud POST vacía.  
- Si la respuesta HTTP tiene un código exitoso (entre 200 y 299):  
  - Imprime mensajes de confirmación y el código HTTP recibido.  
  - Lee la respuesta del servidor.  
  - Si la respuesta no está vacía, se almacena como el token de autenticación (`authToken`) y se imprime parcialmente para depuración.  
  - Si la respuesta está vacía, se imprime una advertencia.  
- Si la respuesta HTTP indica error (fuera del rango 200–299), se imprime el código de error.  
- Finaliza el cliente HTTP liberando recursos.  
- Devuelve el token JWT obtenido, o una cadena vacía si falló.  

```cpp
String getJwt() {

    // ---------------------------------------------
    // REQUEST SETUP
    // ---------------------------------------------
    HTTPClient httpJwt;         // HTTP client for the request.
    httpJwt.setTimeout(20000);  // configura una ventana de tiempo mas amplia para la respuesta.
    String httpResponseData = "";
    String authToken = "";

    // Log request details.
    printInterfaceSentence("Request details: ");
    printInterfaceSentences("-- Server Address: ", urlFirebase);
    printInterfaceSentences("-- X-CLIENT-ID: ", xClientId);

    // Llamada a funcion que intenta varias veces la conexión.
    if (!beginWithRetry(httpJwt, urlFirebase)) {
        return "";  // Falló incluso tras varios intentos
    }

    httpJwt.addHeader("X-CLIENT-ID", xClientId);

    int httpResponseCode = httpJwt.POST("");  // Send the request.

    if (httpResponseCode >= 200 && httpResponseCode < 300) {

        printInterfaceSentence("FIREBASE: Response received.");

        printInterfaceSentences("- HTTP Response Code:", String(httpResponseCode));

        httpResponseData = httpJwt.getString();
        if (httpResponseData.length() > 0) {

            //printInterfaceSentences("- Server Response:", String(httpResponseData));

            printInterfaceSentence("- Server Response:");
            String sub = String(httpResponseData).substring(0, debugInterfaceLength - 7);
            printInterfaceSentences("-- ", String(sub));

            authToken = httpResponseData;

        } else {
            printInterfaceSentence("WARNING: Received an empty response from the server.");
        }

    } else {
        printInterfaceSentences("ERROR: HTTP request failed. Code: ", String(httpResponseCode));
    }

    httpJwt.end();  // Free memory resources.

    return authToken;
}
```
    
![String getJwt()](https://github.com/user-attachments/assets/17c7c331-e502-4a31-a785-cfe647443149)
    
    
## 📌 String getValueFromString(const String& data, const String& key)
Esta función extrae el valor asociado a una clave específica dentro de una cadena de texto, usualmente usada para obtener valores de respuestas en formato JSON.

**Funcionamiento**    

- Busca la posición de la clave indicada (`key`) dentro de la cadena `data`.  
- Si no encuentra la clave, devuelve el mensaje `"CLAVE NO ENCONTRADA"`.  
- Si la clave se encuentra, calcula la posición donde comienza el valor asociado (después de `":`).  
- Verifica si el valor comienza con comillas (`"`):  
  - Si es así, extrae el texto encerrado entre comillas (valor tipo string).  
  - Si no, asume que el valor es un número u otro tipo sin comillas, y extrae el texto hasta encontrar una coma `,` o una llave `}` que indica el final del valor.  
- Si el formato es incorrecto (por ejemplo, faltan comillas o delimitadores), devuelve `"ERROR DE FORMATO"`.  


```cpp
// ---------------------------------------------
// Extracts a value from a string given a key. 
// Used to extract values from a json response.
// ---------------------------------------------
String getValueFromString(const String& data, const String& key) {
    int keyPos = data.indexOf("\"" + key + "\":");
    if (keyPos == -1) return "CLAVE NO ENCONTRADA";

    int valueStart = keyPos + key.length() + 3;  // posición después de ":"

    // Verificar si el valor comienza con comillas (string) o no (número u otro)
    if (data.charAt(valueStart) == '"') {
        // Valor es string entre comillas
        int startQuote = valueStart + 1;
        int endQuote = data.indexOf("\"", startQuote);
        if (endQuote == -1) return "ERROR DE FORMATO";
        return data.substring(startQuote, endQuote);
    } else {
        // Valor es número u otro sin comillas: leer hasta , o }
        int endPos = data.indexOf(",", valueStart);
        if (endPos == -1) {
            endPos = data.indexOf("}", valueStart);
            if (endPos == -1) return "ERROR DE FORMATO";
        }
        return data.substring(valueStart, endPos);
    }
}
```
    
![getValueFromString](https://github.com/user-attachments/assets/8088cfb5-1d7c-496c-9bef-a963eef86b06)

    
## 📌 String urlEncode(const char* msg)
Esta función codifica un mensaje para enviarlo de forma segura dentro de una URL. Su propósito es evitar errores o fallos que puedan ocurrir cuando un dispositivo RS232 responde con cadenas que contienen caracteres no compatibles o no seguros para URLs.

**Funcionamiento**   
- Recorre cada carácter del mensaje original.
- Si el carácter es un carácter "seguro" para URLs (letras mayúsculas y minúsculas, números, y algunos símbolos como `-`, `_`, `.`, `~`), lo añade tal cual al mensaje codificado.
- Si el carácter no es seguro para URLs, lo transforma en una secuencia `%XX`, donde `XX` es el valor hexadecimal del byte que representa el carácter.
- Finalmente, devuelve el mensaje codificado, listo para ser usado en una URL sin riesgo de causar problemas.

```cpp
// ---------------------------------------------
// Encodes the message to send it in a URL in a safe manner.
// Avoids crashes when the RS232 device responds with non url-friendly strings.
// ---------------------------------------------
String urlEncode(const char* msg) {
    const char* hex = "0123456789ABCDEF";
    String encodedMsg = "";
    while (*msg != '\0') {
        char c = *msg;
        if (('a' <= c && c <= 'z') || ('A' <= c && c <= 'Z') || ('0' <= c && c <= '9') || c == '-' || c == '_' || c == '.' || c == '~') {
            encodedMsg += c;
        } else {
            encodedMsg += '%';
            encodedMsg += hex[(c >> 4) & 0xF];
            encodedMsg += hex[c & 0xF];
        }
        msg++;
    }
    return encodedMsg;
}
```
   
![urlEncode](https://github.com/user-attachments/assets/ce35512e-9e43-4b42-b608-8026d59bca39)

   


   
## 📌 String replaceEndingNewline(String input)
Función de uso temporal: Esta función se utiliza como solución provisional para corregir un problema que ocurre al transmitir caracteres de escape en mensajes HTML. Su propósito es reformatear los caracteres de terminación de las órdenes recibidas en las tareas desde el backend.

>[!caution]
>Una vez se desarrolle una solución para la transferencia de ordenes desde el backend hacia el agente remoto para entregar correctamente los caracteres terminadores esta función dejará de tener uso y podrá quedar fuera del código.
    
```cpp
String replaceEndingNewline(String input) {


    if (input.endsWith("\\n")) {
        input = input.substring(0, input.length() - 3) + '\n';  // Elimina "\\n" y añade '\r'
    } else if (input.endsWith("\\r")) {
        input = input.substring(0, input.length() - 3) + '\r';  // Elimina "\\n" y añade '\r'
    }

    return input;
}
```
        
![replaceEndingNewLine](https://github.com/user-attachments/assets/665358a8-7fd3-4b9b-9ba7-6901f7694735)

    
    
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


