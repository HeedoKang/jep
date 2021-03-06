package jep.test;

import java.util.List;
import java.util.Map;

import jep.Interpreter;
import jep.JepConfig;
import jep.SubInterpreter;

/**
 * A test class for verifying that jep.getValue(String) is working well with
 * regards to Python lists, tuples, and dictionaries.
 * 
 * Created: July 2015
 * 
 * @author Nate Jensen
 */
public class TestGetCollectionBoxing {

    public static void main(String[] args) {
        try (Interpreter interp = new SubInterpreter(
                new JepConfig().addIncludePaths("."))) {
            testList(interp);
            testTuple(interp);
            testDictionary(interp);
        } catch (Throwable t) {
            t.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }

    public static void testList(Interpreter interp) throws Exception {
        interp.eval("x = [0, 1, 2, 'three', 4, 5.0, 6]");
        List<?> x = (List<?>) interp.getValue("x");
        assert x.size() == 7;
        assert ((Number) x.get(0)).intValue() == 0;
        assert ((Number) x.get(1)).intValue() == 1;
        assert ((Number) x.get(2)).longValue() == 2L;
        assert x.get(3).equals("three");
        assert ((Number) x.get(4)).intValue() == 4;
        assert ((Number) x.get(5)).doubleValue() == 5.0;
        assert ((Number) x.get(6)).longValue() == 6L;
        interp.eval("del x");
    }

    @SuppressWarnings("unchecked")
    public static void testTuple(Interpreter interp) throws Exception {
        interp.eval("x = ('abc', 'def')");
        List<String> x = (List<String>) interp.getValue("x");
        assert x.size() == 2;
        assert x.get(0).equals("abc");
        assert x.get(1).equals("def");
        try {
            x.add("shouldn't be allowed");
            assert false;
        } catch (UnsupportedOperationException e) {
            // good to reach this
        }
        interp.eval("del x");
    }

    @SuppressWarnings("unchecked")
    public static void testDictionary(Interpreter interp) throws Exception {
        interp.eval("y = [1, 2, 3, 4]");
        interp.eval("x = {'a':'123', 'b':'cdef', 'c':y, 'd':None, None:'e'}");
        Map<String, Object> x = (Map<String, Object>) interp.getValue("x");
        assert x.size() == 5;
        assert x.get("a").equals("123");
        assert x.get("b").equals("cdef");
        List<Integer> y = (List<Integer>) x.get("c");
        assert y.size() == 4;
        assert ((Number) y.get(0)).intValue() == 1;
        assert ((Number) y.get(1)).longValue() == 2L;
        assert ((Number) y.get(2)).longValue() == 3L;
        assert ((Number) y.get(3)).longValue() == 4L;
        assert (x.get("d") == null);
        assert (x.get(null).equals("e"));
        interp.eval("del x");
        interp.eval("del y");
    }
}
