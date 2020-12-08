package ml.dent.servlet;

import ml.dent.util.Default;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

/**
 * Servlet implementation class TestServlet
 */
@WebServlet("/Testing")
public class TestServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    /**
     * @see HttpServlet#HttpServlet()
     */
    public TestServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        PrintWriter pw = response.getWriter();

        pw.println(Default.BadRequest("GET Requests are not allowed!"));
    }


    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setCharacterEncoding("UTF-8");
        PrintWriter pw = response.getWriter();

        String testUserJson = request.getParameter("data");

        if (testUserJson == null) {
            pw.println(Default.BadRequest("No data provided"));
            return;
        }
        PrintWriter fw = new PrintWriter(new File("/efs/UpdateFiles/TestUser.txt"));
        fw.println(testUserJson);
        fw.flush();
        fw.close();
        pw.println(Default.OK("Successfully updated test user data"));
    }
}
