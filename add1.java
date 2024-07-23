import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/add1")
public class add1 extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve form data
        String taskId = request.getParameter("task_id");
        String username = request.getParameter("username");
        String employeeName = request.getParameter("employee_name");
        String project = request.getParameter("project");
        String date = request.getParameter("date_1");
        String startTime = request.getParameter("start_time");
        String endTime = request.getParameter("end_time");
        String taskCategory = request.getParameter("task_category");
        String description = request.getParameter("description");
        
        // Database connection
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;
        
        try {
            // Open a connection
            conn = DatabaseConnection.getConnection();
            
            // Check for existing task with overlapping time for the same username and date
            String checkSql = "SELECT COUNT(*) FROM add_task WHERE username = ? AND date_1 = ? " +
                              "AND ((start_time <= ? AND end_time > ?) OR (start_time < ? AND end_time >= ?) " +
                              "OR (? <= start_time AND ? >= end_time))";
            stmt = conn.prepareStatement(checkSql);
            stmt.setString(1, username);
            stmt.setString(2, date);
            stmt.setString(3, startTime);
            stmt.setString(4, startTime);
            stmt.setString(5, endTime);
            stmt.setString(6, endTime);
            stmt.setString(7, startTime);
            stmt.setString(8, endTime);
            
            rs = stmt.executeQuery();
            
            if (rs.next() && rs.getInt(1) > 0) {
                // Task with overlapping time already exists for the user on the same day
                request.setAttribute("errorMessage", "Task with overlapping time already exists for the user on the same day.");
                request.getRequestDispatcher("/add1.jsp").forward(request, response);
            } else {
                // Prepare SQL statement for insertion
                String insertSql = "INSERT INTO add_task (task_id, username, employee_name, project, date_1, start_time, end_time, task_category, description) " +
                                   "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
                stmt = conn.prepareStatement(insertSql);
                stmt.setString(1, taskId);
                stmt.setString(2, username);
                stmt.setString(3, employeeName);
                stmt.setString(4, project);
                stmt.setString(5, date);
                stmt.setString(6, startTime);
                stmt.setString(7, endTime);
                stmt.setString(8, taskCategory);
                stmt.setString(9, description);
                
                // Execute the statement
                stmt.executeUpdate();
                
                // Redirect back to the form page after successful submission
                response.sendRedirect(request.getContextPath() + "/add1.jsp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exceptions appropriately (e.g., log, redirect to error page)
            request.setAttribute("errorMessage", "Error: Failed to add task. Please try again.");
            request.getRequestDispatcher("/add1.jsp").forward(request, response);
        } finally {
            // Close resources in finally block
            try {
                if (rs != null) {
                    rs.close();
                }
                if (stmt != null) {
                    stmt.close();
                }
                if (conn != null) {
                    conn.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
