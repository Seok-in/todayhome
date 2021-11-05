package com.example.demo.src.store;

import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.store.StoreService;
import com.example.demo.src.store.StoreProvider;
import com.example.demo.src.user.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.example.demo.src.store.model.*;
import com.example.demo.utils.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.persistence.PostUpdate;
import java.util.List;


import static com.example.demo.config.BaseResponseStatus.*;
import static java.lang.Integer.parseInt;

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
    public BaseResponse<GetStoreFirstCtgRes> getStoreFirstCtgRes(@RequestParam(required = false) String categoryName){
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

    @ResponseBody
    @PostMapping("/orders") //토의 및 수정필요
    public BaseResponse<String> createOrder(@RequestBody PostCreateOrderReq postCreateOrderReq){
        try{
            int userIdx = jwtService.getUserIdx();
            storeService.createOrder(postCreateOrderReq, userIdx);
            return new BaseResponse<>("주문완료하였습니다.");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/orders") //토의 및 수정필요
    public BaseResponse<String> createCartOrder(@RequestBody PostCreateOrderReq postCreateOrderReq){
        try{
            int userIdx = jwtService.getUserIdx();
            storeService.createGetCart(postCreateOrderReq, userIdx);
            return new BaseResponse<>("장바구니에 담았습니다.");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/orders") //토의 및 수정필요
    public BaseResponse<String> createOrderByCart(){
        try{
            int userIdx = jwtService.getUserIdx();
            storeService.createOrderByCart(userIdx);
            return new BaseResponse<>("장바구니 구매하였습니다.");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/carts/status")
    public BaseResponse<String> deleteCarts(@RequestBody String Index, @RequestBody ProductOption productOption){
        try{
            int userIdx = jwtService.getUserIdx();
            if (Index != null){
                    int productIdx = Integer.parseInt(Index);
                    storeService.deleteCartByProductIdx(userIdx, productIdx);
                    return new BaseResponse<>("ProductIdx 으로 삭제 완료");
            }
            else if (!productOption.equals(null)){
                int productIdx = Integer.parseInt(Index);
                storeService.deleteCartByStatus(userIdx);
                return new BaseResponse<>("선택된 항목만 삭제 완료");
            }
            else{
                storeService.deleteCartByOptionIdx(userIdx, productOption);
                return new BaseResponse<>("ProductOption 으로 삭제 완료");
            }
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
