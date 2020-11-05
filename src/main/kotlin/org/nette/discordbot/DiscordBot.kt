package org.nette.discordbot

import net.dv8tion.jda.api.JDABuilder
import org.nette.discordbot.blog.BlogChecker
import org.nette.discordbot.forum.ForumChecker
import org.nette.discordbot.forum.ForumListener
import org.nette.discordbot.releases.ReleaseChecker
import org.nette.discordbot.releases.ReleaseListener
import org.nette.discordbot.roles.RoleCommandListener
import org.nette.discordbot.stackoverflow.StackOverflowChecker
import java.util.*

fun main(args: Array<String>) {
    val jda = JDABuilder.createDefault(args[0])
        .addEventListeners(RoleCommandListener())
        .addEventListeners(ReleaseListener())
        .addEventListeners(ForumListener())
        .build()

    jda.awaitReady()

    StackOverflowChecker(jda)
    BlogChecker(jda)
    ReleaseChecker(jda)
    ForumChecker(jda)
}

fun String.consoleLog() {
    val now = Calendar.getInstance()
    val year = now[Calendar.YEAR]
    val month = now[Calendar.MONTH] + 1
    val day = now[Calendar.DAY_OF_MONTH]
    val hour = now[Calendar.HOUR_OF_DAY]
    val minute = now[Calendar.MINUTE]
    val second = now[Calendar.SECOND]
    val millis = now[Calendar.MILLISECOND]

    println(String.format("[%d-%02d-%02d %02d:%02d:%02d.%03d]", year, month, day, hour, minute, second, millis) + " $this")
}
