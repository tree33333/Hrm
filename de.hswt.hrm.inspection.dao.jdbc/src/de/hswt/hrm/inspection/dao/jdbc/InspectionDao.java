package de.hswt.hrm.inspection.dao.jdbc;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.GregorianCalendar;

import javax.inject.Inject;

import com.google.common.base.Optional;

import de.hswt.hrm.common.database.DatabaseFactory;
import de.hswt.hrm.common.database.JdbcUtil;
import de.hswt.hrm.common.database.NamedParameterStatement;
import de.hswt.hrm.common.database.SqlQueryBuilder;
import de.hswt.hrm.common.database.exception.DatabaseException;
import de.hswt.hrm.common.database.exception.ElementNotFoundException;
import de.hswt.hrm.common.database.exception.SaveException;
import de.hswt.hrm.contact.model.Contact;
import de.hswt.hrm.inspection.dao.core.IInspectionDao;
import de.hswt.hrm.inspection.dao.core.ILayoutDao;
import de.hswt.hrm.inspection.model.Inspection;
import de.hswt.hrm.inspection.model.Layout;
import de.hswt.hrm.plant.dao.core.IPlantDao;
import de.hswt.hrm.plant.model.Plant;
import de.hswt.hrm.contact.dao.core.IContactDao;

public class InspectionDao implements IInspectionDao {
    // FIXME Make dao injectable
    ILayoutDao layoutDao = new LayoutDao();

    private final IContactDao contactDao;
    private final IPlantDao plantDao;

    @Inject
    public InspectionDao(IContactDao contactDao, IPlantDao plantDao) {
        this.contactDao = contactDao;
        this.plantDao = plantDao;

    }

    @Override
    public Collection<Inspection> findAll() throws DatabaseException {
        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.select(TABLE_NAME, Fields.ID, Fields.LAYOUT_FK, Fields.PLANT_FK,
                Fields.REQUESTER_FK, Fields.CONTRACTOR_FK, Fields.CHECKER_FK,
                Fields.INSPECTIONDATE, Fields.REPORTDATE, Fields.NEXTDATE, Fields.TEMPERATURE,
                Fields.HUMIDITY, Fields.SUMMARY, Fields.TITEL, Fields.TEMPERATURERATING,
                Fields.TEMPERATUREQUANTIFIER, Fields.HUMIDITYRATING, Fields.HUMIDITYQUANTIFIER);

        String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {

                ResultSet rs = stmt.executeQuery();
                Collection<Inspection> inspections = fromResultSet(rs,
                        Optional.<Inspection> absent());
                rs.close();

                return inspections;
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("Unexpected error.", e);
        }
    }

