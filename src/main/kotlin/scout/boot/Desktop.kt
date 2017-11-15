package scout.boot


import org.eclipse.scout.boot.ui.scout.AbstractScoutBootDesktop
import org.eclipse.scout.rt.client.ui.action.view.IViewButton
import org.eclipse.scout.rt.client.ui.desktop.AbstractDesktopExtension
import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutlineViewButton
import org.eclipse.scout.rt.client.ui.desktop.outline.IOutline
import org.eclipse.scout.rt.platform.BEANS
import org.eclipse.scout.rt.platform.Bean
import org.eclipse.scout.rt.platform.Order
import org.eclipse.scout.rt.platform.config.PlatformConfigProperties.ApplicationNameProperty
import org.eclipse.scout.rt.platform.util.collection.OrderedCollection
import scout.boot.hello.HelloOutline

import javax.inject.Inject

@Bean
class Desktop @Inject
constructor(applicationNameConfig: ApplicationNameProperty) : AbstractScoutBootDesktop(applicationNameConfig) {

    override fun execDefaultView() = setOutline(HelloOutline::class.java)

    @Order(10.0)
    inner class HelloOutlineViewButton constructor(outlineClass: Class<out HelloOutline>) : AbstractOutlineViewButton(this@Desktop, outlineClass) {

        constructor() : this(HelloOutline::class.java)

        override fun getConfiguredDisplayStyle()= IViewButton.DisplayStyle.TAB

    }

    class DesktopExtension : AbstractDesktopExtension() {
        override fun contributeOutlines(outlines: OrderedCollection<IOutline>) {
            outlines.addAllLast(BEANS.all(IOutline::class.java))
        }
    }

}
