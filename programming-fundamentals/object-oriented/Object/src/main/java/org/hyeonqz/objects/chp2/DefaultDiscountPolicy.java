package org.hyeonqz.objects.chp2;

import java.math.BigDecimal;

public abstract class DefaultDiscountPolicy implements DiscountPolicy {
    abstract BigDecimal getDiscountAmount();
}
