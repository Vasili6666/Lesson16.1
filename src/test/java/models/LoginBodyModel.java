package models;

import lombok.Data;
import lombok.AllArgsConstructor;

@Data
@AllArgsConstructor
public class LoginBodyModel {
    private String userName;
    private String password;
}
