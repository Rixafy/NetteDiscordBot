package org.nette.discordbot.blog

import net.dv8tion.jda.api.JDA
import org.nette.discordbot.consoleLog
import org.xml.sax.InputSource
import java.io.StringReader
import java.net.URL
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.concurrent.timerTask

class BlogChecker(private val jda: JDA) {
    private val url = "https://blog.nette.org/en/feed/rss"
    private val cachedArticles = mutableListOf<String>()
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
                    cachedArticles.add(value.textContent)
                    "Cached article ${value.textContent}".consoleLog()
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

                    for (k in 0 until entry.childNodes.length) {
                        val value = entry.childNodes.item(k)
                        if (value.nodeName == "link") {
                            link = value.textContent
                        }
                    }

                    if (link == null || cachedArticles.contains(link)) {
                        continue

                    } else {
                        cachedArticles.add(link)
                        "New article found! $link".consoleLog()
                    }

                    val announcementsChannel = jda.getTextChannelById(772230018515992607)
                    announcementsChannel?.sendMessage(
                        "New article was just published! $link @everyone"
                    )?.queue()
                }
            }, 10000, 30000
        )
    }
}
