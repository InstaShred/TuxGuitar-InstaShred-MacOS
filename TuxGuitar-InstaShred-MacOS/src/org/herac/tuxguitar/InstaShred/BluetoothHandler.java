package org.herac.tuxguitar.InstaShred;

import simpleBLE.SimpleBle_JNA;
import simpleBLE.SimpleBle_JNA.libsimpleble;
import simpleBLE.SimpleBle_JNA.libsimpleble.size_t;
import simpleBLE.SimpleBle_JNA.libsimpleble.uuid_t;

import com.sun.jna.Pointer;

import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.swt.widgets.TableItem;
import org.eclipse.swt.widgets.MessageBox;

// Handles the Bluetooth connection
// connects based on the CYBT service UUID's so should be universal << incorrect as of 2023
// once connected it then sends a connected lightshow and sets the peripheral to myBluetoothPeripheral
public class BluetoothHandler {
	
	// Initialise the JNA instance for SimpleBLE
	private SimpleBle_JNA simpleble = new SimpleBle_JNA();
	private uuid_t.ByValue service_uuid = new uuid_t.ByValue();	// CYBT 65333333-A115-11E2-9E9A-0800200CA100
	private uuid_t.ByValue characteristic_uuid = new uuid_t.ByValue();	// CYBT 65333333-A115-11E2-9E9A-0800200CA102
	Pointer adapter = null;
	Pointer peripheral = null;
	
	// CONSTANT FW VALUES
	private byte UPDATE = (byte) 0xff;
	private byte SPECIAL = (byte) 0x8a;
	private byte OFF = (byte) 0x02;
	private byte CONNECTED = (byte) 0x03;
	
	// GUI
	Shell shell = new Shell(Display.getDefault(), SWT.ON_TOP | SWT.SHELL_TRIM);
	
	// Lightshow commands
	public void sendConnected() {
		// Not sure if I need this setting?
		// Native.setProtected(true);
		System.out.println("Sending connected");
		
		simpleble.connectToPeripheral(peripheral);
		
		byte[] data = new byte[]{SPECIAL, CONNECTED, UPDATE};
		size_t data_length = new size_t(data.length);
		
		libsimpleble.service_t service_t = new libsimpleble.service_t(); 
		
		// Want to break on here and view what the service_t variable looks like
		boolean success = !simpleble.getPeripheralServices(peripheral, 0, service_t);
		libsimpleble.characteristic_t characteristic_t = service_t.characteristics[1]; 
		
		// Set the service and characteristic uuids
		System.arraycopy(service_t.uuid.value, 0, service_uuid.value, 0, service_t.uuid.value.length);
		System.arraycopy(characteristic_t.uuid.value, 0, characteristic_uuid.value, 0, characteristic_t.uuid.value.length);
		
		// for some reason this is the correct calling order???
		simpleble.writeCommand(peripheral, data, data_length, service_uuid, characteristic_uuid);
		
		System.out.println("Connected!");
	}
	
	public void sendData(byte[] data) {
		//System.out.println("Sending data");
		size_t data_length = new size_t(data.length);
		simpleble.writeCommand(peripheral, data, data_length, service_uuid, characteristic_uuid);
	}
	
	public void sendMusicData(byte[] dataBuf, int len) {
		// System.out.println("Sending note data");
		// bufLen is full length of msg, len is the check byte to send to guitar
		int bufLen = len*3;
		
		// data to send
		byte[] data = new byte[bufLen+1];
		
		// create the data buffer
		for (int i = 0; i <= bufLen; i++) {
			// all the note data
			if (i < bufLen) {
				data[i] = dataBuf[i];
			}
			// one byte length value
			else {
				data[i] = (byte) len;
			}
		}
		
		// send the data+len
		size_t data_length = new size_t(data.length);
		simpleble.writeCommand(peripheral, data, data_length, service_uuid, characteristic_uuid);
	}
	
	public void clear() {
		//System.out.println("Clearing the fretboard");
		byte[] data = new byte[]{SPECIAL, OFF};
		size_t data_length = new size_t(data.length);
		simpleble.writeCommand(peripheral, data, data_length, service_uuid, characteristic_uuid);
	}
	
	public void update(){
		//System.out.println("Updating the fretboard");
		byte[] data = new byte[]{UPDATE};
		size_t data_length = new size_t(data.length);
		simpleble.writeCommand(peripheral, data, data_length, service_uuid, characteristic_uuid);
	}
	
