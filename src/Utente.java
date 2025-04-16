import java.util.List;
import java.util.ArrayList;

public class Utente {
    private String username;
    private String password;
    private List<ToDo> listaToDo;

    public Utente(String username, String password){
        this.username = username;
        this.password = password;
        this.listaToDo = new ArrayList<>();
    }
}
