package com.team1;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import oshi.SystemInfo;
import oshi.hardware.GlobalMemory;
import oshi.hardware.PhysicalMemory;
import oshi.util.FormatUtil;

public class Memory extends Application {

    @Override
    public void start(Stage primaryStage) {
        // Create SystemInfo and GlobalMemory objects to get memory data
        SystemInfo systemInfo = new SystemInfo();
        GlobalMemory memory = systemInfo.getHardware().getMemory();

        // Calculate memory values
        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;
        long totalSwap = memory.getVirtualMemory().getSwapTotal();
        long usedSwap = memory.getVirtualMemory().getSwapUsed();
        long availableSwap = totalSwap - usedSwap;
        long pageSize = memory.getPageSize();

        // Calculate the % of available physical memory
        double availablePhysicalPercent = (double) availableMemory / totalMemory * 100;
        availablePhysicalPercent = Math.round(availablePhysicalPercent * 100.0) / 100.0;

        // Calculate the % of available swap memory
        double availableSwapPercent = (double) availableSwap / totalSwap * 100;
        availableSwapPercent = Math.round(availableSwapPercent * 100.0) / 100.0;

        // Calculate memory load percentages
        double memoryLoad = (double) usedMemory / totalMemory * 100;
        memoryLoad = Math.round(memoryLoad * 100.0) / 100.0;
        // Checks if totalSwap is > 0 if True
        double swapLoad = (totalSwap > 0) ? (double) usedSwap / totalSwap * 100 : 0;
        swapLoad = Math.round(swapLoad * 100.0) / 100.0;

        // Additional virtual memory details (paging)
        long pagesIn = memory.getVirtualMemory().getSwapPagesIn();
        long pagesOut = memory.getVirtualMemory().getSwapPagesOut();

        // Calculate the % of pages swapped in and swapped out
        double percentIn = (double) pagesIn / (pagesIn + pagesOut) * 100;
        percentIn = Math.round(percentIn * 100.0) / 100.0;
        double percentOut = (double) pagesOut / (pagesIn + pagesOut) * 100;
        percentOut = Math.round(percentOut * 100.0) / 100.0;

        //Display formatted values in the console for reference
        System.out.println("Memory Usage Report:");
        System.out.println("\nTotal Physical Memory: " + FormatUtil.formatBytes(totalMemory));
        System.out.println("Available Physical Memory: " + FormatUtil.formatBytes(availableMemory));
        System.out.println("Used Physical Memory: " + FormatUtil.formatBytes(usedMemory));
        System.out.println("% of Physical Memory Available: " + availablePhysicalPercent + "%");
        System.out.println("\nTotal Swap Memory: " + FormatUtil.formatBytes(totalSwap));
        System.out.println("Used Swap Memory: " + FormatUtil.formatBytes(usedSwap));
        System.out.println("Free Swap Memory: " + FormatUtil.formatBytes(availableSwap));
        System.out.println("% of Swap Memory Available: " + availableSwapPercent + "%");
        System.out.println("\nMemory Load: " + memoryLoad + "%");
        System.out.println("Swap Load: " + swapLoad + "%");
        System.out.println("\nPage Size: " + FormatUtil.formatBytes(pageSize));
        System.out.println("Pages Swapped In: " + pagesIn);
        System.out.println("Pages Swapped Out: " + pagesOut);
        System.out.println("% of Pages Swapped In: " + percentIn + "%");
        System.out.println("% of Pages Swapped Out: " + percentOut + "%");

        // Print details of each physical memory stick
        for (PhysicalMemory ramStick : memory.getPhysicalMemory()) {
            System.out.println("\nBank Label: " + ramStick.getBankLabel());
            System.out.println("Capacity: " + FormatUtil.formatBytes(ramStick.getCapacity()));
            System.out.println("Clock Speed: " + ramStick.getClockSpeed() / 1_000_000 + " MHz");
            System.out.println("Manufacturer: " + ramStick.getManufacturer());
            System.out.println("Memory Type: " + ramStick.getMemoryType());
            System.out.println("Form Factor: " + ramStick.getBankLabel());  // Assuming form factor info is in bank label
        }

        // Arrays to store memory data
        String[] memoryLabels = {"Physical Memory", "Swap Memory"};
        long[] totalMemoryArray = {totalMemory, totalSwap};
        long[] piechartMemoryArray = {usedMemory, usedSwap, availableMemory, availableSwap};

        // PieChart setup for memory load using arrays
        PieChart memoryPieChart = new PieChart();
        memoryPieChart.setTitle("Memory Usage");

        memoryPieChart.getData().add(new PieChart.Data("Used Physical Memory", piechartMemoryArray[0]));
        memoryPieChart.getData().add(new PieChart.Data("Available Physical Memory", piechartMemoryArray[2]));
        memoryPieChart.getData().add(new PieChart.Data("Used Swap Memory", piechartMemoryArray[1]));
        memoryPieChart.getData().add(new PieChart.Data("Available Swap Memory", piechartMemoryArray[3]));

        // BarChart for Total vs Used Memory (Physical and Swap)
        CategoryAxis xAxis = new CategoryAxis();
        xAxis.setLabel("Memory Type");
        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Bytes");

        BarChart<String, Number> memoryBarChart = new BarChart<>(xAxis, yAxis);
        memoryBarChart.setTitle("Total vs Used Memory");

        XYChart.Series<String, Number> totalSeries = new XYChart.Series<>();
        totalSeries.setName("Total");

        XYChart.Series<String, Number> usedSeries = new XYChart.Series<>();
        usedSeries.setName("Used");

        // Populate the bar chart using arrays
        for (int i = 0; i < memoryLabels.length; i++) {
            totalSeries.getData().add(new XYChart.Data<>(memoryLabels[i], totalMemoryArray[i]));
            usedSeries.getData().add(new XYChart.Data<>(memoryLabels[i], piechartMemoryArray[i]));
        }

        memoryBarChart.getData().addAll(totalSeries, usedSeries);

        // LineChart for simulated memory usage over time using arrays
        NumberAxis lineChartXAxis = new NumberAxis();
        lineChartXAxis.setLabel("Time (s)");
        NumberAxis lineChartYAxis = new NumberAxis();
        lineChartYAxis.setLabel("Bytes");

        LineChart<Number, Number> memoryLineChart = new LineChart<>(lineChartXAxis, lineChartYAxis);
        memoryLineChart.setTitle("Simulated Memory Usage Over Time");

        XYChart.Series<Number, Number> physicalMemorySeries = new XYChart.Series<>();
        physicalMemorySeries.setName("Physical Memory");

        XYChart.Series<Number, Number> swapMemorySeries = new XYChart.Series<>();
        swapMemorySeries.setName("Swap Memory");

        // Simulated data for the line chart using arrays
        int timePoints = 10;
        long[] simulatedPhysicalMemoryUsage = new long[timePoints];
        long[] simulatedSwapMemoryUsage = new long[timePoints];

        for (int i = 0; i < timePoints; i++) {
            simulatedPhysicalMemoryUsage[i] = usedMemory + (long) (Math.random() * availableMemory / 10);
            simulatedSwapMemoryUsage[i] = usedSwap + (long) (Math.random() * availableSwap / 10);
            physicalMemorySeries.getData().add(new XYChart.Data<>(i, simulatedPhysicalMemoryUsage[i]));
            swapMemorySeries.getData().add(new XYChart.Data<>(i, simulatedSwapMemoryUsage[i]));
        }

        memoryLineChart.getData().addAll(physicalMemorySeries, swapMemorySeries);

        // Combine all charts in a VBox layout and display
        VBox root = new VBox(10, memoryPieChart, memoryBarChart, memoryLineChart);
        Scene scene = new Scene(root, 800, 900);

        primaryStage.setTitle("Memory Usage Charts");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}