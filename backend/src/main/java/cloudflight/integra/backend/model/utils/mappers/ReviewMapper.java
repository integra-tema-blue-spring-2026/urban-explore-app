package cloudflight.integra.backend.model.utils.mappers;

import cloudflight.integra.backend.model.PointOfInterest;
import cloudflight.integra.backend.model.Review;
import cloudflight.integra.backend.model.User;
import cloudflight.integra.backend.model.dtos.review.ReviewCreateDto;
import cloudflight.integra.backend.model.dtos.review.ReviewDto;
import cloudflight.integra.backend.model.dtos.review.ReviewUpdateDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class ReviewMapper {

    public ReviewDto toDto(Review review) {
        return ReviewDto.builder()
            .id(review.getId())
            .text(review.getText())
            .rating(review.getRating())
            .postedDate(review.getPostedDate())
            .userId(review.getUser() != null ? review.getUser().getId() : null)
            .poiId(review.getPointOfInterest() != null ? review.getPointOfInterest().getId() : null)
            .build();
    }

    public Review toEntityFromCreateDto(ReviewCreateDto reviewDto) {
        Review review = Review.builder()
            .text(reviewDto.getText())
            .rating(reviewDto.getRating())
            .build();
        
        // Set user and POI as stubs with just IDs (they will be resolved by the service)
        if (reviewDto.getUserId() != null) {
            User user = new User();
            user.setId(reviewDto.getUserId());
            review.setUser(user);
        }
        
        if (reviewDto.getPoiId() != null) {
            PointOfInterest poi = new PointOfInterest();
            poi.setId(reviewDto.getPoiId());
            review.setPointOfInterest(poi);
        }
        
        return review;
    }

    public Review toEntityFromUpdateDto(ReviewUpdateDto reviewDto) {
        if (reviewDto == null) return null;
        return Review.builder()
            .text(reviewDto.getText())
            .rating(reviewDto.getRating())
            .build();
    }
}
