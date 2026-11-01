package dev.battleplacks.team;

import java.text.Normalizer;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;

/**
 * A basic profanity filter for team names (English and Dutch). The word lists are stored ROT13-encoded so the source
 * is not full of slurs. It is a best effort, not a guarantee: it catches common spellings, spacing, repeated letters
 * and simple character swaps (f u c k, fuuuck, sh1t), and can wrongly reject an innocent word that contains a
 * blocked one.
 */
public final class ProfanityFilter {
    /** Blocked wherever they appear in the text. */
    private static final List<String> CONTAINS = collapseAll(decode("shpx", "fuvg", "ovgpu", "nffubyr", "phag", "juber", "fyhg", "onfgneq", "chffl", "qvpxurnq", "jnaxre", "gjng", "avttre", "avttn", "snttbg", "ergneq", "xnaxre", "grevat", "glshf", "xybbgmnx", "zbatbby", "syvxxre", "tbqireqbzzr", "uvgyre", "anmv", "cbea", "encr"));
    /** Short or ambiguous words, blocked only as a whole word. */
    private static final List<String> EXACT = decode("pbpx", "qvpx", "phz", "gvg", "gvgf", "nff", "snt", "xhg", "yhy", "cvx", "ubre", "arhx", "frk", "cvff");

    private ProfanityFilter() {}

    public static boolean isProfane(String text) {
        String leet = unLeet(text);
        for (String token : leet.split("[^a-z]+")) {
            String collapsed = collapse(token);
            if (EXACT.contains(token) || EXACT.contains(collapsed)) return true;
        }
        String squashed = collapse(leet.replaceAll("[^a-z]", ""));
        return CONTAINS.stream().anyMatch(squashed::contains);
    }

    /** Lower-cases, strips accents and maps look-alike characters back to letters. */
    private static String unLeet(String text) {
        String s = Normalizer.normalize(text, Normalizer.Form.NFD).replaceAll("\\p{M}", "").toLowerCase(Locale.ROOT);
        return s.replace('0', 'o').replace('1', 'i').replace('3', 'e').replace('4', 'a')
                .replace('5', 's').replace('7', 't').replace('@', 'a').replace('$', 's').replace('!', 'i');
    }

    /** "fuuuck" -> "fuck". */
    private static String collapse(String s) {
        return s.replaceAll("(.)\\1+", "$1");
    }

    /** The input is collapsed before matching, so the blocked words must be too ("asshole" -> "ashole"). */
    private static List<String> collapseAll(List<String> words) {
        return words.stream().map(ProfanityFilter::collapse).toList();
    }

    private static List<String> decode(String... rot13) {
        return Arrays.stream(rot13).map(ProfanityFilter::rot13).toList();
    }

    private static String rot13(String s) {
        StringBuilder out = new StringBuilder();
        for (char c : s.toCharArray()) {
            if (c >= 'a' && c <= 'z') out.append((char) ('a' + (c - 'a' + 13) % 26));
            else out.append(c);
        }
        return out.toString();
    }
}
