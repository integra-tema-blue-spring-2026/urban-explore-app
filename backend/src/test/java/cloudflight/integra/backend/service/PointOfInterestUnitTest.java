package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.CityNotFoundException;
import cloudflight.integra.backend.exceptions.custom.PointOfInterestNotFoundException;
import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.utils.enums.PointOfInterestType;
import cloudflight.integra.backend.repository.CityRepository;
import cloudflight.integra.backend.repository.PointOfInterestRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class PointOfInterestUnitTest {

    @Mock
    private PointOfInterestRepository pointOfInterestRepository;

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private PointOfInterestService pointOfInterestService;

    private UUID testCityId;
    private UUID testPointOfInterestId;
    private PointOfInterest testPointOfInterest;

    @BeforeEach
    void setUp() {
        testCityId = UUID.randomUUID();
        testPointOfInterestId = UUID.randomUUID();

        City testCity = City.builder()
            .id(testCityId)
            .name("Test City")
            .country("Test Country")
            .description("A test city.")
            .imageUrl("http://example.com/testcity.jpg")
            .build();

        testPointOfInterest = new PointOfInterest();
        testPointOfInterest.setId(testPointOfInterestId) ;
        testPointOfInterest.setName("Test Point of Interest");
        testPointOfInterest.setDescription("A test point of interest.");
        testPointOfInterest.setCity(testCity);
        testPointOfInterest.setType(PointOfInterestType.MUSEUM);
        testPointOfInterest.setAddress("123 Test Street");
    }

    @Test
    void save_ShouldSavePointOfInterest_WhenValidDataProvided() {
        when(cityRepository.existsById(testCityId)).thenReturn(true);

        pointOfInterestService.save(testPointOfInterest);

        verify(pointOfInterestRepository, times(1)).save(testPointOfInterest);
    }

    @Test
    void save_ShouldThrowCityNotFoundException_WhenCityDoesNotExist() {
        when(cityRepository.existsById(testCityId)).thenReturn(false);

        CityNotFoundException exception = assertThrows(CityNotFoundException.class, () -> {
            pointOfInterestService.save(testPointOfInterest);
        });

        verify(pointOfInterestRepository, times(0)).save(any(PointOfInterest.class));
    }

    @Test
    void getAll_ShouldReturnListOfPointsOfInterest() {
        pointOfInterestService.getAll();

        verify(pointOfInterestRepository, times(1)).findAll();
    }

    @Test
    void findById_ShouldReturnPointOfInterest_WhenIdExists() {
        when(pointOfInterestRepository.findById(testPointOfInterestId))
            .thenReturn(java.util.Optional.of(testPointOfInterest));

        PointOfInterest result = pointOfInterestService.findById(testPointOfInterestId);

        assertNotNull(result);
        assertEquals(testPointOfInterestId,result.getId());
        verify(pointOfInterestRepository, times(1)).findById(testPointOfInterestId);
    }

    @Test
    void findById_ShouldThrowPointOfInterestNotFoundException_WhenIdDoesNotExist() {
        when(pointOfInterestRepository.findById(testPointOfInterestId)).thenReturn(java.util.Optional.empty());

        PointOfInterestNotFoundException exception = assertThrows(PointOfInterestNotFoundException.class, () -> {
            pointOfInterestService.findById(testPointOfInterestId);
        });

        assertEquals("PointOfInterest with id " + testPointOfInterestId + " not found!", exception.getMessage());
        verify(pointOfInterestRepository, times(1)).findById(testPointOfInterestId);
    }

    @Test
    void update_ShouldUpdatePointOfInterest_WhenValidDataProvided() {
        when(pointOfInterestRepository.existsById(testPointOfInterestId)).thenReturn(true);
        when(cityRepository.existsById(testCityId)).thenReturn(true);

        pointOfInterestService.update(testPointOfInterest);

        verify(pointOfInterestRepository, times(1)).save(testPointOfInterest);
    }

    @Test
    void update_ShouldThrowPointOfInterestNotFoundException_WhenPointOfInterestDoesNotExist() {
        when(pointOfInterestRepository.existsById(testPointOfInterestId)).thenReturn(false);
        when(cityRepository.existsById(testCityId)).thenReturn(true);
        PointOfInterestNotFoundException exception = assertThrows(PointOfInterestNotFoundException.class, () -> {
        pointOfInterestService.update(testPointOfInterest);
        });

        assertEquals("PointOfInterest with id " + testPointOfInterestId + " not found!", exception.getMessage());
        verify(pointOfInterestRepository, times(0)).save(any(PointOfInterest.class));
    }

    @Test
    void update_ShouldThrowCityNotFoundException_WhenCityDoesNotExist() {
        when(cityRepository.existsById(testCityId)).thenReturn(false);

        CityNotFoundException exception = assertThrows(CityNotFoundException.class, () -> {
        pointOfInterestService.update(testPointOfInterest);
        });

        assertEquals("City with id " + testCityId + " not found", exception.getMessage());
        verify(pointOfInterestRepository, times(0)).save(any(PointOfInterest.class));
    }

    @Test
    void deleteById_ShouldDeletePointOfInterest_WhenIdExists() {
        when(pointOfInterestRepository.existsById(testPointOfInterestId)).thenReturn(true);

        pointOfInterestService.deleteById(testPointOfInterestId);

        verify(pointOfInterestRepository, times(1)).deleteById(testPointOfInterestId);
    }

    @Test
    void  deleteById_ShouldThrowPointOfInterestNotFoundException_WhenIdDoesNotExist() {
        when(pointOfInterestRepository.existsById(testPointOfInterestId)).thenReturn(false);

        PointOfInterestNotFoundException exception = assertThrows(PointOfInterestNotFoundException.class, () -> {
            pointOfInterestService.deleteById(testPointOfInterestId);
        });

        assertEquals("PointOfInterest with id " + testPointOfInterestId + " not found!", exception.getMessage());
        verify(pointOfInterestRepository, times(0)).deleteById(any());
    }

}
