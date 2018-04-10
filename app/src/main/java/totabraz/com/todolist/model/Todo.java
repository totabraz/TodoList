package totabraz.com.todolist.model;

/**
 * Created by totabraz on 09/04/18.
 */

public class Todo {
    private String msgm;
    private String id;

    public String getMsgm() {
        return msgm;
    }

    public void setMsgm(String msgm) {
        this.msgm = msgm;
    }

    public Todo() {}


    public void setTodo(Todo todo) {
        this.id = todo.getId();
        this.msgm = todo.getMsgm();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
