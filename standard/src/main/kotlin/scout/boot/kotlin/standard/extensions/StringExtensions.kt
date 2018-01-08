package scout.boot.kotlin.standard.extensions

inline fun String.between(min: Int, max: Int) = this.length in min..max
