// WiFi library to connect to wireless networks
#include <WiFi.h>
// HTTP client library for making HTTP requests
#include <HTTPClient.h>
// Library for serial communication over RS232
#include <HardwareSerial.h>
// SD card library for file storage
#include <SD.h>
// Lightweight flash file system (for internal storage)
#include <LittleFS.h>
// File system library for handling directories
#include <FS.h>
// ESP32 system functions
#include "esp_system.h"
// NTP library to sync time.
#include <time.h>
// Library to store info in flash memory rather than ram.
#include <pgmspace.h>

// ---------------------------------------------
// Timezone Configuration
// ---------------------------------------------
#define TZ_INFO "CET-1CEST,M3.5.0/02,M10.5.0/03"

// ---------------------------------------------
// RS232 Serial Port Configuration
// ---------------------------------------------
#define rxRS232Port 16      // RX pin for RS232 communication
#define txRS232Port 17      // TX pin for RS232 communication
#define RS232BaudRate 9600  // BaudRt for RS232 communication

// ---------------------------------------------
// SD Card Pin Configuration
// ---------------------------------------------
#define sdCardMOSI 23
#define sdCardMISO 19
#define sdCardClock 18
#define sdCardChipSelect 5

// ---------------------------------------------
// LED Status Indicators
// ---------------------------------------------
#define offlinePin 4   // Indicates offline status
#define onlinePin 0    // Indicates online status
#define littleFSLed 2  // LED for internal storage (LittleFS) status
#define sdFSLed 15     // LED for SD card status

// ---------------------------------------------
// Debugging Configuration
// ---------------------------------------------
#define DEBUG 1

#if DEBUG == 1
#define debug(x) Serial.print(x)
#define debugln(x) Serial.println(x)
#else
#define debug(x)
#define debugln(x)
#endif

// ---------------------------------------------
// WiFi configuration variables.
// ---------------------------------------------
String WifiSSID;
String WifiPassword;
String urlProjectors;
String urlFirebase;
String wifiConfigFilePath = "/wifiConfig.txt";
String wifiConfigMetadataFilePath = "/wifiConfigMetadata.txt";
bool localWifiConfigFileExists = false;
bool sdWifiConfigFileExists = false;

// ---------------------------------------------
// Projector identity variables.
// ---------------------------------------------
String xClientId;
String projectorClassroom;
String projectorClassroomFilePath = "/projectorConfig.txt";
String projectorClassroomMetadataFilePath = "/projectorConfigMetadata.txt";
bool localClassroomFileExists = false;
bool sdClassroomFileExists = false;
String lampStatusInquireCommand = "";

// ---------------------------------------------
// Handled events variables.
// ---------------------------------------------
String lastEventId;
String lastCommandInstruction;
String eventId;
String commandInstruction;

// ---------------------------------------------
// HTTP Response variables.
// ---------------------------------------------
String responseData;  // Stores the response data.
int responseCode;     // Stores the response code.
String lampStatus = ""; 

// ---------------------------------------------
// LittleFS and SDFS variables.
// ---------------------------------------------
bool littleFSInitialized = false;
bool sdCardInitialized = false;

// ---------------------------------------------
// Debug interface configuration.
// ---------------------------------------------
int debugInterfaceLength = 80;

// ---------------------------------------------
// RS232 Transceiver configuration
// ---------------------------------------------
HardwareSerial MySerial(2);

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

  // -------------------------------
  // Load configuration from files.
  // -------------------------------

  // Loads the configuration stored in the file of the local FS.
  loadConfigFromFile(wifiConfigFilePath);

  // Loads the projector identification string in the file of the local FS
  loadProjectorInfoFromFile(projectorClassroomFilePath);

  // -------------------------------
  // Connect to WiFi
  // -------------------------------
  connectToWifi();

  // -------------------------------
  // Configure device RTC block (needs network)
  // -------------------------------
  syncTimeToNtpServer();

  // -------------------------------
  // Initializing RS232 port.
  // -------------------------------
  MySerial.begin(RS232BaudRate, SERIAL_8N1, rxRS232Port, txRS232Port);  // baudrate, config, RX pin, TX pin


  // -------------------------------
  // Ask the server for the lamp status command.
  // Wait one second after the function completes, try again if failed.
  // -------------------------------
  int retries = 0;
  const int maxRetries = 5;

  while (lampStatusInquireCommand.length() == 0 && retries < maxRetries) {
      lampStatusInquireCommand = getStatusInquiryCommand();
      delay(500);
      retries++;
  }

  if (lampStatusInquireCommand.length() == 0) {
    printInterfaceSentenceBox("ERROR: Failed to retrieve lamp status command after multiple attempts.");
  }
  else{
    printInterfaceSentenceBox("Lamp status inquiry command successfully retreived.");
  }

  // -------------------------------
  // End of Setup
  // -------------------------------
}

