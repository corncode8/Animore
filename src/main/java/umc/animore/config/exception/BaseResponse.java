package umc.animore.config.exception;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import lombok.AllArgsConstructor;
import lombok.Getter;

import static umc.animore.config.exception.BaseResponseStatus.SUCCESS;


@Getter
@AllArgsConstructor
@JsonPropertyOrder({"isSuccess", "code", "message", "result"})
public class BaseResponse<T> {
    @JsonProperty("isSuccess")
    private final Boolean isSuccess; //응답 성공 여부
    private final String message; //응답 메시지
    private final int code; //응답 코드

    @JsonInclude(JsonInclude.Include.NON_NULL) //'result'필드가 null이면 출력하지 않도록
    private T result; //응답 결과 데이터


    //만약 요청에 성공했을 경우
    public BaseResponse(T result) {
        this.isSuccess = SUCCESS.isSuccess(); //성공 여부 true로 설정
        this.message = SUCCESS.getMessage(); //성공 메시지 설정
        this.code = SUCCESS.getCode(); //성공 코드 설정
        this.result = result; //응답 결과 데이터 설정
    }

    //요청 실패의 경우
    public BaseResponse(BaseResponseStatus status) {
        this.isSuccess=status.isSuccess(); //실패상태 설정
        this.message = status.getMessage(); //실패 메시지 설정
        this.code = status.getCode(); //실패 코드 설정
    }
}


