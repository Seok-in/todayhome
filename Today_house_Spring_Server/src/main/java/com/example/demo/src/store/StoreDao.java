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
}