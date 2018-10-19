package me.mupu.server.service;

import me.mupu.server.model.CustomUser;
import org.jooq.DSLContext;
import org.jooq.generated.tables.records.BenutzerRecord;
import org.jooq.generated.tables.records.PersonRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import static org.jooq.generated.Tables.*;

@Service
public class CustomUserService implements UserDetailsService {

    @Autowired
    private DSLContext dslContext;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        // get user
        BenutzerRecord userdata = dslContext
                .selectFrom(BENUTZER)
                .where(BENUTZER.BENUTZERNAME.eq(username))
                .fetchAny();

        if (userdata == null)
            throw new UsernameNotFoundException("Username not found!");

        // load roles of this user
        Collection<GrantedAuthority> collection = new HashSet<>();
        List<String> roles = dslContext.select(ROLE.NAME)
                .from(USER_ROLE).leftJoin(ROLE).using(ROLE.ROLEID)
                .where(USER_ROLE.BENUTZERID.eq(userdata.getBenutzerid()))
                .fetch(ROLE.NAME);
        roles.forEach(r -> collection.add(new SimpleGrantedAuthority("ROLE_" + r)));
        return new CustomUser(userdata, collection);
    }

    public PersonRecord findUserByConfimationToken(String confirmationToken) {
        if (confirmationToken == null || confirmationToken.equals(""))
            return null;
        // find user
        return dslContext.selectFrom(PERSON)
                .where(PERSON.TOKEN.eq(confirmationToken))
                .fetchAny();
    }


}
