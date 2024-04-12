package de.obsidiancloud.common.logging;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.SimpleDateFormat;
import java.util.logging.LogRecord;

public class FileFormatter extends java.util.logging.Formatter {
    private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");

    @Override
    public String format(LogRecord record) {
        String message;
        if (record.getThrown() == null) {
            message = record.getMessage();
        } else {
            StringWriter writer = new StringWriter();
            record.getThrown().printStackTrace(new PrintWriter(writer));
            message = writer.toString();
            if (record.getMessage() != null && !record.getMessage().isBlank()) {
                message = record.getMessage() + "\n" + message;
            }
        }
        message = message.replace("\u001b\\[[0-9;]*m", "");
        return "["
                + dateFormat.format(record.getInstant().toEpochMilli())
                + "] ["
                + record.getLoggerName()
                + "/"
                + record.getLevel().getName()
                + "]: "
                + message
                + "\n";
    }
}
