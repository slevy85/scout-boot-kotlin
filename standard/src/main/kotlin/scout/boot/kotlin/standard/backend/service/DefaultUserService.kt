package scout.boot.kotlin.standard.backend.service

import org.eclipse.scout.rt.platform.util.IOUtility
import org.modelmapper.ModelMapper
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import scout.boot.kotlin.standard.backend.controller.ReadApiPermission
import scout.boot.kotlin.standard.backend.entity.UserEntity
import scout.boot.kotlin.standard.backend.repository.UserRepository
import scout.boot.kotlin.standard.backend.security.AccessControlService
import scout.boot.kotlin.standard.model.Document
import scout.boot.kotlin.standard.model.Role
import scout.boot.kotlin.standard.model.User
import scout.boot.kotlin.standard.model.service.DocumentService
import scout.boot.kotlin.standard.model.service.PasswordService
import scout.boot.kotlin.standard.model.service.RoleService
import scout.boot.kotlin.standard.model.service.UserService
import scout.boot.kotlin.standard.ui.ResourceBase
import scout.boot.kotlin.standard.ui.admin.ViewAdminOutlinePermission
import scout.boot.kotlin.standard.ui.admin.db.ReadDatabaseAdministrationConsolePermission
import scout.boot.kotlin.standard.ui.admin.user.UserPictureProviderService
import scout.boot.kotlin.standard.ui.business.task.CreateTaskPermission
import scout.boot.kotlin.standard.ui.business.task.ReadTaskPermission
import scout.boot.kotlin.standard.ui.business.task.UpdateTaskPermission
import scout.boot.kotlin.standard.ui.business.task.ViewAllTasksPermission
import java.io.IOException
import java.util.Arrays
import javax.annotation.PostConstruct
import kotlin.collections.HashMap
import kotlin.collections.HashSet

@Service
class DefaultUserService : UserService, MapperService<User, UserEntity> {

    @Autowired lateinit private var userRepository: UserRepository

    @Autowired lateinit private var roleService: RoleService

    @Autowired lateinit private var documentService: DocumentService

    @Autowired lateinit private var passwordService: PasswordService

    @Autowired lateinit private var userPictureProviderService: UserPictureProviderService

    @Autowired lateinit private var accessControlService: AccessControlService

    override val all: List<User>
        @Transactional(readOnly = true) get() = userRepository.findAll().map { user -> convertToModel(user, User::class.java) }

    override val mapper: ModelMapper
        get() {
            super<MapperService>.mapper.createTypeMap(UserEntity::class.java, User::class.java).setPostConverter {
                it.destination.roles = it.source.roles!!.toHashSet()
                it.destination
            }

            super<MapperService>.mapper.createTypeMap(User::class.java, UserEntity::class.java).setPostConverter {
                it.destination.roles = it.source.roles.toHashSet()
                it.destination
            }

            return super<MapperService>.mapper
        }

    protected fun getDefaultUserPicture(userId: String, image: String): Document? {
        try {
            val data = IOUtility.readFromUrl(ResourceBase::class.java.getResource("img/user/" + image))
            val picture = Document(image, data, Document.TYPE_PICTURE)

            userPictureProviderService.addUserPicture(userId, picture.data)
            documentService.save(picture)

            return picture
        } catch (e: IOException) {
            LOG.error("Error while loading " + image, e)
        }

        return null
    }

    override fun exists(userId: String) = userRepository.exists(userId)

    @Transactional(readOnly = true) override fun get(userId: String): User? {
        val user = userRepository.getOne(userId)
        return if (user != null) convertToModel(user, User::class.java) else null
    }

    @Transactional override fun save(user: User?) {
        if (user == null) {
            return
        }

        validate(user)

        userRepository.save(convertToEntity(user, UserEntity::class.java))
        accessControlService.clearCache()
    }

    @Transactional(readOnly = true) override fun getRoles(userId: String): Set<Role> {
        val userEntity = userRepository.getOne(userId)

        return if (userEntity != null) {
            userEntity.roles!!.mapTo(HashSet()) { roleId -> roleService.get(roleId) }
        } else HashSet()

    }

    @Transactional(readOnly = true) override fun getPicture(userId: String): Document? {
        val userEntity = userRepository.getOne(userId)
        if (userEntity != null) {
            val pictureId = userEntity.pictureId

            if (pictureId != null && documentService.exists(pictureId)) {
                return documentService.get(pictureId)
            }
        }

        return null
    }

    @Transactional override fun setPicture(userId: String, picture: Document?) {
        val userEntity = userRepository.getOne(userId)
        if (userEntity != null) {
            if (picture != null) {
                userEntity.pictureId = picture.id
                documentService.save(picture)
                userPictureProviderService.addUserPicture(userId, picture.data)
                userRepository.save(userEntity)
            }
        }
    }

    /**
     * Add initial demo entities: roles and users.
     */
    @PostConstruct
    fun init() {
        initRoles()
        initUsers()
    }

    /**
     * Add roles: root, dba, super user and user.
     */
    protected fun initRoles() {
        LOG.info("Check and initialise roles")

        if (!roleService.exists(Role.ROOT_ID)) {
            roleService.save(Role.ROOT)
        }
        val roles = HashMap<String, Array<String>>()
        roles.put(API, arrayOf(ReadApiPermission::class.java.name))
        roles.put(DBA, arrayOf(ReadDatabaseAdministrationConsolePermission::class.java.name, ViewAdminOutlinePermission::class.java.name))
        roles.put(SUPER_USER, arrayOf(ReadTaskPermission::class.java.name, CreateTaskPermission::class.java.name, UpdateTaskPermission::class.java.name, ViewAllTasksPermission::class.java.name))
        roles.put(USER, arrayOf(ReadTaskPermission::class.java.name, CreateTaskPermission::class.java.name, UpdateTaskPermission::class.java.name))

        roles.forEach { s, p ->
            if (!roleService.exists(s)) {
                val roleSuperUser = Role(s)
                roleSuperUser.permissions = p.toHashSet()
                roleService.save(roleSuperUser)
            }
        }
    }

    /**
     * Add users: root, alice and bob.
     */
    private fun initUsers() {
        LOG.info("Check and initialise users")

        addUser(USER_ROOT, "Root", "eclipse", "eclipse.jpg", Role.ROOT_ID)
        addUser(USER_ALICE, "Alice", "test", "alice.jpg", USER, SUPER_USER, API, DBA)
        addUser(USER_BOB, "Bob", "test", "bob.jpg", USER, API)
    }

    private fun addUser(login: String, firstName: String, passwordPlain: String, pictureFile: String?, vararg roles: String) {
        if (exists(login)) {
            return
        }

        val user = User(login, firstName, passwordService.calculatePasswordHash(passwordPlain))

        user.roles!!.addAll(Arrays.asList(*roles))

        if (pictureFile != null) {
            val picture = getDefaultUserPicture(login, pictureFile)
            user.pictureId = picture!!.id
        }

        save(user)
    }

    companion object {
        private val LOG = LoggerFactory.getLogger(DefaultUserService::class.java)

        protected val DBA = "DBA"
        protected val API = "API"

        protected val SUPER_USER = "SuperUser"
        protected val USER = "User"

        protected val USER_ROOT = "root"
        protected val USER_ALICE = "alice"
        protected val USER_BOB = "bob"
    }
}
