package dynazzviewer.services.descriptors.tvmaze;

import java.util.List;

public class CollectionParser {
    public static Class<SearchShowResult[]> getArrayType() {
        return SearchShowResult[].class;
    }

    public static Class<Episode[]> getEpisodeArrayType() {
        return Episode[].class;
    }
}
