package com.example.demo.src.store;

import com.example.demo.src.order.model.PostCreateOrderReq;
import com.example.demo.src.store.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.*;

@Repository
public class StoreDao {
    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public List<GetStoreCategoryRes> getStoreCategory(){
        String getCtgQuery = "select categoryName, categoryLogo from ProductCategory where status = 'Y';";
        return this.jdbcTemplate.query(getCtgQuery,
                (rs, rowNum) -> new GetStoreCategoryRes(
                        rs.getString("categoryName"),
                        rs.getString("categoryLogo")
                ));
   }
    public List<GetStoreCategoryRes> getSubCategory(String categoryName){
        String getCtgQuery = "SELECT categoryName, categoryLogo FROM DetailCategory dc\n" +
                "                                        right join (SELECT categoryIdx From ProductCategory where categoryName = ?) as pc on dc.categoryIdx = pc.categoryIdx\n" +
                "                                WHERE status = 'Y';";
        String ctgNameParams = categoryName;
        return this.jdbcTemplate.query(getCtgQuery,
                (rs, rowNum) -> new GetStoreCategoryRes(
                        rs.getString("categoryName"),
                        rs.getString("categoryLogo")),
                ctgNameParams);
    }

    public List<GetAdRes> getAdRes(){
        String getAdQuery = "select adImage, adUrl from Advertisement where status = 'Y';";
        return this.jdbcTemplate.query(getAdQuery,
                (rs, rowNum) -> new GetAdRes(
                        rs.getString("adImage"),
                        rs.getString("adUrl")
                ));
    }

    public int checkProductScrap(int productIdx, int userIdx){
        String checkScrapQuery = "select exists(select scrapUrl from UserScrap where productIdx = ? && userIdx = ? && Status = 'Y';";
        Object[] checkScrapParams = new Object[]{productIdx,userIdx};

        return this. jdbcTemplate.queryForObject(checkScrapQuery,
                int.class,
                checkScrapParams);
    }



    public List<Product> getRecentProduct(int userIdx){
        String getProductQuery =
                "SELECT productImage\n" +
                "     , companyName\n" +
                "     , productName\n" +
                "     , salePercent\n" +
                "     , (productPrice * (100 - p.salePercent)) as price\n" +
                "     , rate\n" +
                "     , reviewNum\n" +
                "     , IFNULL(us.status, 'N') as scrap\n" +
                "\n" +
                "FROM Product p left join (SELECT productIdx, productimage FROM ProductImage where imageFlag = 'Y')\n" +
                "                                                            as PI on p.productIdx = PI.productIdx\n" +
                "                                left join (SELECT companyIdx, companyName FROM Company) as C on p.companyIdx = C.companyIdx\n" +
                "                                left join (SELECT productIdx, Avg(rate) as rate,COUNT(reviewIdx) as reviewNum FROM Review GROUP BY productIdx) as R on p.productIdx = R.productIdx\n" +
                "                                right join (SELECT productIdx FROM UserRecent where flag='C' and status='Y' and userIdx =?) as ur on p.productIdx = ur.productIdx\n" +
                "                                left join (SELECT productIdx, status FROM UserScrap where flag = 'C' and userIdx = ?) as us on p.productIdx = us.productIdx;";
        int getProductParams = userIdx;
        return this.jdbcTemplate.query(getProductQuery,
                (rs, rowNum) -> new Product(
                        rs.getString("productImage"),
                        rs.getString("companyName"),
                        rs.getString("productName"),
                        rs.getInt("salePercent"),
                        rs.getInt("price"),
                        rs.getFloat("rate"),
                        rs.getInt("reviewNum"),
                        rs.getString("scrap")),
                getProductParams, getProductParams);
    }
    public List<String> getReviewImgByProduct(int productIdx){
        String getQuery = "SELECT reviewImage FROM ReviewImage RI left join (SELECT productIdx, reviewIdx FROM Review where productIdx =?) as R\n" +
                "on RI.reviewIdx = R.reviewIdx;";
        int params = productIdx;
        return this.jdbcTemplate.query(getQuery,
                (rs, rowNum) -> new String(rs.getString("reviewImage")), params);
    }
    public List<PopularProduct> getPopularProduct(int userIdx){
        String getProductQuery = "SELECT productImage\n" +
                "     , companyName\n" +
                "     , productName\n" +
                "     , salePercent\n" +
                "     , (productPrice * (100 - p.salePercent)) as price\n" +
                "     , rate\n" +
                "     , reviewNum\n" +
                "     , visitNum\n" +
                "     , IFNULL(z.status, 'N') as scrap\n" +
                "\n" +
                "FROM Product p left join (SELECT productIdx, productimage FROM ProductImage where imageFlag = 'Y')\n" +
                "                                                            as PI on p.productIdx = PI.productIdx\n" +
                "                                left join (SELECT companyIdx, companyName FROM Company) as C on p.companyIdx = C.companyIdx\n" +
                "                                left join (SELECT productIdx, Avg(rate) as rate,COUNT(reviewIdx) as reviewNum FROM Review GROUP BY productIdx) as R on p.productIdx = R.productIdx\n" +
                "                                left join (SELECT productIdx, status FROM UserScrap where flag = 'C' and userIdx = ?) as z on p.productIdx = z.productIdx\n" +
                "                                left join (SELECT productIdx, COUNT(userIdx) as visitNum\n" +
                "                                            FROM UserRecent\n" +
                "                                            WHERE date(updatedAt) >= date(subdate(now(), INTERVAL 7 DAY))\n" +
                "                                            GROUP BY productIdx\n" +
                "                                            ) as u on p.productIdx = u.productIdx\n" +
                "ORDER BY visitNum DESC, rate DESC;";
        int getProductParams = userIdx;

        return this.jdbcTemplate.query(getProductQuery,
                (rs, rowNum) -> new PopularProduct(
                        rs.getString("productImage"),
                        rs.getString("companyName"),
                        rs.getString("productName"),
                        rs.getInt("salePercent"),
                        rs.getInt("price"),
                        rs.getFloat("rate"),
                        rs.getInt("reviewNum"),
                        rs.getString("scrap"),
                        rs.getInt("visitNum"))
                , getProductParams);
    }

