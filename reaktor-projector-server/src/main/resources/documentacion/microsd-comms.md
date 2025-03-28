# 📌 Setup of the MicroSD Card with SPI interface.
The microSD card module communicates using SPI (Serial Peripheral Interface) communication protocol. You can connect it to the ESP32 using the default SPI pins.

## 📍 Wiring the module to the ESP32.
The module connection is labeled and we can easily identify the corresponding pin on the board with the reference image from the manufacturer. 

>[!Caution]
>These pins can be reassigned using HSPI and VSPI; however, it is **not recommended** in most cases due to potential differences in performance, compatibility with existing libraries, and default hardware optimizations.

<table>
  <tr>
    <th>MicroSD Card Module</th>
    <th>ESP32</th>
    <th>Reference image</th>
  </tr>
  <tr>
    <td>3V3</td>
    <td>3.3V</td>
    <td rowspan="6"><img src="https://github.com/user-attachments/assets/7390d1e3-e9a5-4d1f-b9ea-9e96863f41ca" alt="ESP Pinut" width="600"></td>
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

### 🔹 Wiring colours.
In typical wiring setups for SPI communication, MISO is commonly wired with yellow, MOSI with green, CLK with blue, and CS with orange or purple. These color conventions help easily distinguish between the different SPI signals, making it easier to correctly connect devices such as the microSD card module and ESP32. However, it's always important to verify the color coding used in your specific components.

<table border="1">
  <tr>
    <th>Pin</th>
    <th>Common Color</th>
  </tr>
  <tr>
    <td>MISO</td>
    <td>Yellow</td>
  </tr>
  <tr>
    <td>MOSI</td>
    <td>Green</td>
  </tr>
  <tr>
    <td>CLK</td>
    <td>Blue</td>
  </tr>
  <tr>
    <td>CS</td>
    <td>Orange/Purple</td>
  </tr>
</table>



## 📍 Pins definition.

**Explanation of the pins**   
    
<table border="1">
  <tr>
    <th>MicroSD Card Module Pin</th>
    <th>ESP32 Pin</th>
    <th>Description</th>
  </tr>
  <tr>
    <td>3V3</td>
    <td>3.3V</td>
    <td>Powers the module with 3.3V. The microSD module operates at 3.3V, and the ESP32 provides 3.3V to power it.</td>
  </tr>
  <tr>
    <td>CS</td>
    <td>GPIO 5</td>
    <td>Chip Select (CS) is used to select the SD card for communication. When CS is LOW, the module is selected and ready to communicate.</td>
  </tr>
  <tr>
    <td>MOSI</td>
    <td>GPIO 23</td>
    <td>Master Out Slave In (MOSI) is the data line where the master (ESP32) sends data to the microSD card.</td>
  </tr>
  <tr>
    <td>CLK</td>
    <td>GPIO 18</td>
    <td>Clock (CLK) provides the clock signal for synchronizing data transmission between the ESP32 and the microSD card.</td>
  </tr>
  <tr>
    <td>MISO</td>
    <td>GPIO 19</td>
    <td>Master In Slave Out (MISO) is the data line where the microSD card sends data to the master (ESP32).</td>
  </tr>
  <tr>
    <td>GND</td>
    <td>GND</td>
    <td>The Ground (GND) pin connects the ground of the microSD module to the ESP32 ground.</td>
  </tr>
</table>

Assign these pins in your sketch with the preferred name.
```c++
// SD Card definitions.
#define sdCardMOSI 23
#define sdCardMISO 19
#define sdCardClock 18
#define sdCardChipSelect 5
```

## 📍 Use a test script.
There are several examples in Arduino IDE that show how to handle files on the microSD card using the ESP32. In the Arduino IDE, go to File > Examples > SD(esp32) > SD_Test, or copy the following code. 

>[!CAUTION]
> **The card must be formatted in FAT32 format**.


