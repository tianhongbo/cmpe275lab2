package edu.sjsu.cmpe275.lab2.service;

import edu.sjsu.cmpe275.lab2.repository.Address;
import edu.sjsu.cmpe275.lab2.repository.Organization;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;

/**
 * Project Name: cmpe275lab2
 * Packet Name: edu.sjsu.cmpe275.lab2.service
 * Author: Scott
 * Created Date: 10/29/15 5:41 PM
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
@RequestMapping("/org")
public class ManageOrgController {
    @Autowired
    SessionFactory sessionFactory;

    /** (5) Create an organization object
     *
     * Method: POST
     * This API creates an organization object.
     * For simplicity, all the fields (name, description, street, city, etc), except ID, are passed in as query
     * parameters. Only name is required.
     * The request returns the newly created organization object in JSON in its HTTP payload, including all attributes.
     * (Please note this differs from generally recommended practice of only returning the ID.)
     * If the request is invalid, e.g., missing required parameters, the HTTP status code should be 400; otherwise 200.
     *
     * @param name			Name of Organization
     * @param description	Brief description of organization
     * @param street	    Address of Organization
     * @param city			Address of Organization
     * @param state			Address of Organization
     * @param zip		    Address of Organization
     * @return			    Created Organization
     */

    @RequestMapping(value="", method = RequestMethod.POST)
    public ResponseEntity createOrganization(@RequestParam(value = "name", required = true) String name,
                                     @RequestParam(value = "description", required = false) String description,
                                     @RequestParam(value = "street", required = false) String street,
                                     @RequestParam(value = "city", required = false) String city,
                                     @RequestParam(value = "state", required = false) String state,
                                     @RequestParam(value = "zip", required = false) String zip) {
        Session session = null;
        Transaction transaction = null;
        Organization organization = new Organization();
        organization.setAddress(new Address(street,city,state,zip));
        organization.setDescription(description);
        organization.setName(name);

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            session.save(organization);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return new ResponseEntity("Internal server error.", HttpStatus.INTERNAL_SERVER_ERROR);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return new ResponseEntity(organization, HttpStatus.OK);
    }

    /** (6) Get a organization<br>
     Path:org/{id}?format={json | xml | html} <br>
     Method: GET <br>
     This returns a full organization object with the given ID in the given format.
     All existing fields, including the optional organization and list of friends should be returned.
     If the organization of the given user ID does not exist, the HTTP return code should be 404; otherwise, 200.
     The format parameter is optional, and the value is case insensitive. If missing, JSON is assumed.

     * @param id			Description of a
     * @param format			Description of b
     * @return			Description of c
     */

    @RequestMapping(value="/{id}", method = RequestMethod.GET)
    public ResponseEntity getOrganization(@PathVariable("id") long id,
                                          @RequestParam(value = "format", required = true) String format) {
        Session session = null;
        Transaction transaction = null;
        Organization organization = null;
        HttpHeaders httpHeaders = new HttpHeaders();

        if ("json".equals(format)) {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        } else if ("xml".equals(format)) {
            httpHeaders.setContentType(MediaType.APPLICATION_XML);
        } else if ("html".equals(format)) {
            httpHeaders.setContentType(MediaType.TEXT_HTML);
        } else {
            httpHeaders.setContentType(MediaType.APPLICATION_JSON);
        }

        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            organization = (Organization)session.get(Organization.class, id);
            if (organization == null) {
                throw new HibernateException("can't find record with id = " + id);
            }
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return new ResponseEntity("can't find record with id = " + id, httpHeaders, HttpStatus.NOT_FOUND);
        } finally {
            if (session != null) {
                session.close();
            }
        }

        return new ResponseEntity(organization, httpHeaders, HttpStatus.OK);
    }


    /** Function and Requirement:
     (7) Update an organization
     Path: org/{id}?name=XX description=YY street=ZZ ...
     Method: POST

     This API updates an organization object.
     For simplicity, all the fields (name, description, street, city, etc), except ID, are passed in as query
     parameters. Only name is required.
     Similar to the get method, the request returns the updated organization object, including all attributes in JSON.
     If the organization ID does not exist, 404 should be returned. If required parameters are missing,
     return 400 instead. Otherwise, return 200.

     * @param id			Description of a
     * @param name 		Description of b
     * @param description			Description of a
     * @param street	    Address of Organization
     * @param city			Address of Organization
     * @param state			Address of Organization
     * @param zip		    Address of Organization
     * @return			Description of c
     */

    @RequestMapping(value="/{id}", method = RequestMethod.POST)
    public ResponseEntity updateOrganization(@PathVariable("id") long id,
                                     @RequestParam(value = "name", required = true) String name,
                                     @RequestParam(value = "description", required = false) String description,
                                     @RequestParam(value = "street", required = false) String street,
                                     @RequestParam(value = "city", required = false) String city,
                                     @RequestParam(value = "state", required = false) String state,
                                     @RequestParam(value = "zip", required = false) String zip) {
        Session session = null;
        Transaction transaction = null;
        Organization organization = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            organization = (Organization)session.get(Organization.class, id);
            if (organization == null) {
                throw new HibernateException("can't find organization with id = " + id);
            }
            organization.setDescription(description);
            organization.setName(name);
            organization.setAddress(new Address(street, city, state, zip));
            session.update(organization);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return new ResponseEntity("can't find organization with id = " + id, HttpStatus.NOT_FOUND);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return new ResponseEntity(organization, HttpStatus.NOT_FOUND);
    }

    /** Delete an organization object
     (8) Delete an organization
     URL: http://org/{id}
     Method: DELETE

     This method deletes the organization object with the given ID.
     If there is still any person belonging to this organization, return 400.
     If the organization with the given ID does not exist, return 404.
     Return HTTP code 200 and the deleted object in JSON if the object is deleted;

     * @param id			Description of a
     * @return			Description of c
     */

    @RequestMapping(value="/{id}", method = RequestMethod.DELETE)
    public ResponseEntity deleteOrganization(@PathVariable("id") long id) {
        Session session = null;
        Transaction transaction = null;
        Organization organization = null;
        try {
            session = sessionFactory.openSession();
            transaction = session.beginTransaction();
            organization = (Organization)session.get(Organization.class, id);
            if(organization == null) {
                throw new HibernateException("Can't find organization record with id = " + id);
            }
            session.delete(organization);
            transaction.commit();
        } catch (HibernateException e) {
            if (transaction != null) {
                transaction.rollback();
            }
            return new ResponseEntity("Can't find organization record with id = " + id, HttpStatus.NOT_FOUND);
        } finally {
            if (session != null) {
                session.close();
            }
        }
        return new ResponseEntity(organization, HttpStatus.OK);
    }


}
