package com.example.demo.config;

import lombok.Getter;

/**
 * 에러 코드 관리
 */
@Getter
public enum BaseResponseStatus {
    /**
     * 1000 : 요청 성공
     */
    SUCCESS(true, 1000, "요청에 성공하였습니다."),


    /**
     * 2000 : Request 오류
     */
    // Common
    REQUEST_ERROR(false, 2000, "입력값을 확인해주세요."),
    EMPTY_JWT(false, 2001, "JWT를 입력해주세요."),
    INVALID_JWT(false, 2002, "유효하지 않은 JWT입니다."),
    INVALID_USER_JWT(false,2003,"권한이 없는 유저의 접근입니다."),
    INVALID_KAKAO_ACCESS_TOKEN(false,2004,"accessToken 값을 확인하세요."),

    // users
    NULL_PASSWORD(false,2005,"비밀번호를 입력해주세요."),
    INVALID_SIZE_PW(false, 2006,"비밀번호는 7자 ~ 18자 사이의 길이로 입력해주세요."),
    NULL_USERNAME(false, 2007, "닉네임을 입력해주세요."),
    INVALID_SIZE_USERNAME(false,2008,"닉네임은 2자 ~ 15자 사이의 길이로 입력해주세요"),

    //categoryName
    CATEGORYNAME_EMPTY(false, 2011, "카테고리 이름 값을 확인해주세요."),
    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),
    POST_USERS_EXISTS_NAME(false,2018,"중복된 닉네임입니다."),

    SERVICE_TEXT_SIZE_OVER(false,2019,"500글자 이하로 작성해주세요."),
    POST_USERS_REQUIRED_AGREE(false,2020,"약관에 동의해야 합니다."),
    POST_USERS_REQUIRED_CHECK(false,2021,"탈퇴사유를 체크해주세요."),
    POST_QUESTIONS_BLANK(false,2027,"문의 내용을 입력하세요."),
    POST_QUESTIONS_SIZE(false,2028,"문의내용은 5글자 이상 200자 이하 입니다."),
    POST_REVIEWS_BLANK(false,2029,"리뷰내용을 입력하세요"),
    POST_REVIEWS_SIZE(false,2030,"리뷰내용은 5글자 이상 400자 이하입니다."),
    POST_RATE_NULL(false,2031,"별점을 입력해주세요"),
    POST_REVIEWS_ZERO(false,2032,"1 이상의 값을 입력하세요"),
    INVALID_FLAG(false,2040,"올바른 FLAG 값을 입력해주세요."),

    NO_CHOICE_FOR_CART(false,2050,"체크된 상품이 없습니다."),
    INVALID_PRODUCT_NUM(false, 2041, "상품의 개수는 100이하의 양의 정수 값을 입력해주세요."),
    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),
    USER_STATUS_DELETED(false,3015,"탈퇴한 회원입니다."),
    USER_STATUS_INVALID(false, 3016, "휴면이거나 정지된 계정입니다."),




    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),
    EMPTY_RESULT_DATA(false, 4002, "선택된 항목이 없습니다."),
    NO_RESULT_DATA(false,4003, "해당되는 결과가 없습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),

    MODIFY_ONLY_MY_REVIEW(false,4020,"본인이 작성한 리뷰가 아닙니다."),
    NO_RESULT_FOR_CART(false,4021,"장바구니에 담긴 품목이 없습니다."),
    // 5000 : 필요시 만들어서 쓰세요
    NEED_TO_SIGNUP(true, 5000, "회원가입을 진행합니다.");
    // 6000 : 필요시 만들어서 쓰세요


    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
