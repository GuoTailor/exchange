package com.exchange.util;

import com.exchange.domain.Role;
import com.exchange.domain.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by gyh on 2021/2/4
 */
public class JwtUtil {
    private static final Key key = new SecretKeySpec(
            ("VFVsSFprMUJNRWREVTNGSFUwbGlNMFJSUlVKQlVWVkJRVFJIVGtGRVEwSnBVVXRDWjFGRVpHeGhkRkpxVW1wdloyOHpWMjlxWjBkSVJraFpUSFZ" +
                    "uWkFwVlYwRlpPV2xTTTJaNU5HRnlWMDVCTVV0dlV6aHJWbmN6TTJOS2FXSlljamhpZG5kVlFWVndZWEpEZDJ4 MlpHSklObVIyUlU5bWI" +
                    "zVXdMMmREUmxGekNraFZabEZ5VTBSMkswMTFVMVZOUVdVNGFucExSVFJ4Vnl0cVN5dDRVVlU1WVRBe lIxVnVTMGhyYTJ4bEsxRXdjRmd" +
                    "2WnpacVdGbzNjakV2ZUVGTE5VUUtiekpyVVN0WU5YaExPV05wY0ZKblJVdDNTVVJCVVVGQwog")
                    .getBytes(),
            SignatureAlgorithm.HS256.getJcaName()
    );

    //60秒     分    时   天
    public static final long ttlMillis = 60000L * 60 * 24 * 3;

    private static final long refreshTtl = 60000L * 60 * 24 * 30;

    /**
     * Tries to parse specified String as a JWT token. If successful, returns BaseUser object with username, id and role prefilled (extracted from token).
     * If unsuccessful (token is invalid or not containing all required user properties), simply returns null.
     *
     * @param token the JWT token to parse
     * @return the BaseUser object extracted from specified token or null if a token is invalid.
     */
    public static <T extends User> T parseToken(String token, T user) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
        Collection<String> roles = (Collection<String>) claims.get("roles");
        if (roles == null) roles = List.of();
        user.setId((Integer) claims.get("id"));
        user.setUsername((String) claims.get("username"));
        user.setRoles(roles.stream().map(Role::new).collect(Collectors.toList()));
        return user;
    }

    /**
     * Generates a JWT token containing username as subject, and userId and role as additional claims. These properties are taken from the specified
     * BaseUser object. Tokens validity is infinite.
     *
     * @param u the user for which the token will be generated
     * @return the JWT token
     */
    public static String generateToken(User u) {
        return generateToken(u, ttlMillis, key);
    }

    public static String generateRefreshToken(User u, String token) {
        Claims claims;
        try {
            claims = Jwts.parserBuilder()
                    .setSigningKey(key)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (ExpiredJwtException e) {
            claims = e.getClaims();
        }
        u.setId((Integer) claims.get("id"));
        u.setUsername((String) claims.get("username"));
        u.setRoles(((Collection<String>) claims.get("roles")).stream().map(Role::new).collect(Collectors.toList()));
        if (claims.getExpiration().compareTo(new Date(System.currentTimeMillis() - refreshTtl)) > 0) {
            return generateToken(u);
        } else return null;
    }

    public static String generateToken(User u, Long ttl, Key key) {
        Claims claims = Jwts.claims();
        claims.put("id", u.getId());
        claims.put("username", u.getUsername());
        claims.put("roles", u.getStringRoles());
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + ttl))
                .addClaims(claims)
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
}