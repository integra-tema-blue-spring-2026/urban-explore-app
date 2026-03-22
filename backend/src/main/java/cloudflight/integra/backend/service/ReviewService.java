package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exceptions.custom.ReviewException;
import cloudflight.integra.backend.model.Review;
import cloudflight.integra.backend.repository.ReviewRepository;
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

    public ReviewDto createReview(ReviewCreateDto reviewDto) {

        Review review = reviewMapper.toEntityFromCreateDto(reviewDto);

        review.setPostedDate(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toDto(savedReview);
    }

    public List<Review> getAllReviews() {
        return reviewRepository.findAll();
    }

    @Transactional
    public Review updateReview(UUID id, Review inputReview) {
        Review existingReview=reviewRepository.findById(id).orElseThrow(
            ()-> new ReviewException("Review with id: "+ id + " not found"));

        existingReview.setText(inputReview.getText());
        existingReview.setRating(inputReview.getRating() == null ? existingReview.getRating() : inputReview.getRating());

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
