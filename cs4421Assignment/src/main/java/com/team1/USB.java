package com.team1;

import oshi.SystemInfo;
import oshi.hardware.GraphicsCard;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.NetworkIF;
import oshi.hardware.UsbDevice;

import java.util.List;

public class USB {

    // Create a SystemInfo object, which is the entry point to access hardware information
    private static final SystemInfo systemInfo = new SystemInfo();

    // Get the HardwareAbstractionLayer object, which provides access to hardware components
    private static final HardwareAbstractionLayer hardware = systemInfo.getHardware();


    public static void usbInfo(){

        // Retrieve a list of USB devices. The 'false' argument includes child devices
        List<UsbDevice> usbDevices = hardware.getUsbDevices(false);

        // Check if there are any USB devices detected.
        if (usbDevices.isEmpty()) {
            System.out.println("No USB devices found.");
            return; // Exit if no USB devices are detected.
        }

        // Print information for each detected USB device.
        for (UsbDevice device : usbDevices) {
            System.out.println("Device Name: " + device.getName());
            System.out.println("Vendor: " + device.getVendor());
            System.out.println("Product ID: " + device.getProductId());
            System.out.println("Serial Number: " + device.getSerialNumber());
            System.out.println("Unique Device ID: " + device.getUniqueDeviceId());
            System.out.println(); // Blank line for readability between devices
        }

        // Print the total number of USB devices detected
        System.out.println("Number of USB devices found: " + usbDevices.size());
    }


    public static void pciInfo(){

        // Retrieve a list of GraphicsCard objects, each representing a graphics card on the system
        List<GraphicsCard> graphicsCards = hardware.getGraphicsCards();
        System.out.println("Graphics Cards:");

        // Print information for each graphics card
        for (GraphicsCard gpu : graphicsCards) {
            System.out.println("Name: " + gpu.getName());
            System.out.println("Vendor: " + gpu.getVendor());
            System.out.println("VRAM: " + gpu.getVRam() + " bytes"); // VRAM is the memory capacity of the GPU in bytes
            System.out.println("Version Info: " + gpu.getVersionInfo()); // Additional version information for the GPU
            System.out.println(); // Blank line for readability between graphics cards
        }

        // Informational section for PCI devices. OSHI does not directly access all PCI devices,
        // but some PCI-based devices can be accessed through other hardware components,
        // such as network interfaces in this example.
        System.out.println("PCI Devices");

        // Retrieve a list of NetworkIF objects, each representing a network interface on the system
        List<NetworkIF> networkInterfaces = hardware.getNetworkIFs();
        System.out.println("Network Interfaces:");

        // Print information for each network interface
        for (NetworkIF net : networkInterfaces) {
            System.out.println("Name: " + net.getName()); // Network interface name (e.g., eth0, wlan0)
            System.out.println("MAC Address: " + net.getMacaddr()); // MAC address for identifying the interface
            System.out.println("IPv4: " + net.getIPv4addr()); // IPv4 addresses, if assigned (shown as an array of strings)
            System.out.println("IPv6: " + net.getIPv6addr()); // IPv6 addresses, if assigned (shown as an array of strings)
            System.out.println("Speed: " + net.getSpeed()/1000000 + "MB per second"); // Speed in megabits per second (Mbps)
            System.out.println(); // Blank line for readability between network interfaces
        }
    }
    public static void main(String[] args) {
        usbInfo();

        pciInfo();
    }
}