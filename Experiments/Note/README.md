# Conf

## Versions

### Project SDK: 
  - Under `File - Project Structure - Project Settings - Project - SDK`
  - Select `temurin-16` (`Eclipse Termurin version 16.0.2`)

### Virtual Device: 
  - Under `Tools - Android - AVD Manager - Create Virtual Device`.
  - Select `Pixel 4 API 30`

### Android SDK: 
  - Under `Tools - Android - SDK Manager`
    - SDK Platforms: Select `Android 11.0 (R)`
    - SDK Tools: Select the following
      - `Android SDK Build-Tools 33-rc1`
      - `Android SDK Commmand-line Tools (lastest)`
      - `Android Emulator`
      - `Android SDK Platform-Tools`

## Misc
  - How to resolve warning: *"SDK location not found. Define location with sdk.dir in the local.properties file or with an ANDROID_HOME environment variable"* in `IntelliJ`?
    - Under `Tools - Android - SDK Manager`, press the `Edit` button located after `Android SDK Location`, then press `Next`. The `SDK location` will be automatically fixed by `IntelliJ`.
  - Why the app didn't show when I pressed the run button (green triangle)?
    - Press it twice. The first starts the virtual machine and the second installs & start the app.