// ---------------------------------------------
// Loop Function (Runs Continuously)
// ---------------------------------------------
void loop() {
  // Listen for serial input to trigger a reboot

  // If the board is in debug mode, it will wait for instructions and display debug messages..
  if (DEBUG == 1) {
    if (Serial.available()) {
      char received = Serial.read();

      // Order to reboot the board.
      if (received == 'r') {
        printInterfaceSentenceBox("Rebooting now...");
        esp_restart();
      }

      // Prints the stored values of all the variables.
      if (received == 'v') {
        printInterfaceTitle("Showing variables data.");
        printInterfaceSentences("WifiSSID", WifiSSID);
        printInterfaceSentences("WifiPassword", WifiPassword);
        printInterfaceSentences("urlProjectors", urlProjectors);
        printInterfaceSentences("urlFirebase", urlFirebase);
        printInterfaceSentences("xClientId", xClientId);
        printInterfaceSentences("projectorClassroom", projectorClassroom);
        printInterfaceSentences("lampStatusInquireCommand", lampStatusInquireCommand);
        printInterfaceSentences("eventId", eventId);
        printInterfaceSentences("commandInstruction", commandInstruction);
        printInterfaceBottomLine();
      }

      // Inquires for tasks.
      if (received == 't') {

        // -------------------------------------------------
        // SEND REQUEST TO SERVER
        // -------------------------------------------------

        printInterfaceTitle("Inquiring for tasks...");

        // Reset the variables.
        responseCode = 0;
        responseData = "";

        callServer(responseCode, responseData);

        // If the response is OKAY.
        if (responseCode == 200) {

          printInterfaceSentence("HTTP Response 200 OKAY");

          // -------------------------------------------------
          // VALUES EXTRACTION FROM JSON RESPONSE
          // -------------------------------------------------

          // Saca el ID del evento
          eventId = "";  // Reinicia valores.
          eventId = getResponseValue(responseData, "eventId");

          // Saca la instrucción.
          commandInstruction = "";  // Reinicia valores.
          commandInstruction = getResponseValue(responseData, "commandInstruction");

          printInterfaceSentence("Event ID: " + eventId);
          printInterfaceSentence("Instruction: " + commandInstruction);


          // -------------------------------------------------
          // CONVERSION TO BYTE ARRAY
          // -------------------------------------------------

          // Almacena la longitud final del arreglo.
          int numBytes = 0;

          // Array de bytes donde se almacen la instrucción.
          byte* byteArray = hexStringToByteArray(commandInstruction, numBytes);

          printInterfaceSentences("Cantidad de bytes: ", String(numBytes));

          // Print byte array only if in debug mode.
          if (DEBUG == 1) {

            debug("| Instruccion parseada: ");

            // Imprime los bytes (rellena con 0 los <F).
            for (int i = 0; i < numBytes; i++) {
              if (byteArray[i] < 0x10) Serial.print("0");
              Serial.print(byteArray[i], HEX);
              Serial.print(" ");
            }

            printRepeatedChar(" ", debugInterfaceLength - 25 - commandInstruction.length());
            debugln("|");
          }

          // -------------------------------------------------
          // TRANSMISION DE LOS VALORES AL PUERTO SERIE.
          // -------------------------------------------------

          // Escribe al puerto los datos especificando su longitud (implementacion iterada del write normal).
          MySerial.write(byteArray, numBytes);

          MySerial.read(byteArray, numBytes);

          // -------------------------------------------------
          // RECEPCIÓN DE LOS VALORES AL PUERTO SERIE.
          // -------------------------------------------------
          if (MySerial.available()) {
            String unitResponse = MySerial.readStringUntil('\n');  // o '\r' dependiendo del protocolo del proyector
            Serial.println("Respuesta del proyector:");
            Serial.println(unitResponse);
          }
        }


        if (responseCode != 200) {
          printInterfaceSentence("WARNING: HTTP Response code: " + String(responseCode));
        }

        delay(100);

        // -------------------------------------------------
        // RECEPCION DE LOS VALORES AL PUERTO SERIE.
        // -------------------------------------------------

        printInterfaceBottomLine();
      }

      if (received == 'u') {
        updateEvent(eventId, "* 000");
      }
    }
  } 
  // -------------------------------------------------
  // Block that runs when the board is NOT in DEBUG mode.
  // -------------------------------------------------
  else {

    // -------------------------------------------------
    // SEND REQUEST TO SERVER AND HANDLE RESPONSE
    // -------------------------------------------------
    responseCode = 0;
    responseData = "";

    callServer(responseCode, responseData);

    // If the response is OKAY.
    if (responseCode == 200) {
      
      // -------------------------------------------------
      // VALUES EXTRACTION FROM JSON RESPONSE
      // -------------------------------------------------

      // Extract Event ID
      eventId = "";  // Reset value
      eventId = getResponseValue(responseData, "eventId");

      // Extract command instruction.
      commandInstruction = "";  // Reset value
      commandInstruction = getResponseValue(responseData, "commandInstruction");

      // -------------------------------------------------
      // CONVERSION TO BYTE ARRAY FOR SERIAL COMMS
      // -------------------------------------------------

      // Almacena la longitud final del arreglo.
      int numBytes = 0;

      // Array de bytes donde se almacen la instrucción.
      byte* byteArray = hexStringToByteArray(commandInstruction, numBytes);

      // -------------------------------------------------
      // TRANSMIT VALUES TO RS232 COMMS PORT.
      // -------------------------------------------------

      // Send instruction array with iterated variant of Serial Write method.
      MySerial.write(byteArray, numBytes);

      // -------------------------------------------------
      // READ VALUES FROM RS232 COMMS PORT.
      // -------------------------------------------------
      String unitResponse;

      if (MySerial.available()) {
        unitResponse = MySerial.readStringUntil('\r');  // o '\n' dependiendo del protocolo del proyector
      }

      // Wait to give the backend time to process.
      delay(5000);

      // Send event update request to backend based on unit response.
      updateEvent(eventId, unitResponse);

      delay(25000);

    }

    if (responseCode != 200) {
      printInterfaceSentence("WARNING: HTTP Response code: " + String(responseCode));
    }
  }

}

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


