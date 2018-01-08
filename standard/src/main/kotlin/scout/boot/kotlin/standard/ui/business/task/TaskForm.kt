package scout.boot.kotlin.standard.ui.business.task

import org.eclipse.scout.boot.ui.commons.AbstractDirtyFormHandler
import org.eclipse.scout.rt.client.ui.form.AbstractForm
import org.eclipse.scout.rt.client.ui.form.IForm
import org.eclipse.scout.rt.client.ui.form.fields.booleanfield.AbstractBooleanField
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractCancelButton
import org.eclipse.scout.rt.client.ui.form.fields.button.AbstractOkButton
import org.eclipse.scout.rt.client.ui.form.fields.datefield.AbstractDateField
import org.eclipse.scout.rt.client.ui.form.fields.datefield.AbstractDateTimeField
import org.eclipse.scout.rt.client.ui.form.fields.groupbox.AbstractGroupBox
import org.eclipse.scout.rt.client.ui.form.fields.sequencebox.AbstractSequenceBox
import org.eclipse.scout.rt.client.ui.form.fields.smartfield.AbstractSmartField
import org.eclipse.scout.rt.client.ui.form.fields.stringfield.AbstractStringField
import org.eclipse.scout.rt.platform.Bean
import org.eclipse.scout.rt.platform.Order
import org.eclipse.scout.rt.shared.TEXTS
import org.eclipse.scout.rt.shared.services.lookup.ILookupCall
import scout.boot.kotlin.standard.model.Task
import scout.boot.kotlin.standard.model.service.TaskService
import scout.boot.kotlin.standard.ui.ClientSession
import scout.boot.kotlin.standard.ui.admin.user.UserLookupCall
import scout.boot.kotlin.standard.ui.business.task.TaskForm.MainBox.*
import scout.boot.kotlin.standard.ui.business.task.TaskForm.MainBox.TopBox.*
import scout.boot.kotlin.standard.ui.business.task.TaskForm.MainBox.TopBox.AcceptedAndDoneBox.AcceptedField
import scout.boot.kotlin.standard.ui.business.task.TaskForm.MainBox.TopBox.AcceptedAndDoneBox.DoneField
import java.util.*
import javax.inject.Inject

@Bean
class TaskForm : AbstractForm() {

    var taskId: UUID? = null

    @Inject lateinit private var taskService: TaskService

    val cancelButton: CancelButton
        get() = getFieldByClass(CancelButton::class.java)

    val mainBox: MainBox
        get() = getFieldByClass(MainBox::class.java)

    val topBox: TopBox
        get() = getFieldByClass(TopBox::class.java)

    val titleField: TitleField
        get() = getFieldByClass(TitleField::class.java)

    val responsibleField: ResponsibleField
        get() = getFieldByClass(ResponsibleField::class.java)

    val assignedByField: AssignedByField
        get() = getFieldByClass(AssignedByField::class.java)

    val dueDateField: DueDateField
        get() = getFieldByClass(DueDateField::class.java)

    val assignedAtField: AssignedAtField
        get() = getFieldByClass(AssignedAtField::class.java)

    val doneField: DoneField
        get() = getFieldByClass(DoneField::class.java)

    val reminderField: ReminderField
        get() = getFieldByClass(ReminderField::class.java)

    val descriptionField: DescriptionField
        get() = getFieldByClass(DescriptionField::class.java)

    val acceptedField: AcceptedField
        get() = getFieldByClass(AcceptedField::class.java)

    val mySequenceBox: AcceptedAndDoneBox
        get() = getFieldByClass(AcceptedAndDoneBox::class.java)

    val okButton: OkButton
        get() = getFieldByClass(OkButton::class.java)

    override fun computeExclusiveKey(): Any? = taskId

    override fun getConfiguredDisplayHint(): Int = IForm.DISPLAY_HINT_VIEW

    override fun getConfiguredTitle(): String = TEXTS.get("Task")

    fun startModify() {
        startInternal(ModifyHandler())
    }

    fun startNew() {
        startInternal(NewHandler())
    }

