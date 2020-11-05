package org.nette.discordbot.forum

import net.dv8tion.jda.api.JDA
import org.xml.sax.InputSource
import java.io.StringReader
import java.net.URL
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.concurrent.timerTask

class ForumChecker(private val jda: JDA) {
    private val url = "https://nette.rixafy.pro/feed.php"
    private val cachedThreads = mutableListOf<String>()
    private val dbFactory = DocumentBuilderFactory.newInstance()
    private val dBuilder = dbFactory.newDocumentBuilder()

    init {
        val urlText = URL(url).readText()
        val xmlInput = InputSource(StringReader(urlText))
        val doc = dBuilder.parse(xmlInput)
        val items = doc.getElementsByTagName("item")

        for (j in 0 until items.length) {
            val entry = items.item(j)
            for (k in 0 until entry.childNodes.length) {
                val value = entry.childNodes.item(k)
                if (value.nodeName == "link") {
                    cachedThreads.add(value.textContent)
                    println("Cached thread ${value.textContent}")
                }
            }
        }

        runChecker()
    }

    private fun runChecker() {
        Timer().schedule(
            timerTask {
                val urlText = URL(url).readText()
                val xmlInput = InputSource(StringReader(urlText))
                val doc = dBuilder.parse(xmlInput)
                val items = doc.getElementsByTagName("item")

                for (j in 0 until items.length) {
                    val entry = items.item(j)
                    var link: String? = null
                    var title: String? = null
                    var author: String? = null

                    for (k in 0 until entry.childNodes.length) {
                        val value = entry.childNodes.item(k)
                        if (value.nodeName == "link") {
                            link = value.textContent
                        }
                    }

                    if (link == null || cachedThreads.contains(link)) {
                        continue

                    } else {
                        cachedThreads.add(link)
                        println("Cached thread $link")
                    }

                    for (k in 0 until entry.childNodes.length) {
                        val value = entry.childNodes.item(k)
                        if (value.nodeName == "title") {
                            title = value.textContent
                        } else if (value.nodeName == "author") {
                            author = value.textContent
                        }
                    }

                    if (title != null && author !== null) {
                        val forumChannel = jda.getTextChannelById(773007600832872448)
                        forumChannel?.sendMessage(
                            "**$author** just created the thread **$title** $link"
                        )?.queue()
                    }
                }
            }, 20000, 60000
        )
    }
}
