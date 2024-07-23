<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
    <meta charset="UTF-8">
    <title>Task Hours Dashboard</title>
    <!-- Include Chart.js library -->
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <h2>Task Hours Dashboard</h2>

    <!-- Daily Task Hours Pie Chart -->
    <div style="width: 50%; float: left;">
        <canvas id="dailyChart"></canvas>
    </div>

    <!-- Weekly Task Hours Pie Chart -->
    <div style="width: 50%; float: left;">
        <canvas id="weeklyChart"></canvas>
    </div>

    <!-- Monthly Task Hours Bar Chart -->
    <div style="clear: both;">
        <canvas id="monthlyChart"></canvas>
    </div>

    <%-- Print jsonData for debugging --%>
    <% out.println("<script>console.log('jsonData:', '" + request.getAttribute("jsonData") + "');</script>"); %>

    <script>
        // Parse JSON data from servlet response
        var jsonData = '<%= request.getAttribute("jsonData") %>';
        var data = JSON.parse(jsonData);

        // Print data for debugging
        console.log('data:', data);

        // Prepare data for daily pie chart
        var dailyData = [];
        var dailyLabels = [];
        data.daily.forEach(function(item) {
            dailyLabels.push(item.project);
            dailyData.push(item.daily_hours);
        });

        // Prepare data for weekly pie chart
        var weeklyData = [];
        var weeklyLabels = [];
        data.weekly.forEach(function(item) {
            weeklyLabels.push(item.project);
            weeklyData.push(item.weekly_hours);
        });

        // Prepare data for monthly bar chart
        var monthlyData = [];
        var monthlyLabels = [];
        data.monthly.forEach(function(item) {
            monthlyLabels.push(item.project);
            monthlyData.push(item.monthly_hours);
        });

        // Draw charts using Chart.js
        var dailyChartCtx = document.getElementById('dailyChart').getContext('2d');
        var dailyChart = new Chart(dailyChartCtx, {
            type: 'pie',
            data: {
                labels: dailyLabels,
                datasets: [{
                    label: 'Daily Task Hours',
                    data: dailyData,
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.7)',
                        'rgba(54, 162, 235, 0.7)',
                        'rgba(255, 206, 86, 0.7)',
                        'rgba(75, 192, 192, 0.7)',
                        'rgba(153, 102, 255, 0.7)',
                        'rgba(255, 159, 64, 0.7)'
                    ]
                }]
            },
            options: {
                responsive: true
            }
        });

        var weeklyChartCtx = document.getElementById('weeklyChart').getContext('2d');
        var weeklyChart = new Chart(weeklyChartCtx, {
            type: 'pie',
            data: {
                labels: weeklyLabels,
                datasets: [{
                    label: 'Weekly Task Hours',
                    data: weeklyData,
                    backgroundColor: [
                        'rgba(255, 99, 132, 0.7)',
                        'rgba(54, 162, 235, 0.7)',
                        'rgba(255, 206, 86, 0.7)',
                        'rgba(75, 192, 192, 0.7)',
                        'rgba(153, 102, 255, 0.7)',
                        'rgba(255, 159, 64, 0.7)'
                    ]
                }]
            },
            options: {
                responsive: true
            }
        });

        var monthlyChartCtx = document.getElementById('monthlyChart').getContext('2d');
        var monthlyChart = new Chart(monthlyChartCtx, {
            type: 'bar',
            data: {
                labels: monthlyLabels,
                datasets: [{
                    label: 'Monthly Task Hours',
                    data: monthlyData,
                    backgroundColor: 'rgba(54, 162, 235, 0.7)',
                    borderColor: 'rgba(54, 162, 235, 1)',
                    borderWidth: 1
                }]
            },
            options: {
                responsive: true,
                scales: {
                    yAxes: [{
                        ticks: {
                            beginAtZero: true
                        }
                    }]
                }
            }
        });
    </script>
</body>
</html>