    public List<PopularProduct> getRealTimeBest(int userIdx){
        String getProductQuery = "SELECT productImage\n" +
                "     , companyName\n" +
                "     , productName\n" +
                "     , salePercent\n" +
                "     , (productPrice * (100 - p.salePercent)) as price\n" +
                "     , rate\n" +
                "     , reviewNum\n" +
                "     , visitNum\n" +
                "     , IFNULL(z.status, 'N') as scrap\n" +
                "\n" +
                "FROM Product p left join (SELECT productIdx, productimage FROM ProductImage where imageFlag = 'Y')\n" +
                "                                                            as PI on p.productIdx = PI.productIdx\n" +
                "                                left join (SELECT companyIdx, companyName FROM Company) as C on p.companyIdx = C.companyIdx\n" +
                "                                left join (SELECT productIdx, Avg(rate) as rate,COUNT(reviewIdx) as reviewNum FROM Review GROUP BY productIdx) as R on p.productIdx = R.productIdx\n" +
                "                                left join (SELECT productIdx, status FROM UserScrap where flag = 'C' and userIdx = ?) as z on p.productIdx = z.productIdx\n" +
                "                                left join (SELECT productIdx, COUNT(userIdx) as visitNum\n" +
                "                                            FROM UserRecent\n" +
                "                                            WHERE date(updatedAt) >= date(subdate(now(), INTERVAL 1 DAY))\n" +
                "                                            GROUP BY productIdx\n" +
                "                                            ) as u on p.productIdx = u.productIdx\n" +
                "ORDER BY visitNum DESC, rate DESC;";

        int getBestProductParams = userIdx;

        return this.jdbcTemplate.query(getProductQuery,
                (rs, rowNum) -> new PopularProduct(
                        rs.getString("productImage"),
                        rs.getString("companyName"),
                        rs.getString("productName"),
                        rs.getInt("salePercent"),
                        rs.getInt("price"),
                        rs.getFloat("rate"),
                        rs.getInt("reviewNum"),
                        rs.getString("scrap"),
                        rs.getInt("visitNum"))
                , getBestProductParams);

    }

