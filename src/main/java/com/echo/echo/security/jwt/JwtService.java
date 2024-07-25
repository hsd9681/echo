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
import org.springframework.util.StringUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Base64;
import java.util.Collection;
import java.util.Date;

@Component
public class JwtService {
    private Key key;
    public static final String HEADER_PREFIX = "Bearer ";
    private final String AUTHORITIES_KEY = "auth";
    private final String ID_KEY = "id";
    @Value("${jwt.time.access}")
    private Long ACCESS_TOKEN_TIME;
    @Value("${jwt.time.refresh}")
    private Long REFRESH_TOKEN_TIME;

    public JwtService(@Value("${jwt.secret}") String secret) {
        byte[] byteKey = Base64.getDecoder().decode(secret);
        this.key = Keys.hmacShaKeyFor(byteKey);
    }

    public String createToken(Long id, String username) {
        Date now = new Date();
        return Jwts.builder()
                .subject(username)
                .claims()
                .add(AUTHORITIES_KEY, AuthorityUtils.NO_AUTHORITIES)
                .add(ID_KEY, id)
                .expiration(new Date(now.getTime() + ACCESS_TOKEN_TIME))
                .issuedAt(now)
                .and()
                .signWith(key)
                .compact();
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
                .build();

        return new UsernamePasswordAuthenticationToken(new UserPrincipal(user), token, authorities);
    }

    public String resolveToken(ServerHttpRequest request) {
        String token = request.getHeaders().getFirst(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(token) && token.startsWith(HEADER_PREFIX)) {
            return token.substring(HEADER_PREFIX.length()).trim();
        }
        return null;
    }

    private Claims getClaims(String token) {
        return Jwts.parser().verifyWith((SecretKey) key).build().parseSignedClaims(token).getPayload();
    }
}
