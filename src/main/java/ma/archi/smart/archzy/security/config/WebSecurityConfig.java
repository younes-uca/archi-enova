package ma.archi.smart.archzy.security.config;

import ma.archi.smart.archzy.security.common.AuthoritiesConstants;
import ma.archi.smart.archzy.security.jwt.JWTAuthenticationFilter;
import ma.archi.smart.archzy.security.jwt.JWTAuthorizationFiler;
import ma.archi.smart.archzy.security.service.facade.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
prePostEnabled = true,
securedEnabled = true,
jsr250Enabled = true)
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

    @Autowired
    private UserService userService;

    @Autowired
    private PasswordEncoder bCryptPasswordEncoder;
    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception {
        auth.userDetailsService(userService).passwordEncoder(bCryptPasswordEncoder);
    }


    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.csrf().disable();
        http.sessionManagement().sessionCreationPolicy(SessionCreationPolicy.STATELESS);
        http.authorizeRequests().antMatchers("/login").permitAll();
        http.authorizeRequests().antMatchers("/actuator/health").permitAll();
        http.authorizeRequests().antMatchers("/actuator/info").permitAll();

            http.authorizeRequests().antMatchers("/api/admin/login").permitAll();
            http.authorizeRequests().antMatchers("/api/patient/login").permitAll();
            http.authorizeRequests().antMatchers("/api/superviseur/login").permitAll();
            http.authorizeRequests().antMatchers("/api/admin/").hasAnyAuthority(AuthoritiesConstants.ADMIN);
            http.authorizeRequests().antMatchers("/api/patient/").hasAnyAuthority(AuthoritiesConstants.PATIENT);
            http.authorizeRequests().antMatchers("/api/superviseur/").hasAnyAuthority(AuthoritiesConstants.SUPERVISEUR);

        // http.authorizeRequests().anyRequest().authenticated();

        /* http.authorizeRequests().anyRequest()
        .authenticated()
        .and()
        .httpBasic();*/

        // http.formLogin();
        // http.authorizeRequests().anyRequest().permitAll();
        http.addFilter(new JWTAuthenticationFilter(authenticationManager()));
        http.addFilterBefore(new JWTAuthorizationFiler(), UsernamePasswordAuthenticationFilter.class);
    }

    @Bean
    public PasswordEncoder encoder(){
        return new BCryptPasswordEncoder();
    }

}