// ---------------------------------------------
// Asks the server for the Status Inquiry command for this specific projector.
// This string is used to inquire lamp status of the projector.
// ---------------------------------------------
String getStatusInquiryCommand(){

  // ---------------------------------------------
  // PRELIMINARY VALIDATIONS
  // ---------------------------------------------
  /*
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
  }*/

  // ---------------------------------------------
  // REQUEST SETUP
  // ---------------------------------------------
  HTTPClient http;  // HTTP client for the request.

  // Reinicia las variables referenciadas.
  String httpResponseData = "";
  int httpResponseCode = 0;

  /*String authToken = getJwt();*/

  if (authToken.length() == 0)
  {
    printInterfaceSentence("ERROR: Cannot get JWT from the server");
    return "";
  }

  String completeURL = urlProjectors + "/config-params?projectorClassroom=" + projectorClassroom;  // Configures the complete request URL.

  printInterfaceSentence("Request details: ");
  printInterfaceSentences("-- Server Address: ", urlProjectors);
  printInterfaceSentences("-- Specific endpoint: ", "/config-params");
  printInterfaceSentences("-- Projector ID: ", projectorClassroom);
  printInterfaceSeparator();

  http.begin(completeURL);                // uso temporal hasta que se pruebe el certificado.
  //String bearer = "Bearer " + authToken;  // Authorization header.
  //http.addHeader("Authorization", bearer);

  httpResponseCode = http.GET();  // Send the request.

  if (httpResponseCode >= 200 && httpResponseCode < 300) {

    printInterfaceSentence("INQUIRY COMMAND: Response received.");

    printInterfaceSentences("- HTTP Response Code:", String(httpResponseCode));

    httpResponseData = http.getString();

    if (httpResponseData.length() > 0) {

      printInterfaceSentences("- Server Response:", String(httpResponseData));

    } else {
      printInterfaceSentence("WARNING: Received an empty response from the server.");
    }

  } else {
    printInterfaceSentences("ERROR: HTTP request failed. Code: ", String(httpResponseCode));
  }

  http.end();  // Free memory resources.

  printInterfaceSeparator();
  printInterfaceSentence("INQUIRY COMMAND: Server inquiry process completed.");
  printInterfaceBottomLine();

  return httpResponseData;

}


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

