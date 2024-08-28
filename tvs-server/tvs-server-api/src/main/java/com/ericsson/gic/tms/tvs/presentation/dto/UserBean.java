package com.ericsson.gic.tms.tvs.presentation.dto;


import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement
public class UserBean {

    private boolean authenticated;
    private String name;

    public boolean isAuthenticated() {
        return authenticated;
    }

    public void setAuthenticated(boolean authenticated) {
        this.authenticated = authenticated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
