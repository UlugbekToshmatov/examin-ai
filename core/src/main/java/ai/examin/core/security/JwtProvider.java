package ai.examin.core.security;

import ai.examin.core.enums.ResponseStatus;
import ai.examin.core.exception_handler.ApiException;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.DecodingException;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import io.jsonwebtoken.security.WeakKeyException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

@Service
public class JwtProvider {
    private static final String SECRET_KEY = "3C63CB44CD9BAD71AEF3B91EFC163WWLKFGPO25MTO36IOY232O3KRM23ROFI";
    private static final long ACCESS_TOKEN_EXPIRATION = 1_800_000;      // 1 800 000 millis = 30 minutes
    private static final long REFRESH_TOKEN_EXPIRATION = 432_000_000;   // 432 000 000 millis = 5 days


    public String generateAccessToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, ACCESS_TOKEN_EXPIRATION);
    }

    public String generateRefreshToken(UserDetails userDetails) {
        return buildToken(new HashMap<>(), userDetails, REFRESH_TOKEN_EXPIRATION);
    }

    private String buildToken(
        Map<String, Object> extraClaims,
        UserDetails userDetails,
        long tokenExpiration
    ) {
        extraClaims.put("userId", ((UserPrincipal) userDetails).getUserPayload().getId());

        return Jwts
            .builder()
            .setClaims(extraClaims)
            .setSubject(userDetails.getUsername())
            .setIssuedAt(new Date(System.currentTimeMillis()))
            .setExpiration(new Date(System.currentTimeMillis() + tokenExpiration))
            .signWith(getSigningKey(), SignatureAlgorithm.HS256)
            .compact();
    }

    public String extractUsernameFromToken(String token) {
        /* return extractClaim(token, claims -> (String) claims.get("sub")); */
        return extractClaim(token, Claims::getSubject);
    }

    public boolean isTokenExpired(String token) {
        return new Date(System.currentTimeMillis()).after(extractClaim(token, Claims::getExpiration));
    }

    private <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        try {
            return Jwts.parserBuilder()
                    .setSigningKey(getSigningKey())
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | SignatureException |
                 IllegalArgumentException e) {
            throw new ApiException(ResponseStatus.INVALID_TOKEN);
        }
    }

    private Key getSigningKey() {
        try {
            byte[] keyBytes = Decoders.BASE64.decode(SECRET_KEY);
            return Keys.hmacShaKeyFor(keyBytes);
        } catch (DecodingException | WeakKeyException e) {
            throw new ApiException(ResponseStatus.TOKEN_PROCESSING_ERROR);
        }
    }
}
