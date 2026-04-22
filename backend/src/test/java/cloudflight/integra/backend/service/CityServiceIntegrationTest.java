package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.CityNotFoundException;
import cloudflight.integra.backend.model.City;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;


@DataJpaTest
@Import(CityService.class)
public class CityServiceIntegrationTest {

    @Autowired
    private CityService cityService;



    @Test
    void createCity_ShouldPersistCity() {
        City city = City.builder()
            .name("Cluj-Napoca")
            .country("Romania")
            .description("A great city in Transylvania.")
            .imageUrl("http://example.com/cluj.jpg")
            .build();

        City savedCity = cityService.createCity(city);

        Assertions.assertNotNull(savedCity.getId());
        Assertions.assertEquals("Cluj-Napoca", savedCity.getName());
        Assertions.assertEquals(1, cityService.getAllCities().size());
    }

    @Test
    void updateCity_ShouldUpdateDescription_AndPreserveImageUrl() {
        City city = City.builder()
            .name("Cluj-Napoca")
            .country("Romania")
            .description("A great city in Transylvania.")
            .imageUrl("http://example.com/cluj.jpg")
            .build();

        City savedCity = cityService.createCity(city);

        City updateInfo = City.builder()
            .description("An updated description.")
            .build();

        City updatedCity = cityService.updateCity(savedCity.getId(), updateInfo);

        Assertions.assertNotNull(updatedCity);
        Assertions.assertEquals("An updated description.", updatedCity.getDescription());
        Assertions.assertEquals("http://example.com/cluj.jpg", updatedCity.getImageUrl());
    }

    @Test
    void updateCity_ShouldUpdateImageUrl_AndPreserveDescription() {
        City city = City.builder()
            .name("Cluj-Napoca")
            .country("Romania")
            .description("A great city in Transylvania.")
            .imageUrl("http://example.com/cluj.jpg")
            .build();

        City savedCity = cityService.createCity(city);

        City updateInfo = City.builder()
            .imageUrl("http://example.com/new.jpg")
            .build();

        City updatedCity = cityService.updateCity(savedCity.getId(), updateInfo);

        Assertions.assertNotNull(updatedCity);
        Assertions.assertEquals("http://example.com/new.jpg", updatedCity.getImageUrl());
        Assertions.assertEquals("A great city in Transylvania.", updatedCity.getDescription());
    }

    @Test
    void deleteCity_ShouldDeleteCity() {
        City city = City.builder()
            .name("Cluj-Napoca")
            .country("Romania")
            .description("A great city in Transylvania.")
            .imageUrl("http://example.com/cluj.jpg")
            .build();

        City savedCity = cityService.createCity(city);
        cityService.deleteCity(savedCity.getId());

        Assertions.assertTrue(cityService.getAllCities().isEmpty());
    }

    @Test
    void deleteCity_ShouldThrowException_WhenCityDoesntExist() {
        City city = City.builder()
            .name("Cluj-Napoca")
            .country("Romania")
            .description("A great city in Transylvania.")
            .imageUrl("http://example.com/cluj.jpg")
            .build();

        City savedCity = cityService.createCity(city);
        cityService.deleteCity(savedCity.getId());

        Assertions.assertThrows(CityNotFoundException.class,
            () -> cityService.deleteCity(savedCity.getId()));
    }

    @Test
    void getCitiesByName_ShouldReturnCitiesWithGivenName() {
        City city1 = City.builder()
            .name("Cluj-Napoca")
            .country("Romania")
            .description("A great city in Transylvania.")
            .imageUrl("http://example.com/cluj.jpg")
            .build();

        City city2 = City.builder()
            .name("Cluj-Napoca")
            .country("Romania")
            .description("Another great city in Transylvania.")
            .imageUrl("http://example.com/cluj2.jpg")
            .build();

        cityService.createCity(city1);
        cityService.createCity(city2);

        var cities = cityService.getCitiesByName("Cluj-Napoca");
        Assertions.assertEquals(2, cities.size());
    }

    @Test
    void getCitiesByName_ShouldThrowException_WhenNameDoesntExist() {
        Assertions.assertThrows(CityNotFoundException.class,
            () -> cityService.getCitiesByName("NonExistentCity"));
    }
}
