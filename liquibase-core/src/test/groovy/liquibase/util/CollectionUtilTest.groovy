package liquibase.util

import org.hamcrest.MatcherAssert
import org.hamcrest.Matchers
import spock.lang.Specification
import spock.lang.Unroll
import spock.util.matcher.HamcrestMatchers

import static org.assertj.core.api.Assertions.assertThat;

class CollectionUtilTest extends Specification {

    @Unroll("powerSet on #original")
    def "powerSet method"() {

        def comparator = new Comparator<Collection>() {

            @Override
            int compare(Collection o1, Collection o2) {
                List set1 = new ArrayList(o1);
                List set2 = new ArrayList(o2);
                Collections.sort set1
                Collections.sort set2

                return set1.toString().compareTo(set2.toString())
            }
        }

        expect:
        def powerSet = CollectionUtil.powerSet(new HashSet(original))

        for (Set set : powerSet) {
            assertThat(set).usingComparator(comparator).isIn(expected)
        }

        for (set in expected) {
            assertThat(set).usingComparator(comparator).isIn(powerSet)
        }

        where:
        original        | expected
        []              | [[]]
        ['a']           | [[], ['a']]
        ['a', 'b']      | [[], ['a'], ['b'], ['a', 'b']]
        ['a', 'b', 'c'] | [[], ['a'], ['b'], ['c'], ['a', 'b'], ['a', 'c'], ['b', 'c'], ['a', 'b', 'c']]

    }

    @Unroll
    def "permuations for multimap"() {
        when:
        def permutations = CollectionUtil.permutations(original)

        then:
        permutations Matchers.containsInAnyOrder(expected.toArray())

        where:
        original                                   | expected
        [:]                                        | []
        ["a": [1, 2, 3]]                           | [["a": 1], ["a": 2], ["a": 3]]
        ["a": [1, 2, 3], "b": [5, 6]]              | [["a": 1, "b": 5], ["a": 2, "b": 5], ["a": 3, "b": 5],
                                                      ["a": 1, "b": 6], ["a": 2, "b": 6], ["a": 3, "b": 6]]
        ["a": [1, 2, 3], "b": [5, 6], "c": [8, 9]] | [["a": 1, "b": 5, "c": 8], ["a": 2, "b": 5, "c": 8], ["a": 3, "b": 5, "c": 8],
                                                      ["a": 1, "b": 6, "c": 8], ["a": 2, "b": 6, "c": 8], ["a": 3, "b": 6, "c": 8],
                                                      ["a": 1, "b": 5, "c": 9], ["a": 2, "b": 5, "c": 9], ["a": 3, "b": 5, "c": 9],
                                                      ["a": 1, "b": 6, "c": 9], ["a": 2, "b": 6, "c": 9], ["a": 3, "b": 6, "c": 9]]
    }

    @Unroll
    def "permuations for multi-list"() {
        when:
        def permutations = CollectionUtil.permutations(original)

        then:
        permutations Matchers.containsInAnyOrder(expected.toArray())

        where:
        original                    | expected
        []                          | []
        [[1, 2, 3]]                 | [[1], [2], [3]]
        [[1, 2, 3], [5, 6]]         | [[1, 5], [2, 5], [3, 5],
                                       [1, 6], [2, 6], [3, 6]]
        [[1, 2, 3], [5, 6], [8, 9]] | [[1, 5, 8], [2, 5, 8], [3, 5, 8],
                                       [1, 6, 8], [2, 6, 8], [3, 6, 8],
                                       [1, 5, 9], [2, 5, 9], [3, 5, 9],
                                       [1, 6, 9], [2, 6, 9], [3, 6, 9]]
    }

    def "createIfNull using list"() {
        when:
        List<String> emptyStrings = new ArrayList<>()
        List<String> strings = ["a", "b", "c"]

        then:
        CollectionUtil.createIfNull(emptyStrings).is(emptyStrings)
        CollectionUtil.createIfNull(strings) == ["a", "b", "c"]
        CollectionUtil.createIfNull(strings).is(strings)

//        CollectionUtil.createIfNull((List) null) == []  //overlapping signature
//        CollectionUtil.createIfNull(null as List, "x") == ["x"] //overlapping signature
        CollectionUtil.createIfNull(emptyStrings, "y") == ["y"]
        CollectionUtil.createIfNull(strings, "z") == ["a", "b", "c", "z"]

    }

    def "createIfNull using array"() {
        when:
        String[] emptyStrings = new String[0]
        String[] strings = ["a", "b", "c"]

        then:
        CollectionUtil.createIfNull(emptyStrings).is(emptyStrings)
        CollectionUtil.createIfNull(strings).toList() == ["a", "b", "c"]
        CollectionUtil.createIfNull(strings).is(strings)

//        CollectionUtil.createIfNull((String[]) null) == [] as String[] //overlapping signature
//        CollectionUtil.createIfNull((String[]) null, "x") == ["x"] as String[]  //overlapping signature
        CollectionUtil.createIfNull(emptyStrings).toList() == []
        CollectionUtil.createIfNull(strings).toList() == ["a", "b", "c"]

    }
}