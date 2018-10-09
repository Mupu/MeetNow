/*
 * This file is generated by jOOQ.
 */
package org.jooq.generated.tables;


import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
import org.jooq.generated.Indexes;
import org.jooq.generated.Keys;
import org.jooq.generated.Meetnow;
import org.jooq.generated.tables.records.BenutzerRecord;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;
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
public class Benutzer extends TableImpl<BenutzerRecord> {

    private static final long serialVersionUID = 1648316595;

    /**
     * The reference instance of <code>meetnow.benutzer</code>
     */
    public static final Benutzer BENUTZER = new Benutzer();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<BenutzerRecord> getRecordType() {
        return BenutzerRecord.class;
    }

    /**
     * The column <code>meetnow.benutzer.BenutzerId</code>.
     */
    public final TableField<BenutzerRecord, UInteger> BENUTZERID = createField("BenutzerId", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false).identity(true), this, "");

    /**
     * The column <code>meetnow.benutzer.PersonId</code>.
     */
    public final TableField<BenutzerRecord, UInteger> PERSONID = createField("PersonId", org.jooq.impl.SQLDataType.INTEGERUNSIGNED.nullable(false), this, "");

    /**
     * The column <code>meetnow.benutzer.Benutzername</code>.
     */
    public final TableField<BenutzerRecord, String> BENUTZERNAME = createField("Benutzername", org.jooq.impl.SQLDataType.CHAR(50).nullable(false), this, "");

    /**
     * The column <code>meetnow.benutzer.Passwort</code>.
     */
    public final TableField<BenutzerRecord, String> PASSWORT = createField("Passwort", org.jooq.impl.SQLDataType.VARCHAR(551).nullable(false), this, "");

    /**
     * The column <code>meetnow.benutzer.AccountStatus</code>. 0=newDataOnLogin; 1=normal
     */
    public final TableField<BenutzerRecord, UByte> ACCOUNTSTATUS = createField("AccountStatus", org.jooq.impl.SQLDataType.TINYINTUNSIGNED.nullable(false), this, "0=newDataOnLogin; 1=normal");

    /**
     * Create a <code>meetnow.benutzer</code> table reference
     */
    public Benutzer() {
        this(DSL.name("benutzer"), null);
    }

    /**
     * Create an aliased <code>meetnow.benutzer</code> table reference
     */
    public Benutzer(String alias) {
        this(DSL.name(alias), BENUTZER);
    }

    /**
     * Create an aliased <code>meetnow.benutzer</code> table reference
     */
    public Benutzer(Name alias) {
        this(alias, BENUTZER);
    }

    private Benutzer(Name alias, Table<BenutzerRecord> aliased) {
        this(alias, aliased, null);
    }

    private Benutzer(Name alias, Table<BenutzerRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""));
    }

    public <O extends Record> Benutzer(Table<O> child, ForeignKey<O, BenutzerRecord> key) {
        super(child, key, BENUTZER);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Meetnow.MEETNOW;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.BENUTZER_BENUTZERNAME, Indexes.BENUTZER_BENUTZER_FKINDEX1, Indexes.BENUTZER_PRIMARY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<BenutzerRecord, UInteger> getIdentity() {
        return Keys.IDENTITY_BENUTZER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<BenutzerRecord> getPrimaryKey() {
        return Keys.KEY_BENUTZER_PRIMARY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<BenutzerRecord>> getKeys() {
        return Arrays.<UniqueKey<BenutzerRecord>>asList(Keys.KEY_BENUTZER_PRIMARY, Keys.KEY_BENUTZER_BENUTZERNAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<ForeignKey<BenutzerRecord, ?>> getReferences() {
        return Arrays.<ForeignKey<BenutzerRecord, ?>>asList(Keys.BENUTZER_IBFK_1);
    }

    public Person person() {
        return new Person(this, Keys.BENUTZER_IBFK_1);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Benutzer as(String alias) {
        return new Benutzer(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Benutzer as(Name alias) {
        return new Benutzer(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public Benutzer rename(String name) {
        return new Benutzer(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Benutzer rename(Name name) {
        return new Benutzer(name, null);
    }
}
