package com.example.demo.src.user;


import com.example.demo.src.store.model.GetQuestionRes;
import com.example.demo.src.store.model.OrderProduct;
import com.example.demo.src.store.model.PostProductQuestReq;
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
    public int getUserIdx(String email) {
        String getUserIdxQuery = "select userIdx from User where userEmail =?;";
        return this.jdbcTemplate.queryForObject(getUserIdxQuery,int.class, email);
    }
    public UserInfo getUserInfo(int userIdx){
        String getUserInfoQuery = "select userRealName, userCall, userRecentEmail FROM User Where userIdx = ?;";
        int params =userIdx;
        return this.jdbcTemplate.queryForObject(getUserInfoQuery,
                (rs, rowNum) -> new UserInfo(
                        rs.getString("userRealName"),
                        rs.getString("userCall"),
                        rs.getString("userRecentEmail")
                ), params);
    }

    public List<GetQuestionRes> getQuestionRes(int userIdx){
        String getQuestionQuery ="SELECT questionCtgFlag\n" +
                "     , questionText\n" +
                "     , userName\n" +
                "     , firstOptionName\n" +
                "     , secondOptionName\n" +
                "     , thirdOptionName\n" +
                "     , createdAt\n" +
                "     , secretFlag\n" +
                "     , status\n" +
                "     , answerText\n" +
                "     , name\n" +
                "     , answerCreatedAt\n" +
                "FROM Question q left join (SELECT userName, userIdx FROM User) as u on q.userIdx = u.userIdx\n" +
                "                left join (\n" +
                "                    (SELECT questionIdx, firstOptionIdx, secondOptionIdx, thirdOptionIdx FROM QuestionOption) as a\n" +
                "                    left join (SELECT optionIdx, name as firstOptionName FROM ProductFirstOption) as PFO on a.firstOptionIdx = PFO.optionIdx\n" +
                "                    left join (SELECT secondOptionIdx, name as secondOptionName FROM ProductSecondOption) as PCO on a.secondOptionIdx = PCO.secondOptionIdx\n" +
                "                    left join (SELECT thirdOptionIdx, name as thirdOptionName FROM ProductThirdOption) as PTO on a.thirdOptionIdx = PTO.thirdOptionIdx) on a.questionIdx = q.questionIdx\n" +
                "                left join (SELECT questionIdx, answerText, name, createdAt as answerCreatedAt FROM Answer) as rs on rs.questionIdx=q.questionIdx\n" +
                "WHERE q.userIdx =? && status != 'N';";
        int params = userIdx;
        return this.jdbcTemplate.query(getQuestionQuery,
                (rs, rowNum) -> new GetQuestionRes(
                        rs.getString("questionCtgFlag"),
                        rs.getString("userName"),
                        rs.getString("createdAt"),
                        rs.getString("questionText"),
                        rs.getString("status"),
                        rs.getString("answerText"),
                        rs.getString("name"),
                        rs.getString("answerCreatedAt"),
                        rs.getString("firstOptionName"),
                        rs.getString("secondOptionName"),
                        rs.getString("thirdOptionName"),
                        rs.getString("secretFlag")
                ), params);
    }

    public void deleteQuestion(int questionIdx, int userIdx){
        String deleteQuery = "Update Question set status ='C' WHERE questionIdx =? && userIdx=?;";
        Object[] params = new Object[]{ questionIdx, userIdx };
        this.jdbcTemplate.update(deleteQuery,params);
    }

    public void createQuestion(PostProductQuestReq postProductQuestReq, int userIdx, int productIdx){
        String createQuestionQuery = "insert into Question(userIdx, productIdx, questionCtgFlag, questionText, secretFlag) VALUES (?, ?, ?, ?, ?);";
        Object[] params = new Object[]{
                userIdx,
                productIdx,
                postProductQuestReq.getQuestionCtgFlag(),
                postProductQuestReq.getText(),
                postProductQuestReq.getSecretFlag()
        };
        this.jdbcTemplate.update(createQuestionQuery, params);

        String lastInsertQuery = "select last_insert_id();";
        int questionIdx = this.jdbcTemplate.queryForObject(lastInsertQuery, int.class);

        String createQuestionOption = "insert into QuestionOption VALUES(?, ?, ?, ?);";
        Object[] params2 = new Object[]{
                questionIdx,
                postProductQuestReq.getFirstOptionIdx(),
                postProductQuestReq.getSecondOptionIdx(),
                postProductQuestReq.getThirdOptionIdx()
        };
        this.jdbcTemplate.update(createQuestionOption, params2);
    }

    public List<UserRecent> userRecent(int userIdx){
        String getQuery = "SELECT concat(IFNULL(productImage,''), IFNULL(pictureImage,''), IFNULL(coverImage,''), IFNULL(knowHowImage,'')) as image\n" +
                "      , flag\n" +
                "      , (IFNULL(UR.productIdx, 0) + IFNULL(UR.pictureIdx, 0) + IFNULL(UR.houseIdx, 0) + IFNULL(UR.knowHowIdx, 0)) as indexNumber\n" +
                "FROM UserRecent UR left join (SELECT productIdx, productImage FROM ProductImage WHERE imageFlag = 'Y') as PI on UR.productIdx = PI.productIdx\n" +
                "                   left join (SELECT pictureIdx, pictureImage FROM PictureContent WHERE flag = 'Y') as PC on UR.productIdx = PC.pictureIdx\n" +
                "                   left join (SELECT houseIdx, coverImage FROM House) as H on H.houseIdx = UR.houseIdx\n" +
                "                   left join (SELECT knowhowIdx, coverImage as knowHowImage FROM KnowHow) as K on K.knowHowIdx = UR.knowHowIdx\n" +
                "WHERE userIdx = ? && date(updatedAt) >= date(subdate(now(), INTERVAL 7 DAY));\n";
        int params =userIdx;
        return this.jdbcTemplate.query(getQuery,
                (rs, rowNum) -> new UserRecent(
                        rs.getString("image"),
                        rs.getString("flag"),
                        rs.getInt("indexNumber")
                ), params);
    }

    public List<UserRecent> userRecentByFlag(int userIdx, String flag){
        String getQuery = "SELECT concat(IFNULL(productImage,''), IFNULL(pictureImage,''), IFNULL(coverImage,''), IFNULL(knowHowImage,'')) as image\n" +
                "      , flag\n" +
                "      , (IFNULL(UR.productIdx, 0) + IFNULL(UR.pictureIdx, 0) + IFNULL(UR.houseIdx, 0) + IFNULL(UR.knowHowIdx, 0)) as indexNumber\n" +
                "FROM UserRecent UR left join (SELECT productIdx, productImage FROM ProductImage WHERE imageFlag = 'Y') as PI on UR.productIdx = PI.productIdx\n" +
                "                   left join (SELECT pictureIdx, pictureImage FROM PictureContent WHERE flag = 'Y') as PC on UR.productIdx = PC.pictureIdx\n" +
                "                   left join (SELECT houseIdx, coverImage FROM House) as H on H.houseIdx = UR.houseIdx\n" +
                "                   left join (SELECT knowhowIdx, coverImage as knowHowImage FROM KnowHow) as K on K.knowHowIdx = UR.knowHowIdx\n" +
                "WHERE userIdx = ? && date(updatedAt) >= date(subdate(now(), INTERVAL 7 DAY)) && flag = ?;\n";
        int params =userIdx;
        String params2= flag;
        return this.jdbcTemplate.query(getQuery,
                (rs, rowNum) -> new UserRecent(
                        rs.getString("image"),
                        rs.getString("flag"),
                        rs.getInt("indexNumber")
                ), params, params2);
    }

    public GetRecentCountRes getRecentCountRes(int userIdx){
        String getQuery = "SELECT  COUNT(userIdx) as allNum\n" +
                "      , COUNT(case when flag = 'P' THEN 1 end) as productNum\n" +
                "      , COUNT(case when flag = 'I' THEN 1 end) as pictureNum\n" +
                "      , COUNT(case when flag = 'H' THEN 1 end) as houseNum\n" +
                "      , COUNT(case when flag = 'K' THEN 1 end) as knowHowNum\n" +
                "FROM UserRecent\n" +
                "WHERE userIdx = ? && date(updatedAt) >= date(subdate(now(), INTERVAL 7 DAY));";
        int params = userIdx;
        return this.jdbcTemplate.queryForObject(getQuery,
                (rs, rowNum) -> new GetRecentCountRes(
                        rs.getInt("productNum"),
                        rs.getInt("pictureNum"),
                        rs.getInt("houseNum"),
                        rs.getInt("knowHowNum")
                ), params);
    }

    public List<UserOrder> userOrders(int userIdx, int months, String flag){
        String getQuery =""
    }
}
