package cloudflight.integra.backend.model.utils.mappers;

import cloudflight.integra.backend.model.Review;
import cloudflight.integra.backend.model.dtos.review.ReviewCreateDto;
import cloudflight.integra.backend.model.dtos.review.ReviewDto;
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

    public Review toEntityFromCreateDto(ReviewCreateDto reviewDto) {
        return Review.builder()
            .text(reviewDto.getText())
            .rating(reviewDto.getRating())
            .userId(reviewDto.getUserId())
            .poiId(reviewDto.getPoiId())
            .build();
    }
}
