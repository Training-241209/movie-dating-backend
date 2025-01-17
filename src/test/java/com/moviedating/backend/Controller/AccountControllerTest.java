package com.moviedating.backend.Controller;
import com.fasterxml.jackson.databind.ObjectMapper;

import com.moviedating.backend.Entity.Account;
import com.moviedating.backend.Entity.enums.GenderType;
import com.moviedating.backend.Service.AccountService;
import com.moviedating.backend.Service.jwtService;
import com.moviedating.backend.dtos.UpdatePasswordDTO;

import org.junit.jupiter.api.Test;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@WebMvcTest(AccountController.class)
public class AccountControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private AccountService accountService;

    @MockBean
    private jwtService jwtService;

    @MockBean
    private com.moviedating.backend.Repository.AccountRepository accountRepository;

    @MockBean
    private com.moviedating.backend.Service.MatchingService matchingService;

    @Test
    void testRegisterAccount_Success() throws Exception {
        Account account = new Account();
        account.setAccountId(1);
        account.setUsername("testuser");
        account.setPassword("P@ssword123");

        when(accountService.registerAccount(any(Account.class))).thenReturn(account);

        mockMvc.perform(post("/account/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"testuser\",\"password\":\"P@ssword123\"}"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.username").value("testuser"));
    }
    @Test
    void testRegisterAccount_Conflict() throws Exception {
        when(accountService.registerAccount(any(Account.class))).thenReturn(null);

        mockMvc.perform(post("/account/register")
            .contentType(MediaType.APPLICATION_JSON)
            .content("{\"username\":\"testuser\",\"password\":\"P@ssword123\"}"))
            .andExpect(status().isConflict());
    }

@Test
void testLogin_WrongPassword() throws Exception {
    when(accountService.login(anyString(), anyString())).thenReturn(null);

    mockMvc.perform(post("/account/login")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"testuser\",\"password\":\"wrongpassword\"}"))
        .andExpect(status().isBadRequest());
}
@Test
void testUpdateUsername_Success() throws Exception {
    Account account = new Account();
    account.setUsername("newUsername");

    when(jwtService.generateToken(any(Account.class))).thenReturn("newMockToken");
    when(accountService.updateUsername(anyString(), anyString())).thenReturn(account);

    mockMvc.perform(patch("/account/update-username")
        .header("Authorization", "Bearer mockToken")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"newUsername\"}"))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Username updated"))
        .andExpect(jsonPath("$.newToken").value("newMockToken"));
}
@Test
void updateUsername_EmptyAuthHeader() throws Exception {
    mockMvc.perform(patch("/account/update-username")
        .header("Authorization", "")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"username\":\"newUsername\"}"))
        .andExpect(status().isUnauthorized());
}

@Test
void testUpdatePassword_Success() throws Exception {
    String validToken = "mockValidToken";
    String newPassword = "NewSecurePassword123";

    mockMvc.perform(patch("/account/update-password")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
        .content("{\"password\": \"" + newPassword + "\"}"))
        .andExpect(status().isOk()) 
        .andExpect(jsonPath("$.message").value("Password updated")); // Verify response message
}

@Test
void testUpdatePassword_Unauthorized_EmptyAuthorizationHeader() throws Exception {
    mockMvc.perform(patch("/account/update-password")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, "") 
        .content("{\"password\": \"NewSecurePassword123\"}")) 
        .andExpect(status().isUnauthorized()); 
}

@Test
void testUpdateGenderAndPreference_Success() throws Exception {
    String validToken = "mockValidToken";
    String username = "testuser";
    String gender = "MALE";
    String genderPreference = "FEMALE";

    Account mockAccount = new Account();
    mockAccount.setUsername(username);
    when(jwtService.decodeToken(validToken)).thenReturn(mockAccount);
    when(accountRepository.findByUsername(username)).thenReturn(Optional.of(mockAccount));

    doNothing().when(accountService).updateGenderAndPreference(mockAccount, GenderType.FEMALE, GenderType.MALE);

    mockMvc.perform(patch("/account/update-gender-and-preference")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
        .content("{\"gender\": \"" + gender + "\", \"genderPreference\": \"" + genderPreference + "\"}"))
        .andExpect(status().isOk())
        .andExpect(content().string("Gender and preference updated successfully"));
}
@Test
void testUpdateGenderAndPreference_Unauthorized_NoAuthorizationHeader() throws Exception {
    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("gender", "MALE");
    requestBody.put("genderPreference", "FEMALE");
    
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonRequest = objectMapper.writeValueAsString(requestBody);

    mockMvc.perform(patch("/account/update-gender-and-preference")
        .contentType(MediaType.APPLICATION_JSON)
        .content(jsonRequest))
        .andExpect(status().isBadRequest()); 
}

