package com.springboot_authenication.service;

import java.util.List;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.springboot_authenication.model.Role; // Correct import for your Role class
import com.springboot_authenication.repository.RoleRepository;

@Service
public class RoleServiceImpl implements RoleService {
    private static final Logger logger = LoggerFactory.getLogger(RoleServiceImpl.class);
    @Autowired
    private final RoleRepository roleRepository;
    
    public RoleServiceImpl(RoleRepository roleRepository) {
        this.roleRepository = roleRepository;
    }

    @Override
    @Transactional
    public void initializeRoles(List<String> roles) throws Exception {
        for (String roleName : roles) {
            try {
                if (roleRepository.findByName(roleName).isEmpty()) {
                    Role role = new Role(); // Correct Role class
                    role.setName(roleName); // Ensure this method exists in your Role class
                    roleRepository.save(role); // Correct usage of roleRepository
                    logger.info("Role {} has been inserted.", roleName);
                } else {
                    logger.info("Role {} already exists.", roleName);
                }
            } catch (DataAccessException e) {
                logger.error("Database access error while inserting role {}: {}", roleName, e.getMessage());
                throw new Exception("Error while accessing the database for role: " + roleName, e);
            } catch (Exception e) {
                logger.error("Unexpected error while inserting role {}: {}", roleName, e.getMessage());
                throw new Exception("Unexpected error for role: " + roleName, e);
            }
        }
    }
}
