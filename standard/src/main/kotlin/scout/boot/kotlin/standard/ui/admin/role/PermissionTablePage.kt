package scout.boot.kotlin.standard.ui.admin.role

import scout.boot.kotlin.standard.ui.admin.role.PermissionTablePage.PermissionTable
import scout.boot.kotlin.standard.ui.admin.role.ReadAdministrationPermissionPagePermission


import org.eclipse.scout.rt.client.ui.desktop.outline.pages.AbstractPageWithTable
import org.eclipse.scout.rt.platform.Bean
import org.eclipse.scout.rt.shared.TEXTS
import org.eclipse.scout.rt.shared.services.common.jdbc.SearchFilter

@Bean
class PermissionTablePage : AbstractPageWithTable<PermissionTable>() {

    override fun getConfiguredTitle(): String {
        return TEXTS.get("PermissionTablePage")
    }

    override fun execInitPage() {
        setVisiblePermission(ReadAdministrationPermissionPagePermission())
    }

    override fun getConfiguredLeaf(): Boolean {
        return true
    }

    inner class PermissionTable : AbstractPermissionTable() {

        override fun execInitTable() {
            assignedColumn.isDisplayable = false
        }

        override fun execReloadPage() {
            reloadPage()
        }
    }

    override fun execLoadData(filter: SearchFilter) {
        table.loadData(filter)
    }
}
