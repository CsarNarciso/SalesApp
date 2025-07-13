package com.cesar.Authentication.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.cesar.Authentication.exception.NoAuthenticatedException;
import com.cesar.Authentication.persistence.dto.AuthRequest;
import com.cesar.Authentication.persistence.entity.AuthUserEntity;
import com.cesar.Authentication.persistence.entity.JwtTokenType;
import com.cesar.Authentication.persistence.entity.RefreshTokenEntity;
import com.cesar.Authentication.persistence.entity.RoleEntity;
import com.cesar.Authentication.persistence.repository.AuthUserRepository;
import com.cesar.Authentication.util.AuthUserUtils;
import com.cesar.Authentication.util.JwtUtils;
import com.cesar.Authentication.util.AuthorityUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;

@Service
public class AuthService {

    private final UserDetailServiceImpl userDetailService;
    private final AuthUserRepository userRepo;
    private final AuthUserService userService;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthorityUtils authorityUtils;
    private final AuthUserUtils authUserUtils;

    private final int TOKEN_COOKIE_EXPIRATION_TIME;

    public AuthService(UserDetailServiceImpl userDetailService, AuthUserRepository userRepo,
                       AuthUserService userService, AuthUserUtils authUserUtils, JwtUtils jwtUtils,
                       AuthorityUtils authorityUtils,
                       @Value("${jwt.cookie.expirationTime}") int TOKEN_COOKIE_EXPIRATION_TIME){
        this.userDetailService = userDetailService;
        this.userRepo = userRepo;
        this.userService = userService;
        this.authUserUtils = authUserUtils;
        this.jwtUtils = jwtUtils;
        this.authorityUtils = authorityUtils;
        this.passwordEncoder = new BCryptPasswordEncoder();

        this.TOKEN_COOKIE_EXPIRATION_TIME = TOKEN_COOKIE_EXPIRATION_TIME;
    }

    public void signup(AuthRequest signupRequest){

        //Get user data
        String username = signupRequest.username();
        String password = signupRequest.password();

        //Create and save new user in DB
        RoleEntity userRole = authorityUtils.getUserRole();
        AuthUserEntity user = authUserUtils.buildUser(username, password, Set.of(userRole));
        userRepo.save(user);
    }

    public void login(AuthRequest loginRequest, HttpServletResponse res){

        String username = loginRequest.username();
        String password = loginRequest.password();

        // Find user
        AuthUserEntity foundUser = userService.loadByUsername(username);

        // Authenticate
        Authentication auth = authenticateByUsernameAndPassword(foundUser, username, password);

        // Generate auth tokens
        String token = jwtUtils.createToken(auth, JwtTokenType.ACCESS);
        String refresh = jwtUtils.createToken(auth, JwtTokenType.REFRESH);

        // Save refresh token for user in DB
        foundUser.setRefreshToken(new RefreshTokenEntity(refresh));
        userRepo.save(foundUser);

        // Load tokens in cookies
        Cookie tokenCookie = new Cookie("token", token);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setSecure(true);
        tokenCookie.setMaxAge(TOKEN_COOKIE_EXPIRATION_TIME);
        res.addCookie(tokenCookie);

        Cookie refreshCookie = new Cookie("refresh", refresh);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setMaxAge(TOKEN_COOKIE_EXPIRATION_TIME);
        res.addCookie(refreshCookie);
    }

    public void refresh(HttpServletRequest req, HttpServletResponse res) {

        // Try to get request cookie with refresh token
        Cookie[] cookies = req.getCookies();

        Cookie refreshCookie = Arrays.stream(cookies)
                .filter(c -> c.getName().equals("refresh")).findFirst()
                .orElseThrow(() -> new NoAuthenticatedException("Missing token"));

        String refreshToken = refreshCookie.getValue();

        // Verify refresh token
        DecodedJWT decoded = jwtUtils.validateToken(refreshToken, JwtTokenType.REFRESH);

        // Find user
        String username = jwtUtils.extractUsername(decoded);
        AuthUserEntity foundUser = userService.loadByUsername(username);

        // Authenticate
        Authentication auth = authenticateByUserEntity(foundUser);

        // Rotate (update/refresh) tokens
        String token = jwtUtils.createToken(auth, JwtTokenType.ACCESS);
        String refresh = jwtUtils.createToken(auth, JwtTokenType.REFRESH);

        // Save refresh token for user in DB
        foundUser.setRefreshToken(new RefreshTokenEntity(refresh));
        AuthUserEntity updatedUser = userRepo.save(foundUser);

        // Load tokens in cookies
        Cookie tokenCookie = new Cookie("token", token);
        tokenCookie.setHttpOnly(true);
        tokenCookie.setSecure(true);
        tokenCookie.setMaxAge(TOKEN_COOKIE_EXPIRATION_TIME);
        res.addCookie(tokenCookie);

        Cookie newRefreshCookie = new Cookie("refresh", refresh);
        refreshCookie.setHttpOnly(true);
        refreshCookie.setSecure(true);
        refreshCookie.setMaxAge(TOKEN_COOKIE_EXPIRATION_TIME);
        res.addCookie(newRefreshCookie);
    }

    private Authentication authenticateByUsernameAndPassword(AuthUserEntity userEntity, String username, String password){

        UserDetails user = userDetailService.mapUserEntityToUserDetails(userEntity);

        //If user exists (correct credentials)
        if(passwordEncoder.matches(password, user.getPassword())){

            //Authenticate user in Security context
            Authentication authentication = new
                    UsernamePasswordAuthenticationToken(username, null, user.getAuthorities());
            SecurityContextHolder.getContext().setAuthentication(authentication);

            return authentication;
        }

        throw new BadCredentialsException("Invalid username or password");
    }

    private Authentication authenticateByUserEntity(AuthUserEntity userEntity){

        //Authenticate user in Security context
        UserDetails user = userDetailService.mapUserEntityToUserDetails(userEntity);

        Authentication authentication = new
                UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }
}