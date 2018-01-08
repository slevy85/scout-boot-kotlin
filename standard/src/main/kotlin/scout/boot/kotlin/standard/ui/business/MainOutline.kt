package scout.boot.kotlin.standard.ui.business

import scout.boot.kotlin.standard.ui.business.task.AllTasksTablePage
import scout.boot.kotlin.standard.ui.business.task.InboxTablePage
import scout.boot.kotlin.standard.ui.business.task.MyTaskTablePage
import scout.boot.kotlin.standard.ui.business.task.TodaysTaskTablePage

import org.eclipse.scout.boot.ui.commons.fonts.FontAwesomeIcons
import org.eclipse.scout.rt.client.ui.desktop.outline.AbstractOutline
import org.eclipse.scout.rt.client.ui.desktop.outline.pages.IPage
import org.eclipse.scout.rt.platform.BEANS
import org.eclipse.scout.rt.platform.Bean
import org.eclipse.scout.rt.shared.TEXTS

@Bean
class MainOutline : AbstractOutline() {

    override fun getConfiguredTitle(): String {
        return TEXTS.get("Tasks")
    }

    override fun getConfiguredIconId(): String {
        return FontAwesomeIcons.fa_calendarCheckO
    }

    override fun execCreateChildPages(pageList: MutableList<IPage<*>>?) {
        pageList!!.add(BEANS.get(InboxTablePage::class.java))
        pageList.add(BEANS.get(TodaysTaskTablePage::class.java))
        pageList.add(BEANS.get(MyTaskTablePage::class.java))
        pageList.add(BEANS.get(AllTasksTablePage::class.java))
    }
}
