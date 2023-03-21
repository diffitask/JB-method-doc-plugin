package intention

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.*
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

    private fun generateMethodDocumentation(methodName: String): String {
        return "/** Some documentation */" // TODO: to add OpenAI
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
        val methodDocStr = generateMethodDocumentation(element.text)

        when (element.language.id) {
            "kotlin" -> {
                // create factory for new PsiElement generating
                val psiFactory = KtPsiFactory(project)
                // create KDoc
                val methodDocElement = psiFactory.createComment(methodDocStr) as KDoc
                // add generated documentation to method as its first child
                val elementFun = element.parent as KtNamedFunction
                elementFun.addBefore(methodDocElement, elementFun.firstChild)
            }

            "JAVA" -> {
                // create factory for new PsiElement generating
                val psiFactory = JavaPsiFacade.getInstance(project).elementFactory
                // create Javadoc
                val methodDocElement = psiFactory.createDocCommentFromText(methodDocStr)
                // add generated documentation to method as its first child
                val elementFun = element.parent as PsiMethod
                elementFun.addBefore(methodDocElement, elementFun.firstChild)
            }
        }

        // TODO: to process the case when method already had some documentation
    }
}