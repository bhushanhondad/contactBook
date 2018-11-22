package com.plivo.contactbook;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

/**
 * Created by shondad on 21/11/18.
 */

@XmlRootElement( name = "contacts" )
@Entity
@Table( name = "contacts")
public class Contacts {
    private String emailId;
    private String userName;
    private String belongsToUser;

    @XmlElement
    @Column
    @Id
    public String getEmailId() {
        return emailId;
    }

    public void setEmailId(String emailId) {
        this.emailId = emailId;
    }

    @XmlElement
    @Column
    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @XmlElement
    @Column
    public String getBelongsToUser() {
        return belongsToUser;
    }

    public void setBelongsToUser(String belongsToUser) {
        this.belongsToUser = belongsToUser;
    }
}
