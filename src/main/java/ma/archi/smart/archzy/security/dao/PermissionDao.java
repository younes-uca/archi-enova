package ma.archi.smart.archzy.security.dao;

import ma.archi.smart.archzy.security.bean.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionDao extends JpaRepository<Permission, Long> {
    public Permission findByName(String name);
}
