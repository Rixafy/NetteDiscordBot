package org.nette.discordbot.releases

import net.dv8tion.jda.api.events.message.guild.GuildMessageReceivedEvent
import net.dv8tion.jda.api.hooks.ListenerAdapter

class ReleaseListener : ListenerAdapter() {
    override fun onGuildMessageReceived(event: GuildMessageReceivedEvent) {
        if (event.author.isBot && event.channel.name == "announcements") {
            if (event.message.contentRaw.contains("https://nette.org/en/releases")) {
                Thread.sleep(1000)
                event.message.suppressEmbeds(true).queue()
            }
        }
    }
}
