package org.olahammed.SpringStarter.Controller;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import org.apache.commons.lang3.RandomStringUtils;
import org.olahammed.SpringStarter.models.Account;
import org.olahammed.SpringStarter.services.AccountService;
import org.olahammed.SpringStarter.services.EmailService;
import org.olahammed.SpringStarter.util.AppUtil;
import org.olahammed.SpringStarter.util.email.EmailDetails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.validation.Valid;
import org.springframework.web.bind.annotation.RequestBody;


@Controller
public class AccountController {

    @Autowired
    private AccountService accountService;

    @Autowired
    private EmailService emailService;

    // This @Value will only work if it is a controller or service layer
    @Value("${spring.mvc.static-path-pattern}")
    private String photo_prefix;

    @Value("${password.token.reset..timeout.minutes}")
    private int passwordTokenTimeout;

    @Value("${site.domain}")
    private String siteDomain;

    @GetMapping("/register")
    public String register(Model model) {
        Account account = new Account();
        model.addAttribute("account", account);
        return "account_views/register";
    }

    @PostMapping("/register")
    public String register_user(@Valid @ModelAttribute Account account, BindingResult result) {

        if (result.hasErrors()) {
            return "account_views/register";
        }

        accountService.save(account);

        return "redirect:/";
    }

    @GetMapping("/login")
    public String login(Model model) {

        return "account_views/login";
    }

    @GetMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String profile(Model model, Principal principal) {
        String authUser = "email";
        if (principal != null) {
            authUser = principal.getName();
        }
        Optional<Account> optionalAccount = accountService.findOneByEmail(authUser);
        if (optionalAccount.isPresent()) {
            Account account = optionalAccount.get();
            model.addAttribute("account", account);
            model.addAttribute("photo", account.getPhoto());
            return "account_views/profile";
        } else {
            return "redirect:/?error";
        }
    }

    @GetMapping("/test")
    public String test(Model model) {

        return "account_views/test";
    }

    @PostMapping("/profile")
    @PreAuthorize("isAuthenticated()")
    public String postProfile(@Valid @ModelAttribute Account account, BindingResult bindingResult,
            Principal principal) {
        if (bindingResult.hasErrors()) {
            return "account_views/profile";
        }
        String authUser = "email";
        if (principal != null) {
            authUser = principal.getName();
        }
        Optional<Account> optionalAccount = accountService.findOneByEmail(authUser);
        if (optionalAccount.isPresent()) {
            Account accountById = accountService.findById(account.getId()).get();
            accountById.setAge(account.getAge());
            accountById.setDate_of_birth(account.getDate_of_birth());
            accountById.setFirstname(account.getFirstname());
            accountById.setGender(account.getGender());
            accountById.setLastname(account.getLastname());
            accountById.setPassword(account.getPassword());

            accountService.save(accountById);
            SecurityContextHolder.clearContext();
            return "redirect:/";
        } else {
            return "redirect:/?error";
        }

    }

    @PostMapping("/updatephoto")
    @PreAuthorize("isAuthenticated()")
    public String getUpdatePhoto(@RequestParam(name = "file", required = false) MultipartFile file,
            RedirectAttributes attributes, Principal principal) {
        if (file.isEmpty()) {
            attributes.addAttribute("error", "No file uploaded");
            return "redirect:/profile";
        } else {
            String fileName = StringUtils.cleanPath(file.getOriginalFilename());

            try {
                int length = 10;
                boolean useLetters = true;
                boolean useNumbers = true;
                String generatedString = RandomStringUtils.random(length, useLetters, useNumbers);
                String finalPhotoName = generatedString + fileName;
                String absoluteFileLocation = AppUtil.get_upload_path(finalPhotoName);

                Path path = Paths.get(absoluteFileLocation);
                Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
                attributes.addFlashAttribute("message", "You successfully uploaded your image");

                String authUser = "email";
                if (principal != null) {
                    authUser = principal.getName();
                    Optional<Account> optionalAccount = accountService.findOneByEmail(authUser);
                    if (optionalAccount.isPresent()) {
                        Account account = optionalAccount.get();
                        Account accountById = accountService.findById(account.getId()).get();
                        String relativeFileLocation = photo_prefix.replace("**", "uploads/" + finalPhotoName);
                        accountById.setPhoto(relativeFileLocation);
                        accountService.save(accountById);
                    }
                    try {
                        TimeUnit.SECONDS.sleep(1);
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                    return "redirect:/profile";
                }
            } catch (Exception e) {
            }
        }
        return "redirect:/profile?error";
    }

    @GetMapping("/forgot-password")
    public String forgotPassword(Model model) {

        return "account_views/forgot_password";
    }

    @PostMapping("reset_password")
    public String resetPassword(@RequestParam("email") String _email, RedirectAttributes attributes, Model model) {
        Optional<Account> optionalAccount = accountService.findOneByEmail(_email);
        if (optionalAccount.isPresent()) {
            Account account = accountService.findById(optionalAccount.get().getId()).get();
            String resetToken = UUID.randomUUID().toString();
            account.setToken(resetToken);
            account.setPassswordResetTokenExpiry(LocalDateTime.now().plusMinutes(passwordTokenTimeout));
            accountService.save(account);
            String resetMessage = "This is the reset password link: " + siteDomain + "change-password?token="
                    + resetToken;
            EmailDetails details = new EmailDetails(account.getEmail(), resetMessage, "Reset password Olahammed");
            if (emailService.sendSimpleEmail(details) == false) {
                attributes.addFlashAttribute("error", "Error while sending email, contact admin");
                return "redirect:/forgot-password";
            }
            ;

            attributes.addFlashAttribute("message", "Password reset email sent");
            return "redirect:/login";
        } else {
            attributes.addFlashAttribute("error", "No user found with the email supplied!");
            return "redirect:/forgot-password";
        }
    }

    @GetMapping("/change-password")
    public String changePassword(Model model, @RequestParam("token") String token, RedirectAttributes attributes,
            Principal principal) {
                if (token == null) {
                    attributes.addFlashAttribute("error", "Invalid token");
                    return "redirect:/forgot-password";
                }
        Optional<Account> optionalAccount = accountService.findByToken(token);
        if (optionalAccount.isPresent()) {
            Account account = accountService.findById(optionalAccount.get().getId()).get();
            LocalDateTime now = LocalDateTime.now();
            if (now.isAfter(optionalAccount.get().getPassswordResetTokenExpiry())) {
                attributes.addFlashAttribute("error", "Token Expired!");
                return "redirect:/forgot-password";
            }
            ;
            model.addAttribute("account", account);
            return "account_views/change_password";
        }
        attributes.addFlashAttribute("error", "Token Error!");
        return "redirect:/forgot-password";
    }

    @PostMapping("/change-password")
    public String postChangePassword(@ModelAttribute Account account,RedirectAttributes attributes) {
        Account accountById = accountService.getById(account.getId()).get();
        accountById.setPassword(account.getPassword());
        accountById.setToken("");
        accountService.save(accountById);
        attributes.addFlashAttribute("message", "Password updated succesfully!");
        return "redirect:/login";
    }
    

}
