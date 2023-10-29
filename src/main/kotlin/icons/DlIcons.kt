package icons

import com.intellij.openapi.util.IconLoader
import javax.swing.Icon

object DlIcons {
    @JvmField
    val Swap: Icon = load("/icons/swap.svg")

    @JvmStatic
    fun load(path: String): Icon {
        return IconLoader.getIcon(path, DlIcons::class.java)
    }
}
