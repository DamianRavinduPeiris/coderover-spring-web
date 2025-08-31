package com.damian.coderover.dto;

import lombok.*;

import java.io.Serializable;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class UserDTO implements Serializable {
    private String id;

    private String name;
    private String email;
    private String login;
    private String profilePicURL;

    private String company;
    private String blog;
    private String location;
    private String bio;

    private Integer publicRepos;
    private Integer privateRepos;
    private Integer publicGists;
    private Integer followers;
    private Integer following;

    private Boolean siteAdmin;
    private Boolean twoFactorAuth;
    private String accountType;
    private String planName;
    private Long planSpace;
}