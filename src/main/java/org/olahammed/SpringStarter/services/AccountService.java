package org.olahammed.SpringStarter.services;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.olahammed.SpringStarter.models.Account;
import org.olahammed.SpringStarter.models.Authority;
import org.olahammed.SpringStarter.repositories.AccountRepository;
import org.olahammed.SpringStarter.util.constants.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AccountService implements UserDetailsService{
    @Value("${spring.mvc.static-path-pattern}")
    private String photo_prefix;
    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public Account save(Account account) {
        if(account.getId() == null){
            account.setCreatedAt(LocalDateTime.now());
        } if (account.getRole() == null){
            account.setRole(Roles.USER.getRole());
        }
        if (account.getPhoto() == null){
            String path = photo_prefix.replace("**", "images/person.png");
            // String path2 = "/resources/static/images/person.png";
            account.setPhoto(path);
        }
        account.setPassword(passwordEncoder.encode(account.getPassword()));
        return accountRepository.save(account);
    }

    public Optional<Account> getById(Long id){
        return accountRepository.findById(id);
    }

    // public String toString(){
    //     return "email: " + acco;
    // }

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        Optional<Account> optionalAccount = accountRepository.findOneByEmailIgnoreCase(email);
        if(!optionalAccount.isPresent()){
        throw new UsernameNotFoundException("Account not found!");
    }
    Account account = optionalAccount.get();

    List<GrantedAuthority> grantedAuthority = new ArrayList<>();
    grantedAuthority.add(new SimpleGrantedAuthority(account.getRole()));

    for(Authority _auth: account.getAuthority()){
        grantedAuthority.add(new SimpleGrantedAuthority(_auth.getName()));
    }
    return new User(account.getEmail(), account.getPassword(), grantedAuthority);
    }

    public Optional<Account> findOneByEmail(String email){
       return  accountRepository.findOneByEmailIgnoreCase(email);
    }

    public Optional<Account> findById(Long id){
        return  accountRepository.findById(id);
     }

}
