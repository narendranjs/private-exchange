/**
 * 
 */
package com.exchange.recent.rates;

import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.builder.SpringApplicationBuilder;
import org.springframework.boot.web.support.SpringBootServletInitializer;
import org.springframework.boot.SpringApplication;
import org.springframework.context.annotation.PropertySource;

/**
 * @author narendranjs
 *
 */
@SpringBootApplication
@PropertySource("classpath:private-exchange.properties")
public class MainRunnerApplication extends SpringBootServletInitializer{

	@Override
	protected SpringApplicationBuilder configure(SpringApplicationBuilder builder) {
		// TODO Auto-generated method stub
		super.configure(builder);
		return builder.sources(MainRunnerApplication.class);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		SpringApplication.run(MainRunnerApplication.class,args);

	}

}
