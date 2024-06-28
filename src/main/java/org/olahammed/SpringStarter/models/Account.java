package org.olahammed.SpringStarter.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.springframework.format.annotation.DateTimeFormat;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@Setter
@NoArgsConstructor
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long Id;

    @Email(message = "Invalid email")
    @NotEmpty(message = "Email missing")
    private String email;

    @NotEmpty(message = "Password empty")
    private String password;

    @NotEmpty(message = "Firstname empty")
    private String firstname;

    @NotEmpty(message = "Lastname empty")
    private String lastname;

    private String gender;

    @Min(value = 18)
    @Max(value = 99)
    private int age;

    @DateTimeFormat(pattern = "yyyy-MM-dd")
    private LocalDate date_of_birth;

    private String photo;

    private String role;

    private LocalDateTime createdAt;

    @OneToMany(mappedBy = ("account"))
    private List<Post> posts;

    private String passswordResetToken;

    private LocalDateTime passswordResetTokenExpiry;

    @ManyToMany
    @JoinTable(
        name = "account_authority",
        joinColumns = {@JoinColumn(name="account_id", referencedColumnName = "id")},
        inverseJoinColumns = {@JoinColumn(name="authority_id", referencedColumnName = "id")})
        private Set<Authority> authority = new HashSet<>();
}
