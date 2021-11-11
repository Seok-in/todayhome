package com.example.demo.src.order;


import com.example.demo.config.BaseException;
import com.example.demo.config.BaseResponse;
import com.example.demo.src.order.model.*;
import com.example.demo.src.store.model.ProductOption;
import com.example.demo.utils.JwtService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;

import java.util.Objects;

@RestController
@RequestMapping("/ohouse/orders")
public class OrderController {
    final Logger logger = LoggerFactory.getLogger(this.getClass());

    @Autowired
    private final OrderProvider orderProvider;
    @Autowired
    private final OrderService orderService;
    @Autowired
    private final JwtService jwtService;

    public OrderController(OrderProvider orderProvider, OrderService orderService, JwtService jwtService) {
        this.orderProvider = orderProvider;
        this.orderService = orderService;
        this.jwtService = jwtService;
    }

    // ExceptionHandler with Validation
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public BaseResponse<BindingResult> handleValidException (MethodArgumentNotValidException ex) {
        BindingResult message = ex.getBindingResult();
        return new BaseResponse<>(message);
    }

    // 56. 스토어 단일상품 구매 API
    @ResponseBody
    @PostMapping("/{productIdx}")
    public BaseResponse<PostCreateOrderRes> createOrder(@PathVariable ("productIdx") int productIdx, @RequestBody @Validated PostCreateOrderReq postCreateOrderReq){
        try{
            int userIdx = jwtService.getUserIdx();
            PostCreateOrderRes postCreateOrderRes = orderService.createOrder(postCreateOrderReq, userIdx, productIdx);
            return new BaseResponse<>(postCreateOrderRes);
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 57. 스토어 장바구니 담기 API
    @ResponseBody
    @PostMapping("/carts/{productIdx}") //토의 및 수정필요
    public BaseResponse<String> createCartOrder(@PathVariable ("productIdx") int productIdx, @RequestBody @Validated PostCreateOrderReq postCreateOrderReq){
        try{
            int userIdx = jwtService.getUserIdx();
            orderService.createGetCart(postCreateOrderReq, userIdx, productIdx);
            return new BaseResponse<>("장바구니에 담았습니다.");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 58. 유저 장바구니 조회 API
    @ResponseBody
    @GetMapping("/carts")
    public BaseResponse<GetCartInfoRes> getCartInfo() {
        try {
            int userIdx = jwtService.getUserIdx();
            GetCartInfoRes getCartInfoRes = orderProvider.getCartInfoRes(userIdx);
            return new BaseResponse<>(getCartInfoRes);
        } catch (BaseException exception) {
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 59. 스토어 장바구니 구매 API
    @ResponseBody
    @PostMapping("/carts/orders")
    public BaseResponse<PostCreateOrderRes> createOrderByCart(){
        try{
            int userIdx = jwtService.getUserIdx();
            PostCreateOrderRes postCreateOrderRes = orderService.createOrderByCart(userIdx);
            return new BaseResponse<>(postCreateOrderRes);
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 60. 장바구니 삭제 API
    @ResponseBody
    @PatchMapping("/carts/status")
    public BaseResponse<String> deleteCarts(@RequestBody(required = false) PatchCartStatusReq patchCartStatusReq){
        try{
            int userIdx = jwtService.getUserIdx();
            if (Objects.isNull(patchCartStatusReq)){
                orderService.deleteCartByStatus(userIdx);
                return new BaseResponse<>("선택된 항목만 삭제 완료");
            }
            else if (patchCartStatusReq.getProductIdx() != null){
                String index = patchCartStatusReq.getProductIdx();
                int productIdx = Integer.parseInt(index);
                orderService.deleteCartByProductIdx(userIdx, productIdx);
                return new BaseResponse<>("ProductIdx 으로 삭제 완료");
            }
            else{
                ProductOption productOption = patchCartStatusReq.getProductOption();
                orderService.deleteCartByOptionIdx(userIdx, productOption);
                return new BaseResponse<>("ProductOption 으로 삭제 완료");
            }
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 70.1 카트 품목 체크 API
    @ResponseBody
    @PatchMapping("/carts/check")
    public BaseResponse<String> checkCartProduct(@RequestBody PatchCheckCartReq patchCheckCartReq){
        try{
            int userIdx = jwtService.getUserIdx();
            orderService.checkCartProduct(userIdx, patchCheckCartReq.getProductIdx());
            return new BaseResponse<>("품목 체크");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 70.2 체크해제
    @ResponseBody
    @PatchMapping("/carts/non-check")
    public BaseResponse<String> nonCheckCartProduct(@RequestBody PatchCheckCartReq patchCheckCartReq){
        try{
            int userIdx = jwtService.getUserIdx();
            orderService.nonCheckCartProduct(userIdx, patchCheckCartReq.getProductIdx());
            return new BaseResponse<>("품목 체크 해제");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 70.3 전체체크
    @ResponseBody
    @PatchMapping("/carts/all-checks")
    public BaseResponse<String> allCheckCartProduct(){
        try{
            int userIdx = jwtService.getUserIdx();
            orderService.allCheckCartProduct(userIdx);
            return new BaseResponse<>("품목 전체 체크");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 70.4 전체체크해제
    @ResponseBody
    @PatchMapping("/carts/all-non-checks")
    public BaseResponse<String> allNonCheckCartProduct(){
        try{
            int userIdx = jwtService.getUserIdx();
            orderService.allNonCheckCartProduct(userIdx);
            return new BaseResponse<>("품목 전체 체크 해제");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    //71.1 주문 취소 API (직접구매)
    @ResponseBody
    @PatchMapping("/cancel")
    public BaseResponse<String> cancelOrder(){
        try{
            int userIdx = jwtService.getUserIdx();
            orderService.orderDirectCancel(userIdx);
            return new BaseResponse<>("주문 취소 하였습니다.");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 71.2 주문취소 API (장바구니 구매)
    @ResponseBody
    @PatchMapping("/carts/cancel")
    public BaseResponse<String> cancelCartOrder(){
        try{
            int userIdx = jwtService.getUserIdx();
            orderService.orderCartCancel(userIdx);
            return new BaseResponse<>("장바구니에 담긴 품목을 주문 취소 하였습니다.");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }

    // 72. 주문 API
    @ResponseBody
    @PostMapping("/completion")
    public BaseResponse<String> makeOrder(@RequestBody @Validated PostOrderReq postOrderReq) {
        try {
            int userIdx = jwtService.getUserIdx();
            orderService.orderProducts(postOrderReq, userIdx);
            return new BaseResponse<>("주문성공하였습니다.");
        }
        catch(BaseException exception){
            return new BaseResponse<>((exception.getStatus()));
        }
    }
}
