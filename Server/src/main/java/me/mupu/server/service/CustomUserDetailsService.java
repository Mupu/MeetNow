package me.mupu.server.service;

import me.mupu.server.model.CustomUserDetails;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.BenutzerRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import static org.jooq.generated.Tables.*;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private DSLContext dslContext;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        BenutzerRecord userdata = dslContext
                .selectFrom(BENUTZER)
                .where(BENUTZER.BENUTZERNAME.eq(username))
                .fetchAny();

        if (userdata == null)
            throw new UsernameNotFoundException("Username not found!");
        return new CustomUserDetails(userdata);
    }
}
