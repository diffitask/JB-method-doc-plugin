package intention

import com.intellij.codeInsight.intention.PsiElementBaseIntentionAction
import com.intellij.openapi.editor.Editor
import com.intellij.openapi.project.Project
import com.intellij.psi.PsiElement
import com.intellij.psi.*
import org.jetbrains.kotlin.psi.KtNamedFunction

class MethodDocumentationIntentionAction : PsiElementBaseIntentionAction() {
    /**
     * Returns text for name of this family of intentions.
     */
    override fun getFamilyName(): String {
        return "MethodDocumentationIntention"
    }

    /**
     * Checks whether this intention is available at the caret offset in file - the caret must sit just before on a function name. If this condition is met, this intention's entry is shown in the available
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

    override fun invoke(project: Project, editor: Editor?, element: PsiElement) {
        TODO("Not yet implemented")
    }

}