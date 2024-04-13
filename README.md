# TuxGuitar-InstaShred-MacOS
A TuxGuitar plugin to enable the use of this software with InstaShred guitars

Made with modifications to the [SimpleBLE](https://github.com/OpenBluetoothToolbox/SimpleBLE) github project from kevin@dewald.me and the [TuxGuitar Redraw Listener Plugin](https://sourceforge.net/p/tuxguitar/support-requests/37/) provided by Julian Casadesus.

## Description
TuxGuitar is a free and open-source multitrack guitar tablature editor and player written in Java. It can open/edit GuitarPro, PowerTab and TablEdit files. More information on TuxGuitar can be found at its [wikipedia page](https://en.wikipedia.org/wiki/TuxGuitar). This TuxGuitar-InstaShred-Windows plugin allows for the notes and chords of songs opened in TuxGuitar to be displayed on an InstaShred LED-enabled guitar in [realtime!](https://www.youtube.com/watch?v=LtRkjv9bZKI) 

More information on InstaShred guitars can be found at our [official website](https://www.instashred.com.au/). Happy shredding! 🎸🎼🎵

## Requirements
TuxGuitar-InstaShred-MacOS requires your computer to have a BLE/Bluetooth module, TuxGuitar installed (Java 9 minimum requirement) and an InstaShred guitar.  

## Installation Instructions
1. Download and install the appropriate MacOS **SWT** release from the [new GitHub TuxGuitar repository](https://github.com/helge17/tuxguitar/releases). Note this must be the -swt- release (not -jfx-)

2. Ensure that you can run TuxGuitar on your Mac, and that permissions are setup correctly
  - Extract TuxGuitar to your desired install destination
  - Right click on the Application file and click "Open"
  - This will come up with a warning around running unsigned applications, as this is an open-source project from GitHub

3. Provide TuxGuitar permissions on your Mac to access Bluetooth
  - Click on the Apple icon on the top left of your computer and go to "System Settings"
  - Search "Application" and select the option "Allow applications to access Bluetooth"
  - Add TuxGuitar program onto this list using the plus icon

4. Download the TuxGuitar Plugin files
  - SimpleBLE:
  - InstaShred Plugin: 

5. Move the required files to the plugin folder of your TuxGuitar installation
  - The correct **destination** folder for the plugin files (downloaded in step #4) is at: YOUR_TUXGUITAR_APPLICATION/Contents/MacOS/share/plugins. To access this folder, simply right click on the TuxGuitar application and select "Show Package Contents". You can then navigate this just like a normal folder structure, clicking through "Contexts/MacOS/share/plugins". There should be numerous .jar files in this folder.
  -  Move the tuxguitar-instashred--SNAPSHOT-jar-with-dependencies.jar file here
  -  Move 2 files from the SimpleBLE download here. These files are called "libsimpleble-c.dylib" and "libsimpleble.0.dylib", and essentially allow us to access the underlying Bluetooth functionality of MacOS to communicate with the InstaShred guitar.
  -  Both of the required .dylib files are in the SimpleBLE download folder. In: "Users/runner/work/SimpleBLE/SimpleBLE/build/install/lib". In this folder you should see 6 .dylib files and 2 folders. Copy the "libsimpleble-c.dylib" and "libsimpleble.0.dylib" files into the TuxGuitar plugins folder.

6. Run!
  - You are now ready to run the application! Double click the TuxGuitar icon to run.
  - After a few seconds a table will appear containing the discovered devices. If your guitar has not been discovered, simply rerun TuxGuitar and restart your InstaShred guitar.

## Troubleshooting
Please email lachlan@instashred.com if you have any issues or need help.
