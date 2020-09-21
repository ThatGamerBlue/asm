package org.spectral.asm.core

import org.spectral.asm.core.util.JarUtil
import java.applet.Applet
import java.applet.AppletContext
import java.applet.AppletStub
import java.awt.Color
import java.awt.Dimension
import java.awt.GridLayout
import java.io.File
import java.net.URL
import java.net.URLClassLoader
import javax.swing.JFrame
import javax.swing.WindowConstants

object Test {

    @JvmStatic
    fun main(args: Array<String>) {
        val pool = JarUtil.readJar("gamepack-deob.jar")
        println()

        JarUtil.writeJar(pool, "gamepack-output.jar")

        val jarFile = File("gamepack-output.jar")
        val frame = JFrame()

        frame.title = "Test Client"
        frame.defaultCloseOperation = WindowConstants.EXIT_ON_CLOSE
        frame.layout = GridLayout(1, 0)
        frame.add(createApplet(jarFile))
        frame.pack()
        frame.setLocationRelativeTo(null)
        frame.isVisible = true
    }

    fun createApplet(file: File): Applet {
        val params = crawl(URL("http://oldschool1.runescape.com/jav_config.ws"))
        val classloader = URLClassLoader(arrayOf(file.toURI().toURL()))
        val main = params["initial_class"]!!.replace(".class", "")
        val applet = classloader.loadClass(main).newInstance() as Applet
        applet.background = Color.BLACK
        applet.preferredSize = Dimension(params["applet_minwidth"]!!.toInt(), params["applet_minheight"]!!.toInt())
        applet.size = applet.preferredSize
        applet.layout = null
        applet.setStub(applet.createStub(params))
        applet.isVisible = true
        applet.init()
        return applet
    }

    fun crawl(url: URL): Map<String, String> {
        val params = hashMapOf<String, String>()
        val lines = url.readText().split("\n")
        lines.forEach {
            var line = it

            if(line.startsWith("param=")) {
                line = line.substring(6)
            }

            val index = line.indexOf("=")
            if(index >= 0) {
                params[line.substring(0, index)] = line.substring(index + 1)
            }
        }

        return params
    }

    private fun Applet.createStub(params: Map<String, String>): AppletStub = object : AppletStub {
        override fun getCodeBase(): URL = URL(params["codebase"])
        override fun getDocumentBase(): URL = URL(params["codebase"])
        override fun isActive(): Boolean = true
        override fun getParameter(name: String): String? = params[name]
        override fun appletResize(width: Int, height: Int) { this@createStub.size = Dimension(width, height) }
        override fun getAppletContext(): AppletContext? = null
    }
}