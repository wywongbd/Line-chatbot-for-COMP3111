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
		//Write your code here
		try {
			Connection connection = this.getConnection();
			PreparedStatement stmt = connection.prepareStatement("SELECT response, hit FROM keywordResponse WHERE ? like concat('%', LOWER(keyword), '%')");
			stmt.setString(1, text.toLowerCase());
			ResultSet rs = stmt.executeQuery();

			while (result == null && rs.next()) {
				result = rs.getString(1);
				count = rs.getInt(2);

				result = result + "This keyword has been asked " + Integer.toString(count) + " times.";

				try {
					stmt_1 = connection.prepareStatement(
						"UPDATE keywordResponse
						SET hit = hit + 1,
						WHERE ? like concat('%', LOWER(keyword), '%')"
					);

					stmt_1.setString(1, text.toLowerCase());
					stmt_1.executeUpdate();

					stmt_1.close();

				} catch (Exception e) {
					System.out.println(e);
				}

			}

			rs.close();
			stmt.close();
			connection.close();

		} catch (Exception e) {
			System.out.println(e);
		}
		if (result != null)
			return result;

		return null;
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
