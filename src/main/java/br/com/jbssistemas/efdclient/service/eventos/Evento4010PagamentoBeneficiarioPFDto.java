package br.com.jbssistemas.efdclient.service.eventos;

import br.com.jbssistemas.efdclient.model.Declarante;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
public class Evento4010PagamentoBeneficiarioPFDto {

    private LocalDate data;
    private Declarante declarante;

    // ideBenef
    private String cpfBeneficiario;
    private String nomeBeneficiario;
    private String numeroPagamento;

    // idePgto
    private String naturezaRendimento;

    // infoPgto
    private BigDecimal valorRendimentoBruto;
    private BigDecimal valorRendimentoTributavel;
    private BigDecimal valorIr;


}
