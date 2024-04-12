package br.com.jbssistemas.efdclient.service.csv;

import br.com.jbssistemas.efdclient.repository.DeclaranteRepository;
import br.com.jbssistemas.efdclient.service.eventos.Evento4010PagamentoBeneficiarioPFDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Component
@RequiredArgsConstructor
public class Evento4010PagamentoBeneficiarioPFCsvReader {

    private final DeclaranteRepository declaranteRepository;

    public List<Evento4010PagamentoBeneficiarioPFDto> read(String csvFilePath) {

        final String SEPARATOR = ";";
        final List<Evento4010PagamentoBeneficiarioPFDto> list = new ArrayList<>();
        final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        try (BufferedReader br = new BufferedReader(new FileReader(csvFilePath))) {

            String line;
            while ((line = br.readLine()) != null) {

                String[] data = line.split(SEPARATOR);

                var declarante = declaranteRepository.findByNumeroInscricao(data[0]);

                list.add(Evento4010PagamentoBeneficiarioPFDto.builder()
                        .data(LocalDate.parse(data[1], formatter))
                        .numeroPagamento(data[2])
                        .declarante(declarante)
                        .cpfBeneficiario(data[3])
                        .nomeBeneficiario(data[4])
                        .naturezaRendimento(data[5])
                        .valorRendimentoBruto(new BigDecimal(data[6]))
                        .valorRendimentoTributavel(new BigDecimal(data[7]))
                        .valorIr(new BigDecimal(data[8]))
                        .build());

            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        return list;

    }

}
