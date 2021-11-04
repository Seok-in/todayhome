package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponseStatus;
import com.example.demo.src.store.model.GetAdRes;
import com.example.demo.src.store.model.GetStoreCategoryRes;
import com.example.demo.src.store.model.GetStoreHomeRes;
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
            getStoreHomeRes.setPopularProdcts(storeDao.getPopularProduct(userIdx));
            getStoreHomeRes.setRecentProducts(storeDao.getRecentProduct(userIdx));

            return getStoreHomeRes;
        }
        catch (Exception exception){
            System.err.println(exception.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
