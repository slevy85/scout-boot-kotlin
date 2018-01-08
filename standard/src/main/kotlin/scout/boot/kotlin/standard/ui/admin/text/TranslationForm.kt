package scout.boot.kotlin.standard.ui.admin.text

import scout.boot.kotlin.standard.ui.TextDbProviderService
import scout.boot.kotlin.standard.ui.admin.text.TranslationForm.MainBox.CancelButton
import scout.boot.kotlin.standard.ui.admin.text.TranslationForm.MainBox.OkButton
import scout.boot.kotlin.standard.ui.admin.text.TranslationForm.MainBox.TopBox
import scout.boot.kotlin.standard.ui.admin.text.TranslationForm.MainBox.TopBox.HasTextFilterField
import scout.boot.kotlin.standard.ui.admin.text.TranslationForm.MainBox.TopBox.KeyField
import scout.boot.kotlin.standard.ui.admin.text.TranslationForm.MainBox.TopBox.LocaleFilterField
import scout.boot.kotlin.standard.ui.admin.text.TranslationForm.MainBox.TopBox.TranslationTableField
import scout.boot.kotlin.standard.ui.admin.text.TranslationForm.MainBox.TopBox.TranslationTableField.Table
import scout.boot.kotlin.standard.ui.admin.user.LocaleLookupCall

import java.util.Locale

import javax.inject.Inject

import org.eclipse.scout.rt.client.ui.basic.table.AbstractTable
import org.eclipse.scout.rt.client.ui.basic.table.ITableRow
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractBooleanColumn
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractSmartColumn
import org.eclipse.scout.rt.client.ui.basic.table.columns.AbstractStringColumn
import org.eclipse.scout.rt.client.ui.form.AbstractForm
import org.eclipse.scout.rt.client.ui.form.AbstractFormHandler
import org.eclipse.scout.rt.client.ui.form.fields.IFormField
import org.eclipse.scout.rt.client.ui.form.fields.booleanfield.AbstractBooleanField
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField
import org.eclipse.scout.rt.client.ui.form.fields.tablefield.AbstractTableField
import org.eclipse.scout.rt.platform.Bean
import org.eclipse.scout.rt.platform.Order
import org.eclipse.scout.rt.platform.util.StringUtility
import org.eclipse.scout.rt.shared.TEXTS
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall

@Bean
class TranslationForm : AbstractForm() {

    @Inject
    internal var textService: TextDbProviderService? = null

    @Inject
    internal var localeLookup: LocaleLookupCall? = null

    var key: String
        get() = keyField.value
        set(key) {
            keyField.value = key
        }

    val cancelButton: CancelButton
        get() = getFieldByClass(CancelButton::class.java)

    val mainBox: MainBox
        get() = getFieldByClass(MainBox::class.java)

    val topBox: TopBox
        get() = getFieldByClass(TopBox::class.java)

    val keyField: KeyField
        get() = getFieldByClass(KeyField::class.java)

    val translationTableField: TranslationTableField
        get() = getFieldByClass(TranslationTableField::class.java)

    val localeFilterField: LocaleFilterField
        get() = getFieldByClass(LocaleFilterField::class.java)

    val hasTextFilterField: HasTextFilterField
        get() = getFieldByClass(HasTextFilterField::class.java)

    val okButton: OkButton
        get() = getFieldByClass(OkButton::class.java)

    override fun getConfiguredTitle(): String {
        return TEXTS.get("Translation")
    }

    fun startModify() {
        startInternalExclusive(ModifyHandler())
    }