@Test
void testUpdateGenderAndPreference_Unauthorized_EmptyAuthHeader() throws Exception {
    Map<String, String> requestBody = new HashMap<>();
    requestBody.put("gender", "MALE");
    requestBody.put("genderPreference", "FEMALE");
    
    ObjectMapper objectMapper = new ObjectMapper();
    String jsonRequest = objectMapper.writeValueAsString(requestBody);

    mockMvc.perform(patch("/account/update-gender-and-preference")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, "")  
        .content(jsonRequest))
        .andExpect(status().isUnauthorized());
}

@Test
void testUpdateGenderAndPreference_Unauthorized_EmptyAuthorizationHeader() throws Exception {
    mockMvc.perform(patch("/account/update-gender-and-preference")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, "") 
        .content("{\"gender\": \"MALE\", \"genderPreference\": \"FEMALE\"}")) 
        .andExpect(status().isUnauthorized()); 
}

@Test
void testChooseFavorites_Success() throws Exception {
    String validToken = "mockValidToken";
    String username = "testuser";
    int genreId = 58;
    int movieId = 939243;

    Account mockAccount = new Account();
    mockAccount.setUsername(username);
    when(jwtService.decodeToken(validToken)).thenReturn(mockAccount);

    doNothing().when(accountService).saveLikes(username, genreId, movieId);

    mockMvc.perform(post("/account/choose-favorites")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
        .content("{\"genreId\": " + genreId + ", \"movieId\": " + movieId + "}")) 
        .andExpect(status().isOk()) 
        .andExpect(content().string("Favorite genre and movie updated successfully")); 
}

@Test
void testChooseFavorites_MissingAuthorizationHeader() throws Exception {
    mockMvc.perform(post("/account/choose-favorites")
        .contentType(MediaType.APPLICATION_JSON)
        .content("{\"genreId\": 1, \"movieId\": 100}"))
        .andExpect(status().isBadRequest());  
}
@Test
void testChooseFavorites_EmptyAuthorizationHeader() throws Exception {
    mockMvc.perform(post("/account/choose-favorites")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, " ")
        .content("{\"genreId\": 1, \"movieId\": 100}"))
        .andExpect(status().isUnauthorized());
}

@Test
void testUpdateFavorites_Success() throws Exception {
    String validToken = "mockValidToken";
    String username = "testuser";
    int genreId = 1;
    int movieId = 100;

    Account mockAccount = new Account();
    mockAccount.setUsername(username);
    when(jwtService.decodeToken(validToken)).thenReturn(mockAccount);

    doNothing().when(accountService).updateLikes(username, genreId, movieId);

    mockMvc.perform(patch("/account/update-favorites")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)
        .content("{\"genreId\": " + genreId + ", \"movieId\": " + movieId + "}")) 
        .andExpect(status().isOk()) 
        .andExpect(content().string("Favorite genre and movie updated successfully")); 
}

@Test
void testUpdateFavorites_EmptyAuthorizationHeader() throws Exception {
    int genreId = 1;
    int movieId = 100;

    mockMvc.perform(patch("/account/update-favorites")
        .contentType(MediaType.APPLICATION_JSON)
        .header(HttpHeaders.AUTHORIZATION, " ") 
        .content("{\"genreId\": " + genreId + ", \"movieId\": " + movieId + "}"))
        .andExpect(status().isUnauthorized()); 
}

@Test
void testLogoutUser_Success() throws Exception {
    String validToken = "mockValidToken";

    doNothing().when(jwtService).invalidateToken(validToken);

    mockMvc.perform(post("/account/logout")
        .header(HttpHeaders.AUTHORIZATION, "Bearer " + validToken)) 
        .andExpect(status().isOk()) 
        .andExpect(content().string("Logout successful."));
}

@Test
void testLogoutUser_MissingAuthorizationHeader() throws Exception {
    mockMvc.perform(post("/account/logout"))
            .andExpect(status().isBadRequest());
}

    @Test
    void testLogoutUser_EmptyAuthorizationHeader() throws Exception {
        mockMvc.perform(post("/account/logout")
            .header(HttpHeaders.AUTHORIZATION, " "))
            .andExpect(status().isBadRequest())
            .andExpect(content().string("Authorization header missing or invalid."));
    }
}
