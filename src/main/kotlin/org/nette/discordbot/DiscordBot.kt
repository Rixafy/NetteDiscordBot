package org.nette.discordbot

import net.dv8tion.jda.api.JDABuilder
import org.nette.discordbot.blog.BlogChecker
import org.nette.discordbot.forum.ForumChecker
import org.nette.discordbot.forum.ForumListener
import org.nette.discordbot.releases.ReleaseChecker
import org.nette.discordbot.releases.ReleaseListener
import org.nette.discordbot.roles.RoleCommandListener
import org.nette.discordbot.stackoverflow.StackOverflowChecker

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
