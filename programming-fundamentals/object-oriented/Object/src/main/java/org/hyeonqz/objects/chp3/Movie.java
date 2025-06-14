package org.hyeonqz.objects.chp3;

import org.hyeonqz.objects.chp2.DiscountPolicy;

public class Movie {
    private Money fee;
    private DiscountPolicy discountPolicy;

    public Money calculateMovieFee(Screening screening) {
        return fee.minus(discountPolicy.calculateDiscountAmount(screening));
    }
}
