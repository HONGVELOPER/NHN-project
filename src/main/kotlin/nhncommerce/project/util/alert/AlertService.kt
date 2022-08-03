package nhncommerce.project.util.alert

import org.springframework.stereotype.Service
import java.io.PrintWriter
import javax.servlet.http.HttpServletResponse

@Service
class AlertService {

    fun alertMessage(msg:String, redirectPage:String, response:HttpServletResponse){
        var str:String=""
        if(redirectPage.isEmpty()){
            str="<script>alert('" + msg + "');</script>";
        }
        else{
            str=("<script>alert('" + msg + "'); location.href='" + redirectPage + "';</script>");
        }
        response.setContentType("text/html; charset=UTF-8");
        var out:PrintWriter=response.writer
        out.println(str)
        out.flush();
    }

}