// ---------------------------------------------
// Reads projector classroom information from a file on LittleFS line by line.
// Sets the global variable 'projectorClassroom' from the file content.
// ---------------------------------------------
void loadProjectorInfoFromFile(String projectorFilePath) {

  File projectorIdentityFile = LittleFS.open(projectorFilePath, "r");

  if (!projectorIdentityFile) {
    printInterfaceSentence("ERROR: Failed to open Projector information file for reading.");
    return;  // return here to avoid further code execution
  }

  while (projectorIdentityFile.available()) {
    String line = projectorIdentityFile.readStringUntil('\n');
    line.trim();

    if (line.startsWith("projectorClassroom=")) {
      projectorClassroom = line.substring(19);
    }
    else if (line.startsWith("x-client-id=")) {
      xClientId = line.substring(12);
    }
  }
  projectorIdentityFile.close();
}

// ---------------------------------------------
// Attempts to connect to a WiFi network and provides visual and textual feedback.
// Turns on the appropriate status LED based on connection success or failure.
// ---------------------------------------------
void connectToWifi() {

  printInterfaceTopLine();
  printInterfaceSentence("INFO: Connecting to WiFi...");
  debug("│ ");

  // Conectar sin password mediante filtrado mac.
  if ( WifiPassword.length() == 0 ){
     WiFi.begin(WifiSSID.c_str());
  }
  else{
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

// ---------------------------------------------
// Synchronizes the ESP32 system time with NTP servers and prints the result.
// Displays an error message if time synchronization fails.
// ---------------------------------------------
void syncTimeToNtpServer() {
  configTzTime(TZ_INFO, "pool.ntp.org", "time.nist.gov", "time.google.com");  // Configurar NTP
  printInterfaceSentence("INFO: Synchronizing with NTP...");

  struct tm timeinfo;
  if (!getLocalTime(&timeinfo)) {
    printInterfaceSentence("ERROR: Failed to obtain date and time from NTP server.");
    return;
  }

  // Print readable date and time
  char formattedDate[30];
  strftime(formattedDate, sizeof(formattedDate), "%Y-%m-%d %H:%M:%S", &timeinfo);  // Format the time

  printInterfaceSentences("- Date and time set successfully: ", formattedDate);  // Print formatted date and time
}

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

  debugln("INFO: Timestamp successfully written to file '" + filePath + "'.");
}

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
  // REQUEST SETUP
  // ---------------------------------------------
  HTTPClient http;  // HTTP client for the request.

  // Reinicia las variables referenciadas.
  httpResponseCode = 0;
  httpResponseData = "";

  String authToken = getJwt();

  if (authToken.length() == 0)
  {
    printInterfaceSentence("ERROR: Cannot get JWT from the server");
    return;
  }

  String completeURL = urlProjectors + "/server-events/?projectorClassroom=" + projectorClassroom;  // Configures the complete request URL.

  printInterfaceSentence("Request details: ");
  printInterfaceSentences("-- Server Address: ", urlProjectors);
  printInterfaceSentences("-- Specific endpoint: ", "/server-events");
  printInterfaceSentences("-- Projector ID: ", projectorClassroom);
  printInterfaceSeparator();

  http.begin(completeURL);                // uso temporal hasta que se pruebe el certificado.
  String bearer = "Bearer " + authToken;  // Authorization header.
  http.addHeader("Authorization", bearer);

  httpResponseCode = http.GET();  // Send the request.

  if (httpResponseCode >= 200 && httpResponseCode < 300) {

    printInterfaceSentence("TASK: Response received.");

    printInterfaceSentences("- HTTP Response Code:", String(httpResponseCode));

    httpResponseData = http.getString();
    if (httpResponseData.length() > 0) {

      printInterfaceSentences("- Server Response:", String(httpResponseData));

    } else {
      printInterfaceSentence("WARNING: Received an empty response from the server.");
    }

  } else {
    printInterfaceSentences("ERROR: HTTP request failed. Code: ", String(httpResponseCode));
  }

  http.end();  // Free memory resources.

  printInterfaceSeparator();
  printInterfaceSentence("TASK: Server inquiry process completed.");
  printInterfaceBottomLine();
}

