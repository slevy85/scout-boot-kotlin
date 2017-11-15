package scout.boot.hello

import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithNodes
import org.eclipse.scout.rt.client.ui.form.IForm
import org.eclipse.scout.rt.platform.Bean

@Bean
class HelloPage : AbstractPageWithNodes() {

    override fun getConfiguredTitle()= "Hello"
    override fun getConfiguredDetailForm() = HelloForm::class.java

}
