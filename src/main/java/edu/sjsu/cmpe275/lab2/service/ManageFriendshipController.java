package edu.sjsu.cmpe275.lab2.service;

import edu.sjsu.cmpe275.lab2.repository.Person;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Project Name: cmpe275lab2
 * Packet Name: edu.sjsu.cmpe275.lab2.service
 * Author: Scott
 * Created Date: 10/29/15 5:49 PM
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
@RestController
@RequestMapping("/friends")
public class ManageFriendshipController {

    @Autowired
    SessionFactory sessionFactory;

    /** Add a friendship object
     (9) Add a friend
     Path:friends/{id1}/{id2}
     Method: PUT

     This makes the two persons with the given IDs friends with each other.
     If either person does not exist, return 404.
     If the two persons are already friends, do nothing, just return 200. Otherwise,
     Record this friendship relation. If all is successful, return HTTP code 200 and any
     informative text message in the HTTP payload.

     * @param a			Description of a
     * @param b			Description of b
     * @return			Description of c
     */

    @RequestMapping(value="/{id1}/{id2}", method = RequestMethod.PUT)
    public ResponseEntity createFriendship(@PathVariable("id1") long id1,
                                   @PathVariable("id2") long id2) {
        Session session = null;
        Transaction transaction = null;
        Person person1 = null, person2 = null;

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            person1 = (Person) session.get(Person.class, id1);
            person2 = (Person) session.get(Person.class, id2);
            if(person1 == null || person2 == null) {
                throw new HibernateException("Can't find persons with id1 = " + id1 + " and id2 = " + id2);
            }
            List<Person> f = person1.getFriends();
            if (!f.contains(person2)) {
                f.add(person2);
            }
            person1.setFriends(f);
            session.update(person1);

            f = person2.getFriends();
            if (!f.contains(person1)) {
                f.add(person1);
            }
            person2.setFriends(f);
            session.update(person2);

            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return new ResponseEntity("Can't find persons with id1 = " + id1 + " and id2 = " + id2, HttpStatus.NOT_FOUND);
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return new ResponseEntity("Created the friendship between id1 = " + id1 + " and id2 = " + id2, HttpStatus.OK);
    }

    /** Remove a friendship object
     (10) Remove a friend
     Path:friends/{id1}/{id2}
     Method: DELETE

     This request removes the friendship relation between the two persons.
     If either person does not exist, return 404.
     If the two persons are not friends, return 404. Otherwise,
     Remove this friendship relation. Return HTTP code 200 and a meaningful text message if all is successful.

     * @param a			Description of a
     * @param b			Description of b
     * @return			Description of c
     */

    @RequestMapping(value="/{id1}/{id2}", method = RequestMethod.DELETE)
    public ResponseEntity deleteFriendship(@PathVariable("id1") long id1,
                                   @PathVariable("id2") long id2) {
        Session session = null;
        Transaction transaction = null;
        Person person1 = null, person2 = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            person1 = (Person)session.get(Person.class, id1);
            person2 = (Person)session.get(Person.class, id2);
            if (person1 == null || person2 == null) {
                throw new HibernateException("can't find person records with id1 = " + id1 + " and id2 = " + id2);
            }
            List<Person> l = person1.getFriends();
            if (l.contains(person2)) {
                l.remove(person2);
            }

            l = person2.getFriends();
            if (l.contains(person1)) {
                l.remove(person1);
            }

            session.update(person1);
            session.update(person2);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return new ResponseEntity("can't find person records with id1 = " + id1 + " and id2 = " + id2, HttpStatus.NOT_FOUND);

        } finally {
            if (session != null) {
                session.close();
            }
        }
        return new ResponseEntity("you have deleted a friendship.", HttpStatus.OK);
    }

}
