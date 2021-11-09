package com.example.demo.src.mypage;

import com.example.demo.config.BaseException;
import static com.example.demo.config.BaseResponseStatus.*;
import com.example.demo.src.mypage.model.*;
import com.example.demo.src.mypage.model.scrapbook.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;
import java.util.ArrayList;

@Repository
public class MypageDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     Followers 조회
     */
    public List<GetFollowers> getFollowers(int logonIdx,int userIdx){
        int userIdxParams = userIdx;
        int logonIdxParams = logonIdx;
        Object[] getSimilarContentsParams = new Object[]{logonIdxParams, userIdxParams, logonIdxParams};
        String getNewQuery = "SELECT U.userName AS followerName, U.userIntro, U.userImage, IFNULL(followers2.status, 'N') AS followed_by_user, IF(U.userIdx = ?,'Y','N') AS logon_user\n" +
                "FROM User as U\n" +
                "    INNER JOIN (SELECT F.followuserIdx\n" +
                "                FROM UserFollow as F\n" +
                "                WHERE F.userIdx = ? AND F.status = 'Y')  followers ON U.userIdx = followers.followuserIdx\n" +
                "    LEFT OUTER JOIN (SELECT F.status, F.userIdx\n" +
                "                FROM UserFollow as F\n" +
                "                WHERE F.followuserIdx = ? AND F.status = 'Y') followers2 ON followuserIdx = followers2.userIdx";
        return this.jdbcTemplate.query(getNewQuery,
                (rs, rowNum) -> new GetFollowers(
                        rs.getString("followerName"),
                        rs.getString("userIntro"),
                        rs.getString("userImage"),
                        rs.getString("followed_by_user"),
                        rs.getString("logon_user")),
                getSimilarContentsParams
        );
    }

    /**
     Following 조회
     */
    public List<GetFollowers> getFollowing(int logonIdx,int userIdx){
        int userIdxParams = userIdx;
        int logonIdxParams = logonIdx;
        Object[] getSimilarContentsParams = new Object[]{logonIdxParams, userIdxParams, logonIdxParams};
        String getNewQuery = "SELECT U.userName AS following_name, U.userIntro, U.userImage, IFNULL(followers2.status, 'N') AS followed_by_user, IF(U.userIdx = ?,'Y','N') AS logon_user\n" +
                "FROM User as U\n" +
                "    INNER JOIN (SELECT F.userIdx\n" +
                "                FROM UserFollow as F\n" +
                "                WHERE F.followuserIdx = ? AND F.status = 'Y')  followers ON U.userIdx = followers.userIdx\n" +
                "    LEFT OUTER JOIN (SELECT F.status, F.userIxd\n" +
                "                FROM UserFollow as F\n" +
                "                WHERE F.followuserIdx = ? AND F.status = 'Y') followers2 ON followers.userIdx = followers2.userIdx";
        return this.jdbcTemplate.query(getNewQuery,
                (rs, rowNum) -> new GetFollowers(
                        rs.getString("following_name"),
                        rs.getString("userIntro"),
                        rs.getString("userImage"),
                        rs.getString("followed_by_user"),
                        rs.getString("logon_user")),
                getSimilarContentsParams
        );
    }

    /**
     Coupons 조회
     */
    public List<GetCoupons> getCoupons(int myIdx){
        int myIdxParams = myIdx;
        String getNewQuery = "SELECT C.coupon_name, C.discount_percent, C.discount_price, C.enable_price, C.expired_at, C.detailed_explanation, IF(C.couponIdx = UC2.couponIdx, 'Y', 'N') received\n" +
                "FROM Coupon as C\n" +
                "    LEFT OUTER JOIN (SELECT UC.couponIdx, UC.updated_at FROM User_coupon UC WHERE UC.userIdx = ?) AS UC2 ON C.couponIdx = UC2.couponIdx\n" +
                "WHERE C.expired_at > current_timestamp\n" +
                "ORDER BY received desc,\n" +
                "         (CASE WHEN SUBSTRING(coupon_name,1,1) RLIKE '[a-zA-Z]' THEN 1\n" +
                "          WHEN SUBSTRING(coupon_name,1,1) RLIKE '[ㄱ-ㅎ가-힣]' THEN 2 ELSE 3 END), coupon_name;";
        return this.jdbcTemplate.query(getNewQuery,
                (rs, rowNum) -> new GetCoupons(
                        rs.getString("coupon_name"),
                        rs.getDouble("discount_price"),
                        rs.getInt("discount_percent"),
                        rs.getDouble("enable_price"),
                        rs.getString("detailed_explanation"),
                        rs.getString("received")),
                myIdxParams
        );
    }

    /**
     Coupons 발급됐는지 확인
     */
    public int checkReceived(int myIdx, PostPcouponsReq postPcouponsReq){
        String PcouponsReqParams = postPcouponsReq.getCouponName();
        int myIdxParams = myIdx;
        Object[] getPcouponsParams = new Object[]{PcouponsReqParams,myIdx};
        String checkCouponsQuery = "SELECT EXISTS(SELECT * FROM User_coupon UC\n" +
                "              inner join Coupon as C on C.coupon_name = ? and C.couponIdx = UC.couponIdx\n" +
                "              WHERE UC.userIdx = ? and C.expired_at > current_timestamp) received;";
        return this.jdbcTemplate.queryForObject(checkCouponsQuery,
                int.class,
                getPcouponsParams);
    }

   /**
    Coupons 발급 받기
     */
   public int postPcouponsReq(int myIdx, PostPcouponsReq postPcouponsReq){
       int PcouponsReqParams = postPcouponsReq.getCouponId();
       int myIdxParams = myIdx;
       Object[] getPcouponsParams = new Object[]{myIdx, PcouponsReqParams};
       String postPcouponsQuery = "INSERT INTO User_coupon(userIdx, couponIdx) VALUES(?,?);";
       return this.jdbcTemplate.update(postPcouponsQuery, getPcouponsParams);
   }

    /**
     Coupon 코드 발급됐는지 확인
     */
    public String checkUsed(/*PostCodeReq postCodeReq*/String code){
        String CouponCodeParams = /*postCodeReq.getCouponCode()*/code;
        String checkCouponCodeQuery = "SELECT IFNULL((SELECT C.status FROM Coupon AS C\n" +
                "WHERE C.couponCode = ? AND C.open = 'N'),'X') AS codeExists;";
        String checkCouponCodeQuery2 = "SELECT C.status FROM Coupon AS C\n" +
                "WHERE C.couponCode = ?  AND C.open = 'N';";
        String used = this.jdbcTemplate.queryForObject(checkCouponCodeQuery,
                String.class,
                CouponCodeParams);
        if(!used.equals("X"))
            return this.jdbcTemplate.queryForObject(checkCouponCodeQuery2,
                    String.class,
                    CouponCodeParams);
        return used;
    }

    /**
     Coupons 발급 받기 2 (Coupon 코드)
     */
    public int postCodeReq(int myIdx, /*PostCodeReq postCodeReq*/String code){
        int result = 0;

        String CouponCodeParams = /*postCodeReq.getCouponCode()*/code;
        int myIdxParams = myIdx;
        // coupon id 가져오기
        String getCouponIdxQuery = "SELECT C.couponIdx FROM Coupon AS C WHERE C.couponCode = ?;";
        int CouponIdxParams = this.jdbcTemplate.queryForObject(getCouponIdxQuery,int.class,CouponCodeParams);
        Object[] postCouponCodeParams = new Object[]{myIdx, CouponIdxParams};
        // coupon status 상태 수정
        String postCouponCodeQuery = "UPDATE Coupon C SET C.status = 'N' WHERE C.couponCode = ?;";
        // user_coupon에 새로운 값 update
        String postCouponCodeQuery2 = "INSERT INTO User_coupon(userIdx, couponIdx) VALUES(?,?);";

        result = jdbcTemplate.update(postCouponCodeQuery, CouponCodeParams);
        if(result!=0)
            return this.jdbcTemplate.update(postCouponCodeQuery2, postCouponCodeParams);
        return result;
    }

    /**
     개별 Point 조회
     */
    public List<Point> getPoints(int myIdx){
        int myIdxParams = myIdx;
        String getPointQuery = "SELECT Point.pointName, Point.pointText, UP.point, UP.expiredAt, UP.createdAt\n" +
                "FROM UserPoint AS UP\n" +
                "INNER JOIN Point ON UP.pointIdx = Point.pointIdx\n" +
                "WHERE UP.userIdx = ? and UP.expiredAt > current_timestamp;";
        return this.jdbcTemplate.query(getPointQuery,
                (rs, rowNum) -> new Point(
                        rs.getString("pointName"),
                        rs.getString("pointText"),
                        rs.getInt("point"),
                        rs.getString("expiredAt"),
                        rs.getString("createdAt")),
                myIdxParams
        );
    }

    /**
     전체 Point 조회
     */
    public int getUsablePoints(int myIdx){
        int myIdxParams = myIdx;
        String getUsablePointsQuery = "SELECT IFNULL(SUM(UP.point),0)\n" +
                "FROM UserPoint AS UP\n" +
                "WHERE UP.userIdx = ? and UP.expiredAt > current_timestamp";
        return this.jdbcTemplate.queryForObject(getUsablePointsQuery,
                int.class,
                myIdxParams);
    }

    /**
    스크랩북 상단 UserName 조회
     */
    public String getUserName(int userIdx){
        int myIdxParams = userIdx;
        String getUserNameQuery = "SELECT U.userName FROM User U\n" +
                "WHERE U.userIdx = ?";
        return this.jdbcTemplate.queryForObject(getUserNameQuery,
                String.class,
                myIdxParams);
    }

    /**
     전체 스크랩북 & 좋아요 조회
     */
    public List<GetAllScraps> getAllScraps(int userIdx, String filter) throws BaseException {
        Object[] getAllScrapsParams = new Object[]{userIdx, userIdx, userIdx};
        String getAllScrapsQuery = "";
        if (filter.equals("scrapbook"))
            getAllScrapsQuery = "SELECT House.coverImage, UserScrap.flag from House\n" +
                    "INNER JOIN UserScrap ON UserScrap.houseIdx = House.houseIdx\n" +
                    "where UserScrap.userIdx = ?\n" +
                    "union all\n" +
                    "select Knowhow.coverImage,  UserScrap.flag from Knowhow\n" +
                    "INNER JOIN UserScrap ON UserScrap.knowhowIdx = Knowhow.knowhowIdx\n" +
                    "where UserScrap.userIdx = ?\n" +
                    "union all\n" +
                    "select PC.pictureImage,  UserScrap.flag FROM PictureContent as PC\n" +
                    "INNER JOIN UserScrap ON UserScrap.pictureIdx = PC.pictureIdx and PC.Flag = 'Y'\n" +
                    "where UserScrap.userIdx = ?";
        else if (filter.equals("praises"))
            getAllScrapsQuery = "SELECT H.coverImage,UL.flag\n" +
                    "FROM House AS H\n" +
                    "INNER JOIN UserLike UL ON H.houseIdx = UL.houseIdx\n" +
                    "WHERE UL.userIdx = ?\n" +
                    "UNION ALL\n" +
                    "SELECT K.coverImage,UL.flag\n" +
                    "FROM Knowhow AS K\n" +
                    "INNER JOIN UserLike UL ON K.knowhowIdx = UL.knowhowIdx\n" +
                    "WHERE UL.userIdx = ?\n" +
                    "UNION ALL\n" +
                    "SELECT PC.pictureImage, UL.flag\n" +
                    "FROM PictureContent as PC\n" +
                    "INNER JOIN UserLike UL ON UL.pictureIdx = PC.pictureIdx and PC.Flag = 'Y'\n" +
                    "WHERE UL.userIdx = ?";
        else {
            List<GetAllScraps> result = new ArrayList<GetAllScraps>();
            result.add(new GetAllScraps("invalidAccess","N"));
            return result;
            //throw new BaseException(INVALID_USER_ACCESS);
        }
        return this.jdbcTemplate.query(getAllScrapsQuery,
                (rs, rowNum) -> new GetAllScraps(
                        rs.getString("coverImage"),
                        rs.getString("flag")),
                getAllScrapsParams
        );
    }

    /**
     집들이 & 노하우 스크랩북 조회
     */
    public List<GetContentScraps> getContentScraps(int userIdx, String filter, String contents){
        int myIdxParams = userIdx;
        String getContentScrapsQuery = "";
        if(filter.equals("scrapbook")) {
            if (contents.equals("house"))
                getContentScrapsQuery = "SELECT House.coverImage, House.title, User.userName from House\n" +
                        "INNER JOIN UserScrap ON UserScrap.houseIdx = House.houseIdx AND UserScrap.status ='Y'\n" +
                        "INNER JOIN User ON User.userIdx = House.userIdx\n" +
                        "where UserScrap.userIdx = ?";
            else
                getContentScrapsQuery = "SELECT Knowhow.coverImage, Knowhow.title, User.userName from Knowhow\n" +
                        "INNER JOIN UserScrap ON UserScrap.knowhowIdx = Knowhow.knowhowIdx AND UserScrap.status ='Y'\n" +
                        "INNER JOIN User ON User.userIdx = Knowhow.userIdx\n" +
                        "where UserScrap.userIdx = ?";
        }
        else{
            if (contents.equals("house"))
                getContentScrapsQuery = "SELECT House.coverImage, House.title, User.userName from House\n" +
                        "INNER JOIN UserLike ON UserLike.houseIdx = House.houseIdx and UserLike.status ='Y'\n" +
                        "INNER JOIN User ON User.userIdx = House.userIdx\n" +
                        "WHERE UserLike.userIdx = ?";
            else
                getContentScrapsQuery = "SELECT Knowhow.coverImage, Knowhow.title, User.userName from Knowhow\n" +
                        "INNER JOIN UserLike ON UserLike.knowhowIdx = Knowhow.knowhowIdx AND UserLike.status = 'Y'\n" +
                        "INNER JOIN User ON User.userIdx = Knowhow.userIdx\n" +
                        "where UserLike.userIdx = ?";
        }
        return this.jdbcTemplate.query(getContentScrapsQuery,
                (rs, rowNum) -> new GetContentScraps(
                        rs.getString("coverImage"),
                        rs.getString("title"),
                        rs.getString("userName")),
                myIdxParams
        );
    }

    /**
     사진 스크랩북 & 좋아요 조회
     */
    public List<GetPicScraps> getPicScraps(int userIdx, String filter){
        int myIdxParams = userIdx;
        String getPicScrapsQuery = "";
        if(filter.equals("scrapbook"))
            getPicScrapsQuery = "SELECT PC.pictureImage FROM PictureContent AS PC\n" +
                    "INNER JOIN UserScrap ON UserScrap.pictureIdx = PC.pictureIdx\n" +
                    "WHERE UserScrap.userIdx = ? AND UserScrap.status = 'Y'\n" +
                    "GROUP BY PC.pictureIdx";
        else
            getPicScrapsQuery = "SELECT PC.pictureImage FROM PictureContent AS PC\n" +
                    "INNER JOIN UserLike ON UserLike.pictureIdx = PC.pictureIdx\n" +
                    "WHERE UserLike.userIdx = ? AND UserLike.status = 'Y'\n" +
                    "GROUP BY PC.pictureIdx";

        return this.jdbcTemplate.query(getPicScrapsQuery,
                (rs, rowNum) -> new GetPicScraps(
                        rs.getString("pictureImage")),
                myIdxParams
        );
    }

    /**
     상품 스크랩북 조회
     */
    public List<GetProdScraps> getProdScraps(int userIdx){
        int myIdxParams = userIdx;
        String getProdScrapsQuery = "SELECT PI.productImage, Company.companyName, P.productName, P.salesPercent, P.productPrice , IFNULL(R.rate,0) rate, IFNULL(R.cnt,0) cnt FROM Product P\n" +
                "INNER JOIN UserScrap ON UserScrap.productIdx = P.productIdx\n" +
                "INNER JOIN ProductImage PI ON UserScrap.productIdx = PI.productIdx AND PI.imageFlag = 'Y'\n" +
                "INNER JOIN Company ON P.companyIdx = Company.companyIdx\n" +
                "LEFT OUTER JOIN (SELECT productIdx,round(sum(rate)/IFNULL(count(Review.reivewIdx),1),1) rate, count(Review.reivewIdx) AS cnt FROM Review GROUP BY productIdx) R ON R.productIdx = P.productIdx\n" +
                "where UserScrap.userIdx = ?;";

        return this.jdbcTemplate.query(getProdScrapsQuery,
                (rs, rowNum) -> new GetProdScraps(
                        rs.getString("productImage"),
                        rs.getString("companyName"),
                        rs.getString("productName"),
                        rs.getInt("salesPercent"),
                        rs.getInt("productPrice"),
                        rs.getFloat("rate"),
                        rs.getInt("cnt")),
                myIdxParams
        );
    }

}