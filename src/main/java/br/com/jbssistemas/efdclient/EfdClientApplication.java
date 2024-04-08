package br.com.jbssistemas.efdclient;

import br.com.jbssistemas.efdclient.model.User;
import br.com.jbssistemas.efdclient.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class EfdClientApplication {

    public static void main(String[] args) {
        SpringApplication.run(EfdClientApplication.class, args);
    }

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }

//    @Bean
//    public CommandLineRunner runner(XmlApiService xmlApiService) {
//        return (args) -> {
//
//            Reinf.EvtInfoContri.IdeEvento ideEvento = new Reinf.EvtInfoContri.IdeEvento();
//            ideEvento.setTpAmb((short) 1);
//            ideEvento.setProcEmi((short) 1);
//            ideEvento.setVerProc("v1.0.0");
//
//            Reinf.EvtInfoContri.IdeContri ideContri = new Reinf.EvtInfoContri.IdeContri();
//            ideContri.setNrInsc("07811946000187");
//            ideContri.setTpInsc((short) 1);
//
//            Reinf.EvtInfoContri.InfoContri.Inclusao.IdePeriodo idePeriodo = new Reinf.EvtInfoContri.InfoContri.Inclusao.IdePeriodo();
//            LocalDate localDate = LocalDate.of(2024, 1, 1);
//            XMLGregorianCalendar iniValidad =
//                    DatatypeFactory.newInstance().newXMLGregorianCalendar(DateTimeFormatter.ofPattern("yyyy-MM").format(localDate));
//            idePeriodo.setIniValid(iniValidad);
//
//            Reinf.EvtInfoContri.InfoContri.Inclusao.InfoCadastro infoCadastro = new Reinf.EvtInfoContri.InfoContri.Inclusao.InfoCadastro();
//            infoCadastro.setClassTrib("85");
//            infoCadastro.setIndEscrituracao((short) 0);
//            infoCadastro.setIndDesoneracao((short) 0);
//            infoCadastro.setIndAcordoIsenMulta((short) 0);
//            infoCadastro.setIndSitPJ((short) 0);
//
//            Reinf.EvtInfoContri.InfoContri.Inclusao.InfoCadastro.Contato contato = new Reinf.EvtInfoContri.InfoContri.Inclusao.InfoCadastro.Contato();
//            contato.setNmCtt("ANTONIO DE FIGUEIREDO BRITO");
//            contato.setCpfCtt("34675230300");
//            contato.setEmail("antoniodbrito@hotmail.com");
//            contato.setFoneCel("88997926700");
//
//            infoCadastro.setContato(contato);
//
//            Reinf.EvtInfoContri.InfoContri.Inclusao inclusao = new Reinf.EvtInfoContri.InfoContri.Inclusao();
//            inclusao.setIdePeriodo(idePeriodo);
//            inclusao.setInfoCadastro(infoCadastro);
//
//            Reinf.EvtInfoContri.InfoContri infoContri = new Reinf.EvtInfoContri.InfoContri();
//            infoContri.setInclusao(inclusao);
//
//            Reinf.EvtInfoContri evtInfoContri = new Reinf.EvtInfoContri();
//            evtInfoContri.setId("ID1078119460001872024012010000000001");
//            evtInfoContri.setIdeEvento(ideEvento);
//            evtInfoContri.setIdeContri(ideContri);
//            evtInfoContri.setInfoContri(infoContri);
//
//            Reinf reinf = new Reinf();
//            reinf.setEvtInfoContri(evtInfoContri);
//            reinf.setSignature(new SignatureType());
//
//            br.gov.esocial.reinf.schemas.retornoloteeventosassincrono.v1_00_00.Reinf response = xmlApiService.sendSignedXmlData(
//                    reinf.getEvtInfoContri().getId(),
//                    reinf,
//                    "https://reinf.receita.economia.gov.br/recepcao/lotes",
//                    CertData.builder()
//                            .alias("1005216695")
//                            .keystorePath("/opt/sicont/pfx/P151.pfx")
//                            .keystorePassword("123456789")
//                            .build()
//            );
//
//            System.out.println(response);
//
//        };
//    }

    @Bean
    public CommandLineRunner initUser(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        return (args) -> {
            var username = "admin";
            if (userRepository.findByUsername(username) == null) {
                userRepository.save(
                        User.builder()
                                .username(username)
                                .password(passwordEncoder.encode("12345678"))
                                .name("Default Admin")
                                .build()
                );
            }
        };
    }

}
