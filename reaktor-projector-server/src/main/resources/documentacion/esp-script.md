This version includes:
- Wifi connection configuration.
- Certificate checks and copy mechanisms with working timestamp comparison.
- Calls server for task and receives latest simplified event.

## :pencil: Commented & debugged version.
```cpp
// WiFi library for connecting to wireless networks
#include <WiFi.h>
// Library for handling secure HTTPS connections
#include <WiFiClientSecure.h>
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

// ---------------------------------------------
// Timezone Configuration
// ---------------------------------------------
#define TZ_INFO "CET-1CEST,M3.5.0/02,M10.5.0/03"

// ---------------------------------------------
// RS232 Serial Port Configuration
// ---------------------------------------------
#define rxSR232Port 16  // RX pin for RS232 communication
#define txSR232Port 17  // TX pin for RS232 communication

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
// WiFi configuration
// ---------------------------------------------
String WifiSSID;
String WifiPassword;
String serverTasksAddress;

String wifiConfigurationFilePath = "/wifiConfig.txt";
String wifiConfigurationFileMetadataPath = "/wifiConfigMetadata.txt";

bool localConfigFileExists = false;
bool sdConfigFileExists = false;



// WiFi secure client for HTTPS.
WiFiClientSecure client;

// ---------------------------------------------
// SSL Certificate paths and control flags.
// ---------------------------------------------
String certificateFilePath = "/test.txt";          // Path to SSL certificate stored on SD card.   // Path to SSL certificate stored in internal flash (LittleFS).
String sslMetadataFilePath = "/sslMetadata.txt";  // Internal SSL file metadata for timestamps.


// 'File exists' check flags.
bool localCertificateExists = false;
bool sdCertificateExists = false;


bool sdCardInitialized = false;
bool littleFSInitialized = false;

// ---------------------------------------------
// Setup Function (Runs Once at Startup)
// ---------------------------------------------
void setup() {

  // -------------------------------
  // Initialize Serial Monitor
  // -------------------------------
  Serial.begin(115200);
  while (!Serial) { ; }  // Wait for Serial to be ready
  debugln("INFO: Serial monitor connected.");

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

  // -------------------------------
  // Initialize SD Card
  // -------------------------------
  sdCardInitialized = initializeSDCard();

  // -------------------------------
  // Compare and copy the most recent Configuration File
  // -------------------------------
  compareAndCopy( wifiConfigurationFilePath, wifiConfigurationFileMetadataPath, "Configuration File" );

  // -------------------------------
  // Compare and copy the most recent SSL Certificate File
  // -------------------------------
  compareAndCopy( certificateFilePath, sslMetadataFilePath, "SSL Certificate File" );

  
  // Unmount the SD card after processing.
  SD.end();
  // Turn SD card indicator off.
  digitalWrite(sdFSLed, LOW);

  // Loads the configuration stored in the file of the local FS.
  loadConfigFromFile(wifiConfigurationFilePath);


  // -------------------------------
  // Connect to WiFi
  // -------------------------------
  connectToWifi();

  // -------------------------------
  // Configure device RTC block
  // This block needs online status.
  // -------------------------------
  syncTimeToNtpServer();

  // -------------------------------
  // End of Setup
  // -------------------------------
}

// ---------------------------------------------
// Loop Function (Runs Continuously)
// ---------------------------------------------
void loop() {
  // Listen for serial input to trigger a reboot

  if (Serial.available()) {
    char received = Serial.read();

    if (received == 'c') {
      debugln("Calling server now...");
      callServer();
    }

    if (received == 'r') {
      debugln("Rebooting now...");
      esp_restart();
    }
    if (received == 'd') {  // User sends 'd' for delete
      debugln("Attempting to delete certificate...");

      // Delete from LittleFS (Internal Flash)
      if (LittleFS.exists(certificateFilePath)) {
        if (LittleFS.remove(certificateFilePath)) {
          debugln("INFO: Certificate deleted from LittleFS.");
        } else {
          debugln("ERROR: Failed to delete certificate from LittleFS.");
        }
      } else {
        debugln("WARNING: No SSL certificate file found in LittleFS.");
      }
      debugln("Certificate deletion process completed.");
    }

    if (received == 'o') {
      debugln("Attempting to open local SSL certificate...");
      // Delete from LittleFS (Internal Flash)
      if (LittleFS.exists(certificateFilePath)) {

        File file = LittleFS.open(certificateFilePath, "r");

        if (file) {
          debugln("INFO: Opening local SSL certificate file.");
          while (file.available()) {
            Serial.write(file.read());  // Print each character
          }
          file.close();
        } else {
          debugln("ERROR: Failed to open SSL certificate file from LittleFS.");
        }
      } else {
        debugln("WARNING: No SSL certificate file found in LittleFS.");
      }
    }
  }
}

// ---------------------------------------------
// Function to sync date and time with ntp server.
// ---------------------------------------------
// This function configures the ESP32's internal RTC with a timezone and synchronizes
// it with an NTP (Network Time Protocol) server. The RTC keeps track of the current
// date and time based on the server’s data.
void syncTimeToNtpServer() {
  configTzTime(TZ_INFO, "pool.ntp.org", "time.nist.gov", "time.google.com");  // Configurar NTP
  debugln("INFO: Synchronizing with NTP...");

  struct tm timeinfo;
  if (!getLocalTime(&timeinfo)) {
    debugln("ERROR: Failed to obtain date and time from NTP server.");
    return;
  }

  // Print readable date and time
  char formattedDate[30];
  strftime(formattedDate, sizeof(formattedDate), "%Y-%m-%d %H:%M:%S", &timeinfo);  // Format the time

  debug("- Date and time set successfully: ");
  debugln(formattedDate);  // Print formatted date and time
}

// ---------------------------------------------
// Function to print date and time in human readable format.
// ---------------------------------------------
void printTimestampAsDate(unsigned long timestamp) {
  time_t timeStamp = (time_t)timestamp;  // Cast the unsigned long to time_t
  struct tm* timeinfo;
  timeinfo = localtime(&timeStamp);  // Convert timestamp to struct tm

  // Print formatted date & time
  char formattedDate[30];
  strftime(formattedDate, sizeof(formattedDate), "%Y-%m-%d %H:%M:%S", timeinfo);  // Format the time
  debugln(formattedDate);                                                  // Print formatted date and time
}



// ---------------------------------------------
// Function to call the server and retreive server events for this unit.
// ---------------------------------------------
void callServer() {
  debugln("INFO: Starting server task inquiry...");

  if (serverTasksAddress.length() == 0) {
    debugln("ERROR: Server request URL is not set. Aborting request.");
    return;
  }

  if (WiFi.status() != WL_CONNECTED) {
    debugln("ERROR: No WiFi connection available. Aborting request.");
    return;
  }

  debugln("INFO: WiFi is connected. Preparing to call the server...");

  WiFiClientSecure client;
  client.setInsecure();  // Allows connection without SSL certificate TEMPORARY

  HTTPClient http;
  
  debug("INFO: Requesting ");
  debugln(serverTasksAddress);

  // Secure connection version - for SSL connection.
  // http.begin(client, serverTasksAddress);  // Use client with secure connection

  http.begin(serverTasksAddress); // Insecure connection without SSL.

  int httpResponseCode = http.GET();

  if (httpResponseCode > 0) {
    debugln("TASK: Response received.");
    debug("- HTTP Response Code:");
    debugln(httpResponseCode);

    String httpResponseData = http.getString();
    if (httpResponseData.length() > 0) {
      debugln("- Server Response:");
      debugln(httpResponseData);
    } else {
      debugln("WARNING: Received an empty response from the server.");
    }
  } else {
    debug("ERROR: HTTP request failed. Code: ");
    debugln(httpResponseCode);
    debug("ERROR: Reason: ");
    debugln(http.errorToString(httpResponseCode).c_str());
  }

  http.end();     // Free memory resources.
  client.stop();  // Free memory resources.

  debugln("TASK: Server inquiry process completed.");
}

// ---------------------------------------------
// Function to initialize the SD card, if inserted.
// Returns true if initialized successfully, false if not.
// ---------------------------------------------
bool initializeSDCard() {
  if (!SD.begin(sdCardChipSelect)) {
    debugln("WARNING: No SD card detected.");
    return false;  // Return false if SD card is not detected
  }

  debugln("INFO: SD Card mounted successfully.");

  // Verify SD accessibility by opening root directory
  File testFile = SD.open("/");
  if (!testFile) {
    debugln("ERROR: SD Card not accessible.");
    return false;  // Return false if SD card is not accessible
  }
  testFile.close();
  debugln("INFO: SD Card is accessible.");
  // Turn on SD card LED indicator
  digitalWrite(sdFSLed, HIGH);
  delay(1000);
  return true;  // Return true if SD card is successfully initialized
}


// ---------------------------------------------
// Function to initialize the local file stystem LittleFS.
// After the first attempt, if failed, will try again formatting.
// Returns true if initialized successfully, false if not.
// ---------------------------------------------
bool initializeLittleFS() {
  if (!LittleFS.begin()) {
    debugln("ERROR: Failed to mount LittleFS. Attempting format...");

    // Attempt to format and mount
    if (!LittleFS.begin(true)) {
      debugln("ERROR: LittleFS formatting failed.");
      return false;  // Return false if formatting also fails
    } else {
      debugln("WARNING: LittleFS formatted successfully.");
    }
  }
  debugln("INFO: LittleFS mounted successfully.");
  digitalWrite(littleFSLed, HIGH);  // Turn on LittleFS LED
  return true;                      // Return true if successfully mounted or formatted
}


void connectToWifi() {
    debug("INFO: Connecting to WiFi...");
    WiFi.begin(WifiSSID.c_str(), WifiPassword.c_str());

    int attempt = 0;
    const int maxAttempts = 20;  // 20 attempts (~5 sec)

    while (WiFi.status() != WL_CONNECTED && attempt < maxAttempts) {
        digitalWrite(offlinePin, HIGH);
        debug(".");
        delay(250);
        digitalWrite(offlinePin, LOW);
        delay(250);
        attempt++;
    }

    if (WiFi.status() == WL_CONNECTED) {
        digitalWrite(offlinePin, LOW);
        digitalWrite(onlinePin, HIGH);
        debugln("\nINFO: WiFi connected.");
        debug("- IP Address: ");
        debugln(WiFi.localIP());
    } else {
        debugln("\nERROR: WiFi connection failed. Running in offline mode.");
    }
}



// ---------------------------------------------
// Function to correctly save the currently stored certificate last write date.
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
// Function to correctly read the currently stored certificate last write date.
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

void copyFileFromSDToLocalFS(String filePath, String metadataFilePath, String fileName) {

  // Open files for reading from SD and writing to LittleFS
  File sdFile = SD.open(filePath, "r");

  if (!sdFile) {
    debugln("ERROR: Failed to open SD " + fileName +  " for copying.");
     return;  // Exit if SD file can't be opened
  } 
  else 
  {
    // Open local file for writing (using the same filePath).
    File localFile = LittleFS.open(filePath, "w");
    
    if (!localFile) 
    {
      debugln("ERROR: Failed to open local " + fileName +  " for writing.");
      sdFile.close();  // Close SD file if writing fails.
      return;  // Exit if local file can't be opened
    } 
    else {
      // Copy file from SD to local storage.
      while (sdFile.available()) {
        localFile.write(sdFile.read());
      }

      debugln("INFO: " + fileName +  " copied from SD to local storage successfully.");

      // Store the original file's timestamp for future reference.
      unsigned long sdFileTimestamp = sdFile.getLastWrite();

      writeTimestampToFile(sdFileTimestamp, metadataFilePath);

      debug("- " + fileName +  " last modification date: ");
      printTimestampAsDate(readTimestampFromFile(metadataFilePath));

      // Close both files after copying.
      sdFile.close();
      localFile.close();
    }
  }
}

void compareAndCopy( String filePath, String metadataFilePath, String fileName ) {

  bool localFileExists;
  bool sdFileExists;

  if (littleFSInitialized) {
    localFileExists = LittleFS.exists(filePath);
  } else {
    localFileExists = false;
  }

  if (sdCardInitialized) {
    sdFileExists = SD.exists(filePath);
  } else {
    sdFileExists = false;
  }

  debugln(localFileExists ? "INFO: "+ fileName +" found in LittleFS." : "WARNING: "+ fileName +" NOT FOUND in LittleFS.");
  debugln(sdFileExists ? "INFO: "+ fileName +" found in SD card." : "WARNING: "+ fileName +" NOT FOUND in SD card.");


  // CASE 1: Local config file exists
  if (localFileExists) {
    // ... now checking SD card.
    if (sdFileExists) {
      debugln("INFO: Comparing " + fileName + " timestamps...");

      // Open both the SD files to compare modification timestamps.
      File sdFile = SD.open(filePath, "r");

      if (!sdFile) {
        debugln("ERROR: Failed to open '" + fileName + "' from SD Card.");
        sdFile.close();  // Close the local file if SD file failed to open.
        return; // return here to avoid further code execution
      }

      // Get modification timestamps for both files.
      unsigned long localFileModStamp = readTimestampFromFile(metadataFilePath);
      unsigned long sdFileModStamp = sdFile.getLastWrite();

      // Print Local config file last modification time
      debug("- Local '" + fileName + "' last modified: ");
      printTimestampAsDate(localFileModStamp);

      // Print SD config file last modification time
      debug("- SD Card '" + fileName + "' last modified: ");
      printTimestampAsDate(sdFileModStamp);

      // Close file after reading timestamps.
      sdFile.close();

      // CASE 1A: SD certificate is newer -> Overwrite local copy
      if (sdFileModStamp > localFileModStamp) {
        debugln("INFO: SD Card '" + fileName + "' is more recent. Overwriting local copy.");
        copyFileFromSDToLocalFS(filePath, metadataFilePath, fileName);
      }
      // CASE 1B: Local certificate is already the latest -> No update needed
      else if (sdFileModStamp < localFileModStamp) {
        debugln("INFO: Local '" + fileName + "' is more recent. No changes applied.");
      }
      // CASE 1C: Both certificates have the same timestamp -> No action needed
      else {
        debugln("INFO: Local '" + fileName + "' is up to date. No changes applied.");
      }


    }
  }
  // CASE 2: Local certificate does not exist -> Copy from SD if available
  else {
    if (sdFileExists) {
      debugln("INFO: No local '" + fileName + "' found. Copying '" + fileName + "' from SD card...");
      copyFileFromSDToLocalFS(filePath, metadataFilePath, fileName);
    }
    // CASE 3: No certificates available in either location -> Warning
    else {
      debugln("WARNING: No '" + fileName + "' found in local storage nor SD card!.");
    }
  }
}

// Function to read configuration from a file (LittleFS or SD)
void loadConfigFromFile(String configFilePath) {

  File configFile = LittleFS.open(configFilePath, "r");

  if (!configFile) {
    debugln("ERROR: Failed to open Configuration file for reading.");
    return; // return here to avoid further code execution
  }

    while (configFile.available()) {
        String line = configFile.readStringUntil('\n');
        line.trim();

        if (line.startsWith("SSID=")) {
            WifiSSID = line.substring(5);
        } else if (line.startsWith("PASSWORD=")) {
            WifiPassword = line.substring(9);
        } else if (line.startsWith("SERVER=")) {
            serverTasksAddress = line.substring(7);
        }
    }
    configFile.close();
}
```


## :high_voltage: Uncommented version.
This version includes debug messages but does not include any type of comment.
```cpp
```


## :mute: Uncommented & no debug version.
This version does not include debug messages nor comment lines.
```cpp
```

