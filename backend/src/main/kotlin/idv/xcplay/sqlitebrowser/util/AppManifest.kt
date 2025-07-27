package idv.xcplay.sqlitebrowser.util

import java.lang.invoke.MethodHandles
import java.net.JarURLConnection
import java.net.URL
import java.util.jar.Attributes

class AppManifest {
    /**
     * Get App Name of jar manifest
     * @return Implementation-Title/manifest
     */
    var baseName: String = "" // Implementation-Title/manifest

    /**
     * Get App Version of jar manifest
     * @return Implementation-Version/manifest
     */
    var version: String = "" // Implementation-Version/manifest

    companion object {
        val instance: AppManifest
            get() {
                val appManifest = AppManifest()

                val APPLET_CLS =
                    MethodHandles.lookup().lookupClass()

                // val className = APPLET_CLS.simpleName + ".class" // Companion.class
                val className = "AppManifest.class"
                // println("AppManifest.className: " + className.toString())
                val classPath = APPLET_CLS.getResource(className).toString()
                if (!classPath.startsWith("jar")) {
                    //System.out.println("no jar resource");
                    return appManifest
                }

                val attributes: Attributes
                try {
                    val url = URL(classPath)
                    val jarConnection = url.openConnection() as JarURLConnection
                    val manifest = jarConnection.manifest
                    attributes = manifest.mainAttributes
                    appManifest.version = attributes.getValue("Implementation-Version")
                    appManifest.baseName = attributes.getValue("Implementation-Title")
                } catch (e: Exception) {
                    println("get manifest prop NG, exception: " + e.message)
                    return appManifest
                }

                return appManifest
            }
    }
}
