package org.olahammed.SpringStarter.Controller;

import java.util.List;

import org.olahammed.SpringStarter.models.Post;
import org.olahammed.SpringStarter.services.PostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    @Autowired
    private PostService postService;

    @GetMapping("/")
    public String home(Model model){
        List<Post> posts = postService.getAll();
        model.addAttribute("posts", posts);
        return "home_views/home";
    }

    @GetMapping("/editor")
    public String editor(Model model){
        return "editor";
    }
    
    // @GetMapping("/book")
    // public String book(Model model){
    //     return "book";
    // }
    
}
