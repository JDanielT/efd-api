package br.com.jbssistemas.efdclient.repository;

import br.com.jbssistemas.efdclient.model.User;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends AbstractRepository<User> {

    User findByUsername(String username);

}
