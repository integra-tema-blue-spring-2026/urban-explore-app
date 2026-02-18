package cloudflight.integra.backend.model.mappers;

import cloudflight.integra.backend.model.Review;
import cloudflight.integra.backend.model.dtos.ReviewDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewMapper {
    public Review toEntity(ReviewDto reviewDto) {
        return Review.builder()
            .id(reviewDto.getId())
            .text(reviewDto.getText())
            .rating(reviewDto.getRating())
            .postedDate(reviewDto.getPostedDate())
            .userId(reviewDto.getUserId())
            .poiId(reviewDto.getPoiId())
            .build();
    }

    public ReviewDto toDto(Review review) {
        return ReviewDto.builder()
            .id(review.getId())
            .text(review.getText())
            .rating(review.getRating())
            .postedDate(review.getPostedDate())
            .userId(review.getUserId())
            .poiId(review.getPoiId())
            .build();
    }
}