// ---------------------------------------------
// Sends an HTTPS PUT request to update an event’s status on the server.
// Validates inputs, builds the URL, and returns the server's response string.
// ---------------------------------------------
String updateEvent(String eventId, String deviceResponseCode) {

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

  String authToken = getJwt();

  if (authToken.length() == 0)
  {
    printInterfaceSentence("ERROR: Cannot get JWT from the server");
    return "";
  }

  String httpResponseData;

  HTTPClient http;

  debug("INFO: Requesting ");

  String encodedResponseCode = urlEncode(deviceResponseCode.c_str());

  String completeURL = urlProjectors + "?eventId=" + eventId + "&rarc=" + encodedResponseCode + "&classroom=" + projectorClassroom;
  debugln(completeURL);

  http.begin(completeURL);  // Insecure connection without SSL.

  String bearer = "Bearer ";
  bearer += authToken;  // Usa String solo aquí

  http.addHeader("Authorization", bearer);

  int httpResponseCode = http.PUT("");  // Este parguela requiere un parametro minimo aunque sea nulo.

  if (httpResponseCode >= 200 && httpResponseCode < 300) {
    debugln("UPDATE EVENT: Response received.");
    debug("- HTTP Response Code:");

    httpResponseData = http.getString();
    if (httpResponseData.length() > 0) {
      printInterfaceSentence("- Server Response:");
      printInterfaceSentence(httpResponseData);
    } else {
      debugln("WARNING: Received an empty response from the server.");
    }

  } else {
    printInterfaceSentences("ERROR: HTTP request failed. Code: ", String(httpResponseCode));
  }

  http.end();     // Free memory resources.

  debugln("TASK: Server inquiry process finished.");

  return httpResponseData;
}

String getJwt() {
  
  HTTPClient http;  // HTTP client for the request.

  // ---------------------------------------------
  // REQUEST SETUP
  // ---------------------------------------------
  // Reinicia las variables referenciadas.
  String httpResponseData = "";

  String authToken = "" ;

  printInterfaceSentence("Request details: ");
  printInterfaceSentences("-- Server Address: ", urlFirebase);
  printInterfaceSentences("-- X-CLIENT-ID: ", xClientId);

  http.begin(urlFirebase);                // uso temporal hasta que se pruebe el certificado.

  http.addHeader("X-CLIENT-ID", xClientId);

  int httpResponseCode = http.POST("");  // Send the request.

  if (httpResponseCode >= 200 && httpResponseCode < 300) {

    printInterfaceSentence("FIREBASE: Response received.");

    printInterfaceSentences("- HTTP Response Code:", String(httpResponseCode));

    httpResponseData = http.getString();
    if (httpResponseData.length() > 0) {

      printInterfaceSentences("- Server Response:", String(httpResponseData));
      authToken = httpResponseData;

    } else {
      printInterfaceSentence("WARNING: Received an empty response from the server.");
    }

  } else {
    printInterfaceSentences("ERROR: HTTP request failed. Code: ", String(httpResponseCode));
  }

  http.end();  // Free memory resources.

  return authToken;
}

