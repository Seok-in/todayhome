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
import java.util.Objects;


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
    @PostMapping("/order") //토의 및 수정필요
    public BaseResponse<PostCreateOrderRes> createOrder(@RequestBody PostCreateOrderReq postCreateOrderReq){
        try{
            int userIdx = jwtService.getUserIdx();
            PostCreateOrderRes postCreateOrderRes = storeService.createOrder(postCreateOrderReq, userIdx);
            return new BaseResponse<>(postCreateOrderRes);
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/cart") //토의 및 수정필요
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
    @GetMapping("/cart")
    public BaseResponse<GetCartInfoRes> getCartInfo() {
        try {
            int userIdx = jwtService.getUserIdx();
            GetCartInfoRes getCartInfoRes = storeProvider.getCartInfoRes(userIdx);
            return new BaseResponse<>(getCartInfoRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/cart/orders") //토의 및 수정필요
    public BaseResponse<PostCreateOrderRes> createOrderByCart(){
        try{
            int userIdx = jwtService.getUserIdx();
            PostCreateOrderRes postCreateOrderRes = storeService.createOrderByCart(userIdx);
            return new BaseResponse<>(postCreateOrderRes);
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/cart/status")
    public BaseResponse<String> deleteCarts(@RequestBody(required = false) PatchCartStatusReq patchCartStatusReq){
        try{
            int userIdx = jwtService.getUserIdx();
            if (Objects.isNull(patchCartStatusReq)){
                storeService.deleteCartByStatus(userIdx);
                return new BaseResponse<>("선택된 항목만 삭제 완료");
            }
            else if (patchCartStatusReq.getProductIdx() != null){
                    String index = patchCartStatusReq.getProductIdx();
                    int productIdx = Integer.parseInt(index);
                    storeService.deleteCartByProductIdx(userIdx, productIdx);
                    return new BaseResponse<>("ProductIdx 으로 삭제 완료");
            }
            else{
                ProductOption productOption = patchCartStatusReq.getProductOption();
                storeService.deleteCartByOptionIdx(userIdx, productOption);
                return new BaseResponse<>("ProductOption 으로 삭제 완료");
            }
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/cart/check")
    public BaseResponse<String> checkCartProduct(@RequestBody PatchCheckCartReq patchCheckCartReq){
        try{
            int userIdx = jwtService.getUserIdx();
            storeService.checkCartProduct(userIdx, patchCheckCartReq.getProductIdx());
            return new BaseResponse<>("품목 체크");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/cart/non-check")
    public BaseResponse<String> nonCheckCartProduct(@RequestBody PatchCheckCartReq patchCheckCartReq){
        try{
            int userIdx = jwtService.getUserIdx();
            storeService.nonCheckCartProduct(userIdx, patchCheckCartReq.getProductIdx());
            return new BaseResponse<>("품목 체크 해제");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/cart/all-checks")
    public BaseResponse<String> allCheckCartProduct(){
        try{
            int userIdx = jwtService.getUserIdx();
            storeService.allCheckCartProduct(userIdx);
            return new BaseResponse<>("품목 전체 체크");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PatchMapping("/cart/all-non-checks")
    public BaseResponse<String> allNonCheckCartProduct(){
        try{
            int userIdx = jwtService.getUserIdx();
            storeService.allNonCheckCartProduct(userIdx);
            return new BaseResponse<>("품목 전체 체크 해제");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }


    @ResponseBody
    @GetMapping("/{productIdx}/questions")
    public BaseResponse<List<GetQuestionRes>> getQuestionRes(@PathVariable("productIdx") int productIdx){
        try{
            int userIdx = jwtService.getUserIdx();
            List<GetQuestionRes> result = storeProvider.getQuestionRes(productIdx);
            return new BaseResponse<>(result);
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @GetMapping("/{productIdx}/delivery-info")
    public BaseResponse<GetDeliveryInfoRes> getDeliveryInfo(@PathVariable("productIdx") int productIdx){
        try{
            int userIdx = jwtService.getUserIdx();
            GetDeliveryInfoRes getDeliveryInfoRes = storeProvider.getDeliveryInfoRes(productIdx);
            return new BaseResponse<>(getDeliveryInfoRes);
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    @ResponseBody
    @PostMapping("/order/completion")
    public BaseResponse<String> makeOrder(PostOrderReq postOrderReq) {
        try {
            int userIdx = jwtService.getUserIdx();
            storeService.orderProducts(postOrderReq, userIdx);
            return new BaseResponse<>("주문성공하였습니다.");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //직접구매시의 취소
    @ResponseBody
    @PatchMapping("/order/cancel")
    public BaseResponse<String> cancelOrder(){
        try{
            int userIdx = jwtService.getUserIdx();
            storeService.orderDirectCancel(userIdx);
            return new BaseResponse<>("주문 취소 하였습니다.");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //장바구니 구매시의 취소
    @ResponseBody
    @PatchMapping("/cart/cancel")
    public BaseResponse<String> cancelCartOrder(){
        try{
            int userIdx = jwtService.getUserIdx();
            storeService.orderCartCancel(userIdx);
            return new BaseResponse<>("주문 취소 하였습니다.");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
