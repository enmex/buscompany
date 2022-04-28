package net.thumbtack.school.buscompany;

import com.google.gson.Gson;
import net.thumbtack.school.buscompany.dto.request.common.login.LoginDtoRequest;
import net.thumbtack.school.buscompany.dto.request.common.register.RegisterAdminDtoRequest;
import net.thumbtack.school.buscompany.dto.request.common.register.RegisterClientDtoRequest;
import net.thumbtack.school.buscompany.util.MyBatisUtils;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.nio.charset.Charset;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;

@ExtendWith(SpringExtension.class)
public class BaseTest {
    private static boolean setUpIsDone = false;

    @Autowired
    protected MockMvc mvc;

    @Autowired
    protected Gson gson;

    @BeforeAll
    public static void setUp(){
        if (!setUpIsDone) {
            boolean initSqlSessionFactory = MyBatisUtils.initSqlSessionFactory();
            if (!initSqlSessionFactory) {
                throw new RuntimeException("Can't create connection, stop");
            }
            setUpIsDone = true;
        }

    }

    @AfterEach
    public void clearDatabase() throws Exception {
        MvcResult result = mvc.perform(post("/api/debug/clear").contentType(MediaType.APPLICATION_JSON)).andReturn();
        System.out.println(result.getResponse().getStatus());
    }

    protected MvcResult httpPost(String url, String json) throws Exception {
        return mvc.perform(post(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(Charset.defaultCharset())
                .content(json)).andReturn();
    }

    protected MvcResult httpPut(String url, Cookie cookie, String json) throws Exception {
        return mvc.perform(put(url)
                .cookie(cookie)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(Charset.defaultCharset())
                .content(json)).andReturn();
    }

    protected MvcResult httpGet(String url, String json) throws Exception {
        return mvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(Charset.defaultCharset())
                .content(json)).andReturn();
    }

    protected MvcResult httpGet(String url) throws Exception {
        return mvc.perform(get(url)
                .contentType(MediaType.APPLICATION_JSON_VALUE)
                .characterEncoding(Charset.defaultCharset())).andReturn();
    }

    protected MvcResult registerAdmin() throws Exception {
        RegisterAdminDtoRequest request = new RegisterAdminDtoRequest(
                "Михаил", "Привалов", "Андреевич",
                "Директор", "mich", "123"
        );

        return httpPost("/api/admins", gson.toJson(request));
    }

    protected MvcResult registerClient() throws Exception {
        RegisterClientDtoRequest request = new RegisterClientDtoRequest(
                "Михаил", "Привалов", "Андреевич",
                "mikhail@mail.ru", "79998887766",  "mich", "123"
        );

        return httpPost("/api/clients", gson.toJson(request));
    }


    protected MvcResult login(String login, String password) throws Exception {
        LoginDtoRequest request = new LoginDtoRequest(login, password);

        return httpPost("/api/sessions", gson.toJson(request));
    }

}
