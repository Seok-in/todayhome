package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.store.model.*;
import com.example.demo.src.store.*;
import com.example.demo.utils.JwtService;
import com.fasterxml.jackson.databind.ser.Serializers;
import lombok.ToString;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import javax.persistence.NoResultException;

import static com.example.demo.config.BaseResponseStatus.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

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
        if(storeDao.getAllTimeBest(userIdx, categoryName).isEmpty()){
            throw new BaseException(NO_RESULT_DATA);
        }
        try{
            List<PopularProduct> bestProducts = storeDao.getAllTimeBest(userIdx, categoryName);
            return bestProducts;
        }
        catch(Exception exception){
            System.err.println(exception.toString());
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
        catch(EmptyResultDataAccessException e){
            throw new BaseException(NO_RESULT_FOR_CART);
        }
        catch(Exception exception){
            System.err.println(exception.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }
    public GetStoreFirstCtgRes getStoreFirstCtgRes(int userIdx, String categoryName) throws BaseException{
        if(storeDao.getAllTimeBest(userIdx, categoryName).isEmpty()){
            throw new BaseException(NO_RESULT_DATA);
        }
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
        if(storeDao.getSecondCtgBest(userIdx, categoryName).isEmpty()){
            throw new BaseException(NO_RESULT_DATA);
        }
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
        if(storeDao.getQuestionRes(productIdx).isEmpty()){
            throw new BaseException(NO_RESULT_DATA);
        }
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
        //if(Objects.isNull(storeDao.getDeliveryInfoRes(productIdx))){
        //    throw new BaseException(NO_RESULT_DATA);
        //}
        try{
            GetDeliveryInfoRes getDeliveryInfoRes = storeDao.getDeliveryInfoRes(productIdx);
            return getDeliveryInfoRes;
        }
        catch(EmptyResultDataAccessException e){
            throw new BaseException(EMPTY_RESULT_DATA);
        }
        catch (Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public GetProductReviewRes getProductReviewRes(int productIdx, int userIdx) throws BaseException{
        if(storeDao.getUserReviews(userIdx, productIdx).isEmpty()){
            throw new BaseException(NO_RESULT_DATA);
        }
        try{
            GetProductReviewRes getProductReviewRes = new GetProductReviewRes();
            getProductReviewRes.setRate(storeDao.getRate(productIdx));
            int sumRate = (
                            getProductReviewRes.getRate().getFive() + getProductReviewRes.getRate().getFour() +
                            getProductReviewRes.getRate().getThree() + getProductReviewRes.getRate().getTwo() +
                            getProductReviewRes.getRate().getOne());
            getProductReviewRes.setRateNum(sumRate);
            float avgRate = (5*getProductReviewRes.getRate().getFive() + 4*getProductReviewRes.getRate().getFour() +
                    3*getProductReviewRes.getRate().getThree() + 2*getProductReviewRes.getRate().getTwo() +
                    1*getProductReviewRes.getRate().getOne())/sumRate;
            getProductReviewRes.setAvgRate(avgRate);
            getProductReviewRes.setUserReviews(storeDao.getUserReviews(userIdx, productIdx));
            return getProductReviewRes;
        }
        catch (Exception exception){
            System.err.println(exception.toString());
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
            getStoreProductRes.setReviewImages(storeDao.getReviewImgByProduct(productIdx));
            getStoreProductRes.setReviewTodays(storeDao.getProductReviews(productIdx));
            return getStoreProductRes;
        }
        catch(Exception exception){
            System.err.println(exception.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }


}
