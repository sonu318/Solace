package SolaceServletPackage;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

import javax.jms.Session;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;

/**
 * Servlet implementation class SolaceServlet
 */
@WebServlet("/SolaceServlet")
public class SolaceServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;

	/**
	 * Default constructor.
	 */
	public SolaceServlet() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */

	javax.jms.Connection solaceConn;

	protected void doGet(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse
	 *      response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		// TODO Auto-generated method stub
		Session session = null;
		try {

			Connection conn = connectToDatabase();
			String msg = request.getParameter("message");
			SolaceProducer sendmsg = new SolaceProducer();

			sendmsg.sentToSolace(solaceConn, session, msg);

			insertToDatabase(conn, msg);

			commitOrRollback(conn, msg);

		} catch (Exception e) {
			System.out.println("Failure while writing to solace Queue ");

		}

	}

	private Connection connectToDatabase() throws NamingException, SQLException {
		Connection connection;
		Context context = new InitialContext();
		DataSource dataSource = (DataSource) context.lookup("Datasource_name");
		connection = dataSource.getConnection();

		return connection;
	}

	private void insertToDatabase(Connection conn, String message) throws SQLException {
		conn.setAutoCommit(false);
		String sql = "insert into Table_name ( TEST_CASE, MESSAGE)values(?,?)";
		PreparedStatement statement = conn.prepareStatement(sql);
		statement.setString(1, "NON-XA queue");
		statement.setString(2, message);

		statement.executeUpdate();

	}

	private void commitOrRollback(Connection connection, String message) throws SQLException {

		if (message.equalsIgnoreCase("success")) {
			connection.commit();
		} else {
			connection.rollback();
		}
	}

}
