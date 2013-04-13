package de.hswt.hrm.common.database;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
import static com.google.common.base.Preconditions.*;

/**
 * Simple class that enables named parameters in prepared statements.
 * 
 * <p><b>Example Usage:</b>
 * <pre><code>
 * {@code
 * String query = "INSERT INTO (Col1, Col2) VALUES (:col1, :col2);";
 * NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query);
 * stmt.addParameter("col1", "Some String");
 * stmt.addParameter("col2", 5);
 * stmt.executeUpdate();
 * stmt.close();
 * }
 * </code></pre>
 * </p>
 */
public class NamedParameterStatement {
    private final Statement stmt;
    private String query;
    private Map<String, Object> params = new HashMap<>();

    /**
     * @param stmt Underlying statement which should be used.
     * @param query Query containing named parameters.
     */
    public NamedParameterStatement(Statement stmt, String query) {
        this.stmt = stmt;
        
        // Maybe we have to remove this later..
        if (query.indexOf(';') < query.length() -1) {
            throw new IllegalArgumentException("The ';' Character is only allowed at the end of "
                    + "the statement.");
        }
        
        this.query = query;
    }
    
    /**
     * Create a NamedParameterStatement from given connection and query string.
     * 
     * @param con Connection used to create the underlying statement.
     * @param query Query containing named parameters.
     * @throws SQLException
     */
    public static NamedParameterStatement fromConnection(Connection con, String query) 
            throws SQLException {
        checkArgument(!con.isClosed(), "Connection must not be closed to create a statement.");
        
        return new NamedParameterStatement(con.createStatement(), query);
    }

    /**
     * Create a NamedParameterStatement with given statement and query string.
     * 
     * @see {@link #NamedParameterStatement(Statement, String)}
     */
    public static NamedParameterStatement fromStatement(Statement stmt, String query) {
        return new NamedParameterStatement(stmt , query);
    }
    
    /**
     * Add a value for a named parameter.
     * 
     * @param name Key of the parameter.
     * @param value Value for the parameter.
     */
    public void addParameter(String name, Object value) {
        params.put(name, value);
    }
    
    /**
     * Add a couple of named parameters.
     * 
     * @param params Map of parameters.
     */
    public void addParameter(Map<String, Object> params) {
        this.params.putAll(params);
    }
    
    /**
     * Executes the statement.
     * 
     * @return true if the first result is a {@link ResultSet}
     * @throws SQLException if an error occurred
     * @see PreparedStatement#execute()
     */
    public boolean execute() throws SQLException {
        return stmt.execute(parse(query));
    }

    /**
     * Executes the statement, which must be a query.
     * 
     * @return the query results
     * @throws SQLException if an error occurred
     * @see PreparedStatement#executeQuery()
     */
    public ResultSet executeQuery() throws SQLException {
        return stmt.executeQuery(parse(query));
    }

    /**
     * Executes the statement, which must be an SQL INSERT, UPDATE or DELETE statement;
     * or an SQL statement that returns nothing, such as a DDL statement.
     * 
     * @return number of rows affected
     * @throws SQLException if an error occurred
     * @see PreparedStatement#executeUpdate()
     */
    public int executeUpdate() throws SQLException {
        return stmt.executeUpdate(parse(query));
    }

    /**
     * Closes the statement.
     * @throws SQLException if an error occurred
     * @see Statement#close()
     */
    public void close() throws SQLException {
        stmt.close();
    }

    /**
     * Adds the current set of parameters as a batch entry.
     * 
     * @throws SQLException if something went wrong
     */
    public void addBatch() throws SQLException {
        stmt.addBatch(parse(query));
    }

    /**
     * Executes all of the batched statements.
     * 
     * See {@link Statement#executeBatch()} for details.
     * @return update counts for each statement
     * @throws SQLException if something went wrong
     */
    public int[] executeBatch() throws SQLException {
        return stmt.executeBatch();
    }
    
    public String getParsedQuery() {
        return parse(query);
    }
    
    private String parse(final String query) {
        String parsed = query;
        
        for (String key : params.keySet()) {
            if (!this.query.contains(":" + key)) {
                throw new NoSuchElementException("Key '" + key + "' not found.");
            }
            
            Object value = params.get(key);
            if (value instanceof Integer
                    || value instanceof Long
                    || value instanceof Byte
                    || value instanceof Short
                    || value instanceof Double
                    || value instanceof Float) {
                
                parsed = parsed.replace(":" + key, value.toString());
            }
            else {
                parsed = parsed.replace(":" + key, "'" + escape(value.toString()) + "'");
            }
        }
        
        return parsed;
    }
    
    private String escape(String query) {
        // Escape single quotes
        query = query.replace("'", "''");
        
        return query;
    }
}
