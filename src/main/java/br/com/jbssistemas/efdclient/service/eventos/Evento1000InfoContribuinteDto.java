package br.com.jbssistemas.efdclient.service.eventos;

import br.com.jbssistemas.efdclient.model.Declarante;
import br.com.jbssistemas.efdclient.model.ResponsavelTecnico;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
public class Evento1000InfoContribuinteDto {

    private LocalDate data;
    private Declarante declarante;
    private ResponsavelTecnico responsavelTecnico;

}
