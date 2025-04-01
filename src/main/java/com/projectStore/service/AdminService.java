package com.projectStore.service;

import com.projectStore.dto.AdminCreationDTO;
import com.projectStore.entity.User;

import java.util.List;

public interface AdminService {
    User createAdmin(AdminCreationDTO dto, String ipAddress, String creatorUsername);

    List<User> getAllAdmins();
}
