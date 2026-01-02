package com.extension.test.accounts;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class AccountTest {

    @Test
    void deposit() {
        // given
        Account account = new Account("123");

        // when
        account.deposit(1000);

        // then
        assertThat(account.getBalance()).isEqualTo(1000);
    }

    @DisplayName("음수 금액은 입금할 수 없다")
    @Test
    void deposit_fail_negativeAmount() {
        // given
        Account account = new Account("123");

        // when / then
        assertThatThrownBy(() -> account.deposit(-1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("금액은 0원보다 커야합니다.");
    }

    @DisplayName("삭제된 계좌는 입금할 수 없다")
    @Test
    void deposit_fail_closedAccount() {
        // given
        Account account = new Account("123");
        account.delete();

        // when / then
        assertThatThrownBy(() -> account.deposit(1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용할 수 없는 계좌");
    }

    @Test
    void withdraw() {
        // given
        Account account = new Account("123");
        account.deposit(1000);

        // when
        account.withdraw(1000);

        // then
        assertThat(account.getBalance()).isEqualTo(0);
    }

    @DisplayName("음수 금액은 출금할 수 없다")
    @Test
    void withdraw_fail_negativeAmount() {
        // given
        Account account = new Account("123");

        // when / then
        assertThatThrownBy(() -> account.withdraw(-1000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("금액은 0원보다 커야합니다.");
    }

    @DisplayName("잔액이 부족하면 출금할 수 없다")
    @Test
    void withdraw_fail_notEnoughBalance() {
        // given
        Account account = new Account("123");
        account.deposit(1000);

        // when / then
        assertThatThrownBy(() -> account.withdraw(10_000))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("잔액이 부족");
    }

    @DisplayName("삭제된 계좌는 출금할 수 없다")
    @Test
    void withdraw_fail_closedAccount() {
        // given
        Account account = new Account("123");
        account.deposit(1000);
        account.delete();

        // when / then
        assertThatThrownBy(() -> account.withdraw(100))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("사용할 수 없는 계좌");
    }
}
