package com.team1;

import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.PowerSource;
import oshi.software.os.OperatingSystem;
import oshi.software.os.NetworkParams;
import oshi.util.Util;

import java.util.List;

public class Home {
    // Initialize OSHI's SystemInfo object for system-related data
    private static final SystemInfo systemInfo = new SystemInfo();

    // Get the hardware abstraction layer for accessing hardware data
    private static final HardwareAbstractionLayer hardware = systemInfo.getHardware();

    // Get the operating system layer for accessing OS-specific data
    private static final OperatingSystem os = systemInfo.getOperatingSystem();

    /**
     * Prints battery information, including battery name and current charge level.
     * Uses OSHI's PowerSource class to access battery details.
     */
    public static void battery() {
        // Retrieve a list of all power sources (i.e., batteries) available on the system
        List<PowerSource> powerSources = hardware.getPowerSources();

        for (PowerSource powerSource : powerSources) {
            // Print battery name
            System.out.printf("Battery: %s\n", powerSource.getName());

            // Loop through each power source to get specific battery information
            for (PowerSource battery : hardware.getPowerSources()) {
                // Name of the current battery
                String batteryName = battery.getName();

                // Get the current capacity (remaining charge) of the battery
                double batteryPercentage = battery.getCurrentCapacity();

                // Get the maximum capacity the battery can hold
                double maxBattery = battery.getMaxCapacity();

                // Calculate and print the battery level as a percentage
                System.out.printf("Battery Level: %.3f%%\n", (batteryPercentage / maxBattery) * 100);
            }
        }
    }

    /**
     * Prints basic operating system information, including OS family, version, build number,
     * manufacturer, and system uptime (in seconds).
     */
    public static void operatingSystem() {
        // Print the OS family, version, and build number
        System.out.printf("Operating System: %s %s %s\n",
                os.getFamily(),             // OS Family (e.g., Windows, macOS)
                os.getVersionInfo().getVersion(),  // OS Version
                os.getVersionInfo().getBuildNumber());  // OS Build number

        // Print the OS manufacturer
        System.out.printf("OS Manufacturer: %s\n", os.getManufacturer());

        // Print the system uptime in seconds (time since last boot)
        System.out.printf("OS Uptime: %d seconds\n", os.getSystemUptime());
    }

    /**
     * Prints network configuration details, including hostname, domain name, DNS servers, and
     * default gateways for both IPv4 and IPv6.
     */
    public static void networkInfo() {
        // Retrieve network parameters, such as hostname and domain information
        NetworkParams networkParams = os.getNetworkParams();

        // Print the hostname
        System.out.printf("Host Name: %s\n", networkParams.getHostName());

        // Print the domain name
        System.out.printf("Domain Name: %s\n", networkParams.getDomainName());

        // Print the list of DNS servers (as a comma-separated string)
        System.out.printf("DNS Servers: %s\n", String.join(", ", networkParams.getDnsServers()));

        // Print the default IPv4 gateway
        System.out.printf("IPv4 Default Gateway: %s\n", networkParams.getIpv4DefaultGateway());

        // Print the default IPv6 gateway
        System.out.printf("IPv6 Default Gateway: %s\n", networkParams.getIpv6DefaultGateway());
    }

    /**
     * Main method for running the program. Calls each method to display battery, OS,
     * and network information with separation lines for better readability.
     */
    public static void main(String[] args) {
        // Display battery information
        battery();
        System.out.print("\n--------------------------------\n\n");

        // Display operating system information
        operatingSystem();
        System.out.print("\n--------------------------------\n\n");

        // Display network information
        networkInfo();
    }
}