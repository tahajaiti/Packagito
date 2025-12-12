package com.kyojin.packagito.security;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.util.ReflectionTestUtils;

import javax.crypto.SecretKey;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class JwtUtilTest {

    private JwtUtil jwtUtil;

    @Mock
    private Authentication authentication;

    @Mock
    private UserDetails userDetails;

    private static final String TEST_SECRET = Encoders.BASE64.encode(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
    private static final int TEST_EXPIRATION_MS = 3600000;
    private static final String TEST_ISSUER = "PackagitoTest";

    @BeforeEach
    void setUp() {
        jwtUtil = new JwtUtil();

        ReflectionTestUtils.setField(jwtUtil, "jwtSecret", TEST_SECRET);
        ReflectionTestUtils.setField(jwtUtil, "jwtExpirationMs", TEST_EXPIRATION_MS);
        ReflectionTestUtils.setField(jwtUtil, "jwtIssuer", TEST_ISSUER);
        jwtUtil.init();
    }

    @Test
    @DisplayName("generateJwt should create a valid token with correct claims")
    void generateJwt_ShouldReturnValidToken() {
        String username = "testuser";
        Collection<? extends GrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority("ROLE_USER"),
                new SimpleGrantedAuthority("ROLE_ADMIN")
        );

        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn(username);
        when(userDetails.getAuthorities()).thenAnswer(invocation -> authorities);

        String token = jwtUtil.generateJwt(authentication);

        assertThat(token).isNotNull().isNotEmpty();

        assertThat(jwtUtil.validateToken(token)).isTrue();
        assertThat(jwtUtil.getUsernameFromToken(token)).isEqualTo(username);
    }

    @Test
    @DisplayName("getUsernameFromToken should return the correct subject")
    void getUsernameFromToken_ShouldReturnUsername() {
        String expectedUsername = "targetUser";
        String token = createTestToken(expectedUsername, TEST_EXPIRATION_MS, TEST_SECRET);

        String actualUsername = jwtUtil.getUsernameFromToken(token);

        assertThat(actualUsername).isEqualTo(expectedUsername);
    }

    @Test
    @DisplayName("validateToken should return true for a valid token")
    void validateToken_ValidToken_ShouldReturnTrue() {
        String token = createTestToken("validUser", TEST_EXPIRATION_MS, TEST_SECRET);

        boolean isValid = jwtUtil.validateToken(token);

        assertThat(isValid).isTrue();
    }

    @Test
    @DisplayName("validateToken should return false when signature is invalid")
    void validateToken_InvalidSignature_ShouldReturnFalse() {
        String differentSecret = Encoders.BASE64.encode(Keys.secretKeyFor(SignatureAlgorithm.HS256).getEncoded());
        String token = createTestToken("hacker", TEST_EXPIRATION_MS, differentSecret);

        boolean isValid = jwtUtil.validateToken(token);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("validateToken should return false when token is expired")
    void validateToken_ExpiredToken_ShouldReturnFalse() {
        String token = createTestToken("oldUser", -1000, TEST_SECRET);

        boolean isValid = jwtUtil.validateToken(token);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("validateToken should return false when token is malformed")
    void validateToken_MalformedToken_ShouldReturnFalse() {
        String token = "not.a.valid.jwt.token";

        boolean isValid = jwtUtil.validateToken(token);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("validateToken should return false when token is empty")
    void validateToken_EmptyToken_ShouldReturnFalse() {
        String token = "";

        boolean isValid = jwtUtil.validateToken(token);

        assertThat(isValid).isFalse();
    }

    @Test
    @DisplayName("validateToken should return false when token is null")
    void validateToken_NullToken_ShouldReturnFalse() {
        String token = null;

        boolean isValid = jwtUtil.validateToken(token);

        assertThat(isValid).isFalse();
    }

    private String createTestToken(String subject, long expirationMs, String base64Secret) {
        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(base64Secret));
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expirationMs);

        return Jwts.builder()
                .subject(subject)
                .issuer(TEST_ISSUER)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(key)
                .compact();
    }
}