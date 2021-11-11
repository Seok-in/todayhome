package com.example.demo.src.order;


import com.example.demo.config.BaseException;
import com.example.demo.src.order.model.GetCartInfoRes;
import com.example.demo.src.store.model.OrderProduct;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

import static com.example.demo.config.BaseResponseStatus.DATABASE_ERROR;
import static com.example.demo.config.BaseResponseStatus.NO_RESULT_FOR_CART;

@Service
public class OrderProvider {

    private final OrderDao orderDao;
    private final JwtService jwtService;


    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    public OrderProvider(OrderDao orderDao, JwtService jwtService) {
        this.orderDao = orderDao;
        this.jwtService = jwtService;
    }

    public GetCartInfoRes getCartInfoRes(int userIdx) throws BaseException {

        try{
            int cartIdx = orderDao.getCartIdx(userIdx);
            GetCartInfoRes getCartInfoRes = new GetCartInfoRes();
            getCartInfoRes.setOrderProducts(orderDao.getCartProducts(cartIdx));
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
}
