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
    INVALID_USER_ACCESS(false,2004,"유효하지 않은 접근입니다"),

    // users
    USERS_EMPTY_USER_ID(false, 2010, "유저 아이디 값을 확인해주세요."),

    // [POST] /users
    POST_USERS_EMPTY_EMAIL(false, 2015, "이메일을 입력해주세요."),
    POST_USERS_INVALID_EMAIL(false, 2016, "이메일 형식을 확인해주세요."),
    POST_USERS_EXISTS_EMAIL(false,2017,"중복된 이메일입니다."),

    // [POST] comments
    POST_USERS_EMPTY_COMMENT(false, 2018, "내용을 입력해주세요."),
    REMOVE_USERS_EMPTY_COMMENT(false, 2019, "존재하지 않는 댓글입니다"),
    REMOVE_USERS_EMPTY_RECOMMENT(false, 2020, "존재하지 않는 답글입니다"),


    /**
     * 3000 : Response 오류
     */
    // Common
    RESPONSE_ERROR(false, 3000, "값을 불러오는데 실패하였습니다."),

    // [POST] /users
    DUPLICATED_EMAIL(false, 3013, "중복된 이메일입니다."),
    FAILED_TO_LOGIN(false,3014,"없는 아이디거나 비밀번호가 틀렸습니다."),

    // [POST] /users/follow
    FAILED_TO_FOLLOW(false, 3015, "팔로우에 실패하였습니다"),
    FAILED_TO_UNFOLLOW(false, 3016, "팔로우 취소에 실패하였습니다"),

    // [POST]
    FAILED_TO_LIKE(false, 3017, "좋아요 항목 추가가 실패하였습니다"),
    FAILED_TO_UNLIKE(false, 3018, "좋아요 취소에 실패하였습니다"),

    // [POST]
    FAILED_TO_SCRAP(false, 3019, "스크랩을 실패하였습니다"),
    FAILED_TO_UNSCRAP(false, 3020, "스크랩 취소에 실패하였습니다"),

    FAILED_TO_WRITE_COMMENT(false, 3021, "댓글 작성을 실패하였습니다"),
    FAILED_TO_REMOVE_COMMENT(false, 3022, "댓글 삭제를 실패하였습니다"),
    FAILED_TO_WRITE_RECOMMENT(false, 3023, "답글 작성을 실패하였습니다"),
    FAILED_TO_REMOVE_RECOMMENT(false, 3024, "답글 삭제를 실패하였습니다"),



    /**
     * 4000 : Database, Server 오류
     */
    DATABASE_ERROR(false, 4000, "데이터베이스 연결에 실패하였습니다."),
    SERVER_ERROR(false, 4001, "서버와의 연결에 실패하였습니다."),

    //[PATCH] /users/{userIdx}
    MODIFY_FAIL_USERNAME(false,4014,"유저네임 수정 실패"),

    PASSWORD_ENCRYPTION_ERROR(false, 4011, "비밀번호 암호화에 실패하였습니다."),
    PASSWORD_DECRYPTION_ERROR(false, 4012, "비밀번호 복호화에 실패하였습니다."),


    /**
     * 5000 : Coupon 오류
     */
    RECEIVE_FAIL_COUPON(false,5000,"쿠폰을 발급 받는 데에 실패하였습니다"),
    ALREADY_RECEIVED_COUPON(false, 5001, "이미 발급된 쿠폰입니다"),
    EMPTY_COUPON_CODE(false, 5002, "쿠폰 코드를 입력해주세요"),
    INVALID_COUPON_CODE(false, 5003, "올바르지 않은 쿠폰코드입니다. 다시 확인해주세요"),
    // 6000 : 필요시 만들어서 쓰세요
    INVALID_INTRO(false, 6000, "올바르지 않은 인트로입니다"),
    INVALID_CONTENTS(false, 6001, "올바르지 않은 중간 컨텐츠입니다"),
    INVALID_SOCIAL_INFO(false, 6002, "올바르지 않은 Social Info입니다"),
    INVALID_COMMENTS(false, 6003, "올바르지 않은 Comments입니다");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    private BaseResponseStatus(boolean isSuccess, int code, String message) {
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}
