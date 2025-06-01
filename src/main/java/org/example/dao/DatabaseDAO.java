package org.example.dao;

import org.example.model.Bacheca;
import org.example.model.ToDo;
import org.example.model.Utente;

import java.sql.SQLException;
import java.util.List;

public interface DatabaseDAO {
    boolean utenteEsiste(String username) throws SQLException;
    void salvaUtente(Utente utente) throws SQLException;
    void aggiungiUtente(String username, String password) throws SQLException;
    Utente getUtenteByUsername(String username) throws SQLException;
    List<String> getListaUtenti() throws SQLException;
    void salvaBachechePredefinite(List<Bacheca> bacheche, String username) throws SQLException;
    boolean haBachechePredefinite(String username) throws SQLException;
    List<Bacheca> getBachecheUtente(String username) throws SQLException;

     List<Bacheca> executeBachecaQuery(String query, String username) throws SQLException;
    Bacheca aggiungiBacheca(String titolo, String descrizione, String username);
    void modificaBacheca(String titoloCorrente, String nuovoTitolo, String nuovaDescrizione) ;
    void eliminaBacheca(String titolo) throws SQLException;
    List<Bacheca> getBachecheByUsername(String username) throws SQLException;


    void aggiungiCondivisione(ToDo todo, String username) throws SQLException;
    void rimuoviCondivisione(ToDo todo, String username) throws SQLException;
    void creaToDo(ToDo todo, String titoloBacheca) throws SQLException;
    ToDo getToDoByTitolo(String titolo) throws SQLException;
    List<ToDo> getTuttiToDo(String titoloBacheca) throws SQLException;
    void aggiornaToDo(ToDo todo) ;
    void eliminaToDo(String titolo, String titoloBacheca) throws SQLException;
    void trasferisciToDo(ToDo todo, String titoloBachecaDestinazione) throws SQLException;
    List<Bacheca> getListaBachecheDalDB(String username) throws SQLException;
    public void aggiornaOrdineToDo(int idToDo, int nuovaPosizione) throws SQLException;
}



