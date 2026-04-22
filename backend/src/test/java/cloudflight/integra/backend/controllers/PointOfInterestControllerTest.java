package cloudflight.integra.backend.controllers;

import cloudflight.integra.backend.controller.PointOfInterestController;
import cloudflight.integra.backend.exceptions.custom.CityNotFoundException;
import cloudflight.integra.backend.exceptions.custom.PointOfInterestNotFoundException;
import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.dtos.poi.PointOfInterestRequestDto;
import cloudflight.integra.backend.model.dtos.poi.PointOfInterestResponseDto;
import cloudflight.integra.backend.model.utils.enums.PointOfInterestType;
import cloudflight.integra.backend.model.utils.mappers.PointOfInterestMapper;
import cloudflight.integra.backend.service.PointOfInterestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(PointOfInterestController.class)
class PointOfInterestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PointOfInterestService pointOfInterestService;

    @MockitoBean
    private PointOfInterestMapper pointOfInterestMapper;

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
            .id(cityId)
            .name("Cluj-Napoca")
            .country("Romania")
            .build();

        testPoi = new PointOfInterest();
        testPoi.setId(poiId);
        testPoi.setName("Central Park");
        testPoi.setDescription("A nice park.");
        testPoi.setAddress("123 Main St");
        testPoi.setType(PointOfInterestType.PARK);
        testPoi.setCity(testCity);

        testPoiResponseDto = new PointOfInterestResponseDto(
            poiId,
            "Central Park",
            "A nice park.",
            "123 Main St",
            PointOfInterestType.PARK,
            cityId);

        testPoiRequestDto = new PointOfInterestRequestDto(
            "Central Park",
            "A nice park.",
            "123 Main St",
            PointOfInterestType.PARK,
            cityId);
    }

    @Test
    void savePointOfInterest_ShouldReturn201AndCreatedPoi() throws Exception {
        when(pointOfInterestMapper.toEntity(any(PointOfInterestRequestDto.class))).thenReturn(testPoi);
        when(pointOfInterestService.save(any(PointOfInterest.class))).thenReturn(testPoi);
        when(pointOfInterestMapper.toDto(testPoi)).thenReturn(testPoiResponseDto);

        mockMvc.perform(post("/pois")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPoiRequestDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.name").value("Central Park"))
            .andExpect(jsonPath("$.type").value("PARK"));
    }

    @Test
    void savePointOfInterest_ShouldReturn404_WhenCityNotFound() throws Exception {
        when(pointOfInterestMapper.toEntity(any(PointOfInterestRequestDto.class))).thenReturn(testPoi);
        when(pointOfInterestService.save(any(PointOfInterest.class)))
            .thenThrow(new CityNotFoundException("City with id " + cityId + " not found"));

        mockMvc.perform(post("/pois")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPoiRequestDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    void getAll_ShouldReturn200WithPoiList() throws Exception {
        when(pointOfInterestService.getAll()).thenReturn(List.of(testPoi));
        when(pointOfInterestMapper.toDto(testPoi)).thenReturn(testPoiResponseDto);

        mockMvc.perform(get("/pois"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Central Park"))
            .andExpect(jsonPath("$[0].address").value("123 Main St"));
    }

    @Test
    void getAll_ShouldReturn200WithEmptyList_WhenNoPoisExist() throws Exception {
        when(pointOfInterestService.getAll()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/pois"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void findById_ShouldReturn200WithPoi() throws Exception {
        when(pointOfInterestService.findById(poiId)).thenReturn(testPoi);
        when(pointOfInterestMapper.toDto(testPoi)).thenReturn(testPoiResponseDto);

        mockMvc.perform(get("/pois/{id}", poiId))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Central Park"))
            .andExpect(jsonPath("$.description").value("A nice park."));
    }

    @Test
    void findById_ShouldReturn404_WhenPoiNotFound() throws Exception {
        when(pointOfInterestService.findById(poiId))
            .thenThrow(new PointOfInterestNotFoundException("PointOfInterest with id " + poiId + " not found!"));

        mockMvc.perform(get("/pois/{id}", poiId))
            .andExpect(status().isNotFound());
    }


    @Test
    void update_ShouldReturn200AndUpdatedPoi() throws Exception {
        PointOfInterestRequestDto updateDto = new PointOfInterestRequestDto(
            "Updated Park",
            "Updated description.",
            "456 New St",
            PointOfInterestType.PARK,
            cityId);

        PointOfInterestResponseDto updatedResponseDto = new PointOfInterestResponseDto(
            poiId,
            "Updated Park",
            "Updated description.",
            "456 New St",
            PointOfInterestType.PARK,
            cityId);

        when(pointOfInterestMapper.toEntity(any(PointOfInterestRequestDto.class))).thenReturn(testPoi);
        when(pointOfInterestService.update(any(PointOfInterest.class))).thenReturn(testPoi);
        when(pointOfInterestMapper.toDto(testPoi)).thenReturn(updatedResponseDto);

        mockMvc.perform(put("/pois/{id}", poiId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Updated Park"))
            .andExpect(jsonPath("$.description").value("Updated description."));
    }

    @Test
    void update_ShouldReturn404_WhenPoiNotFound() throws Exception {
        when(pointOfInterestMapper.toEntity(any(PointOfInterestRequestDto.class))).thenReturn(testPoi);
        when(pointOfInterestService.update(any(PointOfInterest.class)))
            .thenThrow(new PointOfInterestNotFoundException("PointOfInterest with id " + poiId + " not found!"));

        mockMvc.perform(put("/pois/{id}", poiId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPoiRequestDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    void update_ShouldReturn404_WhenCityNotFound() throws Exception {
        when(pointOfInterestMapper.toEntity(any(PointOfInterestRequestDto.class))).thenReturn(testPoi);
        when(pointOfInterestService.update(any(PointOfInterest.class)))
            .thenThrow(new CityNotFoundException("City with id " + cityId + " not found"));

        mockMvc.perform(put("/pois/{id}", poiId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(testPoiRequestDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteById_ShouldReturn204_WhenPoiExists() throws Exception {
        doNothing().when(pointOfInterestService).deleteById(poiId);

        mockMvc.perform(delete("/pois/{id}", poiId))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteById_ShouldReturn404_WhenPoiNotFound() throws Exception {
        doThrow(new PointOfInterestNotFoundException("PointOfInterest with id " + poiId + " not found!"))
            .when(pointOfInterestService).deleteById(poiId);

        mockMvc.perform(delete("/pois/{id}", poiId))
            .andExpect(status().isNotFound());
    }
}