    @Order(1000.0)
    inner class MainBox : AbstractGroupBox() {

        @Order(1000.0)
        inner class TopBox : AbstractGroupBox() {

            @Order(10.0)
            inner class KeyField : AbstractStringField() {

                override fun getConfiguredEnabled(): Boolean {
                    return false
                }

                override fun getConfiguredLabel(): String {
                    return TEXTS.get("TextKey")
                }

                override fun getConfiguredGridW(): Int {
                    return 2
                }

                override fun getConfiguredMaxLength(): Int {
                    return 128
                }
            }

            @Order(20.0)
            inner class LocaleFilterField : AbstractSmartField<Locale>() {
                override fun getConfiguredLabel(): String {
                    return TEXTS.get("LocaleFilter")
                }

                override fun getConfiguredLookupCall(): Class<out ILookupCall<Locale>> {
                    return LocaleLookupCall::class.java
                }

                override fun execChangedValue() {
                    reloadTranslations()
                }
            }

            @Order(30.0)
            inner class HasTextFilterField : AbstractBooleanField() {
                override fun getConfiguredLabel(): String {
                    return TEXTS.get("HasTextFilter")
                }

                override fun execChangedValue() {
                    reloadTranslations()
                }
            }

            @Order(40.0)
            inner class TranslationTableField : AbstractTableField<Table>() {
                inner class Table : AbstractTable() {

                    val hasTextColumn: HasTextColumn
                        get() = columnSet.getColumnByClass(HasTextColumn::class.java)

                    val translationColumn: TranslationColumn
                        get() = columnSet.getColumnByClass(TranslationColumn::class.java)

                    val localeColumn: LocaleColumn
                        get() = columnSet.getColumnByClass(LocaleColumn::class.java)

                    @Order(10.0)
                    inner class LocaleColumn : AbstractSmartColumn<Locale>() {
                        override fun getConfiguredHeaderText(): String {
                            return TEXTS.get("Language")
                        }

                        override fun getConfiguredWidth(): Int {
                            return 200
                        }

                        override fun getConfiguredLookupCall(): Class<out ILookupCall<Locale>> {
                            return LocaleLookupCall::class.java
                        }
                    }

                    @Order(20.0)
                    inner class TranslationColumn : AbstractStringColumn() {
                        override fun getConfiguredHeaderText(): String {
                            return TEXTS.get("Translation")
                        }

                        override fun getConfiguredWidth(): Int {
                            return 300
                        }

                        override fun getConfiguredEditable(): Boolean {
                            return true
                        }

                        override fun execCompleteEdit(row: ITableRow, editingField: IFormField) {
                            val value = (editingField as AbstractStringField).value

                            row.getCellForUpdate(translationColumn).value = value
                            row.getCellForUpdate(hasTextColumn).value = StringUtility.hasText(value)
                        }
                    }

                    @Order(3000.0)
                    inner class HasTextColumn : AbstractBooleanColumn() {
                        override fun getConfiguredHeaderText(): String {
                            return TEXTS.get("HasText")
                        }

                        override fun getConfiguredWidth(): Int {
                            return 100
                        }
                    }

                }

                override fun getConfiguredLabelVisible(): Boolean {
                    return false
                }

                override fun getConfiguredGridW(): Int {
                    return 2
                }

                override fun getConfiguredGridH(): Int {
                    return 6
                }
            }

        }

        @Order(100000.0)
        inner class OkButton : AbstractOkButton()

        @Order(101000.0)
        inner class CancelButton : AbstractCancelButton()
    }

    inner class ModifyHandler : AbstractFormHandler() {

        override fun execLoad() {
            setEnabledPermission(UpdateTranslationPermission())
            reloadTranslations()
        }

        override fun execStore() {
            saveTranslations()
        }
    }

    private fun reloadTranslations() {
        val key = key
        val table = translationTableField.table
        val localeFilter = localeFilterField.value
        val textFilter = hasTextFilterField.value
        val map = textService!!.getTexts(key)

        table.deleteAllRows()

        localeLookup!!.availableLocales
                .stream()
                .forEach { locale ->
                    var addLocale = true
                    val hasText = map.containsKey(locale)

                    if (localeFilter != null && !localeFilter.toLanguageTag().contentEquals(Locale.ROOT.toLanguageTag())) {
                        if (!locale.toLanguageTag().startsWith(localeFilter.toLanguageTag())) {
                            addLocale = false
                        }
                    }

                    if (textFilter && !hasText) {
                        addLocale = false
                    }

                    if (addLocale) {
                        val row = table.createRow()
                        table.localeColumn.setValue(row, locale)
                        table.translationColumn.setValue(row, map[locale])
                        table.hasTextColumn.setValue(row, hasText)
                        table.addRow(row)
                    }
                }
    }

    private fun saveTranslations() {
        val key = key
        val table = translationTableField.table

        table.rows
                .stream()
                .forEach { row ->
                    val locale = row.keyValues[0] as Locale
                    val text = row.getCell(table.translationColumn).value as String

                    if (StringUtility.hasText(text)) {
                        textService!!.addText(key, locale, text)
                    } else {
                        textService!!.deleteText(key, locale)
                    }
                }
    }
}
