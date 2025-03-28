# Unit info.
name: esp32-10E060   
MAC: 3c:61:05:10:e0:60   
Default Baud Rate: 115200   


# 📍 Check for the presence of the SD card before mounting.
```cpp
bool isSDCardInserted() {
  // Try initializing the SD card
  if (!SD.begin(sdCardChipSelect)) {
    Serial.println("⚠️ No SD card detected or initialization failed.");
    return false;
  }

  // Try to open the root directory to confirm the card is accessible
  File testFile = SD.open("/");
  if (!testFile) {
    Serial.println("⚠️ SD card not accessible.");
    return false;
  }

  // If we got here, the SD card is detected and accessible
  Serial.println("✅ SD card detected and accessible.");
  return true;
}
```

# 📍 Mounting the SD card with the presence check function.
```cpp
  if (isSDCardInserted()) {
    Serial.println("🟢 Mounting SD card...");
    if (!SD.begin(sdCardChipSelect)) {
      Serial.println("❌ SD Card mount failed.");
    } else {
      Serial.println("✅ SD Card mounted successfully.");
    }
  } else {
    Serial.println("❌ No SD Card found.");
  }
}
```
