/*
 * This file is generated by jOOQ.
 */
package org.jooq.generated.tables.records;


import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.generated.tables.UserRole;
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
public class UserRoleRecord extends UpdatableRecordImpl<UserRoleRecord> implements Record2<UInteger, UInteger> {

    private static final long serialVersionUID = 1436765025;

    /**
     * Setter for <code>meetnow.user_role.BenutzerId</code>.
     */
    public UserRoleRecord setBenutzerid(UInteger value) {
        set(0, value);
        return this;
    }

    /**
     * Getter for <code>meetnow.user_role.BenutzerId</code>.
     */
    public UInteger getBenutzerid() {
        return (UInteger) get(0);
    }

    /**
     * Setter for <code>meetnow.user_role.RoleId</code>.
     */
    public UserRoleRecord setRoleid(UInteger value) {
        set(1, value);
        return this;
    }

    /**
     * Getter for <code>meetnow.user_role.RoleId</code>.
     */
    public UInteger getRoleid() {
        return (UInteger) get(1);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record2<UInteger, UInteger> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<UInteger, UInteger> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row2<UInteger, UInteger> valuesRow() {
        return (Row2) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field1() {
        return UserRole.USER_ROLE.BENUTZERID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<UInteger> field2() {
        return UserRole.USER_ROLE.ROLEID;
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
        return getRoleid();
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
        return getRoleid();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserRoleRecord value1(UInteger value) {
        setBenutzerid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserRoleRecord value2(UInteger value) {
        setRoleid(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UserRoleRecord values(UInteger value1, UInteger value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached UserRoleRecord
     */
    public UserRoleRecord() {
        super(UserRole.USER_ROLE);
    }

    /**
     * Create a detached, initialised UserRoleRecord
     */
    public UserRoleRecord(UInteger benutzerid, UInteger roleid) {
        super(UserRole.USER_ROLE);

        set(0, benutzerid);
        set(1, roleid);
    }
}