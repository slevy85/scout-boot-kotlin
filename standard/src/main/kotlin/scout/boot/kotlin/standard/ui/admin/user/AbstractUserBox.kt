package scout.boot.kotlin.standard.ui.admin.user

import org.eclipse.scout.rt.client.ui.dnd.IDNDSupport
import scout.boot.kotlin.standard.model.Document
import scout.boot.kotlin.standard.model.User
import java.util.Locale

import org.eclipse.scout.rt.client.ui.dnd.ResourceListTransferObject
import org.eclipse.scout.rt.client.ui.dnd.TransferObject
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox
import org.eclipse.scout.rt.client.ui.form.fields.imagefield.AbstractImageField
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField
import org.eclipse.scout.rt.platform.Order
import org.eclipse.scout.rt.platform.exception.VetoException
import org.eclipse.scout.rt.platform.resource.BinaryResource
import org.eclipse.scout.rt.platform.util.CollectionUtility
import org.eclipse.scout.rt.shared.TEXTS
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall

abstract class AbstractUserBox : AbstractGroupBox() {

    val pictureField: PictureField
        get() = getFieldByClass(PictureField::class.java)

    val firstNameField: FirstNameField
        get() = getFieldByClass(FirstNameField::class.java)

    val lastNameField: LastNameField
        get() = getFieldByClass(LastNameField::class.java)

    val userIdField: UserIdField
        get() = getFieldByClass(UserIdField::class.java)

    val loacleField: LoacleField
        get() = getFieldByClass(LoacleField::class.java)

    override fun getConfiguredGridWeightY(): Double {
        return 0.0
    }

    @Order(10.0)
    inner class FirstNameField : AbstractStringField() {
        override fun getConfiguredLabel(): String {
            return TEXTS.get("FirstName")
        }

        override fun getConfiguredMandatory(): Boolean {
            return true
        }

        override fun getConfiguredMaxLength(): Int {
            return 128
        }
    }

    @Order(20.0)
    inner class LastNameField : AbstractStringField() {
        override fun getConfiguredLabel(): String {
            return TEXTS.get("LastName")
        }

        override fun getConfiguredMaxLength(): Int {
            return 128
        }
    }

    @Order(30.0)
    inner class LoacleField : AbstractSmartField<Locale>() {
        override fun getConfiguredLabel(): String {
            return TEXTS.get("Language")
        }

        override fun getConfiguredMandatory(): Boolean {
            return true
        }

        override fun getConfiguredLookupCall(): Class<out ILookupCall<Locale>> {
            return LocaleLookupCall::class.java
        }
    }

    @Order(40.0)
    inner class UserIdField : AbstractStringField() {
        override fun getConfiguredLabel(): String {
            return TEXTS.get("UserName")
        }

        override fun getConfiguredMandatory(): Boolean {
            return true
        }

        override fun getConfiguredMaxLength(): Int {
            return 128
        }
    }

    @Order(60.0)
    inner class PictureField : AbstractImageField() {

        var picture: Document? = null
            set(picture) {
                field = picture

                if (picture != null) {
                    image = picture.data
                    imageId = picture.name
                } else {
                    image = byteArrayOf()
                    imageId = ""
                }

                touch()
            }

        override fun getConfiguredTooltipText(): String {
            return TEXTS.get("DropImageFile")
        }

        override fun getConfiguredLabelVisible(): Boolean {
            return false
        }

        override fun getConfiguredGridH(): Int {
            return 4
        }

        override fun getConfiguredAutoFit(): Boolean {
            return true
        }

        override fun getConfiguredDropType(): Int {
            return IDNDSupport.TYPE_FILE_TRANSFER
        }

        override fun execDropRequest(transferObject: TransferObject?) {
            clearErrorStatus()

            if (transferObject is ResourceListTransferObject) {
                val resources = transferObject.resources

                if (resources.size > 0) {
                    val resource = CollectionUtility.firstElement(resources)
                    val resource_size = resource.contentLength

                    if (resource_size > PICTURE_MAX_FILE_SIZE) {
                        throw VetoException(TEXTS.get("ImageFileTooLarge", "" + PICTURE_MAX_FILE_SIZE / 1024))
                    }

                    picture = Document(resource.filename, resource.content, Document.TYPE_PICTURE)
                }
            }
        }
    }

    fun importFormFieldData(user: User?) {
        if (user == null) {
            loacleField.value = User.LOCALE_DEFAULT
            return
        }

        userIdField.value = user.id
        firstNameField.value = user.firstName
        lastNameField.value = user.lastName
        loacleField.value = user.locale
    }

    fun exportFormFieldData(user: User) {
        user.id = userIdField.value
        user.firstName = firstNameField.value
        user.lastName = lastNameField.value
        user.locale = loacleField.value
    }

    fun importUserPicture(picture: Document?) {
        pictureField.picture = picture
    }

    fun exportUserPicture(): Document? {
        return if (pictureField.isSaveNeeded) {
            pictureField.picture
        } else null

    }

    companion object {

        val PICTURE_MAX_FILE_SIZE = 300 * 1024
    }
}
