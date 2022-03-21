package com.learning.security.service;

import java.util.Collection;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.learning.entity.User;
import com.learning.enums.EnabledStatus;

import lombok.Data;

@Data
/**
 * Handles user details.
 * @author Oliver Pagalanan
 * @since Mar 9, 2022
 */
public class UserDetailsImpl implements UserDetails {
	private Integer id;
	private String username;
	private String fullName;
	@JsonIgnore
	private String password;
	private EnabledStatus status;
	
	// Roles
	private Collection<? extends GrantedAuthority> authorities;
	
	private UserDetailsImpl(Integer id, String username, 
			String fullName, String password, EnabledStatus status,
			Collection<? extends GrantedAuthority> authorities) {
		this.id = id;
		this.username = username;
		this.fullName = fullName;
		this.password = password;
		this.status = status;
		this.authorities = authorities;
		
	}
	
	public static UserDetailsImpl build(User user) {
		List<GrantedAuthority> authorities = user.getRoles()
				.stream().map(role-> new SimpleGrantedAuthority(role.getRoleName().name()))
				.collect(Collectors.toList());
		return new UserDetailsImpl(user.getId(), user.getUsername(), 
				user.getFullName(), user.getPassword(), user.getEnabledStatus(), authorities);
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return password;
	}

	@Override
	public String getUsername() {
		return username;
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		// allow access on multiple platforms or not
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return status != EnabledStatus.STATUS_DISABLED;
		}
	
	@Override
	public int hashCode() {
		return Objects.hash(id);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		UserDetailsImpl other = (UserDetailsImpl) obj;
		return Objects.equals(id, other.id);
	} 	
	
	
	

}
