package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.store.StoreService;
import com.example.demo.src.store.StoreProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.src.store.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PostUpdate;
import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;

@RestController
@RequestMapping("/ohouse/store")
public class StoreController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final StoreProvider storeProvider;
    @Autowired
    private final StoreService storeService;
    @Autowired
    private final JwtService jwtService;


    public StoreController(StoreProvider storeProvider, StoreService storeService, JwtService jwtService) {
        this.storeProvider = storeProvider;
        this.storeService = storeService;
        this.jwtService = jwtService;
    }

    @ResponseBody
    @GetMapping("")
    public BaseResponse<GetStoreHomeRes> getStoreHomeRes() {
            try {
                int userIdx = jwtService.getUserIdx();
                GetStoreHomeRes getStoreHomeRes = storeProvider.getStoreHomeRes(userIdx);
                return new BaseResponse<>(getStoreHomeRes);
            }
            catch(BaseException exception) {
                return new BaseResponse<>((exception.getStatus()));
            }
    }

    @ResponseBody
    @GetMapping("/ranks")
    public BaseResponse<List<PopularProduct>> getRealTimeBest(){
        try{
            int userIdx = jwtService.getUserIdx();
            List<PopularProduct> popularProducts = storeProvider.getRealTimeBest(userIdx);
            return new BaseResponse<>(popularProducts);
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/categories/ranks")
    public BaseResponse<List<PopularProduct>> getAllTimeBest(@RequestParam(required = true, defaultValue = "%") String categoryName){
        try{
            int userIdx = jwtService.getUserIdx();
            List<PopularProduct> popularProducts = storeProvider.getAllTimeBest(userIdx, categoryName);
            return new BaseResponse<>(popularProducts);
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/categories")
    public BaseResponse<GetStoreFirstCtgRes> getSToreFirstCtgRes(@RequestParam(required = false) String categoryName){
        try{
            if (categoryName == null){
                return new BaseResponse<>(CATEGORYNAME_EMPTY);
            }
            int userIdx = jwtService.getUserIdx();
            GetStoreFirstCtgRes getStoreFirstCtgRes = storeProvider.getStoreFirstCtgRes(userIdx, categoryName);

            return new BaseResponse<>(getStoreFirstCtgRes);
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/categories/subcategories")
    public BaseResponse<GetStoreSecondCtgRes> getSToreSecondCtgRes(@RequestParam(required = false) String categoryName){
        try{
            if (categoryName == null){
                return new BaseResponse<>(CATEGORYNAME_EMPTY);
            }
            int userIdx = jwtService.getUserIdx();
            GetStoreSecondCtgRes getStoreSecondCtgRes = storeProvider.getStoreSecondCtgRes(userIdx, categoryName);

            return new BaseResponse<>(getStoreSecondCtgRes);
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
