package com.moviedating.backend.Service;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyInt;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.Optional;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.mindrot.jbcrypt.BCrypt;

import com.moviedating.backend.Entity.Account;
import com.moviedating.backend.Entity.enums.GenderType;
import com.moviedating.backend.Repository.AccountRepository;
import com.moviedating.backend.dtos.AccountDTO;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willDoNothing;
import static org.mockito.Mockito.*;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class AccountServiceTests {
    @Mock
    private AccountRepository accountRepository;

    @Mock
    private jwtService jwtService;

    @InjectMocks
    private AccountService accountService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); 
    }
@Test
void testRegisterAccount_Success() {
    
    Account account = new Account();
    account.setUsername("testuser");
    account.setPassword("Password@123");
    account.setFirstName("Test");
    account.setLastName("User");

    when(accountRepository.findByUsername("testuser")).thenReturn(Optional.empty());
    when(accountRepository.save(any(Account.class))).thenReturn(account);


    Account result = accountService.registerAccount(account);

    assertNotNull(result);
    verify(accountRepository, times(1)).save(account);
}
@Test
void testRegisterAccount_FailureExistingUsername(){
    Account existingAccount = new Account();
    existingAccount.setUsername("testuser123");

    Account newAccount = new Account();
    newAccount.setFirstName("FirstName");
    newAccount.setLastName("LastName");
    newAccount.setUsername("testuser123");
    newAccount.setPassword("Password@123");

    when(accountRepository.findByUsername("testuser123")).thenReturn(Optional.of(existingAccount));

    Account result = accountService.registerAccount(newAccount);
    assertNull(result);
    verify(accountRepository, never()).save(any(Account.class));

}

@Test
void testRegisterAccount_FailureUsernameTooShort(){
    Account account = new Account();
    account.setFirstName("FirstName");
    account.setLastName("LastName");
    account.setUsername("u");
    account.setPassword("P@ssword123");

    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
        () -> accountService.registerAccount(account));

    assertEquals("Username must be between 5 and 20 characters.", e.getMessage());
}

@Test
void testRegisterAccount_FailureUsernameInvalidCharacters(){
    Account account = new Account();
    account.setFirstName("FirstName");
    account.setLastName("LastName");
    account.setUsername("u#@$Sername");
    account.setPassword("P@ssword123");

    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
        () -> accountService.registerAccount(account));

    assertEquals("Username can only contain letters, numbers, dots, dashes, and underscores.", e.getMessage());
}

@Test
void testRegisterAccount_FailureFirstNameEmpty(){
    Account account = new Account();
    account.setFirstName("");
    account.setLastName("LastName");
    account.setUsername("testuser123");
    account.setPassword("P@ssword123");

    Account result = accountService.registerAccount(account);

    assertNull(result);
    verify(accountRepository, never()).save(any(Account.class));

}

@Test
void testRegisterAccount_FailureLastNameEmpty(){
    Account account = new Account();
    account.setFirstName("FirstName");
    account.setLastName("");
    account.setUsername("testuser123");
    account.setPassword("P@ssword123");

    Account result = accountService.registerAccount(account);
    assertNull(result);
    verify(accountRepository, never()).save(any(Account.class));
}

@Test
void testRegisterAccount_FailurePasswordEmpty(){
    Account account = new Account();
    account.setFirstName("FirstName");
    account.setLastName("");
    account.setUsername("testuser123");
    account.setPassword("");

    Account result = accountService.registerAccount(account);
    assertNull(result);
    verify(accountRepository, never()).save(any(Account.class));

}
@Test
void testRegisterAccount_FailurePasswordTooShort(){
    Account account = new Account();
    account.setFirstName("FirstName");
    account.setLastName("LastName");
    account.setUsername("testuser123");
    account.setPassword("pass");

    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
        () -> accountService.registerAccount(account));
    assertEquals("Password must be 8 characters minimum.", e.getMessage());
}

