package yangfawu.eroster.config;

import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AnonymousAuthenticationFilter;
import org.springframework.security.web.authentication.AuthenticationFailureHandler;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.security.web.util.matcher.NegatedRequestMatcher;
import org.springframework.security.web.util.matcher.OrRequestMatcher;
import org.springframework.security.web.util.matcher.RequestMatcher;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
@Log4j2
public class WebSecurityConfig {

    private static final RequestMatcher PRIVATE_URLS = new OrRequestMatcher(
        new AntPathRequestMatcher("/api/private/**")
    );
    private static final RequestMatcher PUBLIC_URLS = new NegatedRequestMatcher(PRIVATE_URLS);

    private static TokenAuthenticationFilter RESTAuthenticationFilter; // acts like a bean
    private final TokenAuthenticationProvider tokenAuthProvider;


    @Autowired
    public WebSecurityConfig(
            TokenAuthenticationProvider tokenAuthProvider) {
        this.tokenAuthProvider = tokenAuthProvider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        if (RESTAuthenticationFilter == null) {
            RESTAuthenticationFilter = new TokenAuthenticationFilter(PRIVATE_URLS, authenticationManager());
            RESTAuthenticationFilter.setAuthenticationSuccessHandler(authSuccessHandler());
            RESTAuthenticationFilter.setAuthenticationFailureHandler(authFailureHandler());
        }
        http
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and()
                .exceptionHandling()
                .defaultAuthenticationEntryPointFor(forbiddenEntryPoint(), PRIVATE_URLS)
            .and()
                .authenticationProvider(tokenAuthProvider)
                .addFilterBefore(RESTAuthenticationFilter, AnonymousAuthenticationFilter.class)
                .authorizeRequests()
                .requestMatchers(PUBLIC_URLS).permitAll()
                .requestMatchers(PRIVATE_URLS).authenticated()
            .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .logout().disable();
        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new ProviderManager(Arrays.asList(tokenAuthProvider));
    }

    @Bean
    public SimpleUrlAuthenticationSuccessHandler authSuccessHandler() {
        SimpleUrlAuthenticationSuccessHandler successHandler = new SimpleUrlAuthenticationSuccessHandler();
        successHandler.setRedirectStrategy((request, response, url) -> {
            // INTENTIONALLY LEFT BLANK FOR NO REDIRECT
        });
        return successHandler;
    }

    /**
     * Triggers when a request is made to a secure channel without a proper token.
     */
    @Bean
    public AuthenticationFailureHandler authFailureHandler() {
        return (request, response, exception) -> {
            log.info("{} :: {}", request.getRequestURL(), exception.getMessage());
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, exception.getMessage());
        };
    }

    @Bean
    public AuthenticationEntryPoint forbiddenEntryPoint() {
        return new HttpStatusEntryPoint(HttpStatus.FORBIDDEN);
    }

//    @Bean
//    public AuthenticationEntryPoint authenticationEntryPoint() {
//        return (request, response, authException) -> {
//            System.out.println("authenticationEntryPoint");
//        };
//    }
//
//    @Bean
//    public AccessDeniedHandler accessDeniedHandler() {
//        return (request, response, accessDeniedException) -> {
//            System.out.println("accessDeniedHandler");
//        };
//    }

}
