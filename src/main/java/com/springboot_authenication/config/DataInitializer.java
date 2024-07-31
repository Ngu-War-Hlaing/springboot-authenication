package com.springboot_authenication.config;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.springboot_authenication.service.RoleService;

import jakarta.annotation.PostConstruct;
@Component
public class DataInitializer {
	@Autowired
	private final RoleService roleService;

	public DataInitializer(RoleService roleService) {
		this.roleService = roleService;
	}
	@PostConstruct
	public void init() throws Exception{
		roleService.initializeRoles(Arrays.asList("ADMIN","USER","OTHER"));
	}
	
	

}
