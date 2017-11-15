package scout.boot.hello

import org.eclipse.scout.boot.ui.commons.fonts.FontAwesomeIcons
import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutline
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage
import org.eclipse.scout.rt.platform.Bean

@Bean
class HelloOutline : AbstractOutline() {

    override fun getConfiguredTitle() = "Hello"
    override fun getConfiguredIconId() = FontAwesomeIcons.fa_globe
    override fun execCreateChildPages(pageList: MutableList<IPage<*>>?) {
        pageList!!.add(HelloPage())
    }

}
