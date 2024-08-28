package com.ericsson.gic.tms.tvs.domain.model.verdict.notification;

/**
 * @author Vladimirs Iljins (vladimirs.iljins@ericsson.com)
 *         24/01/2017
 */
public class Recipient {

    private String email;

    private String username;

    private String firstName;

    private String lastName;

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFirstName() {
        return firstName;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }
}
