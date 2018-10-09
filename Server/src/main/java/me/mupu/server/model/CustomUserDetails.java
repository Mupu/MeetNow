package me.mupu.server.model;

import lombok.Getter;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.BenutzerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;

public class CustomUserDetails implements UserDetails {

    @Getter
    private final BenutzerRecord userdata;
    private final Collection<GrantedAuthority> collection;
    public CustomUserDetails(BenutzerRecord userdata, Collection<GrantedAuthority> collection) {
        this.userdata = userdata;
        this.collection = collection;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return collection;
    }

    @Override
    public String getPassword() {
        return userdata.getPasswort();
    }

    @Override
    public String getUsername() {
        return userdata.getBenutzername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return userdata.getAccountstatus().byteValue() == 1;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }

}
