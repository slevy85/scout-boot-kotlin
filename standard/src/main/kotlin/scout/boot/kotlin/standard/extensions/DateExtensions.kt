package scout.boot.kotlin.standard.extensions

import org.eclipse.scout.rt.platform.util.date.DateUtility
import java.util.*

inline fun Date?.isToday(): Boolean {
    return if (this == null) {
        false
    } else DateUtility.isSameDay(Date(), this)
}