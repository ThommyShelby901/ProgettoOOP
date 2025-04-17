import java.time.LocalDate;

public class ToDo {
    private String url;
    private String titolo;
    private String sfondo;
    private String descrizione;
    private String coloreSfondo;
    private LocalDate dataScadenza;
    private Bacheca bacheca;
    private StatoToDo stato;

    //essendo opzionali andiamo a definirli con set
    public void setUrl(String url){ this.url=url; }
    public void setTitolo(String titolo){ this.titolo=titolo; }
    public void setSfondo(String sfondo){ this.sfondo=sfondo; }
    public void setDescrizione(String descrizione){ this.descrizione: }
    public void setColoreSfondo(String coloreSfondo){ this.coloreSfondo=coloreSfondo; }
    public void setDataScadenza(String dataScadenza){ this.dataScadenza=Date; }
    public void setBacheca(Bacheca bacheca){ this.bacheca=bacheca; }
    public void setStato(String stato){ this.stato=stato; }


}
