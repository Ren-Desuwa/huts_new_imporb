package views.panels;

import org.knowm.xchart.*;
import org.knowm.xchart.internal.chartpart.Chart;
import org.knowm.xchart.style.Styler;
import org.knowm.xchart.style.colors.XChartSeriesColors;
import org.knowm.xchart.style.lines.SeriesLines;
import org.knowm.xchart.style.markers.SeriesMarkers;

import javax.swing.*;
import java.awt.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.List;
import models.Bill;

/**
 * A general purpose chart panel for displaying utility consumption data.
 * Can be configured for different utility types like water, electricity, gas, etc.
 */
public class Chart_Panel extends JPanel {
    // Data
    private List<Bill> utilityBills;
    private XYChart chart;
    private JPanel chartContainer;
    
    // Chart configuration
    private final String utilityType;
    private final String consumptionLabel;
    private final Color primaryColor;
    private final Color secondaryColor;
    private final String chartTitle;
    
    /**
     * Creates a chart panel with default configuration for a specific utility type.
     * 
     * @param utilityType The type of utility (e.g., "water", "electricity")
     */
    public Chart_Panel(String utilityType) {
        this.utilityType = utilityType.toLowerCase();
        
        // Configure based on utility type
        switch (this.utilityType) {
            case "water":
                this.primaryColor = new Color(0, 102, 204); // Blue
                this.secondaryColor = new Color(200, 230, 255); // Light blue
                this.consumptionLabel = "Consumption (kL)";
                this.chartTitle = "Water Consumption History";
                break;
            case "electricity":
                this.primaryColor = new Color(255, 165, 0); // Orange
                this.secondaryColor = new Color(255, 240, 220); // Light orange
                this.consumptionLabel = "Consumption (kWh)";
                this.chartTitle = "Electricity Consumption History";
                break;
            case "gas":
                this.primaryColor = new Color(50, 150, 50); // Green
                this.secondaryColor = new Color(220, 255, 220); // Light green
                this.consumptionLabel = "Consumption (MJ)";
                this.chartTitle = "Gas Consumption History";
                break;
            default:
                this.primaryColor = new Color(80, 80, 80); // Gray
                this.secondaryColor = new Color(240, 240, 240); // Light gray
                this.consumptionLabel = "Consumption";
                this.chartTitle = utilityType + " Consumption History";
        }
        
        // Initialize panel
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Initialize chartContainer that will hold the XChartPanel
        chartContainer = new JPanel(new BorderLayout());
        add(chartContainer, BorderLayout.CENTER);
        
        // Initialize with empty data
        utilityBills = new ArrayList<>();
        createChart();
        
        // Add control buttons
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Creates a fully customized chart panel.
     * 
     * @param chartTitle The title for the chart
     * @param consumptionLabel The label for the consumption axis
     * @param primaryColor The main color for chart elements
     * @param secondaryColor The background color for the chart
     */
    public Chart_Panel(String utilityType, String chartTitle, String consumptionLabel, 
                              Color primaryColor, Color secondaryColor) {
        this.utilityType = utilityType;
        this.chartTitle = chartTitle;
        this.consumptionLabel = consumptionLabel;
        this.primaryColor = primaryColor;
        this.secondaryColor = secondaryColor;
        
        // Initialize panel
        setLayout(new BorderLayout());
        setBackground(Color.WHITE);
        setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));
        
        // Initialize chartContainer that will hold the XChartPanel
        chartContainer = new JPanel(new BorderLayout());
        add(chartContainer, BorderLayout.CENTER);
        
        // Initialize with empty data
        utilityBills = new ArrayList<>();
        createChart();
        