@Test
void testLogin_Success() {
    Account account = new Account();
    account.setUsername(("testuser123"));
    String hashedPassword = BCrypt.hashpw("Password@123", BCrypt.gensalt());
    account.setPassword(hashedPassword);

    when(accountRepository.findByUsername("testuser123")).thenReturn(Optional.of(account));

    Account result = accountService.login("testuser123", "Password@123");

    assertNotNull(result);
    assertEquals("testuser123", result.getUsername());
}

@Test
void testLogin_FailureUsernameDoesNotExist(){
    String username = "nonexistentuser";
    String password = "P@ssword123";

    when(accountRepository.findByUsername(username)).thenReturn(Optional.empty());

    Account result = accountService.login(username, password);

    assertNull(result);
    verify(accountRepository,times(1)).findByUsername(username);
}
@Test 
void testLogin_FailureInvalidPassword(){
    Account account = new Account();
    account.setUsername("testuser123");
    account.setPassword(("Password@123"));

    String hashedPassword = BCrypt.hashpw("Password@123", BCrypt.gensalt());

    account.setPassword(hashedPassword);

    when(accountRepository.findByUsername("testuser123")).thenReturn(Optional.of(account));
    Account result = accountService.login("testuser123", "p@ssworD@123");
    assertNull(result);
    verify(accountRepository, times(1)).findByUsername("testuser123");
}

@Test
void testLogin_FailurePasswordsDoNotMatch(){
    String username = "testuser123";
    String wrongPassword = "P@ssword123";

    String hashedPassword = BCrypt.hashpw("Password@123", BCrypt.gensalt());
    
    Account account = new Account();
    account.setUsername(username);
    account.setPassword(hashedPassword);

    when(accountRepository.findByUsername(username)).thenReturn(Optional.of(account));

    Account result = accountService.login(username, wrongPassword);

    assertNull(result);
    verify(accountRepository, times(1)).findByUsername(username);


}
@Test
void testSaveLikes_Successfully() {
    Account account = new Account();
    account.setUsername("testuser123");

    when(accountRepository.findByUsername("testuser123")).thenReturn(Optional.of(account));
    accountService.saveLikes("testuser123", 28, 939243);

    verify(accountRepository, times(1)).save(account);
    assertEquals(28, account.getFavoriteGenre());
    assertEquals(939243, account.getFavoriteMovie());
}

@Test
void testSaveLikes_FailureUsernameNotFound(){
    Account account = new Account();
    String username = "nonexistentuser";

    when(accountRepository.findByUsername(username)).thenReturn(Optional.empty());

    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
        () -> accountService.saveLikes(username, 28, 939243));
    assertEquals("Account cannot be found", e.getMessage());
    
    verify(accountRepository, times(1)).findByUsername(username);
    verify(accountRepository, never()).save(any(Account.class));
}

@Test
void testUpdateLikes_Successfully(){
    String username = "testuser123";

    Account account = new Account();
    account.setUsername(username);

    when(accountRepository.findByUsername(username)).thenReturn(Optional.of(account));
    accountService.updateLikes(username, 53, 157336);

    verify(accountRepository, times(1)).save(account);
    assertEquals(53, account.getFavoriteGenre());
    assertEquals(157336, account.getFavoriteMovie());
}

@Test
void testUpdateLikes_FailureUsernameNotFound(){
    Account account = new Account();
    String username = "nonexistentuser";

    account.setUsername(username);
    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
        () -> accountService.updateLikes(username, 53, 157336));
    assertEquals("Account cannot be found", e.getMessage());
    
    verify(accountRepository, times(1)).findByUsername(username);
    verify(accountRepository, never()).save(any(Account.class));

}

