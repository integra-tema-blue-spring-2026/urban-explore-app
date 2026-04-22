package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.ReviewException;
import cloudflight.integra.backend.model.Review;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@Import(ReviewService.class)
class ReviewServiceIntegrationTest {

    @Autowired
    private ReviewService reviewService;

    private Review testReview;

    @BeforeEach
    void setUp() {
        testReview = Review.builder()
            .text("Great place!")
            .rating(5)
            .userId(1L)
            .poiId(1L)
            .build();
    }

    @Test
    void createReview_ShouldPersistReviewWithGeneratedIdAndPostedDate() {
        Review saved = reviewService.createReview(testReview);

        assertNotNull(saved.getId());
        assertNotNull(saved.getPostedDate());
        assertEquals("Great place!", saved.getText());
        assertEquals(1, reviewService.getAllReviews().size());
    }

    @Test
    void getAllReviews_ShouldReturnAllPersistedReviews() {
        reviewService.createReview(Review.builder().text("First").rating(3).userId(1L).poiId(1L).build());
        reviewService.createReview(Review.builder().text("Second").rating(4).userId(2L).poiId(1L).build());

        List<Review> all = reviewService.getAllReviews();

        assertEquals(2, all.size());
    }

    @Test
    void updateReview_ShouldUpdateText() {
        Review saved = reviewService.createReview(testReview);

        Review input = Review.builder().text("Updated text.").rating(4).build();
        Review updated = reviewService.updateReview(saved.getId(), input);

        assertEquals("Updated text.", updated.getText());
        assertEquals(4, updated.getRating());
    }

    @Test
    void updateReview_ShouldPreserveRating_WhenInputRatingIsNull() {
        Review saved = reviewService.createReview(testReview);

        Review input = Review.builder().text("New text only.").rating(null).build();
        Review updated = reviewService.updateReview(saved.getId(), input);

        assertEquals("New text only.", updated.getText());
        assertEquals(5, updated.getRating());
    }

    @Test
    void updateReview_ShouldThrowException_WhenReviewNotFound() {
        UUID randomId = UUID.randomUUID();

        assertThrows(ReviewException.class,
            () -> reviewService.updateReview(randomId, testReview));
    }

    @Test
    void deleteReview_ShouldRemoveReview() {
        Review saved = reviewService.createReview(testReview);

        reviewService.deleteReview(saved.getId());

        assertTrue(reviewService.getAllReviews().isEmpty());
    }

    @Test
    void deleteReview_ShouldThrowException_WhenReviewNotFound() {
        UUID randomId = UUID.randomUUID();

        ReviewException exception = assertThrows(ReviewException.class,
            () -> reviewService.deleteReview(randomId));

        assertEquals("Review with id: " + randomId + " not found", exception.getMessage());
    }
}
