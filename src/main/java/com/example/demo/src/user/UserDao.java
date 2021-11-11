package com.example.demo.src.user;


import com.example.demo.src.store.model.*;
import com.example.demo.src.user.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.ArrayList;
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

    public int checkUserName(String name){
        String checkNameQuery = "select exists(select userName from User where userName = ?);";
        String checkNameParams = name;
        return this.jdbcTemplate.queryForObject(checkNameQuery, int.class, checkNameParams);
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
        String getUserInfoQuery = "SELECT IFNULL(userRealName,'값을 입력해주세요') as userRealName,\n" +
                "       IFNULL(userCall, '값을 입력해주세요') as userCall,\n" +
                "       IFNULL(userRecentEmail, '값을 입력해주세요') as userRecentEmail,\n" +
                "       point FROM User left join (SELECT SUM(point) as point, userIdx FROM UserPoint) as UP\n" +
                "on UP.userIdx = User.userIdx where User.userIdx = ?;";
        int params =userIdx;
        return this.jdbcTemplate.queryForObject(getUserInfoQuery,
                (rs, rowNum) -> new UserInfo(
                        rs.getString("userRealName"),
                        rs.getString("userCall"),
                        rs.getString("userRecentEmail"),
                        rs.getInt("point")
                ), params);
    }
    public List<Coupon> getUserCoupon(int userIdx) {
        String getUserCouponQuery = "SELECT C.couponIdx, couponName, discountPercent, discountPrice, enablePrice, expiredAt FROM Coupon C\n" +
                "RIGHT JOIN UserCoupon UC on C.couponIdx = UC.couponIdx WHERE userIdx = ? && expiredAt > date(now());";
        int params = userIdx;
        return this.jdbcTemplate.query(getUserCouponQuery,
                (rs, rowNum) -> new Coupon(
                        rs.getInt("couponIdx"),
                        rs.getString("couponName"),
                        rs.getInt("discountPercent"),
                        rs.getInt("discountPrice"),
                        rs.getInt("enablePrice"),
                        rs.getString("expiredAt")
                ) , params);
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

    public void deleteReview(int reviewIdx){
        String deleteQuery = "Update Review set status ='N' WHERE reviewIdx =?;";
        int params = reviewIdx;
        this.jdbcTemplate.update(deleteQuery,params);
    }
    public int getUserIdxByQuest(int questionIdx){
        String getQuery = "SELECT userIdx FROM Question WHERE questionIdx=?;";
        int params = questionIdx;
        return this.jdbcTemplate.queryForObject(getQuery,int.class,params);
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



        if(postProductQuestReq.getFirstOptionIdx() != 0 || postProductQuestReq.getSecondOptionIdx() != 0 || postProductQuestReq.getThirdOptionIdx() !=0)
        {
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
    public int getReviewNum(int userIdx) {
            String getQuery = "SELECT COUNT(reviewIdx) as reviewNum FROM Review WHERE userIdx = ? && status ='Y';";
            int params = userIdx;
            return this.jdbcTemplate.queryForObject(getQuery, int.class, params);
    }


    public List<String> getReviewImages(int reviewIdx){
        String getReviewImagesQuery = "SELECT reviewImage FROM ReviewImage WHERE status='Y' && reviewIdx =?;";
        int params = reviewIdx;
        return this.jdbcTemplate.query(getReviewImagesQuery,
                (rs, rowNum) -> new String(rs.getString("reviewImage")), params);
    }
    public List<ReviewToday> getReviewTodays(int userIdx){
        String getQuery = "SELECT userName\n" +
                "     , R.reviewIdx\n" +
                "     , firstOptionName\n" +
                "     , secondOptionName\n" +
                "     , thirdOptionName\n" +
                "     , reviewFlag\n" +
                "     , updatedAt\n" +
                "     , reviewText\n" +
                "     , rate\n" +
                "     , priceRate\n" +
                "     , designRate\n" +
                "     , deliveryRate\n" +
                "     , healthRate\n" +
                "\n" +
                "FROM (SELECT * FROM Review WHERE reviewFlag = 'T') as R left join (SELECT userName, userIdx FROM User) as US on R.userIdx = US.userIdx\n" +
                "              left join ((SELECT  orderIndex, O.cartIdx, firstOptionName, secondOptionName, thirdOptionName FROM ((SELECT orderIndex, cartIdx FROM OrderNow) as O\n" +
                "                            left join ((SELECT cartIdx, productIdx, firstOptionName, secondOptionName, thirdOptionName FROM (SELECT cartIdx, productIdx, firstOptionIdx, secondOptionIdx, thirdOptionIdx FROM GetCart) as GC\n" +
                "                            left join (SELECT optionIdx, name as firstOptionName FROM ProductFirstOption) as PFO on GC.firstOptionIdx = PFO.optionIdx\n" +
                "                            left join (SELECT secondOptionIdx, name as secondOptionName FROM ProductSecondOption) as PSO on GC.secondOptionIdx = PSO.secondOptionIdx\n" +
                "                            left join (SELECT thirdOptionIdx, name as thirdOptionName FROM ProductThirdOption) as PTO on GC.thirdOptionIdx = PTO.thirdOptionIdx)) as K on O.cartIdx = K.cartIdx) GROUP BY orderIndex)\n" +
                "                                        ) as T on T.orderIndex = R.orderIndex\n" +
                "              left join DetailRate DR on R.reviewIdx = DR.reviewIdx\n" +
                "WHERE R.userIdx = ?;";
        int userReviewParams = userIdx;
        return this.jdbcTemplate.query(getQuery,
                (rs, rowNum) -> new ReviewToday(
                        rs.getFloat("rate"),
                        getReviewImages(rs.getInt("reviewIdx")),
                        rs.getString("userName"),
                        rs.getString("firstOptionName"),
                        rs.getString("secondOptionName"),
                        rs.getString("thirdOptionName"),
                        rs.getString("updatedAt"),
                        rs.getString("reviewText"),
                        rs.getString("reviewFlag"),
                        rs.getInt("priceRate"),
                        rs.getInt("designRate"),
                        rs.getInt("deliveryRate"),
                        rs.getInt("healthRate")
                ), userReviewParams);
    }

    public List<ReviewOther> getUserReviews(int userIdx) {
            String getQuery = "SELECT userName\n" +
                    "     , R.reviewIdx\n" +
                    "     , productName\n" +
                    "     , reviewFlag\n" +
                    "     , updatedAt\n" +
                    "     , reviewText\n" +
                    "     , rate\n" +
                    "\n" +
                    "FROM (SELECT * FROM Review WHERE reviewFlag = 'O') as R left join (SELECT userName, userIdx FROM User) as US on R.userIdx = US.userIdx\n" +
                    "              left join (SELECT productName, productIdx FROM Product) as P on R.productIdx = P.productIdx\n" +
                    "              left join ((SELECT  orderIndex, O.cartIdx, firstOptionName, secondOptionName, thirdOptionName FROM ((SELECT orderIndex, cartIdx FROM OrderNow) as O\n" +
                    "                            left join ((SELECT cartIdx, productIdx, firstOptionName, secondOptionName, thirdOptionName FROM (SELECT cartIdx, productIdx, firstOptionIdx, secondOptionIdx, thirdOptionIdx FROM GetCart) as GC\n" +
                    "                            left join (SELECT optionIdx, name as firstOptionName FROM ProductFirstOption) as PFO on GC.firstOptionIdx = PFO.optionIdx\n" +
                    "                            left join (SELECT secondOptionIdx, name as secondOptionName FROM ProductSecondOption) as PSO on GC.secondOptionIdx = PSO.secondOptionIdx\n" +
                    "                            left join (SELECT thirdOptionIdx, name as thirdOptionName FROM ProductThirdOption) as PTO on GC.thirdOptionIdx = PTO.thirdOptionIdx)) as K on O.cartIdx = K.cartIdx) GROUP BY orderIndex)\n" +
                    "                                        ) as T on T.orderIndex = R.orderIndex\n" +
                    "WHERE R.userIdx = ?;";
            int userParmas = userIdx;
            return this.jdbcTemplate.query(getQuery,
                    (rs, rowNum) -> new ReviewOther(
                            rs.getFloat("rate"),
                            getReviewImages(rs.getInt("reviewIdx")),
                            rs.getString("productName"),
                            rs.getString("updatedAt"),
                            rs.getString("reviewText"),
                            rs.getString("reviewFlag")
                    ), userParmas);
    }
    public String getUserStatus(String userEmail){
        String query = "SELECT status FROM User WHERE userEmail=?";
        return this.jdbcTemplate.queryForObject(query, String.class, userEmail);
    }
    public int getUserIdxByReview(int reviewIdx){
        String getQuery = "SELECT userIdx FROM Review WHERE reviewIdx =?;";
        int param = reviewIdx;
        return this.jdbcTemplate.queryForObject(getQuery, int.class, param);
    }

    public GetRecentCountRes getRecentCountRes(int userIdx){
        String getQuery = "SELECT  COUNT(userIdx) as allNum\n" +
                "      , COUNT(case when flag = 'C' THEN 1 end) as productNum\n" +
                "      , COUNT(case when flag = 'P' THEN 1 end) as pictureNum\n" +
                "      , COUNT(case when flag = 'H' THEN 1 end) as houseNum\n" +
                "      , COUNT(case when flag = 'K' THEN 1 end) as knowHowNum\n" +
                "FROM UserRecent\n" +
                "WHERE userIdx = ? && date(updatedAt) >= date(subdate(now(), INTERVAL 7 DAY));";
        int params = userIdx;
        return this.jdbcTemplate.queryForObject(getQuery,
                (rs, rowNum) -> new GetRecentCountRes(
                        rs.getInt("allNum"),
                        rs.getInt("productNum"),
                        rs.getInt("pictureNum"),
                        rs.getInt("houseNum"),
                        rs.getInt("knowHowNum")
                ), params);
    }
    /*public int getUserPoint(int userIdx){
        String getQuery = "SELECT SUM(point) as point FROM UserPoint WHERE userIdx =? && expiredAt >= date(NOW());";
        int param = userIdx;
        return this.jdbcTemplate.queryForObject(getQuery, int.class, param);
    }*/

    public float createRate(int reviewIdx, int priceRate, int designRate, int deliveryRate, int healthRate){
        String createQuery = "insert into DetailRate VALUES(?, ?, ?, ?, ?);";
        Object[] params = new Object[]{reviewIdx, priceRate, designRate, deliveryRate, healthRate};
        this.jdbcTemplate.update(createQuery, params);
        float rate = (priceRate+designRate+deliveryRate+healthRate)/4;
        return rate;
    }

    public void createReviewByCart(int userIdx, int productIdx, int orderIndex, PostCreateReviewOhouseReq postCreateReviewOhouseReq){
        String createQuery = "insert into Review(productIdx, userIdx, orderIndex, reviewText, reviewFlag) VALUES(?, ?, ?, ?, 'T');";
        Object[] params = new Object[]{
                productIdx,
                userIdx,
                orderIndex,
                postCreateReviewOhouseReq.getReviewText()
        };
        this.jdbcTemplate.update(createQuery, params);

        String lastInsertQuery = "select last_insert_id();";
        int reviewIdx = this.jdbcTemplate.queryForObject(lastInsertQuery, int.class);

        createReviewImages(reviewIdx, postCreateReviewOhouseReq.getReviewImages());
        float rate = createRate(reviewIdx, postCreateReviewOhouseReq.getPriceRate(), postCreateReviewOhouseReq.getDesignRate(),
                postCreateReviewOhouseReq.getDeliveryRate(), postCreateReviewOhouseReq.getHealthRate());
        String updateRateQuery = "update Review set rate = ? where reviewIdx =?;";
        this.jdbcTemplate.update(updateRateQuery, rate, reviewIdx);

    }
    public void createReviewImages(int reviewIdx, List<String> reviewImages){
        int size = reviewImages.size();
        String createImgQuery = "insert into ReviewImage(reviewIdx, reviewImage) VALUES(?, ?);";
        int params1 = reviewIdx;
        for (int i=0; i<size; i++) {
            String params2 = reviewImages.get(i);
            this.jdbcTemplate.update(createImgQuery, params1, params2);
        }
    }

    public void createReviewByOther(int userIdx, PostCreateReviewReq postCreateReviewReq ){
        String createQuery = "insert into Review(productIdx, userIdx, rate, reviewText, reviewFlag) VALUES(?, ?, ?, ?, 'O');";
        Object[] params = new Object[]{
                postCreateReviewReq.getProductIdx(),
                userIdx,
                postCreateReviewReq.getRate(),
                postCreateReviewReq.getReviewText()
        };
        this.jdbcTemplate.update(createQuery, params);

        String lastInsertQuery = "select last_insert_id();";
        int reviewIdx = this.jdbcTemplate.queryForObject(lastInsertQuery, int.class);

        createReviewImages(reviewIdx, postCreateReviewReq.getReviewImages());
    }

    public void modifyReviewImages(int reviewIdx, List<String> reviewImages) {

        List<String> oldReviewImages = getReviewImages(reviewIdx);
        List<String> deleteReviewImages = new ArrayList<String>();

        // 동일한 리뷰이미지 객체에서 삭제
        for (int i = 0; i < reviewImages.size(); i++) {
            for (int j = 0; j < oldReviewImages.size(); j++) {
                if (reviewImages.get(i) == oldReviewImages.get(j)) {
                    reviewImages.remove(i);
                    deleteReviewImages.add(oldReviewImages.get(j));
                }
            }
        }
        // 안쓰는 리뷰이미지 삭제
        int params1 = reviewIdx;
        String updateImageQuery = "update ReviewImage set status='N' where reviewImage = ? && reviewIdx = ?;";
        for (int i = 0; i < deleteReviewImages.size(); i++) {

            String params2 = deleteReviewImages.get(i);
            this.jdbcTemplate.update(updateImageQuery, params2, params1);
        }
        // 추가된 리뷰이미지 추가
        createReviewImages(reviewIdx, reviewImages);
    }
    public void modifyReviewData(int reviewIdx, PatchReviewReq patchReviewReq){
        String updateReviewQuery = "update Review set rate =? ,reviewText = ? WHERE reviewIdx =?;";
        Object[] params = new Object[]{
                patchReviewReq.getRate(),
                patchReviewReq.getReviewText(),
                reviewIdx
        };
        this.jdbcTemplate.update(updateReviewQuery, params);
    }
    public void modifyOHouseReviewData(int reviewIdx, PatchHouseReviewReq patchHouseReviewReq){
        String updateRateQuery = "update DetailRate set priceRate = ?, designRate = ?, deliveryRate = ? , healthRate =? where reviewIdx = ?;";
        Object[] params1 = new Object[]{
                patchHouseReviewReq.getPriceRate(),
                patchHouseReviewReq.getDesignRate(),
                patchHouseReviewReq.getDeliveryRate(),
                patchHouseReviewReq.getHealthRate(),
                reviewIdx
        };
        this.jdbcTemplate.update(updateRateQuery, params1);

        String updateReviewQuery = "update Review set rate = ?, reviewText = ? WHERE reviewIdx =?;";
        float rate =(patchHouseReviewReq.getPriceRate() +
                patchHouseReviewReq.getDesignRate() +
                patchHouseReviewReq.getDeliveryRate() +
                patchHouseReviewReq.getHealthRate())/4;
        Object[] params4 = new Object[]{
                rate, patchHouseReviewReq.getReviewText(), reviewIdx
        };
        this.jdbcTemplate.update(updateReviewQuery, params4);
    }

    /**
     Follow API
     */
    public int userFollow(int userIdx, int followerIdx){
        String checkFollowHistoryQuery = "SELECT EXISTS(SELECT UF.status\n" +
                "FROM UserFollow AS UF\n" +
                "WHERE UF.userIdx = ? AND UF.followuserIdx = ?) followHistory;";
        String newUserFollowQuery = "INSERT INTO UserFollow (userIdx, followuserIdx)\n" +
                "VALUES (?,?)";
        String updateUserFollowQuery = "UPDATE UserFollow\n" +
                "SET status = 'Y'\n" +
                "WHERE userIdx = ? AND followuserIdx = ?";
        Object[] userFollowParams = new Object[]{userIdx, followerIdx};

        int followHistory = this.jdbcTemplate.queryForObject(checkFollowHistoryQuery,int.class,userFollowParams);

        if(followHistory == 0)
            return this.jdbcTemplate.update(newUserFollowQuery, userFollowParams);
        else
            return this.jdbcTemplate.update(updateUserFollowQuery, userFollowParams);
    }

    /**
     Unfollow API
     */
    public int userUnfollow(int userIdx, int followerIdx){
        String userUnfollowQuery = "UPDATE UserFollow\n" +
                "SET status = 'N'\n" +
                "WHERE userIdx = ? AND followuserIdx = ?";
        Object[] userUnfollowParams = new Object[]{userIdx, followerIdx};
        return this.jdbcTemplate.update(userUnfollowQuery, userUnfollowParams);
    }

    /*public List<UserOrder> userOrders(int userIdx, int months, String flag){
        String getQuery =""
    }*/
}
