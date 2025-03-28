# 📌 Setup of the RS232 port communications.
To send a signal from an ESP32 to an RS232 device, you need to match the communication settings (baud rate, data bits, stop bits) and ensure correct wiring. Since the ESP32 uses 3.3V logic and RS232 uses higher voltage levels (±12V), you must use a level shifter or an RS232-to-TTL converter (like a MAX232) to safely interface the two and avoid damaging the ESP32.

## 📍 0.1 - Identify the TX and RX pins on the board for UART2.
The RS232 must be connected to the TX and RX pins of the ESP32 board, whose locations can vary depending on the specific model. To ensure proper connection, it's important to use the correct UART pins. For RS232 communication, we should use UART2.

The default pins for UART2 on the ESP32 are as follows:
- **TX:** `Pin 17`
- **RX:** `Pin 16`

```c++
// Definitions for SR232 communication.
#define txPinSR232 17  // TX pin for RS232
#define rxPinSR232 16  // RX pin for RS232
```

>[!IMPORTANT]
>These are the pins commonly used for UART2 communication on many ESP32 boards. If you're using a different set of pins or have remapped them, ensure to update the pin numbers accordingly in the code.
   
**Image for reference**   
   
![imagen](https://github.com/user-attachments/assets/7390d1e3-e9a5-4d1f-b9ea-9e96863f41ca)

## 📍 0.2 - Gather information about the SR232 device interface.
This information is necessary to configure the communication protocol. Information necessary is.
- Default Baud Rate.
- Data bits.
- Parity bits.
- Stop bits.

In my case, for my specific device I have the following information provided by the manufacturer.  
         
### RS232 Communication with Projector

#### Connection Lines
<table>
  <tr>
    <th>Signal</th>
    <th>Function</th>
  </tr>
  <tr>
    <td><b>RxD</b></td>
    <td>Receives data from the external computer</td>
  </tr>
  <tr>
    <td><b>TxD</b></td>
    <td>Transmits data to the external computer</td>
  </tr>
  <tr>
    <td><b>GND</b></td>
    <td>Ground for data signals</td>
  </tr>
</table>

#### COM Port Settings
<table>
  <tr>
    <th>Parameter</th>
    <th>Value</th>
  </tr>
  <tr>
    <td><b>Baud Rate (Default)</b></td>
    <td>9600</td>
  </tr>
  <tr>
    <td><b>Data Bits</b></td>
    <td>8</td>
  </tr>
  <tr>
    <td><b>Parity</b></td>
    <td>None</td>
  </tr>
  <tr>
    <td><b>Stop Bit</b></td>
    <td>1</td>
  </tr>
</table>


>[!Note]
>**Why is this information important?**    
>To establish a reliable connection and communicate properly with the device, we need to configure key communication parameters. These settings define the speed and structure of the data exchange, ensuring compatibility between devices.
>
>In the following example:
>```c++
>mySerialPort.begin(9600, SERIAL_8N1, 16, 17); // (Baud rate, config, RX, TX)
>```  

## 📍 0.3 -  Logic Level Conversion

The ESP32 operates at 3.3V TTL logic, while RS232 operates at ±12V logic levels. Directly connecting them will damage the ESP32.
To convert the signal levels, use an RS232 to TTL adapter/module, such as:

- MAX232 module (common and cheap)
- SP3232 module (better for 3.3V logic)

These modules will shift the voltage levels safely.

>[!TIP]
>In my case, I’ll be using a MAX232. To check your own chip, inspect your module, find the installed chip, and note its name.
>
>![imagen](https://github.com/user-attachments/assets/f043ed6b-b8a6-4fc0-8619-04fa217aed6c)



#### 🔸 What Does the MAX232 Do?
The MAX232 is used to convert voltage levels between TTL (Transistor-Transistor Logic) and RS-232 serial communication standards.
- TTL Logic (0V and 5V or 3.3V): Used by microcontrollers (like Arduino, PIC, or AVR).
- RS-232 Signals (-12V to +12V or -10V to +10V): Used for serial ports on PCs and other communication devices.

**How It Works:**
- The MAX232 takes a 5V power supply and internally generates the higher positive and negative voltages needed for RS-232 communication using charge pumps.
- It has built-in capacitors (external ones are needed in some versions) for voltage conversion.
- It provides two drivers and two receivers, meaning it can handle two transmit and two receive lines.

   
## 📍 1 - Wiring connections.
To correctly wire the connections, refer to the annotations on the module board. **`TX`** stands for Transmission, and **`RX`** stands for Reception. Connect the wires to the designated pins based on the previous steps.

![srs232](https://github.com/user-attachments/assets/3cce1348-7e50-454f-9153-d7c20fea84fa)

>[!TIP]
>The common color scheme is as represented in the picture.   
>- Black for Ground.   
>- Red for VCC or positive.   
>- Yellow (or Orange) for Transmission (TX).
>- Green for Reception (RX).

## 📍 2 - Set up the variable in the programme.
Now it's time to define the text-based substitutions for the pins and port configurations we've noted so far.
   
```c++
// Library for RS232 communication
#include <HardwareSerial.h>

// Definitions for SR232 communication.
#define txPinSR232 17  // TX pin for RS232
#define rxPinSR232 16  // RX pin for RS232

// Projector serial communication settings
#define proyectorRate 9600  // Baud rate: 9600
// Data Bits: 8
// Parity: None
// Stop Bits: 1
```

## 📍 3 - Initialize the HardwareSerial class with a UART type.
The `HardwareSerial` is a class that allows using the ESP32's hardware UARTs. 

>[!Note]
>UART (Universal Asynchronous Receiver/Transmitter) is a serial communication protocol used in ESP32 to enable communication between devices such as sensors, microcontrollers, and computers. It is a simple and efficient way to send and receive data without requiring a clock signal.
   
```c++
// Use UART2 on ESP32
HardwareSerial mySerialPort(2);
```

**Explanation:**   
The ESP32 has three hardware UARTs:
- UART0 → Used for debugging (default Serial).
- UART1 → Usually connected to the onboard flash memory (avoid using it).
- UART2 → Available for general use (best choice for RS232 communication).

So by using `HardwareSerial mySerialPort(2);` I am telling the ESP32 to use UART2 for serial communication.

By default, UART2 is mapped to:
- TX → GPIO17
- RX → GPIO16

However, these pins can be changed using:
```c++
mySerial.begin(9600, SERIAL_8N1, txPinSR232, rxPinSR232);  // RX on GPIO16, TX on GPIO17

```

## 📍 4 - Perform handshake if needed.
At this point, the serial communication port has been initialized, but you still need to establish a connection with the device (e.g., a projector). This typically involves sending an initial "handshake" or some command to verify the connection is active. In my specific case there is no need for a handshake since the device does not required it.

## 📍 5 - Send data to the port.
To send data from the ESP32 to the RS232 device (e.g., projector), you can use the `print()`, `println()` or `write()` function. These functions allow you to send strings or byte arrays over the serial communication.

```c++
rs232Port.println("Command: Power On");
```

This will transmit the string `"Command: Power On"` to the RS232 device. You can use other commands based on your device's protocol.

#### 🔸 To write an array of bytes use the following instructions.    
To write the byte array for this command : `* 0 IR 001\r`	`2A 20 30 20 49 52 20 30 30 31 0D`	`Power On` 

```c++
    // Define the byte array that corresponds to your command
  byte command[] = {
    0x2A, 0x20, 0x30, 0x20, 0x49, 0x52, 0x20, 0x30, 0x33, 0x32, 0x0D
  };
  
  // Send the command byte by byte
  for (int i = 0; i < sizeof(command); i++) {
    sr232Port.write(command[i]);
  }
```
