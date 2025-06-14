package org.hyeonqz.objects.chp4;

import java.time.Duration;

public class Movie {
    private String title;
    private Duration runningTime;
    private Money fee;
    private List<DiscountCondtion> discountCondtions;

    private MovieType movieType;
    private Money discoutnAmount;
    private double discountPercent;

    public String getTitle () {
        return title;
    }

    public void setTitle (String title) {
        this.title = title;
    }

    public Duration getRunningTime () {
        return runningTime;
    }

    public void setRunningTime (Duration runningTime) {
        this.runningTime = runningTime;
    }

    public Money getFee () {
        return fee;
    }

    public void setFee (Money fee) {
        this.fee = fee;
    }

    public List<DiscountCondtion> getDiscountCondtions () {
        return discountCondtions;
    }

    public void setDiscountCondtions (List<DiscountCondtion> discountCondtions) {
        this.discountCondtions = discountCondtions;
    }

    public MovieType getMovieType () {
        return movieType;
    }

    public void setMovieType (MovieType movieType) {
        this.movieType = movieType;
    }

    public Money getDiscoutnAmount () {
        return discoutnAmount;
    }

    public void setDiscoutnAmount (Money discoutnAmount) {
        this.discoutnAmount = discoutnAmount;
    }

    public double getDiscountPercent () {
        return discountPercent;
    }

    public void setDiscountPercent (double discountPercent) {
        this.discountPercent = discountPercent;
    }

}
