package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.src.store.*;
import com.example.demo.src.store.model.PostProductQuestReq;
import com.example.demo.src.user.UserDao;
import com.example.demo.src.store.model.PostCreateOrderReq;
import com.example.demo.src.store.model.PostCreateOrderRes;
import com.example.demo.src.store.model.ProductOption;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.EMPTY_RESULT_DATA;

@Service
public class StoreService {

    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final StoreDao storeDao;
    private final StoreProvider storeProvider;
    private final JwtService jwtService;
    private final UserDao userDao;


    @Autowired
    public StoreService(StoreDao storeDao, StoreProvider storeProvider, JwtService jwtService, UserDao userDao) {
        this.storeDao = storeDao;
        this.storeProvider = storeProvider;
        this.jwtService = jwtService;
        this.userDao = userDao;
    }

    @Transactional(rollbackFor = {Exception.class})
    public PostCreateOrderRes createOrder(PostCreateOrderReq postCreateOrderReq, int userIdx) throws BaseException{
        try {
            PostCreateOrderRes postCreateOrderRes = new PostCreateOrderRes();

            int cartIdx = storeDao.createCart(userIdx);
            storeDao.createOrder(postCreateOrderReq, cartIdx);

            postCreateOrderRes.setOrderProduct(storeDao.getOrderProducts(cartIdx));
            postCreateOrderRes.setUserCall(userDao.getUserInfo(userIdx).getUserCall());
            postCreateOrderRes.setUserName(userDao.getUserInfo(userIdx).getUserRealName());
            postCreateOrderRes.setUserEmail(userDao.getUserInfo(userIdx).getUserRecentEmail());

            return postCreateOrderRes;
        }
        catch(Exception exception){
            System.err.println(exception.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void createGetCart(PostCreateOrderReq postCreateOrderReq, int userIdx) throws BaseException{
        try {
            if(storeDao.checkUserCart(userIdx)==1){
                int cartIdx = storeDao.getCartIdx(userIdx);
                storeDao.createGetCart(postCreateOrderReq, cartIdx);
            }
            else{
                int cartIdx = storeDao.createCart(userIdx);
                storeDao.createGetCart(postCreateOrderReq, cartIdx);
            }
        }
        catch(Exception exception){
            System.err.println(exception.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public PostCreateOrderRes createOrderByCart(int userIdx) throws BaseException{
        try{
            PostCreateOrderRes postCreateOrderRes = new PostCreateOrderRes();
            int cartIdx = storeDao.getCartIdx(userIdx);
            storeDao.createOrderByCart(cartIdx);
            postCreateOrderRes.setOrderProduct(storeDao.getOrderProducts(cartIdx));
            postCreateOrderRes.setUserCall(userDao.getUserInfo(userIdx).getUserCall());
            postCreateOrderRes.setUserName(userDao.getUserInfo(userIdx).getUserRealName());
            postCreateOrderRes.setUserEmail(userDao.getUserInfo(userIdx).getUserRecentEmail());

            return postCreateOrderRes;
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteCartByStatus(int userIdx) throws BaseException{
        try{
            int cartIdx = storeDao.getCartIdx(userIdx);
            storeDao.deleteCartByStatus(cartIdx);
        }
        catch (EmptyResultDataAccessException exception){
            throw new BaseException(EMPTY_RESULT_DATA);
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteCartByProductIdx(int userIdx, int productIdx) throws BaseException{
        try{
            int cartIdx = storeDao.getCartIdx(userIdx);
            storeDao.deleteCartByProductIdx(cartIdx, productIdx);
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteCartByOptionIdx(int userIdx, ProductOption productOption) throws BaseException{
        try{
            int cartIdx = storeDao.getCartIdx(userIdx);
            storeDao.deleteCartByOptionIdx(cartIdx, productOption);
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = {Exception.class})
    public void checkCartProduct(int userIdx, int productIdx) throws BaseException{
        try{
            int cartIdx = storeDao.getCartIdx(userIdx);
            storeDao.checkCartProduct(cartIdx, productIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = {Exception.class})
    public void nonCheckCartProduct(int userIdx, int productIdx) throws BaseException{
        try{
            int cartIdx = storeDao.getCartIdx(userIdx);
            storeDao.nonCheckCartProduct(cartIdx, productIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void allCheckCartProduct(int userIdx) throws BaseException{
        try{
            int cartIdx = storeDao.getCartIdx(userIdx);
            storeDao.allCheckCartProduct(cartIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void allNonCheckCartProduct(int userIdx) throws BaseException{
        try{
            int cartIdx = storeDao.getCartIdx(userIdx);
            storeDao.allNonCheckCartProduct(cartIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }




}
