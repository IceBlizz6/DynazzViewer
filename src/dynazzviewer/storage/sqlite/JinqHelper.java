package dynazzviewer.storage.sqlite;

import dynazzviewer.entities.ExtReference;
import dynazzviewer.entities.IdContainer;
import dynazzviewer.entities.NameContainer;
import org.jinq.jpa.JPQL;
import org.jinq.jpa.JinqJPAStreamProvider;
import org.jinq.orm.stream.JinqStream;

import java.util.Collection;
import java.util.List;

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

    public static <T extends IdContainer> JinqStream.Where<T, Exception> fromId(Integer id) {
        return e -> e.getId() == id;
    }

    public static <T extends ExtReference> JinqStream.Where<T, Exception> matchAnyExtKey(Collection<String> keys) {
        return e -> JPQL.isInList(e.getUniqueExtKey(), keys);
    }

    public static <T extends NameContainer> JinqStream.Where<T, Exception> matchAnyName(Collection<String> names) {
        return e -> JPQL.isInList(e.getName(), names);
    }
}
