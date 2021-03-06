package de.hswt.hrm.summary.dao.jdbc;

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
import de.hswt.hrm.summary.dao.core.ISummaryDao;
import de.hswt.hrm.summary.model.Summary;

public class SummaryDao implements ISummaryDao {

    @Override
    public Collection<Summary> findAll() throws DatabaseException {

        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.select(TABLE_NAME, Fields.ID, Fields.NAME, Fields.TEXT);

        final String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {
                ResultSet result = stmt.executeQuery();

                Collection<Summary> evaluations = fromResultSet(result);
                DbUtils.closeQuietly(result);

                return evaluations;
            }
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Summary findById(int id) throws DatabaseException, ElementNotFoundException {

        checkArgument(id >= 0, "Id must not be negative.");

        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.select(TABLE_NAME, Fields.ID, Fields.NAME, Fields.TEXT);
        builder.where(Fields.ID);

        final String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {
                stmt.setParameter(Fields.ID, id);
                ResultSet result = stmt.executeQuery();

                Collection<Summary> evaluations = fromResultSet(result);
                DbUtils.closeQuietly(result);

                if (evaluations.size() < 1) {
                    throw new ElementNotFoundException();
                }
                else if (evaluations.size() > 1) {
                    throw new DatabaseException("ID '" + id + "' is not unique.");
                }

                return evaluations.iterator().next();
            }
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Summary insert(Summary evaluation) throws SaveException {
        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.insert(TABLE_NAME, Fields.NAME, Fields.TEXT);

        final String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {
                stmt.setParameter(Fields.NAME, evaluation.getName());
                stmt.setParameter(Fields.TEXT, evaluation.getText());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows != 1) {
                    throw new SaveException();
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);

                        // Create new Summary with id
                        Summary inserted = new Summary(id, evaluation.getName(),
                                evaluation.getText());
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
    public void update(Summary evaluation) throws ElementNotFoundException, SaveException {
        checkNotNull(evaluation, "Summary must not be null.");

        if (evaluation.getId() < 0) {
            throw new ElementNotFoundException("Element has no valid ID.");
        }

        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.update(TABLE_NAME, Fields.NAME, Fields.TEXT);
        builder.where(Fields.ID);

        final String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {
                stmt.setParameter(Fields.ID, evaluation.getId());
                stmt.setParameter(Fields.NAME, evaluation.getName());
                stmt.setParameter(Fields.TEXT, evaluation.getText());

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

    private Collection<Summary> fromResultSet(ResultSet rs) throws SQLException {
        checkNotNull(rs, "Result must not be null.");
        Collection<Summary> evaluationList = new ArrayList<>();

        while (rs.next()) {
            int id = rs.getInt(Fields.ID);
            String evaluationName = rs.getString(Fields.NAME);
            String evaluationTest = rs.getString(Fields.TEXT);

            Summary eval = new Summary(id, evaluationName, evaluationTest);

            evaluationList.add(eval);
        }

        return evaluationList;
    }

    private static final String TABLE_NAME = "Summary";

    private static class Fields {
        public static final String ID = "Summary_ID";
        public static final String NAME = "Summary_Name";
        public static final String TEXT = "Summary_Text";
    }

}
