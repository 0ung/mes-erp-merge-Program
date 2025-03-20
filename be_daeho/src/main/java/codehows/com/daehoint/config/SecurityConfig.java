package codehows.com.daehoint.config;

import codehows.com.daehoint.config.jwt.JwtAccessDeniedHandler;
import codehows.com.daehoint.config.jwt.JwtAuthenticationEntryPoint;
import codehows.com.daehoint.config.jwt.JwtFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.CorsUtils;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import static org.springframework.security.web.util.matcher.AntPathRequestMatcher.antMatcher;


/**
 * <b>SecurityConfig 클래스</b><br>
 * 애플리케이션의 보안 설정을 관리하는 클래스입니다. JWT 인증 및 권한 관리를 포함하여 HTTP 요청에 대한 보안 정책을 정의합니다.<br><br>
 *
 * <b>주요 기능:</b><br>
 * - JWT 인증 필터 및 예외 처리 핸들러 구성.<br>
 * - CORS 설정 및 Stateless 세션 관리.<br>
 * - 특정 요청 경로에 대한 권한 제어.<br><br>
 *
 * <b>핵심 구성 요소:</b><br>
 * 1. <b>`PasswordEncoder`</b>:<br>
 *    - `BCryptPasswordEncoder`를 사용하여 비밀번호를 암호화.<br>
 * 2. <b>`SecurityFilterChain`</b>:<br>
 *    - HTTP 보안 설정을 정의.<br>
 *    - JWT 필터(`JwtFilter`)를 `UsernamePasswordAuthenticationFilter` 이전에 추가.<br>
 *    - 인증 실패 시 `JwtAuthenticationEntryPoint`, 접근 거부 시 `JwtAccessDeniedHandler` 처리.<br>
 *    - 권한에 따른 요청 경로 접근 제어 설정.<br>
 * 3. <b>`CorsConfigurationSource`</b>:<br>
 *    - CORS 설정을 정의.<br>
 *    - 허용된 Origin, Method, Header 설정.<br>
 *    - <b>해당 설정은 개발환경에서만 사용 사용하지 않을때는 주석처리(배포 환경에서 유지할 경우 오류발생)</b> <br>
 *
 * <b>주요 HTTP 보안 설정:</b><br>
 * - <b>CORS</b>: `CorsConfigurationSource`를 기반으로 설정.<br>
 * - <b>CSRF</b>: 비활성화.<br>
 * - <b>Form Login 및 Logout</b>: 비활성화.<br>
 * - <b>세션 관리</b>: Stateless 정책 적용.<br>
 * - <b>익명 사용자 접근</b>: 비활성화.<br>
 * - <b>예외 처리</b>: 인증 및 권한 예외 처리 핸들러 설정.<br>
 * - <b>권한 제어</b>:<br>
 *   - `/api/mail/**`: 모든 사용자 허용.<br>
 *   - `/member/login`, `/member/refresh`, `/manage/signup`: 모든 사용자 허용.<br>
 *   - `/manage/**`: `ADMIN` 권한 사용자만 허용.<br>
 *   - 기타 경로: 인증된 사용자만 허용.<br><br>
 *
 * <b>주요 어노테이션:</b><br>
 * - <b>@Configuration</b>: 스프링 설정 클래스임을 나타냄.<br>
 * - <b>@EnableWebSecurity</b>: Spring Security를 활성화.<br>
 * - <b>@RequiredArgsConstructor</b>: 의존성 주입을 위한 Lombok 어노테이션.<br><br>
 *
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtFilter jwtFilter;
    private final JwtAccessDeniedHandler jwtAccessDeniedHandler = new JwtAccessDeniedHandler();
    private final JwtAuthenticationEntryPoint authenticationEntryPoint = new JwtAuthenticationEntryPoint();

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.httpBasic(AbstractHttpConfigurer::disable)
//                .cors(cors -> {
//                    cors.configurationSource(corsConfigurationSource());
//                })
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(
                        logout -> logout.clearAuthentication(true)
                )
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .anonymous(AbstractHttpConfigurer::disable)
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .exceptionHandling(exceptionHandler -> {
                    exceptionHandler.defaultAuthenticationEntryPointFor(
                                    new HttpStatusEntryPoint(HttpStatus.UNAUTHORIZED),
                                    new AntPathRequestMatcher("/**"))
                            .accessDeniedHandler(jwtAccessDeniedHandler)
                            .authenticationEntryPoint(authenticationEntryPoint);
                })
                .authorizeHttpRequests(
                        request -> {
                            request.requestMatchers(CorsUtils::isPreFlightRequest).permitAll()
                                    .requestMatchers(antMatcher("/api/mail/**")).permitAll()
                                    .requestMatchers(antMatcher("/member/login")).permitAll()
                                    .requestMatchers(antMatcher("/member/refresh")).permitAll()
                                    .requestMatchers(antMatcher("/manage/signup")).permitAll()
                                    .requestMatchers(antMatcher("/manage/**")).hasAnyRole("ADMIN")
                                    .anyRequest().authenticated();
                        }
                );
        return http.build();
    }

//    @Bean
//    public CorsConfigurationSource corsConfigurationSource() {
//        CorsConfiguration configuration = new CorsConfiguration();
//        configuration.addAllowedOrigin("http://localhost:5173/");
//        configuration.addAllowedMethod("*");
//        configuration.addAllowedHeader("*");
//        configuration.setMaxAge(86400L);
//        configuration.setAllowCredentials(true);
//        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
//        source.registerCorsConfiguration("/**", configuration);
//        return source;
//    }
}
