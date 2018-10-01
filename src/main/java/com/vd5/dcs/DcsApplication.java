//package com.vd5.dcs;
//
//import com.vd5.data.repository.ExtendedRepositoryImpl;
//import org.springframework.boot.SpringApplication;
//import org.springframework.boot.autoconfigure.SpringBootApplication;
//import org.springframework.boot.autoconfigure.domain.EntityScan;
//import org.springframework.cache.annotation.EnableCaching;
//import org.springframework.context.annotation.ComponentScan;
//import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
//import org.springframework.scheduling.annotation.EnableScheduling;
//
//@SpringBootApplication
//@EnableJpaRepositories(repositoryBaseClass = ExtendedRepositoryImpl.class, basePackages = {"com.vd5.data.repository"})
//@EntityScan(basePackages = {"com.vd5.data.entities"})
//@ComponentScan(basePackages = {"com.vd5"})
//@EnableScheduling
//@EnableCaching
//public class DcsApplication {
//	public static void main(String[] args) {
//		SpringApplication.run(DcsApplication.class, args);
//	}
//}
