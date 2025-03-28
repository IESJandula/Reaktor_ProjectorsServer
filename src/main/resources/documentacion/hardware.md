# 📔 About the hardware.
In this section, I will provide a detailed overview of the hardware used in this project, along with any valuable insights gathered during the development process.

## 📌 The main board - ESP32.
The board used in the project is a **Keyestudio ESP32-WROOM-32 Module Core Board**. 
The Keyestudio ESP32-WROOM-32 is a versatile development board designed for IoT and smart home applications. It integrates the ESP32-WROOM-32 module, offering both Wi-Fi and Bluetooth connectivity, and is compatible with the Arduino IDE.

**¿What languages can we use to develop for this board?**   
The ESP32 microcontroller can be programmed in several languages, depending on the development environment and use case.    
Among the most popular options:
- C / C++
- MicroPython
- JavaScript
- Lua
     
### 🔹 Keyestudio ESP32-WROOM-32 XX0H32 Specifications.
One important thing to mention is that the ESP32 typically has a 4MB flash chip on board. This flash memory is partitioned to allocate space for different purposes: the application (program) storage, the SPIFFS (or LittleFS) for file storage, and optionally, an OTA partition for **wireless firmware updates**. It is crucial to configure the partition scheme properly to allocate the correct amount of space for each purpose, depending on your project’s needs.
    
<table>
    <tr>
        <th>Feature</th>
        <th>Details</th>
        <th>Images</th>
    </tr>
    <tr>
        <td>Microcontroller</td>
        <td>ESP-WROOM-32 module</td>
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
        <td>80mA (average)</td>
    </tr>
    <tr>
        <td>Current Supply</td>
        <td>500mA (Minimum)</td>
    </tr>
    <tr>
        <td>Operating Temperature Range</td>
        <td>-40℃ ~ +85℃</td>
    </tr>
    <tr>
        <td>WiFi Mode</td>
        <td>Station/SoftAP/SoftAP+Station/P2P</td>
    </tr>
    <tr>
        <td>WiFi Protocol</td>
        <td>802.11 b/g/n/e/i (802.11n, speed up to 150 Mbps)</td>
    </tr>
    <tr>
        <td>WiFi Frequency Range</td>
        <td>2.4 GHz ~ 2.5 GHz</td>
    </tr>
    <tr>
        <td>Bluetooth Protocol</td>
        <td>Conforms to Bluetooth v4.2 BR/EDR and BLE standards</td>
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
        <td>34 (some with ADC/DAC support)</td>
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
        <td>Multiple</td>
    </tr>
    <tr>
        <td>I2C / SPI / UART</td>
        <td>Yes (multiple)</td>
    </tr>
</table>

![imagen](https://github.com/user-attachments/assets/7390d1e3-e9a5-4d1f-b9ea-9e96863f41ca)



## 📌 The comunication interface - RS232 to TTL converter.
An **RS232 to TTL converter** is a device used to interface two types of serial communication protocols: **RS232** and **TTL** (Transistor-Transistor Logic). These protocols operate at different voltage levels and require conversion to communicate effectively. **This device was essential in this project to enable communication between the ESP32 and the projector that needed to be controlled.** The **RS232 to TTL converter** changes the voltage levels from **RS232** signals to **TTL-compatible** levels and vice versa, allowing devices with different voltage levels to communicate with each other.
   
<p>
     <img align="right" src="https://github.com/user-attachments/assets/2f611e96-63cf-4b75-9c10-582fe3969af4">
</p>
   
### Breakdown of RS232 and TTL:
1. **RS232**:
   - It is an older standard used for serial communication between devices (e.g., PCs, modems, printers).
   - RS232 signals are **higher voltage** (typically between **±12V**).
   - **Data transmission** is done using voltage levels: logic “1” is typically represented by **+12V** (marking), and logic “0” by **-12V** (spacing).

2. **TTL (Transistor-Transistor Logic)**:
   - TTL is a logic level standard used in microcontrollers and embedded systems.
   - It uses **lower voltage levels** (typically **0V for logic “0”** and **3.3V or 5V for logic “1”**).
   - It is commonly used in modern electronics, especially for communication between microcontrollers like the ESP32 and other peripherals.
    
#### Key Features:
- **RS232 to TTL Conversion**: The converter translates the voltage levels between the two standards (RS232 and TTL).
- **Bidirectional**: Typically, these converters work in both directions, allowing communication in and out of a device.
- **Level Shifting**: It adjusts the voltage so that the signals are lowered to a level that a microcontroller (3.3V or 5V) can understand.

>[!Note]
> My specific unit features a chip called the MAX3232 ESE+2416, which is responsible for converting the voltage levels between RS232 and TTL, ensuring reliable communication between the ESP32 and the projector. Tha pin layout in my unit can be seen in the picture.
>
>       
>![srs232](https://github.com/user-attachments/assets/3cce1348-7e50-454f-9153-d7c20fea84fa)
    

## 📌 The mass storage - MicroSD Module
This module is perfect for adding mass storage to the project, allowing me to store and retrieve data from a microSD card. It is designed for use with microcontrollers, and communicates via a 4-pin SPI (Serial Peripheral Interface), which is widely supported across various microcontroller platforms. This makes it a versatile and easy-to-implement solution for integrating external storage into the project.

The microSD card module connects to the ESP32 using the default SPI pins. **Each pin on the module is clearly labeled, making it easy to identify and connect to the corresponding pins on the ESP32**, especially when referencing the manufacturer's documentation. **Powering the module is simple, as both the ESP32 and the `LC Technology MicroSD Module` operate at 3.3V**, eliminating the need for any level shifting between the two devices.

This unit is a LC Technology MicroSD Module, a widely used and reliable component. It is compatible with most projects designed for microSD cards, and its functionality will likely work seamlessly with other equivalent modules. The module is compact and easy to integrate into your hardware setup.

>[!NOTE]
>The module operates at 3.3V and that the ESP32 is also 3.3V compatible, so there's no need for a level shifter between the two (which is a common concern with other microcontrollers).

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


