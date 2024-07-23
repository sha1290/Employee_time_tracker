<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
  <meta charset="UTF-8">
  <meta name="viewport" content="width=device-width, initial-scale=1.0">
  <title>Add Task Form</title>
  <!-- Bootstrap CSS -->
  <link rel="stylesheet" href="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/css/bootstrap.min.css">
  <!-- Bootstrap Icons -->
  <link href="https://cdn.jsdelivr.net/npm/bootstrap-icons/font/bootstrap-icons.css" rel="stylesheet">
  <style>
    body {
      font-family: Arial, sans-serif;
      background-color: #090979;
      background-image: url('https://i.pinimg.com/originals/ba/ef/b9/baefb9519114e0b3b4fa6152ec9df0dc.jpg'); /* Replace with your image path */
      background-size: cover; 
      background-position: center; 
      background-repeat: no-repeat;
    }
    .container {
      max-width: 600px;
      margin: 0 auto;
      margin-top: 50px;
    }
    .form-group label {
      font-weight: bold;
    }
    .form-group input[type="text"], 
    .form-group input[type="date"], 
    .form-group input[type="time"], 
    .form-group textarea {
      border-radius: 20px !important;
    }
    .card {
      box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
      background-color: white;
      padding: 20px;
    }
    #footer {
      position: fixed;
      left: 0;
      bottom: 0;
      width: 100%;
      background-color: #090979;
      color: white;
      text-align: center;
      font-weight: bold;
    }
  </style>
</head>
<body>

<div class="container">
  <div class="card p-4">
    <h2 class="text-center">ADD TASK</h2>
    <div class="btn-container">
      <a href="home.jsp" class="btn btn-primary home-btn">Home</a>
    </div>
    <form id="taskForm" action='add' method='post'>
      <div class="form-group">
        <label for="task_id"><i class="bi bi-key"></i> Task ID</label>
        <input type="text" class="form-control" id="task_id" name="task_id" placeholder="Automatically generated" readonly>
      </div>
      <div class="form-group">
        <label for="username"><i class="bi bi-key"></i> Username</label>
        <input type="text" class="form-control" id="username" name="username" placeholder="Enter Username" value="<%= request.getSession().getAttribute("username") %>" readonly>
      </div>
      <div class="form-group">
        <label for="employee_name"><i class="bi bi-person"></i> Employee Name</label>
        <input type="text" class="form-control" id="employee_name" name="employee_name" placeholder="Enter Employee Name">
      </div>
      <div class="form-group">
        <label for="project"><i class="bi bi-journal"></i> Project</label>
        <input type="text" class="form-control" id="project" name="project" placeholder="Enter Project Name">
      </div>
      <div class="form-group">
        <label for="date_1"><i class="bi bi-calendar"></i> Date</label>
        <input type="date" class="form-control" id="date_1" name="date_1" placeholder="Select Date">
      </div>
      <div class="form-group">
        <label for="start_time"><i class="bi bi-clock"></i> Start Time</label>
        <input type="time" class="form-control" id="start_time" name="start_time" placeholder="Select Start Time">
      </div>
      <div class="form-group">
        <label for="end_time"><i class="bi bi-clock"></i> End Time</label>
        <input type="time" class="form-control" id="end_time" name="end_time" placeholder="Select End Time">
      </div>
      <div class="form-group">
        <label for="task_category"><i class="bi bi-card-checklist"></i> Task Category</label>
        <input type="text" class="form-control" id="task_category" name="task_category" placeholder="Enter Task Category">
      </div>
      <div class="form-group">
        <label for="description"><i class="bi bi-textarea"></i> Description</label>
        <textarea class="form-control" id="description" name="description" rows="3" placeholder="Enter Task Description"></textarea>
      </div>
      <button type="submit" class="btn btn-primary">Submit</button>
    </form>
  </div>
</div>

<footer id="footer">
  © 2024 Employee Operations Dashboard.
</footer>

<!-- Bootstrap JS and dependencies -->
<script src="https://code.jquery.com/jquery-3.5.1.slim.min.js"></script>
<script src="https://cdn.jsdelivr.net/npm/@popperjs/core@2.5.4/dist/umd/popper.min.js"></script>
<script src="https://stackpath.bootstrapcdn.com/bootstrap/4.5.2/js/bootstrap.min.js"></script>

<script>
  
  function generateTaskId() {
    return Math.floor(Math.random() * 10000); 
  }

  
  function isTaskIdUnique(taskId) {
    
    return true;
  }

  
  document.addEventListener('DOMContentLoaded', function() {
    let taskId = generateTaskId();
    while (!isTaskIdUnique(taskId)) {
      taskId = generateTaskId();
    }
    document.getElementById('task_id').value = taskId;

    
    const errorMessage = '<%= (String) request.getAttribute("errorMessage") %>';
    if (errorMessage && errorMessage.trim() !== 'null') {
      alert(errorMessage.trim());
    }
  });

 
  document.getElementById('taskForm').addEventListener('submit', function(event) {
    event.preventDefault(); 

    let dateInput = document.getElementById('date_1').value;
    let currentDate = new Date().toISOString().split('T')[0]; 

    if (dateInput < currentDate) {
      alert('Cannot add task for a date earlier than today.');
      return; 
    }

    
    this.submit();
  });

</script>


</body>
</html>
