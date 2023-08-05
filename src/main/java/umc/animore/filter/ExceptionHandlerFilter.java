package umc.animore.filter;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import umc.animore.config.exception.BaseException;
import umc.animore.config.exception.BaseResponse;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;


@Slf4j
@Component
public class ExceptionHandlerFilter extends OncePerRequestFilter {


    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try{
            filterChain.doFilter(request,response);
        }catch(RuntimeException e){
            log.error("RunTime handler filter");
            System.out.println(e.getCause());
            System.out.println(e);
            setErrorResponse(e,response);
        }
    }

    public void setErrorResponse(Exception e,HttpServletResponse response){

        ObjectMapper mapper = new ObjectMapper();

        BaseException exception = (BaseException) e.getCause();

        response.setContentType("application/json");


        try{
            String json = mapper.writeValueAsString(new BaseResponse<>(exception.getStatus()));
            System.out.println(json);
            response.getWriter().write(json);
        }catch (IOException ex){
            e.printStackTrace();
        }
    }
}