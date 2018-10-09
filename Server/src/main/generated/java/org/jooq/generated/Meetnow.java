/*
 * This file is generated by jOOQ.
 */
package org.jooq.generated;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Catalog;
import org.jooq.Table;
import org.jooq.generated.tables.Ausleihe;
import org.jooq.generated.tables.Ausstattungsgegenstand;
import org.jooq.generated.tables.Benutzer;
import org.jooq.generated.tables.Besprechung;
import org.jooq.generated.tables.Person;
import org.jooq.generated.tables.Raum;
import org.jooq.generated.tables.Role;
import org.jooq.generated.tables.Teilnahme;
import org.jooq.generated.tables.UserRole;
import org.jooq.impl.SchemaImpl;


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
public class Meetnow extends SchemaImpl {

    private static final long serialVersionUID = 599126282;

    /**
     * The reference instance of <code>meetnow</code>
     */
    public static final Meetnow MEETNOW = new Meetnow();

    /**
     * The table <code>meetnow.ausleihe</code>.
     */
    public final Ausleihe AUSLEIHE = org.jooq.generated.tables.Ausleihe.AUSLEIHE;

    /**
     * The table <code>meetnow.ausstattungsgegenstand</code>.
     */
    public final Ausstattungsgegenstand AUSSTATTUNGSGEGENSTAND = org.jooq.generated.tables.Ausstattungsgegenstand.AUSSTATTUNGSGEGENSTAND;

    /**
     * The table <code>meetnow.benutzer</code>.
     */
    public final Benutzer BENUTZER = org.jooq.generated.tables.Benutzer.BENUTZER;

    /**
     * The table <code>meetnow.besprechung</code>.
     */
    public final Besprechung BESPRECHUNG = org.jooq.generated.tables.Besprechung.BESPRECHUNG;

    /**
     * The table <code>meetnow.person</code>.
     */
    public final Person PERSON = org.jooq.generated.tables.Person.PERSON;

    /**
     * The table <code>meetnow.raum</code>.
     */
    public final Raum RAUM = org.jooq.generated.tables.Raum.RAUM;

    /**
     * The table <code>meetnow.role</code>.
     */
    public final Role ROLE = org.jooq.generated.tables.Role.ROLE;

    /**
     * The table <code>meetnow.teilnahme</code>.
     */
    public final Teilnahme TEILNAHME = org.jooq.generated.tables.Teilnahme.TEILNAHME;

    /**
     * The table <code>meetnow.user_role</code>.
     */
    public final UserRole USER_ROLE = org.jooq.generated.tables.UserRole.USER_ROLE;

    /**
     * No further instances allowed
     */
    private Meetnow() {
        super("meetnow", null);
    }


    /**
     * {@inheritDoc}
     */
    @Override
    public Catalog getCatalog() {
        return DefaultCatalog.DEFAULT_CATALOG;
    }

    @Override
    public final List<Table<?>> getTables() {
        List result = new ArrayList();
        result.addAll(getTables0());
        return result;
    }

    private final List<Table<?>> getTables0() {
        return Arrays.<Table<?>>asList(
            Ausleihe.AUSLEIHE,
            Ausstattungsgegenstand.AUSSTATTUNGSGEGENSTAND,
            Benutzer.BENUTZER,
            Besprechung.BESPRECHUNG,
            Person.PERSON,
            Raum.RAUM,
            Role.ROLE,
            Teilnahme.TEILNAHME,
            UserRole.USER_ROLE);
    }
}