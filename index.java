import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/index")
public class index extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Set request character encoding to handle different character sets
        request.setCharacterEncoding("UTF-8");

        // Retrieve form data
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet resultSet = null;

        try {
            // Get a connection from the DatabaseConnection utility class
            conn = DatabaseConnection.getConnection();

            // Check if username or email already exists
            if (isUserExists(conn, username, email)) {
                // Username or email already exists
                displayErrorPage(response, "Username or email already exists.", "index.jsp");
                return;
            }

            // Proceed with insertion if username and email are unique
            String insertQuery = "INSERT INTO employee_login (username, email, password) VALUES (?, ?, ?)";
            stmt = conn.prepareStatement(insertQuery);
            stmt.setString(1, username);
            stmt.setString(2, email);
            stmt.setString(3, password);

            // Execute the query
            int rowsInserted = stmt.executeUpdate();
            if (rowsInserted > 0) {
                // Registration successful, redirect to index.jsp
                response.sendRedirect("index.jsp");
            } else {
                displayErrorPage(response, "Failed to register employee.", "index.jsp");
            }
        } catch (SQLException e) {
            e.printStackTrace();
            displayErrorPage(response, "Database access error: " + e.getMessage(), "index.jsp");
        } finally {
            // Close resources
            try {
                if (resultSet != null) {
                    resultSet.close();
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

    private boolean isUserExists(Connection conn, String username, String email) throws SQLException {
        PreparedStatement stmt = null;
        ResultSet resultSet = null;

        try {
            String checkQuery = "SELECT COUNT(*) AS count FROM employee_login WHERE username = ? OR email = ?";
            stmt = conn.prepareStatement(checkQuery);
            stmt.setString(1, username);
            stmt.setString(2, email);
            resultSet = stmt.executeQuery();

            if (resultSet.next()) {
                int count = resultSet.getInt("count");
                return count > 0;
            }
        } finally {
            // Close result set and statement in a separate finally block
            if (resultSet != null) {
                resultSet.close();
            }
            if (stmt != null) {
                stmt.close();
            }
        }
        return false;
    }

    private void displayErrorPage(HttpServletResponse response, String errorMessage, String redirectUrl) throws IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html>");
        out.println("<html>");
        out.println("<head>");
        out.println("<meta charset=\"UTF-8\">");
        out.println("<title>Error</title>");
        out.println("<style>");
        out.println("  .alert {");
        out.println("    padding: 20px;");
        out.println("    background-color: #f44336;"); // Red background
        out.println("    color: white;"); // White text
        out.println("    border-radius: 10px;");
        out.println("    position: fixed;");
        out.println("    top: 50%;");
        out.println("    left: 50%;");
        out.println("    transform: translate(-50%, -50%);");
        out.println("    box-shadow: 0 4px 8px rgba(0,0,0,0.1);");
        out.println("    z-index: 1000;"); // Ensure it's on top
        out.println("  }");
        out.println("  .closebtn {");
        out.println("    position: absolute;");
        out.println("    right: 20px;");
        out.println("    top: 20px;");
        out.println("    color: white;");
        out.println("    font-size: 20px;");
        out.println("    font-weight: bold;");
        out.println("    cursor: pointer;");
        out.println("  }");
        out.println("</style>");
        out.println("</head>");
        out.println("<body>");
        out.println("<div class=\"alert\">");
        out.println("<span class=\"closebtn\" onclick=\"returnToIndex()\">×</span>"); // Close button with JavaScript function
        out.println("<p>" + errorMessage + "</p>"); // Error message
        out.println("</div>");
        out.println("<script>");
        out.println("function returnToIndex() {");
        out.println("  window.location.href = '" + redirectUrl + "';"); // Redirect to index.jsp
        out.println("}");
        out.println("</script>");
        out.println("</body>");
        out.println("</html>");
    }
}
