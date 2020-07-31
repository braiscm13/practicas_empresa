package com.opentach.server.proxy.boot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.EnableAutoConfiguration;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;
import org.springframework.boot.autoconfigure.mail.MailSenderAutoConfiguration;
import org.springframework.boot.autoconfigure.quartz.QuartzAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityFilterAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.SecurityRequestMatcherProviderAutoConfiguration;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.autoconfigure.session.SessionAutoConfiguration;
import org.springframework.boot.autoconfigure.task.TaskSchedulingAutoConfiguration;
import org.springframework.boot.autoconfigure.transaction.TransactionAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.AsyncLoadBalancerAutoConfiguration;
import org.springframework.cloud.client.loadbalancer.LoadBalancerAutoConfiguration;
import org.springframework.cloud.openfeign.FeignAutoConfiguration;
import org.springframework.cloud.openfeign.encoding.FeignContentGzipEncodingAutoConfiguration;
import org.springframework.context.annotation.ComponentScan;

import com.ontimize.boot.autoconfigure.jdbc.JdbcAutoConfiguration;
import com.ontimize.boot.autoconfigure.security.DefaultSecurityAutoConfiguration;
import com.ontimize.boot.autoconfigure.security.MethodSecurityConfiguration;

// @RestController
@SpringBootApplication
@ComponentScan(basePackages = { "com.opentach.server.proxy" })
@EnableAutoConfiguration(
		exclude = { MethodSecurityConfiguration.class, DefaultSecurityAutoConfiguration.class, DataSourceAutoConfiguration.class, //
				MailSenderAutoConfiguration.class, QuartzAutoConfiguration.class, SecurityAutoConfiguration.class, //
				SecurityFilterAutoConfiguration.class, SecurityRequestMatcherProviderAutoConfiguration.class, SessionAutoConfiguration.class, //
				TaskSchedulingAutoConfiguration.class, TransactionAutoConfiguration.class, AsyncLoadBalancerAutoConfiguration.class, //
				LoadBalancerAutoConfiguration.class, FeignAutoConfiguration.class, FeignContentGzipEncodingAutoConfiguration.class, //
				JdbcAutoConfiguration.class, UserDetailsServiceAutoConfiguration.class })
public class Application {

	public static void main(String[] args) {
		final Logger logger = LoggerFactory.getLogger(Application.class);
		logger.info("Application started");
		Thread.setDefaultUncaughtExceptionHandler(new Thread.UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread t, Throwable error) {
				logger.error("Error ", error);
			}
		});

		SpringApplication.run(Application.class, args);
	}

	// @GetMapping("/hello")
	// public String hello(@RequestParam(value = "name", defaultValue = "World") String name) {
	// return String.format("Hello %s!", name);
	// }

}