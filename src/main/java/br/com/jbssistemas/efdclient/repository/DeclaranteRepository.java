package br.com.jbssistemas.efdclient.repository;

import br.com.jbssistemas.efdclient.model.Declarante;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeclaranteRepository extends JpaRepository<Declarante, Long> {

    Declarante findByNumeroInscricao(String numeroInscricao);

}
