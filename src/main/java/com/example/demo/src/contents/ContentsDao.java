package com.example.demo.src.contents;


import com.example.demo.src.contents.model.*;
import com.example.demo.src.contents.model.house.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import org.springframework.dao.EmptyResultDataAccessException;

import javax.sql.DataSource;
import java.util.List;
import java.util.ArrayList;
import java.util.Objects;

@Repository
public class ContentsDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    /**
     Intro 조회 API
     */
    public List<GetHouseIntro> getHouseIntro(int userIdx, int contentIdx){
        int myIdxParams = userIdx;
        int contentIdxParams = contentIdx;
        Object[] getHouseIntroParams = new Object[]{userIdx, contentIdx};

        String getHouseIntroQuery = "SELECT H.coverImage, H.title, H.house, H.houseSize, H.work, H.worker, H.family, DATE(H.createdAt) createdAt, U.userName, U.userIntro, IFNULL(followers2.status, 'N') AS followedByUser\n" +
                "FROM House H\n" +
                "INNER JOIN User U ON H.userIdx = U.userIdx\n" +
                "LEFT JOIN (SELECT F.status, F.userIdx FROM UserFollow F\n" +
                "                WHERE F.followuserIdx = ?) followers2 ON followers2.userIdx = H.userIdx\n" +
                "WHERE H.houseIdx = ?";

        return this.jdbcTemplate.query(getHouseIntroQuery,
                (rs, rowNum) -> new GetHouseIntro(
                        rs.getString("coverImage"),
                        rs.getString("title"),
                        rs.getString("house"),
                        rs.getString("houseSize"),
                        rs.getString("work"),
                        rs.getString("worker"),
                        rs.getString("family"),
                        rs.getString("createdAt"),
                        rs.getString("userName"),
                        rs.getString("userIntro"),
                        rs.getString("followedByUser")),
                getHouseIntroParams
        );
        //return this.jdbcTemplate.queryForObject(getHouseIntroQuery, GetHouseIntro.class, getHouseIntroParams);
    }



    /**
     중간 컨텐츠 조회 API
     */
    public List<GetHouseContents> getHouseContents(int userIdx, int contentIdx){
        int myIdxParams = userIdx;
        int contentIdxParams = contentIdx;

        String getHouseContentIdxQuery = "SELECT  HC.HouseContentIdx\n" +
                "FROM HouseContent HC\n" +
                "WHERE HC.houseIdx =?";
        String getHouseContentQuery = "SELECT HC.houseImage, HC.houseText\n" +
                "FROM HouseContent HC\n" +
                "WHERE HC.houseIdx =?";
        String getproductQuery = "SELECT PI.productImage\n" +
                "FROM ProductImage PI\n" +
                "INNER JOIN HouseProduct HP on HP.productIdx = PI.productIdx AND PI.imageFlag = 'Y'\n" +
                "WHERE HP.HouseContentIdx = ?";

        List<Integer> ContentIdxList = this.jdbcTemplate.queryForList(getHouseContentIdxQuery, Integer.class, contentIdxParams);
        List<GetTempHouseContents> TempContentsList = this.jdbcTemplate.query(getHouseContentQuery,
                (rs, rowNum) -> new GetTempHouseContents(
                        rs.getString("houseImage"),
                        rs.getString("houseText")),
                contentIdxParams);
        List<GetHouseContents> result = new ArrayList<GetHouseContents>();

       for(int i = 0; i < ContentIdxList.size(); i++){
            List<String> products = this.jdbcTemplate.queryForList(getproductQuery , String.class, ContentIdxList.get(i));
            result.add(new GetHouseContents(TempContentsList.get(i),products));
        }

       return result;
    }

    /**
     SocialInfo 조회 API
     */
    public List<GetSocialInfo> getSocialInfo(int userIdx, int contentIdx){
        int myIdxParams = userIdx;
        int contentIdxParams = contentIdx;
        int commentsCnt = 0;
        Object[] getSocialInfoParams = new Object[]{userIdx, contentIdx};
        Object[] getSocialInfoParams2 = new Object[]{contentIdx,contentIdx};

        String getLikeInfoQuery = "SELECT IFNULL(UL2.cnt,0) cnt, IFNULL(UL3.status,'N') AS likedByUser\n" +
                "FROM (SELECT UL.houseIdx, count(UL.houseIdx) cnt, UL.status FROM UserLike UL GROUP BY UL.houseIdx) UL2\n" +
                "LEFT OUTER JOIN (SELECT IFNULL(UL.houseIdx,0) houseIdx, UL.status FROM UserLike UL WHERE UL.userIdx = ? and UL.status = 'Y') UL3 ON UL3.houseIdx = UL2.houseIdx\n" +
                "WHERE UL2.houseIdx = ? AND UL2.status = 'Y'";

        String getScrapInfoQuery = "SELECT IFNULL(US2.cnt,0) cnt, IFNULL(UL3.status,'N') AS scrappedByUser\n" +
                "FROM (SELECT US.houseIdx, count(US.houseIdx) cnt, US.status FROM UserScrap US GROUP BY US.houseIdx) US2\n" +
                "LEFT OUTER JOIN (SELECT IFNULL(US.houseIdx,0) houseIdx, US.status FROM UserScrap US WHERE US.userIdx = ? and US.status = 'Y') UL3 ON UL3.houseIdx = US2.houseIdx\n" +
                "WHERE US2.houseIdx = ? AND US2.status = 'Y'";

        String getCommentsCntQuery = "SELECT (IFNULL(C2.cnt,0)) + (IFNULL(Rec2.cnt,0)) AS cnt\n" +
                "FROM (SELECT C.houseIdx, count(C.houseIdx) cnt, C.status FROM Comment C GROUP BY C.houseIdx) C2\n" +
                "LEFT OUTER JOIN (SELECT Rec.commentIdx, count(Rec.recommentIdx) cnt, Rec.status FROM Recomment Rec\n" +
                "    INNER JOIN Comment C ON C.commentIdx = Rec.commentIdx AND C.houseIdx = ?\n" +
                "    GROUP BY Rec.commentIdx) Rec2 ON 1\n" +
                "WHERE C2.houseIdx = ? AND C2.status = 'Y'";

        /*SocialInfoFormat LikeInfo = this.jdbcTemplate.query(getLikeInfoQuery,
                (rs, rowNum) -> new SocialInfoFormat(
                        rs.getString("cnt"),
                        rs.getString("likedByUser")),
                getSocialInfoParams);*/
        List<SocialInfoFormat> LikeInfo = this.jdbcTemplate.query(getLikeInfoQuery,
                (rs, rowNum) -> new SocialInfoFormat(
                        rs.getInt("cnt"),
                        rs.getString("likedByUser")),
                getSocialInfoParams
        );
        List<SocialInfoFormat> ScrapInfo = this.jdbcTemplate.query(getScrapInfoQuery,
                (rs, rowNum) -> new SocialInfoFormat(
                        rs.getInt("cnt"),
                        rs.getString("scrappedByUser")),
                getSocialInfoParams
        );
        //List<SocialInfoFormat> ScrapInfo = this.jdbcTemplate.queryForObject(getScrapInfoQuery, SocialInfoFormat.class, getSocialInfoParams);
        /*SocialInfoFormat ScrapInfo = this.jdbcTemplate.query(getScrapInfoQuery,
                (rs, rowNum) -> new SocialInfoFormat(
                        rs.getString("cnt"),
                        rs.getString("scrappedByUser")),
                getSocialInfoParams);*/

        if(LikeInfo.size() == 0)
            LikeInfo.add(new SocialInfoFormat(0,"N"));
        if(ScrapInfo.size()==0)
            ScrapInfo.add(new SocialInfoFormat(0,"N"));

        try{
            commentsCnt = this.jdbcTemplate.queryForObject(getCommentsCntQuery, int.class, getSocialInfoParams2);
        }
        catch(EmptyResultDataAccessException exception){
            List<GetSocialInfo> result = new ArrayList<GetSocialInfo>();
            result.add(new GetSocialInfo(LikeInfo, ScrapInfo, 0));
            return result;
        }

        //if(Objects.isNull(commentsCnt))
         //   commentsCnt = 0;

        GetSocialInfo getSocialInfo = new GetSocialInfo(LikeInfo, ScrapInfo, commentsCnt);
        List<GetSocialInfo> result = new ArrayList<GetSocialInfo>();
        result.add(getSocialInfo);

        return result;
    }

    /**
     최신 댓글 조회 API
     */
    public List<GetComments> getComments(int userIdx, int contentIdx){
        int myIdxParams = userIdx;
        int contentIdxParams = contentIdx;
        Object[] getCommentsParams = new Object[]{userIdx, contentIdx};

        String getCommentsQuery = "SELECT U.userName,C.cText, IFNULL(CL2.cnt,0) likeCnt, IFNULL(CL3.likeFlag,'N') AS likedByUser,\n" +
                "       IF(timestampdiff(year, C.createdAt,current_timestamp) > 0,CONCAT(timestampdiff(year, C.createdAt,current_timestamp),'년'),\n" +
                "           IF(timestampdiff(month, C.createdAt,current_timestamp) > 0,CONCAT(timestampdiff(month, C.createdAt,current_timestamp),'달'),\n" +
                "               IF(timestampdiff(day, C.createdAt,current_timestamp) > 0,CONCAT(timestampdiff(day, C.createdAt,current_timestamp),'일'),\n" +
                "                   IF(timestampdiff(hour, C.createdAt,current_timestamp) > 0,CONCAT(timestampdiff(hour, C.createdAt,current_timestamp),'시간'),\n" +
                "                       IF(timestampdiff(minute, C.createdAt,current_timestamp) > 0,CONCAT(timestampdiff(minute, C.createdAt,current_timestamp),'분'),\n" +
                "                           IF(timestampdiff(second, C.createdAt,current_timestamp) > 0,CONCAT(timestampdiff(SECOND, C.createdAt,current_timestamp),'초'),'N')))))) AS pastTime\n" +
                "FROM Comment C\n" +
                "INNER JOIN User U on U.userIdx = C.userIdx\n" +
                "LEFT OUTER JOIN (SELECT CL.userIdx, CL.commentIdx, count(CL.commentIdx) cnt, CL.likeFlag FROM CommentLike CL GROUP BY CL.commentIdx) CL2 ON CL2.commentIdx = C.commentIdx AND CL2.likeFlag = 'Y'\n" +
                "LEFT OUTER JOIN (SELECT IFNULL(CL.commentIdx,0) commentIdx, CL.likeFlag FROM CommentLike CL WHERE CL.userIdx = ? and CL.likeFlag = 'Y') CL3 ON CL3.commentIdx = C.commentIdx\n" +
                "WHERE C.houseIdx = ?\n" +
                "ORDER BY C.createdAt DESC\n" +
                "LIMIT 5";

        return this.jdbcTemplate.query(getCommentsQuery,
                (rs, rowNum) -> new GetComments(
                        rs.getString("userName"),
                        rs.getString("cText"),
                        rs.getInt("likeCnt"),
                        rs.getString("likedByUser"),
                        rs.getString("pastTime")),
                getCommentsParams
        );
    }

}