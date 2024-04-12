package br.com.jbssistemas.efdclient.repository;

import br.com.jbssistemas.efdclient.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EventoRepository extends JpaRepository<Evento, String> {
}
