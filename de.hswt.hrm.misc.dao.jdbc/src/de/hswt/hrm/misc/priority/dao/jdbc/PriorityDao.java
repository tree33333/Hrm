package de.hswt.hrm.misc.priority.dao.jdbc;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.commons.dbutils.DbUtils;

import de.hswt.hrm.common.database.DatabaseFactory;
import de.hswt.hrm.common.database.NamedParameterStatement;
import de.hswt.hrm.common.database.SqlQueryBuilder;
import de.hswt.hrm.common.database.exception.DatabaseException;
import de.hswt.hrm.common.database.exception.ElementNotFoundException;
import de.hswt.hrm.common.database.exception.SaveException;
import de.hswt.hrm.misc.priority.dao.core.IPriorityDao;
import de.hswt.hrm.misc.priority.model.Priority;

public class PriorityDao implements IPriorityDao {

    @Override
    public Collection<Priority> findAll() throws DatabaseException {
        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.select(TABLE_NAME, Fields.ID, Fields.NAME, Fields.TEXT, Fields.PRIORITY);

        final String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {
                ResultSet result = stmt.executeQuery();

                Collection<Priority> priorities = fromResultSet(result);
                DbUtils.closeQuietly(result);

                return priorities;
            }
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Priority findById(int id) throws DatabaseException, ElementNotFoundException {
        checkArgument(id >= 0, "Id must not be negative.");

        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.select(TABLE_NAME, Fields.ID, Fields.NAME, Fields.TEXT, Fields.PRIORITY);
        builder.where(Fields.ID);

        final String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {
                stmt.setParameter(Fields.ID, id);
                ResultSet result = stmt.executeQuery();

                Collection<Priority> priorities = fromResultSet(result);
                DbUtils.closeQuietly(result);

                if (priorities.size() < 1) {
                    throw new ElementNotFoundException();
                }
                else if (priorities.size() > 1) {
                    throw new DatabaseException("ID '" + id + "' is not unique.");
                }

                return priorities.iterator().next();
            }
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Priority insert(Priority priority) throws SaveException {
        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.insert(TABLE_NAME, Fields.NAME, Fields.TEXT, Fields.PRIORITY);

        final String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {
                stmt.setParameter(Fields.NAME, priority.getName());
                stmt.setParameter(Fields.TEXT, priority.getText());
                stmt.setParameter(Fields.PRIORITY, priority.getPriority());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows != 1) {
                    throw new SaveException();
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);

                        // Create new Priority with id
                        Priority inserted = new Priority(id, priority.getName(),
                                priority.getText(), priority.getPriority());

                        return inserted;
                    }
                    else {
                        throw new SaveException("Could not retrieve generated ID.");
                    }
                }
            }

        }
        catch (SQLException | DatabaseException e) {
            throw new SaveException(e);
        }
    }

    @Override
    public void update(Priority priority) throws ElementNotFoundException, SaveException {
        checkNotNull(priority, "Priority must not be null.");

        if (priority.getId() < 0) {
            throw new ElementNotFoundException("Element has no valid ID.");
        }

        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.update(TABLE_NAME, Fields.NAME, Fields.TEXT, Fields.PRIORITY);
        builder.where(Fields.ID);

        final String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {
                stmt.setParameter(Fields.ID, priority.getId());
                stmt.setParameter(Fields.NAME, priority.getName());
                stmt.setParameter(Fields.TEXT, priority.getText());
                stmt.setParameter(Fields.PRIORITY, priority.getPriority());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows != 1) {
                    throw new SaveException();
                }
            }
        }
        catch (SQLException | DatabaseException e) {
            throw new SaveException(e);
        }
    }

    private Collection<Priority> fromResultSet(ResultSet rs) throws SQLException {
        checkNotNull(rs, "Result must not be null.");
        Collection<Priority> priorityList = new ArrayList<>();

        while (rs.next()) {
            int id = rs.getInt(Fields.ID);
            String name = rs.getString(Fields.NAME);
            String text = rs.getString(Fields.TEXT);
            int priority = rs.getInt(Fields.PRIORITY);

            Priority prio = new Priority(id, name, text, priority);

            priorityList.add(prio);
        }

        return priorityList;
    }

    private static final String TABLE_NAME = "Priority";

    private static class Fields {
        public static final String ID = "Priority_Id";
        public static final String NAME = "Priority_Name";
        public static final String TEXT = "Priority_Text";
        public static final String PRIORITY = "Priority_Priority";

    }

}
