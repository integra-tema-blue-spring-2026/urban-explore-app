package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.ReviewException;
import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.Review;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.repository.PointOfInterestRepository;
import cloudflight.integra.backend.repository.ReviewRepository;
import cloudflight.integra.backend.repository.UserRepository;
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
    private final UserRepository userRepository;
    private final PointOfInterestRepository pointOfInterestRepository;

    public Review createReview(Review inputReview) {
        
        if (inputReview.getUser() != null && inputReview.getUser().getId() != null) {
            User user = userRepository.findById(inputReview.getUser().getId())
                .orElseThrow(() -> new ReviewException("User with id: " + inputReview.getUser().getId() + " not found"));
            inputReview.setUser(user);
        } else {
            throw new ReviewException("Review must have a valid user ID");
        }

        if (inputReview.getPointOfInterest() != null && inputReview.getPointOfInterest().getId() != null) {
            PointOfInterest poi = pointOfInterestRepository.findById(inputReview.getPointOfInterest().getId())
                .orElseThrow(() -> new ReviewException("Point of Interest with id: " + inputReview.getPointOfInterest().getId() + " not found"));
            inputReview.setPointOfInterest(poi);
        } else {
            throw new ReviewException("Review must have a valid Point of Interest ID");
        }

        inputReview.setPostedDate(LocalDateTime.now());
        return reviewRepository.save(inputReview);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
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
