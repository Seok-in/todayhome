package com.example.demo.src.order;


import com.example.demo.config.BaseException;
import com.example.demo.src.order.model.PostCreateOrderReq;
import com.example.demo.src.order.model.PostCreateOrderRes;
import com.example.demo.src.order.model.PostOrderReq;
import com.example.demo.src.store.model.ProductOption;
import com.example.demo.src.user.UserDao;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static com.example.demo.config.BaseResponseStatus.*;

@Service
public class OrderService {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    private final OrderDao orderDao;
    private final UserDao userDao;
    private final OrderProvider orderProvider;
    private final JwtService jwtService;


    @Autowired
    public OrderService(OrderDao orderDao, OrderProvider orderProvider, UserDao userDao, JwtService jwtService) {
        this.orderDao = orderDao;
        this.userDao = userDao;
        this.orderProvider = orderProvider;
        this.jwtService = jwtService;
    }

    @Transactional(rollbackFor = {Exception.class})
    public PostCreateOrderRes createOrder(PostCreateOrderReq postCreateOrderReq, int userIdx, int productIdx) throws BaseException {
        try {
            PostCreateOrderRes postCreateOrderRes = new PostCreateOrderRes();

            int cartIdx = orderDao.createCart(userIdx);
            orderDao.createOrder(postCreateOrderReq, cartIdx, productIdx);

            postCreateOrderRes.setOrderProduct(orderDao.getOrderProducts(cartIdx));
            postCreateOrderRes.setUserCall(userDao.getUserInfo(userIdx).getUserCall());
            postCreateOrderRes.setUserName(userDao.getUserInfo(userIdx).getUserRealName());
            postCreateOrderRes.setUserEmail(userDao.getUserInfo(userIdx).getUserRecentEmail());
            postCreateOrderRes.setUserPoint(userDao.getUserInfo(userIdx).getSumPoint());
            postCreateOrderRes.setCoupons(userDao.getUserCoupon(userIdx));

            return postCreateOrderRes;
        }
        catch(EmptyResultDataAccessException e){
            throw new BaseException(EMPTY_RESULT_DATA);
        }
        catch(Exception exception){
            System.err.println(exception.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void createGetCart(PostCreateOrderReq postCreateOrderReq, int userIdx, int productIdx) throws BaseException{
        try {
            if(orderDao.getCartExist(postCreateOrderReq, productIdx)!=1){
                throw new BaseException(POST_GETCART_EXIST);
            }
            if(orderDao.checkUserCart(userIdx)==1){
                int cartIdx = orderDao.getCartIdx(userIdx);
                orderDao.createGetCart(postCreateOrderReq, cartIdx, productIdx);
            }
            else{
                int cartIdx = orderDao.createCart(userIdx);
                orderDao.createGetCart(postCreateOrderReq, cartIdx, productIdx);
            }
        }
        catch(BaseException e){
            throw new BaseException(e.getStatus());
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public PostCreateOrderRes createOrderByCart(int userIdx) throws BaseException{
        try{
            PostCreateOrderRes postCreateOrderRes = new PostCreateOrderRes();
            int cartIdx = orderDao.getCartIdx(userIdx);
            if(orderDao.checkCartStatus(cartIdx, userIdx)!=1){
                throw new BaseException(NO_CHOICE_FOR_CART);
            }
            if(orderDao.getOrderProducts(cartIdx).isEmpty()){
                throw new BaseException(NO_RESULT_FOR_CART);
            }
            orderDao.createOrderByCart(cartIdx);
            postCreateOrderRes.setOrderProduct(orderDao.getOrderProducts(cartIdx));
            postCreateOrderRes.setUserCall(userDao.getUserInfo(userIdx).getUserCall());
            postCreateOrderRes.setUserName(userDao.getUserInfo(userIdx).getUserRealName());
            postCreateOrderRes.setUserEmail(userDao.getUserInfo(userIdx).getUserRecentEmail());
            postCreateOrderRes.setCoupons(userDao.getUserCoupon(userIdx));
            postCreateOrderRes.setUserPoint(userDao.getUserInfo(userIdx).getSumPoint());

            return postCreateOrderRes;
        }
        catch(BaseException baseException){
            throw new BaseException(baseException.getStatus());
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteCartByStatus(int userIdx) throws BaseException{
        try{
            int cartIdx = orderDao.getCartIdx(userIdx);
            orderDao.deleteCartByStatus(cartIdx);
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
            int cartIdx = orderDao.getCartIdx(userIdx);
            orderDao.deleteCartByProductIdx(cartIdx, productIdx);
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void deleteCartByOptionIdx(int userIdx, ProductOption productOption) throws BaseException{
        try{
            int cartIdx = orderDao.getCartIdx(userIdx);
            orderDao.deleteCartByOptionIdx(cartIdx, productOption);
        }
        catch (Exception exception) {
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = {Exception.class})
    public void checkCartProduct(int userIdx, int productIdx) throws BaseException{
        try{
            int cartIdx = orderDao.getCartIdx(userIdx);
            orderDao.checkCartProduct(cartIdx, productIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
    @Transactional(rollbackFor = {Exception.class})
    public void nonCheckCartProduct(int userIdx, int productIdx) throws BaseException{
        try{
            int cartIdx = orderDao.getCartIdx(userIdx);
            orderDao.nonCheckCartProduct(cartIdx, productIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void allCheckCartProduct(int userIdx) throws BaseException{
        try{
            int cartIdx = orderDao.getCartIdx(userIdx);
            orderDao.allCheckCartProduct(cartIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void allNonCheckCartProduct(int userIdx) throws BaseException{
        try{
            int cartIdx = orderDao.getCartIdx(userIdx);
            orderDao.allNonCheckCartProduct(cartIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void orderProducts(PostOrderReq postOrderReq, int userIdx) throws BaseException {

        try{
            if(postOrderReq.getAgreeStatus() == "N"){
                throw new BaseException(POST_USERS_REQUIRED_AGREE);
            }
            int cartIdx = orderDao.getCartIdx(userIdx);
            if(orderDao.checkArea(postOrderReq.getAddress(), cartIdx)==1){
                throw new BaseException(INVALID_DELIVERY_AREA);
            }
            if(orderDao.getUserPoint(userIdx) < postOrderReq.getPoint()){
                throw new BaseException(EXCEED_POINT);
            }

            orderDao.changeOrderStatus(cartIdx);
            orderDao.orderProduct(postOrderReq, userIdx, cartIdx);
            orderDao.createUserPoint(userIdx, postOrderReq.getPoint());
            orderDao.createUserCoupon(userIdx, postOrderReq.getCouponIdx());
        }
        catch(BaseException e){
            throw new BaseException(e.getStatus());
        }
        catch(Exception exception){
            System.err.println(exception.toString());
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void orderDirectCancel(int userIdx) throws BaseException{
        try{
            int cartIdx = orderDao.getDirectCartIdx(userIdx);
            orderDao.deleteDirect(cartIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }

    @Transactional(rollbackFor = {Exception.class})
    public void orderCartCancel(int userIdx) throws BaseException{
        try{
            int cartIdx= orderDao.getCartIdx(userIdx);
            orderDao.orderCancel(cartIdx);
        }
        catch(Exception exception){
            throw new BaseException(DATABASE_ERROR);
        }
    }
}
