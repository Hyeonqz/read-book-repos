package org.hyeonqz.objects.chp4;

import java.math.BigDecimal;

public class Money {
    private BigDecimal amount;

    public Money (BigDecimal amount) {
        this.amount = amount;
    }

    public BigDecimal getAmount () {
        return amount;
    }

    public void setAmount (BigDecimal amount) {
        this.amount = amount;
    }

}
