package de.hswt.hrm.scheme.service.test;

import static org.mockito.Mockito.*;
import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

import de.hswt.hrm.catalog.dao.core.ICatalogDao;
import de.hswt.hrm.common.database.exception.SaveException;
import de.hswt.hrm.component.dao.core.ICategoryDao;
import de.hswt.hrm.component.dao.core.IComponentDao;
import de.hswt.hrm.component.dao.jdbc.CategoryDao;
import de.hswt.hrm.component.dao.jdbc.ComponentDao;
import de.hswt.hrm.component.model.Attribute;
import de.hswt.hrm.component.model.Component;
import de.hswt.hrm.place.dao.core.IPlaceDao;
import de.hswt.hrm.place.dao.jdbc.PlaceDao;
import de.hswt.hrm.place.model.Place;
import de.hswt.hrm.plant.dao.core.IPlantDao;
import de.hswt.hrm.plant.dao.jdbc.PlantDao;
import de.hswt.hrm.plant.model.Plant;
import de.hswt.hrm.scheme.dao.core.ISchemeComponentDao;
import de.hswt.hrm.scheme.dao.core.ISchemeDao;
import de.hswt.hrm.scheme.dao.jdbc.SchemeComponentDao;
import de.hswt.hrm.scheme.dao.jdbc.SchemeDao;
import de.hswt.hrm.scheme.model.Scheme;
import de.hswt.hrm.test.database.AbstractDatabaseTest;

public class SchemeServiceTest extends AbstractDatabaseTest {
	
	private IComponentDao createComponentDao() {
		ICatalogDao catalogDao = mock(ICatalogDao.class);
		ICategoryDao categoryDao = new CategoryDao(catalogDao);
		return new ComponentDao(categoryDao);		
	}
	
	private IPlantDao createPlantDao() {
		IPlaceDao placeDao = new PlaceDao();
		return new PlantDao(placeDao);
	}
	
	private ISchemeDao createSchemeDao() {
		return new SchemeDao(createPlantDao());
	}

	private Plant createTestPlant() throws SaveException {
		IPlantDao plantDao = createPlantDao();
		Plant plant = new Plant("Some plant");
		plant.setPlace(new Place("Somewhere", "55555", "City", "Street", "15"));
		return plantDao.insert(plant);
	}
	
	private List<Attribute> createTestAttributes() throws SaveException {
		IComponentDao componentDao = createComponentDao();
		
		Component component = componentDao.insert(
				new Component(
						"SomeComp",
						new byte[1], 
						new byte[1], 
						new byte[1], 
						new byte[1], 
						0, 
						true));
		
		List<Attribute> attributes = new ArrayList<>();
		attributes.add(componentDao.addAttribute(component, "FirstAttribute"));
		attributes.add(componentDao.addAttribute(component, "SecondAttribute"));
		
		return attributes;
	}

	@Test
	public void testInsertScheme() throws SaveException {
		Plant plant = createTestPlant();
		ISchemeDao schemeDao = createSchemeDao();
		
		Scheme scheme = new Scheme(plant);
		System.out.println(plant.getId());
		schemeDao.insert(scheme);
		
		// TODO: check if scheme can be resolved correctly
	}
	
    @Test
    public void testSetAttributes() throws SaveException {
        List<Attribute> attributes = createTestAttributes();
    }

}
