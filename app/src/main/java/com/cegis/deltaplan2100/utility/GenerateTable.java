package com.cegis.deltaplan2100.utility;

public class GenerateTable {
    public GenerateTable() {

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
