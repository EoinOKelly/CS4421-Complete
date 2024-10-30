package com.team1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.util.Util;

import java.util.Arrays;

public class CPU extends Application {

    // Create a SystemInfo instance, providing access to hardware and system information
    private static final SystemInfo systemInfo = new SystemInfo();

    // Get the CentralProcessor instance to retrieve CPU information
    private static final CentralProcessor processor = systemInfo.getHardware().getProcessor();

    // Arrays to store CPU load, interrupts, and thread switches data
    private static final int DATA_POINTS = 5;
    private static double[] cpuLoadArray = new double[DATA_POINTS];
    private static long[] interruptsArray = new long[DATA_POINTS];
    private static long[] threadSwitchesArray = new long[DATA_POINTS];

    // Method to display general CPU information
    public static void generalCpuInfo() {
        System.out.println("General CPU Info:\n");
        // Print various identifying details about the processor
        System.out.println("Name: " + processor.getProcessorIdentifier().getName());
        System.out.println("Family: " + processor.getProcessorIdentifier().getFamily());
        System.out.println("Model: " + processor.getProcessorIdentifier().getModel());
        System.out.println("Stepping: " + processor.getProcessorIdentifier().getStepping());
        System.out.println("Vendor: " + processor.getProcessorIdentifier().getVendor());
        System.out.println("Processor ID: " + processor.getProcessorIdentifier().getProcessorID());
    }

    // Method to display CPU usage information over time
    public static void cpuLoad() {
        // Retrieve the initial CPU ticks for load calculation
        long[] ticks = processor.getSystemCpuLoadTicks();
        Util.sleep(1000);  // 1-second interval to allow for load measurement
        double cpuUsage = processor.getSystemCpuLoadBetweenTicks(ticks) * 100;
        double totalCpuLoad = 0;

        // Print the current CPU usage
        System.out.printf("CPU Usage: %.2f%%\n", cpuUsage);

        // CPU load trend over a 5-second period
        System.out.println("\nCPU Load Trend (over 5 seconds):");
        for (int i = 0; i < DATA_POINTS; i++) {
            Util.sleep(1000);  // Wait for 1 second before re-evaluating load
            long[] newTicks = processor.getSystemCpuLoadTicks();
            double load = processor.getSystemCpuLoadBetweenTicks(ticks) * 100;
            ticks = newTicks;  // Update ticks for the next interval
            cpuLoadArray[i] = load;  // Store CPU load in array
            totalCpuLoad += load;
            System.out.printf("Second %d: CPU Load = %.2f%%\n", i + 1, load);
        }
        System.out.printf("Average CPU load over 5 seconds: %.2f%%\n", totalCpuLoad / DATA_POINTS);

        // Calculate and print detailed usage percentages (User, System, Idle)
        long[] ticks1 = processor.getSystemCpuLoadTicks();
        long total = Arrays.stream(ticks1).sum();
        double userTimePercent = (double) ticks1[CentralProcessor.TickType.USER.getIndex()] / total * 100;
        double systemTimePercent = (double) ticks1[CentralProcessor.TickType.SYSTEM.getIndex()] / total * 100;
        double idleTimePercent = (double) ticks1[CentralProcessor.TickType.IDLE.getIndex()] / total * 100;
        System.out.printf("User Time: %.2f%%, System Time: %.2f%%, Idle Time: %.2f%%\n", userTimePercent, systemTimePercent, idleTimePercent);
    }

    // Method to monitor the number of CPU interrupts per second over a period
    public static void interrupts() {
        long averageInterrupts = 0;
        System.out.println("Number of Interrupts Per Second (over 5 seconds):");
        for (int j = 0; j < DATA_POINTS; j++) {
            long initialInterrupts = processor.getInterrupts();
            Util.sleep(1000);
            long newInterrupts = processor.getInterrupts();
            long currentInterrupts = (newInterrupts - initialInterrupts);
            interruptsArray[j] = currentInterrupts;  // Store interrupts in array
            System.out.printf("Second %d Interrupts: %d\n", j + 1, currentInterrupts);
            averageInterrupts += currentInterrupts;
        }
        System.out.printf("Average Interrupts Per Second: %d\n", averageInterrupts / DATA_POINTS);

        // Calculate average interrupts per second since system start (uptime)
        long uptime = systemInfo.getOperatingSystem().getSystemUptime();
        System.out.printf("Average Interrupts Since Start of Uptime: %d\n", (processor.getInterrupts() / uptime));
    }

