package com.example.demo.src.store;

import com.example.demo.src.store.model.*;
import org.hibernate.criterion.Order;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

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
                "                                right join (SELECT productIdx FROM UserRecent where flag='P' and status='Y' and userIdx =?) as ur on p.productIdx = ur.productIdx\n" +
                "                                left join (SELECT productIdx, status FROM UserScrap where flag = 'P' and userIdx = ?) as us on p.productIdx = us.productIdx;";
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
                "                                left join (SELECT productIdx, status FROM UserScrap where flag = 'P' and userIdx = ?) as z on p.productIdx = z.productIdx\n" +
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
                "                                left join (SELECT productIdx, status FROM UserScrap where flag = 'P' and userIdx = ?) as z on p.productIdx = z.productIdx\n" +
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
                "                                left join (SELECT productIdx, status FROM UserScrap where flag = 'P' and userIdx = ?) as z on p.productIdx = z.productIdx\n" +
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
                "                                left join (SELECT productIdx, status FROM UserScrap where flag = 'P' and userIdx = ?) as z on p.productIdx = z.productIdx\n" +
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

    public int createCart(int userIdx){
        String createCartQuery ="insert into Cart (userIdx) VALUES (?);";
        int createCartParams = userIdx;
        this.jdbcTemplate.update(createCartQuery, createCartParams);
        String lastInsertQuery = "select last_insert_id();";
        return this.jdbcTemplate.queryForObject(lastInsertQuery, int.class);
    }

    public void createOrder(PostCreateOrderReq postCreateOrderReq, int cartIdx){
        String createOrderQuery ="insert into GetCart(cartIdx, productIdx, cartFlag, num, firstOptionIdx, secondOptionIdx, thirdOptionIdx)" +
                " VALUES (?, ?, 'D', ?, ?, ?, ?);";
        Object[] createOrderParams = {
                cartIdx,
                postCreateOrderReq.getProductIdx(),
                postCreateOrderReq.getProductNum(),
                postCreateOrderReq.getFirstOptionIdx(),
                postCreateOrderReq.getSecondOptionIdx(),
                postCreateOrderReq.getThirdOptionIdx()

        };
        this.jdbcTemplate.update(createOrderQuery, createOrderParams);
    }
    public int checkUserCart(int userIdx){
        String checkQuery = "SELECT exists(select gc.cartIdx from GetCart gc\n" +
                "                left join (SELECT userIdx, cartIdx FROM Cart where userIdx = ?) as c on gc.cartIdx = c.cartIdx\n" +
                "                where status = 'Y' or  status = 'N')";
        int params = userIdx;
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, params);
    }
    public void createGetCart(PostCreateOrderReq postCreateOrderReq, int cartIdx){
        String createOrderQuery ="insert into GetCart(cartIdx, productIdx, cartFlag, num, firstOptionIdx, secondOptionIdx, thirdOptionIdx)" +
                " VALUES (?, ?, 'C', ?, ?, ?, ?);";
        Object[] createOrderParams = {
                cartIdx,
                postCreateOrderReq.getProductIdx(),
                postCreateOrderReq.getProductNum(),
                postCreateOrderReq.getFirstOptionIdx(),
                postCreateOrderReq.getSecondOptionIdx(),
                postCreateOrderReq.getThirdOptionIdx()
        };
        this.jdbcTemplate.update(createOrderQuery, createOrderParams);
    }

    public void createOrderByCart(int cartIdx){
        int params2 = cartIdx;
        String createOrderQuery ="update GetCart set cartFlag = 'D' where cartIdx = ? && status = 'Y';";
        this.jdbcTemplate.update(createOrderQuery, params2);
    }

    public List<OrderProduct> getOrderProducts (int cartIdx){
        String createOrderQuery ="SELECT productName\n" +
                "      , companyName\n" +
                "      , firstOptionName\n" +
                "      , secondOptionName\n" +
                "      , thirdOptionName\n" +
                "      , paymentWay as delivery\n" +
                "      , deliveryFee\n" +
                "      , num\n" +
                "      , saleValue\n"+
                "      , (salePrice + firstPrice + secondPrice + thirdPrice) * num as price\n" +
                "FROM (SELECT productIdx, firstOptionIdx, secondOptionIdx, thirdOptionIdx, num FROM GetCart where cartIdx = ? && status = 'Y' && cartFlag = 'D')\n" +
                "        as GC left join ((SELECT productIdx, productName, companyName, salePrice FROM (SELECT productName, productIdx, companyIdx, (productPrice * Product.salePercent/100) as saleValue, (productPrice * (1-Product.salePercent/100)) as salePrice FROM Product) as P\n" +
                "                                left join (SELECT companyIdx, companyName FROM Company) as C on P.companyIdx = C.companyIdx) as P2\n" +
                "                                left join (SELECT productIdx, deliveryFee, paymentWay FROM DeliveryFee) as DF on P2.productIdx = DF.productIdx) on P2.productIdx = GC.productIdx\n" +
                "              left join (SELECT optionIdx as firstOptionIdx, name as firstOptionName, optionPrice as firstPrice FROM ProductFirstOption) as PFO on GC.firstOptionIdx = PFO.firstOptionIdx\n" +
                "              left join (SELECT secondOptionIdx, name as secondOptionName, optionPrice as secondPrice FROM ProductSecondOption) as PSO on GC.secondOptionIdx = PSO.secondOptionIdx\n" +
                "              left join (SELECT thirdOptionIdx, name as thirdOptionName, optionPrice as thirdPrice FROM ProductThirdOption) as PTO on GC.thirdOptionIdx = PTO.thirdOptionIdx;";
        int params = cartIdx;
        return this.jdbcTemplate.query(createOrderQuery,
                (rs, rowNum) -> new OrderProduct(
                        rs.getString("productName"),
                        rs.getString("companyName"),
                        rs.getString("firstOptionName"),
                        rs.getString("secondOptionName"),
                        rs.getString("thirdOptionName"),
                        rs.getString("delivery"),
                        rs.getInt("saleValue"),
                        rs.getInt("deliveryFee"),
                        rs.getInt("num"),
                        rs.getInt("price"))
                , params);
    }

    public List<OrderProduct> getCartProducts(int cartIdx){
        String createOrderQuery ="SELECT productName\n" +
                "      , companyName\n" +
                "      , firstOptionName\n" +
                "      , secondOptionName\n" +
                "      , thirdOptionName\n" +
                "      , paymentWay as delivery\n" +
                "      , deliveryFee\n" +
                "      , num\n" +
                "      , saleValue\n"+
                "      , (salePrice + firstPrice + secondPrice + thirdPrice) * num as price\n" +
                "FROM (SELECT productIdx, firstOptionIdx, secondOptionIdx, thirdOptionIdx, num FROM GetCart where cartIdx = ? && status = 'Y' && cartFlag = 'D' or cartFlag = 'C')\n" +
                "        as GC left join ((SELECT productIdx, productName, companyName, salePrice, saleValue FROM (SELECT productName, productIdx, companyIdx, (productPrice * Product.salePercent/100) as saleValue, (productPrice * (1-Product.salePercent/100)) as salePrice FROM Product) as P\n" +
                "                                left join (SELECT companyIdx, companyName FROM Company) as C on P.companyIdx = C.companyIdx) as P2\n" +
                "                                left join (SELECT productIdx, deliveryFee, paymentWay FROM DeliveryFee) as DF on P2.productIdx = DF.productIdx) on P2.productIdx = GC.productIdx\n" +
                "              left join (SELECT optionIdx as firstOptionIdx, name as firstOptionName, optionPrice as firstPrice FROM ProductFirstOption) as PFO on GC.firstOptionIdx = PFO.firstOptionIdx\n" +
                "              left join (SELECT secondOptionIdx, name as secondOptionName, optionPrice as secondPrice FROM ProductSecondOption) as PSO on GC.secondOptionIdx = PSO.secondOptionIdx\n" +
                "              left join (SELECT thirdOptionIdx, name as thirdOptionName, optionPrice as thirdPrice FROM ProductThirdOption) as PTO on GC.thirdOptionIdx = PTO.thirdOptionIdx;";
        int params = cartIdx;
        return this.jdbcTemplate.query(createOrderQuery,
                (rs, rowNum) -> new OrderProduct(
                        rs.getString("productName"),
                        rs.getString("companyName"),
                        rs.getString("firstOptionName"),
                        rs.getString("secondOptionName"),
                        rs.getString("thirdOptionName"),
                        rs.getString("delivery"),
                        rs.getInt("saleValue"),
                        rs.getInt("deliveryFee"),
                        rs.getInt("num"),
                        rs.getInt("price"))
                , params);
    }

    public int getCartIdx(int userIdx){
        String getCartIdxQuery ="SELECT DISTINCT GetCart.cartIdx FROM GetCart left join Cart C on C.cartIdx = GetCart.cartIdx " +
                "where userIdx =? && status = 'Y' or status='N';";
        int params = userIdx;
        return this.jdbcTemplate.queryForObject(getCartIdxQuery, int.class, params);
    }

    public void deleteCartByStatus(int cartIdx){
        String createOrderQuery ="update GetCart set status = 'D' where cartIdx = ? && status = 'Y';";
        int params2 = cartIdx;
        this.jdbcTemplate.update(createOrderQuery, params2);
    }

    public void deleteCartByProductIdx(int cartIdx, int productIdx){
        String deleteQuery = "update GetCart set status = 'D' where cartIdx = ? && productIdx = ?;";
        int params2 = cartIdx;
        int params3 = productIdx;
        this.jdbcTemplate.update(deleteQuery, params2, params3);
    }

    public void deleteCartByOptionIdx(int cartIdx, ProductOption productOption){
        String deleteQuery = "update GetCart set status = 'D' where cartIdx = ? && firstOptionIdx = ? " +
                                "&& secondOptionIdx = ? && thirdOptionIdx = ?;";
        Object[] params = new Object[]{
                cartIdx,
                productOption.getFirstOption(),
                productOption.getSecondOption(),
                productOption.getThirdOption()
        };
        this.jdbcTemplate.update(deleteQuery, params);
    }

    public void checkCartProduct(int cartIdx, int productIdx){
        String checkQuery = "update GetCart set status = 'Y' where cartIdx = ? && productIdx = ? && status = 'N';";
        int params = cartIdx;
        int params2 = productIdx;
        this.jdbcTemplate.update(checkQuery, params, params2);
    }

    public void nonCheckCartProduct(int cartIdx, int productIdx){
        String nonCheckQuery = "update GetCart set status = 'N' where cartIdx = ? && productIdx = ? && status = 'Y';";
        int params = cartIdx;
        int params2 = productIdx;
        this.jdbcTemplate.update(nonCheckQuery, params, params2);
    }

    public void allCheckCartProduct(int cartIdx){
        String allCheckQuery = "update GetCart set status = 'Y' where cartIdx = ? && status = 'N';";
        int params = cartIdx;
        this.jdbcTemplate.update(allCheckQuery, params);
    }

    public void allNonCheckCartProduct(int cartIdx){
        String allCheckQuery = "update GetCart set status = 'N' where cartIdx = ? && status = 'Y';";
        int params = cartIdx;
        this.jdbcTemplate.update(allCheckQuery, params);
    }



    public List<GetQuestionRes> getQuestionRes(int productIdx){
        String getQuery ="SELECT questionCtgFlag\n" +
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
                "     , paymentWay\n" +
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
    public void changeOrderStatus(int cartIdx){
        String changeQuery = "update GetCart set Status = 'C' WHERE cartIdx = ? && status = 'Y' && cartFlag ='D';";
        int params =cartIdx;
        this.jdbcTemplate.update(changeQuery, params);
    }

    public void orderProducts(PostOrderReq postOrderReq, int userIdx, int cartIdx) {
        String makeOrderQuery = "insert into OrderNow(userIdx, cartIdx, userCall, receiverName, " +
                "receiverCall, address, detailAddress, request, couponIdx, point, payment, price, deliveryPrice )" +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        Object[] params = new Object[]{
                userIdx,
                cartIdx,
                postOrderReq.getUserInfo().getUserCall(),
                postOrderReq.getReceiverName(),
                postOrderReq.getReceiverCall(),
                postOrderReq.getAddress(),
                postOrderReq.getDetailAddress(),
                postOrderReq.getRequest(),
                postOrderReq.getCouponIdx(),
                postOrderReq.getPoint(),
                postOrderReq.getPayment(),
                postOrderReq.getPrice(),
                postOrderReq.getDeliveryPrice()
        };
        this.jdbcTemplate.update(makeOrderQuery, params);
    }

    public void orderCancel(int cartIdx){
        String changeQuery = "update GetCart cartFlag ='C' where status = 'Y' && cartIdx=?;";
        int params = cartIdx;
        this.jdbcTemplate.update(changeQuery, params);
    }
    public int getDirectCartIdx(int userIdx){
        String getCartIdxQuery ="SELECT DISTINCT GetCart.cartIdx FROM GetCart left join Cart C on C.cartIdx = GetCart.cartIdx " +
                "where userIdx =? && status = 'Y' && cartFlag='D';";
        int params = userIdx;
        return this.jdbcTemplate.queryForObject(getCartIdxQuery, int.class, params);
    }

    public void deleteDirect(int cartIdx){
        String changeQuery = "update GetCart status ='D' where status ='Y' && cartIdx =?;";
        int params = cartIdx;
        this.jdbcTemplate.update(changeQuery, params);
    }
    public List<UserReview> getUserReviews(int userIdx,int productIdx){
        String getReviewQuery ="SELECT userName\n" +
                "     , reviewIdx\n" +
                "     , firstOptionName\n" +
                "     , secondOptionName\n" +
                "     , thirdOptionName\n" +
                "     , reviewFlag\n" +
                "     , updatedAt\n" +
                "     , helpfulFlag\n" +
                "     , helpfulNum\n" +
                "     , reviewText\n" +
                "     , ((priceRate + designRate + deliveryRate + healthRate)/4) as rate\n" +
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
                "      , paymentWay\n" +
                "      , deliveryFee\n" +
                "      , mountainFee\n" +
                "      , reviewNum\n" +
                "      , questionNum\n" +
                "      , rate\n" +
                "FROM Product P left join (SELECT companyIdx, companyName FROM Company) as C ON P.companyIdx = C.companyIdx\n" +
                "                        left join (SELECT productIdx, paymentWay, deliveryFee, mountainFee FROM DeliveryFee) as D ON D.productIdx =P.productIdx\n" +
                "                        left join (SELECT COUNT(reviewIdx) as reviewNum, AVG(rate) as rate, productIdx FROM Review WHERE productIdx =? && status ='Y') as R ON P.productIdx = R.productIdx\n" +
                "                        left join (SELECT COUNT(questionIdx) as questionNum, productIdx FROM Question WHERE productIdx =? && status ='Y') as Q ON P.productIdx = Q.productIdx;";
        int param = productIdx;
        return this.jdbcTemplate.queryForObject(getStoreProductQuery,
                (rs, rowNum) -> new GetStoreProduct(
                        rs.getString("companyName"),
                        rs.getString("productName"),
                        rs.getInt("salePercent"),
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