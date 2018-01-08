package scout.boot.kotlin.standard.ui.admin

import scout.boot.kotlin.standard.ui.admin.db.DatabaseAdministrationConsolePage
import scout.boot.kotlin.standard.ui.admin.role.PermissionTablePage
import scout.boot.kotlin.standard.ui.admin.role.RoleTablePage
import scout.boot.kotlin.standard.ui.admin.user.UserTablePage

import org.eclipse.scout.boot.ui.commons.fonts.FontAwesomeIcons
import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutline
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage
import org.eclipse.scout.rt.platform.BEANS
import org.eclipse.scout.rt.platform.Bean
import org.eclipse.scout.rt.shared.TEXTS

@Bean
class AdminOutline : AbstractOutline() {

    override fun getConfiguredTitle(): String {
        return TEXTS.get("Administration")
    }

    override fun getConfiguredIconId(): String {
        return FontAwesomeIcons.fa_users
    }

    override fun execCreateChildPages(pageList: MutableList<IPage<*>>?) {
        pageList!!.add(BEANS.get(UserTablePage::class.java))
        pageList.add(BEANS.get(RoleTablePage::class.java))
        pageList.add(BEANS.get(PermissionTablePage::class.java))
        pageList.add(BEANS.get(DatabaseAdministrationConsolePage::class.java))
    }
}
