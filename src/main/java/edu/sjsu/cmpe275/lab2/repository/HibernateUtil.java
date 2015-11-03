package edu.sjsu.cmpe275.lab2.repository;

import org.hibernate.HibernateException;
import org.hibernate.SessionFactory;
import org.hibernate.Session;
import org.hibernate.Query;
import org.hibernate.cfg.Configuration;
import org.hibernate.metadata.ClassMetadata;
import org.hibernate.service.ServiceRegistry;
import org.hibernate.service.ServiceRegistryBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

import java.util.Map;

/**
 * Project Name: cmpe275lab2
 * Packet Name: PACKAGE_NAME
 * Author: Scott
 * Created Date: 10/28/15 5:38 PM
 * Copyright (c) 2015, 2015 All Right Reserved, http://sjsu.edu/
 * This source is subject to the GPL2 Permissive License.
 * Please see the License.txt file for more information.
 * All other rights reserved.
 * <p>
 * THIS CODE AND INFORMATION ARE PROVIDED "AS IS" WITHOUT WARRANTY OF ANY
 * KIND, EITHER EXPRESSED OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND/OR FITNESS FOR A
 * PARTICULAR PURPOSE.
 */
@Component
public class HibernateUtil {
    private static SessionFactory sessionFactory;

    @Bean
    public static SessionFactory getSessionFactory() {
        if (sessionFactory == null) {
            //initialize one factory
            try {
                Configuration configuration = new Configuration().configure();
                ServiceRegistry serviceRegistry = new ServiceRegistryBuilder().applySettings(configuration.getProperties()).buildServiceRegistry();
                sessionFactory = configuration.buildSessionFactory(serviceRegistry);
            } catch (Throwable e) {
                throw new ExceptionInInitializerError(e);
            }
        }
        return sessionFactory;

    }

    public static void main(final String[] args) throws Exception {
        Session session = getSessionFactory().openSession();

        try {
            System.out.println("querying all the managed entities...");
            final Map metadataMap = session.getSessionFactory().getAllClassMetadata();
            for (Object key : metadataMap.keySet()) {
                final ClassMetadata classMetadata = (ClassMetadata) metadataMap.get(key);
                final String entityName = classMetadata.getEntityName();
                final Query query = session.createQuery("from " + entityName);
                System.out.println("executing: " + query.getQueryString());
                for (Object o : query.list()) {
                    System.out.println("  " + o);
                }
            }
        } finally {
            session.close();
        }
    }
}
