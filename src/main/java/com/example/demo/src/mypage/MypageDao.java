package com.example.demo.src.model;


import com.example.demo.src.mypage.model.*;
import com.example.demo.src.mypage.model.scrapbook.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

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
                "                WHERE F.userIdx = ?)  followers ON U.userIdx = followers.followuserIdx\n" +
                "    LEFT OUTER JOIN (SELECT F.status, F.userIdx\n" +
                "                FROM UserFollow as F\n" +
                "                WHERE F.followuserIdx = ?) followers2 ON followuserIdx = followers2.userIdx";
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
                "                WHERE F.followuserIdx = ?)  followers ON U.userIdx = followers.userIdx\n" +
                "    LEFT OUTER JOIN (SELECT F.status, F.userIxd\n" +
                "                FROM UserFollow as F\n" +
                "                WHERE F.followuserIdx = ?) followers2 ON followers.userIdx = followers2.userIdx";
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
     전체 스크랩북 조회
     */
    public List<GetAllScraps> getAllScraps(int myIdx){
        int myIdxParams = myIdx;
        Object[] getAllScrapsParams = new Object[]{myIdx, myIdx, myIdx};
        String getAllScrapsQuery = "SELECT House.coverImage, UserScrap.flag from House\n" +
                "INNER JOIN UserScrap ON UserScrap.houseIdx = House.houseIdx\n" +
                "where UserScrap.userIdx = ?\n" +
                "union all\n" +
                "select Knowhow.coverImage,  UserScrap.flag from Knowhow\n" +
                "INNER JOIN UserScrap ON UserScrap.knowhowIdx = Knowhow.knowhowIdx\n" +
                "where UserScrap.userIdx = ?\n" +
                "union all\n" +
                "select PC.pictureImage,  UserScrap.flag FROM PictureContent as PC\n" +
                "INNER JOIN UserScrap ON UserScrap.pictureIdx = PC.pictureIdx and PC.contentIdx = 1\n" +
                "where UserScrap.userIdx = ?";
        return this.jdbcTemplate.query(getAllScrapsQuery,
                (rs, rowNum) -> new GetAllScraps(
                        rs.getString("coverImage"),
                        rs.getString("flag")),
                getAllScrapsParams
        );
    }

}