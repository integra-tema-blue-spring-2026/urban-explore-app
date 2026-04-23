package cloudflight.integra.backend.controllers;

import cloudflight.integra.backend.exceptions.custom.CityNotFoundException;
import cloudflight.integra.backend.exceptions.custom.UpdateCityException;
import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.dtos.city.CityDto;
import cloudflight.integra.backend.model.dtos.city.CreateCityDto;
import cloudflight.integra.backend.model.dtos.city.UpdateCityDto;
import cloudflight.integra.backend.model.utils.enums.CityStatus;
import cloudflight.integra.backend.model.utils.mappers.CityMapper;
import cloudflight.integra.backend.model.utils.mappers.PointOfInterestMapper;
import cloudflight.integra.backend.service.CityService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CityControllerIntegrationTest extends BaseControllerIntegrationTest {

    @MockitoBean
    private CityService cityService;

    @MockitoBean
    private CityMapper cityMapper;

    @MockitoBean
    private PointOfInterestMapper pointOfInterestMapper;

    private String token;
    private UUID cityId;
    private City testCity;
    private CityDto testCityDto;

    @BeforeEach
    void setUp() {
        cityId = UUID.randomUUID();

        testCity = City.builder()
            .id(cityId)
            .name("Cluj-Napoca")
            .country("Romania")
            .description("A great city in Transylvania.")
            .imageUrl("http://example.com/cluj.jpg")
            .status(CityStatus.APPROVED)
            .build();

        testCityDto = CityDto.builder()
            .id(cityId)
            .name("Cluj-Napoca")
            .country("Romania")
            .description("A great city in Transylvania.")
            .imageUrl("http://example.com/cluj.jpg")
            .status(CityStatus.APPROVED)
            .build();

        token = setupAuthAndGetToken();
    }


    @Test
    void getAllCities_ShouldReturn200WithCityList() {
        when(cityService.getAllCities()).thenReturn(List.of(testCity));
        when(cityMapper.toDto(testCity)).thenReturn(testCityDto);

        ResponseEntity<List> response = restTemplate.exchange(
            "/cities", HttpMethod.GET, authEntity(token), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getAllCities_ShouldReturn200WithEmptyList_WhenNoCitiesExist() {
        when(cityService.getAllCities()).thenReturn(Collections.emptyList());

        ResponseEntity<List> response = restTemplate.exchange(
            "/cities", HttpMethod.GET, authEntity(token), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getAllCities_ShouldReturn401_WhenNoTokenProvided() {
        ResponseEntity<String> response = restTemplate.getForEntity("/cities", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    void createCity_ShouldReturn200AndCreatedCity() {
        CreateCityDto createDto = CreateCityDto.builder()
            .name("Cluj-Napoca").country("Romania")
            .description("A great city in Transylvania.")
            .imageUrl("http://example.com/cluj.jpg")
            .population(100000)
            .build();

        when(cityMapper.toEntity(any(CreateCityDto.class))).thenReturn(testCity);
        when(cityService.createCity(any(City.class))).thenReturn(testCity);
        when(cityMapper.toDto(testCity)).thenReturn(testCityDto);

        ResponseEntity<CityDto> response = restTemplate.exchange(
            "/cities", HttpMethod.POST, authEntity(createDto, token), CityDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getName()).isEqualTo("Cluj-Napoca");
        assertThat(response.getBody().getCountry()).isEqualTo("Romania");
    }

    @Test
    void createCity_ShouldReturn401_WhenNoTokenProvided() {
        CreateCityDto createDto = CreateCityDto.builder()
            .name("Cluj-Napoca").country("Romania").build();

        ResponseEntity<String> response = restTemplate.postForEntity(
            "/cities", createDto, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    void updateCity_ShouldReturn200AndUpdatedCity() {
        UpdateCityDto updateDto = UpdateCityDto.builder()
            .description("Updated description.").build();

        CityDto updatedDto = CityDto.builder()
            .id(cityId).name("Cluj-Napoca").country("Romania")
            .description("Updated description.")
            .imageUrl("http://example.com/cluj.jpg")
            .build();

        when(cityMapper.toEntity(any(UpdateCityDto.class))).thenReturn(testCity);
        when(cityService.updateCity(eq(cityId), any(City.class))).thenReturn(testCity);
        when(cityMapper.toDto(testCity)).thenReturn(updatedDto);

        ResponseEntity<CityDto> response = restTemplate.exchange(
            "/cities/" + cityId, HttpMethod.PUT, authEntity(updateDto, token), CityDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getDescription()).isEqualTo("Updated description.");
    }

    @Test
    void updateCity_ShouldReturn404_WhenCityNotFound() {
        UpdateCityDto updateDto = UpdateCityDto.builder().description("Updated.").build();

        when(cityMapper.toEntity(any(UpdateCityDto.class))).thenReturn(testCity);
        when(cityService.updateCity(eq(cityId), any(City.class)))
            .thenThrow(new CityNotFoundException("City not found with id: " + cityId));

        ResponseEntity<String> response = restTemplate.exchange(
            "/cities/" + cityId, HttpMethod.PUT, authEntity(updateDto, token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void updateCity_ShouldReturn400_WhenNoFieldsProvided() {
        UpdateCityDto updateDto = UpdateCityDto.builder().build();

        when(cityMapper.toEntity(any(UpdateCityDto.class))).thenReturn(testCity);
        when(cityService.updateCity(eq(cityId), any(City.class)))
            .thenThrow(new UpdateCityException(
                "At least one field (description or imageUrl) must be provided for update."));

        ResponseEntity<String> response = restTemplate.exchange(
            "/cities/" + cityId, HttpMethod.PUT, authEntity(updateDto, token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }


    @Test
    void deleteCity_ShouldReturn200_WhenCityExists() {
        doNothing().when(cityService).deleteCity(cityId);

        ResponseEntity<Void> response = restTemplate.exchange(
            "/cities/" + cityId, HttpMethod.DELETE, authEntity(token), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        verify(cityService, times(1)).deleteCity(cityId);
    }

    @Test
    void deleteCity_ShouldReturn404_WhenCityNotFound() {
        doThrow(new CityNotFoundException("City not found with id: " + cityId))
            .when(cityService).deleteCity(cityId);

        ResponseEntity<String> response = restTemplate.exchange(
            "/cities/" + cityId, HttpMethod.DELETE, authEntity(token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    void getCitiesByName_ShouldReturn200WithMatchingCities() {
        when(cityService.getCitiesByName("Cluj-Napoca")).thenReturn(List.of(testCity));
        when(cityMapper.toDto(testCity)).thenReturn(testCityDto);

        ResponseEntity<List> response = restTemplate.exchange(
            "/cities/Cluj-Napoca", HttpMethod.GET, authEntity(token), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getCitiesByName_ShouldReturn404_WhenNameNotFound() {
        when(cityService.getCitiesByName("Unknown"))
            .thenThrow(new CityNotFoundException("No cities found with name: Unknown"));

        ResponseEntity<String> response = restTemplate.exchange(
            "/cities/Unknown", HttpMethod.GET, authEntity(token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    void getPoisFromCity_ShouldReturn200WithEmptyPoisList() {
        testCity.setPointOfInterests(Collections.emptyList());
        when(cityService.getCitiesByName("Cluj-Napoca")).thenReturn(List.of(testCity));

        ResponseEntity<List> response = restTemplate.exchange(
            "/cities/Cluj-Napoca/pois", HttpMethod.GET, authEntity(token), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getPoisFromCity_ShouldReturn404_WhenCityNotFound() {
        when(cityService.getCitiesByName("Unknown"))
            .thenThrow(new CityNotFoundException("No cities found with name: Unknown"));

        ResponseEntity<String> response = restTemplate.exchange(
            "/cities/Unknown/pois", HttpMethod.GET, authEntity(token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
