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
import javax.servlet.http.HttpSession;

@WebServlet("/signin")
public class signin extends HttpServlet {
    private static final long serialVersionUID = 1L;

    public signin() {
        super();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Retrieve username and password from the request
        String username = request.getParameter("username");
        String password = request.getParameter("password");

        Connection con = null;
        PreparedStatement pstmt = null;
        ResultSet rs = null;

        try {
            // Get the database connection from the utility class
            con = DatabaseConnection.getConnection();

            // SQL query to authenticate the user
            String sql = "SELECT * FROM employee_login WHERE username = ? AND password = ?";
            pstmt = con.prepareStatement(sql);
            pstmt.setString(1, username);
            pstmt.setString(2, password);

            rs = pstmt.executeQuery();

            if (rs.next()) {
                // User authenticated, create session
                HttpSession session = request.getSession();
                session.setAttribute("username", username);

                // Redirect to the home page
                response.sendRedirect("home.jsp");
            } else {
                // Authentication failed, redirect to login page with error
                response.getWriter().printf("Invalid");
                 
            }
        } catch (SQLException e) {
            e.printStackTrace();
            response.getWriter().printf("SQLException: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
            response.getWriter().printf("Exception: " + e.getMessage());
        } finally {
            // Clean up resources
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (pstmt != null) {
                try {
                    pstmt.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
            if (con != null) {
                try {
                    con.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
