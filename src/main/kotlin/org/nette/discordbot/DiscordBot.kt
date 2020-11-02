package org.nette.discordbot

import net.dv8tion.jda.api.JDABuilder
import org.nette.discordbot.roles.RoleCommandListener
import org.nette.discordbot.stackoverflow.StackOverflowChecker

fun main(args: Array<String>) {
    val jda = JDABuilder.createDefault(args[0])
        .addEventListeners(RoleCommandListener())
        .build()

    jda.awaitReady()

    StackOverflowChecker(jda)
}
