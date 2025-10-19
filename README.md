Stacey Quarcoo (216672115) -- staceyq@my.yorku.ca

Bibi Fatema Sumaya (218986885) -- fatema23@my.yorku.ca

Dhruv Kapadia (215515281) -- dkapa@my.yorku.ca

Division of Work:
Fatema: Tasks 3,7,8
Stacey: Tasks 2,6
Dhruv: Tasks 4,5 

Theme:C- Task Logger / To-Do List – Save tasks with title, deadline, and notes.

App Architecture:
This app follows the Model–View–Controller (MVC) architecture pattern.
The View is implemented using XML layout files (activity_main.xml, activity_new_task.xml, item_task.xml, and activity_task_detail.xml), which define the user interface and visuals.
The Controller layer consists of the Java Activities (MainActivity, NewTaskActivity, and TaskDetailActivity), which handle user input, validation, navigation, and gesture logic for adding, viewing, deleting, or updating tasks.
The Model is represented by the Task class and the TaskDatabaseHelper, which manage task data stored in either SQLite or SharedPreferences depending on user selection.
This structure maintains a clear separation of concerns, ensuring modularity, easier debugging, and scalability as new features are introduced.