	public boolean isConnected() {
		// Argument to simpleble_peripheral_is_connected is Pointer to peripheral and Pointer to isConnected
		boolean[] isConnected = {false};
		if (peripheral != null) {
			simpleble.isPeripheralConnected(peripheral, isConnected);
			if (isConnected[0]) {
				return true;
			}
		}
		return false;
	}
	
	public void disconnect() {
		if (peripheral != null) {
			if (isConnected()) {
				System.out.println("Disconnecting");
				clear();
				update();
				simpleble.peripheralDisconnect(peripheral);	
				simpleble.releasePeripheral(peripheral);
			}
		}
		shell.dispose();
	}
	
	// TODO: Table doesnt work on reload?
	Table createTable() {
		
		// Create the table/shell
		shell.setText("Discovered Devices");
		shell.setSize(800, 600);
		shell.setLayout(new GridLayout(1, false));
		
		Table bleTable = new Table(shell, SWT.BORDER | SWT.FULL_SELECTION);
		bleTable.setHeaderVisible(true);
		bleTable.setSize(800, 500);
		bleTable.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		bleTable.setParent(shell);
		
		// Create columns
		TableColumn colDeviceName = new TableColumn(bleTable, SWT.NONE);
		colDeviceName.setText("Device Name");
		colDeviceName.setWidth(400);
		
		TableColumn colDeviceAddr = new TableColumn(bleTable, SWT.NONE);
		colDeviceAddr.setText("Device Address");
		colDeviceAddr.setWidth(400);
		
		// Create the connection button
		Button connectButton = new Button(shell, SWT.PUSH);
		connectButton.setText("Connect");
		connectButton.setTouchEnabled(true);
		connectButton.setLayoutData(new GridData(SWT.CENTER, SWT.END, true, false));
		
		connectButton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent e) {
				System.out.println("Connecting ...");
				TableItem[] selectedDevice = bleTable.getSelection();
				int selectedDeviceInt = bleTable.getSelectionIndex();
				
				peripheral = simpleble.getPeripheralHandle(adapter, selectedDeviceInt);
				
				// release all other handles
				int deviceCount = simpleble.getScanResultsCount(adapter);
				for (int i = 0; i < deviceCount; i++) {
					if (i != selectedDeviceInt) {
						Pointer device = simpleble.getPeripheralHandle(adapter, i);
						simpleble.releasePeripheral(device);
					}
				}
				
				sendConnected();
				// Close window
				shell.close();
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
				// TODO Auto-generated method stub
				
			}
			
		});
		
		shell.open();
		shell.setActive();
		shell.forceFocus();
		return bleTable;
	}
	
	// Constructor, create a BluetoothCentral Manager, begin scanning
	// TODO: ? maybe make this more compliant with Java "rules"
	// I am not sure logic/actions are meant to be performed in the constructor
	public BluetoothHandler() {
		
		// Important to run this function first?
		// https://github.com/OpenBluetoothToolbox/SimpleBLE/blob/main/examples/simpleble/c/scan.c
		simpleble.getAdapterCount();
		
		if (simpleble.isBluetoothEnabled()) {
			System.out.println("Initialising Bluetooth");
			
			int count = simpleble.getAdapterCount();
			System.out.println("Adapter count: " + count);
			
			// Use first adapter by default
			adapter = simpleble.getAdapterHandle(0);
			
			// scan for 2000ms
			simpleble.scanFor(adapter, 2000);
			
			System.out.println("Creating the Table");
			Table bleTable = createTable();
			
			// Add results of scan to table
			int resultsCount = simpleble.getScanResultsCount(adapter);
			for (int i = 0; i < resultsCount; i++) {
				TableItem device = new TableItem(bleTable, SWT.NONE);
				
				Pointer deviceHandle = simpleble.getPeripheralHandle(adapter, i);
				
				String deviceID = simpleble.getPeripheralIdentifier(deviceHandle);
				String deviceAddress = simpleble.getPeripheralAddress(deviceHandle);
				
				device.setText(0, deviceID);
				device.setText(1, deviceAddress);
			}
		}
		
		else {
			System.out.println("Bluetooth not enabled");
			MessageBox bluetoothNotEnabled = new MessageBox(shell, SWT.ICON_ERROR | SWT.OK);
			bluetoothNotEnabled.setMessage("InstaShred Plugin: Bluetooth is not enabled!");
			bluetoothNotEnabled.open();
		}	
	}
}



