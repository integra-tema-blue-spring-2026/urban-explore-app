package cloudflight.integra.backend.controller;

import cloudflight.integra.backend.model.Review;
import cloudflight.integra.backend.model.dtos.review.ReviewCreateDto;
import cloudflight.integra.backend.model.dtos.review.ReviewDto;
import cloudflight.integra.backend.model.dtos.review.ReviewUpdateDto;
import cloudflight.integra.backend.model.utils.mappers.ReviewMapper;
import cloudflight.integra.backend.service.ReviewService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/reviews")
@RequiredArgsConstructor
public class ReviewController {
    private final ReviewService reviewService;
    private final ReviewMapper reviewMapper;

    @PostMapping
    public ResponseEntity<ReviewDto> createReview(@Valid @RequestBody ReviewCreateDto reviewDto){
        Review savedReview = reviewService.createReview(reviewMapper.toEntityFromCreateDto(reviewDto));
        return new ResponseEntity<>(reviewMapper.toDto(savedReview), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ReviewDto>> getReviews(@RequestParam(required = false) UUID poiId, @RequestParam(required = false) UUID userId){
        return new ResponseEntity<>(reviewService.getFilteredReviews(poiId, userId).stream()
            .map(reviewMapper::toDto)
            .toList(), HttpStatus.OK);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ReviewDto> updateReview(@PathVariable UUID id, @Valid @RequestBody ReviewUpdateDto reviewDto){

        Review updatedReview = reviewService
            .updateReview(id, reviewMapper.toEntityFromUpdateDto(reviewDto));

        return new ResponseEntity<>( reviewMapper.toDto(updatedReview), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteReview(@PathVariable UUID id){
        reviewService.deleteReview(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

}
