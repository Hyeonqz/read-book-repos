package org.hyeonqz.objects.chp2;

import java.math.BigDecimal;

public class NoneDiscountPolicy implements DiscountPolicy {

    @Override
    public BigDecimal calculateDiscountAmount () {
        return BigDecimal.ZERO;
    }

}
