package ml.dent.app;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.junit.Test;

public class GradeServletTest {

	@Test
	public void test() throws Exception {
		HttpServletRequest req = mock(HttpServletRequest.class);
		HttpServletResponse resp = mock(HttpServletResponse.class);

		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		when(resp.getWriter()).thenReturn(pw);

		// new GradeServlet().doGet(req, resp);

		pw.flush();
		System.out.println(sw.toString());
	}
}
