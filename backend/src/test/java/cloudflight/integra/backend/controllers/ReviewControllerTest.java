package cloudflight.integra.backend.controllers;

import cloudflight.integra.backend.controller.ReviewController;
import cloudflight.integra.backend.exceptions.custom.ReviewException;
import cloudflight.integra.backend.model.Review;
import cloudflight.integra.backend.model.dtos.review.ReviewCreateDto;
import cloudflight.integra.backend.model.dtos.review.ReviewDto;
import cloudflight.integra.backend.model.dtos.review.ReviewUpdateDto;
import cloudflight.integra.backend.model.utils.mappers.ReviewMapper;
import cloudflight.integra.backend.service.ReviewService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(ReviewController.class)
class ReviewControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private ReviewMapper reviewMapper;

    private UUID reviewId;
    private Review testReview;
    private ReviewDto testReviewDto;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID();

        testReview = Review.builder()
            .id(reviewId)
            .text("Great place!")
            .rating(5)
            .postedDate(LocalDateTime.now())
            .userId(1L)
            .poiId(1L)
            .build();

        testReviewDto = ReviewDto.builder()
            .id(reviewId)
            .text("Great place!")
            .rating(5)
            .postedDate(testReview.getPostedDate())
            .userId(1L)
            .poiId(1L)
            .build();
    }

    @Test
    void createReview_ShouldReturn201AndCreatedReview() throws Exception {
        ReviewCreateDto createDto = ReviewCreateDto.builder()
            .text("Great place!")
            .rating(5)
            .userId(1L)
            .poiId(1L)
            .build();

        when(reviewMapper.toEntityFromCreateDto(any(ReviewCreateDto.class))).thenReturn(testReview);
        when(reviewService.createReview(any(Review.class))).thenReturn(testReview);
        when(reviewMapper.toDto(testReview)).thenReturn(testReviewDto);

        mockMvc.perform(post("/reviews")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(createDto)))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.text").value("Great place!"))
            .andExpect(jsonPath("$.rating").value(5));
    }

    @Test
    void getAllReviews_ShouldReturn200WithReviewList() throws Exception {
        when(reviewService.getAllReviews()).thenReturn(List.of(testReview));
        when(reviewMapper.toDto(testReview)).thenReturn(testReviewDto);

        mockMvc.perform(get("/reviews"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$[0].text").value("Great place!"))
            .andExpect(jsonPath("$[0].rating").value(5));
    }

    @Test
    void getAllReviews_ShouldReturn200WithEmptyList_WhenNoReviewsExist() throws Exception {
        when(reviewService.getAllReviews()).thenReturn(Collections.emptyList());

        mockMvc.perform(get("/reviews"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$").isEmpty());
    }

    @Test
    void updateReview_ShouldReturn200AndUpdatedReview() throws Exception {
        ReviewUpdateDto updateDto = ReviewUpdateDto.builder()
            .text("Updated text.")
            .rating(4)
            .build();

        ReviewDto updatedDto = ReviewDto.builder()
            .id(reviewId)
            .text("Updated text.")
            .rating(4)
            .postedDate(testReview.getPostedDate())
            .userId(1L)
            .poiId(1L)
            .build();

        when(reviewMapper.toEntityFromUpdateDto(any(ReviewUpdateDto.class))).thenReturn(testReview);
        when(reviewService.updateReview(eq(reviewId), any(Review.class))).thenReturn(testReview);
        when(reviewMapper.toDto(testReview)).thenReturn(updatedDto);

        mockMvc.perform(put("/reviews/{id}", reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.text").value("Updated text."))
            .andExpect(jsonPath("$.rating").value(4));
    }

    @Test
    void updateReview_ShouldReturn404_WhenReviewNotFound() throws Exception {
        ReviewUpdateDto updateDto = ReviewUpdateDto.builder()
            .text("Updated text.")
            .rating(4)
            .build();

        when(reviewMapper.toEntityFromUpdateDto(any(ReviewUpdateDto.class))).thenReturn(testReview);
        when(reviewService.updateReview(eq(reviewId), any(Review.class)))
            .thenThrow(new ReviewException("Review with id: " + reviewId + " not found"));

        mockMvc.perform(put("/reviews/{id}", reviewId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(updateDto)))
            .andExpect(status().isNotFound());
    }

    @Test
    void deleteReview_ShouldReturn204_WhenReviewExists() throws Exception {
        doNothing().when(reviewService).deleteReview(reviewId);

        mockMvc.perform(delete("/reviews/{id}", reviewId))
            .andExpect(status().isNoContent());
    }

    @Test
    void deleteReview_ShouldReturn404_WhenReviewNotFound() throws Exception {
        doThrow(new ReviewException("Review with id: " + reviewId + " not found"))
            .when(reviewService).deleteReview(reviewId);

        mockMvc.perform(delete("/reviews/{id}", reviewId))
            .andExpect(status().isNotFound());
    }
}
