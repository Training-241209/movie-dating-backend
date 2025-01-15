package com.moviedating.backend.Service;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.moviedating.backend.Entity.Account;
import com.moviedating.backend.Repository.AccountRepository;

@Service
public class MatchingService {
    @Autowired
    AccountRepository accountRepository;

    public Optional<Account> matchAccounts(Account currentAccount) {
        List<Account> possibleMatches = accountRepository.findByFavoriteMovieAndFavoriteGenre(
            currentAccount.getFavoriteGenre(),
            currentAccount.getFavoriteMovie(),
            currentAccount.getAccountId()
        );
        
        //return match with correct gender preference 
        return possibleMatches.stream()
            .filter(account -> isMatch(currentAccount, account))
            .findFirst();
    }

    //make sure both account's gender match the other user's gender preference 

    private boolean isMatch(Account account1, Account account2) {
        return account1.getGenderPreference().equals(account2.getGender()) &&
               account2.getGenderPreference().equals(account1.getGender());
    }
    



}
