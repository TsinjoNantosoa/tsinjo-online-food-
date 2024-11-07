package com.tsinjo.service;

import com.tsinjo.model.USER_ROLE;
import com.tsinjo.model.User;
import com.tsinjo.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class CustomerUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByEmail(username);

        // Vérification si l'utilisateur n'existe pas
        if (user == null) {
            throw new UsernameNotFoundException("User not found with email: " + username);
        }

        // Récupérer le rôle de l'utilisateur
        USER_ROLE role = user.getRole();

        // Créer une liste d'autorisations
        List<GrantedAuthority> authorities = new ArrayList<>();
        authorities.add(new SimpleGrantedAuthority(role.toString()));

        // Retourner les détails de l'utilisateur pour Spring Security
        return new org.springframework.security.core.userdetails.User(
                user.getEmail(),
                user.getPassword(),
                authorities
        );
    }
}


