import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; // Import HttpSession
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

@WebServlet("/Web")
public class Web extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession(); // Get the session
        String username = (String) session.getAttribute("username"); // Retrieve username from session

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        JSONObject responseJson = new JSONObject();

        try {
            conn = DatabaseConnection.getConnection(); // Get connection from your DatabaseConnection class

            // Get today's date
            LocalDate today = LocalDate.now();

            // Calculate start and end dates for the week
            LocalDate startOfWeek = today.with(java.time.DayOfWeek.MONDAY);
            LocalDate endOfWeek = startOfWeek.plusDays(6);

            // Calculate start and end dates for the month
            LocalDate startOfMonth = today.withDayOfMonth(1);
            LocalDate endOfMonth = today.withDayOfMonth(today.lengthOfMonth());

            // Prepare SQL queries
            String dailySql = "SELECT project, SUM(TIMESTAMPDIFF(HOUR, start_time, end_time)) AS daily_hours "
                            + "FROM add_task "
                            + "WHERE username = ? AND date_1 = ? "
                            + "GROUP BY project";
            
            String weeklySql = "SELECT project, SUM(TIMESTAMPDIFF(HOUR, start_time, end_time)) AS weekly_hours "
                             + "FROM add_task "
                             + "WHERE username = ? AND date_1 BETWEEN ? AND ? "
                             + "GROUP BY project";
            
            String monthlySql = "SELECT project, SUM(TIMESTAMPDIFF(HOUR, start_time, end_time)) AS monthly_hours "
                              + "FROM add_task "
                              + "WHERE username = ? AND date_1 BETWEEN ? AND ? "
                              + "GROUP BY project";

            // Prepare statements for each query
            stmt = conn.prepareStatement(dailySql);
            stmt.setString(1, username);
            stmt.setString(2, today.format(DateTimeFormatter.ISO_DATE));
            rs = stmt.executeQuery();
            JSONArray dailyProjects = new JSONArray();
            while (rs.next()) {
                JSONObject projectObj = new JSONObject();
                projectObj.put("project", rs.getString("project"));
                projectObj.put("daily_hours", Math.abs(rs.getInt("daily_hours"))); // Convert to positive using Math.abs()
                dailyProjects.add(projectObj);
            }

            stmt = conn.prepareStatement(weeklySql);
            stmt.setString(1, username);
            stmt.setString(2, startOfWeek.format(DateTimeFormatter.ISO_DATE));
            stmt.setString(3, endOfWeek.format(DateTimeFormatter.ISO_DATE));
            rs = stmt.executeQuery();
            JSONArray weeklyProjects = new JSONArray();
            while (rs.next()) {
                JSONObject projectObj = new JSONObject();
                projectObj.put("project", rs.getString("project"));
                projectObj.put("weekly_hours", Math.abs(rs.getInt("weekly_hours"))); // Convert to positive using Math.abs()
                weeklyProjects.add(projectObj);
            }

            stmt = conn.prepareStatement(monthlySql);
            stmt.setString(1, username);
            stmt.setString(2, startOfMonth.format(DateTimeFormatter.ISO_DATE));
            stmt.setString(3, endOfMonth.format(DateTimeFormatter.ISO_DATE));
            rs = stmt.executeQuery();
            JSONArray monthlyProjects = new JSONArray();
            while (rs.next()) {
                JSONObject projectObj = new JSONObject();
                projectObj.put("project", rs.getString("project"));
                projectObj.put("monthly_hours", Math.abs(rs.getInt("monthly_hours"))); // Convert to positive using Math.abs()
                monthlyProjects.add(projectObj);
            }

            // Construct final JSON response object
            responseJson.put("daily", dailyProjects);
            responseJson.put("weekly", weeklyProjects);
            responseJson.put("monthly", monthlyProjects);

            // Forward to chart.jsp with jsonData attribute
            request.setAttribute("jsonData", responseJson.toJSONString());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/chart.jsp");
            dispatcher.forward(request, response);

        } catch (SQLException e) {
            e.printStackTrace();
            // Handle SQL exception
            responseJson.put("error", "SQL Exception: " + e.getMessage());
            response.setContentType("application/json");
            PrintWriter out = response.getWriter();
            out.print(responseJson);
            out.flush();
        } finally {
            try {
                if (rs != null) rs.close();
                if (stmt != null) stmt.close();
                if (conn != null) conn.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
