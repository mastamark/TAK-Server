

package com.bbn.marti;

import java.sql.Array;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javax.naming.NamingException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.core.PreparedStatementCreator;

import com.bbn.marti.logging.AuditLogUtil;

/**
 * 
 * Class that logs SQL queries.
 * 
 */
public class JDBCQueryAuditLogHelper {
	
	private static final Logger logger = LoggerFactory.getLogger(JDBCQueryAuditLogHelper.class);

	/**
	 * Creates a general-purpose <code>PreparedStatement</code> from a string of SQL code.
	 * This can be used for any type of SQL statement: <code>SELECT</code>, <code>INSERT</code>, 
	 * <code>UPDATE</code>.
	 * 
	 * The <code>PreparedStatement</code> generated by this method does not return any 
	 * keys it may happen to auto-generate. If you want to retrieve the auto-generated keys,
	 * use <code>prepareInsert</code> instead.
	 * 
	 * 
	 * @param stmt A syntactically correct SQL statement, which may include <code>?</code> placeholders.
	 * @param con JDBC connection
	 * 
	 */
	public PreparedStatement prepareStatement(String stmt, final Connection con) throws SQLException,
	NamingException {

		AuditLogUtil.auditLog(stmt);
		
		return new PreparedStatementCreator() {

			@Override
			public PreparedStatement createPreparedStatement(Connection con) throws SQLException {
				return con.prepareStatement(stmt);
			}
		}.createPreparedStatement(con);
	}

	/**
	 * Creates a <code>PreparedStatement</code> that can return the database keys that were generated
	 * by its SQL <code>INSERT</code> statement.
	 */
	public PreparedStatement prepareInsert(String stmt, Connection connection) throws SQLException,
	NamingException {
		AuditLogUtil.auditLog(stmt);

		PreparedStatement sql = connection.prepareStatement(stmt, Statement.RETURN_GENERATED_KEYS);
		return sql;
	}


	/**
	 * Convenience method for creating SQL Arrays using the DbQueryWrapper's encapsulated Connection
	 * @param type type name of the array; must be one of the strings recognized by the database
	 * @param elements elements to store in the array
	 * @return a java.sql.Array whose elements map to the specified SQL type
	 * @throws SQLException if a database error occurs, the JDBC type is not appropriate for the typeName and the 
	 *  conversion is not supported, the typeName is null or this method is called on a closed connection 
	 */
	public Array createArrayOf(String type, Object[] elements, Connection conn) throws SQLException {

		return conn.createArrayOf(type, elements);
	}

	public ResultSet doQuery(PreparedStatement sql) throws SQLException {
		logger.debug("Executing " + sql.toString());

		auditLog(sql);

		return sql.executeQuery();
	}

	public int doUpdate(PreparedStatement sql) throws SQLException {
		logger.debug("Executing " + sql.toString());

		auditLog(sql);

		return sql.executeUpdate();
	}
	
	public ResultSet getGeneratedKeys(PreparedStatement sql) throws SQLException {
		return sql.getGeneratedKeys();
	}

	public void auditLog(PreparedStatement sql) {
		if (sql != null) {
			AuditLogUtil.auditLog(sql.toString());
		}
	}
	
	public void auditLog(String sqlString) {
		if (sqlString != null) {
			AuditLogUtil.auditLog(sqlString);
		}
	}
}
