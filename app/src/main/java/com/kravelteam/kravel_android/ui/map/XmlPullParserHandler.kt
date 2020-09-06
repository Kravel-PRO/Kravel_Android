package com.kravelteam.kravel_android.ui.map

import android.os.AsyncTask
import com.kravelteam.kravel_android.data.mock.MapNearPlaceData
import org.xmlpull.v1.XmlPullParser
import org.xmlpull.v1.XmlPullParserException
import org.xmlpull.v1.XmlPullParserFactory
import timber.log.Timber
import java.io.IOException
import java.io.InputStream

 class XmlPullParserHandler {
    var checkImage = false
    var checkTitle = false
    var title : String? = null
    var image : String? = null
    private val datas = mutableListOf<MapNearPlaceData>()


     fun parse(inputStream : InputStream) : List<MapNearPlaceData> {
         try {
             val factory = XmlPullParserFactory.newInstance()
             val parser = factory.newPullParser()
             parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true)
             parser.setInput(inputStream, null)
             var eventType = parser.eventType

             var tag: String?

             while (eventType != XmlPullParser.END_DOCUMENT) {
                 tag = parser.name
                 when (eventType) {
                     XmlPullParser.START_TAG -> {
                         if (tag.equals("firstimage")) {
                             checkImage = true
                         }
                         if (tag.equals("title")) {
                             checkTitle = true
                         }

                     }
                     XmlPullParser.TEXT -> {
                         if (checkTitle) {
                             title = parser.text
                             checkTitle = false
                         }
                         if (checkImage) {
                             image = parser.text
                             checkImage = false
                         }
                     }
                     XmlPullParser.END_TAG -> {
                         if (parser.name.equals("item")) {
                             datas.add(MapNearPlaceData(image, title))
                         }
                     }
                 }
                 eventType = parser.next()
             }
         } catch (e: XmlPullParserException) {
             Timber.e("XmlPullParserEx")
             e.printStackTrace()
         } catch (e: IOException) {
             Timber.e("IO")

             e.printStackTrace()
         }


         return datas
     }


}