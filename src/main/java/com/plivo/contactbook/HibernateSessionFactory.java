package com.plivo.contactbook;

import org.hibernate.Session;
import org.hibernate.SessionFactory;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.boot.registry.StandardServiceRegistryBuilder;
import org.hibernate.cfg.Configuration;
import org.hibernate.service.ServiceRegistry;

/**
 * Created by shondad on 20/11/18.
 */

enum HibernateSessionFactory
{
    INSTANCE;

    static SessionFactory sessionFactoryObj;

    // This Method Is Used To Create The Hibernate's SessionFactory Object
    protected static SessionFactory getSessionFactory() {

        // Creating Configuration Instance & Passing Hibernate Configuration File
        Configuration configObj = new Configuration();
        configObj.configure("hibernate.cfg.xml");


        // Since Hibernate Version 4.x, ServiceRegistry Is Being Used
        ServiceRegistry serviceRegistryObj = new StandardServiceRegistryBuilder().applySettings(configObj.getProperties()).build();


        // Creating Hibernate SessionFactory Instance
        sessionFactoryObj = configObj.buildSessionFactory(serviceRegistryObj);
        return sessionFactoryObj;
    }
}
