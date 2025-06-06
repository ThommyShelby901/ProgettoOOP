package org.example.dao;

import org.example.model.Bacheca;
import org.example.model.ToDo;
import org.example.model.Utente;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public interface DatabaseDAO {
    boolean utenteEsiste(String username) throws SQLException;
    void salvaUtente(Utente utente) throws SQLException;
    void aggiungiUtente(String username, String password) throws SQLException;
    Utente getUtenteByUsername(String username) throws SQLException;
    List<String> getListaUtenti() throws SQLException;
    void salvaBachechePredefinite(List<Bacheca> bacheche, String username) throws SQLException;
    boolean haBachechePredefinite(String username) throws SQLException;
    List<ToDo> getToDoInScadenza(String username, LocalDate data) throws SQLException;
    public List<ToDo> getToDoInScadenzaEntro(String username, LocalDate dataLimite) throws SQLException;
    List<ToDo> cercaToDoPerTesto(String username, String testo) throws SQLException;
     List<ToDo> executeToDoQuery(PreparedStatement pstmt) throws SQLException;
    public boolean isToDoSharedWithUser(int idToDo, String username) throws SQLException;
    public String getAutoreToDo(int idToDo) throws SQLException;
    public List<String> getCondivisioniPerToDo(int idToDo) throws SQLException;


    List<Bacheca> executeBachecaQuery(String query, String username) throws SQLException;
    Bacheca aggiungiBacheca(String titolo, String descrizione, String username)throws SQLException;
    public void modificaBacheca(String titoloCorrente, String nuovoTitolo, String nuovaDescrizione, String username);
    public void eliminaBacheca(String titolo, String username) throws SQLException;
    List<Bacheca> getBachecheByUsername(String username) throws SQLException;
    public List<ToDo> getToDoByUsername(String username) throws SQLException;


    void aggiungiCondivisione(ToDo todo, String username) throws SQLException;
    void rimuoviCondivisione(ToDo todo, String username) throws SQLException;
    void creaToDo(ToDo todo, String titoloBacheca) throws SQLException;
    ToDo getToDoByTitolo(String titolo) throws SQLException;
    List<ToDo> getTuttiToDo(String titoloBacheca, String username) throws SQLException;
    void aggiornaToDo(ToDo todo) ;
    void eliminaToDo(String titolo, String titoloBacheca) throws SQLException;
    void trasferisciToDo(ToDo todo, String titoloBachecaDestinazione) throws SQLException;
    List<Bacheca> getListaBachecheDalDB(String username) throws SQLException;
    public void aggiornaOrdineToDo(int idToDo, int nuovaPosizione) throws SQLException;
}



