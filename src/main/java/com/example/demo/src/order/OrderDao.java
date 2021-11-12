package com.example.demo.src.order;

import com.example.demo.src.order.model.PostCreateOrderReq;
import com.example.demo.src.store.model.OrderProduct;
import com.example.demo.src.order.model.PostOrderReq;
import com.example.demo.src.store.model.ProductOption;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

@Repository
public class OrderDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    public int createCart(int userIdx){
        String createCartQuery ="insert into Cart (userIdx) VALUES (?);";
        int createCartParams = userIdx;
        this.jdbcTemplate.update(createCartQuery, createCartParams);
        String lastInsertQuery = "select last_insert_id();";
        return this.jdbcTemplate.queryForObject(lastInsertQuery, int.class);
    }

    public void createOrder(PostCreateOrderReq postCreateOrderReq, int cartIdx, int productIdx){
        String createOrderQuery ="insert into GetCart(cartIdx, productIdx, cartFlag, num, firstOptionIdx, secondOptionIdx, thirdOptionIdx)" +
                " VALUES (?, ?, 'D', ?, ?, ?, ?);";
        Object[] createOrderParams = {
                cartIdx,
                productIdx,
                postCreateOrderReq.getProductNum(),
                postCreateOrderReq.getFirstOptionIdx(),
                postCreateOrderReq.getSecondOptionIdx(),
                postCreateOrderReq.getThirdOptionIdx()
        };
        this.jdbcTemplate.update(createOrderQuery, createOrderParams);
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
                "        as GC left join ((SELECT productIdx, productName, companyName, saleValue, salePrice FROM (SELECT productName, productIdx, companyIdx, (productPrice * Product.salePercent/100) as saleValue, (productPrice * (1-Product.salePercent/100)) as salePrice FROM Product) as P\n" +
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

    public int getCartExist(PostCreateOrderReq postCreateOrderReq, int productIdx){
        String existQuery = "SELECT EXIST(SELECT cartIdx FROM GetCart" +
                " WHERE productIdx =? && firstOptionIdx = ? && secondOptionIdx = ? && thirdOptionIdx = ? && status = 'Y' or status = 'N');";
        Object[] checkParmas = new Object[]{
                productIdx,
                postCreateOrderReq.getFirstOptionIdx(),
                postCreateOrderReq.getSecondOptionIdx(),
                postCreateOrderReq.getThirdOptionIdx()
        };
        return this.jdbcTemplate.queryForObject(existQuery, int.class, checkParmas);
    }

    public int checkUserCart(int userIdx){
        String checkQuery = "SELECT exists(select gc.cartIdx from GetCart gc\n" +
                "                left join (SELECT userIdx, cartIdx FROM Cart where userIdx = ?) as c on gc.cartIdx = c.cartIdx\n" +
                "                where status = 'Y' or  status = 'N')";
        int params = userIdx;
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, params);
    }

    public int getCartIdx(int userIdx){
        String getCartIdxQuery ="SELECT DISTINCT GetCart.cartIdx FROM GetCart left join Cart C on C.cartIdx = GetCart.cartIdx " +
                "where userIdx =? && status = 'Y' or status='N';";
        int params = userIdx;
        return this.jdbcTemplate.queryForObject(getCartIdxQuery, int.class, params);
    }

    public void createGetCart(PostCreateOrderReq postCreateOrderReq, int cartIdx, int productIdx){
        String createOrderQuery ="insert into GetCart(cartIdx, productIdx, cartFlag, num, firstOptionIdx, secondOptionIdx, thirdOptionIdx)" +
                " VALUES (?, ?, 'C', ?, ?, ?, ?);";
        Object[] createOrderParams = {
                cartIdx,
                productIdx,
                postCreateOrderReq.getProductNum(),
                postCreateOrderReq.getFirstOptionIdx(),
                postCreateOrderReq.getSecondOptionIdx(),
                postCreateOrderReq.getThirdOptionIdx()
        };
        this.jdbcTemplate.update(createOrderQuery, createOrderParams);
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

    public void createOrderByCart(int cartIdx){
        int params2 = cartIdx;
        String createOrderQuery ="update GetCart set cartFlag = 'D' where cartIdx = ? && status = 'Y';";
        this.jdbcTemplate.update(createOrderQuery, params2);
    }

    public int checkCartStatus(int cartIdx, int userIdx){
        String checkQuery = "select exists(select productIdx FROM GetCart Where status = 'Y' && cartIdx =? && userIdx = ?;";
        int checkParam = cartIdx;
        int userParam = userIdx;
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, checkParam, userParam);
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

    public void changeOrderStatus(int cartIdx){
        String changeQuery = "update GetCart set Status = 'C' WHERE cartIdx = ? && status = 'Y' && cartFlag ='D';";
        int params =cartIdx;
        this.jdbcTemplate.update(changeQuery, params);
    }

    public void orderProduct(PostOrderReq postOrderReq, int userIdx, int cartIdx) {
        String makeOrderQuery = "insert into OrderNow(userIdx, cartIdx, userCall, receiverName, " +
                "receiverCall, address, detailAddress, request, couponIdx, point, payment, price, deliveryPrice )" +
                "VALUES(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?);";
        Object[] params = new Object[]{
                userIdx,
                cartIdx,
                postOrderReq.getUserCall(),
                postOrderReq.getReceiverName(),
                postOrderReq.getReceiverCall(),
                postOrderReq.getAddress(),
                postOrderReq.getDetailAddress(),
                postOrderReq.getRequest(),
                postOrderReq.getCouponIdx(),
                postOrderReq.getPoint(),
                postOrderReq.getPayment(),
                getPrice(cartIdx),
                getDeliveryPrice(cartIdx)
        };
        this.jdbcTemplate.update(makeOrderQuery, params);
    }

    public int getPrice(int cartIdx){
        String getQuery = "SELECT ifNULL(SUM((productPrice + first +second + third) * num,0)as sumPrice\n" +
                "                FROM GetCart GC left join (SELECT productIdx, (productPrice * (100-salePercent)/100) as productprice FROM Product) as P on P.productIdx = GC.productIdx\n" +
                "                                left join (SELECT optionIdx, IFNULL(optionPrice,0) as first FROM ProductFirstOption) as PFO on GC.firstOptionIdx = PFO.optionIdx\n" +
                "                                left join (SELECT secondOptionIdx, IFNULL(optionPrice,0) as second FROM ProductSecondOption) as PSO on GC.secondOptionIdx = PSO.secondOptionIdx\n" +
                "                                left join (SELECT thirdOptionIdx, IFNULL(optionPrice,0) as third FROM ProductThirdOption) as PTO on GC.thirdOptionIdx = PTO.thirdOptionIdx\n" +
                "                WHERE cartIdx = ? && status ='Y';";
        int params= cartIdx;
        return this.jdbcTemplate.queryForObject(getQuery, int.class, params);
    }

    public int getDeliveryPrice(int cartIdx){
        String getQuery="SELECT ifNULL(SUM(deliveryFee),0) as deliveryPrice FROM GetCart GC\n" +
                "    left join (SELECT deliveryFee, productIdx FROM DeliveryFee) as D on GC.productIdx = D.productIdx\n" +
                "WHERE cartIdx = ? && status ='Y';";
        int params = cartIdx;
        return this.jdbcTemplate.queryForObject(getQuery, int.class, params);
    }

    public int checkArea(String Address, int cartIdx){
        String checkQuery = "SELECT EXISTS(SELECT productIdx  FROM DeliveryFee where disabledArea like '%'? && productIdx =\n" +
                "                                                                                  (SELECT productIdx FROM GetCart where cartIdx =?));";
        Object[] params = new Object[]{
                Address,
                cartIdx
        };
        return this.jdbcTemplate.queryForObject(checkQuery,int.class,params);
    }

    public void orderCancel(int cartIdx){
        String changeQuery = "update GetCart set cartFlag ='C' where status = 'Y' && cartIdx=?;";
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
        String changeQuery = "update GetCart set status ='D' where status ='Y' && cartIdx =?;";
        int params = cartIdx;
        this.jdbcTemplate.update(changeQuery, params);
    }

    public int getUserPoint(int userIdx){
        String getQuery = "SELECT SUM(point) as point FROM UserPoint WHERE userIdx =? && expiredAt >= date(NOW());";
        int param = userIdx;
        return this.jdbcTemplate.queryForObject(getQuery, int.class, param);
    }

    public void createUserPoint(int userIdx, int point){
        String createQuery = "insert into UserPoint(userIdx, point, pointIdx) VALUES (?, -?, 3);";
        Object[] params = new Object[]{ userIdx, point };
        this.jdbcTemplate.update(createQuery, params);
    }

    public void createUserCoupon(int userIdx, int couponIdx){
        String createQuery = "update UserCoupon set status='C' where userIdx =? && couponIdx=?;";
        Object[] params = new Object[]{ userIdx, couponIdx };
        this.jdbcTemplate.update(createQuery, params);
    }
}
