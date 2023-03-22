package com.methoddoc.intention

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Assert

class MethodDocumentationIntentionTest : BasePlatformTestCase() {
    fun testMethodWithoutAnyDoc() {
        checkMethodDocumentationIntention("WithoutAnyDoc", "/** Some generated documentation */")
    }

    fun testComplicatedMethodIntention() {
        checkMethodDocumentationIntention("ComplicatedMethod", "/** Some generated documentation for the complicated method */")
    }

    fun testMethodWithPreviousDoc() {
        checkMethodDocumentationIntention("WithPreviousDoc", "/** Some new documentation that replaced the old one */")
    }

    fun testMethodWithDocAndComment() {
        checkMethodDocumentationIntention("WithDocAndComment", "/** Some new one documentation. The comment below remained untouched. */")
    }

    fun testMethodWithManyCommentsWithSpaces() {
        checkMethodDocumentationIntention("ManyCommentsWithSpaces", "/** Some new one documentation. The comments below remained untouched with all their white spaces.*/")
    }

    fun testWrongCaretPosition() {
        checkWrongCaretPosition("WrongCaretPosition1")
        checkWrongCaretPosition("WrongCaretPosition2")
        checkWrongCaretPosition("WrongCaretPosition3")
    }

    override fun getTestDataPath() = "src/test/testData"

    private fun checkMethodDocumentationIntention(testName: String, nextDocSuggestion: String) {
        val beforeTestFileName = "$testName.before.kt"
        val afterTestFileName = "$testName.after.kt"

        myFixture.configureByFile(beforeTestFileName).putUserData(mockedDocKey, nextDocSuggestion)
        val intentionHint = MyBundle.message("intentionHint")
        val intentionAction = myFixture.findSingleIntention(intentionHint)
        Assert.assertNotNull(intentionAction)

        myFixture.apply {
            launchAction(intentionAction)
            checkResultByFile(afterTestFileName)
        }
    }

    private fun checkWrongCaretPosition(testName: String) {
        val beforeTestFileName = "$testName.before.kt"
        myFixture.configureByFile(beforeTestFileName)

        val intentionHint = MyBundle.message("intentionHint")
        Assert.assertEquals(myFixture.filterAvailableIntentions(intentionHint).size, 0)
    }
}
