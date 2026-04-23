package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.ReviewException;
import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.Review;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.utils.enums.PointOfInterestType;
import cloudflight.integra.backend.repository.CityRepository;
import cloudflight.integra.backend.repository.PointOfInterestRepository;
import cloudflight.integra.backend.repository.UserRepository;
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
    private UserRepository userRepository;

    @Autowired
    private CityRepository cityRepository;

    @Autowired
    private PointOfInterestRepository poiRepository;

    @Autowired
    private ReviewService reviewService;

    private Review testReview;
    private User testUser1;
    private User testUser2;
    private PointOfInterest testPoi;

    @BeforeEach
    void setUp() {
        testUser1 = User.builder()
            .username("testuser1")
            .email("user1@gmail.com")
            .password("password")
            .build();
        userRepository.save(testUser1);
        testUser2 = User.builder()
            .username("testuser2")
            .email("user2@gmail.com")
            .password("password")
            .build();
        userRepository.save(testUser2);

        City testCity = City.builder()
            .name("Cluj-Napoca")
            .country("Romania")
            .build();
        cityRepository.save(testCity);


        testPoi = PointOfInterest.builder()
            .name("test poi")
            .description("test desc")
            .address("address 123")
            .type(PointOfInterestType.MUSEUM)
            .city(testCity)
            .build();
        poiRepository.save(testPoi);
        testReview = Review.builder()
            .text("Great place!")
            .rating(5)
            .user(testUser1)
            .pointOfInterest(testPoi)
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
        reviewService.createReview(Review.builder()
            .text("First")
            .rating(3)
            .user(testUser1)
            .pointOfInterest(testPoi).build());
        reviewService.createReview(Review.builder()
            .text("Second")
            .rating(4)
            .user(testUser2)
            .pointOfInterest(testPoi).build());

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