    @Override
    public Inspection findById(int id) throws DatabaseException, ElementNotFoundException {
        checkArgument(id >= 0, "ID must be non negative.");

        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.select(TABLE_NAME, Fields.ID, Fields.LAYOUT_FK, Fields.PLANT_FK,
                Fields.REQUESTER_FK, Fields.CONTRACTOR_FK, Fields.CHECKER_FK,
                Fields.INSPECTIONDATE, Fields.REPORTDATE, Fields.NEXTDATE, Fields.TEMPERATURE,
                Fields.HUMIDITY, Fields.SUMMARY, Fields.TITEL, Fields.TEMPERATURERATING,
                Fields.TEMPERATUREQUANTIFIER, Fields.HUMIDITYRATING, Fields.HUMIDITYQUANTIFIER);
        builder.where(Fields.ID);

        String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {
                stmt.setParameter(Fields.ID, id);

                ResultSet rs = stmt.executeQuery();
                Collection<Inspection> ratings = fromResultSet(rs, Optional.<Inspection> absent());
                rs.close();

                if (ratings.isEmpty()) {
                    throw new ElementNotFoundException();
                }
                else if (ratings.size() > 1) {
                    throw new DatabaseException("ID is not unique.");
                }

                return ratings.iterator().next();
            }
        }
        catch (SQLException e) {
            throw new DatabaseException("Unknown error.", e);
        }
    }

    @Override
    public Inspection insert(Inspection inspection) throws SaveException {
        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.insert(TABLE_NAME, Fields.LAYOUT_FK, Fields.PLANT_FK, Fields.REQUESTER_FK,
                Fields.CONTRACTOR_FK, Fields.CHECKER_FK, Fields.INSPECTIONDATE, Fields.REPORTDATE,
                Fields.NEXTDATE, Fields.TEMPERATURE, Fields.HUMIDITY, Fields.SUMMARY, Fields.TITEL,
                Fields.TEMPERATURERATING, Fields.TEMPERATUREQUANTIFIER, Fields.HUMIDITYRATING,
                Fields.HUMIDITYQUANTIFIER);

        final String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {
                stmt.setParameter(Fields.LAYOUT_FK, inspection.getLayout().getId());
                stmt.setParameter(Fields.PLANT_FK, inspection.getPlant().getId());
                stmt.setParameter(Fields.REQUESTER_FK, inspection.getRequester().get().getId());
                stmt.setParameter(Fields.CONTRACTOR_FK, inspection.getContractor().get().getId());
                stmt.setParameter(Fields.CHECKER_FK, inspection.getChecker().get().getId());
                stmt.setParameter(Fields.INSPECTIONDATE, inspection.getInspectionDate());
                stmt.setParameter(Fields.REPORTDATE, inspection.getReportDate());
                stmt.setParameter(Fields.NEXTDATE, inspection.getNextInspectionDate());
                stmt.setParameter(Fields.TEMPERATURE, inspection.getTemperature().orNull());
                stmt.setParameter(Fields.HUMIDITY, inspection.getHumidity().orNull());
                stmt.setParameter(Fields.SUMMARY, inspection.getSummary().orNull());
                stmt.setParameter(Fields.TITEL, inspection.getTitle());
                stmt.setParameter(Fields.TEMPERATURERATING, inspection.getTemperatureRating()
                        .orNull());
                stmt.setParameter(Fields.TEMPERATUREQUANTIFIER, inspection
                        .getTemperatureQuantifier().orNull());
                stmt.setParameter(Fields.HUMIDITYRATING, inspection.getHumidityRating().orNull());
                stmt.setParameter(Fields.HUMIDITYQUANTIFIER, inspection.getHumidityQuantifier()
                        .orNull());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows != 1) {
                    throw new SaveException();
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);

                        // Create new Inspection with id
                        Inspection inserted = new Inspection(id, inspection.getReportDate(),
                                inspection.getInspectionDate(), inspection.getNextInspectionDate(),
                                inspection.getTitle(), inspection.getLayout(),
                                inspection.getPlant());

                        inserted.setRequester(inspection.getRequester().orNull());
                        inserted.setContractor(inspection.getContractor().orNull());
                        inserted.setChecker(inspection.getChecker().orNull());
                        inserted.setTemperature(inspection.getTemperature().orNull());
                        inserted.setHumidity(inspection.getHumidity().orNull());
                        inserted.setSummary(inspection.getSummary().orNull());
                        inserted.setTemperatureRating(inspection.getTemperatureRating().orNull());
                        inserted.setTemperatureQuantifier(inspection.getTemperatureQuantifier()
                                .orNull());
                        inserted.setHumidityRating(inspection.getHumidityRating().orNull());
                        inserted.setHumidityQuantifier(inspection.getHumidityQuantifier().orNull());
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
    public void update(Inspection inspection) throws ElementNotFoundException, SaveException {
        checkNotNull(inspection, "inspection must not be null.");

        if (inspection.getId() < 0) {
            throw new ElementNotFoundException("Element has no valid ID.");
        }

        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.update(TABLE_NAME, Fields.LAYOUT_FK, Fields.PLANT_FK, Fields.REQUESTER_FK,
                Fields.CONTRACTOR_FK, Fields.CHECKER_FK, Fields.INSPECTIONDATE, Fields.REPORTDATE,
                Fields.NEXTDATE, Fields.TEMPERATURE, Fields.HUMIDITY, Fields.SUMMARY, Fields.TITEL,
                Fields.TEMPERATURERATING, Fields.TEMPERATUREQUANTIFIER, Fields.HUMIDITYRATING,
                Fields.HUMIDITYQUANTIFIER);
        builder.where(Fields.ID);

        final String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {
                stmt.setParameter(Fields.LAYOUT_FK, inspection.getLayout().getId());
                stmt.setParameter(Fields.PLANT_FK, inspection.getPlant().getId());
                stmt.setParameter(Fields.REQUESTER_FK, inspection.getRequester().get().getId());
                stmt.setParameter(Fields.CONTRACTOR_FK, inspection.getContractor().get().getId());
                stmt.setParameter(Fields.CHECKER_FK, inspection.getChecker().get().getId());
                stmt.setParameter(Fields.INSPECTIONDATE, inspection.getInspectionDate());
                stmt.setParameter(Fields.REPORTDATE, inspection.getReportDate());
                stmt.setParameter(Fields.NEXTDATE, inspection.getNextInspectionDate());
                stmt.setParameter(Fields.TEMPERATURE, inspection.getTemperature().orNull());
                stmt.setParameter(Fields.HUMIDITY, inspection.getHumidity().orNull());
                stmt.setParameter(Fields.SUMMARY, inspection.getSummary().orNull());
                stmt.setParameter(Fields.TITEL, inspection.getTitle());
                stmt.setParameter(Fields.TEMPERATURERATING, inspection.getTemperatureRating()
                        .orNull());
                stmt.setParameter(Fields.TEMPERATUREQUANTIFIER, inspection
                        .getTemperatureQuantifier().orNull());
                stmt.setParameter(Fields.HUMIDITYRATING, inspection.getHumidityRating().orNull());
                stmt.setParameter(Fields.HUMIDITYQUANTIFIER, inspection.getHumidityQuantifier()
                        .orNull());

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

    private Collection<Inspection> fromResultSet(final ResultSet rs, Optional<Inspection> inspection)
            throws SQLException, ElementNotFoundException, DatabaseException {

        checkNotNull(rs, "Result must not be null.");
        Collection<Inspection> inspectionList = new ArrayList<>();
        while (rs.next()) {
            int id = rs.getInt(Fields.ID);
            
            // mandatory fk's
            int layoutId = JdbcUtil.getId(rs, Fields.LAYOUT_FK);
            checkArgument(layoutId >= 0, "Invalid layout key from database.");
            Layout layout = layoutDao.findById(layoutId);
            int plantId = JdbcUtil.getId(rs, Fields.PLANT_FK);
            checkArgument(plantId >= 0, "Invalid plant key from database.");
            Plant plant = plantDao.findById(plantId);
            
            // optional fks's
            int requesterId = JdbcUtil.getId(rs, Fields.REQUESTER_FK);
            int contractorId = JdbcUtil.getId(rs, Fields.CONTRACTOR_FK);
            int checkerId = JdbcUtil.getId(rs, Fields.CHECKER_FK);
            
            // calendars
            Timestamp inspectionTimestamp = rs.getTimestamp(Fields.INSPECTIONDATE);
            Calendar inspectionDate = JdbcUtil.calendarFromTimestamp(inspectionTimestamp);
            Timestamp reportTimestamp = rs.getTimestamp(Fields.REPORTDATE);
            Calendar reportDate = JdbcUtil.calendarFromTimestamp(reportTimestamp);
            Timestamp nextInspectionTimestamp = rs.getTimestamp(Fields.NEXTDATE);
            Calendar nextInspectionDate = JdbcUtil.calendarFromTimestamp(nextInspectionTimestamp);
            // rest
            int temperature = rs.getInt(Fields.TEMPERATURE);
            int humidity = rs.getInt(Fields.HUMIDITY);
            String summary = rs.getString(Fields.SUMMARY);
            String title = rs.getString(Fields.TITEL);
            int temperatureRating = rs.getInt(Fields.TEMPERATURERATING);
            int temperatureQuantifier = rs.getInt(Fields.TEMPERATUREQUANTIFIER);
            int humidityRating = rs.getInt(Fields.HUMIDITYRATING);
            int humidityQuantifier = rs.getInt(Fields.HUMIDITYQUANTIFIER);

            Inspection inserted = new Inspection(id, reportDate, inspectionDate,
                    nextInspectionDate, title, layout, plant);

            // optional fk's
            if (requesterId >= 0) {
                Contact requester = contactDao.findById(requesterId);
                inserted.setRequester(requester);
            }
            if (contractorId >= 0) {
                Contact contractor = contactDao.findById(contractorId);
                inserted.setContractor(contractor);
            }
            if (checkerId >= 0) {
                Contact checker = contactDao.findById(checkerId);
                inserted.setChecker(checker);
            }

            // rest
            inserted.setTemperature(temperature);
            inserted.setHumidity(humidity);
            inserted.setSummary(summary);
            inserted.setTemperatureRating(temperatureRating);
            inserted.setTemperatureQuantifier(temperatureQuantifier);
            inserted.setHumidityRating(humidityRating);
            inserted.setHumidityQuantifier(humidityQuantifier);

            inspectionList.add(inserted);
        }

        return inspectionList;
    }

    private static final String TABLE_NAME = "Report";

    private static final class Fields {
        public static final String ID = "Report_ID";
        public static final String LAYOUT_FK = "Report_Layout_FK";
        public static final String PLANT_FK = "Report_Plant_FK";
        public static final String REQUESTER_FK = "Report_Requester_FK";
        public static final String CONTRACTOR_FK = "Report_Contractor_FK";
        public static final String CHECKER_FK = "Report_Checker_FK";
        public static final String INSPECTIONDATE = "Report_Jobdate";
        public static final String REPORTDATE = "Report_Reportdate";
        public static final String NEXTDATE = "Report_Nextdate";
        public static final String TEMPERATURE = "Report_Airtemperature";
        public static final String HUMIDITY = "Report_Humidity";
        public static final String SUMMARY = "Report_Summary";
        public static final String TITEL = "Report_Titel";
        public static final String TEMPERATURERATING = "Report_Airtemperature_Rating";
        public static final String TEMPERATUREQUANTIFIER = "Report_Airtemperature_Quantifier";
        public static final String HUMIDITYRATING = "Report_Humidity_Rating";
        public static final String HUMIDITYQUANTIFIER = "Report_Humidity_Quantifier";
    }

}
