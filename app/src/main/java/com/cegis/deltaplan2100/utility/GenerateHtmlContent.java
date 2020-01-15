package com.cegis.deltaplan2100.utility;

public class GenerateHtmlContent {
    public GenerateHtmlContent() {

    }

    public static String getHtmlText(String rawContent) {
        String content = "<html>" +
                "<head>" +
                "<link rel=\"stylesheet\" href=\"file:///android_asset/style.css\" />" +
                "</head>" +
                "<body>" + rawContent + "</body>" +
                "</html>";

        return content;
    }

    public static String getHtmlTable(String rawContent) {
        String content = "<html>" +
                "<head>" +
                "<link rel=\"stylesheet\" href=\"file:///android_asset/style.css\" />" +
                "</head>" +
                "<body>" +
                "<table>" + rawContent + "</table>" +
                "</body>" +
                "</html>";

        return content;
    }
}