        // Add control buttons
        JPanel controlPanel = createControlPanel();
        add(controlPanel, BorderLayout.SOUTH);
    }
    
    /**
     * Updates the chart with new bill data.
     * 
     * @param bills List of bills to display in the chart
     */
    public void updateData(List<Bill> bills) {
        if (bills == null) {
            this.utilityBills = new ArrayList<>();
        } else {
            this.utilityBills = new ArrayList<>(bills);
        }
        updateChart();
    }
    
    /**
     * Creates the initial chart with empty data.
     */
    private void createChart() {
        // Create Chart
        chart = new XYChartBuilder()
                .width(600)
                .height(300)
                .title(chartTitle)
                .xAxisTitle("Date")
                .yAxisTitle(consumptionLabel)
                .build();
        
        // Customize Chart
        customizeChart(chart);
        
        // Add empty data initially
        List<Date> xData = new ArrayList<>();
        List<Double> yData = new ArrayList<>();
        
        XYSeries series = chart.addSeries("Consumption", xData, yData);
        series.setLineColor(primaryColor);
        series.setMarkerColor(primaryColor);
        series.setMarker(SeriesMarkers.CIRCLE);
        series.setLineWidth(2.5f);
        
        // Add the chart to the container panel
        refreshChartDisplay();
    }
    
    /**
     * Apply standard styling to any chart
     */
    private void customizeChart(Chart chart) {
        chart.getStyler().setLegendPosition(Styler.LegendPosition.InsideNE);
        chart.getStyler().setChartBackgroundColor(Color.WHITE);
        chart.getStyler().setPlotBackgroundColor(secondaryColor);
        chart.getStyler().setPlotGridLinesColor(new Color(180, 180, 180));
        chart.getStyler().setChartFontColor(Color.DARK_GRAY);
        chart.getStyler().setAxisTickLabelsColor(Color.DARK_GRAY);
        
        if (chart instanceof XYChart) {
            ((XYChart) chart).getStyler().setDatePattern("yyyy-MM");
            ((XYChart) chart).getStyler().setXAxisLabelRotation(45);
        }
        
        chart.getStyler().setDecimalPattern("#0.0");
        chart.getStyler().setToolTipsEnabled(true);
        chart.getStyler().setLegendVisible(false);
    }
    
    /**
     * Creates the control panel with buttons for different chart types.
     */
    private JPanel createControlPanel() {
        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        controlPanel.setBackground(Color.WHITE);
        
        JButton lineChartBtn = new JButton("Line Chart");
        JButton barChartBtn = new JButton("Monthly View");
        JButton comparisonBtn = new JButton("Year Comparison");
        JButton costChartBtn = new JButton("Cost View");
        
        lineChartBtn.addActionListener(e -> showTrendLineChart());
        barChartBtn.addActionListener(e -> showMonthlyBarChart());
        costChartBtn.addActionListener(e -> showCostTrendChart());
        
        // For year comparison, get current and previous year
        int currentYear = LocalDate.now().getYear();
        comparisonBtn.addActionListener(e -> showYearComparisonChart(currentYear, currentYear - 1));
        
        controlPanel.add(lineChartBtn);
        controlPanel.add(barChartBtn);
        controlPanel.add(comparisonBtn);
        controlPanel.add(costChartBtn);
        
        return controlPanel;
    }
    
    /**
     * Updates the chart with current bill data.
     */
    private void updateChart() {
        if (utilityBills == null || utilityBills.isEmpty()) {
            // Reset to empty chart
            chart.getSeriesMap().clear();
            chart.addSeries("Consumption", new ArrayList<>(), new ArrayList<>());
            refreshChartDisplay();
            return;
        }
        
        // Sort bills by date (oldest first)
        List<Bill> sortedBills = new ArrayList<>(utilityBills);
        sortedBills.sort(Comparator.comparing(Bill::getIssueDate));
        
        // Extract data for chart
        List<Date> dates = new ArrayList<>();
        List<Double> consumptions = new ArrayList<>();
        
        for (Bill bill : sortedBills) {
            // Convert LocalDate to Date for XChart compatibility
            LocalDate localDate = bill.getIssueDate();
            Date date = java.sql.Date.valueOf(localDate);
            dates.add(date);
            
            // Add consumption value
            consumptions.add(bill.getConsumption());
        }
        
        // Update chart with new data
        chart.getSeriesMap().clear();
        XYSeries series = chart.addSeries("Consumption", dates, consumptions);
        series.setLineColor(primaryColor);
        series.setMarkerColor(primaryColor);
        series.setMarker(SeriesMarkers.CIRCLE);
        series.setLineWidth(2.5f);
        
        // Add optional monthly average line if we have enough data
        if (consumptions.size() > 2) {
            double avg = consumptions.stream().mapToDouble(d -> d).average().orElse(0.0);
            List<Double> avgLine = new ArrayList<>(Collections.nCopies(dates.size(), avg));
            XYSeries avgSeries = chart.addSeries("Average", dates, avgLine);
            avgSeries.setLineColor(new Color(220, 80, 80));
            avgSeries.setMarker(SeriesMarkers.NONE);
            avgSeries.setLineStyle(SeriesLines.DASH_DASH);
            avgSeries.setLineWidth(1.5f);
        }
        
        // Refresh the chart panel
        refreshChartDisplay();
    }

    /**
     * Helper method to refresh the chart display
     */
    private void refreshChartDisplay() {
        chartContainer.removeAll();
        chartContainer.add(new XChartPanel<>(chart), BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }

    /**
     * Displays a bar chart showing monthly consumption.
     */
    public void showMonthlyBarChart() {
        if (utilityBills == null || utilityBills.isEmpty()) {
            return;
        }
        
        // Group bills by month and calculate total consumption
        Map<String, Double> monthlyConsumption = new HashMap<>();
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("yyyy-MM");
        
        for (Bill bill : utilityBills) {
            String month = bill.getIssueDate().format(monthFormatter);
            monthlyConsumption.put(month, 
                monthlyConsumption.getOrDefault(month, 0.0) + bill.getConsumption());
        }
        
        // Sort months chronologically
        List<String> months = new ArrayList<>(monthlyConsumption.keySet());
        Collections.sort(months);
        
        // Create data lists
        List<Double> consumptions = new ArrayList<>();
        for (String month : months) {
            consumptions.add(monthlyConsumption.get(month));
        }
        
        // Create and display the bar chart
        CategoryChart barChart = new CategoryChartBuilder()
                .width(600)
                .height(300)
                .title("Monthly " + capitalizeFirstLetter(utilityType) + " Consumption")
                .xAxisTitle("Month")
                .yAxisTitle(consumptionLabel)
                .build();
        
        // Customize chart
        customizeChart(barChart);
        barChart.getStyler().setHasAnnotations(true);
        barChart.getStyler().setXAxisLabelRotation(45);
        barChart.getStyler().setSeriesColors(new Color[]{primaryColor});
        
        barChart.addSeries("Consumption", months, consumptions);
        
        // Replace the current chart with the bar chart
        chartContainer.removeAll();
        chartContainer.add(new XChartPanel<>(barChart), BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }
    
    /**
     * Returns to the default line chart showing consumption trend over time.
     */
    public void showTrendLineChart() {
        updateChart(); // Revert to default line chart
    }
    
    /**
     * Displays a chart comparing consumption between two years.
     * 
     * @param currentYear The current year
     * @param previousYear The previous year to compare with
     */
    public void showYearComparisonChart(int currentYear, int previousYear) {
        if (utilityBills == null || utilityBills.isEmpty()) {
            return;
        }
        
        // Group bills by month and year
        Map<String, Double> currentYearData = new HashMap<>();
        Map<String, Double> previousYearData = new HashMap<>();
        
        for (Bill bill : utilityBills) {
            int year = bill.getIssueDate().getYear();
            int month = bill.getIssueDate().getMonthValue();
            
            if (year == currentYear) {
                String key = String.format("%02d", month);
                currentYearData.put(key, 
                    currentYearData.getOrDefault(key, 0.0) + bill.getConsumption());
            } else if (year == previousYear) {
                String key = String.format("%02d", month);
                previousYearData.put(key, 
                    previousYearData.getOrDefault(key, 0.0) + bill.getConsumption());
            }
        }
        
        // Prepare data for chart
        List<String> months = Arrays.asList("01", "02", "03", "04", "05", "06", 
                                           "07", "08", "09", "10", "11", "12");
        List<Double> currentYearValues = new ArrayList<>();
        List<Double> previousYearValues = new ArrayList<>();
        
        for (String month : months) {
            currentYearValues.add(currentYearData.getOrDefault(month, 0.0));
            previousYearValues.add(previousYearData.getOrDefault(month, 0.0));
        }
        
        // Create the comparison chart
        CategoryChart comparisonChart = new CategoryChartBuilder()
                .width(600)
                .height(300)
                .title(capitalizeFirstLetter(utilityType) + " Yearly Comparison")
                .xAxisTitle("Month")
                .yAxisTitle(consumptionLabel)
                .build();
        
        // Customize chart
        customizeChart(comparisonChart);
        comparisonChart.getStyler().setLegendPosition(Styler.LegendPosition.OutsideE);
        comparisonChart.getStyler().setLegendVisible(true);
        
        // Add month names instead of numbers
        String[] monthNames = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", 
                              "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
        
        comparisonChart.addSeries(currentYear + "", monthNames, currentYearValues)
                      .setFillColor(primaryColor);
        comparisonChart.addSeries(previousYear + "", monthNames, previousYearValues)
                      .setFillColor(primaryColor.brighter());
        
        // Replace the current chart with the comparison chart
        chartContainer.removeAll();
        chartContainer.add(new XChartPanel<>(comparisonChart), BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }
    
    /**
     * Helper method to display cost trends over time.
     */
    public void showCostTrendChart() {
        if (utilityBills == null || utilityBills.isEmpty()) {
            return;
        }
        
        // Sort bills by date (oldest first)
        List<Bill> sortedBills = new ArrayList<>(utilityBills);
        sortedBills.sort(Comparator.comparing(Bill::getIssueDate));
        
        // Extract data for chart
        List<Date> dates = new ArrayList<>();
        List<Double> costs = new ArrayList<>();
        
        for (Bill bill : sortedBills) {
            // Convert LocalDate to Date for XChart compatibility
            LocalDate localDate = bill.getIssueDate();
            Date date = java.sql.Date.valueOf(localDate);
            dates.add(date);
            
            // Add cost value
            costs.add(bill.getAmount());
        }
        
        // Create cost chart
        XYChart costChart = new XYChartBuilder()
                .width(600)
                .height(300)
                .title(capitalizeFirstLetter(utilityType) + " Cost History")
                .xAxisTitle("Date")
                .yAxisTitle("Cost ($)")
                .build();
        
        // Customize chart
        customizeChart(costChart);
        costChart.getStyler().setDecimalPattern("$#0.00");
        
        XYSeries series = costChart.addSeries("Cost", dates, costs);
        series.setLineColor(new Color(128, 0, 128)); // Purple for cost
        series.setMarkerColor(new Color(128, 0, 128));
        series.setMarker(SeriesMarkers.DIAMOND);
        series.setLineWidth(2.5f);
        
        // Replace the current chart with the cost chart
        chartContainer.removeAll();
        chartContainer.add(new XChartPanel<>(costChart), BorderLayout.CENTER);
        chartContainer.revalidate();
        chartContainer.repaint();
    }
    
    /**
     * Helper method to capitalize the first letter of a string.
     */
    private String capitalizeFirstLetter(String text) {
        if (text == null || text.isEmpty()) {
            return text;
        }
        return text.substring(0, 1).toUpperCase() + text.substring(1);
    }
    
    /**
     * Save the chart as an image file.
     */
    public void saveChartAsImage(String filePath) {
        // Implementation for saving chart as image
        // This would use XChart's BitmapEncoder to save the chart
    }
    
    /**
     * Helper method to get a color for the specified utility type.
     */
    public static Color getUtilityColor(String utilityType) {
        switch (utilityType.toLowerCase()) {
            case "water":
                return new Color(0, 102, 204); // Blue
            case "electricity":
                return new Color(255, 165, 0); // Orange
            case "gas":
                return new Color(50, 150, 50); // Green
            case "internet":
                return new Color(100, 50, 200); // Purple
            case "phone":
                return new Color(200, 50, 100); // Pink
            default:
                return new Color(80, 80, 80); // Gray
        }
    }
    
    /**
     * Helper method to get a background color for the specified utility type.
     */
    public static Color getUtilityBackgroundColor(String utilityType) {
        switch (utilityType.toLowerCase()) {
            case "water":
                return new Color(200, 230, 255); // Light blue
            case "electricity":
                return new Color(255, 240, 220); // Light orange
            case "gas":
                return new Color(220, 255, 220); // Light green
            case "internet":
                return new Color(230, 220, 255); // Light purple
            case "phone":
                return new Color(255, 220, 230); // Light pink
            default:
                return new Color(240, 240, 240); // Light gray
        }
    }
}