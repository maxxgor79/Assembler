package ru.retro.assembler.editor.core.util;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.NonNull;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.text.StringSubstitutor;
import ru.retro.assembler.editor.core.types.LineEnding;

/**
 * @Author: Maxim Gorin
 * Date: 13.03.2024
 */
public final class TextUtils {
    private TextUtils() {

    }

    public static ReplaceResult replace(@NonNull String text, @NonNull final String oldText
            , @NonNull final String newNext, final boolean all) {
        final ReplaceResult result = new ReplaceResult();
        if (all) {
            result.numReplaces = StringUtils.countMatches(text, oldText);
            result.text = StringUtils.replace(text, oldText, newNext, -1);
        } else {
            result.text = StringUtils.replace(text, oldText, newNext, 1);
            result.numReplaces = text.hashCode() != result.text.hashCode() ? 1 : 0;
        }
        return result;
    }

    public static String replaceUnixLineEnding(@NonNull String text, @NonNull LineEnding lineEnding) {
        return StringUtils.replace(text, LineEnding.LF.getValue(), lineEnding.getValue());
    }

    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReplaceResult {
        @Getter
        private String text;

        @Getter
        private int numReplaces;
    }
}
