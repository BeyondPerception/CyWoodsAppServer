package ml.dent.servlet;

import ml.dent.util.Default;
import ml.dent.util.Logger;
import ml.dent.web.StudentFetcher;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Scanner;

/**
 * The frontend will send an HTTP request to this servlet at the above endpoint
 * (e.g. www.dent.ml/CyWoodsAppServer/Grades), and we'll write our response to
 * the HTTP response.
 *
 * @author Ronak Malik
 */
@WebServlet("/Student")
public class StudentServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private Logger logger;

    public StudentServlet() {
        super();
        logger = new Logger("Student");
    }

    /**
     * GET Requests are the default HTTP request, so we want to give a formatted
     * response if someone tries to perform a GET, but we don't wan't to accept any
     * GET requests as they are insecure for this servlet.
     */
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        PrintWriter pw = resp.getWriter();

        pw.println(Default.BadRequest("GET Requests are not allowed!"));
    }

    /**
     * If we use a GET request, the username and password will stored in the url and
     * can be easily intercepted. Even if both are encrypted in some way, this is
     * usually bad practice. In this case, since we are sending and receiving
     * usernames and passwords, POST requests can send parameters through a hidden
     * form.
     */
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        resp.setCharacterEncoding("UTF-8");
        PrintWriter pw = resp.getWriter();

        String username = req.getParameter("username");
        String password = req.getParameter("password");
        String id = req.getParameter("id");

        if (username == null) {
            pw.println(Default.BadRequest("No Username Provided"));
            return;
        }
        if (password == null) {
            pw.println(Default.BadRequest("No Password Provided"));
            return;
        }
        if (id == null) {
            pw.println(Default.BadRequest("No ID Provided"));
            return;
        }
        // Required test user to publish to App Store as well as useful for front-end debugging.
        if (username.equals("s0") && password.equals("test")) {
            boolean success = handleTestUser(pw);
            if (success) {
                return;
            }
        }

        StudentFetcher grades = new StudentFetcher(username, password, id);
        try {
            String ret = grades.populateStudent();
            if (ret.contains("false")) {
                pw.println(ret);
            } else {
                String data = grades.returnStudent().getJsonData().format();
                pw.println(data);
            }
        } catch (Exception e) {
            pw.println(Default.InternalServerError("Failed to fetch grades"));
            logger.log("ID: " + id);
            logger.logError(e);
        }
        /*
         * Scanner file = new Scanner(new File(System.getProperty("user.home") +
         * "/dummyData.json")); while (file.hasNext()) { pw.println(file.nextLine()); }
         */
    }

    /**
     * Get test user data from the provided file and send it back to the client, or generate our own data if the file doesn't exist.
     * The data in this file is directly sent to the client, so it should be correctly formatted as server json data.
     */
    private boolean handleTestUser(PrintWriter pw) {
        File testUserFile = new File("/efs/UpdateFiles/TestUser.txt");
        if (testUserFile.exists()) {
            StringBuilder res = new StringBuilder();
            try {
                Scanner file = new Scanner(testUserFile);
                while (file.hasNext()) {
                    pw.println(file.nextLine());
                }
            } catch (FileNotFoundException e) {
                pw.println(Default.InternalServerError("Failed to read test user's file"));
                logger.logError(e);
            }
        } else {
            // Fall back to legacy method of generating test data.
            return false;
        }
        return true;
    }
}
