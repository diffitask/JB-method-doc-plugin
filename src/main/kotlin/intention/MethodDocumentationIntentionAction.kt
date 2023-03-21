package intention

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
import com.intellij.psi.javadoc.PsiDocComment
import kotlinx.coroutines.runBlocking
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPsiFactory

class MethodDocumentationIntentionAction : PsiElementBaseIntentionAction() {
    /**
     * If this action is applicable, returns the text to be shown in the list of intention actions available.
     */
    override fun getText(): String {
        return "Generate method documentation"
    }

    /**
     * Returns text for name of this family of intentions.
     *
     * @return the intention family name.
     */
    override fun getFamilyName(): String {
        return "Generate method documentation"
    }

    /**
     * Checks whether this intention is available at the caret offset in file - the caret must sit just on the function name. If this condition is met, this intention's entry is shown in the available
     * intentions list.
     *
     * @param project a reference to the Project object being edited.
     * @param editor  a reference to the object editing the project source
     * @param element a reference to the PSI element currently under the caret
     * @return {@code true} if the caret is in a literal string element, so this functionality should be added to the
     * intention menu or {@code false} for all other types of caret positions
     */
    override fun isAvailable(project: Project, editor: Editor?, element: PsiElement): Boolean {
        when (element.language.id) {
            "kotlin" -> {
                if (element.parent !is KtNamedFunction) {
                    return false
                }
                val elementFun = element.parent as KtNamedFunction
                if (elementFun.nameIdentifier !== element) {
                    return false
                }
            }

            "JAVA" -> {
                if (element.parent !is PsiMethod) {
                    return false
                }
                val elementFun = element.parent as PsiMethod
                if (elementFun.nameIdentifier !== element) {
                    return false
                }
            }
        }
        return true
    }

    private fun generateMethodDocumentation(methodCode: String): String {
        val openAIApiSecretKey = "" // TODO: to add secret key before running app
        val openAI = OpenAI(openAIApiSecretKey)

        val completionRequest = CompletionRequest(
            model = ModelId("text-davinci-003"),
            prompt = """
                Generate Javadoc or KDoc for method below. Please write only documentation in Java style:

                $methodCode

                /**
            """.trimIndent(),
            stop = listOf("*/"),
            temperature = 0.7,
            maxTokens = 256
        )
        val completion = runBlocking {
            openAI.completion(completionRequest)
        }
        val methodDoc = completion.choices[0].text
        return "/** $methodDoc */"
    }

    /**
     * Modifies the Psi to add a method description before method declaration.
     * Called when user selects this intention action from the available intentions list.
     *
     * @param project a reference to the Project object being edited.
     * @param editor  a reference to the object editing the project source
     * @param element a reference to the PSI element currently under the caret
     */
    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        // generate method documentation
        val methodDocStr = generateMethodDocumentation(element.parent.text)

        when (element.language.id) {
            "kotlin" -> {
                // create factory for new PsiElement generating
                val psiFactory = KtPsiFactory(project)
                // create KDoc
                val methodDocElement = psiFactory.createComment(methodDocStr) as KDoc

                val elementMethod = element.parent as KtNamedFunction

                //  if the method already had documentation (it can only be the 1st child) delete it
                //  if there were some comments before declaration, they will be saved (they may be needed by the user and theoretically do not relate to documentation
                if (elementMethod.firstChild is KDoc) {
                    elementMethod.firstChild.delete()
                }

                // add generated documentation to method as its first child
                elementMethod.addBefore(methodDocElement, elementMethod.firstChild)
            }

            "JAVA" -> {
                // create factory for new PsiElement generating
                val psiFactory = JavaPsiFacade.getInstance(project).elementFactory
                // create Javadoc
                val methodDocElement = psiFactory.createDocCommentFromText(methodDocStr)
                // our method
                val elementMethod = element.parent as PsiMethod

                //  check if the method already had documentation
                if (elementMethod.firstChild is PsiDocComment) {
                    elementMethod.firstChild.delete()
                }

                // add generated documentation to method as its first child
                elementMethod.addBefore(methodDocElement, elementMethod.firstChild)
            }
        }
    }
}