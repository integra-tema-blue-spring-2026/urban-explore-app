package cloudflight.integra.backend.controllers;

import cloudflight.integra.backend.controller.CityController;
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
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(CityController.class)
class CityControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private CityService cityService;

    @MockitoBean
    private CityMapper cityMapper;

    @MockitoBean
    private PointOfInterestMapper pointOfInterestMapper;

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
    }


    @Test
    void getAllCities_ShouldReturn200WithCityList() throws Exception {
        when(cityService.getAllCities()).thenReturn(List.of(testCity));
        when(cityMapper.toDto(testCity)).thenReturn(testCityDto);

        mockMvc.perform(get("/cities"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Cluj-Napoca"))
            .andExpect(jsonPath("$[0].country").value("Romania"));
    }

    @Test
    void getAllCities_ShouldReturn200WithEmptyList_WhenNoCitiesExist() throws Exception {
        when(cityService.getAllCities()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/cities"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }


    @Test
    void createCity_ShouldReturn200AndCreatedCity() throws Exception {
        CreateCityDto createDto = CreateCityDto.builder()
            .name("Cluj-Napoca")
            .country("Romania")
            .description("A great city in Transylvania.")
            .imageUrl("http://example.com/cluj.jpg")
            .population(100000)
            .build();

        when(cityMapper.toEntity(any(CreateCityDto.class))).thenReturn(testCity);
        when(cityService.createCity(any(City.class))).thenReturn(testCity);
        when(cityMapper.toDto(testCity)).thenReturn(testCityDto);

        mockMvc.perform(post("/cities")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.name").value("Cluj-Napoca"))
            .andExpect(jsonPath("$.country").value("Romania"));
    }


    @Test
    void updateCity_ShouldReturn200AndUpdatedCity() throws Exception {
        UpdateCityDto updateDto = UpdateCityDto.builder()
            .description("Updated description.")
            .build();

        CityDto updatedDto = CityDto.builder()
            .id(cityId)
            .name("Cluj-Napoca")
            .country("Romania")
            .description("Updated description.")
            .imageUrl("http://example.com/cluj.jpg")
            .build();

        when(cityMapper.toEntity(any(UpdateCityDto.class))).thenReturn(testCity);
        when(cityService.updateCity(eq(cityId), any(City.class))).thenReturn(testCity);
        when(cityMapper.toDto(testCity)).thenReturn(updatedDto);

        mockMvc.perform(put("/cities/{id}", cityId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.description").value("Updated description."));
    }

    @Test
    void updateCity_ShouldReturn404_WhenCityNotFound() throws Exception {
        UpdateCityDto updateDto = UpdateCityDto.builder().description("Updated.").build();

        when(cityMapper.toEntity(any(UpdateCityDto.class))).thenReturn(testCity);
        when(cityService.updateCity(eq(cityId), any(City.class)))
            .thenThrow(new CityNotFoundException("City not found with id: " + cityId));

        mockMvc.perform(put("/cities/{id}", cityId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    void updateCity_ShouldReturn400_WhenNoFieldsProvided() throws Exception {
        UpdateCityDto updateDto = UpdateCityDto.builder().build();

        when(cityMapper.toEntity(any(UpdateCityDto.class))).thenReturn(testCity);
        when(cityService.updateCity(eq(cityId), any(City.class)))
            .thenThrow(new UpdateCityException(
                "At least one field (description or imageUrl) must be provided for update."));

        mockMvc.perform(put("/cities/{id}", cityId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isBadRequest());
    }

    @Test
    void deleteCity_ShouldReturn200_WhenCityExists() throws Exception {
        doNothing().when(cityService).deleteCity(cityId);

        mockMvc.perform(delete("/cities/{id}", cityId))
            .andExpect(status().isOk());
    }

    @Test
    void deleteCity_ShouldReturn404_WhenCityNotFound() throws Exception {
        doThrow(new CityNotFoundException("City not found with id: " + cityId))
            .when(cityService).deleteCity(cityId);

        mockMvc.perform(delete("/cities/{id}", cityId))
            .andExpect(status().isNotFound());
    }


    @Test
    void getCitiesByName_ShouldReturn200WithMatchingCities() throws Exception {
        when(cityService.getCitiesByName("Cluj-Napoca")).thenReturn(List.of(testCity));
        when(cityMapper.toDto(testCity)).thenReturn(testCityDto);

        mockMvc.perform(get("/cities/{name}", "Cluj-Napoca"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].name").value("Cluj-Napoca"));
    }

    @Test
    void getCitiesByName_ShouldReturn404_WhenNameNotFound() throws Exception {
        when(cityService.getCitiesByName("Unknown"))
            .thenThrow(new CityNotFoundException("No cities found with name: Unknown"));

        mockMvc.perform(get("/cities/{name}", "Unknown"))
            .andExpect(status().isNotFound());
    }


    @Test
    void getPoisFromCity_ShouldReturn200WithPoisList() throws Exception {
        testCity.setPointOfInterests(Collections.emptyList());
        when(cityService.getCitiesByName("Cluj-Napoca")).thenReturn(List.of(testCity));

        mockMvc.perform(get("/cities/{name}/pois", "Cluj-Napoca"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void getPoisFromCity_ShouldReturn404_WhenCityNotFound() throws Exception {
        when(cityService.getCitiesByName("Unknown"))
            .thenThrow(new CityNotFoundException("No cities found with name: Unknown"));

        mockMvc.perform(get("/cities/{name}/pois", "Unknown"))
            .andExpect(status().isNotFound());
    }
}
