package ma.enset.jwtauthlab.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.Map;
import java.util.UUID;

@Service
public class JwtService {

    private final Key key;
    private final long expirationMs;
    private final String issuer; // Ajout de l'émetteur

    public JwtService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.expiration-ms}") long expirationMs,
            @Value("${app.jwt.issuer}") String issuer
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
        this.expirationMs = expirationMs;
        this.issuer = issuer;
    }

    public String generateToken(String username, Map<String, Object> claims) {
        Date now = new Date();
        Date exp = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(now)
                .setExpiration(exp)
                .setIssuer(issuer)
                .setId(UUID.randomUUID().toString())
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }

    public String extractUsername(String token) {
        return parse(token).getBody().getSubject();
    }

    public boolean isTokenValid(String token, String username) {
        try {
            Claims claims = parse(token).getBody();
            // Vérification du nom d'utilisateur, de l'émetteur et de l'expiration
            return claims.getSubject().equals(username)
                    && claims.getIssuer().equals(this.issuer)
                    && !isExpired(claims.getExpiration());
        } catch (JwtException e) {
            // Le token est invalide (signature, format, etc.)
            return false;
        }
    }

    private boolean isExpired(Date expirationDate) {
        return expirationDate.before(new Date());
    }

    private Jws<Claims> parse(String token) {
        // Le parser vérifie automatiquement la signature et l'expiration
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .requireIssuer(this.issuer) // Exige que l'émetteur soit correct
                .build()
                .parseClaimsJws(token);
    }
}
