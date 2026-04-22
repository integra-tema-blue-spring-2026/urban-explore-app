package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.CityNotFoundException;
import cloudflight.integra.backend.exceptions.custom.UpdateCityException;
import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.repository.CityRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class CityServiceUnitTest {

    @Mock
    private CityRepository cityRepository;

    @InjectMocks
    private CityService cityService;

    private City testCity;
    private UUID testCityId;

    @BeforeEach
    void setUp() {
        testCityId = UUID.randomUUID();
        testCity = City.builder()
            .id(testCityId)
            .name("Cluj-Napoca")
            .country("Romania")
            .description("A great city in Transylvania.")
            .imageUrl("http://example.com/cluj.jpg")
            .build();
    }

    @Test
    void getAllCities_ShouldReturnAllCities() {
        when(cityRepository.findAll()).thenReturn(List.of(testCity));

        List<City> result = cityService.getAllCities();

        assertEquals(1, result.size());
        verify(cityRepository, times(1)).findAll();
    }

    @Test
    void createCity_ShouldReturnSavedCity() {
        when(cityRepository.save(any(City.class))).thenReturn(testCity);

        City savedCity = cityService.createCity(testCity);

        assertNotNull(savedCity);
        assertEquals("Cluj-Napoca", savedCity.getName());
        verify(cityRepository, times(1)).save(testCity);
    }


    @Test
    void getCitiesByName_ShouldReturnListOfCities_WhenNameExists() {
        when(cityRepository.findByName("Cluj-Napoca")).thenReturn(List.of(testCity));

        List<City> cities = cityService.getCitiesByName("Cluj-Napoca");

        assertFalse(cities.isEmpty());
        assertEquals(1, cities.size());
        assertEquals("Cluj-Napoca", cities.getFirst().getName());
    }

    @Test
    void getCitiesByName_ShouldThrowException_WhenNameDoesntExist() {
        when(cityRepository.findByName("NonExistentCity")).thenReturn(Collections.emptyList());

        CityNotFoundException exception = assertThrows(CityNotFoundException.class, () -> {
            cityService.getCitiesByName("NonExistentCity");
        });

        assertEquals("No cities found with name: NonExistentCity", exception.getMessage());
        verify(cityRepository, times(1)).findByName("NonExistentCity");
    }

    @Test
    void updateCity_ShouldUpdateDescription_WhenOnlyDescriptionProvided() {
        City inputCity = City.builder().description("Updated Description").build();
        when(cityRepository.findById(testCityId)).thenReturn(Optional.of(testCity));
        when(cityRepository.save(any(City.class))).thenAnswer(inv -> inv.getArgument(0));

        City updatedCity = cityService.updateCity(testCityId, inputCity);

        assertEquals("Updated Description", updatedCity.getDescription());
        assertEquals("http://example.com/cluj.jpg", updatedCity.getImageUrl());

        ArgumentCaptor<City> captor = ArgumentCaptor.forClass(City.class);
        verify(cityRepository, times(1)).save(captor.capture());
        assertEquals("Updated Description", captor.getValue().getDescription());
        assertEquals("http://example.com/cluj.jpg", captor.getValue().getImageUrl());
    }

    @Test
    void updateCity_ShouldUpdateImageUrl_WhenOnlyImageUrlProvided() {
        City inputCity = City.builder().imageUrl("http://example.com/new.jpg").build();
        when(cityRepository.findById(testCityId)).thenReturn(Optional.of(testCity));
        when(cityRepository.save(any(City.class))).thenAnswer(inv -> inv.getArgument(0));

        City updatedCity = cityService.updateCity(testCityId, inputCity);

        assertEquals("http://example.com/new.jpg", updatedCity.getImageUrl());
        assertEquals("A great city in Transylvania.", updatedCity.getDescription());
    }

    @Test
    void updateCity_ShouldThrowException_WhenNoFieldsProvided() {
        City inputCity = City.builder().build();

        UpdateCityException exception = assertThrows(UpdateCityException.class, () ->
            cityService.updateCity(testCityId, inputCity));

        assertEquals("At least one field (description or imageUrl) must be provided for update.",
            exception.getMessage());
        verify(cityRepository, never()).findById(any());
        verify(cityRepository, never()).save(any(City.class));
    }

    @Test
    void updateCity_ShouldThrowException_WhenCityNotFound() {
        City inputCity = City.builder().description("Updated Description").build();
        when(cityRepository.findById(testCityId)).thenReturn(Optional.empty());

        CityNotFoundException exception = assertThrows(CityNotFoundException.class, () ->
            cityService.updateCity(testCityId, inputCity));

        assertEquals("City not found with id: " + testCityId, exception.getMessage());
        verify(cityRepository, times(1)).findById(testCityId);
        verify(cityRepository, never()).save(any(City.class));
    }

    @Test
    void deleteCity_ShouldDeleteCity_WhenCityExists() {
        when(cityRepository.existsById(testCityId)).thenReturn(true);

        cityService.deleteCity(testCityId);

        verify(cityRepository, times(1)).existsById(testCityId);
        verify(cityRepository, times(1)).deleteById(testCityId);
    }

    @Test
    void deleteCity_ShouldThrowException_WhenCityDoesntExist() {
        when(cityRepository.existsById(testCityId)).thenReturn(false);

        assertThrows(CityNotFoundException.class, () -> cityService.deleteCity(testCityId));

        verify(cityRepository, times(1)).existsById(testCityId);
        verify(cityRepository, never()).deleteById(testCityId);
    }
}
