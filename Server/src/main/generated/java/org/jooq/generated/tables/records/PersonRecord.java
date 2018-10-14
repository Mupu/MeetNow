/*
 * This file is generated by jOOQ.
 */
package org.jooq.generated.tables.records;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.generated.tables.Person;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.UInteger;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.11.5"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PersonRecord extends UpdatableRecordImpl<PersonRecord> implements Record5<UInteger, String, String, String, String> {

    private static final long serialVersionUID = -1225093102;

    /**
     * Setter for <code>meetnow.person.PersonId</code>.
     */
    public PersonRecord setPersonid(UInteger value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>meetnow.person.PersonId</code>.
     */
    public UInteger getPersonid() {
        return (UInteger) get(0);
    }

    /**
     * Setter for <code>meetnow.person.Vorname</code>.
     */
    public PersonRecord setVorname(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>meetnow.person.Vorname</code>.
     */
    public String getVorname() {
        return (String) get(1);
    }

    /**
     * Setter for <code>meetnow.person.Nachname</code>.
     */
    public PersonRecord setNachname(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>meetnow.person.Nachname</code>.
     */
    public String getNachname() {
        return (String) get(2);
    }

    /**
     * Setter for <code>meetnow.person.Email</code>.
     */
    public PersonRecord setEmail(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>meetnow.person.Email</code>.
     */
    public String getEmail() {
        return (String) get(3);
    }

    /**
     * Setter for <code>meetnow.person.Token</code>.
     */
    public PersonRecord setToken(String value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>meetnow.person.Token</code>.
     */
    public String getToken() {
        return (String) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<UInteger> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<UInteger, String, String, String, String> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<UInteger, String, String, String, String> valuesRow() {
        return (Row5) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field1() {
        return Person.PERSON.PERSONID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Person.PERSON.VORNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Person.PERSON.NACHNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Person.PERSON.EMAIL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return Person.PERSON.TOKEN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger component1() {
        return getPersonid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getVorname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getNachname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getEmail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getToken();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value1() {
        return getPersonid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getVorname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getNachname();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getEmail();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getToken();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersonRecord value1(UInteger value) {
        setPersonid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersonRecord value2(String value) {
        setVorname(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersonRecord value3(String value) {
        setNachname(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersonRecord value4(String value) {
        setEmail(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersonRecord value5(String value) {
        setToken(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PersonRecord values(UInteger value1, String value2, String value3, String value4, String value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached PersonRecord
     */
    public PersonRecord() {
        super(Person.PERSON);
    }

    /**
     * Create a detached, initialised PersonRecord
     */
    public PersonRecord(UInteger personid, String vorname, String nachname, String email, String token) {
        super(Person.PERSON);

        set(0, personid);
        set(1, vorname);
        set(2, nachname);
        set(3, email);
        set(4, token);
    }
}
