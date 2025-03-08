package br.com.jbssistemas.efdclient.model;

import jakarta.persistence.*;
import lombok.Data;

@Data
@Entity
public class Declarante extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private int tipoInscricao;
    private String numeroInscricao;
    private String classificacaoTributaria;

    private String certAlias;
    private String keystorePath;
    private String keystorePassword;

}
