package com.nostronmo.manage.instancer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import com.nostronmo.manage.instancer.config.CustomBanner;

@SpringBootApplication
public class InstanceManagerApplication {

	public static void main(String[] args) {
		SpringApplication app = new SpringApplication(InstanceManagerApplication.class);
		app.setBanner(new CustomBanner());
		app.run(args);
	}

}
