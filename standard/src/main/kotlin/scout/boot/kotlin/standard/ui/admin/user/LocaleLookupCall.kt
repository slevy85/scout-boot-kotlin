package scout.boot.kotlin.standard.ui.admin.user

import java.util.ArrayList
import java.util.Arrays
import java.util.Comparator
import java.util.Locale

import org.eclipse.scout.rt.platform.Bean
import org.eclipse.scout.rt.platform.exception.ProcessingException
import org.eclipse.scout.rt.shared.services.lookup.LocalLookupCall
import org.eclipse.scout.rt.shared.services.lookup.LookupRow

@Bean
class LocaleLookupCall : LocalLookupCall<Locale>() {

    val availableLocales: List<Locale>
        get() = Arrays.asList(*sort(Locale.getAvailableLocales()))

    @Throws(ProcessingException::class)
    override fun execCreateLookupRows(): List<LookupRow<Locale>> {
        val rows = ArrayList<LookupRow<Locale>>()

        availableLocales
                .stream()
                .forEach { locale -> rows.add(LookupRow(locale, locale.displayName)) }

        return rows
    }

    private fun sort(locales: Array<Locale>): Array<Locale> {
        Arrays.sort(locales) { locale1, locale2 -> locale1.toString().compareTo(locale2.toString()) }

        return locales
    }

    companion object {

        private val serialVersionUID = 1L
    }

}
