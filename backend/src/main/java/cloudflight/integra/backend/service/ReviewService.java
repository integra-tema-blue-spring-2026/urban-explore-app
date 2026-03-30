package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.ReviewException;
import cloudflight.integra.backend.model.Review;
import cloudflight.integra.backend.repository.ReviewRepository;
import cloudflight.integra.backend.repository.PointOfInterestRepository;
import cloudflight.integra.backend.repository.UserRepository;
import cloudflight.integra.backend.exceptions.custom.PointOfInterestNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final PointOfInterestRepository pointOfInterestRepository;
    private final UserRepository userRepository;

    public Review createReview(Review inputReview) {
        if (!pointOfInterestRepository.existsById(inputReview.getPoiId())) {
            throw new PointOfInterestNotFoundException("POI with id: " + inputReview.getPoiId() + " not found");
        }
            if (!userRepository.existsById(inputReview.getUserId())) {
                throw new ReviewException("User with id: " + inputReview.getUserId() + " not found");
            }

        inputReview.setPostedDate(LocalDateTime.now());
        return reviewRepository.save(inputReview);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    public List<Review> getFilteredReviews(UUID poiId, UUID userId) {
        if(poiId != null && userId != null) {
            return reviewRepository.findByPoiIdAndUserId(poiId, userId);
        } else if (poiId != null) {
            return reviewRepository.findByPoiId(poiId);
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
        if(!(reviewRepository.existsById(id))) {
            throw new ReviewException("Review with id: " + id + " not found");
        }

        reviewRepository.deleteById(id);
    }
}
