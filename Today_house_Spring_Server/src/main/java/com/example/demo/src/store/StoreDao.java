package com.example.demo.src.store;

import com.example.demo.src.store.model.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import javax.sql.DataSource;
import java.util.List;

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

    public List<GetAdRes> getAdRes(){
        String getAdQuery = "select adImage, adUrl from Advertisement where status = 'Y';";
        return this.jdbcTemplate.query(getAdQuery,
                (rs, rowNum) -> new GetAdRes(
                        rs.getString("adImage"),
                        rs.getString("adUrl")
                ));
    }

    public int checkProductScrap(int productIdx, int userIdx){
        String checkScrapQuery = "select exists(select scrapUrl from UserScrap where productIdx = ? && userIdx = ?;";
        Object[] checkScrapParams = new Object[]{productIdx,userIdx};

        return this. jdbcTemplate.queryForObject(checkScrapQuery,
                int.class,
                checkScrapParams);
    }

    public Product getProduct(int productIdx, int userIdx){
        String getProductQuery = "SELECT productImage\n" +
                "     , companyName\n" +
                "     , productName\n" +
                "     , salePercent\n" +
                "     , (productPrice * (100 - p.salePercent)) as price\n" +
                "     , rate\n" +
                "     , reviewNum\n" +
                "FROM Product p left join (SELECT productIdx, productimage FROM ProductImage where imageFlag = 'Y')\n" +
                "                                                            as PI on p.productIdx = PI.productIdx\n" +
                "                left join (SELECT companyIdx, companyName FROM Company) as C on p.productIdx = C.companyIdx\n" +
                "                left join (SELECT productIdx, Avg(rate) as rate,COUNT(reviewIdx) as reviewNum FROM Review) as R on p.productIdx = R.productIdx" +
                "WHERE p.productIdx = ?;";
        int getProductParams = productIdx;
        int scrap = checkProductScrap(productIdx,userIdx);
        return this.jdbcTemplate.queryForObject(getProductQuery,
                (rs, rowNum) -> new Product(
                        rs.getString("productImage"),
                        rs.getString("companyName"),
                        rs.getString("productName"),
                        rs.getInt("salePercent"),
                        rs.getInt("price"),
                        rs.getFloat("rate"),
                        rs.getInt("reviewNum"),
                        scrap),
                getProductParams);
    }
}