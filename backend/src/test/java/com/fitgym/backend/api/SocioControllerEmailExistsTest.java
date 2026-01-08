package com.fitgym.backend.api;

import com.fitgym.backend.api.error.GlobalExceptionHandler;
import com.fitgym.backend.service.SocioService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class SocioControllerEmailExistsTest {

  private MockMvc mockMvc;
  private SocioService socioService;

  @BeforeEach
  void setUp() {
    socioService = Mockito.mock(SocioService.class);
    SocioController controller = new SocioController(socioService);
    mockMvc = MockMvcBuilders.standaloneSetup(controller)
        .setControllerAdvice(new GlobalExceptionHandler())
        .build();
  }

  @Test
  void email_exists_true() throws Exception {
    when(socioService.emailExiste("test@fitgym.com")).thenReturn(true);

    mockMvc.perform(get("/api/socios/email-exists")
            .param("email", "test@fitgym.com"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.exists").value(true));
  }

  @Test
  void email_exists_false() throws Exception {
    when(socioService.emailExiste("new@fitgym.com")).thenReturn(false);

    mockMvc.perform(get("/api/socios/email-exists")
            .param("email", "new@fitgym.com"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.exists").value(false));
  }
}
