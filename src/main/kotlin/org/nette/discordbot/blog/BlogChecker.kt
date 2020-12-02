package org.nette.discordbot.blog

import net.dv8tion.jda.api.JDA
import org.nette.discordbot.consoleLog
import org.xml.sax.InputSource
import java.io.StringReader
import java.net.URL
import java.util.concurrent.Executors
import java.util.concurrent.TimeUnit
import javax.xml.parsers.DocumentBuilderFactory


class BlogChecker(private val jda: JDA) {
    private val scheduler = Executors.newScheduledThreadPool(1)
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
                if (value.nodeName == "guid") {
                    cachedArticles.add(value.textContent)
                    "Cached article ${value.textContent}".consoleLog()
                }
            }
        }

        runChecker()
    }

    private fun runChecker() {
        scheduler.scheduleAtFixedRate({
            val urlText = URL(url).readText()
            val xmlInput = InputSource(StringReader(urlText))
            val doc = dBuilder.parse(xmlInput)
            val items = doc.getElementsByTagName("item")

            for (j in 0 until items.length) {
                val entry = items.item(j)
                var link: String? = null
                var guid: String? = null

                for (k in 0 until entry.childNodes.length) {
                    val value = entry.childNodes.item(k)
                    if (value.nodeName == "link") {
                        link = value.textContent
                    } else if (value.nodeName == "guid") {
                        guid = value.textContent
                    }
                }

                if (guid == null || cachedArticles.contains(guid)) {
                    continue

                } else {
                    cachedArticles.add(guid)
                    "New article found! $guid".consoleLog()
                }

                val announcementsChannel = jda.getTextChannelById(772230018515992607)
                announcementsChannel?.sendMessage(
                    "New article was just published! $link @everyone"
                )?.queue()
            }
        }, 5, 60, TimeUnit.SECONDS)
    }
}
