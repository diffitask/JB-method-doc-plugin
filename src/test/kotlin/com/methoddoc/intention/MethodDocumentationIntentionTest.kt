package com.methoddoc.intention

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Assert

class MethodDocumentationIntentionTest : BasePlatformTestCase() {
    override fun getTestDataPath() = "src/test/testData"
    fun testMethodWithoutAnyDoc() {
        val testName = "WithoutAnyDoc"
        val nextDocSuggestion = "/** Some generated documentation */"
        checkIntentionForKotlinAndJava(testName, nextDocSuggestion)
    }

    fun testComplicatedMethodIntention() {
        val testName = "ComplicatedMethod"
        val nextDocSuggestion = "/** Some generated documentation for the complicated method */"
        checkIntentionForKotlinAndJava(testName, nextDocSuggestion)
    }

    fun testMethodWithPreviousDoc() {
        val testName = "WithPreviousDoc"
        val nextDocSuggestion = "/** Some new documentation that replaced the old one */"
        checkIntentionForKotlinAndJava(testName, nextDocSuggestion)
    }

    fun testMethodWithDocAndComment() {
        val testName = "WithDocAndComment"
        val nextDocSuggestion = "/** Some new one documentation. The comment below remained untouched. */"
        checkIntentionForKotlinAndJava(testName, nextDocSuggestion)
    }

    fun testMethodWithManyCommentsWithSpaces() {
        val testName = "ManyCommentsWithSpaces"
        val nextDocSuggestion =
            "/** Some new one documentation. The comments below remained untouched with all their white spaces. */"
        checkIntentionForKotlinAndJava(testName, nextDocSuggestion)
    }

    fun testWrongCaretPosition() {
        checkWrongCaretPosForKotlinAndJava("WrongCaretPosition1")
        checkWrongCaretPosForKotlinAndJava("WrongCaretPosition2")
        checkWrongCaretPosForKotlinAndJava("WrongCaretPosition3")
    }

    private fun checkIntentionForKotlinAndJava(testName: String, nextDocSuggestion: String) {
        checkMethodDocumentationIntention(testName, "kotlin", "kt", nextDocSuggestion)
        checkMethodDocumentationIntention(testName, "java", "java", nextDocSuggestion)
    }

    private fun checkWrongCaretPosForKotlinAndJava(testName: String) {
        checkWrongCaretPosition(testName, "kotlin", "kt")
        checkWrongCaretPosition(testName, "java", "java")
    }

    private fun checkMethodDocumentationIntention(testName: String, codeLanguage: String, fileExtension: String, nextDocSuggestion: String) {
        val beforeTestFileName = "/$codeLanguage.code.examples/$testName.before.$fileExtension"
        val afterTestFileName = "/$codeLanguage.code.examples/$testName.after.$fileExtension"


        myFixture.configureByFile(beforeTestFileName).putUserData(mockedDocKey, nextDocSuggestion)
        val intentionHint = MyBundle.message("intentionHint")
        val intentionAction = myFixture.findSingleIntention(intentionHint)
        Assert.assertNotNull(intentionAction)

        myFixture.apply {
            launchAction(intentionAction)
            checkResultByFile(afterTestFileName)
        }
    }

    private fun checkWrongCaretPosition(testName: String, codeLanguage: String, fileExtension: String) {
        val beforeTestFileName = "/$codeLanguage.code.examples/$testName.before.$fileExtension"
        myFixture.configureByFile(beforeTestFileName)

        val intentionHint = MyBundle.message("intentionHint")
        Assert.assertEquals(myFixture.filterAvailableIntentions(intentionHint).size, 0)
    }
}
