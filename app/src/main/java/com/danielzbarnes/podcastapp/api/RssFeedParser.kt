package com.danielzbarnes.podcastapp.api

import android.util.Log
import com.danielzbarnes.podcastapp.database.Podcast
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

private const val TAG = "PodcastRssParser"



class RssFeedParser {

    // this is for parsing multiple authors and guest authors
    private val listOfAuthors: List<String> = listOf("A.person", "B.person", "etc")
    private var isGuest: Boolean = false

    fun parseRss(input: InputStream): List<Podcast>{

        val podcastList = ArrayList<Podcast>()

        try {
            var podcast = Podcast()
            var tagname: String?
            var text = ""
            var depth: Int

            val factory: XmlPullParserFactory = XmlPullParserFactory.newInstance()
            factory.isNamespaceAware = true

            val parser: XmlPullParser = factory.newPullParser()

            parser.apply {
                setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false)
                setInput(input, null)
            }

            var event = parser.eventType

            while (event != XmlPullParser.END_DOCUMENT){

                tagname = parser.name
                depth = parser.depth

                when (event){
                    XmlPullParser.START_TAG -> if (tagname == "item") podcast = Podcast()
                    XmlPullParser.TEXT -> text = parser.text
                    XmlPullParser.END_TAG -> when (tagname) {

                        "title" -> if (depth == 4) podcast.title = text

                        "itunes:author" -> if (depth == 4) podcast.author = parseAuthor(text)

                        "enclosure" -> podcast.url = parser.getAttributeValue(null, "url")

                        "pubDate" -> podcast.date = parsePubDate(text)

                        "guid" -> podcast.id = parseGuid(text)

                        "itunes:duration" -> podcast.duration = text

                        "itunes:summary" -> if (depth == 4) {
                            podcast.series = parseSummary(text, podcast.title)
                            podcast.reference = text
                        }
                        "item" -> podcastList.add(podcast)
                    }
                }
                event = parser.next()
            }
        } catch (e:Exception) { e.printStackTrace() }
        catch (e: XmlPullParserException) { e.printStackTrace() }

        return podcastList
    }

    // return the author for the podcast
    private fun parseAuthor(text: String): String{

        // if this is a regular author for the podcast, return the author
        for (author in listOfAuthors)
            if (text.contains(author, true)){
                isGuest  = false
                return author
            }

        // else count it as a guest speaker episode and return the name
        isGuest = true
        return text
    }

    // returns the date in the format of Jan, 01, 2021
    private fun parsePubDate(date: String): String{

        // the format used by the RSS feed
        val rssDateFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss zzz", Locale.ENGLISH)
        val dateObj = rssDateFormat.parse(date) // a date object to handle reformatting
        val uiFormat = SimpleDateFormat("MMM dd, yyyy", Locale.ENGLISH) // date format friendly to the ui

        return uiFormat.format(dateObj!!)
    }

    // returns the url as a string
    private fun parseGuid(url: String): String {

        // the '=' assigns the podcast url to the guid
        val equalsIndex = url.indexOf("=", 0, false)
        // everything from the '=' to the end of the guid is the podcast URL
        return if ( equalsIndex != -1) url.slice(IntRange(equalsIndex+1, url.length-1)) else ""
    }

    // returns the series by parsing the summary text block of the RSS feed
    private fun parseSummary(text: String, title: String): String{

        val series: String

        when {
            text.contains("music", true) -> series = "Musical Concert"

            // if the guest speaker flag is true, then set the series as guest speaker
            isGuest -> series = "Guest Speakers"

            // if the title contains " Part "(with specific whitespaces) then its a part of series(probably)
            title.contains(" Part ", true) -> {

                // get the index of the " Part "
                val partIndex = title.indexOf(" Part ", 0, true)

                // slice off everything after the end of the title
                var titleSplit = title.slice(IntRange(0, partIndex)).trim()

                // removes any trailing ',' '-' characters from the string
                if (titleSplit.endsWith(",") || titleSplit.endsWith("-"))
                    titleSplit = titleSplit.dropLast(1)

                series = titleSplit.trim()
            }
            else -> series = text
        }
        return series
    }
}