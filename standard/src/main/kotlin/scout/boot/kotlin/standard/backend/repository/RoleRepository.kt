package scout.boot.kotlin.standard.backend.repository

import scout.boot.kotlin.standard.backend.entity.RoleEntity
import org.springframework.data.jpa.repository.JpaRepository

interface RoleRepository : JpaRepository<RoleEntity, String>
