/*
 * This file is generated by jOOQ.
 */
package org.jooq.generated.tables.records;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.generated.tables.Ausstattungsgegenstand;
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
public class AusstattungsgegenstandRecord extends UpdatableRecordImpl<AusstattungsgegenstandRecord> implements Record3<UInteger, String, UInteger> {

    private static final long serialVersionUID = 489730050;

    /**
     * Setter for <code>meetnow.ausstattungsgegenstand.AusstattungsgegenstandId</code>.
     */
    public AusstattungsgegenstandRecord setAusstattungsgegenstandid(UInteger value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>meetnow.ausstattungsgegenstand.AusstattungsgegenstandId</code>.
     */
    public UInteger getAusstattungsgegenstandid() {
        return (UInteger) get(0);
    }

    /**
     * Setter for <code>meetnow.ausstattungsgegenstand.Name</code>.
     */
    public AusstattungsgegenstandRecord setName(String value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>meetnow.ausstattungsgegenstand.Name</code>.
     */
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>meetnow.ausstattungsgegenstand.Anzahl</code>.
     */
    public AusstattungsgegenstandRecord setAnzahl(UInteger value) {
        set(2, value);
        return this;
    }

    /**
     * Getter for <code>meetnow.ausstattungsgegenstand.Anzahl</code>.
     */
    public UInteger getAnzahl() {
        return (UInteger) get(2);
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
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<UInteger, String, UInteger> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<UInteger, String, UInteger> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field1() {
        return Ausstattungsgegenstand.AUSSTATTUNGSGEGENSTAND.AUSSTATTUNGSGEGENSTANDID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return Ausstattungsgegenstand.AUSSTATTUNGSGEGENSTAND.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field3() {
        return Ausstattungsgegenstand.AUSSTATTUNGSGEGENSTAND.ANZAHL;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger component1() {
        return getAusstattungsgegenstandid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger component3() {
        return getAnzahl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value1() {
        return getAusstattungsgegenstandid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UInteger value3() {
        return getAnzahl();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AusstattungsgegenstandRecord value1(UInteger value) {
        setAusstattungsgegenstandid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AusstattungsgegenstandRecord value2(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AusstattungsgegenstandRecord value3(UInteger value) {
        setAnzahl(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public AusstattungsgegenstandRecord values(UInteger value1, String value2, UInteger value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached AusstattungsgegenstandRecord
     */
    public AusstattungsgegenstandRecord() {
        super(Ausstattungsgegenstand.AUSSTATTUNGSGEGENSTAND);
    }

    /**
     * Create a detached, initialised AusstattungsgegenstandRecord
     */
    public AusstattungsgegenstandRecord(UInteger ausstattungsgegenstandid, String name, UInteger anzahl) {
        super(Ausstattungsgegenstand.AUSSTATTUNGSGEGENSTAND);

        set(0, ausstattungsgegenstandid);
        set(1, name);
        set(2, anzahl);
    }
}