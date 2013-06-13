package de.hswt.hrm.component.dao.jdbc;

import static com.google.common.base.Preconditions.checkArgument;
import static com.google.common.base.Preconditions.checkNotNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import javax.inject.Inject;

import org.apache.commons.dbutils.DbUtils;

import de.hswt.hrm.common.database.DatabaseFactory;
import de.hswt.hrm.common.database.NamedParameterStatement;
import de.hswt.hrm.common.database.SqlQueryBuilder;
import de.hswt.hrm.common.database.exception.DatabaseException;
import de.hswt.hrm.common.database.exception.ElementNotFoundException;
import de.hswt.hrm.common.database.exception.SaveException;
import de.hswt.hrm.component.dao.core.ICategoryDao;
import de.hswt.hrm.component.dao.core.IComponentDao;
import de.hswt.hrm.component.model.Attribute;
import de.hswt.hrm.component.model.Component;

public class ComponentDao implements IComponentDao {
    
    private final ICategoryDao categoryDao;
    
    @Inject
    public ComponentDao(ICategoryDao categoryDao){
        this.categoryDao = categoryDao;
    }

    @Override
    public Collection<Component> findAll() throws DatabaseException {
        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.select(TABLE_NAME, Fields.ID, Fields.NAME, Fields.SYMBOL_LR, Fields.SYMBOL_RL,
                Fields.SYMBOL_UD, Fields.SYMBOL_DU, Fields.QUANTIFIER, Fields.BOOL_RATING,
                Fields.CATEGORY);

        final String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {
                ResultSet result = stmt.executeQuery();
                Collection<Component> components = fromResultSet(result);
                
                DbUtils.closeQuietly(result);

                return components;
            }
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Component findById(int id) throws DatabaseException, ElementNotFoundException {
        checkArgument(id >= 0, "Id must not be negative.");

        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.select(TABLE_NAME, Fields.ID, Fields.NAME, Fields.SYMBOL_LR, Fields.SYMBOL_RL,
                Fields.SYMBOL_UD, Fields.SYMBOL_DU, Fields.QUANTIFIER, Fields.BOOL_RATING,
                Fields.CATEGORY);
        builder.where(Fields.ID);

        final String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {
                stmt.setParameter(Fields.ID, id);
                ResultSet result = stmt.executeQuery();

                Collection<Component> components = fromResultSet(result);
                DbUtils.closeQuietly(result);

                if (components.size() < 1) {
                    throw new ElementNotFoundException();
                }
                else if (components.size() > 1) {
                    throw new DatabaseException("ID '" + id + "' is not unique.");
                }

                return components.iterator().next();
            }
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
    
    @Override
    public Attribute findAttributeById(final int id) 
    		throws ElementNotFoundException, DatabaseException {
    	
    	checkArgument(id >= 0, "ID must be valid.");
    	
    	SqlQueryBuilder builder = new SqlQueryBuilder();
    	builder.select(ATTRIBUTE_TABLE_NAME, AttributeFields.ID, AttributeFields.NAME);
    	builder.where(AttributeFields.ID);
    	
    	String query = builder.toString();
    	
    	try (Connection con = DatabaseFactory.getConnection()) {
    		try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {
    			stmt.setParameter(AttributeFields.ID, id);
    			
    			ResultSet rs = stmt.executeQuery();
    			Collection<Attribute> attributes = fromAttributeResultSet(rs);
    			DbUtils.closeQuietly(rs);
    			
    			if (attributes.isEmpty()) {
    				throw new ElementNotFoundException();
    			}
    			else if(attributes.size() > 1) {
    				throw new DatabaseException("ID is not unique.");
    			}
    			
    			return attributes.iterator().next();
    		}
    	}
    	catch (SQLException e) {
    		throw new DatabaseException("Unknown error.", e);
    	}
    }
    
    @Override
    public Collection<Attribute> findAttributesByComponent(Component component) 
    		throws DatabaseException {
    	
    	checkNotNull(component, "Component must not be null.");
        checkArgument(component.getId() >= 0, "Component must have a valid ID.");

        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.select(ATTRIBUTE_TABLE_NAME, AttributeFields.ID, AttributeFields.NAME);
        builder.where(AttributeFields.FK_COMPONENT);

        final String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {
                stmt.setParameter(AttributeFields.FK_COMPONENT, component.getId());
                ResultSet result = stmt.executeQuery();

                Collection<Attribute> attributes = fromAttributeResultSet(result);
                DbUtils.closeQuietly(result);

                return attributes;
            }
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    @Override
    public Collection<String> findAttributeNames() throws DatabaseException {
    	StringBuilder builder = new StringBuilder();
    	builder.append("SELECT DISTINCT ");
    	builder.append(AttributeFields.NAME);
    	builder.append(" FROM ").append(ATTRIBUTE_TABLE_NAME).append(";");
    	
        final String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (PreparedStatement stmt = con.prepareStatement(query)) {
            	
                ResultSet rs = stmt.executeQuery();

                Collection<String> names = new ArrayList<>();
                while (rs.next()) {
                    String name = rs.getString(AttributeFields.NAME);
                    names.add(name);
                }

                DbUtils.closeQuietly(rs);

                return names;
            }
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }
    
    /**
     * @see {@link IComponentDao#insert(Component)}
     */
    @Override
    public Component insert(Component component) throws SaveException {
        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.insert(TABLE_NAME,Fields.NAME, Fields.SYMBOL_LR, Fields.SYMBOL_RL,
                Fields.SYMBOL_UD, Fields.SYMBOL_DU, Fields.QUANTIFIER, Fields.BOOL_RATING,
                Fields.CATEGORY);

        final String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {
                stmt.setParameter(Fields.NAME, component.getName());
                stmt.setParameter(Fields.SYMBOL_LR, component.getLeftRightImage());
                stmt.setParameter(Fields.SYMBOL_RL, component.getRightLeftImage());
                stmt.setParameter(Fields.SYMBOL_UD, component.getUpDownImage());
                stmt.setParameter(Fields.SYMBOL_DU, component.getDownUpImage());
                stmt.setParameter(Fields.QUANTIFIER, component.getQuantifier());
                stmt.setParameter(Fields.BOOL_RATING, component.getBoolRating());
                stmt.setParameter(Fields.CATEGORY, component.getCategory().get().getId());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows != 1) {
                    throw new SaveException();
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);

                        // Create new Component with id
                        Component inserted = new Component(id, component.getName(),
                                component.getLeftRightImage(), component.getRightLeftImage(),
                                component.getUpDownImage(), component.getDownUpImage(),
                                component.getQuantifier(), component.getBoolRating());

                        inserted.setCategory(component.getCategory().orNull());
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
    public void update(Component component) throws ElementNotFoundException, SaveException {
        checkNotNull(component, "Component must not be null.");

        if (component.getId() < 0) {
            throw new ElementNotFoundException("Element has no valid ID.");
        }

        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.update(TABLE_NAME, Fields.ID, Fields.NAME, Fields.SYMBOL_LR, Fields.SYMBOL_RL,
                Fields.SYMBOL_UD, Fields.SYMBOL_DU, Fields.QUANTIFIER, Fields.BOOL_RATING,
                Fields.CATEGORY);
        builder.where(Fields.ID);

        final String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {
                stmt.setParameter(Fields.NAME, component.getName());
                stmt.setParameter(Fields.SYMBOL_LR, component.getLeftRightImage());
                stmt.setParameter(Fields.SYMBOL_RL, component.getRightLeftImage());
                stmt.setParameter(Fields.SYMBOL_UD, component.getUpDownImage());
                stmt.setParameter(Fields.SYMBOL_DU, component.getDownUpImage());
                stmt.setParameter(Fields.QUANTIFIER, component.getQuantifier());
                stmt.setParameter(Fields.BOOL_RATING, component.getBoolRating());
                stmt.setParameter(Fields.CATEGORY, component.getCategory().get().getId());

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
    
    @Override
    public Attribute addAttribute(final Component component, final String attributeName)
    		throws SaveException {
    		
    	checkNotNull(component, "Component must not be null.");
        checkArgument(component.getId() >= 0, "Component must have a valid ID.");
    	
    	SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.insert(ATTRIBUTE_TABLE_NAME, AttributeFields.NAME, AttributeFields.FK_COMPONENT);

        final String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {
                stmt.setParameter(AttributeFields.NAME, attributeName);
                stmt.setParameter(AttributeFields.FK_COMPONENT, component.getId());

                int affectedRows = stmt.executeUpdate();
                if (affectedRows != 1) {
                    throw new SaveException();
                }

                try (ResultSet generatedKeys = stmt.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        int id = generatedKeys.getInt(1);

                        // Create new Component with id
                        Attribute attribute = new Attribute(id, attributeName);
                        return attribute;
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
    public void deleteAttribute(final Attribute attribute) throws DatabaseException {
    	checkNotNull(attribute, "Attribute must not be null.");
    	checkArgument(attribute.getId() >= 0, "Attribute must have a valid ID.");
    	
    	StringBuilder builder = new StringBuilder();
    	builder.append("DELETE FROM ").append(ATTRIBUTE_TABLE_NAME);
    	builder.append(" WHERE ").append(AttributeFields.ID);
    	builder.append(" = ?;");
    	
    	String query = builder.toString();
    	
    	try (Connection con = DatabaseFactory.getConnection()) {
    		try (PreparedStatement stmt = con.prepareStatement(query)) {
    			stmt.setInt(1, attribute.getId());
    			
    			con.setAutoCommit(false);
    			int affected = stmt.executeUpdate();
    			
    			if (affected > 1) {
    				con.rollback();
    				throw new DatabaseException("Accidently more than one row affected.");
    			}
    			else if (affected < 1) {
    				con.rollback();
    				throw new ElementNotFoundException();
    			}
    			
    			con.commit();
    		}
    	}
    	catch (SQLException | DatabaseException e) {
            throw new DatabaseException("Unknown error.", e);
        }
    }

    private Collection<Component> fromResultSet(ResultSet rs) throws SQLException, DatabaseException {
        checkNotNull(rs, "Result must not be null.");
        Collection<Component> componentList = new ArrayList<>();

        while (rs.next()) {
            int id = rs.getInt(Fields.ID);
            String name = rs.getString(Fields.NAME);
            byte[] leftRightImage = findBlob(rs.getInt(Fields.SYMBOL_LR));
            byte[] rightLeftImage = findBlob(rs.getInt(Fields.SYMBOL_RL));
            byte[] upDownImage = findBlob(rs.getInt(Fields.SYMBOL_UD));
            byte[] downUpImage = findBlob(rs.getInt(Fields.SYMBOL_DU));
            int quantifier = rs.getInt(Fields.QUANTIFIER);
            boolean boolRating = rs.getBoolean(Fields.BOOL_RATING);
            Component component = new Component(id, name, leftRightImage, rightLeftImage,
                    upDownImage, downUpImage, quantifier, boolRating);
            component.setCategory(categoryDao.findById(rs.getInt(Fields.CATEGORY)));
            componentList.add(component);
        }

        return componentList;
    }
    
    private Collection<Attribute> fromAttributeResultSet(final ResultSet rs) 
    		throws SQLException, DatabaseException {
    	
        checkNotNull(rs, "ResultSet must not be null.");
        Collection<Attribute> attributeList = new ArrayList<>();

        while (rs.next()) {
            int id = rs.getInt(AttributeFields.ID);
            String name = rs.getString(AttributeFields.NAME);
            
            Attribute attribute = new Attribute(id, name);
            attributeList.add(attribute);
        }

        return attributeList;
    }
    
    public byte[] findBlob(int id) throws DatabaseException{
        if(id <= 0){
            return null;
        }
        SqlQueryBuilder builder = new SqlQueryBuilder();
        builder.select(BLOB_TABLE_NAME, BlobFields.BLOB);
        builder.where(BlobFields.ID);
        final String query = builder.toString();

        try (Connection con = DatabaseFactory.getConnection()) {
            try (NamedParameterStatement stmt = NamedParameterStatement.fromConnection(con, query)) {
                stmt.setParameter(BlobFields.ID, id);
                ResultSet result = stmt.executeQuery();
                if(!result.next()){
                    throw new ElementNotFoundException();
                }
                byte[] bytes = result.getBytes(BlobFields.BLOB);
                DbUtils.closeQuietly(result);
                return bytes;
            }
        }
        catch (SQLException e) {
            throw new DatabaseException(e);
        }
    }

    private static final String TABLE_NAME = "Component";
    private static final String BLOB_TABLE_NAME = "Component_Picture";
    private static final String ATTRIBUTE_TABLE_NAME = "Attribute";

    private static class Fields {
        public static final String ID = "Component_ID";
        public static final String NAME = "Component_Name";
        public static final String SYMBOL_LR = "Component_Symbol_LR_FK";
        public static final String SYMBOL_RL = "Component_Symbol_RL_FK";
        public static final String SYMBOL_UD = "Component_Symbol_DU_FK";
        public static final String SYMBOL_DU = "Component_Symbol_UD_FK";
        public static final String QUANTIFIER = "Component_Quantifier";
        public static final String BOOL_RATING = "Component_Bool_Rating";
        public static final String CATEGORY = "Component_Category_FK";
    }
    
    private static class BlobFields {
        public static final String ID = "Component_Picture_ID";
        public static final String FILENAME = "Component_Picture_Filename";
        public static final String BLOB = "Component_Picture_Blob";
    }
    
    private static class AttributeFields {
    	public static final String ID = "Attribute_ID";
    	public static final String NAME = "Attribute_Name";
    	public static final String FK_COMPONENT = "Attribute_Component_FK";
    }
}
