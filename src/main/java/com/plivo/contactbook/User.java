package com.plivo.contactbook;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.persistence.Table;


/**
 * Created by shondad on 21/11/18.
 */


@XmlRootElement( name = "user" )
@Entity
@Table ( name = "user")
public class User {

    String userName;
    String password;

    @XmlElement
    @Column
    @Id
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @XmlElement
    @Column
    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
