package com.subscriptio.domain.model;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class MoneyTest {

    @Test
    void createsMoney() {
        Money money = Money.usd(BigDecimal.valueOf(49.99));
        assertThat(money.amount()).isEqualByComparingTo("49.99");
        assertThat(money.currency()).isEqualTo("USD");
    }

    @Test
    void rejectsNegativeAmount() {
        assertThatThrownBy(() -> Money.usd(BigDecimal.valueOf(-1)))
                .isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    void addsSameCurrency() {
        Money a = Money.usd(BigDecimal.valueOf(10));
        Money b = Money.usd(BigDecimal.valueOf(20));
        assertThat(a.add(b).amount()).isEqualByComparingTo("30");
    }

    @Test
    void rejectsDifferentCurrencyAdd() {
        Money usd = Money.usd(BigDecimal.TEN);
        Money eur = Money.of(BigDecimal.TEN, "EUR");
        assertThatThrownBy(() -> usd.add(eur))
                .isInstanceOf(IllegalArgumentException.class);
    }
}
