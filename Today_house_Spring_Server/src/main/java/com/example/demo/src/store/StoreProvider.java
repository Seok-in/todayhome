package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.store.model.*;
import com.example.demo.src.store.*;
import com.example.demo.utils.JwtService;
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
            System.err.println(exception.toString());
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
}
