package scout.boot.kotlin.standard.ui.admin.db

import org.eclipse.scout.boot.ui.commons.widgets.BrowserFieldBorderless
import org.eclipse.scout.rt.client.context.ClientRunContexts
import org.eclipse.scout.rt.client.job.ModelJobs
import org.eclipse.scout.rt.client.ui.desktop.IDesktop
import org.eclipse.scout.rt.client.ui.desktop.notification.DesktopNotification
import org.eclipse.scout.rt.client.ui.desktop.notification.IDesktopNotification
import org.eclipse.scout.rt.client.ui.form.AbstractForm
import org.eclipse.scout.rt.client.ui.form.IForm
import org.eclipse.scout.rt.client.ui.form.fields.browserfield.AbstractBrowserField
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox
import org.eclipse.scout.rt.platform.Bean
import org.eclipse.scout.rt.platform.Order
import org.eclipse.scout.rt.platform.util.concurrent.IRunnable

@Bean
class DatabaseAdministrationConsoleForm : AbstractForm() {

    override fun execInitForm() {
        execDisplayJdbcUrl()
    }

    override fun getConfiguredDisplayHint(): Int {
        return IForm.DISPLAY_HINT_VIEW
    }

    @Order(1000.0)
    inner class MainBox : AbstractGroupBox() {

        override fun getConfiguredLabelVisible() = false
        override fun getConfiguredStatusVisible() = false
        override fun getConfiguredBorderVisible() = false
        override fun getConfiguredCssClass() = BrowserFieldBorderless.MAIN_BOX_CSS_CLASS

        @Order(1000.0)
        inner class BrowserField : AbstractBrowserField() {

            override fun getConfiguredGridWeightY() = 1.0

            override fun getConfiguredLabelVisible() = false

            override fun getConfiguredStatusVisible(): Boolean {
                return false
            }

            override fun getConfiguredSandboxEnabled(): Boolean {
                return false
            }

            override fun getConfiguredCssClass(): String {
                return BrowserFieldBorderless.BROWSER_FIELD_CSS_CLASS
            }

            override fun getConfiguredScrollBarEnabled(): Boolean {
                return true
            }

            override fun execInitField() {
                location = H2_CONSOLE_URL
            }
        }
    }

    fun execDisplayJdbcUrl() {
        ModelJobs.schedule({
            val notification = DesktopNotification("" + "Connect to JDBC URL:\n" + JDBC_URL)
            IDesktop.CURRENT.get().addNotification(notification)
        }, ModelJobs.newInput(ClientRunContexts.copyCurrent()))
    }

    companion object {

        protected val H2_CONSOLE_URL = "http://localhost:8080/h2-console"
        protected val JDBC_URL = "jdbc:h2:~/standard_db"
    }
}
