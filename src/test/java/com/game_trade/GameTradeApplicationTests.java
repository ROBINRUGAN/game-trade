package com.game_trade;

import com.game_trade.utils.MD5Util;
import lombok.extern.slf4j.Slf4j;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.servlet.ServletComponentScan;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


@Slf4j
@ServletComponentScan
@EnableTransactionManagement
@EnableCaching
@RunWith(SpringRunner.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class GameTradeApplicationTests {

    @Test
    void contextLoads() throws IOException {
        log.info( "*********************"+MD5Util.getMD5WithSalt("114514"));

    }


}