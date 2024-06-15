package org.olahammed.SpringStarter.config;


import java.util.List;

import org.olahammed.SpringStarter.models.Account;
import org.olahammed.SpringStarter.models.Post;
import org.olahammed.SpringStarter.services.AccountService;
import org.olahammed.SpringStarter.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SeedData implements CommandLineRunner{

    @Autowired
    private PostService postService;

    @Autowired
    private AccountService accountService;

    @Override
    public void run(String... args) throws Exception {

        Account account01 = new Account();
        Account account02 = new Account();

        account01.setEmail("account01@ecobank.com");
        account01.setPassword("password");
        account01.setFirstname("USER01");

        account02.setEmail("account02@ecobank.com");
        account02.setPassword("password");
        account02.setFirstname("USER02");

        accountService.save(account01);
        accountService.save(account02);

        List<Post> posts = postService.getAll();
        if (posts.size() == 0) {
            Post post01 = new Post();
            post01.setTitle("Post 01");
            post01.setBody("Post 01 body......................................");
            post01.setAccount(account01);
            postService.save(post01);

            Post post02 = new Post();
            post02.setTitle("Post 02");
            post02.setBody("Post 02 body......................................");
            post02.setAccount(account02);
            postService.save(post02);
        }
    }
    
}
