package br.com.jbssistemas.efdclient.repository;

import br.com.jbssistemas.efdclient.model.BaseEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.NoRepositoryBean;

@NoRepositoryBean
public interface AbstractRepository<T extends BaseEntity> extends JpaRepository<T, Long> {
}
