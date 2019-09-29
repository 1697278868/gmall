package com.atguigu.gmall.manage;

import com.atguigu.gmall.util.RedisUtil;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import redis.clients.jedis.Jedis;

@RunWith(SpringRunner.class)
@SpringBootTest
public class GmallManageServiceApplicationTests {

	@Autowired
	RedisUtil redisUtil;
	@Test
	public void contextLoads() {
	}

	@Test
	public void RedisTest(){
		Jedis jedis = redisUtil.getJedis();
		jedis.set("test","text_value" );

	}
}
