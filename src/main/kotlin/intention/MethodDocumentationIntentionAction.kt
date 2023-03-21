package intention

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.*
import com.intellij.psi.codeStyle.CodeStyleManager
import org.jetbrains.kotlin.kdoc.psi.api.KDoc
import org.jetbrains.kotlin.psi.KtNamedFunction
import org.jetbrains.kotlin.psi.KtPsiFactory
import org.jetbrains.kotlin.utils.addToStdlib.firstIsInstance

class MethodDocumentationIntentionAction : PsiElementBaseIntentionAction() {
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
            "JAVA" -> {
                if (element.parent !is PsiMethod) {
                    return false
                }
                val elementFun = element.parent as PsiMethod
                if (elementFun.nameIdentifier !== element) {
                    return false
                }
            }

            "KOTLIN" -> {
                if (element.parent !is KtNamedFunction) {
                    return false
                }
                val elementFun = element.parent as KtNamedFunction
                if (elementFun.nameIdentifier !== element) {
                    return false
                }
            }
        }
        return true
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
        // create factory for new PsiElement generating, and code style manager to format new statement
        val psiFactory = KtPsiFactory(project)
        val codeStyleManager = CodeStyleManager.getInstance(project)

        // create new KDoc PSI element
        val methodDocStr = "/** Some description */" // TODO: to add OpenAI
        val methodDocFile = psiFactory.createFile("documentationFile.kt", methodDocStr)
        var methodDocElement = methodDocFile.children.firstIsInstance<KDoc>()

        // apply style managing
        methodDocElement = codeStyleManager.reformat(methodDocElement) as KDoc

        // add generated documentation to method as its first child
        val elementFun = element.parent as KtNamedFunction
        elementFun.addBefore(methodDocElement, elementFun.firstChild)

        // TODO: to process the case when method already had some documentation
    }
    override fun getText(): String {
        return "Generate method documentation"
    }
}