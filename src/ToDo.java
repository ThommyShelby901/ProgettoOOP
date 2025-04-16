import java.time.LocalDate;

public class ToDo {
    private String link;
    private String titolo;
    private String foto;
    private String descrizione;
    private String coloreSfondo;
    private LocalDate dataScadenza;
    private Bacheca bacheca;
    private StatoToDo stato;

    public ToDo(String link, String titolo, String foto, String descrizione, String coloreSfondo){
        this.link = link;
        this.titolo = titolo;
        this.foto = foto;
        this.descrizione = descrizione;
        this.coloreSfondo = coloreSfondo;
    }

}