@Test
void testFindMatches_Success() {
    Account account = new Account();
    account.setUsername("testuser123");
    account.setFavoriteMovie(101);
    account.setGender(GenderType.MALE);
    account.setGenderPreference(GenderType.FEMALE);

    Account match = new Account();
    match.setUsername("match123");
    match.setFavoriteMovie(101);
    match.setGender(GenderType.FEMALE);
    match.setGenderPreference(GenderType.MALE);

    Account notAMatch = new Account();
    notAMatch.setUsername("nonMatch");
    notAMatch.setFavoriteMovie(102);
    notAMatch.setGender(GenderType.NON_BINARY);
    notAMatch.setGenderPreference(GenderType.OTHER);

    when(accountRepository.findByUsername("testuser123")).thenReturn(Optional.of(account));
    when(accountRepository.findByFavoriteMovie(101)).thenReturn(List.of(match, notAMatch));

    List<AccountDTO> matches = accountService.findMatches("testuser123");

    assertEquals(1, matches.size());
    assertEquals("match123", matches.get(0).getUsername());
}

@Test
void testFindMatches_FailureUsernameNotFound(){

    String username = "nonexistentuser";

    when(accountRepository.findByUsername(username)).thenReturn(Optional.empty());

    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
        () -> accountService.findMatches(username));

    assertEquals("User not found", e.getMessage());

    verify(accountRepository, times(1)).findByUsername(username);
    verify(accountRepository, never()).findByFavoriteMovie(anyInt());
}

//Finish rest of updateMatches tests

@Test
void testUpdateUsername_Successfully(){
    String token = "token";
    String newUsername = "newuser123";
    Account account = new Account();
    account.setAccountId(1);
    account.setUsername("olduser123");

    when(jwtService.decodeToken(token)).thenReturn(account);
    when(accountRepository.existsByUsername(newUsername)).thenReturn(false);

    Account result = accountService.updateUsername(token, newUsername);
    assertNotNull(result);
    assertEquals(newUsername, result.getUsername());
    verify(accountRepository, times(1)).updateUsername(1, newUsername);

}

@Test
void testUpdateUsername_NullUsername(){
    String token = "token";
    String newUsername = null;

    Account account = new Account();
    account.setAccountId(1);
    account.setUsername("olduser123");

    when(jwtService.decodeToken(token)).thenReturn(account);

    IllegalArgumentException e = assertThrows(IllegalArgumentException.class, 
        () -> accountService.updateUsername(token, newUsername));

    assertEquals("Username cannot be empty", e.getMessage());
    verify(accountRepository, never()).updateUsername(anyInt(), anyString());
}

@Test
void testUsername_UsernameAlreadyExists(){
    String token = "token";
    String newUsername = "newAccount";

    Account account = new Account();
    account.setAccountId(1);
    account.setUsername("oldAccount");

    when(jwtService.decodeToken(token)).thenReturn(account);
    when(accountRepository.existsByUsername(newUsername)).thenReturn(true);

    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
        () -> accountService.updateUsername(token, newUsername));

    assertEquals("Username already exists", e.getMessage());
    verify(accountRepository, never()).updateUsername(anyInt(), anyString());
}

@Test
void testUpdateUsername_FailureEmptyUsername(){
    String token = "token";
    String newUsername = "";

    Account account = new Account();
    account.setAccountId(1);
    account.setUsername("olduser123");
    when(jwtService.decodeToken(token)).thenReturn(account);

    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
        () -> accountService.updateUsername(token, newUsername));

    assertEquals("Username cannot be empty", e.getMessage());
    verify(accountRepository, never()).updateUsername(anyInt(), anyString());

}

@Test
void testUpdateUsername_FailureInvalidUsername(){
    String token = "token";
    String invalidUsername = "USER@#$%NAME";

    Account account = new Account();
    account.setAccountId(1);
    account.setUsername("olduser123");

    when(jwtService.decodeToken(token)).thenReturn(account);

    IllegalArgumentException e = assertThrows(IllegalArgumentException.class,
        () -> accountService.updateUsername(token, invalidUsername));

    assertEquals("Username can only contain letters, numbers, dots, dashes, and underscores.", e.getMessage());
    verify(accountRepository, never()).updateUsername(anyInt(), anyString());

}
@Test
void testUpdatePassword_Successfully(){
    String token = "token";
    String newPassword = "newPassword@123";
    
    Account account = new Account();
    account.setUsername("testuser123");
    account.setPassword("oldPassword@123");

    when(jwtService.decodeToken(token)).thenReturn(account);
    accountService.updatePassword(token, newPassword);
    verify(accountRepository, times(1)).updatePassword(eq("testuser123"), anyString());

}

