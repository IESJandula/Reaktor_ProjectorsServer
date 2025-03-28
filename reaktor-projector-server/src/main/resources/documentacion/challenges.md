# ⛰️ Challenges encountered.
Here I will gather my discoveries and what i learn during my adventure with this new technology and project.

## 📍 Setting up the development environment.
Most guides online suggest to start using the Arduino IDE with the ESP32 and therefore this is what i did.
Before i could start programming the board I needed to get the ESP32 Board PAckage. 

To do that, click on the Board icon (`Board manager`) on the left panel.    
   
![imagen](https://github.com/user-attachments/assets/2ae11bce-a9cd-4083-b0d3-069a8a35cc16)

Make sure to choose the right option.   
   
![imagen](https://github.com/user-attachments/assets/61963493-aea2-4381-a567-e3e3cb0b3a40)

The IDE will start downloading the needed packages.    
   
![imagen](https://github.com/user-attachments/assets/bc720ce5-250e-4377-93d1-eccdc987b414)

Once installed the packages we will be able to select our board. **Remember to plug it in!**   
   
![imagen](https://github.com/user-attachments/assets/b3553df3-c3e5-4f34-b46d-4f2644b252d7)

Select the 'ESP32 Dev Module`.    
   
![imagen](https://github.com/user-attachments/assets/8c5fe0a7-f1ec-4814-9060-4cce1bd7fc60)

>[!CAUTION]
>On windows an additional step would be required:
>The USB to serial port chip of this control board is CP2102-GMR. So you need to install the driver for the chip.   
>You can click the driver tool download link:    
>https://www.silabs.com/products/development-tools/software/usb-to-uart-bridge-vcp-drivers

**Check if the driver for the board is installed.**    
On linux, many drivers come preinstalled with the Kernel, therefore before we start the installation process we should check if the needed driver for the board is already present.
In my case the board uses a CP2102-GMR, to check for the drivers in my kernel i used the following commands.
```bash
lsmod | grep cp201x
```
   
Giving me the postive result, the presence of this line means the drivers are currently installed.   
   
![imagen](https://github.com/user-attachments/assets/5c82aaed-f698-4756-971a-5bf2a21c0a31)

Afterwards I checked if the board was being recognized - also a positive result.

```bash
sudo dmesg | grep cp201x
```
    
![imagen](https://github.com/user-attachments/assets/971c7a81-e77c-482d-8f74-9b8e158052d5)
   
---
   
## 📍 Permission denied on output to board.
Since i work on a Linux Arch device and not a Microsoft Windows, i encountered a problem related to permissions to directly write on ports. This was a pain in the ass to diagnose and easy to resolve.
To cut it short, the solution was to add my user `jaydee` to the group that handles the direct access to the host ports. .

>[!NOTE]
>This configuration is necessary because the Arduino IDE in most cases cannot be run as a super user and even when possible is not a good idea to do so. 

#### 1 - Find out the port that requires access.
To find out  the port go to your arduino and look for the board, it will show the connection port.   

![imagen](https://github.com/user-attachments/assets/8cb9636f-0769-4113-a724-a3195c7b2abc)
   
#### 2 - Find the group that handles the port.
To to that type the following command in your terminal `ls -l <your port>`.
```bash
ls -l /dev/ttyUSB0
```

This will give the following result.
![imagen](https://github.com/user-attachments/assets/fdbd6b06-476e-46ec-a3b8-de3352950a82)

#### 3 - Add the user to the found group.
Now we just need to add our current user to the group that we have discovered.
To do this use the following syntax.
```bash
sudo usermod -a -G <gruop> <user>
```
#### 4 - Restart the computer to test the configuration.
The changes will not be effective until we reset our computer so this step is needed to make things work.

https://support.arduino.cc/hc/en-us/articles/360016495679-Fix-port-access-on-Linux

Restar to test the device.
```c++
int ledPin = 2; // Pin where the LED is connected

void setup() {
  // put your setup code here, to run once:
  pinMode(ledPin, OUTPUT);  // Set the LED pin as an output
  digitalWrite(ledPin, HIGH);  // Turn the LED on initially
}

void loop() {
  // put your main code here, to run repeatedly:
  digitalWrite(ledPin, HIGH);  // Turn the LED on
  delay(1000);  // Wait for 1 second (1000 milliseconds)
  
  digitalWrite(ledPin, LOW);  // Turn the LED off
  delay(1000);  // Wait for 1 second (1000 milliseconds)
}

```
   
---
   
## 📍 The IDE Serial monitor is not displaying characters correctly.
When wworking with output to the serial monitor for testing, the result was not what I expected. All I got was a mess of garbled characters.

![imagen](https://github.com/user-attachments/assets/23b68269-b40f-4c73-a408-21feac2c55e7)

Potential reasons for this issue were the desyncronized BAUD rate from the programme, chip and serial monitor. But in my case this had nothing to do with it.
To check the BAUD rate of my specific unity, i ran the command `sudo dmesg | grep tty`.    
   
![imagen](https://github.com/user-attachments/assets/0227d738-3ccf-43ec-bd60-c6b42f461615)

TURNS OUT, I only needed to push the 'EN' button on the board, which resets the board to make it work propperly.
   
---
   
## 📍 Connecting to the wifi network as a client.
To utilize the Wi-Fi capabilities, the device must first connect to a Wi-Fi network. To achieve this, I modified an example script to report connection status directly to the serial monitor. After making the necessary changes, I verified the device's successful connection by checking its presence on my router, ensuring the program and device were functioning correctly.
```c++
#include <WiFi.h>

void setup() {

  Serial.begin(115200);
  WiFi.begin("mySSID", "myPass");
  Serial.print("Connecting to WiFi");

  while (WiFi.status() != WL_CONNECTED) {
    Serial.print(".");
    delay(500);
  }

  Serial.print("\nConnected to the wifi.");
  Serial.print("\nIP ADDRESS: ");
  Serial.println(WiFi.localIP());
}

void loop() {

  if ((WiFi.status() == WL_CONNECTED)) {
    Serial.print("\nConnection online.");
    Serial.flush();
  } else {
    Serial.print("\nConnection lost.");
    Serial.flush();
  }
  delay(2000);
}
```

And once the serial monitor was configured i pushed the EN button and i got the expected result.    
   
![imagen](https://github.com/user-attachments/assets/c36515af-1969-4d9b-ab33-2c6f0eb9d82c)
   
In a later revision I added two leds that indicate the status of the connection.



---
   
## 📍 Finding the MAC address of the device.
Once the device was connected to my network, I simply searched for it among the devices listed in my Wireless LAN users.    
    
![imagen](https://github.com/user-attachments/assets/7ad795ea-f9d1-4880-b2ad-af1d1d1354ae)

## 📍 Sending request to the server.
I only put these observations here because I had no clue how to do this given that is my first time working with this Micro.
To send a request to an HTTP server we need to follow these steps.
- Include the needed library.
- Create the client object.
- Initialize the object with the connection string (endpoint url).
- Send the request with the chosen request method, assign to a variable the result of the call.
- Do whatever is needed with the result.

**Include the necessary library.**    
The HTTPClient library provides the necessary functions to send requests and receive responses from an HTTP server.
```c++
#include <HTTPClient.h>
```

**Create the HTTP client object.**   
We need to create an instance of `HTTPClient`, which will be used to make HTTP requests (GET, POST, etc.).
```c++
// Create the client object.
HTTPClient http;
```
   
**Initialize the object with the connection string.**
```c++
// Initialize the HTTP request by specifying the target URL.
http.begin("http://192.168.1.100:8085/micro-greeting");
```

**Send the request and process the result.**   
Now we send the request using the `.GET()` method and store the response code.
```c++
// Send the GET request and store the response code.
int httpResponseCode = http.GET();

// Print the response code.
Serial.print("HTTP Response Code: ");
Serial.println(httpResponseCode);
```

## 📍 Handle API responses of JsonObjects.
By default we wont be able to handle the json objects we receive. To fix that we can use the `ArduinoJson` library to parse JSON.

First we must install the library in our IDE.
Go to `Tools` -> `Manage Libraries...` or click on the Books icon in the left hand panel.   
    
![imagen](https://github.com/user-attachments/assets/a39e1373-dba4-4611-8b74-bb496095c729)

Search for `ArduinoJson` and click on `Install`.  
   
![imagen](https://github.com/user-attachments/assets/2a36c1c3-3feb-43dd-a877-16f4a24ea48d)


Then we include the needed library to parse Json Objects.
```c++
#include <ArduinoJson.h>
```

**Get the response code**   
```c++
int httpResponseCode = http.GET();
```

**Fetch the JSON Response**   
This retrieves the raw JSON string returned by the server.
```c++
String payload = http.getString();  // Get response as a string
```

**Parse JSON using ArduinoJson**  
```c++
// Allocates memory to store JSON. You can increase this if you want or need to.
DynamicJsonDocument doc(1024);
// Parses the JSON string into a usable object.
DeserializationError error = deserializeJson(doc, payload);
```

**Extract and access the data**   
```c++
const char* message = doc["message"];
long timestamp = doc["timestamp"];
bool success = doc["success"];
```

## 📍 Changing partition configuration to improve Storage and Memory size.
The ESP32's 4MB flash memory stores firmware, SPIFFS/LittleFS files (e.g., SSL certificates, configs), and NVS data (WiFi credentials, settings). If OTA updates are enabled, space is reserved for new firmware. A small portion is used for the bootloader and partition management. In my case OTA will not be a funcionality that will be used so I can choose a different partition scheme which grants more space for LittleFS and my program. 

>[!TIP]
>To select a new partition scheme, click on `Tools` > `Partition Scheme` > `{Desired partition}`.

![imagen](https://github.com/user-attachments/assets/06fa7569-6017-4160-96ab-9aac3be0406a)


## 📍 LittleFS would not mount.
After partitioning and giving it a go, for some reason the mounting operation for the FS failed constantly.  This happens because the flash memory must be formatted at least once before it is used.    
To format the flash memory use this instruction:
   
```c++
LittleFS.begin(true)
```
   
This instruction will format the file system and then try to initialize it.

A good idea is, when possible, try adding a fallback initialization with a format operation included such as this:   
   
```c++
  // Initialize LittleFS without formatting.
  if (!LittleFS.begin()) {
    debugln("Failed to mount LittleFS. Formatting now.");
    // Initialize trying to format.
    if (!LittleFS.begin(true)) {
      debugln("Failed to mount LittleFS even after formatting.");
    }
  }
  debugln("LittleFS mounted successfully.");
```

**Detailed Explanation:**    
- **`LittleFS.begin()`:** This tries to mount the LittleFS filesystem. If the filesystem is not mounted or initialized correctly (for example, if it's corrupted or missing), it will fail.
- **`LittleFS.begin(true)`:** By passing true as an argument, you are telling the function to force formatting the filesystem before trying to initialize it. So, if the filesystem cannot be mounted, it will automatically try to format the partition and then initialize it.

## 📍 Optimization of Json objects to reduce RAM impact and memory fragmentation.

## 📍 Storing data into the flash memory (non-volatile storage).
To achieve this objective I used `LittleFS_esp32` library.
Thiss library allowed me to mount an extremely lightweight file system on the ESP32 flash memory. To get it to work I had to install the library and include it in the project before using any functionality.
   
![imagen](https://github.com/user-attachments/assets/1d5b3c96-4fbc-4119-9714-bce55c0b73b7)
    

https://randomnerdtutorials.com/esp32-microsd-card-arduino/


## 📍 Storing the SSL certificate into the flash memory of the device.
To make certain requests the device needed an SSL certificate and this had to be stored in the flash memory of the device.

For this I used the LittleFS which is a file system that allows to store files in the flash memory, including my SSL certificate.
This would allow me to upload the SSL certificate as a file to the flash filesystem, then access it in my application.

The SSL update mechanism i wanted to employ is the following:
The device turns on, checks the memory for an SSL, if it exits, it compares it to the one stored in the SD card, if the one in the SD card is newer, copies and overwrites the one present in the device.

The main mechanism for LittleFS seems to be rather easy to work with.
Some main orders are the following.

```cpp
// returns true if the file exists, false otherwise.
if (LittleFS.exists("/example.txt")) {
  Serial.println("File exists");
} else {
  Serial.println("File does not exist");
}


// Opens the file in the selected mode.
// "r" = read.
// "w" = write. -> Used to overwrite existing files or write to new ones.
// "a" = append.
File file = LittleFS.open("/example.txt", "w");
if (file) {
  file.println("Hello, LittleFS!");
  file.close();
}

// Delete a file.
if (LittleFS.remove("/example.txt")) {
  Serial.println("File deleted successfully");
} else {
  Serial.println("Failed to delete file");
}

// Rename a file.
if (LittleFS.rename("/oldfile.txt", "/newfile.txt")) {
  Serial.println("File renamed successfully");
} else {
  Serial.println("Failed to rename file");
}

File file = LittleFS.open("/example.txt", "r");
if (file) {
  Serial.print("File size: ");
  Serial.println(file.size());
  file.close();
}

// Always close the file after operations
file.close(); 

```

## 📍 SD Certificate always percieved as more recent.
Even ater copying the certificate to the device, the SD certificate, although being the very same, would always be detecetd as more recent.
This could've happened due to several factors.
One of them being that the LittleFS and SDfs might handle timestamps differently therefore giving back different type of results or units. To solve this problem I thought of using an NTP server as an external source of precise date time and sync the ESP32 date time with the given one.
I found this interesting [blog post](https://vasanza.blogspot.com/2021/08/esp32-sincronizar-rtc-interno-con.html).

First thing I included the time library. 
```cpp
#include <ESP32Time.h>
```
Then create an ESP32Time object which represents the internal real time clock.
```cpp
ESP32Time rtc;
```
Then configure the NTP request parameters.
```cpp
// NPT server config
const char* ntpServer = "pool.ntp.org"; // Servidor NTP para sincronización horaria.
const long gmtOffset_sec = 1 * 3600; // Offset de GMT para Madrid (GMT+1).
const int daylightOffset_sec = 1 * 3600; // Offset adicional para horario de verano (GMT+2 durante verano).
```
Now syc the RTC with the ntp values.
```cpp
  // Start configuring timezone.
  configTime(gmtOffset_sec, daylightOffset_sec, ntpServer);
  struct tm timeinfo;
  if (getLocalTime(&timeinfo)) {
     Serial.println(&timeinfo, "Hora local: %H:%M:%S");
  } else {
    Serial.println("Error al obtener la hora.");
  }
```

Based on this configuration, we can use the `struct tm` object 'timeinfo' to get the desired information.
```cpp
void loop() {
  // Obtener y mostrar la hora actual periódicamente
  struct tm timeinfo;
  if (getLocalTime(&timeinfo)) {
    Serial.println(&timeinfo, "Hora local: %H:%M:%S");
  }
  delay(1000); // Actualizar cada segundo
```
   
**Another issue arises.**
Basically, the ESP32's file system libraries (including LittleFS and SD) do not directly provide a function to preserve the creation timestamp during file copy operations. When you copy a file, it will inherit the timestamp of the time when the copy operation occurs. This is not what i want for my project.

However, you can work around this limitation by manually saving the original file's timestamp before copying the file and then applying it to the new file in LittleFS after the copy operation.

