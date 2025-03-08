package br.com.jbssistemas.efdclient;

import br.com.jbssistemas.efdclient.job.EventoUpdateJob;
import br.com.jbssistemas.efdclient.repository.DeclaranteRepository;
import br.com.jbssistemas.efdclient.repository.EventoRepository;
import br.com.jbssistemas.efdclient.repository.ResponsavelTecnicoRepository;
import br.com.jbssistemas.efdclient.service.csv.Evento4010PagamentoBeneficiarioPFCsvReader;
import br.com.jbssistemas.efdclient.service.csv.Evento4020PagamentoBeneficiarioPJCsvReader;
import br.com.jbssistemas.efdclient.service.eventos.Evento1000InfoContribuinteDto;
import br.com.jbssistemas.efdclient.service.eventos.Evento1000InfoContribuinteService;
import br.com.jbssistemas.efdclient.service.eventos.Evento4010PagamentoBeneficiarioPFService;
import br.com.jbssistemas.efdclient.service.eventos.Evento4020PagamentoBeneficiarioPJService;
import lombok.extern.log4j.Log4j2;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;

@SpringBootApplication
@Log4j2
public class EfdClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(EfdClientApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @Bean
//    CommandLineRunner runner(
//            Evento1000InfoContribuinteService evento1000InfoContribuinteService,
//            DeclaranteRepository declaranteRepository,
//            ResponsavelTecnicoRepository responsavelTecnicoRepository
//    ) {
//        return (args) -> {
//
//            var declarante = declaranteRepository.findByNumeroInscricao("07655277000100");
//            var responsavelTecnico = responsavelTecnicoRepository.findById(1L).orElseThrow();
//            var dto = Evento1000InfoContribuinteDto.builder()
//                    .data(LocalDate.of(2024,1, 1))
//                    .declarante(declarante)
//                    .responsavelTecnico(responsavelTecnico)
//                    .build();
//
//            evento1000InfoContribuinteService.enviar(dto);
//
//            log.info("finalizado");
//
//        };
//    }

//    @Bean
//    CommandLineRunner runner(
//            Evento4010PagamentoBeneficiarioPFCsvReader evento4010CsvReader,
//            Evento4010PagamentoBeneficiarioPFService evento4010PagamentoBeneficiarioPFService
//    ) {
//        return (args) -> {
//
//            var csvPath = "/home/daniel/Desktop/reinf/p031/e-4010-08-2024.csv";
//            var dtos = evento4010CsvReader.read(csvPath);
//
//            dtos.forEach(evento4010PagamentoBeneficiarioPFService::enviar);
//
//            log.info("finalizado");
//
//        };
//    }

    @Bean
    CommandLineRunner runner(
            Evento4020PagamentoBeneficiarioPJCsvReader evento4020CsvReader,
            Evento4020PagamentoBeneficiarioPJService evento4020PagamentoBeneficiarioPJService
    ) {
        return (args) -> {

            var csvPath = "/home/daniel/Desktop/reinf/p031/e-4020-08-2024.csv";
            var dtos = evento4020CsvReader.read(csvPath)
                    .stream()
                    .toList();

            dtos.forEach(evento4020PagamentoBeneficiarioPJService::enviar);

            log.info("finalizado");

        };
    }

//    @Bean
//    CommandLineRunner runner(EventoUpdateJob eventoUpdateJob, DeclaranteRepository declaranteRepository) {
//        return (args) -> {
//
//            var declarante = declaranteRepository.findByNumeroInscricao("14770466000180");
//            eventoUpdateJob.updateEvt4010(declarante);
//
//            log.info("finalizado");
//
//        };
//    }

//    @Bean
//    CommandLineRunner runner(PasswordEncoder encoder) {
//        return (args) -> {
//
//            System.out.println(encoder.encode("admin"));
//
//        };
//    }

}
