package com.learn.kidstinyworld.service;

import com.learn.kidstinyworld.entity.Parent;
import com.learn.kidstinyworld.repository.ParentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;

@Service
@RequiredArgsConstructor // Final sahələr üçün Constructor Injection (DI) edir
public class ParentDetailsService implements UserDetailsService {

    private final ParentRepository parentRepository;

    // Spring Security bu metodu login ve JWT filteri zamanı çağırır
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Parent parent = parentRepository.findByUsername(username);

        if (parent == null) {
            throw new UsernameNotFoundException("Valideyn tapılmadı: " + username);
        }

        return parent;
    }

}