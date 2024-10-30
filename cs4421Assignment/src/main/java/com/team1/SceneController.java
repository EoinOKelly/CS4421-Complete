package com.team1;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.XYChart;
import javafx.scene.control.TextArea;
import javafx.stage.Stage;
import oshi.SystemInfo;
import oshi.hardware.*;
import oshi.software.os.NetworkParams;
import oshi.software.os.OperatingSystem;
import oshi.util.FormatUtil;
import oshi.util.Util;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

public class SceneController {
    @FXML
    private TextArea memoryTextArea;
    @FXML
    private BarChart<String, Number> memoryBarChart;
    @FXML
    private TextArea usbTextArea;
    @FXML
    private TextArea cpuTextArea;
    @FXML
    private LineChart<Number, Number> cpuLineChart;
    @FXML
    private TextArea homeTextArea;
    @FXML
    private PieChart homePieChart;
    private Stage stage;
    private Scene scene;
    private Parent root;

    public void switchToScene1(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/GUI.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToScene2(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/USB.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToScene3(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/Memory.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToScene4(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/CPU.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void switchToScene6(ActionEvent event) throws IOException {
        Parent root = FXMLLoader.load(getClass().getResource("/HOME.fxml"));
        stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
        scene = new Scene(root);
        stage.setScene(scene);
        stage.show();
    }

    public void setText(ActionEvent event) {
        // Create SystemInfo and GlobalMemory objects to get memory data
        SystemInfo systemInfo = new SystemInfo();
        GlobalMemory memory = systemInfo.getHardware().getMemory();

        // Calculate memory values
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;
        long totalSwap = memory.getVirtualMemory().getSwapTotal();
        long usedSwap = memory.getVirtualMemory().getSwapUsed();
        long freeSwap = totalSwap - usedSwap;
        long pageSize = memory.getPageSize();

        // Calculate memory load percentages
        double memoryLoad = (double) usedMemory / totalMemory * 100;
        double swapLoad = (totalSwap > 0) ? (double) usedSwap / totalSwap * 100 : 0;

        // Additional virtual memory details (paging)
        long pagesIn = memory.getVirtualMemory().getSwapPagesIn();
        long pagesOut = memory.getVirtualMemory().getSwapPagesOut();

        // Collect information about each physical memory stick
        StringBuilder memorySticksInfo = new StringBuilder();
        for (PhysicalMemory ramStick : memory.getPhysicalMemory()) {
            memorySticksInfo.append(String.format(
                    "\nBank Label: %s\nCapacity: %s\nClock Speed: %d MHz\nManufacturer: %s\nMemory Type: %s\n",
                    ramStick.getBankLabel(),
                    FormatUtil.formatBytes(ramStick.getCapacity()),
                    ramStick.getClockSpeed() / 1_000_000, // Convert Hz to MHz
                    ramStick.getManufacturer(),
                    ramStick.getMemoryType()
            ));
        }

        // Format the complete memory report for display in the TextArea
        String memoryInfo = String.format("Memory Usage Report:\n\n" +
                        "Total Physical Memory: %s\n" +
                        "Available Physical Memory: %s\n" +
                        "Used Physical Memory: %s\n" +
                        "Memory Load: %.2f%%\n\n" +
                        "Total Swap Memory: %s\n" +
                        "Used Swap Memory: %s\n" +
                        "Free Swap Memory: %s\n" +
                        "Swap Load: %.2f%%\n\n" +
                        "Page Size: %s\n" +
                        "Pages Swapped In: %d\n" +
                        "Pages Swapped Out: %d\n" +
                        "\nPhysical Memory Sticks:\n%s",
                FormatUtil.formatBytes(totalMemory),
                FormatUtil.formatBytes(availableMemory),
                FormatUtil.formatBytes(usedMemory),
                memoryLoad,
                FormatUtil.formatBytes(totalSwap),
                FormatUtil.formatBytes(usedSwap),
                FormatUtil.formatBytes(freeSwap),
                swapLoad,
                FormatUtil.formatBytes(pageSize),
                pagesIn,
                pagesOut,
                memorySticksInfo.toString());

        // Display the formatted data in memoryTextArea
        memoryTextArea.setText(memoryInfo);

        // Populate the BarChart with memory data
        XYChart.Series<String, Number> totalSeries = new XYChart.Series<>();
        totalSeries.setName("Total Memory");

        XYChart.Series<String, Number> usedSeries = new XYChart.Series<>();
        usedSeries.setName("Used Memory");

        totalSeries.getData().add(new XYChart.Data<>("Physical Memory", totalMemory));
        totalSeries.getData().add(new XYChart.Data<>("Swap Memory", totalSwap));
        usedSeries.getData().add(new XYChart.Data<>("Physical Memory", usedMemory));
        usedSeries.getData().add(new XYChart.Data<>("Swap Memory", usedSwap));

        // Clear previous data if any and add new data to the BarChart
        memoryBarChart.getData().clear();
        memoryBarChart.getData().addAll(totalSeries, usedSeries);
    }
    public void displayUSBInfo(ActionEvent event) {
        SystemInfo systemInfo = new SystemInfo();
        List<UsbDevice> usbDevices = systemInfo.getHardware().getUsbDevices(false);

        StringBuilder usbInfo = new StringBuilder();

        if (usbDevices.isEmpty()) {
            usbInfo.append("No USB devices found.");
        } else {
            for (UsbDevice device : usbDevices) {
                usbInfo.append("Device Name: ").append(device.getName()).append("\n");
                usbInfo.append("Vendor: ").append(device.getVendor()).append("\n");
                usbInfo.append("Product ID: ").append(device.getProductId()).append("\n");
                usbInfo.append("Serial Number: ").append(device.getSerialNumber()).append("\n");
                usbInfo.append("Unique Device ID: ").append(device.getUniqueDeviceId()).append("\n\n");
            }
            usbInfo.append("Number of USB devices found: ").append(usbDevices.size()).append("\n");
        }

        // Set the USB information in the usbTextArea
        usbTextArea.setText(usbInfo.toString());
    }

    // Method to display CPU information
    public void displayCPUInfo(ActionEvent event) {
        SystemInfo systemInfo = new SystemInfo();
        CentralProcessor processor = systemInfo.getHardware().getProcessor();

        // Capture initial CPU ticks for calculating load over time
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(1000); // Wait for 1 second to measure CPU load

        // Calculate CPU load between ticks
        double cpuLoad = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;

        // Get updated ticks for user, system, and idle times
        long[] ticks = processor.getSystemCpuLoadTicks();
        long totalTicks = Arrays.stream(ticks).sum();
        double userTimePercent = (double) ticks[CentralProcessor.TickType.USER.getIndex()] / totalTicks * 100;
        double systemTimePercent = (double) ticks[CentralProcessor.TickType.SYSTEM.getIndex()] / totalTicks * 100;
        double idleTimePercent = (double) ticks[CentralProcessor.TickType.IDLE.getIndex()] / totalTicks * 100;

        // Gather more detailed CPU information
        String cpuInfo = String.format(
                "CPU Usage Report:\n\n" +
                        "CPU Usage: %.2f%%\n" +
                        "User Time: %.2f%%\n" +
                        "System Time: %.2f%%\n" +
                        "Idle Time: %.2f%%\n\n" +
                        "Processor Name: %s\n" +
                        "Physical Cores: %d\n" +
                        "Logical Cores: %d\n" +
                        "Vendor: %s\n" +
                        "Model: %s\n" +
                        "Microarchitecture: %s\n",
                cpuLoad,
                userTimePercent,
                systemTimePercent,
                idleTimePercent,
                processor.getProcessorIdentifier().getName(),
                processor.getPhysicalProcessorCount(),
                processor.getLogicalProcessorCount(),
                processor.getProcessorIdentifier().getVendor(),
                processor.getProcessorIdentifier().getModel(),
                processor.getProcessorIdentifier().getMicroarchitecture()
        );

        // Display detailed CPU information in cpuTextArea
        cpuTextArea.setText(cpuInfo);

        // Populate the LineChart (cpuLineChart) with CPU usage data over time
        XYChart.Series<Number, Number> cpuUsageSeries = new XYChart.Series<>();
        cpuUsageSeries.setName("CPU Usage Over Time");

        // Simulate multiple data points to demonstrate CPU load over time (e.g., over 5 seconds)
        for (int i = 1; i <= 5; i++) {
            Util.sleep(1000); // Wait 1 second to simulate real-time data collection
            double load = processor.getSystemCpuLoadBetweenTicks(prevTicks) * 100;
            prevTicks = processor.getSystemCpuLoadTicks();
            cpuUsageSeries.getData().add(new XYChart.Data<>(i, load));
        }

        // Clear previous data if any and add the new data to the CPU LineChart
        cpuLineChart.getData().clear();
        cpuLineChart.getData().add(cpuUsageSeries);
    }

    public void displayHomeInfo(ActionEvent event) {
        SystemInfo systemInfo = new SystemInfo();
        HardwareAbstractionLayer hardware = systemInfo.getHardware();
        OperatingSystem os = systemInfo.getOperatingSystem();

        StringBuilder homeInfo = new StringBuilder();
        double batteryPercentage = 0.0; // Initialize to track battery percentage

        // Collect battery information with checks
        List<PowerSource> powerSources = hardware.getPowerSources();
        if (powerSources.isEmpty()) {
            homeInfo.append("No battery information available.\n\n");
            homePieChart.setData(null); // Clear the PieChart if no battery is available
        } else {
            for (PowerSource powerSource : powerSources) {
                homeInfo.append("Battery: ").append(powerSource.getName()).append("\n");

                double currentCapacity = powerSource.getCurrentCapacity();
                double maxCapacity = powerSource.getMaxCapacity();

                // Check if max capacity is valid before calculating percentage
                if (maxCapacity > 0 && currentCapacity > 0) {
                    batteryPercentage = (currentCapacity / maxCapacity) * 100;
                    homeInfo.append(String.format("Battery Level: %.2f%%\n\n", batteryPercentage));
                } else {
                    homeInfo.append("Battery Level: Unknown\n\n");
                    batteryPercentage = 0;
                }
            }

            // Update PieChart with battery percentage
            homePieChart.getData().clear();
            homePieChart.getData().addAll(
                    new PieChart.Data("Battery Level", batteryPercentage),
                    new PieChart.Data("Remaining", 100 - batteryPercentage)
            );
        }

        // Collect OS and network information (as before)...
        homeInfo.append("Operating System Information:\n");
        homeInfo.append("OS: ").append(os.getFamily()).append(" ")
                .append(os.getVersionInfo().getVersion()).append(" ")
                .append(os.getVersionInfo().getBuildNumber()).append("\n");
        homeInfo.append("Manufacturer: ").append(os.getManufacturer()).append("\n");
        homeInfo.append("Uptime: ").append(FormatUtil.formatElapsedSecs(os.getSystemUptime())).append("\n\n");

        NetworkParams networkParams = os.getNetworkParams();
        homeInfo.append("Network Information:\n");
        homeInfo.append("Hostname: ").append(networkParams.getHostName()).append("\n");
        homeInfo.append("Domain Name: ").append(networkParams.getDomainName()).append("\n");
        homeInfo.append("DNS Servers: ").append(String.join(", ", networkParams.getDnsServers())).append("\n");
        homeInfo.append("IPv4 Default Gateway: ").append(networkParams.getIpv4DefaultGateway()).append("\n");
        homeInfo.append("IPv6 Default Gateway: ").append(networkParams.getIpv6DefaultGateway()).append("\n");

        // Display the collected information in homeTextArea
        homeTextArea.setText(homeInfo.toString());
    }
}



