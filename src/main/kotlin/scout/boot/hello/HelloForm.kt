package scout.boot.hello

import org.eclipse.scout.rt.client.ui.form.AbstractForm
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField

class HelloForm : AbstractForm() {

    inner class MainBox : AbstractGroupBox() {

        inner class HelloBox : AbstractGroupBox() {

            inner class HelloField : AbstractStringField() {

                override fun execInitField() {
                    label = "Hello"
                    value = "World"
                }
            }
        }
    }
}