@Test
void testUpdatePassword_EmptyPassword(){
    String token = "token";
    String newPassword = "";

    Account account = new Account();
    account.setUsername("testuser123");
    when(jwtService.decodeToken(token)).thenReturn(account);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
        () -> accountService.updatePassword(token, newPassword));
    assertEquals("Password cannot be empty", exception.getMessage());

    verify(accountRepository, never()).updatePassword(anyString(), anyString());
    
}

@Test
void testUpdatePassword_InvalidPassword(){
    String token = "token";
    String newPassword = "p";

    Account account = new Account();
    account.setUsername("testuser123");
    when(jwtService.decodeToken(token)).thenReturn(account);

    IllegalArgumentException exception = assertThrows(IllegalArgumentException.class, 
        () -> accountService.updatePassword(token, newPassword));
    assertTrue(exception.getMessage().contains("Password must"));

    verify(accountRepository, never()).updatePassword(anyString(), anyString());
}

@Test
void testUpdateGenderAndPreference_Success(){
    Account account = new Account();
    account.setAccountId(1);
    account.setGender(GenderType.MALE);
    account.setGenderPreference(GenderType.FEMALE);

    GenderType newGender = GenderType.NON_BINARY;
    GenderType newGenderPreference = GenderType.MALE;

    accountService.updateGenderAndPreference(account, newGenderPreference, newGender);

    assertEquals(newGender, account.getGender());
    assertEquals(newGenderPreference, account.getGenderPreference());

    verify(accountRepository, times(1)).save(account);
}

@Test
void updateGenderAndPreference_FailureNullUsername(){
    Account account = null;
    GenderType gender = GenderType.FEMALE;
    GenderType genderPreference = GenderType.MALE;

    assertThrows(NullPointerException.class,
    () -> accountService.updateGenderAndPreference(account, genderPreference, gender));

    verify(accountRepository, never()).save(any(Account.class));
}

@Test 
void testUpdateGenderAndPreference_OverwriteGenderPreferences(){
    Account account = new Account();
    account.setAccountId(1);
    account.setUsername("testuser123");
    account.setGender(GenderType.MALE);
    account.setGenderPreference(GenderType.FEMALE);

    GenderType newGender = GenderType.NON_BINARY;
    GenderType newGenderPreference = GenderType.NON_BINARY;

    accountService.updateGenderAndPreference(account, newGender, newGenderPreference);

    assertEquals(newGender, account.getGender());
    assertEquals(newGenderPreference, account.getGenderPreference());

    verify(accountRepository,times(1)).save(account);
    
}
    
@Test
void testFillAccountInfo_Success(){
    Account account = new Account();
    account.setUsername("username123");

    Account retrievedAccount = new Account();
    retrievedAccount.setAccountId(1);
    retrievedAccount.setUsername("username123");
    retrievedAccount.setFavoriteMovie(101);

    when(accountRepository.findByUsername("username123")).thenReturn(Optional.of(retrievedAccount));

    Account result = accountService.fillAccountInfo(account);

    assertNotNull(result);
    assertEquals(retrievedAccount.getAccountId(), result.getAccountId());
    assertEquals(retrievedAccount.getUsername(), result.getUsername());
    assertEquals(retrievedAccount.getFavoriteMovie(), result.getFavoriteMovie());
    verify(accountRepository, times(1)).findByUsername("username123");

}

@Test
void testFillAccountInfo_FailureNullAccount(){
    Account account = null;

    assertThrows(NullPointerException.class,
        () -> accountService.fillAccountInfo(account));

    verify(accountRepository, never()).findByUsername(anyString());
}

}
