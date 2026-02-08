package com.konrad.movieseeker.model;

/**
 * The Review class represents a review for a movie.
 * It contains information about the author of the review, the content of the review,
 * the rating given to the movie, and the date when the review was last updated.
 */
public class Review {
    private String author;
    private String content;
    private double rating;
    private String updatedAt;

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public double getRating() {
        return rating;
    }

    public void setRating(double rating) {
        this.rating = rating;
    }

    public String getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(String updatedAt) {
        this.updatedAt = updatedAt;
    }
}