    public List<PopularProduct> getAllTimeBest(int userIdx, String categoryName){
        String getProductQuery ="SELECT productImage\n" +
                "     , companyName\n" +
                "     , productName\n" +
                "     , salePercent\n" +
                "     , (productPrice * (100 - p.salePercent)) as price\n" +
                "     , rate\n" +
                "     , reviewNum\n" +
                "     , visitNum\n" +
                "     , IFNULL(z.status, 'N') as scrap\n" +
                "\n" +
                "FROM Product p left join (SELECT productIdx, productimage FROM ProductImage where imageFlag = 'Y')\n" +
                "                                                            as PI on p.productIdx = PI.productIdx\n" +
                "                                right join (SELECT categoryIdx FROM ProductCategory where categoryName like ?) as pc on p.categoryIdx = pc.categoryIdx\n" +
                "                                left join (SELECT companyIdx, companyName FROM Company) as C on p.companyIdx = C.companyIdx\n" +
                "                                left join (SELECT productIdx, Avg(rate) as rate,COUNT(reviewIdx) as reviewNum FROM Review GROUP BY productIdx) as R on p.productIdx = R.productIdx\n" +
                "                                left join (SELECT productIdx, status FROM UserScrap where flag = 'C' and userIdx = ?) as z on p.productIdx = z.productIdx\n" +
                "                                left join (SELECT productIdx, COUNT(userIdx) as visitNum\n" +
                "                                            FROM UserRecent\n" +
                "                                            WHERE date(updatedAt) >= date(subdate(now(), INTERVAL 30 DAY))\n" +
                "                                            GROUP BY productIdx\n" +
                "                                            ) as u on p.productIdx = u.productIdx\n" +
                "ORDER BY visitNum DESC, rate DESC;";

        int idxParams = userIdx;
        String categoryParams = categoryName;

        return this.jdbcTemplate.query(getProductQuery,
                (rs, rowNum) -> new PopularProduct(
                        rs.getString("productImage"),
                        rs.getString("companyName"),
                        rs.getString("productName"),
                        rs.getInt("salePercent"),
                        rs.getInt("price"),
                        rs.getFloat("rate"),
                        rs.getInt("reviewNum"),
                        rs.getString("scrap"),
                        rs.getInt("visitNum"))
                , categoryParams, idxParams);
    }

    public List<PopularProduct> getSecondCtgBest(int userIdx, String categoryName){
        String getProductQuery ="SELECT productImage\n" +
                "     , companyName\n" +
                "     , productName\n" +
                "     , salePercent\n" +
                "     , (productPrice * (100 - p.salePercent)) as price\n" +
                "     , rate\n" +
                "     , reviewNum\n" +
                "     , visitNum\n" +
                "     , IFNULL(z.status, 'N') as scrap\n" +
                "\n" +
                "FROM Product p left join (SELECT productIdx, productimage FROM ProductImage where imageFlag = 'Y')\n" +
                "                                                            as PI on p.productIdx = PI.productIdx\n" +
                "                                right join (SELECT categoryIdx FROM DetailCategory where categoryName like ?) as pc on p.categoryIdx = pc.categoryIdx\n" +
                "                                left join (SELECT companyIdx, companyName FROM Company) as C on p.companyIdx = C.companyIdx\n" +
                "                                left join (SELECT productIdx, Avg(rate) as rate,COUNT(reviewIdx) as reviewNum FROM Review GROUP BY productIdx) as R on p.productIdx = R.productIdx\n" +
                "                                left join (SELECT productIdx, status FROM UserScrap where flag = 'C' and userIdx = ?) as z on p.productIdx = z.productIdx\n" +
                "                                left join (SELECT productIdx, COUNT(userIdx) as visitNum\n" +
                "                                            FROM UserRecent\n" +
                "                                            WHERE date(updatedAt) >= date(subdate(now(), INTERVAL 30 DAY))\n" +
                "                                            GROUP BY productIdx\n" +
                "                                            ) as u on p.productIdx = u.productIdx\n" +
                "ORDER BY visitNum DESC, rate DESC;";

        int idxParams = userIdx;
        String categoryParams = categoryName;

        return this.jdbcTemplate.query(getProductQuery,
                (rs, rowNum) -> new PopularProduct(
                        rs.getString("productImage"),
                        rs.getString("companyName"),
                        rs.getString("productName"),
                        rs.getInt("salePercent"),
                        rs.getInt("price"),
                        rs.getFloat("rate"),
                        rs.getInt("reviewNum"),
                        rs.getString("scrap"),
                        rs.getInt("visitNum"))
                , categoryParams, idxParams);
    }













