package br.com.jbssistemas.efdclient.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Evento {

    @Id
    private String id;

    private String tipoEvento;
    private String referencia;
    private String protocoloEnvio;
    private String observacao;

    @ManyToOne
    @JoinColumn(name = "declarante_id")
    private Declarante declarante;

}
