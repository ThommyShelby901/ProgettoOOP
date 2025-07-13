package org.example.gui;

/**
 * Interfaccia funzionale che rappresenta un listener per aggiornamenti.
 * <p>
 * Viene usata per notificare una parte della GUI che deve aggiornarsi quando
 * avviene una modifica, ad esempio dopo un cambiamento a una checklist o un to-do.
 * <p>
 * È un'applicazione semplificata del pattern Observer: una classe osservatrice
 * implementa questo metodo per essere avvisata e reagire a un evento di aggiornamento.
 */
public interface AggiornaListener {
    /**
     * Metodo chiamato quando è necessario aggiornare la GUI o i dati.
     */
    void aggiorna();
}
