package com.echo.echo.security.jwt;

import com.echo.echo.domain.user.entity.User;
import com.echo.echo.security.principal.UserPrincipal;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.server.reactive.ServerHttpRequest;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.AuthorityUtils;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

@Component
public class JwtProvider {
    private Key key;
    public static final String HEADER_PREFIX = "Bearer ";
    private final String AUTHORITIES_KEY = "auth";
    private final String ID_KEY = "id";
    private final String NICKNAME_KEY = "nickname";
    @Value("${jwt.time.access}")
    private Long ACCESS_TOKEN_TIME;
    @Value("${jwt.time.refresh}")
    private Long REFRESH_TOKEN_TIME;

    public JwtProvider(@Value("${jwt.secret}") String secret) {
        byte[] byteKey = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(byteKey);
    }

    public String createAccessToken(Long id, String email, String nickname) {
        Date now = new Date();
        return Jwts.builder()
                .subject(email)
                .claims()
                .add(AUTHORITIES_KEY, AuthorityUtils.NO_AUTHORITIES)
                .add(ID_KEY, id)
                .add(NICKNAME_KEY, nickname)
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_TIME))
                .issuedAt(now)
                .and()
                .signWith(key)
                .compact();
    }

    public String createRefreshToken() {
        Date now = new Date();
        return Jwts.builder()
                .claims()
                .expiration(new Date(now.getTime() + REFRESH_TOKEN_TIME))
                .issuedAt(now)
                .and()
                .signWith(key)
                .compact();
    }

    public Mono<Token> createToken(Long id, String email, String nickname) {
        return Mono.fromCallable(() -> createAccessToken(id, email, nickname))
                .zipWith(Mono.fromCallable(this::createRefreshToken))
                .map(tuple -> Token.builder()
                        .accessToken(tuple.getT1())
                        .refreshToken(tuple.getT2())
                        .build()
                );
    }

    public boolean isValidToken(String token) {
        try {
            Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token);
            return true;
        } catch (SecurityException | MalformedJwtException e) {
            throw new SecurityException(e.getMessage());
        } catch (ExpiredJwtException e) {
            throw new ExpiredJwtException(e.getHeader(), e.getClaims(), e.getMessage());
        } catch (UnsupportedJwtException e) {
            throw new UnsupportedJwtException(e.getMessage());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException(e);
        }
    }

    public Authentication getAuthentication(String token) {
        Claims claims = getClaims(token);

        Object authoritiesClaim = claims.get(AUTHORITIES_KEY);

        Collection<? extends GrantedAuthority> authorities = authoritiesClaim == null ? AuthorityUtils.NO_AUTHORITIES
                : AuthorityUtils.commaSeparatedStringToAuthorityList(authoritiesClaim.toString());

        User user = User.builder()
                .id(Long.valueOf((Integer) claims.get(ID_KEY)))
                .email(claims.getSubject())
                .nickname((String) claims.get(NICKNAME_KEY))
                .build();

        return new UsernamePasswordAuthenticationToken(new UserPrincipal(user), null, authorities);
    }

    public Mono<String> resolveToken(ServerHttpRequest request) {
        return Mono.justOrEmpty(request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION))
                .flatMap(token -> {
                    if (token.startsWith(HEADER_PREFIX)) {
                        return Mono.just(token.substring(HEADER_PREFIX.length()));
                    }
                    return Mono.empty();
                });
    }

    public Claims getClaims(String token) {
        return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token).getPayload();
    }

    public Long getRefreshTokenTime() {
        return REFRESH_TOKEN_TIME;
    }

    public String getNickName(String token){
        return getClaims(token).get(NICKNAME_KEY).toString();
    }

    public Long getUserId(String token){
        return Long.valueOf((Integer) getClaims(token).get(ID_KEY));
    }
}
