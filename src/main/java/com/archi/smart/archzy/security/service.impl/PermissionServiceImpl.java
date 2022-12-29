package com.archi.smart.archzy.security.service.impl;

import com.archi.smart.archzy.security.bean.Permission;
import com.archi.smart.archzy.security.dao.PermissionDao;
import com.archi.smart.archzy.security.service.facade.PermissionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class PermissionServiceImpl implements PermissionService {
    @Autowired
    private PermissionDao permissionDao;

    @Override
    public Permission save(Permission permission) {
        Permission perm = permissionDao.findByName(permission.getName());
        return perm == null ? permissionDao.save(permission) : perm;
    }
}
