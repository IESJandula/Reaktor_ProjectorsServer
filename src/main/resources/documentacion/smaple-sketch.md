# Sample sketch.
```c++
// Library  for Wifi connection.
#include <WiFi.h>
// Library for HTTP request.
#include <HTTPClient.h>
// Library for RS232 comms.
#include <HardwareSerial.h>

// Definitions for Server requests.
#define serverAddress "http://192.168.1.100:8085/micro-greeting"

// Definitions for WiFi connection
#define offlinePin 4
#define onlinePin 0
#define functionPin 2
#define WifiSSID "¿?"
#define WifiPassword "¿?"

// Definitions for SR232 communication.
#define txPinSR232 17  // TX pin for RS232
#define rxPinSR232 16  // RX pin for RS232

// Definitions for SD-CARD communication.
#define txPinB 9
#define rxPinB 10

// Other textual substitution.


// Debug configuration definitions
#define DEBUG 1

#if DEBUG == 1
#define debug(x) Serial.print(x)
#define debugln(x) Serial.println(x)
#else
#define debug(x)
#define debugln(x)
#endif

// Use UART2 on ESP32
HardwareSerial sr232Port(2);

void setup() {
  // Connection baudrate to serial monitor output.
  Serial.begin(115200);

  // Configure wifi signal pins as outputs
  pinMode(offlinePin, OUTPUT);
  pinMode(onlinePin, OUTPUT);

  // Start WiFi connection
  WiFi.begin(WifiSSID, WifiPassword);
  debug("Connecting to WiFi");

  // Indicate offline status while connecting
  digitalWrite(offlinePin, HIGH);
  digitalWrite(onlinePin, LOW);
  digitalWrite(functionPin, LOW);

  // connection loop
  while (WiFi.status() != WL_CONNECTED) {
    digitalWrite(offlinePin, HIGH);
    debug(".");
    delay(250);
    digitalWrite(offlinePin, LOW);
    delay(250);
  }

  // Indicate successful connection
  digitalWrite(offlinePin, LOW);
  digitalWrite(onlinePin, HIGH);
  debugln("\nConnected to WiFi.");
  debug("IP Address: ");
  debugln(WiFi.localIP());

  // Establishin connection to the serial port.
  sr232Port.begin(9600, SERIAL_8N1, rxPinSR232, txPinSR232); // (Baud rate, config, RX, TX)

    // Define the byte array that corresponds to your command
  byte command[] = {
    0x2A, 0x20, 0x30, 0x20, 0x49, 0x52, 0x20, 0x30, 0x33, 0x32, 0x0D
  };
  
  // Send the command byte by byte
  for (int i = 0; i < sizeof(command); i++) {
    sr232Port.write(command[i]);
  }

  callServer();

}

void loop() {
  if (WiFi.status() == WL_CONNECTED) {
    debugln("Connection online.");
    digitalWrite(onlinePin, HIGH);
    digitalWrite(offlinePin, LOW);
  } else {
    debugln("Connection lost.");
    digitalWrite(onlinePin, LOW);
    digitalWrite(offlinePin, HIGH);

    // Try reconnecting
    WiFi.disconnect();
    WiFi.reconnect();
  }

  delay(2000);
}

void callServer() {
  debugln("Calling server");

  if (WiFi.status() == WL_CONNECTED) {
    debugln("WiFi connected.");

    HTTPClient http;

    http.begin(serverAddress);

    int httpResponseCode = http.GET();
    String httpResponseData = http.getString();

    if (httpResponseCode > 0) {
      debug("HTTP RESPONSE CODE:");
      debugln(httpResponseCode);

      if (httpResponseData == "turn-on") {
        delay(5000);
        pinMode(functionPin, OUTPUT);
        debugln(httpResponseData);
        digitalWrite(functionPin, HIGH);
        sr232Port.write();
      }


    } else {
      debugln("HTTP ERROR CODE:");
      debugln(httpResponseCode);
    }

    http.end();

  } else {
    debugln("WiFi NOT connected.");
  }
}
```
