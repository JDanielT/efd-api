package br.com.jbssistemas.efdclient.repository;

import br.com.jbssistemas.efdclient.model.Evento;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface EventoRepository extends JpaRepository<Evento, String> {

    List<Evento> findByDeclaranteIdAndTipoEvento(Long declaranteId, String tipoEvento);

    List<Evento> findByDeclaranteIdAndTipoEventoAndReferencia(Long declaranteId, String tipoEvento, String referencia);

    boolean existsByIdentificadorAdicional(String identificador);

}
