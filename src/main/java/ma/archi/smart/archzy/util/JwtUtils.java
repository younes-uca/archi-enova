package ma.archi.smart.archzy.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Date;

@Component
public class JwtUtils implements Serializable {
    private static final long serialVersionUID = 9036803203940273082L;

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.bearer-token}")
    private Boolean isBearerToken;

    private Date generateExpirationDate() {
        return new Date(System.currentTimeMillis() + expiration * 1000);
    }

    public String getUsernameFromToken(String token) {
        String username;
        try {
            Claims claims = getClaimsFromToken(token);
            username = claims.getSubject();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    public Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            if (token.startsWith("Bearer "))
                token = token.substring(7);
            // @formatter:off
            claims = Jwts.parser().setSigningKey(secret).parseClaimsJws(token).getBody();
            // @formatter:on
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    public Boolean validateTokenFormat(String token) {
        final String username = getUsernameFromToken(token);
        return username != null && !username.trim().isEmpty();
    }

    public Boolean isTokenMalformed(String token) {
        return isBearerToken && !token.startsWith("Bearer ");
    }

    public Boolean validateToken(String token, UserDetails userDetails) {
        User user = (User) userDetails;
        final String username = getUsernameFromToken(token);
        return (username.equals(user.getUsername()) && !isTokenExpired(token));
    }
}