// ---------------------------------------------
// Extracts the value for a given key from a formatted string like "key=value".
// Returns the substring between '=' and the next comma or closing parenthesis.
// ---------------------------------------------
String getResponseValue(String text, String key) {

  // Busca la posición inicial de la clave seguida de "=" en el texto
  int startIndex = text.indexOf(key + "=");

  // Si no se encuentra la clave, retorna una cadena vacía
  if (startIndex == -1) return "";

  // Ajusta el índice para que apunte justo después del signo "="
  // Es decir, al comienzo del valor
  startIndex += key.length() + 1;

  // Busca la próxima coma desde startIndex, que indica el final del valor
  int endIndex = text.indexOf(",", startIndex);

  // Si no encuentra una coma, busca el paréntesis de cierre
  // Esto ocurre si el valor es el último en la cadena
  if (endIndex == -1) {
    endIndex = text.indexOf(")", startIndex);
  }

  // Extrae y retorna la subcadena entre startIndex y endIndex
  // Es decir, el valor correspondiente a la clave
  return text.substring(startIndex, endIndex);
}

// ---------------------------------------------
// Converts a hex string (with optional spaces) into a dynamically allocated byte array.
// Returns the byte array and sets outputLength to the number of bytes converted.
// ---------------------------------------------
byte* hexStringToByteArray(const String& input, int& outputLength) {

  int len = input.length();
  int byteIndex = 0;

  int maxBytes = len / 2;                  // estimación máxima de bytes
  byte* outputArray = new byte[maxBytes];  // reserva dinámica.

  // por cada caracter en la cadena.
  for (int i = 0; i < len;) {

    // Si hay espacios entre los hexadecimales, los ignora
    while (i < len && input[i] == ' ') i++;

    // Verifica si hay dos chars disponibles desde la i.
    if (i + 1 < len) {

      String hexByte = input.substring(i, i + 2);  // Extraer dos caracteres

      outputArray[byteIndex++] = strtoul(hexByte.c_str(), NULL, 16);  // Convierte las cadena extraida a un byte entero en base 16.

      i += 2;  // avanzar 2 caracteres

    } else {
      // Finaliza si no hay 2 caracteres más
      break;
    }
  }

  // Guarda la cantidad de bytes procesados
  outputLength = byteIndex;
  // Devuelve la REFERENCIA AL PUNTERO.
  return outputArray;
}

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

// ---------------------------------------------------------------
// Interface Printing Utilities
// Functions for formatted debug output with boxed and aligned text.
// ---------------------------------------------------------------
void printRepeatedChar(String c, int count) {
  for (int i = 0; i < count; ++i) {
    debug(c);
  }
}

void printInterfaceTopLine() {
  debug("┌");
  printRepeatedChar("─", debugInterfaceLength - 2);
  debugln("┐");
}

void printInterfaceSentence(String sentence) {
  debug("│ " + sentence);
  int padding = debugInterfaceLength - 3 - sentence.length();
  printRepeatedChar(" ", padding);
  debugln("│");
}

void printInterfaceSentences(String sentence, String sentence2) {
  debug("│ " + sentence + sentence2);
  int padding = debugInterfaceLength - 3 - sentence.length() - sentence2.length();
  printRepeatedChar(" ", padding);
  debugln("│");
}

void printInterfaceBottomLine() {
  debug("└");
  printRepeatedChar("─", debugInterfaceLength - 2);
  debugln("┘");
}

void printInterfaceSeparator() {
  int padding = debugInterfaceLength - 2;
  debug("├");
  printRepeatedChar("─", padding);
  debugln("┤");
}

void printInterfaceTitle(String title) {
  int padding = debugInterfaceLength - 7 - title.length();
  debug("┌─[ " + title + " ]");
  printRepeatedChar("─", padding);
  debugln("┐");
}

void printInterfaceSentenceBox(String sentence) {
  printInterfaceTopLine();
  printInterfaceSentence(sentence);
  printInterfaceBottomLine();
}
