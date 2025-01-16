package com.moviedating.backend.Controller;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.apache.tomcat.util.http.parser.Authorization;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.RequestEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.moviedating.backend.Entity.Account;
import com.moviedating.backend.Entity.enums.GenderType;
import com.moviedating.backend.Repository.AccountRepository;
import com.moviedating.backend.Service.AccountService;
import com.moviedating.backend.Service.MatchingService;
import com.moviedating.backend.Service.jwtService;
import com.moviedating.backend.dtos.UpdateUsernameDTO;
import com.moviedating.backend.dtos.UpdatePasswordDTO;
import com.moviedating.backend.dtos.FavoritesDTO;
import com.moviedating.backend.dtos.GenderPreferenceDTO;

@RestController
@RequestMapping("/account")
@CrossOrigin(origins = "${front.end.url}")
public class AccountController {

    @Autowired
    AccountService accountService;
    @Autowired
    jwtService jwtService;
    @Autowired
    AccountRepository accountRepository;
    @Autowired
    MatchingService matchingService;

    @PostMapping("/register")
    public ResponseEntity<Account> registerAccount(@RequestBody Account account) {
        Account registeredAccount = accountService.registerAccount(account);

        if (registeredAccount != null) {
            return ResponseEntity.status(HttpStatus.OK).body(registeredAccount);
        } else
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody Account account) {
        Account loggedInAccount = accountService.login(account.getUsername(), account.getPassword());

        if (loggedInAccount != null) {
            String token = jwtService.generateToken(loggedInAccount);

            Optional<Account> optionalAccount = accountRepository.findByUsername(loggedInAccount.getUsername());

            if (optionalAccount.isPresent()) {
                Account retrievedAccount = optionalAccount.get();
                HashMap<String, String> response = new HashMap<>();
                response.put("token", token);
                response.put("id", retrievedAccount.getAccountId().toString());
                response.put("username", retrievedAccount.getUsername());
                response.put("firstName", retrievedAccount.getFirstName());
                response.put("lastName", retrievedAccount.getLastName());
                response.put("gender",
                        retrievedAccount.getGender() != null ? retrievedAccount.getGender().toString() : "");
                response.put("genderPreference",
                        retrievedAccount.getGenderPreference() != null
                                ? retrievedAccount.getGenderPreference().toString()
                                : "");


                return ResponseEntity.status(HttpStatus.OK).body(response);
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No account found");
            }

        } else {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

    }

    @PatchMapping("/update-username")
    public ResponseEntity<?> updateUsername(@RequestBody UpdateUsernameDTO usernameRequest,
            @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || authHeader.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.replace("Bearer ", "");
        Account updatedAccount = accountService.updateUsername(token, usernameRequest.getUsername());
        String newToken = jwtService.generateToken(updatedAccount);
        return ResponseEntity.ok(Map.of("message", "Username updated", "newToken", newToken));
    }

    @PatchMapping("/update-password")
    public ResponseEntity<?> updatePassword(@RequestBody UpdatePasswordDTO passwordRequest,
        @RequestHeader("Authorization") String authHeader) {
        if (authHeader == null || authHeader.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.replace("Bearer ", "");

        accountService.updatePassword(token, passwordRequest.getPassword());

        return ResponseEntity.ok(Map.of("message", "Password updated"));
    }

    @PatchMapping("/update-gender-and-preference")
    public ResponseEntity<String> updateGenderAndPreference(@RequestBody Map<String, String> request,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || authHeader.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.replace("Bearer ", "");
        System.out.println("token " + token);
        Account account = jwtService.decodeToken(token);

        if (account != null) {
            String newGenderPreference = request.get("genderPreference");
            String newGender = request.get("gender");

            GenderType genderPreference;
            GenderType gender;

            genderPreference = GenderType.valueOf(newGenderPreference.toUpperCase());
            gender = GenderType.valueOf(newGender.toUpperCase());
            Account user = accountRepository.findByUsername(account.getUsername()).orElse(null);

            accountService.updateGenderAndPreference(user, genderPreference, gender);

            return ResponseEntity.status(HttpStatus.OK).body("Gender and preference updated successfully");

        } else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();

    }

    // endpoint for favorite genre and movie, also finds a match after updating
    @PostMapping("/choose-favorites")
    public ResponseEntity<?> chooseFavorites(
            @RequestBody FavoritesDTO favorites,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || authHeader.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.replace("Bearer ", "");
        System.out.println("token " + token);
        Account extractedAccount = jwtService.decodeToken(token);
        String username = extractedAccount.getUsername();

        accountService.saveLikes(username, favorites.getGenreId(), favorites.getMovieId());
        return ResponseEntity.ok("Favorite genre and movie updated successfully");

        // incomplete, finish later

        /*
         * //Optional<Account> match = matchingService.matchAccounts(extractedAccount);
         * 
         * if(match.isPresent()) {
         * ResponseEntity.ok(match.get());
         * } else{
         * return
         * ResponseEntity.status(HttpStatus.NOT_FOUND).body("Couldn't find a match");
         * }
         * return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
         */
    }

    @PatchMapping("/update-favorites")
    public ResponseEntity<?> updateFavorites(
            @RequestBody FavoritesDTO favorites,
            @RequestHeader("Authorization") String authHeader) {

        if (authHeader == null || authHeader.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        String token = authHeader.replace("Bearer ", "");
        System.out.println("token " + token);
        Account extractedAccount = jwtService.decodeToken(token);
        String username = extractedAccount.getUsername();

        accountService.updateLikes(username, favorites.getGenreId(), favorites.getMovieId());
        return ResponseEntity.ok("Favorite genre and movie updated successfully");

        // incomplete, finish later

        /*
         * //Optional<Account> match = matchingService.matchAccounts(extractedAccount);
         * 
         * if(match.isPresent()) {
         * ResponseEntity.ok(match.get());
         * } else{
         * return
         * ResponseEntity.status(HttpStatus.NOT_FOUND).body("Couldn't find a match");
         * }
         * return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error");
         */
    }

    @GetMapping("/me")
    public ResponseEntity<Account> authCheck(@RequestHeader(name = "Authorization") String authHeader) {

        System.out.println(authHeader);
        if (authHeader == null || authHeader.trim().isEmpty()) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String token = authHeader.replace("Bearer ", "");
        System.out.println("token " + token);
        Account account = jwtService.decodeToken(token);

        if (account != null) {
            Account temp = accountRepository.findByUsername(account.getUsername()).orElse(null);
            return ResponseEntity.status(HttpStatus.OK).body(temp);
        } else
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build();
    }

    @PostMapping("logout")
    public ResponseEntity<String> logoutUser(@RequestHeader(name = "Authorization") String authorizationHeader) {
        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            String token = authorizationHeader.substring(7);
            try {
                jwtService.invalidateToken(token);
                return ResponseEntity.ok("Logout successful.");
            } catch (Exception e) {
                return ResponseEntity.status(500).body("Failed to invalidate token.");
            }
        }
        return ResponseEntity.status(400).body("Authorization header missing or invalid.");
    }

    /*
     * @DeleteMapping("/delete")
     * public ResponseEntity<String> deleteAccount(@RequestHeader("Authorization")
     * String authHeader) {
     * String token = authHeader.replace("Bearer ", "");
     * accountService.deleteAccount(token);
     * return ResponseEntity.ok("Account has been deleted");
     * }
     */

    
}
