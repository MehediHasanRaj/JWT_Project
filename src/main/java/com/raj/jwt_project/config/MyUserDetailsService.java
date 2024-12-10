// this is the implementation of UserDetailsService, that will talk to database.
//it has only one method.

package com.raj.jwt_project.config;

import com.raj.jwt_project.Entity.User;
import org.springframework.beans.factory.annotation.Autowired;
import com.raj.jwt_project.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class MyUserDetailsService implements UserDetailsService {
    @Autowired
    UserRepository userRepository;

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email).orElseThrow(()->new UsernameNotFoundException("User not found"));
        return MyUserDetails.build(user);
    }
}
