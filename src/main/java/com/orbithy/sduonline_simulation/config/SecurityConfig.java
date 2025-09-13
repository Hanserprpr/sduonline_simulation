package com.orbithy.sduonline_simulation.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;

@Configuration
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .requestMatchers("/", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/api/login").permitAll()
                        .requestMatchers("/oauth2/**", "/login/**").permitAll()
                        .anyRequest().authenticated()
                )
                .oauth2Login(Customizer.withDefaults())
                .oauth2Login(o -> o.successHandler(successHandler()))
                .oauth2ResourceServer(o -> o.jwt(Customizer.withDefaults()));

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            String redirect = request.getParameter("redirect_uri");
            if (redirect != null && !redirect.isBlank()) {
                response.sendRedirect(redirect);
            } else {
                response.sendRedirect("http://121.250.208.1:8082/login/callback");
            }
        };
    }

    @Bean
    public JwtDecoder jwtDecoder() {
        // Create a JWT decoder using the JWK Set URI directly
        // This bypasses the issuer-uri auto-configuration that was causing the problem
        return NimbusJwtDecoder
                .withJwkSetUri("https://pass.orbithy.com/.well-known/jwks")
                .build();
    }

    @Bean
    public org.springframework.web.cors.CorsConfigurationSource corsConfigurationSource() {
        var conf = new org.springframework.web.cors.CorsConfiguration();

        // ✅ 写明允许的前端来源（按你的实际情况填写，协议+域名/IP+端口）
        conf.setAllowedOrigins(java.util.List.of(
                "http://localhost:5173",
                "http://localhost:8082",
                "http://121.250.208.1:8082",
                "http://23.158.24.179:8082",
                "https://sduonlinesimulation.pycmg.top",
                "https://sduonlinesimulation.orbithy.com/"
        ));
        // 如果需要支持子域名通配，改用 setAllowedOriginPatterns(List.of("https://*.rpcrpc.com"))

        conf.setAllowedMethods(java.util.List.of("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS"));
        conf.setAllowedHeaders(java.util.List.of("*"));
        conf.setExposedHeaders(java.util.List.of("Authorization", "Location")); // 按需
        conf.setAllowCredentials(true);   // ✅ 需要携带 Cookie/凭证
        conf.setMaxAge(3600L);

        var source = new org.springframework.web.cors.UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", conf);
        return source;
    }

}