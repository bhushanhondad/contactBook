package com.plivo.contactbook;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import java.util.List;

/**
 * Created by shondad on 21/11/18.
 */

@XmlRootElement(name = "contactList")
@XmlAccessorType(XmlAccessType.FIELD)
public class ContactList
{
    @XmlElement(name = "contact")
    private List<Contacts> contactsList = null;

    public List<Contacts> getContactsList() {
        return contactsList;
    }

    public void setContactsList(List<Contacts> contactsList) {
        this.contactsList = contactsList;
    }
}