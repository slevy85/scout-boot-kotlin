package scout.boot.kotlin.standard.ui

import scout.boot.kotlin.standard.ui.admin.AdminOutline
import scout.boot.kotlin.standard.ui.admin.ViewAdminOutlinePermission
import scout.boot.kotlin.standard.ui.admin.user.OptionsForm
import scout.boot.kotlin.standard.ui.business.MainOutline

import javax.inject.Inject

import org.eclipse.scout.boot.ui.commons.fonts.FontAwesomeIcons
import org.eclipse.scout.rt.client.session.ClientSessionProvider
import org.eclipse.scout.rt.client.ui.action.keystroke.IKeyStroke
import org.eclipse.scout.rt.client.ui.action.menu.AbstractMenu
import org.eclipse.scout.rt.client.ui.action.view.IViewButton
import org.eclipse.scout.rt.client.ui.desktop.AbstractDesktop
import org.eclipse.scout.rt.client.ui.desktop.AbstractDesktopExtension
import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutlineViewButton
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline
import org.eclipse.scout.rt.client.ui.form.AbstractFormMenu
import org.eclipse.scout.rt.platform.BEANS
import org.eclipse.scout.rt.platform.Bean
import org.eclipse.scout.rt.platform.Order
import org.eclipse.scout.rt.platform.config.PlatformConfigProperties.ApplicationNameProperty
import org.eclipse.scout.rt.platform.util.collection.OrderedCollection
import org.eclipse.scout.rt.shared.TEXTS

/**
 * This Spring managed bean represents the desktop of web applications user interface.
 */
@Bean
class Desktop @Inject
constructor(private val applicationNameConfig: ApplicationNameProperty) : AbstractDesktop(false) {

    init {
        callInitializer()
    }

    override fun getConfiguredTitle(): String {
        return applicationNameConfig.value
    }

    override fun getConfiguredLogoId(): String {
        return LOGO_ICON
    }

    override fun execDefaultView() {
        setOutline(MainOutline::class.java)
    }

    @Order(10.0)
    inner class MainOutlineViewButton protected constructor(outlineClass: Class<out MainOutline>) : AbstractOutlineViewButton(this@Desktop, outlineClass) {

        constructor() : this(MainOutline::class.java) {}

        override fun getConfiguredDisplayStyle(): IViewButton.DisplayStyle {
            return IViewButton.DisplayStyle.TAB
        }

        override fun getConfiguredKeyStroke(): String {
            return IKeyStroke.F2
        }
    }

    @Order(2000.0)
    inner class AdminOutlineViewButton protected constructor(outlineClass: Class<out AdminOutline>) : AbstractOutlineViewButton(this@Desktop, outlineClass) {

        constructor() : this(AdminOutline::class.java) {}

        override fun execInitAction() {
            setVisiblePermission(ViewAdminOutlinePermission())
        }

        override fun getConfiguredIconId(): String {
            return FontAwesomeIcons.fa_users
        }

        override fun getConfiguredDisplayStyle(): IViewButton.DisplayStyle {
            return IViewButton.DisplayStyle.TAB
        }

        override fun getConfiguredKeyStroke(): String {
            return IKeyStroke.F3
        }
    }

    @Order(1000.0)
    inner class OptionsMenu : AbstractFormMenu<OptionsForm>() {

        override fun getConfiguredIconId(): String {
            return FontAwesomeIcons.fa_cog
        }

        override fun getConfiguredKeyStroke(): String {
            return IKeyStroke.F10
        }

        override fun getConfiguredTooltipText(): String {
            return TEXTS.get("Options")
        }

        /**
         * Force a reload of the user data when the options form is shown again.
         */
        override fun execSelectionChanged(selected: Boolean) {
            super.execSelectionChanged(selected)

            if (selected && form.isFormStarted) {
                (form as OptionsForm).reload()
            }
        }

        override fun createForm(): OptionsForm {
            return BEANS.get(OptionsForm::class.java)
        }
    }

    @Order(2000.0)
    inner class FileMenu : AbstractMenu() {

        override fun getConfiguredIconId(): String {
            return FontAwesomeIcons.fa_signOut
        }

        override fun getConfiguredTooltipText(): String {
            return TEXTS.get("Exit")
        }

        override fun execAction() {
            ClientSessionProvider.currentSession(ClientSession::class.java).stop()
        }
    }

    class DesktopExtension : AbstractDesktopExtension() {

        override fun contributeOutlines(outlines: OrderedCollection<IOutline>) {
            outlines.addAllLast(BEANS.all(IOutline::class.java))
        }
    }

    companion object {

        val LOGO_ICON = "eclipse_scout"
    }
}
