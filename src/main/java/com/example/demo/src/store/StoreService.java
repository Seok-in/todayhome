package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.src.order.model.PostCreateOrderReq;
import com.example.demo.src.order.model.PostCreateOrderRes;
import com.example.demo.src.store.model.*;
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
}
