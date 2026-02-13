package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.exception.PointOfInterestNotFoundException;
import cloudflight.integra.backend.model.PointOfInterestType;
import cloudflight.integra.backend.model.dtos.PointOfInterestCreateDto;
import cloudflight.integra.backend.model.dtos.PointOfInterestDto;
import cloudflight.integra.backend.service.PointOfInterestService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.Arrays;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PointOfInterestController.class)
public class PointOfInterestControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private PointOfInterestService pointOfInterestService;

    @Test
    public void POST_PointOfInterest_OK() throws Exception {
        PointOfInterestCreateDto dto = new PointOfInterestCreateDto(
            "Museum A",
            "A Museum",
            "Str. A",
            PointOfInterestType.MUSEUM,
            1L
        );

        PointOfInterestDto responseDto = new PointOfInterestDto(
            1L,
            "Museum A",
            "A Museum",
            "Str. A",
            PointOfInterestType.MUSEUM,
            1L
        );

        when(pointOfInterestService.save(any(PointOfInterestCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(post("/pois")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                        )
                        .andExpect(status().isCreated())
                        .andExpect(jsonPath("$.id").exists())
                        .andExpect(jsonPath("$.name").value("Museum A"))
                        .andExpect(jsonPath("$.description").value("A Museum"))
                        .andExpect(jsonPath("$.address").value("Str. A"))
                        .andExpect(jsonPath("$.type").value(PointOfInterestType.MUSEUM.name()))
                        .andExpect(jsonPath("$.cityId").value(1L));
    }

    @Test
    public void POST_PointOfInterest_ERROR() throws Exception {
        PointOfInterestCreateDto dto = new PointOfInterestCreateDto(
            "",
            "",
            "",
            null,
            null
        );

        mockMvc.perform(
                        post("/pois")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                        )
                        .andExpect(status().isBadRequest());
    }

    @Test
    public void GET_all_PointOfInterest_OK() throws Exception {
        PointOfInterestDto dto1 = new PointOfInterestDto(
            1L,
            "Museum A",
            "A Museum",
            "Str. A",
            PointOfInterestType.MUSEUM,
            1L
        );

        PointOfInterestDto dto2 = new PointOfInterestDto(
            2L,
            "Museum B",
            "A Museum",
            "Str. B",
            PointOfInterestType.MUSEUM,
            1L
        );

        when(pointOfInterestService.getAll()).thenReturn(Arrays.asList(dto1, dto2));

        mockMvc.perform(
                        get("/pois")
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()").value(2))
                        .andExpect(jsonPath("$[0].id").value(1))
                        .andExpect(jsonPath("$[0].name").value("Museum A"))
                        .andExpect(jsonPath("$[0].description").value("A Museum"))
                        .andExpect(jsonPath("$[0].address").value("Str. A"))
                        .andExpect(jsonPath("$[1].id").value(2))
                        .andExpect(jsonPath("$[1].name").value("Museum B"))
                        .andExpect(jsonPath("$[1].description").value("A Museum"))
                        .andExpect(jsonPath("$[1].address").value("Str. B"));
    }

    @Test
    public void GET_PointOfInterest_by_id_OK() throws Exception {
        PointOfInterestDto responseDto = new PointOfInterestDto(
            1L,
            "Museum A",
            "A Museum",
            "Str. A",
            PointOfInterestType.MUSEUM,
            1L
        );

        when(pointOfInterestService.findById(1L)).thenReturn(responseDto);

        mockMvc.perform(
                        get("/pois/1")
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.name").value("Museum A"))
                        .andExpect(jsonPath("$.description").value("A Museum"))
                        .andExpect(jsonPath("$.address").value("Str. A"))
                        .andExpect(jsonPath("$.type").value(PointOfInterestType.MUSEUM.name()))
                        .andExpect(jsonPath("$.cityId").value(1L));
    }

    @Test
    public void GET_PointOfInterest_by_id_ERROR() throws Exception {
        when(pointOfInterestService.findById(999L))
            .thenThrow(new PointOfInterestNotFoundException("PointOfInterest with id 999 not found!"));

        mockMvc.perform(
                        get("/pois/999")
                        )
                        .andExpect(status().isNotFound());
    }

    @Test
    public void PUT_PointOfInterest_OK() throws Exception {
        PointOfInterestCreateDto dto = new PointOfInterestCreateDto(
            "Museum Updated",
            "Updated Museum",
            "Str. Updated",
            PointOfInterestType.MUSEUM,
            1L
        );

        PointOfInterestDto responseDto = new PointOfInterestDto(
            1L,
            "Museum Updated",
            "Updated Museum",
            "Str. Updated",
            PointOfInterestType.MUSEUM,
            1L
        );

        when(pointOfInterestService.update(eq(1L), any(PointOfInterestCreateDto.class))).thenReturn(responseDto);

        mockMvc.perform(put("/pois/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                        )
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.id").value(1))
                        .andExpect(jsonPath("$.name").value("Museum Updated"))
                        .andExpect(jsonPath("$.description").value("Updated Museum"))
                        .andExpect(jsonPath("$.address").value("Str. Updated"))
                        .andExpect(jsonPath("$.type").value(PointOfInterestType.MUSEUM.name()))
                        .andExpect(jsonPath("$.cityId").value(1L));
    }

    @Test
    public void PUT_PointOfInterest_VALIDATION_ERROR() throws Exception {
        PointOfInterestCreateDto dto = new PointOfInterestCreateDto(
            "",
            "",
            "",
            null,
            null
        );

        mockMvc.perform(
                        put("/pois/1")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                        )
                        .andExpect(status().isBadRequest());
    }

    @Test
    public void PUT_PointOfInterest_NOT_FOUND_ERROR() throws Exception {
        PointOfInterestCreateDto dto = new PointOfInterestCreateDto(
            "Museum Updated",
            "Updated Museum",
            "Str. Updated",
            PointOfInterestType.MUSEUM,
            1L
        );

        when(pointOfInterestService.update(eq(999L), any(PointOfInterestCreateDto.class)))
            .thenThrow(new PointOfInterestNotFoundException("PointOfInterest with id 999 not found!"));

        mockMvc.perform(put("/pois/999")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(objectMapper.writeValueAsString(dto))
                        )
                        .andExpect(status().isNotFound());
    }

    @Test
    public void DELETE_PointOfInterest_OK() throws Exception {
        doNothing().when(pointOfInterestService).deleteById(1L);

        mockMvc.perform(
                        delete("/pois/1")
                        )
                        .andExpect(status().isNoContent());
    }

    @Test
    public void DELETE_PointOfInterest_ERROR() throws Exception {
        doThrow(new PointOfInterestNotFoundException("PointOfInterest with id 999 not found!"))
            .when(pointOfInterestService).deleteById(999L);

        mockMvc.perform(
                        delete("/pois/999")
                        )
                        .andExpect(status().isNotFound());
    }
}
