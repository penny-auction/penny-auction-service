package com.pennyauction.pennyauction.auth;

import io.jsonwebtoken.Jwts;
import org.apache.tomcat.util.codec.binary.Base64;
import org.springframework.context.annotation.PropertySource;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.security.KeyFactory;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.ArrayList;

@PropertySource("file:config.properties")
public class JWTAuthFilter extends BasicAuthenticationFilter {

    public JWTAuthFilter(AuthenticationManager authenticationManager) {
        super(authenticationManager);
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws IOException, ServletException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer")) {
            SecurityContextHolder.getContext().setAuthentication(null);
            chain.doFilter(request, response);
            return;
        }
        UsernamePasswordAuthenticationToken authentication = getAuthentication(request);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        chain.doFilter(request, response);
    }

    private UsernamePasswordAuthenticationToken getAuthentication(HttpServletRequest request) {
        String token = request.getHeader("Authorization");
        if (token != null) {
            RSAPublicKey publicKey;
            try {
                String keyString = System.getenv("JWT_PUBLIC_KEY");
                KeyFactory keyFactory = KeyFactory.getInstance("RSA");
                byte[] bytes = Base64.decodeBase64(keyString);
                publicKey = (RSAPublicKey) keyFactory.generatePublic(new X509EncodedKeySpec(bytes));

                // Parse token.
                String user = Jwts.parser()
                        .setSigningKey(publicKey)
                        .parseClaimsJws(token.replace("Bearer ", ""))
                        .getBody()
                        .getSubject();

                if (user != null) {
                    return new UsernamePasswordAuthenticationToken(user, null, new ArrayList<>());
                }
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
            return null;
        }
        return null;
    }
}
