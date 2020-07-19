package com.lakshay.redditclone.service;
import java.util.Collection;
import java.util.Optional;
import static java.util.Collections.singletonList;
//user details service implementation as user details service is an interface
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.lakshay.redditclone.model.User;
import com.lakshay.redditclone.repository.UserRepository;
import lombok.AllArgsConstructor;

@Service
@AllArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {
    private final UserRepository userRepository;

    @Override
    @Transactional(readOnly = true)
    public UserDetails loadUserByUsername(String username) {
        Optional<User> userOptional = userRepository.findByUsername(username); //finding user with user name
        User user = userOptional
                .orElseThrow(() -> new UsernameNotFoundException("No user " +
                        "Found with username : " + username)); // if user not present then user not found exception

        return new org.springframework.security
                .core.userdetails.User( user.getUsername(), user.getPassword(), //userdetails = interface Provides core user information.
                user.isEnabled(), true, true,
                true, getAuthorities("USER") ); //with user object creating another object with same name User then mapping userdetils interface present in core with User class
        	//(usernme,pass,isEnabled, accountNonExpired or acc expired, credentialsNonExpired or pass expired, accountNonLocked, Returns the authorities granted to the user.)
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String role) {
        return singletonList(new SimpleGrantedAuthority(role));
    }
}