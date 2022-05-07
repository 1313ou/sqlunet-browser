package org.sqlunet.test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Objects;

public class SqlProcessor
{
	@FunctionalInterface
	public interface Consumer<T>
	{
		void accept(T t) throws SQLException;

		default Consumer<T> andThen(Consumer<? super T> after)
		{
			Objects.requireNonNull(after);
			return (T t) -> {
				accept(t);
				after.accept(t);
			};
		}
	}

	final String db;

	public SqlProcessor(final String db)
	{
		this.db = db;
	}

	public void process(final String sql) throws SQLException
	{
		System.out.println(sql);
		connect(this.db, conn -> {

			try (Statement stmt = conn.createStatement())
			{
				try (ResultSet rs = stmt.executeQuery("EXPLAIN QUERY PLAN " + sql))
				{
					while (rs.next())
					{
						int id = rs.getInt("id");
						int parent = rs.getInt("parent");
						boolean notused = rs.getBoolean("notused");
						String detail = rs.getString("detail");

						System.out.printf("id=%s parent=%d nu=%b %s%n", id, parent, notused, detail);
					}
					System.out.println();
				}
			}
		});
	}

	public static void connect(String db, Consumer<Connection> consumer) throws SQLException
	{
		String url = "jdbc:sqlite:" + db;
		try (Connection conn = DriverManager.getConnection(url))
		{
			consumer.accept(conn);
		}
	}
}