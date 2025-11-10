package com.loctran.store.dtos;

import lombok.Data;

@Data
public class UpdatePasswordRequest {
    String oldPassword;
    String newPassword;
}
