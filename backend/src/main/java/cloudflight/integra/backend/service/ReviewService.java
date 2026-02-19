package cloudflight.integra.backend.service;

import cloudflight.integra.backend.exception.ReviewException;
import cloudflight.integra.backend.model.Review;
import cloudflight.integra.backend.model.dtos.ReviewDto;
import cloudflight.integra.backend.model.mappers.ReviewMapper;
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
    private final ReviewMapper reviewMapper;

    public ReviewDto createReview(ReviewDto reviewDto) {
        Review review=reviewMapper.toEntity(reviewDto);

        if(!(review.getRating()>=1 && review.getRating()<=5)){
            throw new ReviewException("Rating must be between 1 and 5");
        }

        review.setPostedDate(LocalDateTime.now());

        Review savedReview=reviewRepository.save(review);
        return reviewMapper.toDto(savedReview);
    }

    public List<ReviewDto> getAllReviews() {
        List<Review> reviews=reviewRepository.findAll();
        return reviews.stream()
            .map(reviewMapper::toDto)
            .toList();
    }

    @Transactional
    public ReviewDto updateReview(UUID id, ReviewDto reviewDto) {
        Review existingReview=reviewRepository.findById(id).orElseThrow(()-> new ReviewException("Review with id: "+ id + "not found"));

        if(!(reviewDto.getText()!=null && !reviewDto.getText().isEmpty()))
            existingReview.setText(reviewDto.getText());

        if(!(reviewDto.getRating()>=1 && reviewDto.getRating()<=5)){
            throw new ReviewException("Rating must be between 1 and 5");
        }
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
