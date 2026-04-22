package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.ReviewException;
import cloudflight.integra.backend.model.Review;
import cloudflight.integra.backend.repository.ReviewRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ReviewServiceUnitTest {

    @Mock
    private ReviewRepository reviewRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Review testReview;
    private UUID testReviewId;

    @BeforeEach
    void setUp() {
        testReviewId = UUID.randomUUID();
        testReview = Review.builder()
            .id(testReviewId)
            .text("Great place!")
            .rating(5)
            .postedDate(LocalDateTime.now())
            .userId(1L)
            .poiId(1L)
            .build();
    }

    @Test
    void createReview_ShouldSetPostedDateAndSave() {
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review result = reviewService.createReview(testReview);

        assertNotNull(result.getPostedDate());
        assertTrue(result.getPostedDate().isBefore(LocalDateTime.now().plusSeconds(1)));

        ArgumentCaptor<Review> captor = ArgumentCaptor.forClass(Review.class);
        verify(reviewRepository, times(1)).save(captor.capture());
        assertNotNull(captor.getValue().getPostedDate());
    }

    @Test
    void createReview_ShouldReturnSavedReview() {
        when(reviewRepository.save(any(Review.class))).thenReturn(testReview);

        Review result = reviewService.createReview(testReview);

        assertNotNull(result);
        assertEquals("Great place!", result.getText());
        assertEquals(5, result.getRating());
    }

    @Test
    void getAllReviews_ShouldReturnAllReviews() {
        when(reviewRepository.findAll()).thenReturn(List.of(testReview));

        List<Review> result = reviewService.getAllReviews();

        assertEquals(1, result.size());
        assertEquals(testReviewId, result.getFirst().getId());
        verify(reviewRepository, times(1)).findAll();
    }

    @Test
    void getAllReviews_ShouldReturnEmptyList_WhenNoReviewsExist() {
        when(reviewRepository.findAll()).thenReturn(Collections.emptyList());

        List<Review> result = reviewService.getAllReviews();

        assertTrue(result.isEmpty());
    }

    @Test
    void updateReview_ShouldUpdateText_WhenProvided() {
        Review input = Review.builder().text("Updated text.").rating(4).build();
        when(reviewRepository.findById(testReviewId)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review result = reviewService.updateReview(testReviewId, input);

        assertEquals("Updated text.", result.getText());
        assertEquals(4, result.getRating());
        verify(reviewRepository, times(1)).save(testReview);
    }

    @Test
    void updateReview_ShouldPreserveExistingRating_WhenInputRatingIsNull() {
        Review input = Review.builder().text("New text only.").rating(null).build();
        when(reviewRepository.findById(testReviewId)).thenReturn(Optional.of(testReview));
        when(reviewRepository.save(any(Review.class))).thenAnswer(inv -> inv.getArgument(0));

        Review result = reviewService.updateReview(testReviewId, input);

        assertEquals("New text only.", result.getText());
        assertEquals(5, result.getRating());
    }

    @Test
    void updateReview_ShouldThrowException_WhenReviewNotFound() {
        when(reviewRepository.findById(testReviewId)).thenReturn(Optional.empty());

        ReviewException exception = assertThrows(ReviewException.class,
            () -> reviewService.updateReview(testReviewId, testReview));

        assertEquals("Review with id: " + testReviewId + " not found", exception.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void deleteReview_ShouldDelete_WhenReviewExists() {
        when(reviewRepository.existsById(testReviewId)).thenReturn(true);

        reviewService.deleteReview(testReviewId);

        verify(reviewRepository, times(1)).existsById(testReviewId);
        verify(reviewRepository, times(1)).deleteById(testReviewId);
    }

    @Test
    void deleteReview_ShouldThrowException_WhenReviewNotFound() {
        when(reviewRepository.existsById(testReviewId)).thenReturn(false);

        ReviewException exception = assertThrows(ReviewException.class,
            () -> reviewService.deleteReview(testReviewId));

        assertEquals("Review with id: " + testReviewId + " not found", exception.getMessage());
        verify(reviewRepository, never()).deleteById(any());
    }
}
