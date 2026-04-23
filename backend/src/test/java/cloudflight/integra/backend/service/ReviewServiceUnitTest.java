package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.ReviewException;
import cloudflight.integra.backend.model.City;
import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.Review;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.utils.enums.PointOfInterestType;
import cloudflight.integra.backend.repository.PointOfInterestRepository;
import cloudflight.integra.backend.repository.ReviewRepository;
import cloudflight.integra.backend.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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

    @Mock
    private PointOfInterestRepository poiRepo;

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private ReviewService reviewService;

    private Review testReview;
    private UUID testReviewId;
    private User testUser1;
    private PointOfInterest testPoi;

    @BeforeEach
    void setUp() {
        testUser1 = User.builder()
            .id(UUID.randomUUID())
            .username("testuser1")
            .email("user1@gmail.com")
            .password("password")
            .build();

        City testCity = City.builder()
            .id(UUID.randomUUID())
            .name("Cluj-Napoca")
            .country("Romania")
            .build();

        testPoi = PointOfInterest.builder()
            .id(UUID.randomUUID())
            .name("test poi")
            .description("test desc")
            .address("address 123")
            .type(PointOfInterestType.MUSEUM)
            .city(testCity)
            .build();

        testReviewId = UUID.randomUUID();
        testReview = Review.builder()
            .id(testReviewId)
            .text("Great place!")
            .rating(5)
            .postedDate(LocalDateTime.now())
            .user(testUser1)
            .pointOfInterest(testPoi)
            .build();
    }


    @Test
    void createReview_ShouldReturnCreatedReview_OnValidData() {
        when(userRepository.findById(testReview.getUser().getId())).thenReturn(Optional.of(testUser1));
        when(poiRepo.findById(testReview.getPointOfInterest().getId())).thenReturn(Optional.of(testPoi));
        when(reviewRepository.save(testReview)).thenReturn(testReview);

        Review result = reviewService.createReview(testReview);

        assertNotNull(result.getPostedDate());
        assertTrue(result.getPostedDate().isBefore(LocalDateTime.now().plusSeconds(1)));
        assertEquals("Great place!", result.getText());
        assertEquals(5, result.getRating());
        assertEquals(PointOfInterestType.MUSEUM, result.getPointOfInterest().getType());

        verify(userRepository, times(1)).findById(testUser1.getId());
        verify(poiRepo, times(1)).findById(testPoi.getId());
        verify(reviewRepository, times(1)).save(testReview);
    }

    @Test
    void createReview_ShouldThrowReviewException_WhenUserIsNull() {
        testReview.setUser(null);

        ReviewException exception = assertThrows(ReviewException.class, () -> reviewService.createReview(testReview));

        assertEquals("Review must have a valid user ID", exception.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void createReview_ShouldThrowReviewException_WhenUserIdIsNull() {
        testReview.getUser().setId(null);

        ReviewException exception = assertThrows(ReviewException.class, () -> reviewService.createReview(testReview));

        assertEquals("Review must have a valid user ID", exception.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void createReview_ShouldThrowReviewException_OnNonPersistedUser() {
        when(userRepository.findById(testReview.getUser().getId())).thenReturn(Optional.empty());

        ReviewException exception = assertThrows( ReviewException.class, () -> reviewService.createReview(testReview));

        assertEquals("User with id: " + testReview.getUser().getId() + " not found", exception.getMessage());
        verify(reviewRepository, never()).save(any(Review.class));
        verify(poiRepo, never()).findById(any());
    }

    @Test
    void createReview_ShouldThrowReviewException_WhenPoiIsNull() {
        when(userRepository.findById(testReview.getUser().getId())).thenReturn(Optional.of(testUser1));
        testReview.setPointOfInterest(null);

        ReviewException exception = assertThrows(ReviewException.class, () -> reviewService.createReview(testReview));

        assertEquals("Review must have a valid Point of Interest ID", exception.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void createReview_ShouldThrowReviewException_WhenPoiIdIsNull() {
        when(userRepository.findById(testReview.getUser().getId())).thenReturn(Optional.of(testUser1));
        testReview.getPointOfInterest().setId(null);

        ReviewException exception = assertThrows(ReviewException.class, () -> reviewService.createReview(testReview));

        assertEquals("Review must have a valid Point of Interest ID", exception.getMessage());
        verify(reviewRepository, never()).save(any());
    }

    @Test
    void createReview_ShouldThrowReviewException_OnNonPersistedPoi() {
        when(userRepository.findById(testReview.getUser().getId())).thenReturn(Optional.of(testUser1));
        when(poiRepo.findById(testReview.getPointOfInterest().getId())).thenReturn(Optional.empty());

        ReviewException exception = assertThrows(ReviewException.class, () -> reviewService.createReview(testReview));

        assertEquals("Point of Interest with id: " + testReview.getPointOfInterest().getId() + " not found", exception.getMessage());
        verify(reviewRepository, never()).save(any());
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
    void getFilteredReviews_ShouldReturnByPoiIdAndUserId() {
        when(reviewRepository.findByPointOfInterestIdAndUserId(testPoi.getId(), testUser1.getId()))
            .thenReturn(List.of(testReview));

        List<Review> result = reviewService.getFilteredReviews(testPoi.getId(), testUser1.getId());

        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findByPointOfInterestIdAndUserId(testPoi.getId(), testUser1.getId());
    }

    @Test
    void getFilteredReviews_ShouldReturnByPoiIdOnly() {
        when(reviewRepository.findByPointOfInterestId(testPoi.getId()))
            .thenReturn(List.of(testReview));

        List<Review> result = reviewService.getFilteredReviews(testPoi.getId(), null);

        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findByPointOfInterestId(testPoi.getId());
    }

    @Test
    void getFilteredReviews_ShouldReturnByUserIdOnly() {
        when(reviewRepository.findByUserId(testUser1.getId()))
            .thenReturn(List.of(testReview));

        List<Review> result = reviewService.getFilteredReviews(null, testUser1.getId());

        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findByUserId(testUser1.getId());
    }

    @Test
    void getFilteredReviews_ShouldReturnAll_WhenBothFiltersAreNull() {
        when(reviewRepository.findAll())
            .thenReturn(List.of(testReview));

        List<Review> result = reviewService.getFilteredReviews(null, null);

        assertEquals(1, result.size());
        verify(reviewRepository, times(1)).findAll();
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
