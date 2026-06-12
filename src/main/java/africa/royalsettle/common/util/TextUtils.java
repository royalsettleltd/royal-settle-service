package africa.royalsettle.common.util;

import java.time.LocalDateTime;
import java.util.UUID;

import static africa.royalsettle.common.constants.AppConstant.CODE_TIMESTAMP_FORMATTER;

public class TextUtils {

    private TextUtils() {}

    public static String generateCode() {
        return CODE_TIMESTAMP_FORMATTER.format(LocalDateTime.now())
                + UUID.randomUUID().toString().replace("-", "").substring(0, 8).toUpperCase();
    }
}