    // Method to measure and display the rate of thread context switches per second
    public static void threadSwitches() {
        long averageThreadSwitches = 0;
        System.out.println("Number of Thread Switches Per Second (over 5 seconds): ");
        for (int j = 0; j < DATA_POINTS; j++) {
            long initialThreadSwitches = processor.getContextSwitches();
            Util.sleep(1000);
            long newThreadSwitches = processor.getContextSwitches();
            long currentThreadSwitches = (newThreadSwitches - initialThreadSwitches);
            threadSwitchesArray[j] = currentThreadSwitches;  // Store thread switches in array
            System.out.printf("Second %d Thread Switches: %d\n", j + 1, currentThreadSwitches);
            averageThreadSwitches += currentThreadSwitches;
        }
        System.out.printf("Average Thread Switches Per Second: %d\n", averageThreadSwitches / DATA_POINTS);

        // Calculate the average rate of context switches since system start
        long uptime = systemInfo.getOperatingSystem().getSystemUptime();
        System.out.printf("Average Thread Switches Since Start of Uptime: %d\n", (processor.getContextSwitches() / uptime));
    }

    // Method to display CPU frequency details
    public static void frequency() {
        long[] currentFrequencies = processor.getCurrentFreq();
        if (currentFrequencies[0] == 0) {
            System.out.println("Cannot access real-time CPU frequency");
            System.out.println("CPU Clock Frequency According to Vendor : " + processor.getProcessorIdentifier().getVendorFreq() / 1_000_000 + " MHz");
        } else {
            System.out.println("Max CPU Frequency: " + processor.getMaxFreq() / 1_000_000 + " MHz");
            for (int i = 0; i < currentFrequencies.length; i++) {
                System.out.println("Core " + i + " Frequency: " + currentFrequencies[i] / 1_000_000 + " MHz\n");
            }
        }
    }

    // Method to print information about the number of CPU cores
    public static void cores() {
        System.out.println("Physical CPU Cores: " + processor.getPhysicalProcessorCount());
        System.out.println("Logical CPU Cores (Threads): " + processor.getLogicalProcessorCount());
    }

    // Method to display CPU microarchitecture information
    public static void microarchitecture() {
        System.out.printf("CPU Microarchitecture: %s\n", processor.getProcessorIdentifier().getMicroarchitecture());
    }

    // Method to display cache details for the CPU
    public static void cache() {
        for (CentralProcessor.ProcessorCache cache : processor.getProcessorCaches()) {
            short lineSize = cache.getLineSize(); // Cache line size in bytes
            System.out.printf("Cache Level: %d\n", cache.getLevel());
            System.out.printf("Cache Type: %s\n", cache.getType());
            System.out.printf("Cache Line Size: %d bytes\n", lineSize);
            System.out.printf("Associativity: %d\n", cache.getAssociativity());
            System.out.println("-------------------");
        }
    }

    // JavaFX line graphs method
    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("CPU Monitoring Charts");

        // Create line charts for each data array
        LineChart<Number, Number> cpuLoadChart = createLineChart("CPU Load", cpuLoadArray);
        LineChart<Number, Number> interruptsChart = createLineChart("Interrupts per Second", interruptsArray);
        LineChart<Number, Number> threadSwitchesChart = createLineChart("Thread Switches per Second", threadSwitchesArray);

        // Add all charts to a VBox layout
        VBox root = new VBox(cpuLoadChart, interruptsChart, threadSwitchesChart);
        Scene scene = new Scene(root, 800, 600);

        primaryStage.setScene(scene);
        primaryStage.show();
    }

    // Create line chart for double array data
    private LineChart<Number, Number> createLineChart(String title, double[] data) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time (seconds)");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(title);

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(title);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i < data.length; i++) {
            series.getData().add(new XYChart.Data<>(i + 1, data[i]));
        }
        lineChart.getData().add(series);
        return lineChart;
    }

    // Create line chart for long array data
    private LineChart<Number, Number> createLineChart(String title, long[] data) {
        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Time (seconds)");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel(title);

        LineChart<Number, Number> lineChart = new LineChart<>(xAxis, yAxis);
        lineChart.setTitle(title);

        XYChart.Series<Number, Number> series = new XYChart.Series<>();
        for (int i = 0; i < data.length; i++) {
            series.getData().add(new XYChart.Data<>(i + 1, data[i]));
        }
        lineChart.getData().add(series);
        return lineChart;
    }

    // Main method to call each specific information-gathering method
    public static void main(String[] args) {
        // Display general CPU information
        generalCpuInfo();
        System.out.print("\n--------------------------------\n\n");

        // Display number of cores information
        cores();
        System.out.print("\n--------------------------------\n\n");

        // Display CPU load information
        cpuLoad();
        System.out.print("\n--------------------------------\n\n");

        // Display frequency information of each CPU core
        frequency();
        System.out.print("\n--------------------------------\n\n");

        // Display interrupts information
        interrupts();
        System.out.print("\n--------------------------------\n\n");

        // Display thread switch rates
        threadSwitches();
        System.out.print("\n--------------------------------\n\n");

        // Display CPU microarchitecture details
        microarchitecture();
        System.out.print("\n--------------------------------\n\n");

        // Display cache details of the CPU
        cache();

        // Launch the JavaFX application
        launch(args);
    }
}
