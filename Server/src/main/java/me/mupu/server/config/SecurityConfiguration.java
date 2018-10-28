package me.mupu.server.config;

import me.mupu.server.HashPasswordEncoder;
import me.mupu.server.service.CustomUserService;
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
        securedEnabled = true
//        jsr250Enabled = true,
//        prePostEnabled = true
)
public class SecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Autowired
    private CustomUserService customUserService;

    @Autowired
    HashPasswordEncoder hashPasswordEncoder;

    @Override
    protected void configure(AuthenticationManagerBuilder auth)
            throws Exception {
        auth
                .userDetailsService(customUserService)
                .passwordEncoder(hashPasswordEncoder);
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .authorizeRequests()
                .antMatchers("/", "/home").permitAll()

                .antMatchers("/favicon.ico").permitAll()

                .antMatchers("/registration").permitAll()
                .antMatchers("/confirmation").permitAll()
                .antMatchers("/resendConfirmationEmail").permitAll()

                .antMatchers("/login").permitAll()

                .antMatchers("/forgotCredentials/resetPassword").permitAll()
                .antMatchers("/forgotCredentials/username").permitAll()
                .antMatchers("/forgotCredentials/password").permitAll()
                
                .anyRequest().authenticated()

                .and().formLogin()
                .loginPage("/login").failureUrl("/login?error")
                .defaultSuccessUrl("/home")

                .and().logout()
                .logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
                .logoutSuccessUrl("/login?logout");

//                .and().exceptionHandling().accessDeniedPage("/access-denied");
    }

//    @Override
//    public void configure(WebSecurity web) throws Exception {
//        web
//                .ignoring()
//                .antMatchers("/resources/**", "/static/**", "/css/**", "/js/**", "/images/**");
//    }

}
