package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.store.model.*;
import com.example.demo.src.store.*;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.databind.ser.Serializers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.*;
import java.util.ArrayList;
import java.util.List;

@Service
public class StoreProvider {

    private final StoreDao storeDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public StoreProvider(StoreDao storeDao, JwtService jwtService) {
        this.storeDao = storeDao;
        this.jwtService = jwtService;
    }

    public GetStoreHomeRes getStoreHomeRes(int userIdx) throws BaseException {
        try {
            GetStoreHomeRes getStoreHomeRes = new GetStoreHomeRes();
            getStoreHomeRes.setAdvertisements(storeDao.getAdRes());
            getStoreHomeRes.setStoreCategories(storeDao.getStoreCategory());
            getStoreHomeRes.setPopularProducts(storeDao.getPopularProduct(userIdx));
            getStoreHomeRes.setRecentProducts(storeDao.getRecentProduct(userIdx));

            return getStoreHomeRes;
        }
        catch (Exception exception){
            System.err.println(exception.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<PopularProduct> getRealTimeBest(int userIdx) throws BaseException{
        try{
            List<PopularProduct> bestProducts = storeDao.getRealTimeBest(userIdx);
            return bestProducts;
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<PopularProduct> getAllTimeBest(int userIdx, String categoryName) throws BaseException{
        try{
            List<PopularProduct> bestProducts = storeDao.getAllTimeBest(userIdx, categoryName);
            return bestProducts;
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetCartInfoRes getCartInfoRes(int userIdx) throws BaseException{
        try{
            int cartIdx = storeDao.getCartIdx(userIdx);
            GetCartInfoRes getCartInfoRes = new GetCartInfoRes();
            getCartInfoRes.setOrderProducts(storeDao.getCartProducts(cartIdx));
            int size = getCartInfoRes.getOrderProducts().size();
            int sumDeliveryFee = 0;
            int salePrice = 0;
            int sumPrice = 0;
            for(int i=0; i< size; i++){
                OrderProduct orderProduct = getCartInfoRes.getOrderProducts().get(i);
                sumDeliveryFee += orderProduct.getDeliveryFee();
                salePrice += (orderProduct.getSalePrice() * orderProduct.getNum());
                sumPrice += orderProduct.getPrice();
            }
            getCartInfoRes.setSumDeliveryFee(sumDeliveryFee);
            getCartInfoRes.setSumSales(salePrice);
            getCartInfoRes.setResultPrice(sumPrice);
            getCartInfoRes.setSumPrice(salePrice + sumPrice);

            return getCartInfoRes;
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GetStoreFirstCtgRes getStoreFirstCtgRes(int userIdx, String categoryName) throws BaseException{
        try{
            GetStoreFirstCtgRes getStoreFirstCtgRes = new GetStoreFirstCtgRes();
            getStoreFirstCtgRes.setAdvertisements(storeDao.getAdRes());
            getStoreFirstCtgRes.setSubCategories(storeDao.getSubCategory(categoryName));
            getStoreFirstCtgRes.setPopularProducts(storeDao.getAllTimeBest(userIdx, categoryName));
            return getStoreFirstCtgRes;
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetStoreSecondCtgRes getStoreSecondCtgRes(int userIdx, String categoryName) throws BaseException{
        try{
            GetStoreSecondCtgRes getStoreSecondCtgRes = new GetStoreSecondCtgRes();
            getStoreSecondCtgRes.setAdvertisements(storeDao.getAdRes());
            getStoreSecondCtgRes.setPopularProducts(storeDao.getSecondCtgBest(userIdx, categoryName));
            return getStoreSecondCtgRes;
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public List<GetQuestionRes> getQuestionRes(int productIdx) throws BaseException {
        try{
            List<GetQuestionRes> getQuestionRes = storeDao.getQuestionRes(productIdx);
            return getQuestionRes;
        }
        catch (Exception exception){
            System.err.println(exception.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetDeliveryInfoRes getDeliveryInfoRes(int productIdx) throws BaseException{
        try{
            GetDeliveryInfoRes getDeliveryInfoRes = storeDao.getDeliveryInfoRes(productIdx);
            return getDeliveryInfoRes;
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetProductReviewRes getProductReviewRes(int productIdx, int userIdx) throws BaseException{
        try{
            GetProductReviewRes getProductReviewRes = new GetProductReviewRes();
            getProductReviewRes.setRate(storeDao.getRate(productIdx));
            getProductReviewRes.setUserReviews(storeDao.getUserReviews(userIdx, productIdx));
            return getProductReviewRes;
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetStoreProductRes getStoreProductRes(int productIdx) throws BaseException{
        try{
            GetStoreProductRes getStoreProductRes = new GetStoreProductRes();
            getStoreProductRes.setStoreProduct(storeDao.getStoreProduct(productIdx));
            getStoreProductRes.setProductImages(storeDao.getProductImages(productIdx));
            getStoreProductRes.setAdvertisement(storeDao.getAdRes());
            getStoreProductRes.setRateNum(storeDao.getRate(productIdx));
            return getStoreProductRes;
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
