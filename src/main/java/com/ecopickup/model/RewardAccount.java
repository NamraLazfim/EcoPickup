package com.ecopickup.model;

/**
 * A resident's reward points. The balance can never go below zero.
 */
public class RewardAccount {

    private int balance;

    /**
     * Adds points to the balance.
     *
     * @param points points to add; must be greater than zero
     */
    public void addPoints(int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points to add must be greater than 0");
        }
        balance += points;
    }

    /**
     * Spends points from the balance.
     *
     * @param points points to spend; must be greater than zero and not more than the balance
     * @throws IllegalStateException if the balance is too low
     */
    public void redeem(int points) {
        if (points <= 0) {
            throw new IllegalArgumentException("Points to redeem must be greater than 0");
        }
        if (points > balance) {
            throw new IllegalStateException("Insufficient points: balance is " + balance);
        }
        balance -= points;
    }

    /**
     * Returns the current points balance.
     *
     * @return the balance
     */
    public int getBalance() {
        return balance;
    }
}
