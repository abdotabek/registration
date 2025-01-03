package api.gossip.uz.util;

import api.gossip.uz.dto.jwt.JwtDTO;
import api.gossip.uz.enums.ProfileRole;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

import javax.crypto.SecretKey;
import java.util.*;
import java.util.stream.Collectors;

public class JwtUtil {
    private static final long tokenLiveTime = 1000 * 3600 * 24; // 1-day
    private static final long refreshTokenLiveTime = 1000L * 3600 * 24 * 30; // 30-day
    private static final String secretKey = "veryLongSecretmazgillattayevlasharaaxmojonjinnijonsurbetbekkiydirhonuxlatdibekloxovdangasabekochkozjonduxovmashaynikmaydagapchishularnioqiganbolsangizgapyoqaniqsizmazgi";


    public static String encode(Integer id) {

        return Jwts
                .builder()
                .subject(String.valueOf(id))
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + (60 * 60 * 100)))
                .signWith(getSignInKey())
                .compact();
    }

    public static String encode(Integer id, List<ProfileRole> roleList) {
        String strRoles = roleList.stream().map(Enum::name).collect(Collectors.joining(","));

        Map<String, String> claims = new HashMap<>();
        claims.put("roles", strRoles);
        return Jwts
                .builder()
                .subject(String.valueOf(id))
                .claims(claims)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + tokenLiveTime))
                .signWith(getSignInKey())
                .compact();
    }

    public static JwtDTO decode(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        Integer id = Integer.valueOf(claims.getSubject());
        String strRole = (String) claims.get("role");
        // ROLE_USER, USER_ADMIN
        List<ProfileRole> roleList = Arrays.stream(strRole.split(","))
                .map(ProfileRole::valueOf)
                .toList();

        return new JwtDTO(id, roleList);
    }

    public static Integer decodeRegVerToken(String token) {
        Claims claims = Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();
        return Integer.valueOf(claims.getSubject());
    }


    public static boolean isValid(String token) {

        return Jwts
                .parser()
                .verifyWith(getSignInKey())
                .build()
                .parseSignedClaims(token)
                .getPayload()
                .getExpiration().after(new Date());
    }


    private static SecretKey getSignInKey() {
        byte[] ketBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(ketBytes);
    }

}
