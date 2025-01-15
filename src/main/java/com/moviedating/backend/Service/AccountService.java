package com.moviedating.backend.Service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.mindrot.jbcrypt.BCrypt;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moviedating.backend.Entity.Account;
import com.moviedating.backend.Entity.enums.GenderType;
import com.moviedating.backend.Repository.AccountRepository;
import com.moviedating.backend.dtos.UpdateUsernameDTO;

import jakarta.transaction.Transactional;

import com.moviedating.backend.dtos.AccountDTO;
import com.moviedating.backend.dtos.UpdatePasswordDTO;

@Service
public class AccountService {
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    jwtService jwtService;


    public Account registerAccount(Account account) {
        Optional<Account> existingAccount = accountRepository.findByUsername(account.getUsername());
        String firstName = account.getFirstName();
        String lastName = account.getLastName();
        String password = account.getPassword();

        if (existingAccount.isPresent() || firstName.trim().isEmpty() || lastName.trim().isEmpty()
                || password.trim().isEmpty()) {
            return null;
        }
        validateUsername(account.getUsername());
        validatePassword(account.getPassword());

        String hashedPass = BCrypt.hashpw(account.getPassword(), BCrypt.gensalt());
        account.setPassword(hashedPass);
        return accountRepository.save(account);
    }

    public Account login(String username, String password) {
        Optional<Account> account = accountRepository.findByUsername(username);
        if (account.isPresent() && account.get().getUsername().equals(username)
                && BCrypt.checkpw(password, account.get().getPassword())) {
            return account.get();
        } else
            return null;
    }

    public void saveLikes(String username, Integer genreId, Integer movieId) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Account cannot be found"));

        account.setFavoriteGenre(genreId);
        account.setFavoriteMovie(movieId);

        accountRepository.save(account);
    }

    public void updateLikes(String username, Integer genreId, Integer movieId) {
        Account account = accountRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("Account cannot be found"));

        account.setFavoriteGenre(genreId);
        account.setFavoriteMovie(movieId);

        accountRepository.save(account);
    }

    public List<AccountDTO> findMatches(String username) {
        Optional<Account> user = accountRepository.findByUsername(username);
        if (user.isEmpty()) {
            throw new IllegalArgumentException("User not found");
        }
        Account temp = user.get();
    
        String favoriteMovie = temp.getFavoriteMovie().toString();
    
        return accountRepository.findByFavoriteMovie(Integer.valueOf(favoriteMovie)).stream()
                .filter(account -> !account.getUsername().equals(username)) 
                .filter(account -> account.getGenderPreference().equals(temp.getGender()))
                .filter(account -> temp.getGenderPreference().equals(account.getGender()))
                .map(account -> {
                    AccountDTO accountDTO = new AccountDTO();
                    accountDTO.setAccountId(account.getAccountId());
                    accountDTO.setUsername(account.getUsername());
                    accountDTO.setFavoriteMovie(account.getFavoriteMovie());
                    return accountDTO;
                })
                .collect(Collectors.toList());
    }

    @Transactional
    public Account updateUsername(String token, String newUsername){
        Account currentAccount = jwtService.decodeToken(token);

        if(newUsername == null || newUsername.isEmpty()){
            throw new IllegalArgumentException("Username cannot be empty");
        }

        if(accountRepository.existsByUsername(newUsername)){
            throw new IllegalArgumentException("Username already exists");
        }
        
        validateUsername(newUsername);
        accountRepository.updateUsername( currentAccount.getAccountId(), newUsername);
        currentAccount.setUsername(newUsername);
        return currentAccount;
        
    }

    @Transactional
    public void updatePassword(String token, String newPassword) {
        Account currentAccount = jwtService.decodeToken(token);

        if(newPassword.isEmpty()){
            throw new IllegalArgumentException("Password cannot be empty");
        }
        validatePassword(newPassword);
        String hashedPass = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        currentAccount.setPassword(hashedPass);
        accountRepository.updatePassword(currentAccount.getUsername(), hashedPass);
    }

    public void updateGenderAndPreference(Account user, GenderType genderPreference, GenderType gender){
        user.setGenderPreference(genderPreference);    
        user.setGender(gender);
        accountRepository.save(user);
    }

    public Account fillAccountInfo(Account account) {
        Optional<Account> accountFromDb = accountRepository.findByUsername(account.getUsername());
        if (accountFromDb.isPresent())
            return accountFromDb.get();
        else
            return null;
    }

    //input validation methods
private void validateUsername(String username){
        if (username == null || username.length() < 5 || username.length() > 20) {
            throw new IllegalArgumentException("Username must be between 5 and 20 characters.");
        }
        if (!username.matches("^[a-zA-Z0-9_.-]*$")) {
            throw new IllegalArgumentException("Username can only contain letters, numbers, dots, dashes, and underscores.");
        }
    }
    
    private void validatePassword(String password) {
        if(password == null || password.length() < 8){
            throw new IllegalArgumentException("Password must be 8 characters minimum.");
        }
        if(!password.matches(".*[A-Z].*")) {
            throw new IllegalArgumentException("Password must contain an uppercase letter.");
        }
        if (!password.matches(".*[a-z].*")) {
            throw new IllegalArgumentException("Password must contain at least one lowercase letter.");
        }
        if (!password.matches(".*\\d.*")) {
            throw new IllegalArgumentException("Password must contain at least one number.");
        }
        if (!password.matches(".*[!@#$%^&*(),.?\":{}|<>].*")) {
            throw new IllegalArgumentException("Password must contain at least one special character.");
        }
    }

}
