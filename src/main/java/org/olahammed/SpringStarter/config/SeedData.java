package org.olahammed.SpringStarter.config;


import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.olahammed.SpringStarter.models.Account;
import org.olahammed.SpringStarter.models.Authority;
import org.olahammed.SpringStarter.models.Post;
import org.olahammed.SpringStarter.services.AccountService;
import org.olahammed.SpringStarter.services.AuthorityService;
import org.olahammed.SpringStarter.services.PostService;
import org.olahammed.SpringStarter.util.constants.Priviledges;
import org.olahammed.SpringStarter.util.constants.Roles;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class SeedData implements CommandLineRunner{

    @Autowired
    private PostService postService;

    @Autowired
    private AccountService accountService;

    @Autowired
    private AuthorityService authorityService;

    @Override
    public void run(String... args) throws Exception {

        for(Priviledges auth: Priviledges.values()){

            Authority authority = new Authority();
            authority.setId(auth.getId());
            authority.setName(auth.getPriviledge());
            authorityService.save(authority);
        }

        Account account01 = new Account();
        Account account02 = new Account();
        Account account03 = new Account();
        Account account04 = new Account();

        account01.setEmail("user@user.com");
        account01.setPassword("password");
        account01.setFirstname("user");
        account01.setLastname("lastname");

        account02.setEmail("admin@admin.com");
        account02.setPassword("password");
        account02.setFirstname("admin");
        account02.setLastname("lastname");
        Set<Authority> authoritiesAdmin = new HashSet<>();
        authorityService.getById(Priviledges.SERVE_AS_ADMIN.getId()).ifPresent(authoritiesAdmin::add);
        authorityService.getById(Priviledges.SERVE_AS_EDITOR.getId()).ifPresent(authoritiesAdmin::add);
        account02.setAuthority(authoritiesAdmin);
        account02.setAuthority(authoritiesAdmin);
        account02.setRole(Roles.ADMIN.getRole());

        account03.setEmail("editor@editor.com");
        account03.setPassword("password");
        account03.setFirstname("editor");
        account03.setLastname("lastname");
        Set<Authority> authoritiesEditor = new HashSet<>();
        authorityService.getById(Priviledges.SERVE_AS_EDITOR.getId()).ifPresent(authoritiesEditor::add);
        account03.setAuthority(authoritiesEditor);
        account03.setRole(Roles.EDITOR.getRole());
        // System.out.println(account03);

        account04.setEmail("supereditor@supereditor.com");
        account04.setPassword("password");
        account04.setFirstname("super_editor");
        account04.setLastname("lastname");
        Set<Authority> authorities = new HashSet<>();
        authorityService.getById(Priviledges.RESET_ANY_USER_PASSWORD.getId()).ifPresent(authorities::add);
        authorityService.getById(Priviledges.ACCESS_ADMIN_PANEL.getId()).ifPresent(authorities::add);
        authorityService.getById(Priviledges.SERVE_AS_SUPER_USER.getId()).ifPresent(authorities::add);
        account04.setAuthority(authorities);

        accountService.save(account01);
        accountService.save(account02);
        accountService.save(account03);
        accountService.save(account04);

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
