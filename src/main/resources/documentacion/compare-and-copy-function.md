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
