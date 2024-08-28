package com.ericsson.gic.tms.tvs.presentation.dto;


import com.ericsson.gic.tms.presentation.dto.jsonapi.Attributes;
import org.hibernate.validator.constraints.Email;

import javax.validation.constraints.NotNull;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class RecipientBean implements Attributes<String> {

    @NotNull
    @Email
    private String email;

    private String username;

    private String firstName;

    private String lastName;

    public RecipientBean() {
        //needed for parsing
    }

    public RecipientBean(String email) {
        this.email = email;
    }

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

    @Override
    public String getId() {
        return email;
    }
}
