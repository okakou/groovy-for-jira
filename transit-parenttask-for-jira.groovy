import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.customfields.manager.OptionsManager
import com.atlassian.jira.util.JiraUtils
import com.atlassian.jira.workflow.WorkflowTransitionUtilImpl
import com.atlassian.jira.workflow.TransitionOptions

def constantManager = ComponentAccessor.getConstantsManager()
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def issueFactory = ComponentAccessor.getIssueFactory()
def subTaskManager = ComponentAccessor.getSubTaskManager()
def issueManager = ComponentAccessor.getIssueManager()

def doTransition(issue, int actionId, user) {
    def issueService = ComponentAccessor.getIssueService()
    def issueInputParameters = issueService.newIssueInputParameters();
    def transitionValidationResult = issueService.validateTransition(user, issue.id, actionId, issueInputParameters);
    if (transitionValidationResult.isValid()) {
        issueService.transition(user, transitionValidationResult);
        return true
    } else {
        return false
    }
}

if (issue.getIssueTypeObject().getName() != 'サブタスク')
    return

Issue parent = issue.getParentObject()

subtasks = parent.getSubTaskObjects()
done_flag = 1
subtasks.each {
    if (it.getStatus().getSimpleStatus().getName() != '完了')
        done_flag = 0
}

if (done_flag == 1) {
    doTransition(parent, 81, user)
}