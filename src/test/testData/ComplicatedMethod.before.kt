fun complicatedMethod<caret>WithArgsAndReturnValue(strArg: String, intArg: Int, doubleArg: Double): String {
    doubleArg += intArg
    val congrats = "$strArg is our new lottery winner! He won $doubleArg smiles!"
    return congrats
}