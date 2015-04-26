package com.yugy.v2ex.daily.model;

import java.text.SimpleDateFormat;
import java.util.TimeZone;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by yugy on 14-3-14.
 */
public class NotificationModel {

    public String title;
    public int topicId;
    public long time;
    public String author;
    public String content;

    private static final Pattern TITLE_PATTERN = Pattern.compile("<title>([^<]+)</title>");
    private static final Pattern TOPIC_ID_PATTERN = Pattern.compile("https://www\\.v2ex\\.com/t/([0-9]+)");
    private static final Pattern TIME_PATTERN = Pattern.compile("<published>([^<]+)</published>");
    private static final Pattern AUTHOR_PATTERN = Pattern.compile("<name>([^<]+)</name>");
    private static final Pattern CONTENT_PATTERN = Pattern.compile("<!\\[CDATA\\[\\n\\t\\t\\n\\t([^\\t]+)");

    //2014-03-10T14:14:14Z
    private static final SimpleDateFormat TIME_FORMATTER = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'"){{
        setTimeZone(TimeZone.getTimeZone("GMT"));
    }};


    public void parse(String entry) throws Exception {
        Matcher titleMatcher = TITLE_PATTERN.matcher(entry);
        if(titleMatcher.find()){
            title = titleMatcher.group(1);
        }else{
            throw new Exception("xml title no found");
        }

        Matcher topicIdMatcher = TOPIC_ID_PATTERN.matcher(entry);
        if(topicIdMatcher.find()){
            topicId = Integer.parseInt(topicIdMatcher.group(1));
        }else{
            throw new Exception("xml topicId no found");
        }

        Matcher timeMatcher = TIME_PATTERN.matcher(entry);
        if(timeMatcher.find()){
            time = TIME_FORMATTER.parse(timeMatcher.group(1)).getTime();
        }else{
            throw new Exception("xml time no found");
        }

        Matcher authorMatcher = AUTHOR_PATTERN.matcher(entry);
        if(authorMatcher.find()){
            author = authorMatcher.group(1);
        }else{
            throw new Exception("xml author no found");
        }

        Matcher contentMatcher = CONTENT_PATTERN.matcher(entry);
        if(contentMatcher.find()){
            content = contentMatcher.group(1);
        }else{
            throw new Exception("xml content no found");
        }
    }

}