    @Order(1000.0) inner class MainBox : AbstractGroupBox() {

        @Order(1000.0) inner class TopBox : AbstractGroupBox() {

            @Order(1000.0) inner class TitleField : AbstractStringField() {
                override fun getConfiguredLabel(): String = TEXTS.get("Title")

                override fun getConfiguredMandatory(): Boolean = true

                override fun getConfiguredMaxLength(): Int = 128
            }

            @Order(1500.0) inner class ResponsibleField : AbstractSmartField<String>() {
                override fun getConfiguredLabel(): String = TEXTS.get("Responsible")

                override fun getConfiguredMandatory(): Boolean = true

                override fun getConfiguredLookupCall(): Class<out ILookupCall<String>> = UserLookupCall::class.java

                override fun execChangedValue() {
                    if (!form.isFormLoading) {
                        val userId = ClientSession.get()!!.userId
                        acceptedField.value = userId == value
                        assignedByField.value = userId
                        assignedAtField.value = Date()
                    }
                }
            }

            @Order(1800.0) inner class AssignedByField : AbstractSmartField<String>() {
                override fun getConfiguredLabel(): String = TEXTS.get("AssignedBy")

                override fun getConfiguredEnabled(): Boolean = false

                override fun getConfiguredLookupCall(): Class<out ILookupCall<String>> = UserLookupCall::class.java
            }

            @Order(2500.0) inner class AssignedAtField : AbstractDateTimeField() {
                override fun getConfiguredLabel(): String = TEXTS.get("AssignedAt")

                override fun getConfiguredEnabled(): Boolean = false
            }

            @Order(3000.0) inner class DueDateField : AbstractDateField() {
                override fun getConfiguredLabel(): String = TEXTS.get("DueDate")

                override fun getConfiguredMandatory(): Boolean = true
            }

            @Order(4000.0) inner class ReminderField : AbstractDateTimeField() {
                override fun getConfiguredLabel(): String = TEXTS.get("Reminder")
            }

            @Order(5000.0) inner class AcceptedAndDoneBox : AbstractSequenceBox() {

                override fun getConfiguredAutoCheckFromTo(): Boolean = false

                @Order(1000.0) inner class AcceptedField : AbstractBooleanField() {
                    override fun getConfiguredLabel(): String = TEXTS.get("Accepted")
                }

                @Order(2000.0) inner class DoneField : AbstractBooleanField() {
                    override fun getConfiguredLabel(): String = TEXTS.get("Done")
                }
            }

            @Order(6000.0) inner class DescriptionField : AbstractStringField() {
                override fun getConfiguredLabel(): String = TEXTS.get("Description")

                override fun getConfiguredMultilineText(): Boolean = true

                override fun getConfiguredMaxLength(): Int = 2000

                override fun getConfiguredGridH(): Int = 4

                override fun getConfiguredGridW(): Int = 2
            }
        }

        @Order(100000.0) inner class OkButton : AbstractOkButton()

        @Order(101000.0) inner class CancelButton : AbstractCancelButton()
    }

    inner class ModifyHandler : AbstractDirtyFormHandler() {

        override fun execLoad() {
            setEnabledPermission(UpdateTaskPermission())
            val task = taskService.get(taskId!!) ?: return
            importFormFieldData(task)
            form.subTitle = calculateSubTitle()
        }

        override fun execStore() {
            val task = taskService.get(taskId!!) ?: return
            store(task)
        }

        override fun execDirtyStatusChanged(dirty: Boolean) {
            form.subTitle = calculateSubTitle()
        }

        override fun getConfiguredOpenExclusive(): Boolean = true
    }

    inner class NewHandler : AbstractDirtyFormHandler() {

        override fun execLoad() {
            setEnabledPermission(CreateTaskPermission())
            setDefaultFieldValues()
        }

        override fun execStore() {
            store(Task())
        }

        override fun execDirtyStatusChanged(dirty: Boolean) {
            form.subTitle = calculateSubTitle()
        }
    }

    private fun store(task: Task) {
        exportFormFieldData(task)
        taskService.save(task)
    }

    private fun setDefaultFieldValues() {
        val userId = ClientSession.get()!!.userId

        assignedByField.value = userId
        responsibleField.value = userId
        acceptedField.value = true
        dueDateField.value = Date()
        assignedAtField.value = Date()
    }

    private fun importFormFieldData(task: Task) {
        titleField.value = task.name
        assignedByField.value = task.assignedBy
        responsibleField.value = task.responsible
        dueDateField.value = task.dueDate
        assignedAtField.value = task.assignedAt

        reminderField.value = task.reminder
        acceptedField.value = task.accepted
        doneField.value = task.done
        descriptionField.value = task.description
    }

    private fun exportFormFieldData(task: Task) {
        task.name = titleField.value
        task.assignedBy = assignedByField.value
        task.responsible = responsibleField.value
        task.dueDate = dueDateField.value
        task.assignedAt = assignedAtField.value

        task.reminder = reminderField.value
        task.accepted = acceptedField.value
        task.done = doneField.value
        task.description = descriptionField.value
    }

    private fun calculateSubTitle(): String = titleField.value
}
