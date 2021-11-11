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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import javax.naming.Binding;
import javax.persistence.PostUpdate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<BindingResult> handleValidException (MethodArgumentNotValidException ex) {
        BindingResult message = ex.getBindingResult();
        return new BaseResponse<>(message);
    }


    // 46. 스토어 홈 화면 조회 API
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

    // 47. 스토어 베스트 실시간 베스트 화면 조회 API
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

    // 48. 스토어 카테고리별 역대 베스트 조회 API
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

    // 50. 스토어 메인 카테고리 상품 조회 API
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

    // 51. 스토어 세부 카테고리 상품 조회 API
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

    // 53. 단일 상품 리뷰 조회 API
    @ResponseBody
    @GetMapping("/products/{productIdx}/reviews")
    public BaseResponse<GetProductReviewRes> getProductReviews(@PathVariable("productIdx") int productIdx){
        try {
            int userIdx = jwtService.getUserIdx();
            GetProductReviewRes getProductReviewRes = storeProvider.getProductReviewRes(productIdx, userIdx);
            return new BaseResponse<>(getProductReviewRes);
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 54. 단일 상품 문의 조회 API
    @ResponseBody
    @GetMapping("/products/{productIdx}/questions")
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

    // 55. 단일 상품 배송/교환/환불 조회 API
    @ResponseBody
    @GetMapping("/products/{productIdx}/delivery-info")
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

    // 56. 스토어 단일상품 구매 API
    @ResponseBody
    @PostMapping("/order") //토의 및 수정필요
    public BaseResponse<PostCreateOrderRes> createOrder(@RequestBody @Validated PostCreateOrderReq postCreateOrderReq){
        try{
            int userIdx = jwtService.getUserIdx();
            PostCreateOrderRes postCreateOrderRes = storeService.createOrder(postCreateOrderReq, userIdx);
            return new BaseResponse<>(postCreateOrderRes);
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 57. 스토어 장바구니 담기 API
    @ResponseBody
    @PostMapping("/cart") //토의 및 수정필요
    public BaseResponse<String> createCartOrder(@RequestBody @Validated PostCreateOrderReq postCreateOrderReq){
        try{
            int userIdx = jwtService.getUserIdx();
            storeService.createGetCart(postCreateOrderReq, userIdx);
            return new BaseResponse<>("장바구니에 담았습니다.");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 58. 유저 장바구니 조회 API
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

    // 59. 스토어 장바구니 구매 API
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

    // 60. 장바구니 삭제 API
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

    // 70.1 카트 품목 체크 API
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

    // 70.2 체크해제
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

    // 70.3 전체체크
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

    // 70.4 전체체크해제
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


    // 주문 API
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

    //71.1 주문 취소 API (직접구매)
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

    // 71.2 주문취소 API (장바구니 구매)
    @ResponseBody
    @PatchMapping("/cart/cancel")
    public BaseResponse<String> cancelCartOrder(){
        try{
            int userIdx = jwtService.getUserIdx();
            storeService.orderCartCancel(userIdx);
            return new BaseResponse<>("장바구니에 담긴 품목을 주문 취소 하였습니다.");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 56.단일 상품 조회 API
    @ResponseBody
    @GetMapping("/products/{productIdx}")
    public BaseResponse<GetStoreProductRes> getStoreProductRes(@PathVariable("productIdx") int productIdx){
        try{
            GetStoreProductRes getStoreProductRes1 = storeProvider.getStoreProductRes(productIdx);
            int userIdx = jwtService.getUserIdx();
            return new BaseResponse<>(getStoreProductRes1);
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