    public List<GetQuestionRes> getQuestionRes(int productIdx){
        String getQuery ="SELECT questionCtgFlag\n" +
                "     , questionText\n" +
                "     , userName\n" +
                "     , ifNULL(firstOptionName, '??????') as firstOptionName\n" +
                "     , ifNULL(secondOptionName, '??????') as secondOptionName\n" +
                "     , ifNULL(thirdOptionName, '??????') as thirdOptionName\n" +
                "     , createdAt\n" +
                "     , secretFlag\n" +
                "     , status\n" +
                "     , ifNULL(answerText, '?????????') as answerText\n" +
                "     , ifNULL(name, '?????????') as name\n" +
                "     , ifNULL(answerCreatedAt, '?????????') as answerCreatedAt\n" +
                "FROM Question q left join (SELECT userName, userIdx FROM User) as u on q.userIdx = u.userIdx\n" +
                "                left join (\n" +
                "                    (SELECT questionIdx, firstOptionIdx, secondOptionIdx, thirdOptionIdx FROM QuestionOption) as a\n" +
                "                    left join (SELECT optionIdx, name as firstOptionName FROM ProductFirstOption) as PFO on a.firstOptionIdx = PFO.optionIdx\n" +
                "                    left join (SELECT secondOptionIdx, name as secondOptionName FROM ProductSecondOption) as PCO on a.secondOptionIdx = PCO.secondOptionIdx\n" +
                "                    left join (SELECT thirdOptionIdx, name as thirdOptionName FROM ProductThirdOption) as PTO on a.thirdOptionIdx = PTO.thirdOptionIdx) on a.questionIdx = q.questionIdx\n" +
                "                left join (SELECT questionIdx, answerText, name, createdAt as answerCreatedAt FROM Answer) as rs on rs.questionIdx=q.questionIdx\n" +
                "WHERE productIdx =? && status != 'N';";
        int params = productIdx;
        return this.jdbcTemplate.query(getQuery,
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



    public GetDeliveryInfoRes getDeliveryInfoRes(int productIdx){
        String getDeliveryQuery = "SELECT deliveryWay\n" +
                "     , deliveryFee\n" +
                "     , ifNULL(paymentWay, '????????????') as paymentWay\n" +
                "     , mountainFee\n" +
                "     , ifNULL(disabledArea,'??????') as disabledArea\n" +
                "     , numDeliveryFlag\n" +
                "     , etcDelivery\n" +
                "     , exchangeFee\n" +
                "     , refundFee\n" +
                "     , address\n" +
                "FROM DeliveryFee left join Exchange E on DeliveryFee.productIdx = E.productIdx\n" +
                "WHERE E.productIdx = ?;";
        int params = productIdx;
        return this.jdbcTemplate.queryForObject(getDeliveryQuery,
                (rs,rowNum) -> new GetDeliveryInfoRes(
                        rs.getString("deliveryWay"),
                        rs.getInt("deliveryFee"),
                        rs.getString("paymentWay"),
                        rs.getInt("mountainFee"),
                        rs.getString("disabledArea"),
                        rs.getString("numDeliveryFlag"),
                        rs.getString("etcDelivery"),
                        rs.getInt("exchangeFee"),
                        rs.getInt("refundFee"),
                        rs.getString("address")
                ), params);
    }

    public List<ReviewToday> getProductReviews(int productIdx){
        String getReviewQuery = "SELECT userName\n" +
                "     , R.reviewIdx\n" +
                "     , productName\n" +
                "     , ifNULL(firstOptionName, '??????') as firstOptionName\n" +
                "     , ifNULL(secondOptionName, '??????') as secondOptionName\n" +
                "     , ifNULL(thirdOptionName, '??????') as thirdOptionName\n" +
                "     , reviewFlag\n" +
                "     , updatedAt\n" +
                "     , reviewText\n" +
                "     , rate\n" +
                "     , priceRate\n" +
                "     , designRate\n" +
                "     , deliveryRate\n" +
                "     , healthRate\n" +
                "\n" +
                "FROM Review R left join (SELECT userName, userIdx FROM User) as US on R.userIdx = US.userIdx\n" +
                "              left join ((SELECT  orderIndex, O.cartIdx, firstOptionName, secondOptionName, thirdOptionName FROM ((SELECT orderIndex, cartIdx FROM OrderNow) as O\n" +
                "                            left join ((SELECT cartIdx, productIdx, firstOptionName, secondOptionName, thirdOptionName FROM (SELECT cartIdx, productIdx, firstOptionIdx, secondOptionIdx, thirdOptionIdx FROM GetCart) as GC\n" +
                "                            left join (SELECT optionIdx, name as firstOptionName FROM ProductFirstOption) as PFO on GC.firstOptionIdx = PFO.optionIdx\n" +
                "                            left join (SELECT secondOptionIdx, name as secondOptionName FROM ProductSecondOption) as PSO on GC.secondOptionIdx = PSO.secondOptionIdx\n" +
                "                            left join (SELECT thirdOptionIdx, name as thirdOptionName FROM ProductThirdOption) as PTO on GC.thirdOptionIdx = PTO.thirdOptionIdx)) as K on O.cartIdx = K.cartIdx) GROUP BY orderIndex)\n" +
                "                                        ) as T on T.orderIndex = R.orderIndex\n" +
                "              left join (SELECT productName, productIdx FROM Product) as P on R.productIdx = P.productIdx\n" +
                "              left join DetailRate DR on R.reviewIdx = DR.reviewIdx\n" +
                "WHERE R.productIdx = ?;";
        int params = productIdx;
        return this.jdbcTemplate.query(getReviewQuery,
                (rs,rowNum) -> new ReviewToday(
                        rs.getInt("rate"),
                        getReviewImages(rs.getInt("reviewIdx")),
                        rs.getString("productName"),
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
                ), params);
    }
    public List<UserReview> getUserReviews(int userIdx,int productIdx){
        String getReviewQuery ="SELECT userName\n" +
                "     , R.reviewIdx\n" +
                "     , ifNULL(firstOptionName, '??????') as firstOptionName\n" +
                "     , ifNULL(secondOptionName, '??????') as secondOptionName\n" +
                "     , ifNULL(thirdOptionName, '??????') as thirdOptionName\n" +
                "     , reviewFlag\n" +
                "     , updatedAt\n" +
                "     , ifNULL(helpfulFlag,'N') as helpfulFlag\n" +
                "     , helpfulNum\n" +
                "     , reviewText\n" +
                "     , rate\n" +
                "     , priceRate\n" +
                "     , designRate\n" +
                "     , deliveryRate\n" +
                "     , healthRate\n" +
                "\n" +
                "FROM Review R left join (SELECT userName, userIdx FROM User) as US on R.userIdx = US.userIdx\n" +
                "              left join ((SELECT  orderIndex, O.cartIdx, firstOptionName, secondOptionName, thirdOptionName FROM ((SELECT orderIndex, cartIdx FROM OrderNow) as O\n" +
                "                            left join ((SELECT cartIdx, productIdx, firstOptionName, secondOptionName, thirdOptionName FROM (SELECT cartIdx, productIdx, firstOptionIdx, secondOptionIdx, thirdOptionIdx FROM GetCart) as GC\n" +
                "                            left join (SELECT optionIdx, name as firstOptionName FROM ProductFirstOption) as PFO on GC.firstOptionIdx = PFO.optionIdx\n" +
                "                            left join (SELECT secondOptionIdx, name as secondOptionName FROM ProductSecondOption) as PSO on GC.secondOptionIdx = PSO.secondOptionIdx\n" +
                "                            left join (SELECT thirdOptionIdx, name as thirdOptionName FROM ProductThirdOption) as PTO on GC.thirdOptionIdx = PTO.thirdOptionIdx)) as K on O.cartIdx = K.cartIdx) GROUP BY orderIndex)\n" +
                "                                        ) as T on T.orderIndex = R.orderIndex\n" +
                "              left join DetailRate DR on R.reviewIdx = DR.reviewIdx\n" +
                "              left join (SELECT COUNT(reviewIdx) as helpfulNum, reviewIdx FROM ReviewHelpful WHERE status = 'Y' GROUP BY reviewIdx) as C on C.reviewIdx =R.reviewIdx\n" +
                "              left join (SELECT reviewIdx, status as helpfulFlag FROM ReviewHelpful WHERE userIdx =?) as RH on RH.reviewIdx = R.reviewIdx\n" +
                "WHERE productIdx = ?;";
        int params1 = userIdx;
        int params2 = productIdx;
        return this.jdbcTemplate.query(getReviewQuery,
                (rs, rowNum) -> new UserReview(
                        getReviewImages(rs.getInt("reviewIdx")),
                        rs.getString("userName"),
                        rs.getString("firstOptionName"),
                        rs.getString("secondOptionName"),
                        rs.getString("thirdOptionName"),
                        rs.getString("reviewFlag"),
                        rs.getString("updatedAt"),
                        rs.getString("helpfulFlag"),
                        rs.getInt("helpfulNum"),
                        rs.getString("reviewText"),
                        rs.getFloat("rate"),
                        rs.getInt("priceRate"),
                        rs.getInt("designRate"),
                        rs.getInt("deliveryRate"),
                        rs.getInt("healthRate")
                ), params1, params2);
    }

    public Rate getRate(int productIdx){
        String getQuery ="SELECT COUNT(case when rate = 5 then 1 END) as fiveRate\n" +
                "     , COUNT(case when rate < 5 && rate >= 4 then 1 END) as fourRate\n" +
                "     , COUNT(case when rate < 4 && rate >= 3 then 1 END) as threeRate\n" +
                "     , COUNT(case when rate < 3 && rate >= 2 then 1 END) as twoRate\n" +
                "     , COUNT(case when rate < 2 then 1 END) as oneRate\n" +
                "FROM Review\n" +
                "WHERE productIdx = ?;";
        int params = productIdx;
        return this.jdbcTemplate.queryForObject(getQuery,
                (rs,rowNum) -> new Rate(
                        rs.getInt("fiveRate"),
                        rs.getInt("fourRate"),
                        rs.getInt("threeRate"),
                        rs.getInt("twoRate"),
                        rs.getInt("oneRate")
                ), params);
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
    public List<String> getReviewImages(int reviewIdx){
        String getReviewImagesQuery = "SELECT reviewImage FROM ReviewImage WHERE status='Y' && reviewIdx =?;";
        int params = reviewIdx;
        return this.jdbcTemplate.query(getReviewImagesQuery,
                (rs, rowNum) -> new String(rs.getString("reviewImage")), params);
    }



    public List<String> getProductImages(int productIdx){
        String getProductImagesQuery = "SELECT productImage From ProductImage WHERE productIdx = ?;";
        int param = productIdx;
        return this.jdbcTemplate.query(getProductImagesQuery,
                (rs, rowNum) -> new String(
                        rs.getString("productImage")
                ), param);
    }

    public GetStoreProduct getStoreProduct(int productIdx){
        String getStoreProductQuery ="SELECT companyName\n" +
                "      , productName\n" +
                "      , salePercent as discountPercent\n" +
                "      , productPrice as originPrice\n" +
                "      , productPrice * (100-salePercent)/100 as salePrice\n" +
                "      , productInfo\n" +
                "      , ifNULL(paymentWay, '????????????') as paymentWay\n" +
                "      , deliveryFee\n" +
                "      , mountainFee\n" +
                "      , reviewNum\n" +
                "      , questionNum\n" +
                "      , rate\n" +
                "FROM Product P left join (SELECT companyIdx, companyName FROM Company) as C ON P.companyIdx = C.companyIdx\n" +
                "                        left join (SELECT productIdx, paymentWay, deliveryFee, mountainFee FROM DeliveryFee) as D ON D.productIdx = P.productIdx\n" +
                "                        left join (SELECT COUNT(reviewIdx) as reviewNum, AVG(rate) as rate, productIdx FROM Review WHERE Review.productIdx =? && status ='Y') as R ON P.productIdx = R.productIdx \n" +
                "                        left join (SELECT COUNT(questionIdx) as questionNum, productIdx FROM Question WHERE status ='Y') as Q ON P.productIdx = Q.productIdx \n" +
                "WHERE P.productIdx =?;";
        int param = productIdx;
        return this.jdbcTemplate.queryForObject(getStoreProductQuery,
                (rs, rowNum) -> new GetStoreProduct(
                        rs.getString("companyName"),
                        rs.getString("productName"),
                        rs.getInt("discountPercent"),
                        rs.getInt("originPrice"),
                        rs.getInt("salePrice"),
                        rs.getString("productInfo"),
                        rs.getString("paymentWay"),
                        rs.getInt("deliveryFee"),
                        rs.getInt("mountainFee"),
                        rs.getInt("reviewNum"),
                        rs.getInt("questionNum"),
                        rs.getInt("rate")
                ), param, param);
    }
}