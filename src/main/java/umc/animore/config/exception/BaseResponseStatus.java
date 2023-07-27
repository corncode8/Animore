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

    //3000 : 응답 오류
    RESPONSE_ERROR(false,3000,"값을 불러오는데 실패"),


    //4000 : 데이터베이스, 서버 오류
    DATABASE_ERROR(false,4000,"데이터베이스 연결에 실패"),

    SERVER_ERROR(false,4001,"서버 연결 실패"),
    NO_MATCHING_STORE(false,4002,"데이터베이스에 저장된 값이 없음");


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
