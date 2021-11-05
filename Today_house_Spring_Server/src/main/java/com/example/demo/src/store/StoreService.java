package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.src.store.*;
import com.example.demo.src.store.model.PostCreateOrderReq;
import com.example.demo.src.store.model.ProductOption;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;

@Service
public class StoreService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StoreDao storeDao;
    private final StoreProvider storeProvider;
    private final JwtService jwtService;


    @Autowired
    public StoreService(StoreDao storeDao, StoreProvider storeProvider, JwtService jwtService) {
        this.storeDao = storeDao;
        this.storeProvider = storeProvider;
        this.jwtService = jwtService;
    }

    public void createOrder(PostCreateOrderReq postCreateOrderReq, int userIdx) throws BaseException{
        try {
            storeDao.createOrder(postCreateOrderReq, userIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void createGetCart(PostCreateOrderReq postCreateOrderReq, int userIdx) throws BaseException{
        try {
            storeDao.createGetCart(postCreateOrderReq, userIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void createOrderByCart(int userIdx) throws BaseException{
        try{
            storeDao.createOrderByCart(userIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteCartByStatus(int userIdx) throws BaseException{
        try{
            storeDao.deleteCartByStatus(userIdx);
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteCartByProductIdx(int userIdx, int productIdx) throws BaseException{
        try{
            storeDao.deleteCartByProductIdx(userIdx, productIdx);
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    public void deleteCartByOptionIdx(int userIdx, ProductOption productOption) throws BaseException{
        try{
            storeDao.deleteCartByOptionIdx(userIdx, productOption);
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
