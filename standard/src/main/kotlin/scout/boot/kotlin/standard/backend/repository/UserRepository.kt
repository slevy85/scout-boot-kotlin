package scout.boot.kotlin.standard.backend.repository

import scout.boot.kotlin.standard.backend.entity.UserEntity
import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<UserEntity, String>
