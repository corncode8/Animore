package umc.animore.config.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class BaseException extends Exception{
    private BaseResponseStatus status; //예외 상태 나타냄
}
//status를 통해 예외 상태를 전달함
