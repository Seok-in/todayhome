package com.example.demo.src.oAuthLogin;

import com.example.demo.src.oAuthLogin.model.KakaoPayReq;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;


import javax.sql.DataSource;

@Repository
public class oAuthLoginDao {

    private JdbcTemplate jdbcTemplate;

    @Autowired
    public void setDataSource(DataSource dataSource){
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }
    public int checkUserIdxByOrder(int orderIndex){
        String checkQuery = "SELECT userIdx FROM OrderNow WHERE orderIndex =?;";
        int checkParam = orderIndex;
        return this.jdbcTemplate.queryForObject(checkQuery, int.class, checkParam);
    }
    public KakaoPayReq getPayment(int orderIndex){
        String getQuery = "SELECT orderIndex, userIdx, concat(productName,' 등 ', SUM(num), ' 개') as productName, SUM((productPrice + first +second + third) * num )as sumPrice, SUM(num) as num\n" +
                "FROM GetCart GC left join (SELECT productIdx, productName, productPrice * (100-salePercent)/100 as productprice FROM Product) as P on P.productIdx = GC.productIdx\n" +
                "                left join (SELECT optionIdx, optionPrice as first FROM ProductFirstOption) as PFO on GC.firstOptionIdx = PFO.optionIdx\n" +
                "                left join (SELECT secondOptionIdx, optionPrice as second FROM ProductSecondOption) as PSO on GC.secondOptionIdx = PSO.secondOptionIdx\n" +
                "                left join (SELECT thirdOptionIdx, optionPrice as third FROM ProductThirdOption) as PTO on GC.thirdOptionIdx = PTO.thirdOptionIdx\n" +
                "                left join (SELECT orderIndex, cartIdx, userIdx FROM OrderNow) as O on GC.cartIdx = O.cartIdx\n" +
                "WHERE orderIndex =?;";
        int params = orderIndex;
        return this.jdbcTemplate.queryForObject(getQuery,
                (rs, rowNum) -> new KakaoPayReq(
                        "TC0ONETIME",
                        Integer.toString(rs.getInt("orderIndex")),
                        Integer.toString(rs.getInt("userIdx")),
                        rs.getString("productName"),
                        rs.getInt("num"),
                        rs.getInt("sumPrice"),
                        0,
                        "https://prod.seokin-test.shop",
                        "https://prod.seokin-test.shop",
                        "https://prod.seokin-test.shop"
                ), params);
    }
}
