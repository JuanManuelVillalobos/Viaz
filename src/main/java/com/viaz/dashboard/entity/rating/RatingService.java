package com.viaz.dashboard.entity.rating;

import org.springframework.stereotype.Service;

@Service
public class RatingService {

    private final RatingRepository ratingRepository;

    public RatingService(RatingRepository ratingRepository) {
        this.ratingRepository = ratingRepository;
    }

    public void createRating(Rating rating) {
        ratingRepository.save(rating);
    }
}
