package com.example.demo.src.model;


import com.example.demo.src.mypage.model.*;
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
        String getNewQuery = "SELECT U.user_name AS follower_name, U.bio, U.profile_image, IFNULL(followers2.status, 'N') AS followed_by_user, IF(U.user_id = ?,'Y','N') AS logon_user\n" +
                "FROM User as U\n" +
                "    INNER JOIN (SELECT F.follower_id\n" +
                "                FROM UserFollow as F\n" +
                "                WHERE F.user_id = ?)  followers ON U.user_id = followers.follower_id\n" +
                "    LEFT OUTER JOIN (SELECT F.status, F.user_id\n" +
                "                FROM UserFollow as F\n" +
                "                WHERE F.follower_id = ?) followers2 ON follower_id = followers2.user_id";
        return this.jdbcTemplate.query(getNewQuery,
                (rs, rowNum) -> new GetFollowers(
                        rs.getString("follower_name"),
                        rs.getString("bio"),
                        rs.getString("profile_image"),
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
        String getNewQuery = "SELECT U.user_name AS following_name, U.bio, U.profile_image, IFNULL(followers2.status, 'N') AS followed_by_user, IF(U.user_id = ?,'Y','N') AS logon_user\n" +
                "FROM User as U\n" +
                "    INNER JOIN (SELECT F.user_id\n" +
                "                FROM UserFollow as F\n" +
                "                WHERE F.follower_id = ?)  followers ON U.user_id = followers.user_id\n" +
                "    LEFT OUTER JOIN (SELECT F.status, F.user_id\n" +
                "                FROM UserFollow as F\n" +
                "                WHERE F.follower_id = ?) followers2 ON followers.user_id = followers2.user_id";
        return this.jdbcTemplate.query(getNewQuery,
                (rs, rowNum) -> new GetFollowers(
                        rs.getString("following_name"),
                        rs.getString("bio"),
                        rs.getString("profile_image"),
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
        String getNewQuery = "SELECT C.coupon_name, C.discount_percent, C.discount_price, C.enable_price, C.expired_at, C.detailed_explanation, IF(C.coupon_idx = UC2.coupon_idx, 'Y', 'N') received\n" +
                "FROM Coupon as C\n" +
                "    LEFT OUTER JOIN (SELECT UC.coupon_idx, UC.updated_at FROM User_coupon UC WHERE UC.user_id = ?) AS UC2 ON C.coupon_idx = UC2.coupon_idx\n" +
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
                "              inner join Coupon as C on C.coupon_name = ? and C.coupon_idx = UC.coupon_idx\n" +
                "              WHERE UC.user_id = ? and C.expired_at > current_timestamp) received;";
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
       String postPcouponsQuery = "INSERT INTO User_coupon(user_id, coupon_idx) VALUES(?,?);";
       return this.jdbcTemplate.update(postPcouponsQuery, getPcouponsParams);
   }

    /**
     Coupon 코드 발급됐는지 확인
     */
    public String checkUsed(/*PostCodeReq postCodeReq*/String code){
        String CouponCodeParams = /*postCodeReq.getCouponCode()*/code;
        String checkCouponCodeQuery = "SELECT IFNULL((SELECT C.status FROM Coupon AS C\n" +
                "WHERE C.coupon_code = ? AND C.open = 'N'),'X') AS codeExists;";
        String checkCouponCodeQuery2 = "SELECT C.status FROM Coupon AS C\n" +
                "WHERE C.coupon_code = ?  AND C.open = 'N';";
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
        String getCouponIdxQuery = "SELECT C.coupon_idx FROM Coupon AS C WHERE C.coupon_code = ?;";
        int CouponIdxParams = this.jdbcTemplate.queryForObject(getCouponIdxQuery,int.class,CouponCodeParams);
        Object[] postCouponCodeParams = new Object[]{myIdx, CouponIdxParams};
        // coupon status 상태 수정
        String postCouponCodeQuery = "UPDATE Coupon C SET C.status = 'N' WHERE C.coupon_code = ?;";
        // user_coupon에 새로운 값 update
        String postCouponCodeQuery2 = "INSERT INTO User_coupon(user_id, coupon_idx) VALUES(?,?);";

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

}