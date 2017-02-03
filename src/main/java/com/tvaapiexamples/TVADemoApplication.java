package com.tvaapiexamples;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class TVADemoApplication {


	public static void main(String[] args) {

		CreateProgInfoTable createProgInfoTable = new CreateProgInfoTable();
		createProgInfoTable.runCreateProgInfoTableExample();

		APICodeExamples apiCodeExamples = new APICodeExamples();
		apiCodeExamples.runAPICodeExamples();

		SpringApplication.run(TVADemoApplication.class, args);
	}
}
