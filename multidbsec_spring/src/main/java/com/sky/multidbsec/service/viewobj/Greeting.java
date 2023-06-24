package com.sky.multidbsec.service.viewobj;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Greeting {
    private String username;
    private String password;
    private boolean enabled;

    public Greeting() {
      enabled=true;
    }
  }
