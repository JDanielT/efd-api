package br.com.jbssistemas.efdclient.repository;

import br.com.jbssistemas.efdclient.model.ResponsavelTecnico;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ResponsavelTecnicoRepository extends JpaRepository<ResponsavelTecnico, Long> {
}
