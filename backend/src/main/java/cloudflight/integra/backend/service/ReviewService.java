package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.ReviewException;
import cloudflight.integra.backend.model.Review;
import cloudflight.integra.backend.model.dtos.ReviewCreateDto;
import cloudflight.integra.backend.model.dtos.ReviewDto;
import cloudflight.integra.backend.model.dtos.ReviewUpdateDto;
import cloudflight.integra.backend.model.mappers.ReviewMapper;
import cloudflight.integra.backend.repository.ReviewRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class ReviewService {
    private final ReviewRepository reviewRepository;
    private final ReviewMapper reviewMapper;

    public ReviewDto createReview(ReviewCreateDto reviewDto) {
        Review review = reviewMapper.toEntityFromCreateDto(reviewDto);

        review.setPostedDate(LocalDateTime.now());

        Review savedReview = reviewRepository.save(review);
        return reviewMapper.toDto(savedReview);
    }

    public List<ReviewDto> getAllReviews() {
        List<Review> reviews=reviewRepository.findAll();
        return reviews.stream()
            .map(reviewMapper::toDto)
            .toList();
    }

    @Transactional
    public ReviewDto updateReview(UUID id, ReviewUpdateDto reviewDto) {
        Review existingReview=reviewRepository.findById(id).orElseThrow(()-> new ReviewException("Review with id: "+ id + " not found"));

        existingReview.setText(reviewDto.getText());

        existingReview.setRating(reviewDto.getRating());

        Review updatedReview=reviewRepository.save(existingReview);
        return reviewMapper.toDto(updatedReview);
    }

    @Transactional
    public void deleteReview(UUID id) {
        if(!(reviewRepository.existsById(id))) {
            throw new ReviewException("Review with id: " + id + " not found");
        }

        reviewRepository.deleteById(id);
    }
}
