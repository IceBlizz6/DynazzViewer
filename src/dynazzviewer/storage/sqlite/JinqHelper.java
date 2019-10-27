package dynazzviewer.storage.sqlite;

import dynazzviewer.base.ViewStatus;
import dynazzviewer.entities.*;
import org.jinq.jpa.JPQL;
import org.jinq.orm.stream.JinqStream;
import org.jinq.tuples.Tuple3;

import java.util.Collection;
import java.util.List;
import java.util.Set;

/**
 * Jinq references in Java while waiting for Kotlin support
 * https://github.com/my2iu/Jinq/issues/81
 */
public class JinqHelper {
    public static <T extends NameContainer> JinqStream.Where<T, Exception> equalsName(String partialName) {
        return e -> e.getName().equals(partialName);
    }

    public static <T extends NameContainer> JinqStream.Where<T, Exception> containsName(String partialName) {
        String targetPartialName = "%" + partialName + "%";
        return e -> JPQL.like(e.getName(), targetPartialName);
    }

    public static <T extends NameContainer> JinqStream.Where<T, Exception> likeName(String likePattern) {
        return e -> JPQL.like(e.getName(), likePattern);
    }

    public static <T extends IdContainer> JinqStream.Where<T, Exception> fromId(Integer id) {
        return e -> e.getId() == id;
    }

    public static <T extends ExtReference> JinqStream.Where<T, Exception> matchAnyExtKey(Collection<String> keys) {
        return e -> JPQL.isInList(e.getUniqueExtKey(), keys);
    }

    public static <T extends NameContainer> JinqStream.Where<T, Exception> matchAnyName(Collection<String> names) {
        return e -> JPQL.isInList(e.getName(), names);
    }

    public static List<Tuple3<String, Integer, ViewStatus>> mediaFiles(JinqStream<MediaFile> source, Set<String> names) {
        return source
                .where(e -> JPQL.isInList(e.getName(), names))
                .select(e -> new Tuple3<String, Integer, ViewStatus>(e.getName(), e.getId(), e.getStatus()))
                .toList();
    }
}
