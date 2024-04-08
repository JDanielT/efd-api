package br.com.jbssistemas.efdclient.service;

import br.com.jbssistemas.efdclient.model.User;
import br.com.jbssistemas.efdclient.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final UserRepository userRepository;

    @Override
    public User loadUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
