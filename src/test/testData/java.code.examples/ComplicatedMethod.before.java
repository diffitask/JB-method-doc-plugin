public class TestClass {
    public String complicatedMethod<caret>WithArgsAndReturnValue(String strArg, Int intArg, Double doubleArg) {
        doubleArg += intArg
        String congrats = strArg + doubleArg
        return congrats
    }
}