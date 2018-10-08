package me.mupu.server.model;

import org.jooq.generated.tables.records.BenutzerRecord;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.HashSet;

public class CustomUserDetails implements UserDetails {

    private final BenutzerRecord userdata;
    public CustomUserDetails(BenutzerRecord userdata) {
        this.userdata = userdata;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        Collection<GrantedAuthority> collection = new HashSet<>();
        collection.add(new SimpleGrantedAuthority("ROLE_" + (userdata.getRechte().byteValue() == 1 ? "ADMIN" : "USER")));
        collection.forEach(System.out::println);
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
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
