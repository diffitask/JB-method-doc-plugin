package com.methoddoc.intention

import com.intellij.testFramework.fixtures.BasePlatformTestCase
import org.junit.Assert

class MethodDocumentationIntentionTest : BasePlatformTestCase() {
    fun testRun() {
        testMethodDocumentationIntention("SimpleMethod", "/** Some documentation */")
    }

    override fun getTestDataPath() = "src/test/testData"

    private fun testMethodDocumentationIntention(testName: String, nextDocSuggestion: String) {
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
}
