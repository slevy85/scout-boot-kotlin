package scout.boot.kotlin.standard.ui.admin.user

import scout.boot.kotlin.standard.model.User
import scout.boot.kotlin.standard.model.service.UserService

import java.util.ArrayList

import javax.inject.Inject
import org.eclipse.scout.rt.shared.services.lookup.ILookupRow
import org.eclipse.scout.rt.shared.services.lookup.LocalLookupCall
import org.eclipse.scout.rt.shared.services.lookup.LookupRow

class UserLookupCall : LocalLookupCall<String>() {

    @Inject
    internal var userService: UserService? = null

    override fun execCreateLookupRows(): List<ILookupRow<String>> {
        val list = ArrayList<ILookupRow<String>>()

        for (user in userService!!.all) {
            list.add(LookupRow(user.id, user.toDisplayText()))
        }

        return list
    }

    companion object {

        private val serialVersionUID = 1L
    }
}
