package org.nette.discordbot.stackoverflow

import net.dv8tion.jda.api.JDA
import org.xml.sax.InputSource
import java.io.StringReader
import java.net.URL
import java.util.*
import javax.xml.parsers.DocumentBuilderFactory


class StackOverflowChecker(private val jda: JDA) {
    private val cachedQuestions = mutableListOf<String>()
    private val dbFactory = DocumentBuilderFactory.newInstance()
    private val dBuilder = dbFactory.newDocumentBuilder()

    init {
        val urlText = URL("https://stackoverflow.com/feeds/tag?tagnames=nette&sort=newest").readText()
        val xmlInput = InputSource(StringReader(urlText))
        val doc = dBuilder.parse(xmlInput)

        for (i in 0 until doc.childNodes.length) {
            val feed = doc.childNodes.item(i)
            for (j in 0 until feed.childNodes.length) {
                val entry = feed.childNodes.item(j)
                for (k in 0 until entry.childNodes.length) {
                    val value = entry.childNodes.item(k)
                    if (value.nodeName == "id") {
                        cachedQuestions.add(value.textContent)
                    }
                }
            }
        }

        runChecker()
    }

    private fun runChecker() {
        val timer = Timer()
        val task = object : TimerTask() {
            override fun run() {
                val urlText = URL("https://stackoverflow.com/feeds/tag?tagnames=nette&sort=newest").readText()
                val xmlInput = InputSource(StringReader(urlText))
                val doc = dBuilder.parse(xmlInput)

                for (i in 0 until doc.childNodes.length) {
                    val feed = doc.childNodes.item(i)
                    for (j in 0 until feed.childNodes.length) {
                        val entry = feed.childNodes.item(j)
                        var url: String? = null

                        for (k in 0 until entry.childNodes.length) {
                            val value = entry.childNodes.item(k)
                            if (value.nodeName == "id") {
                                url = value.textContent
                            }
                        }

                        if (url != null) {
                            if (cachedQuestions.contains(url)) {
                                continue
                            } else {
                                cachedQuestions.add(url)
                            }

                            var author: String? = null
                            var title: String? = null

                            for (k in 0 until entry.childNodes.length) {
                                val value = entry.childNodes.item(k)
                                if (value.nodeName == "author") {
                                    author = value.textContent.split("\n")[1].trim()
                                } else if (value.nodeName == "title") {
                                    title = value.textContent
                                }
                            }

                            val stackOverflowChannel = jda.getTextChannelById(772312208863395841)

                            stackOverflowChannel?.sendMessage(
                                "**$author** just asked the question **$title** $url"
                            )?.queue()
                        }
                    }
                }
            }
        }
        timer.schedule(task, 0, 10000)
    }
}