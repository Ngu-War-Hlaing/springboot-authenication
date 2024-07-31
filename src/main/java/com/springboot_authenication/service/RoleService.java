package com.springboot_authenication.service;

import java.util.List;

import org.springframework.stereotype.Service;
@Service
public interface RoleService {
	void initializeRoles(List<String> roles) throws Exception;
	

}
