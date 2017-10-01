package com.example.bot.spring;

import lombok.extern.slf4j.Slf4j;
import javax.annotation.PostConstruct;
import javax.sql.DataSource;
import java.sql.*;
import java.net.URISyntaxException;
import java.net.URI;


@Slf4j
public class SQLDatabaseEngine extends DatabaseEngine {
	@Override
	String search(String text) throws Exception {
		String result = null;
		Connection connection = null;
		PreparedStatement stmt = null;
		ResultSet rs = null;
		PreparedStatement stmtUpdate = null;

		//Write your code here
		try {
			connection = this.getConnection();
			stmt = connection.prepareStatement("SELECT response FROM keywordResponse WHERE ? like concat('%', LOWER(keyword), '%')");
			stmt.setString(1, text.toLowerCase());
			rs = stmt.executeQuery();

			while (result == null && rs.next()) {
				result = rs.getString(1);
				int count = rs.getInt(2);
				result = result + " This has been encountered " + Integer.toString(count) + " times.";

				try {
					stmtUpdate = connection.prepareStatement("UPDATE keywordResponse SET hit = hit + 1 WHERE ? like concat('%', LOWER(keyword), '%')");

					stmtUpdate.setString(1, text.toLowerCase());
					stmtUpdate.executeUpdate();

				} catch (SQLException e){
					System.out.println(e);
				} finally {
					if (stmtUpdate != null)
						stmtUpdate.close();
				}
			}

		} catch (SQLException e) {
			System.out.println(e);
		} finally {
			if (rs != null)
				rs.close();
			if (stmt != null)
				stmt.close();
			if (connection != null)
				connection.close();
		}
		if (result != null)
			return result;

		return new Exception("NOT FOUND");
	}


	private Connection getConnection() throws URISyntaxException, SQLException {
		Connection connection;
		URI dbUri = new URI(System.getenv("DATABASE_URL"));

		String username = dbUri.getUserInfo().split(":")[0];
		String password = dbUri.getUserInfo().split(":")[1];
		String dbUrl = "jdbc:postgresql://" + dbUri.getHost() + ':' + dbUri.getPort() + dbUri.getPath() +  "?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory";

		log.info("Username: {} Password: {}", username, password);
		log.info ("dbUrl: {}", dbUrl);

		connection = DriverManager.getConnection(dbUrl, username, password);

		return connection;
	}

}
