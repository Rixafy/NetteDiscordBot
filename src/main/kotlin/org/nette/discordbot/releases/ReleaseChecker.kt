package org.nette.discordbot.releases

import net.dv8tion.jda.api.JDA
import org.nette.discordbot.consoleLog
import org.xml.sax.InputSource
import java.io.StringReader
import java.net.URL
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory
import kotlin.concurrent.timerTask

class ReleaseChecker(private val jda: JDA) {
    private val url = "https://nette.org/en/releases/rss"
    private val cachedReleases = mutableListOf<String>()
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
                    cachedReleases.add(value.textContent)
                    "Cached release ${value.textContent}".consoleLog()
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

                    for (k in 0 until entry.childNodes.length) {
                        val value = entry.childNodes.item(k)
                        if (value.nodeName == "link") {
                            link = value.textContent
                        }
                    }

                    if (link == null || cachedReleases.contains(link)) {
                        continue

                    } else {
                        cachedReleases.add(link)
                        "New release found! $link".consoleLog()
                    }

                    for (k in 0 until entry.childNodes.length) {
                        val value = entry.childNodes.item(k)
                        if (value.nodeName == "title") {
                            title = value.textContent
                        }
                    }

                    if (title != null) {
                        val announcementsChannel = jda.getTextChannelById(772230018515992607)
                        announcementsChannel?.sendMessage(
                            "Package **$title** was just released! $link :partying_face: \n*All releases can be found at https://nette.org/en/releases*"
                        )?.queue()
                    }
                }
            }, 5000, 30000
        )
    }
}
