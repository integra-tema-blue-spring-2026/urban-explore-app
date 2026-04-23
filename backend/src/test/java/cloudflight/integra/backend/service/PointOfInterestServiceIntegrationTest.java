package cloudflight.integra.backend.service;
import cloudflight.integra.backend.exceptions.custom.CityNotFoundException;
import cloudflight.integra.backend.exceptions.custom.PointOfInterestNotFoundException;
import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.utils.enums.PointOfInterestType;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import({PointOfInterestService.class, CityService.class})
public class PointOfInterestServiceIntegrationTest {

    @Autowired
    private PointOfInterestService pointOfInterestService;

    @Autowired
    private CityService cityService;

    private City persistedCity;

    @BeforeEach
    void setUp() {
        persistedCity = cityService.createCity(City.builder()
            .name("Cluj-Napoca")
            .country("Romania")
            .description("A great city in Transylvania.")
            .imageUrl("http://example.com/cluj.jpg")
            .build());
    }

    private PointOfInterest buildPoi() {
        PointOfInterest poi = new PointOfInterest();
        poi.setName("Central Park Museum");
        poi.setDescription("A museum in the city center.");
        poi.setType(PointOfInterestType.MUSEUM);
        poi.setAddress("1 Main Street");
        poi.setCity(persistedCity);
        return poi;
    }

    @Test
    void save_ShouldPersistPointOfInterest_AndAssignId() {
        PointOfInterest saved = pointOfInterestService.save(buildPoi());

        assertNotNull(saved.getId());
        assertEquals("Central Park Museum", saved.getName());
        assertEquals(1, pointOfInterestService.getAll().size());
    }

    @Test
    void save_ShouldThrowCityNotFoundException_WhenCityDoesNotExist() {
        City nonPersistedCity = City.builder()
            .id(java.util.UUID.randomUUID())
            .name("Ghost City")
            .country("Nowhere")
            .build();

        PointOfInterest poi = buildPoi();
        poi.setCity(nonPersistedCity);

        assertThrows(CityNotFoundException.class, () -> pointOfInterestService.save(poi));
        assertTrue(pointOfInterestService.getAll().isEmpty());
    }

    @Test
    void findById_ShouldReturnCorrectPointOfInterest_WhenIdExists() {
        PointOfInterest saved = pointOfInterestService.save(buildPoi());

        PointOfInterest found = pointOfInterestService.findById(saved.getId());

        assertNotNull(found);
        assertEquals(saved.getId(), found.getId());
        assertEquals("Central Park Museum", found.getName());
    }

    @Test
    void findById_ShouldThrowPointOfInterestNotFoundException_WhenIdDoesNotExist() {
        assertThrows(PointOfInterestNotFoundException.class,
            () -> pointOfInterestService.findById(java.util.UUID.randomUUID()));
    }

    @Test
    void update_ShouldPersistChanges_WhenValidDataProvided() {
        PointOfInterest saved = pointOfInterestService.save(buildPoi());

        saved.setName("Renamed Museum");
        saved.setDescription("Updated description.");
        pointOfInterestService.update(saved);

        PointOfInterest reloaded = pointOfInterestService.findById(saved.getId());
        assertEquals("Renamed Museum", reloaded.getName());
        assertEquals("Updated description.", reloaded.getDescription());
    }

    @Test
    void update_ShouldThrowPointOfInterestNotFoundException_WhenPoiDoesNotExist() {
        PointOfInterest nonExistent = buildPoi();
        nonExistent.setId(java.util.UUID.randomUUID());

        assertThrows(PointOfInterestNotFoundException.class,
            () -> pointOfInterestService.update(nonExistent));
    }

    @Test
    void update_ShouldThrowCityNotFoundException_WhenCityDoesNotExist() {
        PointOfInterest saved = pointOfInterestService.save(buildPoi());

        City ghostCity = City.builder()
            .id(java.util.UUID.randomUUID())
            .name("Ghost City")
            .country("Nowhere")
            .build();
        saved.setCity(ghostCity);

        assertThrows(CityNotFoundException.class,
            () -> pointOfInterestService.update(saved));
    }

    @Test
    void deleteById_ShouldRemovePointOfInterest_WhenIdExists() {
        PointOfInterest saved = pointOfInterestService.save(buildPoi());

        pointOfInterestService.deleteById(saved.getId());

        assertTrue(pointOfInterestService.getAll().isEmpty());
    }

    @Test
    void deleteById_ShouldThrowPointOfInterestNotFoundException_WhenIdDoesNotExist() {
        assertThrows(PointOfInterestNotFoundException.class,
            () -> pointOfInterestService.deleteById(java.util.UUID.randomUUID()));
    }
}
