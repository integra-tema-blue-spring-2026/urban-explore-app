package cloudflight.integra.backend.controllers;

import cloudflight.integra.backend.exceptions.custom.CityNotFoundException;
import cloudflight.integra.backend.exceptions.custom.PointOfInterestNotFoundException;
import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.dtos.poi.PointOfInterestRequestDto;
import cloudflight.integra.backend.model.dtos.poi.PointOfInterestResponseDto;
import cloudflight.integra.backend.model.utils.enums.PointOfInterestType;
import cloudflight.integra.backend.model.utils.mappers.PointOfInterestMapper;
import cloudflight.integra.backend.service.PointOfInterestService;
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
import static org.mockito.Mockito.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class PointOfInterestControllerIntegrationTest extends BaseControllerIntegrationTest {

    @MockitoBean
    private PointOfInterestService pointOfInterestService;

    @MockitoBean
    private PointOfInterestMapper pointOfInterestMapper;

    private String token;
    private UUID poiId;
    private UUID cityId;
    private PointOfInterest testPoi;
    private PointOfInterestResponseDto testPoiResponseDto;
    private PointOfInterestRequestDto testPoiRequestDto;

    @BeforeEach
    void setUp() {
        poiId = UUID.randomUUID();
        cityId = UUID.randomUUID();

        City testCity = City.builder()
            .id(cityId).name("Cluj-Napoca").country("Romania").build();

        testPoi = new PointOfInterest();
        testPoi.setId(poiId);
        testPoi.setName("Central Park");
        testPoi.setDescription("A nice park.");
        testPoi.setAddress("123 Main St");
        testPoi.setType(PointOfInterestType.PARK);
        testPoi.setCity(testCity);

        testPoiResponseDto = new PointOfInterestResponseDto(
            poiId, "Central Park", "A nice park.", "123 Main St", PointOfInterestType.PARK, cityId);

        testPoiRequestDto = new PointOfInterestRequestDto(
            "Central Park", "A nice park.", "123 Main St", PointOfInterestType.PARK, cityId);

        token = setupAuthAndGetToken();
    }


    @Test
    void savePointOfInterest_ShouldReturn201AndCreatedPoi() {
        when(pointOfInterestMapper.toEntity(any(PointOfInterestRequestDto.class))).thenReturn(testPoi);
        when(pointOfInterestService.save(any(PointOfInterest.class))).thenReturn(testPoi);
        when(pointOfInterestMapper.toDto(testPoi)).thenReturn(testPoiResponseDto);

        ResponseEntity<PointOfInterestResponseDto> response = restTemplate.exchange(
            "/pois", HttpMethod.POST,
            authEntity(testPoiRequestDto, token), PointOfInterestResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().name()).isEqualTo("Central Park");
        assertThat(response.getBody().type()).isEqualTo(PointOfInterestType.PARK);
    }

    @Test
    void savePointOfInterest_ShouldReturn404_WhenCityNotFound() {
        when(pointOfInterestMapper.toEntity(any(PointOfInterestRequestDto.class))).thenReturn(testPoi);
        when(pointOfInterestService.save(any(PointOfInterest.class)))
            .thenThrow(new CityNotFoundException("City with id " + cityId + " not found"));

        ResponseEntity<String> response = restTemplate.exchange(
            "/pois", HttpMethod.POST, authEntity(testPoiRequestDto, token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void savePointOfInterest_ShouldReturn401_WhenNoTokenProvided() {
        ResponseEntity<String> response = restTemplate.postForEntity(
            "/pois", testPoiRequestDto, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    void getAll_ShouldReturn200WithPoiList() {
        when(pointOfInterestService.getAll()).thenReturn(List.of(testPoi));
        when(pointOfInterestMapper.toDto(testPoi)).thenReturn(testPoiResponseDto);

        ResponseEntity<List> response = restTemplate.exchange(
            "/pois", HttpMethod.GET, authEntity(token), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getAll_ShouldReturn200WithEmptyList_WhenNoPoisExist() {
        when(pointOfInterestService.getAll()).thenReturn(Collections.emptyList());

        ResponseEntity<List> response = restTemplate.exchange(
            "/pois", HttpMethod.GET, authEntity(token), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getAll_ShouldReturn401_WhenNoTokenProvided() {
        ResponseEntity<String> response = restTemplate.getForEntity("/pois", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    void findById_ShouldReturn200WithPoi() {
        when(pointOfInterestService.findById(poiId)).thenReturn(testPoi);
        when(pointOfInterestMapper.toDto(testPoi)).thenReturn(testPoiResponseDto);

        ResponseEntity<PointOfInterestResponseDto> response = restTemplate.exchange(
            "/pois/" + poiId, HttpMethod.GET, authEntity(token), PointOfInterestResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().name()).isEqualTo("Central Park");
        assertThat(response.getBody().description()).isEqualTo("A nice park.");
    }

    @Test
    void findById_ShouldReturn404_WhenPoiNotFound() {
        when(pointOfInterestService.findById(poiId))
            .thenThrow(new PointOfInterestNotFoundException(
                "PointOfInterest with id " + poiId + " not found!"));

        ResponseEntity<String> response = restTemplate.exchange(
            "/pois/" + poiId, HttpMethod.GET, authEntity(token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    void update_ShouldReturn200AndUpdatedPoi() {
        PointOfInterestRequestDto updateDto = new PointOfInterestRequestDto(
            "Updated Park", "Updated description.", "456 New St", PointOfInterestType.PARK, cityId);

        PointOfInterestResponseDto updatedDto = new PointOfInterestResponseDto(
            poiId, "Updated Park", "Updated description.", "456 New St", PointOfInterestType.PARK, cityId);

        when(pointOfInterestMapper.toEntity(any(PointOfInterestRequestDto.class))).thenReturn(testPoi);
        when(pointOfInterestService.update(any(PointOfInterest.class))).thenReturn(testPoi);
        when(pointOfInterestMapper.toDto(testPoi)).thenReturn(updatedDto);

        ResponseEntity<PointOfInterestResponseDto> response = restTemplate.exchange(
            "/pois/" + poiId, HttpMethod.PUT,
            authEntity(updateDto, token), PointOfInterestResponseDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().name()).isEqualTo("Updated Park");
        assertThat(response.getBody().description()).isEqualTo("Updated description.");
    }

    @Test
    void update_ShouldReturn404_WhenPoiNotFound() {
        when(pointOfInterestMapper.toEntity(any(PointOfInterestRequestDto.class))).thenReturn(testPoi);
        when(pointOfInterestService.update(any(PointOfInterest.class)))
            .thenThrow(new PointOfInterestNotFoundException(
                "PointOfInterest with id " + poiId + " not found!"));

        ResponseEntity<String> response = restTemplate.exchange(
            "/pois/" + poiId, HttpMethod.PUT, authEntity(testPoiRequestDto, token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }

    @Test
    void update_ShouldReturn404_WhenCityNotFound() {
        when(pointOfInterestMapper.toEntity(any(PointOfInterestRequestDto.class))).thenReturn(testPoi);
        when(pointOfInterestService.update(any(PointOfInterest.class)))
            .thenThrow(new CityNotFoundException("City with id " + cityId + " not found"));

        ResponseEntity<String> response = restTemplate.exchange(
            "/pois/" + poiId, HttpMethod.PUT, authEntity(testPoiRequestDto, token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    void deleteById_ShouldReturn204_WhenPoiExists() {
        doNothing().when(pointOfInterestService).deleteById(poiId);

        ResponseEntity<Void> response = restTemplate.exchange(
            "/pois/" + poiId, HttpMethod.DELETE, authEntity(token), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(pointOfInterestService, times(1)).deleteById(poiId);
    }

    @Test
    void deleteById_ShouldReturn404_WhenPoiNotFound() {
        doThrow(new PointOfInterestNotFoundException(
            "PointOfInterest with id " + poiId + " not found!"))
            .when(pointOfInterestService).deleteById(poiId);

        ResponseEntity<String> response = restTemplate.exchange(
            "/pois/" + poiId, HttpMethod.DELETE, authEntity(token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
