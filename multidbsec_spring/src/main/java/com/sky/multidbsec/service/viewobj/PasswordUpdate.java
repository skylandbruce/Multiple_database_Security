package com.sky.multidbsec.service.viewobj;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class PasswordUpdate {
    private String username;
    private String password_old;
    private String password_new;
    private boolean enabled;

    public PasswordUpdate() {
      enabled=true;
    }
  }
