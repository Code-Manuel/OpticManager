package com.opticpatientmanager;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class OpticApplicationTests {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void contextLoads() {
        // verifica che il contesto Spring si avvii senza errori
    }

    @Test
    void listaPazientiVuotaRestituisce200() throws Exception {
        mockMvc.perform(get("/api/patients")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content").isArray());
    }

    @Test
    void creazionePazienteValido() throws Exception {
        String body = """
                {
                  "firstName": "Mario",
                  "lastName": "Rossi",
                  "fiscalCode": "RSSMRA80A01H501Z",
                  "birthDate": "1980-01-01",
                  "phone": "+393331234567",
                  "email": "mario.rossi@example.com"
                }
                """;

        mockMvc.perform(post("/api/patients")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(body))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").exists())
                .andExpect(jsonPath("$.lastName").value("Rossi"));
    }

    @Test
    void pazienteNonEsistenteRestituisce404() throws Exception {
        mockMvc.perform(get("/api/patients/9999")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }
}
