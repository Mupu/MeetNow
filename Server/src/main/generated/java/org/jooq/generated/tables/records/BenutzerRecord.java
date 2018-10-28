/*
 * This file is generated by jOOQ.
 */
package org.jooq.generated.tables.records;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record6;
import org.jooq.Row6;
import org.jooq.generated.tables.Benutzer;
import org.jooq.impl.UpdatableRecordImpl;
import org.jooq.types.UByte;
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
public class BenutzerRecord extends UpdatableRecordImpl<BenutzerRecord> implements Record6<UInteger, UInteger, String, String, UByte, String> {

    private static final long serialVersionUID = -254383982;

    /**
     * Setter for <code>meetnow.benutzer.BenutzerId</code>.
     */
    public BenutzerRecord setBenutzerid(UInteger value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>meetnow.benutzer.BenutzerId</code>.
     */
    public UInteger getBenutzerid() {
        return (UInteger) get(0);
    }

    /**
     * Setter for <code>meetnow.benutzer.PersonId</code>.
     */
    public BenutzerRecord setPersonid(UInteger value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>meetnow.benutzer.PersonId</code>.
     */
    public UInteger getPersonid() {
        return (UInteger) get(1);
    }

    /**
     * Setter for <code>meetnow.benutzer.Benutzername</code>.
     */
    public BenutzerRecord setBenutzername(String value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>meetnow.benutzer.Benutzername</code>.
     */
    public String getBenutzername() {
        return (String) get(2);
    }

    /**
     * Setter for <code>meetnow.benutzer.Passwort</code>.
     */
    public BenutzerRecord setPasswort(String value) {
        set(3, value);
        return this;
    }

    /**
     * Getter for <code>meetnow.benutzer.Passwort</code>.
     */
    public String getPasswort() {
        return (String) get(3);
    }

    /**
     * Setter for <code>meetnow.benutzer.IsEnabled</code>.
     */
    public BenutzerRecord setIsenabled(UByte value) {
        set(4, value);
        return this;
    }

    /**
     * Getter for <code>meetnow.benutzer.IsEnabled</code>.
     */
    public UByte getIsenabled() {
        return (UByte) get(4);
    }

    /**
     * Setter for <code>meetnow.benutzer.ResetPasswordToken</code>.
     */
    public BenutzerRecord setResetpasswordtoken(String value) {
        set(5, value);
        return this;
    }

    /**
     * Getter for <code>meetnow.benutzer.ResetPasswordToken</code>.
     */
    public String getResetpasswordtoken() {
        return (String) get(5);
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
    // Record6 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<UInteger, UInteger, String, String, UByte, String> fieldsRow() {
        return (Row6) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row6<UInteger, UInteger, String, String, UByte, String> valuesRow() {
        return (Row6) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field1() {
        return Benutzer.BENUTZER.BENUTZERID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field2() {
        return Benutzer.BENUTZER.PERSONID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return Benutzer.BENUTZER.BENUTZERNAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field4() {
        return Benutzer.BENUTZER.PASSWORT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UByte> field5() {
        return Benutzer.BENUTZER.ISENABLED;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field6() {
        return Benutzer.BENUTZER.RESETPASSWORDTOKEN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger component1() {
        return getBenutzerid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger component2() {
        return getPersonid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getBenutzername();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component4() {
        return getPasswort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UByte component5() {
        return getIsenabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component6() {
        return getResetpasswordtoken();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value1() {
        return getBenutzerid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value2() {
        return getPersonid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getBenutzername();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value4() {
        return getPasswort();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UByte value5() {
        return getIsenabled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value6() {
        return getResetpasswordtoken();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BenutzerRecord value1(UInteger value) {
        setBenutzerid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BenutzerRecord value2(UInteger value) {
        setPersonid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BenutzerRecord value3(String value) {
        setBenutzername(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BenutzerRecord value4(String value) {
        setPasswort(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BenutzerRecord value5(UByte value) {
        setIsenabled(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BenutzerRecord value6(String value) {
        setResetpasswordtoken(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public BenutzerRecord values(UInteger value1, UInteger value2, String value3, String value4, UByte value5, String value6) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached BenutzerRecord
     */
    public BenutzerRecord() {
        super(Benutzer.BENUTZER);
    }

    /**
     * Create a detached, initialised BenutzerRecord
     */
    public BenutzerRecord(UInteger benutzerid, UInteger personid, String benutzername, String passwort, UByte isenabled, String resetpasswordtoken) {
        super(Benutzer.BENUTZER);

        set(0, benutzerid);
        set(1, personid);
        set(2, benutzername);
        set(3, passwort);
        set(4, isenabled);
        set(5, resetpasswordtoken);
    }
}
