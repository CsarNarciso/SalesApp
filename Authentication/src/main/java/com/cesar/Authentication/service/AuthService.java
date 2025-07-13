package com.cesar.JwtServer.service;

import com.auth0.jwt.interfaces.DecodedJWT;
import com.cesar.JwtServer.exception.NoAuthenticatedException;
import com.cesar.JwtServer.persistence.entity.JwtTokenType;
import com.cesar.JwtServer.persistence.entity.RefreshTokenEntity;
import com.cesar.JwtServer.persistence.entity.RoleEntity;
import com.cesar.JwtServer.persistence.entity.UserEntity;
import com.cesar.JwtServer.persistence.repository.UserRepository;
import com.cesar.JwtServer.presentation.dto.LogInRequest;
import com.cesar.JwtServer.presentation.dto.SignUpRequest;
import com.cesar.JwtServer.presentation.dto.SignUpResponse;
import com.cesar.JwtServer.util.AuthorityUtils;
import com.cesar.JwtServer.util.JwtUtils;
import com.cesar.JwtServer.util.UserUtils;
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

import java.io.Console;
import java.util.Arrays;
import java.util.Set;

@Service
public class AuthService {

    private final UserDetailServiceImpl userDetailService;
    private final UserRepository userRepo;
    private final UserService userService;
    private final UserUtils userUtils;
    private final JwtUtils jwtUtils;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AuthorityUtils authorityUtils;

    private final int TOKEN_COOKIE_EXPIRATION_TIME;

    public AuthService(UserDetailServiceImpl userDetailService, UserRepository userRepo,
                       UserService userService, UserUtils userUtils, JwtUtils jwtUtils,
                       AuthorityUtils authorityUtils,
                       @Value("${jwt.cookie.expirationTime}") int TOKEN_COOKIE_EXPIRATION_TIME){
        this.userDetailService = userDetailService;
        this.userRepo = userRepo;
        this.userService = userService;
        this.userUtils = userUtils;
        this.jwtUtils = jwtUtils;
        this.authorityUtils = authorityUtils;
        this.passwordEncoder = new BCryptPasswordEncoder();

        this.TOKEN_COOKIE_EXPIRATION_TIME = TOKEN_COOKIE_EXPIRATION_TIME;
    }


    public void login(LogInRequest loginRequest, HttpServletResponse res){

        String username = loginRequest.username();
        String password = loginRequest.password();

        // Find user
        UserEntity foundUser = userService.loadByUsername(username);

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

    public SignUpResponse signup(SignUpRequest signupRequest){

        //Get user data
        String username = signupRequest.username();
        String password = signupRequest.password();

        //Create and save new user in DB
        RoleEntity userRole = authorityUtils.getUserRole();
        UserEntity user = userUtils.buildUser(username, password, Set.of(userRole));
        user = userRepo.save(user);

        return SignUpResponse
                .builder()
                .username(user.getUsername())
                .created(true)
                .build();
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
        UserEntity foundUser = userService.loadByUsername(username);

        // Authenticate
        Authentication auth = authenticateByUserEntity(foundUser);

        // Rotate (update/refresh) tokens
        String token = jwtUtils.createToken(auth, JwtTokenType.ACCESS);
        String refresh = jwtUtils.createToken(auth, JwtTokenType.REFRESH);

        // Save refresh token for user in DB
        foundUser.setRefreshToken(new RefreshTokenEntity(refresh));
        UserEntity updatedUser = userRepo.save(foundUser);
        System.out.println("UPDATED USER -> " + updatedUser.getUsername());

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

    private Authentication authenticateByUsernameAndPassword(UserEntity userEntity, String username, String password){

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

    private Authentication authenticateByUserEntity(UserEntity userEntity){

        //Authenticate user in Security context
        UserDetails user = userDetailService.mapUserEntityToUserDetails(userEntity);

        Authentication authentication = new
                UsernamePasswordAuthenticationToken(user.getUsername(), null, user.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
        return authentication;
    }
}