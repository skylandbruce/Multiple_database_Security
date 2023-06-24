package com.sky.multidbsec.service.auth;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.provisioning.UserDetailsManager;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    @Qualifier("firstDataSource") 
    private DataSource firstDataSource;
    @Autowired
    @Qualifier("secondDataSource") 
    private DataSource secondDataSource;

    @Autowired
    private PasswordEncoder passwordEncoder;
    
// 회원등록
    public boolean registUser(String username, String password) {
        UserDetails newUser = User.builder()
            .username(username)
            .password(passwordEncoder.encode(password))
            .roles("USER")
            .build();

        UserDetailsManager usersDB2 = new JdbcUserDetailsManager(secondDataSource);    

        try{
            usersDB2.createUser(newUser);
            return true;
        }catch (Exception e){
            System.out.println("fail to regist user");
            System.out.println(e);
            return false;
        }
    }

    // 패스워드 수정
    public boolean updateUserPassword(String username, String oldPassword, String newPassword) {
        UserDetailsManager usersDB1 = new JdbcUserDetailsManager(firstDataSource);    
        UserDetailsManager usersDB2 = new JdbcUserDetailsManager(secondDataSource);    
        // 현재 사용자의 이름이 맞는지 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username_auth = authentication.getName();
        if(!username_auth.equals(username)) return false;
        // 기존 패스워드가 맞는지 확인-password는 보안상 메모리에 두지 않는다
        // if (!passwordEncoder.matches(oldPassword, authentication.getCredentials().toString())) return false;
        
        try{
            UserDetails userDB2=usersDB2.loadUserByUsername(username);
            if(!passwordEncoder.matches(oldPassword, userDB2.getPassword())) return false;

            usersDB1.deleteUser(username);
            usersDB2.changePassword(oldPassword, passwordEncoder.encode(newPassword) );

            return true;
            
        }catch (Exception e){
            System.out.println("fail to change password");
            System.out.println(e);
            return false;
        }
    }

// 탈퇴 - 회원정보 유지를 위해, enabled 를 false로 처리 예정
    public boolean deleteUser() {
        UserDetailsManager usersDB1 = new JdbcUserDetailsManager(firstDataSource);    
        UserDetailsManager usersDB2 = new JdbcUserDetailsManager(secondDataSource);    
        
        // 현재 사용자의 이름이 맞는지 확인
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username_auth = authentication.getName();

        try{
            usersDB1.deleteUser(username_auth);
            usersDB2.deleteUser(username_auth);
            return true;
        }catch (Exception e){
            System.out.println("fail delete user");
            System.out.println(e);
            return false;
        }        
    }
}
