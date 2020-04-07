package com.jumbodinosaurs.auth.server;

import java.time.LocalDateTime;
import java.util.ArrayList;

public class User
{
    private String username;
    private String password;
    private String email;
    private LocalDateTime joinDate;
    private ArrayList<AuthToken> tokens;
    
  
}
