package org.example.dao;

import org.example.model.Bacheca;
import org.example.model.Utente;

import java.sql.SQLException;
import java.util.List;

public interface DatabaseDAO {
    void aggiungiUtente(String username, String password) throws SQLException;
    Utente getUtenteByUsername(String username) throws SQLException;

    void aggiungiBacheca(String titolo, String descrizione) throws SQLException;
    List<Bacheca> getBachecheByUsername() throws SQLException;
}


