package com.cesar.Authentication.service;

import java.util.HashSet;
import java.util.Set;

import com.cesar.Authentication.persistence.entity.AuthUserEntity;
import com.cesar.Authentication.persistence.entity.RoleEntity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class UserDetailServiceImpl implements UserDetailsService {

	private final AuthUserService userService;

	public UserDetailServiceImpl(AuthUserService userService){
		this.userService = userService;
	}


	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		
		AuthUserEntity foundUser = userService.loadByUsername(username);

		//Convert entity and authorities to Spring Security objects (UserDetails and GrantedAuthorities)
        return new User(
                foundUser.getUsername(),
                foundUser.getPassword(),
                getAuthoritiesFromRoles(foundUser.getRoles())
        );
	}

	public UserDetails mapUserEntityToUserDetails(AuthUserEntity entity) {

		//Convert entity and authorities to Spring Security objects (UserDetails and GrantedAuthorities)
		return new User(
				entity.getUsername(),
				entity.getPassword(),
				getAuthoritiesFromRoles(entity.getRoles())
		);
	}

	private Set<SimpleGrantedAuthority> getAuthoritiesFromRoles(Set<RoleEntity> roles){

		Set<SimpleGrantedAuthority> authorities = new HashSet<>();

		//Roles
		roles
			.forEach(role -> authorities.add(new SimpleGrantedAuthority("ROLE_".concat(role.getName().name()))));

		//Permissions
		roles.stream()
				.flatMap(role -> role.getPermissions().stream())
				.forEach(permission -> authorities.add(new SimpleGrantedAuthority(permission.getName().name())));

		return authorities;
	}
}