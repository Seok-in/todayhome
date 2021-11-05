package com.example.demo.src.user;


import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class UserDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createUser(PostUserReq postUserReq){
        String createUserQuery = "insert into User (userEmail, userPw, userName) VALUES (?,?,?);";
        Object[] createUserParams = new Object[]{postUserReq.getUserEmail(), postUserReq.getUserPw(), postUserReq.getUserName()};
        this.jdbcTemplate.update(createUserQuery, createUserParams);

        String lastInserIdQuery = "select last_insert_id()";
        return this.jdbcTemplate.queryForObject(lastInserIdQuery,int.class);
    }

    public void signoutUser(PostSignOutReq postSignOutReq, int userIdx){
        String createSignOutQuery = "insert into UserSignOut VALUES (?, ?, ?, ?, ?, ?, ?, 'Y', ?);";
        Object[] createSignOutParams = new Object[]{
                userIdx,
                postSignOutReq.getLowUse(),
                postSignOutReq.getReSignup(),
                postSignOutReq.getLowResource(),
                postSignOutReq.getProtection(),
                postSignOutReq.getLowService(),
                postSignOutReq.getEtc(),
                postSignOutReq.getServiceText()
        };
        this.jdbcTemplate.update(createSignOutQuery, createSignOutParams);

        String updateSignOutQuery = "update User set Status = 'D' where userIdx = ?;";
        int updateSignOutParams = userIdx;
        this.jdbcTemplate.update(updateSignOutQuery, updateSignOutParams);

    }
    public void login(int userIdx){
        String loginQuery = "update User set loginStatus = 'Y' where userIdx = ?;";
        int loginParams = userIdx;
        this.jdbcTemplate.update(loginQuery,loginParams);
    }

    public void logout(int userIdx){
        String logoutQuery = "update User set loginStatus = 'N' where userIdx = ?;";
        int logoutParams = userIdx;
        this.jdbcTemplate.update(logoutQuery, logoutParams);
    }

    public int checkEmail(String email){
        String checkEmailQuery = "select exists(select userEmail from User where userEmail = ?);";
        String checkEmailParams = email;
        return this.jdbcTemplate.queryForObject(checkEmailQuery,
                int.class,
                checkEmailParams);
    }

    public User getPwd(PostLoginReq postLoginReq){
        String getPwdQuery = "select userIdx,userEmail,userPw,userName from User where userEmail = ?;";
        String getPwdParams = postLoginReq.getUserEmail();

        return this.jdbcTemplate.queryForObject(getPwdQuery,
                (rs,rowNum)-> new User(
                        rs.getInt("userIdx"),
                        rs.getString("userEmail"),
                        rs.getString("userPw"),
                        rs.getString("userName")
                ),
                getPwdParams
                );
    }

}
