package scout.boot.kotlin.standard.ui.admin.db

import org.eclipse.scout.boot.ui.commons.fonts.FontAwesomeIcons
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes
import org.eclipse.scout.rt.client.ui.form.IForm
import org.eclipse.scout.rt.platform.Bean
import org.eclipse.scout.rt.platform.Order
import org.eclipse.scout.rt.shared.TEXTS

@Bean
class DatabaseAdministrationConsolePage : AbstractPageWithNodes() {

    override fun getConfiguredTitle(): String {
        return TEXTS.get("DatabaseAdministrationConsole")
    }

    override fun execInitPage() {
        setVisiblePermission(ReadDatabaseAdministrationConsolePermission())
    }

    override fun getConfiguredDetailForm(): Class<out IForm> {
        return DatabaseAdministrationConsoleForm::class.java
    }

    override fun getConfiguredLeaf(): Boolean {
        return true
    }

    @Order(10.0)
    inner class AssignMenu : AbstractMenu() {

        override fun getConfiguredText(): String {
            return "Show JDBC URL"
        }

        override fun getConfiguredIconId(): String {
            return FontAwesomeIcons.fa_info
        }

        override fun execAction() {
            if (detailForm is DatabaseAdministrationConsoleForm) {
                val form = detailForm as DatabaseAdministrationConsoleForm
                form.execDisplayJdbcUrl()
            }
        }

    }

}
