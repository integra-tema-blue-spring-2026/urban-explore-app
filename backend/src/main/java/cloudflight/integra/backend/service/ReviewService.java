package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.ReviewException;
import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.Review;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.dtos.review.UserProfileReviewDto;
import cloudflight.integra.backend.repository.PointOfInterestRepository;
import cloudflight.integra.backend.repository.ReviewRepository;
import cloudflight.integra.backend.repository.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final UserRepository userRepository;
    private final PointOfInterestRepository pointOfInterestRepository;

    public Review createReview(Review inputReview) {

        if (inputReview.getUser() != null && inputReview.getUser().getId() != null) {
            User user = userRepository.findById(inputReview.getUser().getId())
                .orElseThrow(() -> new ReviewException(
                    "User with id: " + inputReview.getUser().getId() + " not found"));
            inputReview.setUser(user);
        } else {
            throw new ReviewException("Review must have a valid user ID");
        }

        if (inputReview.getPointOfInterest() != null && inputReview.getPointOfInterest().getId() != null) {
            PointOfInterest poi = pointOfInterestRepository.findById(inputReview.getPointOfInterest().getId())
                .orElseThrow(() ->
                new ReviewException("Point of Interest with id: " + inputReview.getPointOfInterest().getId() +
                " not found"));
            inputReview.setPointOfInterest(poi);
        } else {
            throw new ReviewException("Review must have a valid Point of Interest ID");
        }

        inputReview.setPostedDate(LocalDateTime.now());
        return reviewRepository.save(inputReview);
    }



    public List<UserProfileReviewDto> getUserProfileReviews(UUID userId) {
        List<Review> reviews = reviewRepository.findByUserId(userId);

        List<UUID> poiIds = reviews.stream()
            .map(review -> review.getPointOfInterest().getId())
            .distinct()
            .toList();


        Map<UUID, PointOfInterest> poiMap = pointOfInterestRepository.findAllByIdWithCity(poiIds)
            .stream()
            .collect(Collectors.toMap(PointOfInterest::getId, poi -> poi));


        return reviews.stream().map(r -> {

            PointOfInterest poi = poiMap.get(r.getPointOfInterest().getId());

            return UserProfileReviewDto.builder()
                .id(r.getId())
                .text(r.getText())
                .rating(r.getRating())
                .postedDate(r.getPostedDate())
                .poiId(poi.getId())
                .poiName(poi.getName())
                .poiImageUrl(poi.getCity() != null ? poi.getCity().getImageUrl() : null)
                .cityName(poi.getCity() != null ? poi.getCity().getName() : "Unknown City")
                .build();
        }).toList();
    }
    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getFilteredReviews(UUID poiId, UUID userId) {
        if(poiId != null && userId != null) {
            return reviewRepository.findByPointOfInterestIdAndUserId(poiId, userId);
        } else if (poiId != null) {
            return reviewRepository.findByPointOfInterestId(poiId);
        } else if (userId != null) {
            return reviewRepository.findByUserId(userId);
        } else {
            return reviewRepository.findAll();
        }
    }

    @Transactional
    public Review updateReview(UUID id, Review inputReview) {
        Review existingReview=reviewRepository.findById(id).orElseThrow(
            ()-> new ReviewException("Review with id: "+ id + " not found"));
        existingReview.setText(inputReview.getText());
        existingReview.setRating(
            inputReview.getRating() == null ?
                existingReview.getRating() :
                inputReview.getRating()
        );

        return reviewRepository.save(existingReview);
    }

    @Transactional
    public void deleteReview(UUID id) {
        log.info("Attempting to delete review with id: {}", id);
        if(!(reviewRepository.existsById(id))) {
            log.warn("Review with id: {} not found", id);
            throw new ReviewException("Review with id: " + id + " not found");
        }

        reviewRepository.deleteById(id);
        log.info("Review with id: {} deleted successfully", id);
    }
}
