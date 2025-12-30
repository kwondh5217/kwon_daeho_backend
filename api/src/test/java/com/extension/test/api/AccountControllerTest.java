package com.extension.test.api;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.extension.test.accounts.AccountService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

  @Autowired
  MockMvc mvc;

  @MockBean
  AccountService accountService;

  @DisplayName("validation 이 실패하면 커스텀 예외를 반환한다")
  @Test
  void create_blankAccountNumber_returns400_and_ApiResponseFail() throws Exception {
    mvc.perform(post("/accounts")
            .contentType(MediaType.APPLICATION_JSON)
            .content("""
                {"accountNumber":""}
                """))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.success").value(false))
        .andExpect(jsonPath("$.code").value("INVALID_REQUEST"))
        .andExpect(jsonPath("$.errors[0].field").value("accountNumber"));
  }
}
