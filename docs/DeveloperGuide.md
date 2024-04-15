---
  layout: default.md
  title: "Developer Guide"
  pageNav: 3
---

# Connectify Developer Guide

<!-- * Table of Contents -->
<page-nav-print />

--------------------------------------------------------------------------------------------------------------------

## **Acknowledgements**

This project is adapted from the AddressBook Level 3 (AB-3) project created by the [SE-EDU intiative](https://se-education.org/).<br>
We employed the help of ChatGPT to generate some code for the functionality of some features.

--------------------------------------------------------------------------------------------------------------------

## **Setting up, getting started**

Refer to the guide [_Setting up and getting started_](SettingUp.md).

--------------------------------------------------------------------------------------------------------------------

## **Design**

### Architecture

<puml src="diagrams/ArchitectureDiagram.puml" width="280" />

The ***Architecture Diagram*** given above explains the high-level design of the App.

Given below is a quick overview of main components and how they interact with each other.

**Main components of the architecture**

**`Main`** (consisting of classes [`Main`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/Main.java) and [`MainApp`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/MainApp.java)) is in charge of the app launch and shut down.
* At app launch, it initializes the other components in the correct sequence, and connects them up with each other.
* At shut down, it shuts down the other components and invokes cleanup methods where necessary.

The bulk of the app's work is done by the following four components:

* [**`UI`**](#ui-component): The UI of the App.
* [**`Logic`**](#logic-component): The command executor.
* [**`Model`**](#model-component): Holds the data of the App in memory.
* [**`Storage`**](#storage-component): Reads data from, and writes data to, the hard disk.

[**`Commons`**](#common-classes) represents a collection of classes used by multiple other components.

**How the architecture components interact with each other**

The *Sequence Diagram* below shows how the components interact with each other for the scenario where the user issues the command `delete Alex Yeoh`.

<puml src="diagrams/ArchitectureSequenceDiagram.puml" width="574" />

Each of the four main components (also shown in the diagram above),

* defines its *API* in an `interface` with the same name as the Component.
* implements its functionality using a concrete `{Component Name}Manager` class (which follows the corresponding API `interface` mentioned in the previous point.

For example, the `Logic` component defines its API in the `Logic.java` interface and implements its functionality using the `LogicManager.java` class which follows the `Logic` interface. Other components interact with a given component through its interface rather than the concrete class (reason: to prevent outside component's being coupled to the implementation of a component), as illustrated in the (partial) class diagram below.

<puml src="diagrams/ComponentManagers.puml" width="300" />

The sections below give more details of each component.

### UI component

The **API** of this component is specified in [`Ui.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/Ui.java)

<puml src="diagrams/UiClassDiagram.puml" alt="Structure of the UI Component"/>

The UI consists of a `MainWindow` that is made up of parts e.g.`CommandBox`, `ResultDisplay`, `PersonListPanel`, `StatusBarFooter` etc. All these, including the `MainWindow`, inherit from the abstract `UiPart` class which captures the commonalities between classes that represent parts of the visible GUI.

The `UI` component uses the JavaFx UI framework. The layout of these UI parts are defined in matching `.fxml` files that are in the `src/main/resources/view` folder. For example, the layout of the [`MainWindow`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/ui/MainWindow.java) is specified in [`MainWindow.fxml`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/resources/view/MainWindow.fxml)

The `UI` component,

* executes user commands using the `Logic` component.
* listens for changes to `Model` data so that the UI can be updated with the modified data.
* keeps a reference to the `Logic` component, because the `UI` relies on the `Logic` to execute commands.
* depends on some classes in the `Model` component, as it displays `Person` object residing in the `Model`.

### Logic component

**API** : [`Logic.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/logic/Logic.java)

Here's a (partial) class diagram of the `Logic` component:

<puml src="diagrams/LogicClassDiagram.puml" width="550"/>

The sequence diagram below illustrates the interactions within the `Logic` component, taking `execute("delete Alex Yeoh")` API call as an example.

<puml src="diagrams/DeleteSequenceDiagram.puml" alt="Interactions Inside the Logic Component for the `delete Alex Yeoh` Command" />

<box type="info" seamless>

**Note:** The lifeline for `DeleteCommandParser` should end at the destroy marker (X) but due to a limitation of PlantUML, the lifeline continues till the end of diagram.
</box>

How the `Logic` component works:

1. When `Logic` is called upon to execute a command, it is passed to an `AddressBookParser` object which in turn creates a parser that matches the command (e.g., `DeleteCommandParser`) and uses it to parse the command.
1. This results in a `Command` object (more precisely, an object of one of its subclasses e.g., `DeleteCommand`) which is executed by the `LogicManager`.
1. The command can communicate with the `Model` when it is executed (e.g. to delete a person).<br>
   Note that although this is shown as a single step in the diagram above (for simplicity), in the code it can take several interactions (between the command object and the `Model`) to achieve.
1. The result of the command execution is encapsulated as a `CommandResult` object which is returned back from `Logic`.

Here are the other classes in `Logic` (omitted from the class diagram above) that are used for parsing a user command:

<puml src="diagrams/ParserClasses.puml" width="600"/>

How the parsing works:
* When called upon to parse a user command, the `AddressBookParser` class creates an `XYZCommandParser` (`XYZ` is a placeholder for the specific command name e.g., `AddCommandParser`) which uses the other classes shown above to parse the user command and create a `XYZCommand` object (e.g., `AddCommand`) which the `AddressBookParser` returns back as a `Command` object.
* All `XYZCommandParser` classes (e.g., `AddCommandParser`, `DeleteCommandParser`, ...) inherit from the `Parser` interface so that they can be treated similarly where possible e.g, during testing.

### Model component
**API** : [`Model.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/model/Model.java)

<puml src="diagrams/ModelClassDiagram.puml" width="450" />


The `Model` component,

* stores the address book data i.e., all `Person` objects (which are contained in a `UniquePersonList` object).
* stores the currently 'selected' `Person` objects (e.g., results of a search query) as a separate _filtered_ list which is exposed to outsiders as an unmodifiable `ObservableList<Person>` that can be 'observed' e.g. the UI can be bound to this list so that the UI automatically updates when the data in the list change.
* stores a `UserPref` object that represents the user’s preferences. This is exposed to the outside as a `ReadOnlyUserPref` objects.
* does not depend on any of the other three components (as the `Model` represents data entities of the domain, they should make sense on their own without depending on other components)

<box type="info" seamless>

**Note:** An alternative (arguably, a more OOP) model is given below. It has a `Tag` list in the `AddressBook`, which `Person` references. This allows `AddressBook` to only require one `Tag` object per unique tag, instead of each `Person` needing their own `Tag` objects.<br>

<puml src="diagrams/BetterModelClassDiagram.puml" width="450" />

</box>


### Storage component

**API** : [`Storage.java`](https://github.com/se-edu/addressbook-level3/tree/master/src/main/java/seedu/address/storage/Storage.java)

<puml src="diagrams/StorageClassDiagram.puml" width="550" />

The `Storage` component,
* can save both address book data and user preference data in JSON format, and read them back into corresponding objects.
* inherits from both `AddressBookStorage` and `UserPrefStorage`, which means it can be treated as either one (if only the functionality of only one is needed).
* depends on some classes in the `Model` component (because the `Storage` component's job is to save/retrieve objects that belong to the `Model`)

### Common classes

Classes used by multiple components are in the `seedu.addressbook.commons` package.

--------------------------------------------------------------------------------------------------------------------

## **Implementation**

This section describes some noteworthy details on how certain features are implemented


### Company feature
The company attribute is kept as a String and each Person has a company attribute, which is initialised as an empty String
and only added on later to the contact with the `co NAME c/COMPANY_NAME` command.

Step 1. When the execute method of CompanyCommand is called, it takes in a Model object as a parameter. We check whether the 
contactName of the CompanyCommand object is an empty string. If it is, we will throw a CommandException, else we continue. <br>
Step 2. We will get the list of persons object representing the contacts in Connectify with `model.getFilteredPersonList()` and iterate
through it to check for the contact in Connectify with the corresponding contactName. If we do not find a matching person, 
we throw a CommandException that the contact does not exist in Connectify. <br>
Step 3. Check whether the CompanyCommand object's company variable is an empty String. If it is, this means that it is a
remove company command. Otherwise, it is an add company command.<br>
Step 4. If it is a remove company command, check whether the contact currently has an existing company attribute. If there is
currently no company attribute, we set the message to be displayed as MESSAGE_DELETE_COMPANY_FAILURE. Else, we set the message
as MESSAGE_DELETE_COMPANY_SUCCESS. <br>
Step 5. If it is an add company command, we set the message to be displayed as MESSAGE_ADD_COMPANY_SUCCESS.<br>
Step 6. Create a new updatedPerson Person object with the updated company attribute. <br>
Step 7. Set the Person personToEdit to the new updatedPerson Person object in the model, with `model.setPerson(personToEdit, editedPerson)`.<br>
Step 8. Update the filteredPersonsList to display all contacts and the updated information by calling `model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS)`<br>
Step 9. Return the CommandResult with the corresponding message.

The adding of a company attribute to a contact follows the following activity diagram.

<puml src="diagrams/CompanyCommandActivityDiagram.puml" alt="CompanyCommandActivityDiagram"/>

The company attribute is displayed in the contact card. We initialise it as visible=False in the PersonListCard.fxml file,
and then set it as visible when the company attribute is not an empty String in the PersonCard.java file in the UI component.
This way there won't be awkward empty spaces in the contact card when the contact does not have a company attribute.

#### Design considerations:
The company attribute can only be added and removed with the company command format `co NAME c/COMPANY_NAME`, where COMPANY_NAME
is specified as empty for a remove company command. This is for user convenience as the current add command has several 
prefixes which might make an add command too length. Hence, we believe that with the company attribute being handled differently,
it would increase user convenience and usability.

### Find by Company feature
The find by company feature works in a similar way to AB-3's find by contact name feature. We were inspired by the implementation
of AB-3's find feature and modified it to cater to the company attribute. We actually modified our find feature to find
by partial words rather than full words, as it would increase user convenience. However, for the find by company feature, 
it works by matching full words rather than partial words.

Step 1. FindCompanyCommandParser object's `parse` function is called and creates a CompanyContainsKeywordsPredicate object, that checks
whether the contacts in Connectify have company attributes that match the keywords inputted. <br>
Step 2. FindCompanyCommand is created, with the corresponding CompanyContainsKeywordsPredicate object. <br>
Step 3. FindCompanyCommand's `execute(model)` method is called and we update the filtered persons list by calling `model.updateFilteredPersonList(predicate)`,
with the predicate being the CompanyContainsKeywordsPredicate. This is to get the contacts with the matching company attributes.<br>
Step 4. Get the count of matching contacts by calling `model.getFilteredPersonList().size()`.<br>
Step 5. Return the CommandResult with the corresponding number of matching contacts.

The feature flow follows the following sequence diagram.

<puml src="diagrams/FindCompanySequenceDiagram.puml" alt="FindCompanySequenceDiagram" />

#### Design considerations:
The find by company feature checks for contacts with the company attribute that matches the full keyword inputted, case-insensitive.
This is to ensure higher accuracy when searching for contacts from a particular company. 

By using the similar method used in the FindCommand and using a CompanyContainsKeywordsPredicate, we maintain consistency
of our implementation throughout the program.

### Meeting feature
The meeting attribute of an object is kept as a Meeting object with 4 attributes, a String description, a LocalDate date,
a LocalTime start and a LocalTime end. A Meeting object is initialized with 4 Strings, the meeting description, date, start and end
time. For our current implementation, each contact is only allowed to have one meeting object.

Step 1. When the execute method of AddMeetingCommand is called, it takes in a Model object as a parameter. We check whether the
contactName of the AddMeetingCommand object is an empty string. If it is, we will throw a CommandException, else we continue. <br>
Step 2. We will get the list of persons object representing the contacts in Connectify with `model.getFilteredPersonList()` and iterate
through it to check for the contact in Connectify with the corresponding contactName. If we do not find a matching person,
we throw a CommandException that the contact does not exist in Connectify. <br>
Step 3. Check whether the AddMeetingCommand object's meeting variable is an empty String. If it is, this means that it is a
remove meeting command. Otherwise, it is an add meeting command.<br>
Step 4. If it is a remove meeting command, check whether the contact currently has an existing meeting attribute. If there is
currently no meeting attribute, we set the message to be displayed as MESSAGE_DELETE_MEETING_FAILURE. Else, we set the message
as MESSAGE_DELETE_MEETING_SUCCESS. <br>
Step 5. If it is an add meeting command, we set the message to be displayed as MESSAGE_ADD_MEETING_SUCCESS.<br>
Step 6. Create a new updatedPerson Person object with the updated meeting attribute. <br>
Step 7. Set the Person personToEdit to the new updatedPerson Person object in the model, with `model.setPerson(personToEdit, editedPerson)`.<br>
Step 8. Update the filteredPersonsList to display all contacts and the updated information by calling `model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS)`<br>
Step 9. Return the CommandResult with the corresponding message.

The adding of a meeting attribute to a contact follows the following sequence diagram.

<puml src="diagrams/AddMeetingCommandSequenceDiagram.puml" alt="AddMeetingCommandSequenceDiagram" />

**Challenges faced**
We initially meant to do the parsing of the date and time strings to the LocalDate and LocalTime respectively in the AddMeetingCommandParser.
However, we realised that if we wanted to initialize the meeting object with LocalDate and LocalTime, we would face several issues like
NullPointerException when we try to initialize 'null' meetings in a similar fashion to initializing empty company with "" to indicate
no company attribute. We considered using Optional class but were worried that it would overcomplicate the application architecture.
As a result, we decided to initialize a Meeting object with 4 Strings, the meeting description, date, start and end all stored
as Strings, and parse it into LocalDate and LocalTime in the initialization itself, handling empty strings accordingly.

### View meetings feature
The view meetings feature works in a simple way by filtering the contact list in Connectify by whether the meeting attribute of
the person is empty or not. The whole implementation is done in the ViewMeetingCommand.

Step 1. When the execute method of ViewMeetingCommand is called, it takes in a Model object as a parameter. We filter
the persons list by whether their meeting attribute is 'empty' (meeting.toString() returns an empty string) by calling 
`model.updateFilteredPersonList(person -> !person.getMeeting().toString().equals(""))`. <br>
Step 2. Check if the filtered list is empty by calling `model.getFilteredPersonList().isEmpty()`. If it is empty, throw a CommandException to specify that there
are no contacts with meetings found. <br>
Step 3. Return the CommandResult with the corresponding message.

The logic of the view meetings function follows the following activity diagram.

<puml src="diagrams/ViewMeetingsActivityDiagram.puml" width="574" />


### Star Feature
The implementation of the star feature allows users to star specific contacts in Connectify.

#### 1. Command Structure:
* The `StarCommand` class extends the abstract class `Command`.
* It defines a `COMMAND_WORD` which is used to invoke this specific command.
* The `MESSAGE_USAGE` constant provides information on how to use this command, including parameters and examples.

#### 2. Execution:
* When the execute method of `StarCommand` is called, it takes a `Model` object as a parameter, which represents the application's data model.
* It searches for the contact specified by the user within the list of contacts retrieved from the model.
* If the contact is not found, it throws a `CommandException`.
* If the contact is already starred, it throws a `CommandException`.
* If the contact is found and not already starred, it sets the `starred boolean` of the contact to `true` and updates the contact in the model.
* Finally, it returns a `CommandResult` indicating the success of the operation.

#### 3. Model Update:
* Upon successfully starring the contact, the `starredContact` object is created with the updated information, including the `starred boolean`.
* The model's `setPerson` method is called to update the contact with the new starred status.
* The filtered person list is then updated to reflect the changes in the model.

#### 4. Error Handling:

* The implementation handles various error scenarios, such as contact not found or already starred, by throwing `CommandException` with appropriate error messages.

#### Design Considerations:
* Data Consistency: The implementation ensures that the model is updated consistently after starring a contact to maintain data integrity.
* Scalability: Depending on the size of the contact list and frequency of use, the efficiency of searching for contacts might be a consideration for optimization.
* Flexibility: The design allows for easy extension with additional functionalities related to starring contacts, such as unstarring.

The diagram below shows the activity diagram for StarCommand.

<puml src="diagrams/StarCommandActivityDiagram.puml" width="574" />

### Add Priority Feature
The add priority feature allows users to set a priority level of `high`, `med`, or `none`, which are the only priority
levels accepted. Priority commands for each accepted priority level use different commands: `pr/high NAME`,
.`pr/med NAME` and `pr/none NAME` for `high`, `med` and `none` priorities respectively.
The `AddressBookParser` class identifies the command and creates 
a new PriorityCommandParser instance for each command with the corresponding priority, which is 
stored as a String. The parser constructs a PriorityCommand object with the specified priority level (`high`, `med`, 
or an empty string for `none`) and the contact's name. The PriorityCommand handles potential errors then updates the 
contact's priority in the model, as shown in the following activity diagram.

<puml src="diagrams/AddPriorityActivityDiagram.puml" width="574" />

The UI reflects these priorities through the use of colored dots next to contact names, where a the dot is red for 
`high` priority, orange for `med`, and not visible for `none`.

#### Design Considerations
* Only priority levels of `high`, `med`, or `none` are accepted to simplify decision-making for the user. In the case 
where too many priority levels are accepted, this would also dilute the importance of truly urgent contacts.
* The `pr/none NAME` command functions as removing the priority level from a contact and also removes the indication 
of a priority in the UI as it offers users greater consistency with other priority-related commands, making it 
easier for users who can type fast as they can quickly adjust a contact's priority without changing their typing flow.

### Filter by Priority Feature
The filter by priority feature allows users to view a the contact list filtered by `high` priority or `med` priority.
The filter by priority commands users can use are `filter-high` to view a list of `high` priority contacts and
`filter-med` to view a list of `med` priority contacts. The `AddressBookParser` class identifies the command and creates
an instance of FilterHighPriorityCommand and FilterMedPriorityCommand for `filter-high` and `filter-med` respectively.
This command then interacts with the model to update the list of displayed contacts. It does this by invoking a method 
that filters all contacts, checking if each contact's priority attribute matches the specified priority level. The 
filtered list is then either displayed to the user if it contains any entries or a message is displayed indicating no 
contacts were found with the specified priority. This is seen in the as shown in the following activity diagram.

<puml src="diagrams/FilterHighPriorityActivityDiagram.puml" width="574" /> 

for `filter-high` and 

<puml src="diagrams/FilterMedPriorityActivityDiagram.puml" width="574" /> 

for `filter-med`.

#### Design Considerations
A message is shown to the user in the case where no contacts meet the filter by priority criteria.
The helps the user in understanding the result of the command without confusion, as users might otherwise 
think the command does not work if an empty filtered list is shown without any message.  

### Remark feature

The remark attribute is kept as a String and each Person has a Remark attribute, which is 
initialised as an empty String and only added on later to the contact with the 
`remark NAME r/REMARK` command.

Step 1. We will get the list of persons object representing the contacts in Connectify with `model.getFilteredPersonList()` and iterate
through it to check for the contact in Connectify with the corresponding contactName. If we do not find a matching person,
we throw a CommandException that the contact does not exist in Connectify. <br>
Step 2. Check whether the RemarkCommand object's remark variable is an empty String. If it is, this means that it is a
remove remark command. Otherwise, it is an add remark command.<br>
Step 3. If it is a remove remark command, check whether the contact currently has an existing remark attribute. If there is
currently no remark attribute, we set the message to be displayed as MESSAGE_DELETE_REMARK_FAILURE. Else, we set the message
as MESSAGE_DELETE_REMARK_SUCCESS. <br>
Step 4. If it is an add remark command, we set the message to be displayed as MESSAGE_ADD_REMARK_SUCCESS.<br>
Step 5. Create a new updatedPerson Person object with the updated remark attribute. <br>
Step 6. Set the Person personToEdit to the new updatedPerson Person object in the model, with `model.setPerson(personToEdit, editedPerson)`.<br>
Step 7. Update the filteredPersonsList to display all contacts and the updated information by calling `model.updateFilteredPersonList(PREDICATE_SHOW_ALL_PERSONS)`<br>
Step 8. Return the CommandResult with the corresponding message.

The following sequence diagram shows how an remark operation goes through the Logic component:

<puml src="diagrams/RemarkSequenceDiagram.puml" alt="RemarkSequenceDiagram"/>

The following activity diagram shows the process of RemarkCommand:

<puml src="diagrams/RemarkCommandActivityDiagram.puml" alt="RemarkCommandActivityDiagram"/>

The remark attribute is displayed in the contact card. We initialise it as visible=False in the PersonListCard.fxml file,
and then set it as visible when the remark attribute is not an empty String in the PersonCard.java file in the UI component.
This way there won't be awkward empty spaces in the contact card when the contact does not have a remark.


--------------------------------------------------------------------------------------------------------------------

## **Documentation, logging, testing, configuration, dev-ops**

* [Documentation guide](Documentation.md)
* [Testing guide](Testing.md)
* [Logging guide](Logging.md)
* [Configuration guide](Configuration.md)
* [DevOps guide](DevOps.md)

--------------------------------------------------------------------------------------------------------------------

## **Appendix A: Requirements**

### Product scope

**Target user profile**:

* **Manages a Substantial Contact Network:** Connectify's target users have a need to manage a significant number of contacts within their professional network.
* **Prefers Desktop Applications:** These users prefer desktop applications over other types of software solutions.
* **Proficient Typists:** Connectify's target users are proficient typists who can type quickly and accurately. They prefer typing over mouse interactions.
* **Comfortable with Command-Line Interfaces (CLI):** While they may not be experts, Connectify's target users are reasonably comfortable using command-line interfaces (CLI) and appreciate the efficiency and control that CLI applications offer.
* **Value Efficiency and Organization:** Connectify's target users value efficiency and organization in managing their professional contacts.

**Value proposition**: Tailored specifically for computing students, our team project, Connectify, optimises networking for future career opportunities. Connectify seamlessly organises contact details, efficiently manages professional relationships and simplifies event and meeting planning. Designed for seamless usage via Command Line Interface (CLI), Connectify’s ease of use sets up computing students for success.

--------------------------------------------------------------------------------------------------------------------
<div style="page-break-after: always;"></div>

## **Appendix B: Planned Enhancements**
Team size: 5

1. **Change priority command format to be more consistent with the other commands.** <br>
The current priority command format is
`pr/PRIORITY_LEVEL NAME`. We plan to change the format to `pr NAME pr/PRIORITY_LEVEL`, to match the formats of our other commands.
2. **Add priority level low** <br>
Currently, we only allow users to assign their contacts priority levels high and medium.
We plan to add and allow users to assign their contacts with the priority level low.
3. **Allow users to filter by low and no priority** <br>
Users are only allowed to filter by priority high and medium currently.
We plan to allow users to filter by no priority and also by low priority (see point 2). This is to provide users with more
convenient information for them to prioritise their networking efforts accordingly.
4. **Disallow users from adding meetings with timings where the end time is earlier than the start time** <br>
Currently, if users try to add a meeting with the timing where they specify the end time to be earlier than the start time, no error is raised.
We plan to introduce an error when users accidentally use the function this way. This is to prevent misuse of the function
as it would typically be illogical to have an end time earlier than the start.
5. **Enforce stricter constraints on email input for add and edit command** <br>
The current implementation of add and edit command only checks for the @domain. 
We plan to ensure that the email address input for the add and edit command follows 
the standard internet email address conventions strictly, which is to have a compulsory top-level domain (TLD). Hence, we
plan to introduce additional checks for email inputs without a top-level domain and raise an error. This is to prevent users
from accidentally adding an incorrect email.
6. **Allow users to perform commands on contacts that are not currently listed/displayed** <br>
Currently, after running some commands like `find` and `findco` where the displayed contact list is narrowed down, running other commands will only execute it on the
currently displayed list. As a result, for users to use the commands accurately and effectively, they currently need to run
the `list` command before running other commands to execute it on the entire contact list, which is inconvenient. Hence, we
plan to allow users to perform commands on contacts, based on names that are independent to the list that is filtered/displayed.
7. **Allow contact name inputs to include characters like '/'** <br>
Our current implementation of add and edit command does not
allow for special characters and only allows alphanumeric characters. We plan to allow the name input for add and edit command
to include special characters like '/' for inclusivity as some names have it.
8. **Allow duplicate contact names if they have different attributes** <br>
Currently, we do not allow duplicate contact names at all,
and names are case-insensitive (as in real life). However, we do note that while rare, some people may have the same exact name.
Hence, in consideration of this, we plan to allow duplicate contact names if they have different attributes
(e.g different company) in future versions.
9. **More consistent error messages throughout application** <br>
In our current implementation of the features, error messages
for incorrect command usage varies across different commands. Hence, we plan to modify error messages for incorrect command usage
to ensure cohesiveness and consistency throughout the application, to provide more clarity to users.

--------------------------------------------------------------------------------------------------------------------


### User stories

Priorities: High (must have) - `* * *`, Medium (nice to have) - `* *`, Low (unlikely to have) - `*`

| Priority | As a …​           | I want to …​                                                    | So that I can…​                                                                      |
|----------|-------------------|-----------------------------------------------------------------|--------------------------------------------------------------------------------------|
| `* * *`  | user              | add a new contact easily                                        |                                                                                      |
| `* * *`  | user              | edit the details that I’ve added                                | my application will contain the most updated and accurate information                |
| `* * *`  | user              | list all my contacts                                            | remember whom I’ve met at a glance                                                   |
| `* * *`  | user              | delete a contact                                                | only keep those that are necessary                                                   |
| `* *`    | computing student | search a contact using key word                                 | find the contact quickly                                                             |
| `* *`    | user              | add a meeting with my contact                                   | know when to meet with the contact                                                   |
| `* *`    | user              | find all meetings                                               | know who I will be meeting with                                                      |
| `* *`    | user              | differentiate which companies my professional contacts are from | know which company I am connecting with                                              |
| `* *`    | user              | find my contacts by company                                     | easily find the contacts from the company I want                                     |
| `* *`    | computing student | assign priority levels to my contacts                           | prioritise certain contacts in my network who would be more beneficial for my career |
| `*`      | computing student | filter my contacts by priority                                  | identify high-priority contacts at a glance                                          |
| `*`      | user              | have a "favourites" or "star" feature for important contacts    | easily access them without scrolling through the entire list                         |
| `*`      | user              | know the number of contacts quickly                             | get a sensing of how many people are in my network                                   |
| `*`      | user              | unstar a contact                                                |                                                                                      |
| `*`      | user              | remove priority from a contact                                  |                                                                                      |


### Use cases

(For all use cases below, the **System** is the `Connectify` and the **Actor** is the `user`, unless specified otherwise)

**Use case: Add a contact**

**MSS**

1.  User requests to add a contact
2.  Connectify adds the person

    Use case ends.

**Extensions**


* 1a. The given contact name is already in the contact list.

    * 1a1. Connectify shows an error message.

      Use case resumes from step 1.

* 1b. Connectify detects an error in the entered data.

    * 1b1. Connectify shows an error message.

      Use case resumes from step 1.
  
* 1c. Connectify detects a similar contact name in the entered contact name.

    * 1c1. Connectify warns the user that there is existing contacts with similar names.

      Use case resumes from step 2.
        

**Use case: Delete a contact**

**MSS**

1.  User requests to delete a specific person from the contact list
2.  Connectify deletes the person

    Use case ends.

**Extensions**


* 1a. The given contact name is not in the contact list.

    * 1a1. Connectify shows an error message.

      Use case resumes at step 1.

**Use case: List contacts**

**MSS**

1.  User requests to list contacts
2.  Connectify shows a list of contacts

    Use case ends.

**Extensions**


* 1a. The list is empty.

    * 1a1. Connectify shows an error message.

      Use case ends.

**Use case: Edit contacts**

**MSS**

1.  User requests to edit a specific contact
2.  Connectify edits the contact information and displays the full updated contact information

    Use case ends.

**Extensions**


* 1a. The given contact name is not in the contact list.

    * 1a1. Connectify shows an error message.

      Use case resumes at step 1.

* 1b. Connectify detects an error in the entered data.

    * 1b1. Connectify shows an error message

      Use case resumes from step 1.
  
* 1c. Connectify detects that the new edited information violates the duplicate contact constraint. (updated name
exists in Connectify already)

    * 1c1. Connectify shows an error message

      Use case resumes from step 1.

**Use case: Add company attribute to a contact**

**MSS**

1.  User requests to add a specific company attribute to a specific contact.
2.  Connectify adds the company's name attribute to the contact and displays the full updated contact information

    Use case ends.

**Extensions**


* 1a. The given contact name is not in the contact list.

    * 1a1. Connectify shows an error message.

      Use case resumes at step 1.

* 1b. The contact already has an existing company attribute.

    * 1b1. Connectify warns the user about the existing company attribute and updates the company attribute to the new one.

        Use Case ends

**Use case: Add a meeting to a contact**

**MSS**

1.  User requests to add a meeting to a specific contact name.
2.  Connectify adds the meeting details to the contact and displays the meeting details and the contact it has been added to.

    Use case ends.

**Extensions**


* 1a. The given contact name is not in the contact list.

    * 1a1. Connectify shows an error message.

      Use case resumes at step 1.

* 1b. The contact already has an existing meeting with the contact.

    * 1b1. Connectify warns user about the existing meeting details and updates the current meeting
      details to the new one.

      Use case ends

**Use case: Assign high or medium priority to a contact**

**MSS**

1.  User requests to assign medium or high priority to a specific contact name.
2.  Connectify adds the given priority to the contact and displays the full updated contact information.

    Use case ends.

**Extensions**


* 1a. The given contact name is not in the contact list.

    * 1a1. Connectify shows an error message.

      Use case resumes at step 1.

* 1b. The contact already has an existing priority label.

    * 1b1. Connectify adds the priority level to the contact according to the new request and displays the full updated contact information.

        Use case ends.

**Use case: Remove priority from a contact**

**MSS**

1.  User requests to remove priority to a specific contact name.
2.  Connectify removes priority from the contact and displays the full updated contact information.

    Use case ends.

**Extensions**


* 1a. The given contact name is not in the contact list.

    * 1a1. Connectify shows an error message.

      Use case resumes at step 1.

* 1b. The contact does not have an existing priority label.

    * 1b1. Connectify again sets the priority level of the contact as none according to the new request and displays the full updated contact information.

      Use case ends.

### Non-Functional Requirements

1.  Should work on any _mainstream OS_ as long as it has Java `11` or above installed.
2.  Should be able to hold up to 1000 persons without a noticeable sluggishness in performance for typical usage.
3.  A user with above average typing speed for regular English text (i.e. not code, not system admin commands) should be able to accomplish most of the tasks faster using commands than using the mouse.
4.  The application should implement certain security measures to protect the users' private contact details from unauthorized access. This includes the encryption of sensitive data and preventing unauthorized access to the application's database.
5.  The application should be convenient enough for the users(including those that are disabled).
### Glossary

* **Mainstream OS**: Windows, Linux, Unix, MacOS
* **Private contact detail**: A contact detail that is not meant to be shared with others
* **Favorites**: The users can mark some users as favorites. This will increase the user’s efficiency when finding the people they like to contact
* **Tags**: The users can classify different contacts as from different groupings
--------------------------------------------------------------------------------------------------------------------

## **Appendix C: Instructions for manual testing**

Given below are instructions to test the app manually.

<box type="info" seamless>

**Note:** These instructions only provide a starting point for testers to work on;
testers are expected to do more *exploratory* testing.

</box>

### Launch and shutdown

1. Initial launch

   1. Download the jar file and copy into an empty folder

   1. Double-click the jar file Expected: Shows the GUI with a set of sample contacts. The window size may not be optimum.

1. Saving window preferences

   1. Resize the window to an optimum size. Move the window to a different location. Close the window.

   1. Re-launch the app by double-clicking the jar file.<br>
       Expected: The most recent window size and location is retained.

### Deleting a contact

1. Deleting a contact from the addressbook

   1. Test case: `delete Alex Yeoh`<br>
      Expected: Okay, Alex Yeoh's contact has been deleted.

   1. Test case: `delete`<br>
      Expected: Invalid command format! delete: Deletes the person identified by the name used in the contact list. Parameters: CONTACT_NAME

   1. Test case: `delete nonexistent`<br>
      Expected: Oops, nonexistent's contact does not exist.

### Adding a person

1. Adding a new person into the list of contacts
   
    1. Test case: `add n/Alex Yeo p/98765432 a/Yeo street, block 321, #02-03`<br>
       Expected: The program will display "New person added: Alex Yeo; Phone: 98765432; Email: ; Address: Yeo street, block 321, #02-03; Tags:"
                The new person's info will be added into the addressbook. 
   
    1. Test case: `add`<br>
       Expected: Invalid command format!
       add: Adds a person to the address book. Parameters: n/NAME p/PHONE [e/EMAIL] [a/ADDRESS] [t/TAG]...
       EMAIL, ADDRESS and TAGs are optional. 
   
    1. Test case: `add Alex Tan`<br>
       Expected: Same as above
   
    1. Test case: `add n/Alex Yeo p/89765432 a/Yang street, block 123, #02-01`<br>
       Expected: The program will display "This person already exists in the address book"

### Editing a contact

1. Editing a contact's information.

    1. Test case: `edit Alex Yeo p/91234567`<br>
       Expected: The program will display "Edited Alex Yeo's contact." The changes made to Alex Yeo's info will be saved. 
   
    1. Test case: `edit Alex Yeo a/AlexDorm`<br>
       Expected: Same as above.

    1. Test case: `edit`<br>
       Expected: The program will display "Invalid command format!
       edit: Edits the details of the person identified by the contact name used in the displayed person list. Existing values will be overwritten by the input values. 
       Parameters: NAME [n/NAME] [p/PHONE] [e/EMAIL] [a/ADDRESS] [t/TAG]..."
   
    1. Test case: `edit Null n/Newname`<br>
       Expected: The program will display "Oops, Null's contact does not exist."
    
    1. Test case: `edit Alex Yeo n/ExistentPerson`<br>
       Expected: The program will display "Oops, you cannot change the contact name to this. This person already exists in the address book."

### Finding a contact

1. Finding contacts in the addressbook by typing names/part of names. 

1. Assuming 4 people, Alex Yeoh, John, Alex, and Alex Yeo, are in the addressbook.
    
    1. Test case: `find Alex`<br>
       Expected: The program will display "3 persons listed!". All the info of Alex, Alex Yeo, and Alex Yeoh would appear on the screen.  
    
    1. Test case: `find alex`<br>
       Expected: Same as above
    
    1. Test case: `find a`<br>
       Expected: Same as above
   
    1. Test case: 'find Alex John'<br>
       Expected: The program will display "4 persons listed!". All the info of Alex, Alex Yeo, Alex Yeoh, and John would appear on the screen.

    1. Test case: `find b`<br>
       Expected: The program will display "0 persons listed!"
    
    1. Test case: `find`<br>
       Expected: The program will display "Invalid command format!
       find: Finds all persons whose names contain any of the specified keywords (case-insensitive) and displays them as a list with index numbers.
       Parameters: KEYWORD [MORE_KEYWORDS]..."

### Adding a company

1. Adding a company to a contact

    1. Test case: `co Alex c/Alexcomp`<br>
       Expected: The program will display "Tagged Alex's company as Alexcomp". Also, there will be a company tag named Alexcomp attributed to Alex.

    1. Test case: `co Alex c/Alexcompany`<br>
       Expected: The program will display "Changed the existing company tag for Alex's contact
       Previous company tag: Alexcomp  
       Updated company tag: Alexcompany". And the tag of Alex will be changed and displayed on the screen.
    
    1. Test case: `co`<br>
       Expected: The program will display "Oops, please state the name of the contact".
    
    1. Test case: `co Alex`<br>
       Expected: The program will display "Removed the company tag from Alex's contact".
    
    1. Test case: `co Nonexistent`<br>
      Expected: The program will display "Oops, Nonexistent's contact does not exist. Unable to add company tag".
   
    1. Test case: `co Nonexistent c/Alexcomp`<br>
      Expected: same as above

### Finding by Company

1. Finding contacts by their company 

1. Assuming there are 4 people in the contact list. Alex Yeoh, Alex, and Alex Yeo are working in Company Alexcomp, and John is working in Company Johncomp.

    1. Test case: `findco Alexcomp`<br>
       Expected: The program will display "Found 3 contacts with matching company tag(s)." And the info of the three people working in Alexcomp will be displayed on the screen. 
    
    1. Test case: `findco Alexcomp Johncomp`<br>
       Expected: The program will display "Found 4 contacts with matching company tag(s)." And the info of the people working in either Alexcomp or Johncomp will be displayed on the screen.
   
    1. Test case: `findco Nonexistent`<br>
       Expected: The program will display "Found 0 contacts with matching company tag(s)."
    
    1. Test case: `findco`<br>
       Expected: The program will display "Invalid command format!
       findco: Finds all contacts with company tag containing the specified keywords (case-insensitive) and displays them as a list with index numbers.
       Parameters: KEYWORD [MORE_KEYWORDS]...".

### Prioritising a contact:
1. Assign priority to a new contact.

   1. Prerequisites: Have a contact shown in the displayed contact list.

   1. Test case: `pr/high John Doe`<br>
   Expected: John Doe is assigned high priority level, a red circle appears behind the contact name. Confirmation message displayed.

   1. Test case: `pr/med Jane Smith`<br>
   Expected: Jane Smith is assigned medium priority level, an orange circle appears behind the contact name. Confirmation message displayed.

   1. Test case: `pr/none Alex Tan`<br>
   Expected: Priority level is removed from Alex Tan. Confirmation message displayed.

   1. Test case: `pr/low Michael Johnson`<br>
   Expected: Error message indicating unknown command. No changes made.

2. Assign priority to a contact that is already assigned with priority.

    1. Prerequisites: Have a contact shown in the displayed contact list, and John Doe is assigned high priority level.

    1. Test case: `pr/med John Doe`<br>
       Expected: John Doe is assigned medium priority level, an orange circle appears instead of the red circle. Confirmation message displayed.

### Filtering contacts by priority:
1. Prerequisites: Have contacts with different priority levels.

2. Test case: `filter-high`<br>
Expected: List of contacts with high priority is displayed.

3. Test case: `filter-med`<br>
Expected: List of contacts with medium priority is displayed.

4. Test case: `filter-low`<br>
Expected: Error message indicating unknown command. No changes made.

### Adding a meeting to a person:
1. Add a meeting to a new contact.

   1. Prerequisites: Have a contact shown in the displayed contact list.

   1. Test case: `mtg John Doe m/Coffee meeting time/14-04-2024 1500-1600`<br>
   Expected: A meeting named "Coffee meeting" with John Doe on 14th April 2024 from 3 PM to 4 PM is added. Confirmation message displayed.

   1. Test case: `mtg Jane Smith m/Call`<br>
   Expected: Error message indicating missing meeting time. No changes made.

   1. Test case: `mtg Michael Johnson m/Team meeting time/31-02-2025 0900-1000`<br>
   Expected: Error message indicating invalid date. No changes made.

2. Change the existing meeting with a contact.

   1. Prerequisites: Have a contact shown in the displayed contact list, and a meeting is added with John Doe.

   1. Test case: `mtg John Doe m/interview time/23-03-2024 1600-1700`<br>
   Expected: A meeting named "interview" with John Doe on 23rd March 2024 from 4 PM to 5 PM replaces the previous meeting.

### Viewing all contacts with meetings:
1. View all contacts with meetings.

   1. Prerequisites: Have contacts with meetings.

   1. Test case: `viewmtgs`<br>
   Expected: List of all contacts with scheduled meetings is displayed.

2. No contact found with meetings.

   1. Prerequisites: There is no meeting added to any existing contact.

   1. Test case: `viewmtgs`<br>
      Expected: Error message indicating that no contact found with meetings.

### Adding a remark to a person:

1. Add a meeting to a new contact.

   1. Prerequisites: Have a contact shown in the displayed contact list.

   1. Test case: `remark John Doe r/Met at conference`<br>
   Expected: Remark "Met at conference" is added to John Doe. Confirmation message displayed.

   1. Test case: `remark Jane Smith r/`<br>
   Expected: Remark is removed from Jane Smith. Confirmation message displayed.

   1. Test case: `remark Alex Tan`<br>
   Expected: Error message indicating missing remark description. No changes made.

2. Change the existing meeting with a contact.

    1. Prerequisites: Have a contact shown in the displayed contact list, and a remark is added with John Doe.

    1. Test case: `remark John Doe r/Met at school`<br>
       Expected: Remark "Met at conference" replaces the previous remark for John Doe.

### Getting the number of contacts:
1. Test case: `count`<br>
Expected: Total number of contacts in Connectify is displayed.

### Starring a contact:
1. Add a star to a new contact.

   1. Prerequisites: Have a contact shown in the displayed contact list, and the contact is not starred.

   1. Test case: `star John Doe`<br>
   Expected: John Doe is starred. A star appears behind the contact name. Confirmation message displayed.

2. Add a star to a contact that is already starred.

   1. Prerequisites: Have a contact shown in the displayed contact list, and Jane Smith is already starred.

   1. Test case: `star Jane Smith`<br>
   Expected: Error message indicating that the contact is already starred. No changes made.

### Removing the star from a contact:
1. Remove a star from a starred contact.

    1. Prerequisites: Have a contact shown in the displayed contact list, and the contact is starred.

    1. Test case: `unstar John Doe`<br>
       Expected: Star is removed from John Doe. Confirmation message displayed.

2. Remove a star from a contact that is not starred.

    1. Prerequisites: Have a contact shown in the displayed contact list, and the contact is not starred.

    1. Test case: `unstar Jane Smith`<br>
       Expected: Error message indicating that the contact is not starred. No changes made.



--------------------------------------------------------------------------------------------------------------------

## **Appendix D: Effort**

### Overview

Connectify builds upon the foundation laid by AB3, an address book application that manages contact information. 
It extends the functionality of AB3 by introducing new features, enhancing existing ones, 
and addressing various challenges to improve user experience and productivity.

### Difficulty and challenges

As Connectify builds upon the existing structure of AB3, integrating new features into the original AB3 design 
like company tags, priority assignment, meeting scheduling, and remark addition required careful design and 
implementation to maintain system integrity and usability. 

### Effort Required

Effort was allocated across various phases including requirement analysis, design, implementation, testing, 
and documentation. Extensive testing was performed to ensure the reliability and robustness of the application. 
Additionally, user feedback was actively sought and incorporated to refine features and enhance usability.


### Achievements

Connectify added 11 new features to the existing features from AB3 and changed elements of the GUI.
These new features include the those related to the attributes company, priority, meeting, remark, and star,
as well as counting the number of contacts and retrieving the previously entered commands. 
These improvements expanded the application's capabilities, making it more versatile and useful for users. 
The addition of these features enhances productivity by providing users with tools to better organize 
and manage their contacts and interactions.
