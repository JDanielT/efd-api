package br.com.jbssistemas.efdclient.service;

import br.com.jbssistemas.efdclient.mapper.UserMapper;
import br.com.jbssistemas.efdclient.model.User;
import br.com.jbssistemas.efdclient.repository.UserRepository;
import br.com.jbssistemas.efdclient.response.dto.UserDto;
import br.com.jbssistemas.efdclient.service.commom.AbstractService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService, AbstractService<User, UserDto> {

    private final UserRepository userRepository;

    @Override
    public UserRepository getRepository() {
        return userRepository;
    }

    @Override
    public UserMapper getMapper() {
        return UserMapper.INSTANCE;
    }

    @Override
    public User loadUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

}
