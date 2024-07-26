package com.junksail.vinchik_bot;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;

//https://habr.com/ru/articles/735666/
@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class},
		scanBasePackages={"com.junksail.vinchik_bot"})
public class VinchikBotApplication {

	public static void main(String[] args) {
		SpringApplication.run(VinchikBotApplication.class, args);
	}

}
