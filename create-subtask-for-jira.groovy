import com.atlassian.jira.component.ComponentAccessor
import com.atlassian.jira.issue.Issue
import com.atlassian.jira.issue.MutableIssue
import com.atlassian.jira.issue.customfields.manager.OptionsManager

def constantManager = ComponentAccessor.getConstantsManager()
def user = ComponentAccessor.getJiraAuthenticationContext().getLoggedInUser()
def issueFactory = ComponentAccessor.getIssueFactory()
def subTaskManager = ComponentAccessor.getSubTaskManager()
def issueManager = ComponentAccessor.getIssueManager()

Issue parentIssue = issue

if (parentIssue.getIssueTypeObject().getName() == 'Sub-Task' || parentIssue.getIssueTypeObject().getName() == 'サブタスク')
    return

//if (parentIssue.getIssueTypeObject().name != 'Feature')
//    return

def summariesList = ["Summary 1", "Summary 2", "Summary 3"]

// code in order to create the values to update a CascadingSelect custom field
//def cascadingSelect = ComponentAccessor.customFieldManager.getCustomFieldObjectByName("CascadingSelect")
//def fieldConfig = cascadingSelect?.getRelevantConfig(parentIssue)
//def listOfOptions = ComponentAccessor.getComponent(OptionsManager).getOptions(fieldConfig)
//def parentOption = listOfOptions?.find {it.value == "AAA"}
//def childOption = parentOption?.getChildOptions()?.find {it.value == "A2"}

//def mapWithValues = [:]
//mapWithValues.put(null, parentOption)
//mapWithValues.put('1', childOption)

summariesList.each {
    MutableIssue newSubTask = issueFactory.getIssue()
    newSubTask.setAssigneeId(parentIssue.assigneeId)
    newSubTask.setSummary(it)
    newSubTask.setParentObject(parentIssue)
    newSubTask.setProjectObject(parentIssue.getProjectObject())
    newSubTask.setIssueTypeId(constantManager.getAllIssueTypeObjects().find{
        it.getName() == "Sub-Task" || it.getName() == 'サブタスク'
    }.id)

    // Add any other fields you want for the newly created sub task
    //newSubTask.setCustomFieldValue(cascadingSelect, mapWithValues)

    def newIssueParams = ["issue" : newSubTask] as Map<String,Object>

    //for JIRA v6.*
    //issueManager.createIssueObject(user.directoryUser, newIssueParams)
    //subTaskManager.createSubTaskIssueLink(parentIssue, newSubTask, user.directoryUser)

    // for JIRA v7.*
    issueManager.createIssueObject(user, newIssueParams)
    subTaskManager.createSubTaskIssueLink(parentIssue, newSubTask, user)

    log.info "Issue with summary ${newSubTask.summary} created"
}
