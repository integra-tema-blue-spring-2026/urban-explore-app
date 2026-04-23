package cloudflight.integra.backend.controllers;

import cloudflight.integra.backend.exceptions.custom.ReviewException;
import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.Review;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.dtos.review.ReviewCreateDto;
import cloudflight.integra.backend.model.dtos.review.ReviewDto;
import cloudflight.integra.backend.model.dtos.review.ReviewUpdateDto;
import cloudflight.integra.backend.model.utils.enums.PointOfInterestType;
import cloudflight.integra.backend.model.utils.mappers.ReviewMapper;
import cloudflight.integra.backend.service.ReviewService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.*;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import java.time.LocalDateTime;
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
class ReviewControllerIntegrationTest extends BaseControllerIntegrationTest {

    @MockitoBean
    private ReviewService reviewService;

    @MockitoBean
    private ReviewMapper reviewMapper;

    private String token;
    private UUID reviewId;
    private UUID userId;
    private UUID poiId;
    private Review testReview;
    private ReviewDto testReviewDto;

    @BeforeEach
    void setUp() {
        reviewId = UUID.randomUUID();
        userId = UUID.randomUUID();
        poiId = UUID.randomUUID();

        User testUser = new User();
        testUser.setId(userId);
        testUser.setUsername("testuser");
        testUser.setEmail("user@gmail.com");
        testUser.setPassword("password");

        City testCity = City.builder()
            .id(UUID.randomUUID()).name("Cluj-Napoca").country("Romania").build();

        PointOfInterest testPoi = PointOfInterest.builder()
            .id(poiId).name("test poi").description("test desc")
            .address("address 123").type(PointOfInterestType.MUSEUM).city(testCity).build();

        testReview = Review.builder()
            .id(reviewId).text("Great place!").rating(5)
            .postedDate(LocalDateTime.now())
            .user(testUser).pointOfInterest(testPoi)
            .build();

        testReviewDto = ReviewDto.builder()
            .id(reviewId).text("Great place!").rating(5)
            .postedDate(testReview.getPostedDate())
            .userId(userId).poiId(poiId)
            .build();

        token = setupAuthAndGetToken();
    }


    @Test
    void createReview_ShouldReturn201AndCreatedReview() {
        ReviewCreateDto createDto = ReviewCreateDto.builder()
            .text("Great place!").rating(5).userId(userId).poiId(poiId).build();

        when(reviewMapper.toEntityFromCreateDto(any(ReviewCreateDto.class))).thenReturn(testReview);
        when(reviewService.createReview(any(Review.class))).thenReturn(testReview);
        when(reviewMapper.toDto(testReview)).thenReturn(testReviewDto);

        ResponseEntity<ReviewDto> response = restTemplate.exchange(
            "/reviews", HttpMethod.POST, authEntity(createDto, token), ReviewDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CREATED);
        assertThat(response.getBody().getText()).isEqualTo("Great place!");
        assertThat(response.getBody().getRating()).isEqualTo(5);
    }

    @Test
    void createReview_ShouldReturn401_WhenNoTokenProvided() {
        ReviewCreateDto createDto = ReviewCreateDto.builder()
            .text("Great place!").rating(5).userId(userId).poiId(poiId).build();

        ResponseEntity<String> response = restTemplate.postForEntity(
            "/reviews", createDto, String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    void getAllReviews_ShouldReturn200WithEmptyList_WhenNoFilters() {
        when(reviewService.getAllReviews()).thenReturn(Collections.emptyList());

        ResponseEntity<List> response = restTemplate.exchange(
            "/reviews", HttpMethod.GET, authEntity(token), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).isEmpty();
    }

    @Test
    void getAllReviews_ShouldReturn200WithFilteredReviews_WhenParamsProvided() {
        when(reviewService.getFilteredReviews(poiId, userId)).thenReturn(List.of(testReview));
        when(reviewMapper.toDto(testReview)).thenReturn(testReviewDto);

        ResponseEntity<List> response = restTemplate.exchange(
            "/reviews?userId=" + userId + "&poiId=" + poiId,
            HttpMethod.GET, authEntity(token), List.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody()).hasSize(1);
    }

    @Test
    void getAllReviews_ShouldReturn401_WhenNoTokenProvided() {
        ResponseEntity<String> response = restTemplate.getForEntity("/reviews", String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }


    @Test
    void updateReview_ShouldReturn200AndUpdatedReview() {
        ReviewUpdateDto updateDto = ReviewUpdateDto.builder()
            .text("Updated text.").rating(4).build();

        ReviewDto updatedDto = ReviewDto.builder()
            .id(reviewId).text("Updated text.").rating(4)
            .postedDate(testReview.getPostedDate()).userId(userId).poiId(poiId)
            .build();

        when(reviewMapper.toEntityFromUpdateDto(any(ReviewUpdateDto.class))).thenReturn(testReview);
        when(reviewService.updateReview(eq(reviewId), any(Review.class))).thenReturn(testReview);
        when(reviewMapper.toDto(testReview)).thenReturn(updatedDto);

        ResponseEntity<ReviewDto> response = restTemplate.exchange(
            "/reviews/" + reviewId, HttpMethod.PUT, authEntity(updateDto, token), ReviewDto.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
        assertThat(response.getBody().getText()).isEqualTo("Updated text.");
        assertThat(response.getBody().getRating()).isEqualTo(4);
    }

    @Test
    void updateReview_ShouldReturn404_WhenReviewNotFound() {
        ReviewUpdateDto updateDto = ReviewUpdateDto.builder()
            .text("Updated text.").rating(4).build();

        when(reviewMapper.toEntityFromUpdateDto(any(ReviewUpdateDto.class))).thenReturn(testReview);
        when(reviewService.updateReview(eq(reviewId), any(Review.class)))
            .thenThrow(new ReviewException("Review with id: " + reviewId + " not found"));

        ResponseEntity<String> response = restTemplate.exchange(
            "/reviews/" + reviewId, HttpMethod.PUT, authEntity(updateDto, token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }


    @Test
    void deleteReview_ShouldReturn204_WhenReviewExists() {
        doNothing().when(reviewService).deleteReview(reviewId);

        ResponseEntity<Void> response = restTemplate.exchange(
            "/reviews/" + reviewId, HttpMethod.DELETE, authEntity(token), Void.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NO_CONTENT);
        verify(reviewService, times(1)).deleteReview(reviewId);
    }

    @Test
    void deleteReview_ShouldReturn404_WhenReviewNotFound() {
        doThrow(new ReviewException("Review with id: " + reviewId + " not found"))
            .when(reviewService).deleteReview(reviewId);

        ResponseEntity<String> response = restTemplate.exchange(
            "/reviews/" + reviewId, HttpMethod.DELETE, authEntity(token), String.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
    }
}
