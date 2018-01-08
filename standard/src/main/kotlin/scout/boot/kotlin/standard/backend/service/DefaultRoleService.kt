package scout.boot.kotlin.standard.backend.service

import org.modelmapper.Converter
import org.modelmapper.ModelMapper
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import scout.boot.kotlin.standard.backend.entity.RoleEntity
import scout.boot.kotlin.standard.backend.repository.RoleRepository
import scout.boot.kotlin.standard.backend.security.AccessControlService
import scout.boot.kotlin.standard.model.Role
import scout.boot.kotlin.standard.model.service.RoleService

@Service
class DefaultRoleService(private val roleRepository: RoleRepository,
                         private val accessControlService: AccessControlService?) : RoleService, MapperService<Role, RoleEntity> {

    override val all: List<Role>
        @Transactional(readOnly = true)
        get() = roleRepository.findAll().map { role -> convertToModel(role, Role::class.java) }

    override val mapper: ModelMapper
        get() {
            val mapper = super.mapper

            mapper.createTypeMap(RoleEntity::class.java, Role::class.java).postConverter = Converter {
                it.destination.permissions = it.source.permissions.toMutableSet()
                it.destination
            }

            mapper.createTypeMap(Role::class.java, RoleEntity::class.java).postConverter = Converter {
                it.destination.permissions = it.source.permissions.toHashSet()
                it.destination
            }

            return mapper
        }

    override fun exists(roleId: String) = roleRepository.exists(roleId)

    @Transactional(readOnly = true)
    override fun get(roleId: String) = convertToModel(roleRepository.getOne(roleId), Role::class.java)

    @Transactional
    override fun save(role: Role) {
        validate(role)
        val roleEntity = convertToEntity(role, RoleEntity::class.java)
        roleRepository.save(roleEntity)
        accessControlService?.clearCache()
    }

}
