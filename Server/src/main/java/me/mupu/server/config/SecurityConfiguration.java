package me.mupu.server.config;

import me.mupu.server.HashPasswordEncoder;
import me.mupu.server.service.CustomUserDetailsService;
import org.jooq.DSLContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;


@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {
//    @Autowired
//    private BCryptPasswordEncoder bCryptPasswordEncoder;

    @Autowired
    private DSLContext dslContext;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Autowired
    HashPasswordEncoder hashPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth
                .userDetailsService(customUserDetailsService)
                .passwordEncoder(hashPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .anyRequest().authenticated()
//                .and()
//                .formLogin();
//        http.
//                authorizeRequests()
                .antMatchers("/", "/home").anonymous()
//                .antMatchers("/login").permitAll()
//                .antMatchers("/loggedout").permitAll()
////                .antMatchers("/registration").permitAll()
//                .antMatchers("/admin").hasRole("ADMIN")
//                .antMatchers("/admin/**").hasRole("ADMIN")
//                .anyRequest().authenticated()
//                .anyRequest().hasAnyRole("USER", "ADMIN")
//                .antMatchers("/admin/**").hasAuthority("ADMIN").anyRequest()
////                .authenticated()
                .and().formLogin()
//                .loginPage("/login").failureUrl("/login?error=true")
                .defaultSuccessUrl("/test")
////                .usernameParameter("username")
////                .passwordParameter("password")
                .and().logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/loggedout");
////                .and().exceptionHandling().accessDeniedPage("/access-denied");
    }

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web
//                .ignoring()
//                .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
//    }

}
