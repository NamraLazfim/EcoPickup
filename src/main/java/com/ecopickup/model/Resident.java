package com.ecopickup.model;

/**
 * A city resident who books pickups. Each resident has one reward account.
 */
public class Resident extends User {

    private final RewardAccount rewardAccount = new RewardAccount();

    /**
     * Creates a resident with an empty reward account.
     *
     * @param id    unique user id
     * @param name  full name
     * @param email email address
     */
    public Resident(String id, String name, String email) {
        super(id, name, email);
    }

    /**
     * Returns this resident's reward account.
     *
     * @return the reward account
     */
    public RewardAccount getRewardAccount() {
        return rewardAccount;
    }

    /**
     * Returns "Resident".
     *
     * @return the role name
     */
    @Override
    public String getRole() {
        return "Resident";
    }
}
