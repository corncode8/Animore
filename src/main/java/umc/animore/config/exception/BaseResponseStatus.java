package umc.animore.config.exception;
import lombok.Getter;

@Getter
public enum BaseResponseStatus { //열거 상수를 통해 응답상태를 나타냄
    //1000 : 요청 성공
    SUCCESS(true, 1000, "요청에 성공"),

    //2000 : 요청 오류
    REQUEST_ERROR(false,2000,"입력값을 확인 필요"),

    GET_SEARCH_EMPTY_QUERY(false, 2001, "검색어를 입력하시오."),

    GET_SEARCH_INVALID_QUERY1(false, 2002, "검색어는 20자 내로 입력해야함."),
    GET_SEARCH_INVALID_QUERY2(false, 2003, "검색어는 100자 내로 입력해야함."),

    GET_JWT_Expired(false, 2004, "jwt 만료기한이 지났습니다."),
    GET_JWT_InValid(false, 2005, "유효하지 않은 jwt 입니다."),
    GET_JWT_Decode_ERROR(false, 2006, "jwt 형식이 잘못되었습니다"),

    GET_PET_EMPTY_ERROR(false, 2007, "user가 등록한 Pet이 존재하지 않습니다."),
    GET_PET_EMPTY_NAME(false, 2008, "PET의 이름이 존재하지 않습니다."),

    GET_USER_EMPTY_NICKNAME_NAME(false, 2009, "user의 별명을 입력하지 않았습니다."),

    GET_USER_PASSWORD_ERROR(false, 2009, "비밀번호가 일치하지 않습니다"),


    //3000 : 응답 오류
    RESPONSE_ERROR(false,3000,"값을 불러오는데 실패"),


    //4000 : 데이터베이스, 서버 오류
    DATABASE_ERROR(false,4000,"데이터베이스 연결에 실패"),

    SERVER_ERROR(false,4001,"서버 연결 실패"),

    NO_MATCHING_STORE(false,4002,"데이터베이스에 저장된 값이 없음"),

    //5000 : 후기작성 오류
    EMPTY_REVIEW_CONTENT(false,5000,"후기 내용 입력안함"),
    EMPTY_REVIEW_LIKE(false,5001,"후기 별점 입력안함"),
    IMAGE_UPLOAD_ERROR(false,5002,"이미지 저장에 실패했습니다."),
    NOT_FOUND_REVIEW(false,5003,"해당 리뷰가 없습니다."),
    NOT_FOUND_RESERVATION(false,5004,"해당 예약 내역이 없습니다."),
    NOT_REVIEWER_USER(false,5005,"해당 사용자가 아닙니다."),
    EMPTY_REVIEW_DTO(false,5006,"해당 리뷰 내용을 작성하지 않았습니다."),
    EMPTY_PET_ID(false,5007,"후기 펫아이디 입력안함"),

    //6000 : 예약 오류
    NO_TIME_AVAILABLE(false, 6000, "예약 가능한 시간이 없습니다."),
    RESERVAION_MODIFY_ERROR(false, 6001, "예약 수정에 실패했습니다."),
    INVALID_REQUEST_INFO(false, 6002, "잘못된 업체 정보 요청입니다."),
    NOT_MATCHED_USER(false, 6003, "일치하지 않은 유저입니다."),
    FALSE_RESERVATION(false, 6004, "예약에 실패하였습니다."),
    NOT_FOUND_RECENT_BOOKING(false, 6005, "최근 예약을 찾을 수 없습니다"),
    EMPTY_REQUEST_VALUE(false, 6006, "요청사항이 선택되지 않았습니다.");

    private final boolean isSuccess;
    private final int code;
    private final String message;

    //열거 상수에 대한 생성자로, 응답 상태값을 초기화함
    private BaseResponseStatus(boolean isSuccess, int code, String message){
        this.isSuccess = isSuccess;
        this.code = code;
        this.message = message;
    }
}

